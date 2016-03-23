package com.simpletour.gateway.ctrip.rest.pojo.type.transType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Mario on 2016/3/18.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "body")
public class RequestBodyTypeForTransOrder {

    private String otaOrderId;
    private String vendorOrderId;
    private String productId;
    private String price;
    private Integer count;
    private String contactName;
    private String contactMobile;
    /**
     * 该字段用于查询具体哪一天来自携程车票的所有订单,该日期明确指定为订单创建的那天
     */
    private String date;

    /**
     * 订单使用时间
     */
    private String useDate;

    private List<PassengerInfo> passengerInfos;

    public RequestBodyTypeForTransOrder() {
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    @XmlElementWrapper(name = "passengerInfos")
    @XmlElement(name = "passengerInfo", required = true)
    public List<PassengerInfo> getPassengerInfos() {
        return passengerInfos;
    }

    public void setPassengerInfos(List<PassengerInfo> passengerInfos) {
        this.passengerInfos = passengerInfos;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }
}
