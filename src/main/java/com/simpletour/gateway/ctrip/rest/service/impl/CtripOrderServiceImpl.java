package com.simpletour.gateway.ctrip.rest.service.impl;

import com.simpletour.biz.inventory.IStockBiz;
import com.simpletour.biz.order.IOrderBiz;
import com.simpletour.biz.order.impl.OrderBizImpl;
import com.simpletour.biz.product.IProductBiz;
import com.simpletour.biz.resources.IResourcesBiz;
import com.simpletour.common.core.dao.IBaseDao;
import com.simpletour.common.core.dao.query.condition.AndConditionSet;
import com.simpletour.common.core.dao.query.condition.Condition;
import com.simpletour.common.core.exception.BaseSystemException;
import com.simpletour.domain.inventory.Stock;
import com.simpletour.domain.order.Order;
import com.simpletour.domain.order.OrderItem;
import com.simpletour.domain.order.OrderStatus;
import com.simpletour.domain.user.User;
import com.simpletour.gateway.ctrip.error.CtripOrderError;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderResponse;
import com.simpletour.gateway.ctrip.rest.pojo.bo.CtripOrderBo;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.ResponseBodyType;
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
import javax.swing.text.html.Option;
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


    @Override
    public VerifyOrderResponse verifyOrder(VerifyOrderRequest verifyOrderRequest) {

        //获取转化为实体后的数据,并对数据进行组装
        CtripOrderBo ctripOrderBo = new CtripOrderBo(verifyOrderRequest.getHeader(), verifyOrderRequest.getBody());
        //传入order模块,进行业务单元处理
        Order order = ctripOrderBo.asOrder();
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
        Order order = ctripOrderBo.asOrder();

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

        Optional<Order> orderOptional;
        try {
            orderOptional = orderService.addOrder(order);
        } catch (BaseSystemException e) {
            stockValidForOrder(order);
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
    public VerifyOrderResponse cancelOrder(VerifyOrderRequest verifyOrderRequest) {
        //获取转化为实体后的数据,并对数据进行组装
        CtripOrderBo ctripOrderStatusBo = new CtripOrderBo(verifyOrderRequest.getHeader(), verifyOrderRequest.getBody());
        //传入orderStatus模块,进行业务单元处理
        OrderStatus orderStatus = ctripOrderStatusBo.asOrderStatus();
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

        orderStatus.setOrder(orderOptional.get());

        try {
            orderStatusOptional = orderService.updateOrderStatus(orderStatus);
        } catch (Exception e) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_STATUS_UPDATE_FAILD), new ResponseBodyType(1, "4", 0));
        }
        if (!orderStatusOptional.isPresent()) {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_STATUS_UPDATE_FAILD), new ResponseBodyType(1, "4", 0));
        }
        return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(1, "2", 0));
    }

    @Override
    public VerifyOrderResponse queryOrder(VerifyOrderRequest verifyOrderRequest) throws ParseException {
        //获取转化为实体后的数据,并对数据进行组装
        CtripOrderBo ctripOrderBo = new CtripOrderBo(verifyOrderRequest.getHeader(), verifyOrderRequest.getBody());
        //传入order模块,进行业务单元处理
        Order order = ctripOrderBo.asOrder();

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
            if (orderStatus.getStatus() == OrderStatus.Status.CANCELED) {
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(order.getSourceOrderId(), order.getId().toString(), "", order.getAmount(), orderOptional.get().getOrderItems().get(0).getCerts().size(), orderOptional.get().getOrderItems().get(0).getCerts().size(), 0));
            }
            if (DateUtil.convertStrToDate(DateUtil.convertDateToStr(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd").after(orderOptional.get().getOrderItems().get(0).getDate())) {
                return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(order.getSourceOrderId(), order.getId().toString(), "", order.getAmount(), orderOptional.get().getOrderItems().get(0).getCerts().size(), 0, orderOptional.get().getOrderItems().get(0).getCerts().size()));
            }
        } else {
            return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.ORDER_QUERY_FAILD), new ResponseBodyType(order.getSourceOrderId(), order.getId().toString()));
        }
        return new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.OPERATION_SUCCESS), new ResponseBodyType(order.getSourceOrderId(), order.getId().toString(), "", order.getAmount(), orderOptional.get().getOrderItems().get(0).getCerts().size(), 0, 0));
    }

    @Override
    public VerifyOrderResponse resend(VerifyOrderRequest verifyOrderRequest) {
        //获取转化为实体后的数据,并对数据进行组装
        CtripOrderBo ctripOrderBo = new CtripOrderBo(verifyOrderRequest.getHeader(), verifyOrderRequest.getBody());
        //传入order模块,进行业务单元处理
        Order order = ctripOrderBo.asOrder();

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
