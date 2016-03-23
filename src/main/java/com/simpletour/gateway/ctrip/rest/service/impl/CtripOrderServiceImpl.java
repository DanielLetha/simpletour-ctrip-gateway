package com.simpletour.gateway.ctrip.rest.service.impl;

import com.alibaba.fastjson.JSON;
import com.simpletour.biz.inventory.IStockBiz;
import com.simpletour.biz.order.IOrderBiz;
import com.simpletour.biz.product.IProductBiz;
import com.simpletour.common.core.dao.IBaseDao;
import com.simpletour.common.core.dao.query.condition.AndConditionSet;
import com.simpletour.common.core.dao.query.condition.Condition;
import com.simpletour.common.core.exception.BaseSystemException;
import com.simpletour.domain.inventory.Stock;
import com.simpletour.domain.order.Order;
import com.simpletour.domain.order.OrderItem;
import com.simpletour.domain.order.OrderStatus;
import com.simpletour.domain.user.User;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.error.CtripOrderError;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderResponse;
import com.simpletour.gateway.ctrip.rest.pojo.bo.CtripOrderBo;
import com.simpletour.gateway.ctrip.rest.pojo.bo.CtripOrderCallBackBo;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.ResponseBodyType;
import com.simpletour.gateway.ctrip.rest.service.CtripCallBackUrl;
import com.simpletour.gateway.ctrip.rest.service.CtripOrderService;
import com.simpletour.gateway.ctrip.util.DateUtil;
import com.simpletour.service.order.IOrderService;
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
 * Created by Mario on 2016/1/3.
 */
@Service
public class CtripOrderServiceImpl implements CtripOrderService {

    @Resource
    private IOrderBiz orderBiz;

    @Resource
    private IOrderService orderService;

    @Resource
    private IStockBiz stockBiz;

    @Resource
    private ISMSService ismsService;

    @Resource
    private TaskExecutor threadPoolTaskExecutor;

    @Resource
    private UserService userService;

    @Resource
    private IProductBiz iProductBiz;

    @Resource
    private CtripCallBackUrl ctripCallBackUrl;

    private void stockValidForOrder(Order order) {
        order.getOrderItems().stream().forEach(orderItem -> {
            if (orderItem.getTourism() != null && orderItem.getTourism().getId() != null) {
                orderItem.setTourism(iProductBiz.getTourismWithPackagesById(orderItem.getTourism().getId()));
            }
            if (orderItem.getProduct() != null && orderItem.getProduct().getId() != null) {
                orderItem.setProduct(iProductBiz.getProductWithPackagesById(orderItem.getProduct().getId()));
            }
        });
    }

    private Optional<Stock> getStock(Order order) {
        Optional<Stock> stock = stockBiz.getStock(order.getOrderItems().get(0).getType() == OrderItem.Type.tourism ?
                order.getOrderItems().get(0).getTourism() : order.getOrderItems().get(0).getProduct(), order.getOrderItems().get(0).getDate(), true);
        return stock;
    }


