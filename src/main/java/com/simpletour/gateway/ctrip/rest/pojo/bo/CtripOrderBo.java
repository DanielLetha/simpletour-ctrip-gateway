package com.simpletour.gateway.ctrip.rest.pojo.bo;

import com.simpletour.common.core.exception.BaseSystemException;
import com.simpletour.domain.order.*;
import com.simpletour.domain.product.Product;
import com.simpletour.domain.product.Tourism;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.RequestBodyType;
import com.simpletour.gateway.ctrip.util.DateUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mario on 2016/1/6.
 */
public class CtripOrderBo {

    /**
     * 请求头实体
     */
    private RequestHeaderType requestHeaderType;
    /**
     * 请求体实体
     */
    private RequestBodyType requestBodyType;

    /**
     * constructor
     */
    CtripOrderBo() {
    }


    public CtripOrderBo(RequestHeaderType requestHeaderType, RequestBodyType requestBodyType) {
        this.requestHeaderType = requestHeaderType;
        this.requestBodyType = requestBodyType;
    }

    public RequestHeaderType getRequestHeaderType() {
        return requestHeaderType;
    }

    public void setRequestHeaderType(RequestHeaderType requestHeaderType) {
        this.requestHeaderType = requestHeaderType;
    }

    public RequestBodyType getRequestBodyType() {
        return requestBodyType;
    }

    public void setRequestBodyType(RequestBodyType requestBodyType) {
        this.requestBodyType = requestBodyType;
    }

    public Order asOrderOnlyById() {
        Order order = new Order();
        if (!(this.requestBodyType.getOtaOrderId() == null || this.requestBodyType.getOtaOrderId().isEmpty())) {
            order.setSourceOrderId(this.requestBodyType.getOtaOrderId());
        }
        if (!(this.requestBodyType.getVendorOrderId() == null || this.requestBodyType.getVendorOrderId().isEmpty())) {
            try {
                order.setId(Long.parseLong(this.requestBodyType.getVendorOrderId()));
            } catch (NumberFormatException e) {
                throw new BaseSystemException("供应商订单号错误");
            }
        }
        return order;
    }


    /**
     * 对order实体进行封装
     *
     * @return
     */
    public Order asOrder() {
        Order order = new Order();
        if (!(this.requestBodyType.getOtaOrderId() == null || this.requestBodyType.getOtaOrderId().isEmpty())) {
            order.setSourceOrderId(this.requestBodyType.getOtaOrderId());
        }
        if (!(this.requestBodyType.getVendorOrderId() == null || this.requestBodyType.getVendorOrderId().isEmpty())) {
            order.setId(Long.parseLong(this.requestBodyType.getVendorOrderId()));
        }
        order.setName(this.requestBodyType.getContactName());
        order.setMobile(this.requestBodyType.getContactMobile());
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
        if (this.requestBodyType.getExtendInfo() != null) {
            if (SysConfig.PRODUCT_TYPE.equals(this.requestBodyType.getExtendInfo().getProductType())) {
                Product product = new Product();
                try {
                    product.setId(Long.parseLong(this.requestBodyType.getProductId()));
                } catch (NumberFormatException e) {
                    throw new BaseSystemException("产品id错误");
                }
                product.setOnline(true);
                orderItem.setProduct(product);
                orderItem.setType(OrderItem.Type.product);
            } else if (SysConfig.TOURISM_TYPE.equals(this.requestBodyType.getExtendInfo().getProductType())) {
                Tourism tourism = new Tourism();
                try {
                    tourism.setId(Long.parseLong(this.requestBodyType.getProductId()));
                } catch (NumberFormatException e) {
                    throw new BaseSystemException("产品id错误");
                }
                tourism.setOnline(true);
                orderItem.setTourism(tourism);
                orderItem.setType(OrderItem.Type.tourism);
            }
        }
        if (!(this.requestBodyType.getPrice() == null || this.requestBodyType.getPrice().isEmpty())) {
            orderItem.setSourcePrice(BigDecimal.valueOf(Double.parseDouble(this.requestBodyType.getPrice())));
        } else {
            throw new BaseSystemException("产品单价不存在");
        }
        orderItem.setQuantity(this.requestBodyType.getCount());
        try {
            orderItem.setDate(DateUtil.convertStrToDate(this.requestBodyType.getUseDate(), "yyyy-MM-dd"));
        } catch (Exception e) {
            throw new BaseSystemException("产品使用日期错误");
        }
        List<Cert> certList = new ArrayList<>();
        if (!(this.requestBodyType.getPassengerInfos() == null || this.requestBodyType.getPassengerInfos().isEmpty())) {
            this.requestBodyType.getPassengerInfos().stream().forEach(passengerInfo -> {
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
                    case "7":
                        cert.setIdType(Cert.IdType.HKM_TRAVEL_PERMIT);
                        break;
                    case "8":
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
        //校验count是否和certs的数量一致
        if (this.requestBodyType.getCount() != null && this.requestBodyType.getCount() >= 1) {
            if (orderItem.getCerts().size() != this.getRequestBodyType().getCount())
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

    /**
     * 对orderStatus对象进行封装
     *
     * @return
     */
    public OrderStatus asOrderStatus() {
        OrderStatus orderStatus = new OrderStatus();
        Order order = new Order();
        try {
            order.setId(Long.parseLong(this.requestBodyType.getVendorOrderId()));
        } catch (Exception e) {
            throw new BaseSystemException("供应商订单号错误");
        }
        //可以不用封装这个字段,由于后台是取order的Id来取order
        order.setSourceOrderId(this.requestBodyType.getOtaOrderId());
        orderStatus.setOrder(order);
        orderStatus.setOperateTime(System.currentTimeMillis());
        orderStatus.setOperation(OrderStatus.Operation.MODIFY);
        orderStatus.setStatus(OrderStatus.Status.MODIFY);
        orderStatus.setRemark("来自携程的订单");
        return orderStatus;
    }

}
