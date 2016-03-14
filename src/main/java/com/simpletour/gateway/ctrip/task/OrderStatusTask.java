package com.simpletour.gateway.ctrip.task;

import com.simpletour.common.core.dao.IBaseDao;
import com.simpletour.common.core.dao.query.condition.AndConditionSet;
import com.simpletour.common.core.dao.query.condition.Condition;
import com.simpletour.common.core.dao.query.condition.OrConditionSet;
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
        Date today = new Date();
        AndConditionSet condition = new AndConditionSet();
        condition.addCondition(new OrConditionSet().addCondition("status", OrderStatus.Status.MODIFY.name()).addCondition("status", OrderStatus.Status.FINISHED.name()));
        condition.addCondition(new OrConditionSet().addCondition("source", SysConfig.XIECHENG_MP_SOURCE_ID).addCondition("source", SysConfig.XIECHENG_CP_SOURCE_ID));
        condition.addCondition("useDate", today, Condition.MatchType.less);
        //指定两天
        condition.addCondition("useDate", DateUtil.getYesterDay(), Condition.MatchType.greater);
        List<Order> orders = orderService.findOrdersByConditions(condition, IBaseDao.SortBy.ASC);
        if (orders.size() <= 0)
            return;
        try {
            orders.forEach(tmp -> {
                if (tmp.getLastestStatus().getStatus() != OrderStatus.Status.FINISHED) {
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
                }
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
