package com.simpletour.gateway.ctrip.rest.service.impl;

import com.simpletour.biz.inventory.IStockBiz;
import com.simpletour.biz.order.IOrderBiz;
import com.simpletour.common.core.dao.IBaseDao;
import com.simpletour.common.core.dao.query.condition.AndConditionSet;
import com.simpletour.common.core.dao.query.condition.Condition;
import com.simpletour.common.core.exception.BaseSystemException;
import com.simpletour.domain.inventory.Stock;
import com.simpletour.domain.order.Order;
import com.simpletour.domain.order.OrderStatus;
import com.simpletour.domain.order.Source;
import com.simpletour.domain.product.Tourism;
import com.simpletour.domain.user.User;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.error.CtripOrderError;
import com.simpletour.gateway.ctrip.error.CtripTransError;
import com.simpletour.gateway.ctrip.rest.pojo.*;
import com.simpletour.gateway.ctrip.rest.pojo.bo.CtripTransOrderBo;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.ResponseBodyType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.OrderInfo;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.ResponseBodyTypeForTrans;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.ResponseBodyTypeForTransOrder;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.TourismInfo;
import com.simpletour.gateway.ctrip.rest.service.CtripTransService;
import com.simpletour.gateway.ctrip.util.DateUtil;
import com.simpletour.service.order.IOrderService;
import com.simpletour.service.product.IProductService;
import com.simpletour.service.sms.ISMSService;
import com.simpletour.service.user.UserService;
import com.simpletour.sms.core.SMSTemplateEnum;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Mario on 2016/1/12.
 */
@Service
public class CtripTransServiceImpl implements CtripTransService {

    @Resource
    private IProductService productService;

    @Resource
    private IStockBiz stockBiz;

    @Resource
    private IOrderService orderService;

    @Resource
    private IOrderBiz orderBiz;

    @Resource
    private UserService userService;

    @Resource
    private ISMSService ismsService;

    @Resource
    private TaskExecutor threadPoolTaskExecutor;

