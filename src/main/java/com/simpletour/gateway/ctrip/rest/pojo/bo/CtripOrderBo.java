package com.simpletour.gateway.ctrip.rest.pojo.bo;

import com.simpletour.domain.order.*;
import com.simpletour.domain.product.Product;
import com.simpletour.domain.product.Tourism;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.RequestBodyType;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.util.DateUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
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
        order.setSub(false);
        //来自携程的订单设置为tenantId=1L
        order.setTenantId(1L);
        //设置source
        Source source = new Source();
        source.setId(Long.parseLong(this.requestHeaderType.getAccountId()));
        order.setSource(source);
        //设置orderItem
        OrderItem orderItem = new OrderItem();
        if (SysConfig.PRODUCT_TYPE.equals(this.requestBodyType.getExtendInfo().getProductType())) {
            Product product = new Product();
            product.setId(Long.parseLong(this.requestBodyType.getProductId()));
            product.setOnline(true);
            orderItem.setProduct(product);
            orderItem.setType(OrderItem.Type.product);
        } else if (SysConfig.TOURISM_TYPE.equals(this.requestBodyType.getExtendInfo().getProductType())) {
            Tourism tourism = new Tourism();
            tourism.setId(Long.parseLong(this.requestBodyType.getProductId()));
            tourism.setOnline(true);
            orderItem.setTourism(tourism);
            orderItem.setType(OrderItem.Type.tourism);
        }
        //TODO.....后台的价格和订购数量都是通过查询数据库或者计算得出，这个地方对接时需要注意
        if (!(this.requestBodyType.getPrice() == null || this.requestBodyType.getPrice().isEmpty())) {
            orderItem.setSourcePrice(BigDecimal.valueOf(Long.parseLong(this.requestBodyType.getPrice())));
        }
        orderItem.setQuantity(this.requestBodyType.getCount());
        try {
            orderItem.setDate(DateUtil.convertStrToDate(this.requestBodyType.getUseDate(), "yyyy-MM-dd"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Cert> certList = new ArrayList<>();
        if (!(this.requestBodyType.getPassengerInfos() == null || this.requestBodyType.getPassengerInfos().isEmpty())) {
            this.requestBodyType.getPassengerInfos().stream().forEach(passengerInfo -> {
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
        orderItem.setOrder(order);
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOperation(OrderStatus.Operation.BOOKING);
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
        order.setId(Long.parseLong(this.requestBodyType.getVendorOrderId()));
        //可以不用封装这个字段,由于后台是取order的Id来取order
        order.setSourceOrderId(this.requestBodyType.getOtaOrderId());
        orderStatus.setOrder(order);
        orderStatus.setOperateTime(System.currentTimeMillis());
        orderStatus.setOperation(OrderStatus.Operation.CANCEL);
        orderStatus.setStatus(OrderStatus.Status.CANCELED);
        orderStatus.setRemark("来自携程的订单");
        return orderStatus;
    }

}
