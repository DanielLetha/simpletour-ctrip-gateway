package com.simpletour.gateway.ctrip.task;

import com.simpletour.common.core.dao.IBaseDao;
import com.simpletour.common.core.dao.query.condition.AndConditionSet;
import com.simpletour.common.core.exception.BaseSystemException;
import com.simpletour.domain.order.Order;
import com.simpletour.domain.order.OrderStatus;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.RequestBodyType;
import com.simpletour.gateway.ctrip.rest.service.CtripCallBackUrl;
import com.simpletour.gateway.ctrip.util.DateUtil;
import com.simpletour.service.order.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mario on 2016/2/29.
 */
@Component
public class OrderStatusTask {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private CtripCallBackUrl ctripCallBackUrl;

    @Transactional
    public void refresh() throws ParseException {
        Date date = DateUtil.convertStrToDate(DateUtil.convertDateToStr(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
        AndConditionSet condition = new AndConditionSet();
        condition.addCondition("status", OrderStatus.Status.MODIFY.toString());
        condition.addCondition("source", SysConfig.XIECHENG_SOURCE_ID);
        List<Order> orders = orderService.findOrdersByConditions(condition, IBaseDao.SortBy.ASC);
        orders = orders.stream().filter(order -> date.after(order.getOrderItems().get(0).getDate())).collect(Collectors.toList());
        if (orders.size() <= 0)
            return;
        try {
            orders.forEach(tmp -> {
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setOperateTime(new Date().getTime());
                orderStatus.setOperation(OrderStatus.Operation.FINISH);
                orderStatus.setStatus(OrderStatus.getStatusByOperation(orderStatus.getOperation()));
                orderStatus.setOrder(tmp);
                orderStatus.setAdminId(2L);
                orderService.updateOrderStatus(orderStatus);
                RequestBodyType requestBodyType = new RequestBodyType(tmp.getSourceOrderId(), tmp.getId().toString(), DateUtil.convertDateToStr(tmp.getOrderItems().get(0).getDate(), "yyyy-MM-dd hh:mm:ss"), tmp.getOrderItems().get(0).getCerts().size(), tmp.getOrderItems().get(0).getCerts().size(), 0);
                try {
                    ctripCallBackUrl.getConsumeOrderCallBack(requestBodyType);
                } catch (UnsupportedEncodingException e) {
                    //do...nothing
                }
            });
        } catch (BaseSystemException e) {
            //do...nothing
        }
    }
}
