package com.simpletour.gateway.ctrip.task;

import com.simpletour.common.core.dao.IBaseDao;
import com.simpletour.common.core.dao.query.condition.AndConditionSet;
import com.simpletour.common.core.dao.query.condition.Condition;
import com.simpletour.common.core.exception.BaseSystemException;
import com.simpletour.domain.order.Order;
import com.simpletour.domain.order.OrderStatus;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.RequestBodyType;
import com.simpletour.gateway.ctrip.util.DateUtil;
import com.simpletour.service.order.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * Created by Mario on 2016/3/22.
 * 这个类区别于门票的定时器单独写,目的是因为两个业务很独立，
 */
@Component
public class OrderStatusForTransTask {

    @Autowired
    private IOrderService orderService;

    @Transactional
    public void refresh() {
        Date today = new Date();
        AndConditionSet conditionSet = new AndConditionSet();
        conditionSet.addCondition("status", OrderStatus.Status.MODIFY.name());
        conditionSet.addCondition("source", SysConfig.XIECHENG_CP_SOURCE_ID);
        conditionSet.addCondition("useDate", today, Condition.MatchType.lessOrEqual);
        conditionSet.addCondition("useDate", DateUtil.getYesterDay(), Condition.MatchType.greater);
        List<Order> orders = orderService.findOrdersByConditions(conditionSet, IBaseDao.SortBy.ASC);
        if (orders.size() <= 0)
            return;
        orders.forEach(tmp -> {
            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setOperateTime(new Date().getTime());
            orderStatus.setOperation(OrderStatus.Operation.FINISH);
            orderStatus.setStatus(OrderStatus.getStatusByOperation(orderStatus.getOperation()));
            orderStatus.setOrder(tmp);
            orderStatus.setAdminId(2L);
            try {
                orderService.updateOrderStatus(orderStatus);
            } catch (Exception e) {
                //do...nothing
            }
        });
    }
}
