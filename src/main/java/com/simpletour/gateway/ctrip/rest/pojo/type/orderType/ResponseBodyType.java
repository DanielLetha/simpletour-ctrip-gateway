package com.simpletour.gateway.ctrip.rest.pojo.type.orderType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;

/**
 * Created by Jeff.Song on 2015/12/29.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ResponseBodyType {

    public enum SmsCodeType {
        VENDOR("供应商短信", 0), OTA_ORDINARY("OTA普通短信", 1), OTA_CERTIFICATION("OTA凭证短信", 2), OTA_DIMENSION("OTA二维码短信", 3);

        private String remark;

        private Integer val;

        SmsCodeType(String remark, Integer val) {
            this.remark = remark;
            this.val = val;
        }

        public String getRemark() {
            return remark;
        }

        public Integer getVal() {
            return val;
        }
    }

    /**
     * 库存数量
     */
    private Integer inventory;

    /**
     * OTA订单号
     */
    private String otaOrderId;

    /**
     * 供应商订单号
     */
    private String vendorOrderId;

    /**
     * 凭证短信发送方式
     * 0、供应商发
     * 1、OTA发普通短信（凭身份证/手机号/订单单号等）(默认)
     * 2、OTA发凭证短信
     * 3、OTA发二维码短信(含凭证码)
     */
    private Integer smsCodeType = SmsCodeType.VENDOR.getVal();

    /**
     * 短信凭证码
     * smsCodeType为2或3时必返回值
     * smsCodeType为3返回值为辅助码,可生成二维码消费
     */
    private String smsCode;

    /**
     * 申请取消成功 2
     * 取消（审核）成功 3
     * 取消（审核）失败 4
     * 已入园5
     * 注意：orderStatus为2或3时resultCode均返回0000
     */
    private String orderStatus;

    /**
     * 实际取消数量
     */
    private Integer cancelCount;

    /**
     * 最长审核时间
     */
    private Integer auditDuration;

    /**
     * 订单总金额
     */
    private BigDecimal amount;

    /**
     * 订单产品总数量
     */
    private Integer count;

    /**
     * 实际使用数量
     */
    private Integer useCount;

    /**
     * constructor
     */
    ResponseBodyType() {
    }

    public ResponseBodyType(String otaOrderId, String vendorOrderId) {
        this.otaOrderId = otaOrderId;
        this.vendorOrderId = vendorOrderId;
    }

    public ResponseBodyType(Integer inventory) {
        this.inventory = inventory;
    }

    public ResponseBodyType(Integer inventory, String otaOrderId, String vendorOrderId, Integer smsCodeType, String smsCode) {
        this.inventory = inventory;
        this.otaOrderId = otaOrderId;
        this.vendorOrderId = vendorOrderId;
        this.smsCodeType = smsCodeType;
        this.smsCode = smsCode;
    }

    public ResponseBodyType(Integer cancelCount, String orderStatus, Integer auditDuration) {
        this.cancelCount = cancelCount;
        this.orderStatus = orderStatus;
        this.auditDuration = auditDuration;
    }

    public ResponseBodyType(String otaOrderId, String vendorOrderId, String orderStatus, BigDecimal amount, Integer count,Integer cancelCount,Integer useCount) {
        this.otaOrderId = otaOrderId;
        this.vendorOrderId = vendorOrderId;
        this.orderStatus = orderStatus;
        this.amount = amount;
        this.count = count;
        this.cancelCount = cancelCount;
        this.useCount = useCount;
    }

    /**
     * getter & setter
     */
    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
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

    public Integer getSmsCodeType() {
        return smsCodeType;
    }

    public void setSmsCodeType(Integer smsCodeType) {
        this.smsCodeType = smsCodeType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getCancelCount() {
        return cancelCount;
    }

    public void setCancelCount(Integer cancelCount) {
        this.cancelCount = cancelCount;
    }

    public Integer getAuditDuration() {
        return auditDuration;
    }

    public void setAuditDuration(Integer auditDuration) {
        this.auditDuration = auditDuration;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }
}
