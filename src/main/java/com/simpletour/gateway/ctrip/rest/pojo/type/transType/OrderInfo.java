package com.simpletour.gateway.ctrip.rest.pojo.type.transType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by Mario on 2016/3/21.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class OrderInfo {
    /**
     * ota订单号
     */
    private String otaOrderId;
    /**
     * 供应商订单号
     */
    private String vendorOrderId;

    /**
     * 已完成,未消费1
     * 已完成,已消费2
     * 已退款3
     * 申请取消中4
     * 申请取消失败5
     */
    private String orderStatus;

    /**
     * 渠道总价
     */
    private String sourceAmount;

    /**
     * 订购产品的数量
     */
    private Integer count;

    /**
     * 订单使用时间
     */
    private String useDate;

    /**
     * 订单创建的时间
     */
    private String createTime;
    /**
     * 关联的行程信息
     */
    private TourismInfo tourismInfo;

    public OrderInfo() {
    }

    public OrderInfo(String otaOrderId, String vendorOrderId, String orderStatus, String sourceAmount, Integer count, String useDate, String createTime, TourismInfo tourismInfo) {
        this.otaOrderId = otaOrderId;
        this.vendorOrderId = vendorOrderId;
        this.orderStatus = orderStatus;
        this.sourceAmount = sourceAmount;
        this.count = count;
        this.useDate = useDate;
        this.createTime = createTime;
        this.tourismInfo = tourismInfo;
    }

    public String getOtaOrderId() {
        return otaOrderId;
    }

    public void setOtaOrderId(String otaOrderId) {
        this.otaOrderId = otaOrderId;
    }

    public String getVendorOrderId() {
        return vendorOrderId;
    }

    public void setVendorOrderId(String vendorOrderId) {
        this.vendorOrderId = vendorOrderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getSourceAmount() {
        return sourceAmount;
    }

    public void setSourceAmount(String sourceAmount) {
        this.sourceAmount = sourceAmount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public TourismInfo getTourismInfo() {
        return tourismInfo;
    }

    public void setTourismInfo(TourismInfo tourismInfo) {
        this.tourismInfo = tourismInfo;
    }
}
