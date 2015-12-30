package com.simpletour.gateway.ctrip.rest.pojo.type;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


/**
 * Created by Jeff.Song on 2015/12/28.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "body")
public class RequestBodyType {

    private String productId;
    private String price;
    private Integer count;
    private String contactName;
    private String contactMobile;
    private String contactIdCardType;//1：身份证；2：护照；3：学生证；4：军人证；6：驾驶证；7：回乡证；8：台胞证；10：港澳通行证；11：国际海员证；20：外国人永久居留证；22：台湾通行证；99：其他
    private String contactIdCardNo;
    private String contactEmail;
    private List<PassengerInfo> passengerInfos;
    private String useDate;
    private String useEndDate;
    private ExtendInfoType extendInfo;

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

    public String getContactIdCardType() {
        return contactIdCardType;
    }

    public void setContactIdCardType(String contactIdCardType) {
        this.contactIdCardType = contactIdCardType;
    }

    public String getContactIdCardNo() {
        return contactIdCardNo;
    }

    public void setContactIdCardNo(String contactIdCardNo) {
        this.contactIdCardNo = contactIdCardNo;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public List<PassengerInfo> getPassengerInfos() {
        return passengerInfos;
    }

    public void setPassengerInfos(List<PassengerInfo> passengerInfos) {
        this.passengerInfos = passengerInfos;
    }

    public String getUseDate() {
        return useDate;
    }

    public void setUseDate(String useDate) {
        this.useDate = useDate;
    }

    public String getUseEndDate() {
        return useEndDate;
    }

    public void setUseEndDate(String useEndDate) {
        this.useEndDate = useEndDate;
    }

    public ExtendInfoType getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(ExtendInfoType extendInfo) {
        this.extendInfo = extendInfo;
    }
}