    private VerifyOrderResponse validateOrderBasicInfo(Order order) {
        if (order.getSourceOrderId() == null || order.getSourceOrderId().isEmpty())
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.VALIDATE_FAILED.custom("OTA订单号不能为空")), null);
        if (order.getName() == null || order.getName().isEmpty())
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.VALIDATE_FAILED.custom("联系人姓名不能为空")), null);
        if (order.getMobile() == null || order.getMobile().isEmpty())
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.VALIDATE_FAILED.custom("联系人手机号不能为空")), null);
        return null;
    }

    @Override
    public VerifyOrderResponse verifyOrder(VerifyOrderRequest verifyOrderRequest) {

        //获取转化为实体后的数据,并对数据进行组装
        CtripOrderBo ctripOrderBo = new CtripOrderBo(verifyOrderRequest.getHeader(), verifyOrderRequest.getBody());
        //传入order模块,进行业务单元处理
        Order order = null;
        try {
            order = ctripOrderBo.asOrder();
        } catch (BaseSystemException e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.DATA_PARSE_EXCEPTION.custom(e.getExtMessage())), null);
        }
        //验证订单基本信息
        VerifyOrderResponse validate = this.validateOrderBasicInfo(order);
        if (validate != null) {
            return validate;
        }
        try {
            orderBiz.validateOrder(order);
        } catch (BaseSystemException e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.VALIDATE_FAILED.custom(e.getError().getErrorMessage())), null);
        }
        //验证车次是否排班,并设置可用库存为0
        if (!orderService.validateIsBusPlanAvailable(order.getOrderItems().get(0), order)) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.VALIDATE_FAILED), new ResponseBodyType(0));
        }
        //验证库存
        //先检查库存还有多少
        if (!getStock(order).isPresent()) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.STOCK_NOT_ENOUGH), new ResponseBodyType(0));
        }
        try {
            stockBiz.checkDemandQuantity(ctripOrderBo.asOrder());
        } catch (BaseSystemException e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.STOCK_NOT_ENOUGH), new ResponseBodyType(getStock(order).get().getAvailableQuantity()));
        }
        return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(getStock(order).get().getAvailableQuantity()));
    }

    @Override
    @Transactional
    public VerifyOrderResponse createOrder(VerifyOrderRequest verifyOrderRequest) {
        //获取转化为实体后的数据,并对数据进行组装
        CtripOrderBo ctripOrderBo = new CtripOrderBo(verifyOrderRequest.getHeader(), verifyOrderRequest.getBody());
        //传入order模块,进行业务单元处理
        Order order = null;
        try {
            order = ctripOrderBo.asOrder();
        } catch (BaseSystemException e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.DATA_PARSE_EXCEPTION.custom(e.getExtMessage())), null);
        }
        //验证订单基本信息
        VerifyOrderResponse validate = this.validateOrderBasicInfo(order);
        if (validate != null) {
            return validate;
        }
        //验证订单信息,防止库存溢出
        try {
            orderBiz.validateOrder(order);
        } catch (BaseSystemException e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.VALIDATE_FAILED.custom(e.getError().getErrorMessage())), null);
        }

        //验证传进来的otaOrderId是否存在并且是否是数据库已存在
        if (!(order.getSourceOrderId() == null || order.getSourceOrderId().isEmpty())) {
            AndConditionSet andConditionSet = new AndConditionSet();
            andConditionSet.addCondition("sourceOrderId", order.getSourceOrderId(), Condition.MatchType.eq);
            List<Order> orders = orderService.findOrdersByConditions(andConditionSet, IBaseDao.SortBy.ASC);
            orders.stream().forEach(order1 -> stockValidForOrder(order1));
            if (!(orders == null || orders.isEmpty())) {
                if (!getStock(orders.get(0)).isPresent()) {
                    return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.STOCK_NOT_ENOUGH), new ResponseBodyType(0));
                }
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS.custom("该订单已经存在")), new ResponseBodyType(getStock(orders.get(0)).get().getAvailableQuantity(), order.getSourceOrderId(), orders.get(0).getId().toString(), ResponseBodyType.SmsCodeType.VENDOR.getVal(), ""));
            }
        } else {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_SOURCE_ID_NULL), new ResponseBodyType(0));
        }
        //验证传进来的用户是否存在,如果有用户则通过,没有则创建用户
        User userOrginal = userService.getAvailableUserByMobile(order.getMobile());
        if (userOrginal == null) {
            try {
                User user = userService.addUser(new User(order.getMobile(), order.getMobile(), order.getMobile(), order.getMobile().trim().substring(order.getMobile().length() - 6), "web-xiecheng", User.Status.ACTIVE));
                order.setUserId(user.getId());
            } catch (Exception e) {
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.VALIDATE_FAILED), new ResponseBodyType(0));
            }
        } else {
            order.setUserId(userOrginal.getId());
        }

        //将订单下的产品的名称关联到订单项的名称中
        order.getOrderItems().get(0).setName(order.getOrderItems().get(0).getType() == OrderItem.Type.product ? order.getOrderItems().get(0).getProduct().getName() : order.getOrderItems().get(0).getTourism().getName());

        Optional<Order> orderOptional;
        try {
            orderOptional = orderService.addOrder(order);
        } catch (BaseSystemException e) {
            //验证库存
            //先检查库存还有多少
            if (!getStock(order).isPresent()) {
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.STOCK_NOT_ENOUGH), new ResponseBodyType(0));
            }
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.VALIDATE_FAILED.custom(e.getError().getErrorMessage())), new ResponseBodyType(getStock(order).get().getAvailableQuantity()));
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
        return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(getStock(order).get().getAvailableQuantity(), orderOptional.get().getSourceOrderId(), orderOptional.get().getId().toString(), ResponseBodyType.SmsCodeType.VENDOR.getVal(), ""));
    }

    @Override
    @Transactional
    public VerifyOrderResponse cancelOrder(VerifyOrderRequest verifyOrderRequest) throws ParseException {
        //获取转化为实体后的数据,并对数据进行组装
        CtripOrderBo ctripOrderStatusBo = new CtripOrderBo(verifyOrderRequest.getHeader(), verifyOrderRequest.getBody());
        //传入orderStatus模块,进行业务单元处理
        OrderStatus orderStatus = null;
        try {
            orderStatus = ctripOrderStatusBo.asOrderStatus();
        } catch (BaseSystemException e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.DATA_PARSE_EXCEPTION.custom(e.getExtMessage())), null);
        }
        Optional<OrderStatus> orderStatusOptional;
        //查询一次order
        Optional<Order> orderOptional;
        try {
            orderOptional = orderService.findOrderById(orderStatus.getOrder().getId());
        } catch (Exception e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_ID_EMPTY), null);
        }
        if (!orderOptional.isPresent()) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_ID_EMPTY), null);
        }

        //判断当前订单状态,当前是已取消或者是已退款，当前是修改中，当前是订单已经使用，都不会主动去更新订单状态
        if (!(orderOptional.get().getOrderStatuses() == null || orderOptional.get().getOrderStatuses().isEmpty())) {
            OrderStatus orderStatusOriginal = orderOptional.get().getOrderStatuses().stream().sorted(Comparator.comparing(OrderStatus::getId)).collect(Collectors.toList()).get(orderOptional.get().getOrderStatuses().size() - 1);
            //订单已经成功取消
            if (orderStatusOriginal.getStatus() == OrderStatus.Status.CANCELED || orderStatusOriginal.getStatus() == OrderStatus.Status.REFUND) {
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(orderOptional.get().getOrderItems().get(0).getCerts().size(), "3", 8));
            }
            //如果申请取消的时间在订单已经使用之后，认为订单不能取消，申请取消订单失败
            if (DateUtil.convertStrToDate(DateUtil.convertDateToStr(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd").after(orderOptional.get().getOrderItems().get(0).getDate())
                    && orderStatusOriginal.getStatus() == OrderStatus.Status.MODIFY) {
                //构造回调函数，用于告知携程取消审核失败
                ctripCallBackUrl.getCancelOrderCallBack(JSON.toJSONString(new CtripOrderCallBackBo(orderOptional.get().getId(), SysConfig.CANCEL_TYPE_FAIL)));
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_HAS_BEEN_USED), new ResponseBodyType(0, "4", 8));
            }
            //如果申请取消的时间在订单使用之后，并且这个订单的状态为已完成
            if (DateUtil.convertStrToDate(DateUtil.convertDateToStr(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd").after(orderOptional.get().getOrderItems().get(0).getDate())
                    && orderStatusOriginal.getStatus() == OrderStatus.Status.FINISHED) {
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_HAS_BEEN_USED), new ResponseBodyType(0, "4", 8));
            }
            //订单取消申请中,订单状态为修改中
            if (orderStatusOriginal.getStatus() == OrderStatus.Status.MODIFY) {
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(orderOptional.get().getOrderItems().get(0).getCerts().size(), "2", 8));
            }
        }

        orderStatus.setOrder(orderOptional.get());

        try {
            orderStatusOptional = orderService.updateOrderStatus(orderStatus);
        } catch (Exception e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_STATUS_UPDATE_FAILD), new ResponseBodyType(orderOptional.get().getOrderItems().get(0).getCerts().size(), "4", 8));
        }
        if (!orderStatusOptional.isPresent()) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_STATUS_UPDATE_FAILD), new ResponseBodyType(orderOptional.get().getOrderItems().get(0).getCerts().size(), "4", 8));
        }
        return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(orderOptional.get().getOrderItems().get(0).getCerts().size(), "2", 8));
    }

    @Override
    public VerifyOrderResponse queryOrder(VerifyOrderRequest verifyOrderRequest) throws ParseException {
        //获取转化为实体后的数据,并对数据进行组装
        CtripOrderBo ctripOrderBo = new CtripOrderBo(verifyOrderRequest.getHeader(), verifyOrderRequest.getBody());
        //传入order模块,进行业务单元处理
        Order order = null;
        try {
            order = ctripOrderBo.asOrderOnlyById();
        } catch (BaseSystemException e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.DATA_PARSE_EXCEPTION.custom(e.getExtMessage())), null);
        }

        Optional<Order> orderOptional;
        try {
            orderOptional = orderService.findOrderById(order.getId());
        } catch (Exception e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_ID_NULL), new ResponseBodyType(order.getSourceOrderId(), order.getId().toString()));
        }
        if (!orderOptional.isPresent()) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_QUERY_FAILD), new ResponseBodyType(order.getSourceOrderId(), order.getId().toString()));
        }
        if (!(orderOptional.get().getOrderStatuses() == null || orderOptional.get().getOrderStatuses().isEmpty())) {
            OrderStatus orderStatus = orderOptional.get().getOrderStatuses().stream().sorted(Comparator.comparing(OrderStatus::getId)).collect(Collectors.toList()).get(orderOptional.get().getOrderStatuses().size() - 1);
            //订单已经成功取消
            if (orderStatus.getStatus() == OrderStatus.Status.CANCELED || orderStatus.getStatus() == OrderStatus.Status.REFUND) {
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(order.getSourceOrderId(), order.getId().toString(), "3", order.getAmount(), orderOptional.get().getOrderItems().get(0).getCerts().size(), orderOptional.get().getOrderItems().get(0).getCerts().size(), 0));
            }
            //订单过期的情况
            if (DateUtil.convertStrToDate(DateUtil.convertDateToStr(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd").after(orderOptional.get().getOrderItems().get(0).getDate())
                    && orderStatus.getStatus() == OrderStatus.Status.FINISHED) {
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(order.getSourceOrderId(), order.getId().toString(), "5", order.getAmount(), orderOptional.get().getOrderItems().get(0).getCerts().size(), 0, orderOptional.get().getOrderItems().get(0).getCerts().size()));
            }
            if (DateUtil.convertStrToDate(DateUtil.convertDateToStr(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd").after(orderOptional.get().getOrderItems().get(0).getDate())
                    && orderStatus.getStatus() == OrderStatus.Status.MODIFY) {
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(order.getSourceOrderId(), order.getId().toString(), "4", order.getAmount(), orderOptional.get().getOrderItems().get(0).getCerts().size(), 0, orderOptional.get().getOrderItems().get(0).getCerts().size()));
            }
            //订单取消申请中,订单状态位修改中,订单还未过期
            if (orderStatus.getStatus() == OrderStatus.Status.MODIFY) {
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(order.getSourceOrderId(), order.getId().toString(), "2", order.getAmount(), orderOptional.get().getOrderItems().get(0).getCerts().size(), 0, 0));
            }
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(order.getSourceOrderId(), order.getId().toString(), "1", order.getAmount(), orderOptional.get().getOrderItems().get(0).getCerts().size(), 0, 0));
        } else {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_QUERY_FAILD), new ResponseBodyType(order.getSourceOrderId(), order.getId().toString()));
        }
    }

    @Override
    public VerifyOrderResponse resend(VerifyOrderRequest verifyOrderRequest) {
        //获取转化为实体后的数据,并对数据进行组装
        CtripOrderBo ctripOrderBo = new CtripOrderBo(verifyOrderRequest.getHeader(), verifyOrderRequest.getBody());
        //传入order模块,进行业务单元处理
        Order order = null;
        try {
            order = ctripOrderBo.asOrderOnlyById();
        } catch (BaseSystemException e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.DATA_PARSE_EXCEPTION.custom(e.getExtMessage())), null);
        }

        //查询一次订单的基本信息,用于封装短信服务
        Optional<Order> orderOptional;
        try {
            orderOptional = orderService.findOrderById(order.getId());
        } catch (Exception e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_ID_EMPTY), null);
        }
        if (!orderOptional.isPresent()) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_NULL), null);
        }
        List<String> mobiles = new ArrayList<>();
        mobiles.add(orderOptional.get().getMobile());
        String params = DateUtil.convertDateToStr(orderOptional.get().getOrderItems().get(0).getDate(), "yyyy-MM-dd") + "," + orderOptional.get().getOrderItems().get(0).getName();
        String key = SMSTemplateEnum.PAIDSUCCESSCONTACTS.getKey();
        try {
            ismsService.send(threadPoolTaskExecutor, mobiles, key, params);
        } catch (Exception e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.MESSAGE_SEND_FAILED), null);
        }
        return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), null);
    }
}
