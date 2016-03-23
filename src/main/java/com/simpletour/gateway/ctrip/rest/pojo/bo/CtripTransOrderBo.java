package com.simpletour.gateway.ctrip.rest.pojo.bo;

import com.simpletour.common.core.exception.BaseSystemException;
import com.simpletour.domain.order.*;
import com.simpletour.domain.product.Tourism;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.RequestBodyTypeForTransOrder;
import com.simpletour.gateway.ctrip.util.DateUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mario on 2016/3/18.
 */
public class CtripTransOrderBo {
    /**
     * 请求头实体
     */
    private RequestHeaderType requestHeaderType;
    /**
     * 请求实体
     */
    private RequestBodyTypeForTransOrder requestBodyTypeForTransOrder;

    public CtripTransOrderBo() {
    }

    public CtripTransOrderBo(VerifyTransOrderRequest verifyTransOrderRequest) {
        this.requestHeaderType = verifyTransOrderRequest.getHeader();
        this.requestBodyTypeForTransOrder = verifyTransOrderRequest.getBody();
    }

    public RequestHeaderType getRequestHeaderType() {
        return requestHeaderType;
    }

    public void setRequestHeaderType(RequestHeaderType requestHeaderType) {
        this.requestHeaderType = requestHeaderType;
    }

    public RequestBodyTypeForTransOrder getRequestBodyTypeForTransOrder() {
        return requestBodyTypeForTransOrder;
    }

    public void setRequestBodyTypeForTransOrder(RequestBodyTypeForTransOrder requestBodyTypeForTransOrder) {
        this.requestBodyTypeForTransOrder = requestBodyTypeForTransOrder;
    }

    public Order asOrderOnlyById() {
        Order order = new Order();
        if (!(this.requestBodyTypeForTransOrder.getVendorOrderId() == null || this.requestBodyTypeForTransOrder.getVendorOrderId().isEmpty())) {
            try {
                order.setId(Long.parseLong(this.requestBodyTypeForTransOrder.getVendorOrderId()));
            } catch (NumberFormatException e) {
                throw new BaseSystemException("供应商订单号错误");
            }
        }
        return order;
    }

    public Order asOrder() {
        Order order = new Order();
        if (!(this.requestBodyTypeForTransOrder.getOtaOrderId() == null || this.requestBodyTypeForTransOrder.getOtaOrderId().isEmpty())) {
            order.setSourceOrderId(this.requestBodyTypeForTransOrder.getOtaOrderId());
        }
        order.setName(this.requestBodyTypeForTransOrder.getContactName());
        order.setMobile(this.requestBodyTypeForTransOrder.getContactMobile());
        order.setReserveTime(new Date(order.getCreatedTime().getTime() + 5 * 60 * 1000));
        order.setSub(false);
        //来自携程的订单设置为tenantId=1L
        order.setTenantId(1L);

        //设置source
        Source source = new Source();
        try {
            source.setId(Long.parseLong(this.requestHeaderType.getAccountId()));
        } catch (NumberFormatException e) {
            throw new BaseSystemException("渠道id错误");
        }
        order.setSource(source);
        //设置orderItem
        OrderItem orderItem = new OrderItem();
        Tourism tourism = new Tourism();
        try {
            tourism.setId(Long.parseLong(this.requestBodyTypeForTransOrder.getProductId()));
        } catch (NumberFormatException e) {
            throw new BaseSystemException("车次id错误");
        }
        tourism.setOnline(true);
        orderItem.setTourism(tourism);
        orderItem.setType(OrderItem.Type.tourism);
        if (!(this.requestBodyTypeForTransOrder.getPrice() == null || this.requestBodyTypeForTransOrder.getPrice().isEmpty())) {
            orderItem.setSourcePrice(new BigDecimal(this.requestBodyTypeForTransOrder.getPrice()));
        } else {
            throw new BaseSystemException("产品单价不存在");
        }
        orderItem.setQuantity(this.requestBodyTypeForTransOrder.getCount());
        try {
            orderItem.setDate(DateUtil.convertStrToDate(this.requestBodyTypeForTransOrder.getUseDate(), "yyyy-MM-dd"));
        } catch (ParseException e) {
            throw new BaseSystemException("产品使用日期错误");
        }
        List<Cert> certList = new ArrayList<>();
        if (!(this.requestBodyTypeForTransOrder.getPassengerInfos() == null || this.requestBodyTypeForTransOrder.getPassengerInfos().isEmpty())) {
            this.requestBodyTypeForTransOrder.getPassengerInfos().stream().forEach(passengerInfo -> {
                if ((passengerInfo.getCardType() == null || passengerInfo.getCardType().isEmpty()) || (passengerInfo.getCardNo() == null || passengerInfo.getCardNo().isEmpty())
                        || (passengerInfo.getName() == null || passengerInfo.getName().isEmpty()) || (passengerInfo.getMobile() == null || passengerInfo.getMobile().isEmpty()))
                    throw new BaseSystemException("出行人实名制认证信息不正确");
                Cert cert = new Cert();
                cert.setName(passengerInfo.getName());
                cert.setMobile(passengerInfo.getMobile());
                switch (passengerInfo.getCardType()) {
                    case "1":
                        cert.setIdType(Cert.IdType.ID);
                        break;
                    case "2":
                        cert.setIdType(Cert.IdType.PASSPORT);
                        break;
                    case "3":
                        cert.setIdType(Cert.IdType.HKM_TRAVEL_PERMIT);
                        break;
                    case "4":
                        cert.setIdType(Cert.IdType.TW_TRAVEL_PERMIT);
                        break;
                    default:
                        break;
                }
                cert.setIdNo(passengerInfo.getCardNo());
                cert.setOrderItem(orderItem);
                cert.setOrder(order);
                certList.add(cert);
            });
        }
        orderItem.setCerts(certList);
        if (this.requestBodyTypeForTransOrder.getCount() != null && this.requestBodyTypeForTransOrder.getCount() >= 1) {
            if (orderItem.getCerts().size() != this.requestBodyTypeForTransOrder.getCount())
                throw new BaseSystemException("出行人数量不正确");
        } else {
            throw new BaseSystemException("订单数量不正确");
        }

        orderItem.setOrder(order);
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOperation(OrderStatus.Operation.FINISH);
        orderStatus.setStatus(OrderStatus.getStatusByOperation(orderStatus.getOperation()));
        orderStatus.setOrder(order);
        orderStatus.setOperateTime(System.currentTimeMillis());
        List<OrderStatus> orderStatusList = new ArrayList<>();
        orderStatusList.add(orderStatus);
        order.setOrderStatuses(orderStatusList);

        return order;
    }

}