    private VerifyTransOrderResponse validateOrderBasicInfo(Order order) {
        if (order.getSourceOrderId() == null || order.getSourceOrderId().isEmpty())
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_VALIDATE_FAIL.custom("OTA订单号不能为空")), null);
        if (order.getName() == null || order.getName().isEmpty())
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_VALIDATE_FAIL.custom("联系人姓名不能为空")), null);
        if (order.getMobile() == null || order.getMobile().isEmpty())
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_VALIDATE_FAIL.custom("联系人手机号不能为空")), null);
        return null;
    }

    @Override
    public VerifyTransResponse queryTourism(VerifyTransRequest verifyTransRequest) {

        //查询是否有该source
        Optional<Source> sourceOptional = null;
        try {
            sourceOptional = orderService.findSourceById(Long.parseLong(verifyTransRequest.getHeader().getAccountId()));
        } catch (BaseSystemException e) {
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OTA_SOURCE_ID_NULL), null);
        }
        if (!sourceOptional.isPresent())
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OTA_SOURCE_NOT_EXISTED), null);

        if (verifyTransRequest.getBody() == null)
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OTA_BUS_SEARCH_PARAM_WRONG), null);

        Date dateForStock = null;
        if (!(verifyTransRequest.getBody().getDate() == null || verifyTransRequest.getBody().getDate().isEmpty())) {
            try {
                dateForStock = DateUtil.convertStrToDate(verifyTransRequest.getBody().getDate(), "yyyy-MM-dd");
            } catch (Exception e) {
                return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OTA_BUS_SEARCH_PARAM_WRONG), null);
            }
        }

        List<Tourism> tourisms;
        try {
            AndConditionSet andConditionSet = new AndConditionSet();
            if (verifyTransRequest.getBody().getTourismId() != null) {
                andConditionSet.addCondition("id", verifyTransRequest.getBody().getTourismId());
            }
            if (!(verifyTransRequest.getBody().getDepart() == null || verifyTransRequest.getBody().getDepart().isEmpty())) {
                andConditionSet.addCondition("depart", verifyTransRequest.getBody().getDepart());
            }
            if (!(verifyTransRequest.getBody().getArrive() == null || verifyTransRequest.getBody().getArrive().isEmpty())) {
                andConditionSet.addCondition("arrive", verifyTransRequest.getBody().getArrive());
            }
            andConditionSet.addCondition("online", true);
            andConditionSet.addCondition("shuttle", false);
            andConditionSet.addCondition("productNum", 0);
            tourisms = productService.getTourismByCondition(andConditionSet);
        } catch (IllegalArgumentException e) {
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.BUS_NO_FIND_FAILD), null);
        }
        if (tourisms == null || tourisms.isEmpty()) {
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS), new ResponseBodyTypeForTrans(0, null));
        }
        //转化数据
        final Date finalDateForStock = dateForStock;
        List<TourismInfo> tourismInfos = tourisms.stream().map(tourism1 -> {
            //获取库存以及价格
            Optional<Stock> stockOptional = stockBiz.getStock(tourism1, (verifyTransRequest.getBody().getDate() == null || verifyTransRequest.getBody().getDate().isEmpty()) ? new Date() : finalDateForStock, true);
            if (!stockOptional.isPresent()) {
                return null;
            }
            return new TourismInfo(tourism1.getId(), tourism1.getArrive(), tourism1.getDepart(), tourism1.getArriveTime(), tourism1.getDepartTime(), tourism1.getName(), tourism1.getDays(), stockOptional.get().getAvailableQuantity(), stockOptional.get().getPrice());
        }).filter(tourism -> tourism != null).collect(Collectors.toList());
        return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS), new ResponseBodyTypeForTrans(tourismInfos.size(), tourismInfos));
    }

    @Override
    public VerifyTransOrderResponse transVerifyOrder(VerifyTransOrderRequest verifyTransOrderRequest) {
        //获取转化数据,对数据进行组装
        CtripTransOrderBo ctripTransOrderBo = new CtripTransOrderBo(verifyTransOrderRequest);
        //组装成order实体对象
        Order order = null;
        try {
            order = ctripTransOrderBo.asOrder();
        } catch (BaseSystemException e) {
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.DATA_PARSE_EXCEPTION.custom(e.getExtMessage())));
        }
        //验证订单基本信息
        VerifyTransOrderResponse validate = this.validateOrderBasicInfo(order);
        if (validate != null) {
            return validate;
        }

        try {
            orderBiz.validateOrder(order);
        } catch (BaseSystemException e) {
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_VALIDATE_FAIL.custom(e.getError().getErrorMessage())));
        }
        //验证价格是否正确
        if (order.getOrderItems().get(0).getTourism() != null) {
            //获取库存以及价格
            Optional<Stock> stockOptional = stockBiz.getStock(order.getOrderItems().get(0).getTourism(), order.getOrderItems().get(0).getDate(), true);
            if (!stockOptional.isPresent()) {
                return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_VALIDATE_FAIL.custom("库存不存在")));
            }
            if (!order.getOrderItems().get(0).getSourcePrice().equals(stockOptional.get().getPrice()))
                return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_VALIDATE_FAIL.custom("车次单价不正确")));
        }

        //验证车次是否排班,并设置可用库存为0
        if (!orderService.validateIsBusPlanAvailable(order.getOrderItems().get(0), order)) {
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_VALIDATE_FAIL.custom("该车次未排班")));
        }
        //验证库存是否充足
        try {
            stockBiz.checkDemandQuantity(order);
        } catch (BaseSystemException e) {
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_VALIDATE_FAIL.custom("库存不足")));
        }
        return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS));
    }

    @Override
    @Transactional
    public VerifyTransOrderResponse transCreateOrder(VerifyTransOrderRequest verifyTransOrderRequest) {
        //获取转化数据,对数据进行组装
        CtripTransOrderBo ctripTransOrderBo = new CtripTransOrderBo(verifyTransOrderRequest);
        //组装成order实体对象
        Order order = null;
        try {
            order = ctripTransOrderBo.asOrder();
        } catch (BaseSystemException e) {
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.DATA_PARSE_EXCEPTION.custom(e.getExtMessage())));
        }
        //验证订单基本信息
        VerifyTransOrderResponse validate = this.validateOrderBasicInfo(order);
        if (validate != null) {
            return validate;
        }
        //先做一次验证,可以保证相应的行程和行程包被填满
        try {
            orderBiz.validateOrder(order);
        } catch (BaseSystemException e) {
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_VALIDATE_FAIL.custom(e.getError().getErrorMessage())));
        }
        //验证价格是否正确
        if (order.getOrderItems().get(0).getTourism() != null) {
            //获取库存以及价格
            Optional<Stock> stockOptional = stockBiz.getStock(order.getOrderItems().get(0).getTourism(), order.getOrderItems().get(0).getDate(), true);
            if (!stockOptional.isPresent()) {
                return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_VALIDATE_FAIL.custom("库存不存在")));
            }
            if (!order.getOrderItems().get(0).getSourcePrice().equals(stockOptional.get().getPrice()))
                return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_VALIDATE_FAIL.custom("车次单价不正确")));
        }

        //验证传进来的otaOrderId是否在数据库中已经存在
        if (!(order.getSourceOrderId() == null || order.getSourceOrderId().isEmpty())) {
            AndConditionSet andConditionSet = new AndConditionSet();
            andConditionSet.addCondition("sourceOrderId", order.getSourceOrderId(), Condition.MatchType.eq);
            List<Order> orders = orderService.findOrdersByConditions(andConditionSet, IBaseDao.SortBy.ASC);
            if (!(orders == null || orders.isEmpty())) {
                return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_HAS_BEEN_EXSITED));
            }
        }
        //验证传进来的用户是否存在,如果有用户则通过,没有则创建用户
        User userOriginal = userService.getAvailableUserByMobile(order.getMobile());
        if (userOriginal == null) {
            try {
                User user = userService.addUser(new User(order.getMobile(), order.getMobile(), order.getMobile(), order.getMobile().trim().substring(order.getMobile().length() - 6), "web-xiecheng", User.Status.ACTIVE));
                order.setUserId(user.getId());
            } catch (Exception e) {
                return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_USER_CREATE_FAIL));
            }
        } else {
            order.setUserId(userOriginal.getId());
        }

        //将订单下的产品的名称关联到订单项的名称中
        order.getOrderItems().get(0).setName(order.getOrderItems().get(0).getTourism().getName());

        Optional<Order> orderOptional = null;
        try {
            orderOptional = orderService.addOrder(order);
        } catch (BaseSystemException e) {
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_CREATE_FAIL.custom(e.getError().getErrorMessage())));
        }
        //下单成功后发送短信
        List<String> mobiles = new ArrayList<>();
        mobiles.add(orderOptional.get().getMobile());
        String params = DateUtil.convertDateToStr(orderOptional.get().getOrderItems().get(0).getDate(), "yyyy-MM-dd") + "," + orderOptional.get().getOrderItems().get(0).getName();
        String key = SMSTemplateEnum.PAIDSUCCESSCONTACTS.getKey();
        try {
            ismsService.send(threadPoolTaskExecutor, mobiles, key, params);
        } catch (Exception e) {
            //do nothing....
        }
        Tourism tourism = order.getOrderItems().get(0).getTourism();
        return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS),
                new ResponseBodyTypeForTransOrder(order.getSourceOrderId(), order.getId().toString(), "1", order.getSourceAmount().toString(), order.getOrderItems().get(0).getCerts().size(), DateUtil.convertDateToStr(order.getOrderItems().get(0).getDate(), "yyyy-MM-dd"),
                        new TourismInfo(tourism.getId(), tourism.getArrive(), tourism.getDepart(), tourism.getArriveTime(), tourism.getDepartTime(), tourism.getName())));
    }

    @Override
    public VerifyTransOrderResponse transQueryOrderById(VerifyTransOrderRequest verifyTransOrderRequest) throws ParseException {
        //获取转化数据,对数据进行组装
        CtripTransOrderBo ctripTransOrderBo = new CtripTransOrderBo(verifyTransOrderRequest);
        //组装成order实体对象
        Order order = null;
        try {
            order = ctripTransOrderBo.asOrderOnlyById();
        } catch (BaseSystemException e) {
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.DATA_PARSE_EXCEPTION.custom(e.getExtMessage())));
        }

        Optional<Order> orderOptional;
        try {
            orderOptional = orderService.findOrderById(order.getId());
        } catch (Exception e) {
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_QUERY_BY_ID_FAIL));
        }
        if (!orderOptional.isPresent()) {
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_QUERY_BY_ID_FAIL));
        }
        Tourism tourism = orderOptional.get().getOrderItems().get(0).getTourism();
        if (!(orderOptional.get().getOrderStatuses() == null || orderOptional.get().getOrderStatuses().isEmpty())) {
            OrderStatus orderStatus = orderOptional.get().getOrderStatuses().stream().sorted(Comparator.comparing(OrderStatus::getId)).collect(Collectors.toList()).get(orderOptional.get().getOrderStatuses().size() - 1);
            //订单已经成功取消
            if (orderStatus.getStatus() == OrderStatus.Status.CANCELED || orderStatus.getStatus() == OrderStatus.Status.REFUND) {
                return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS),
                        new ResponseBodyTypeForTransOrder(orderOptional.get().getSourceOrderId(), orderOptional.get().getId().toString(), "3", orderOptional.get().getSourceAmount().toString(), orderOptional.get().getOrderItems().get(0).getCerts().size(), DateUtil.convertDateToStr(orderOptional.get().getOrderItems().get(0).getDate(), "yyyy-MM-dd"),
                                new TourismInfo(tourism.getId(), tourism.getArrive(), tourism.getDepart(), tourism.getArriveTime(), tourism.getDepartTime(), tourism.getName())));
            }
            //订单过期
            if (DateUtil.convertStrToDate(DateUtil.convertDateToStr(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd").after(orderOptional.get().getOrderItems().get(0).getDate())
                    && orderStatus.getStatus() == OrderStatus.Status.FINISHED) {
                return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS),
                        new ResponseBodyTypeForTransOrder(orderOptional.get().getSourceOrderId(), orderOptional.get().getId().toString(), "2", orderOptional.get().getSourceAmount().toString(), orderOptional.get().getOrderItems().get(0).getCerts().size(), DateUtil.convertDateToStr(orderOptional.get().getOrderItems().get(0).getDate(), "yyyy-MM-dd"),
                                new TourismInfo(tourism.getId(), tourism.getArrive(), tourism.getDepart(), tourism.getArriveTime(), tourism.getDepartTime(), tourism.getName())));
            }
            if (DateUtil.convertStrToDate(DateUtil.convertDateToStr(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd").after(orderOptional.get().getOrderItems().get(0).getDate())
                    && orderStatus.getStatus() == OrderStatus.Status.MODIFY) {
                return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS),
                        new ResponseBodyTypeForTransOrder(orderOptional.get().getSourceOrderId(), orderOptional.get().getId().toString(), "5", orderOptional.get().getSourceAmount().toString(), orderOptional.get().getOrderItems().get(0).getCerts().size(), DateUtil.convertDateToStr(orderOptional.get().getOrderItems().get(0).getDate(), "yyyy-MM-dd"),
                                new TourismInfo(tourism.getId(), tourism.getArrive(), tourism.getDepart(), tourism.getArriveTime(), tourism.getDepartTime(), tourism.getName())));
            }
            //订单取消申请中,订单状态位修改中,订单还未过期
            if (orderStatus.getStatus() == OrderStatus.Status.MODIFY) {
                return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS),
                        new ResponseBodyTypeForTransOrder(orderOptional.get().getSourceOrderId(), orderOptional.get().getId().toString(), "4", orderOptional.get().getSourceAmount().toString(), orderOptional.get().getOrderItems().get(0).getCerts().size(), DateUtil.convertDateToStr(orderOptional.get().getOrderItems().get(0).getDate(), "yyyy-MM-dd"),
                                new TourismInfo(tourism.getId(), tourism.getArrive(), tourism.getDepart(), tourism.getArriveTime(), tourism.getDepartTime(), tourism.getName())));
            }
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS),
                    new ResponseBodyTypeForTransOrder(orderOptional.get().getSourceOrderId(), orderOptional.get().getId().toString(), "1", orderOptional.get().getSourceAmount().toString(), orderOptional.get().getOrderItems().get(0).getCerts().size(), DateUtil.convertDateToStr(orderOptional.get().getOrderItems().get(0).getDate(), "yyyy-MM-dd"),
                            new TourismInfo(tourism.getId(), tourism.getArrive(), tourism.getDepart(), tourism.getArriveTime(), tourism.getDepartTime(), tourism.getName())));
        } else {
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.ORDER_QUERY_BY_ID_FAIL));
        }
    }

    @Override
    public VerifyTransOrderResponse transQueryOrderList(VerifyTransOrderRequest verifyTransOrderRequest) throws ParseException {
        if (verifyTransOrderRequest.getBody() == null || (verifyTransOrderRequest.getBody().getDate() == null || verifyTransOrderRequest.getBody().getDate().isEmpty()))
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.DATA_PARSE_EXCEPTION));
        Date dateForSearch;
        try {
            dateForSearch = DateUtil.convertStrToDate(verifyTransOrderRequest.getBody().getDate() + " 00:00:00", "yyyy-MM-dd hh:mm:ss");
        } catch (Exception e) {
            return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.DATA_PARSE_EXCEPTION));
        }
        AndConditionSet conditionSet = new AndConditionSet();
        conditionSet.addCondition("source", SysConfig.XIECHENG_CP_SOURCE_ID);
        conditionSet.addCondition("end", DateUtil.convertStrToDate(verifyTransOrderRequest.getBody().getDate() + " 23:59:59","yyyy-MM-dd hh:mm:ss"), Condition.MatchType.lessOrEqual);
        conditionSet.addCondition("start", dateForSearch, Condition.MatchType.greater);
        List<Order> orders = orderService.findOrdersByConditions(conditionSet, IBaseDao.SortBy.ASC);
        Date datetemp = null;
        try {
            datetemp = DateUtil.convertStrToDate(DateUtil.convertDateToStr(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Date dateTempFinal = datetemp;
        List<OrderInfo> orderInfos = new ArrayList<>();
        if (!(orders == null || orders.isEmpty())) {
            orderInfos = orders.stream().map(order -> {
                Tourism tourism = order.getOrderItems().get(0).getTourism();
                if (!(order.getOrderStatuses() == null || order.getOrderStatuses().isEmpty())) {
                    OrderStatus orderStatus = order.getOrderStatuses().stream().sorted(Comparator.comparing(OrderStatus::getId)).collect(Collectors.toList()).get(order.getOrderStatuses().size() - 1);
                    //订单已经成功取消
                    if (orderStatus.getStatus() == OrderStatus.Status.CANCELED || orderStatus.getStatus() == OrderStatus.Status.REFUND) {
                        return new OrderInfo(order.getSourceOrderId(), order.getId().toString(), "3", order.getSourceAmount().toString(), order.getOrderItems().get(0).getCerts().size(), DateUtil.convertDateToStr(order.getOrderItems().get(0).getDate(), "yyyy-MM-dd"), DateUtil.convertDateToStr(order.getCreatedTime(), "yyyy-MM-dd hh:mm:ss"),
                                new TourismInfo(tourism.getId(), tourism.getArrive(), tourism.getDepart(), tourism.getArriveTime(), tourism.getDepartTime(), tourism.getName()));
                    }
                    //订单过期
                    if (dateTempFinal.after(order.getOrderItems().get(0).getDate())
                            && orderStatus.getStatus() == OrderStatus.Status.FINISHED) {
                        return new OrderInfo(order.getSourceOrderId(), order.getId().toString(), "2", order.getSourceAmount().toString(), order.getOrderItems().get(0).getCerts().size(), DateUtil.convertDateToStr(order.getOrderItems().get(0).getDate(), "yyyy-MM-dd"), DateUtil.convertDateToStr(order.getCreatedTime(), "yyyy-MM-dd hh:mm:ss"),
                                new TourismInfo(tourism.getId(), tourism.getArrive(), tourism.getDepart(), tourism.getArriveTime(), tourism.getDepartTime(), tourism.getName()));
                    }
                    if (dateTempFinal.after(order.getOrderItems().get(0).getDate())
                            && orderStatus.getStatus() == OrderStatus.Status.MODIFY) {
                        return new OrderInfo(order.getSourceOrderId(), order.getId().toString(), "5", order.getSourceAmount().toString(), order.getOrderItems().get(0).getCerts().size(), DateUtil.convertDateToStr(order.getOrderItems().get(0).getDate(), "yyyy-MM-dd"), DateUtil.convertDateToStr(order.getCreatedTime(), "yyyy-MM-dd hh:mm:ss"),
                                new TourismInfo(tourism.getId(), tourism.getArrive(), tourism.getDepart(), tourism.getArriveTime(), tourism.getDepartTime(), tourism.getName()));
                    }
                    //订单取消申请中,订单状态位修改中,订单还未过期
                    if (orderStatus.getStatus() == OrderStatus.Status.MODIFY) {
                        return new OrderInfo(order.getSourceOrderId(), order.getId().toString(), "4", order.getSourceAmount().toString(), order.getOrderItems().get(0).getCerts().size(), DateUtil.convertDateToStr(order.getOrderItems().get(0).getDate(), "yyyy-MM-dd"), DateUtil.convertDateToStr(order.getCreatedTime(), "yyyy-MM-dd hh:mm:ss"),
                                new TourismInfo(tourism.getId(), tourism.getArrive(), tourism.getDepart(), tourism.getArriveTime(), tourism.getDepartTime(), tourism.getName()));
                    }
                    return new OrderInfo(order.getSourceOrderId(), order.getId().toString(), "1", order.getSourceAmount().toString(), order.getOrderItems().get(0).getCerts().size(), DateUtil.convertDateToStr(order.getOrderItems().get(0).getDate(), "yyyy-MM-dd"), DateUtil.convertDateToStr(order.getCreatedTime(), "yyyy-MM-dd hh:mm:ss"),
                            new TourismInfo(tourism.getId(), tourism.getArrive(), tourism.getDepart(), tourism.getArriveTime(), tourism.getDepartTime(), tourism.getName()));
                } else {
                    return null;
                }
            }).filter(orderInfo -> orderInfo != null).collect(Collectors.toList());
        }
        return new VerifyTransOrderResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS), new ResponseBodyTypeForTransOrder(orderInfos));
    }
}
