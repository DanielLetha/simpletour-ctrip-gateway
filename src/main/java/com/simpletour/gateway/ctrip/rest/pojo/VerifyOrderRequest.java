package com.simpletour.gateway.ctrip.rest.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.simpletour.common.utils.MD5;
import com.simpletour.domain.order.Order;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.RequestBodyType;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.util.DateUtil;
import com.simpletour.gateway.ctrip.util.StringUtils;
import com.simpletour.gateway.ctrip.util.XMLParseUtil;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by Jeff.Song on 2015/12/28.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "request")
public class VerifyOrderRequest extends VerifyRequest {

    private RequestBodyType body;

    public RequestBodyType getBody() {
        return body;
    }

    public void setBody(RequestBodyType body) {
        this.body = body;
    }

    /**
     * 构造请求用于请求携程接口
     *
     * @param order
     * @param signKey
     * @return
     * @throws UnsupportedEncodingException
     */
    public VerifyOrderRequest buildRequest(Order order, String signKey,String type ) throws UnsupportedEncodingException {
        this.header = new RequestHeaderType(SysConfig.SIMPLETOUR_ACCOUNT_ID, SysConfig.NOTICE_ORDER_CANCEL, DateUtil.convertDateToStr(new Date(), "yyyy-MM-dd hh:mm:ss"), SysConfig.XIECHENG_VERSION);
        Integer orderStatus = 0;
        if(SysConfig.CANCEL_TYPE_SUCCESS.equals(type)){
            orderStatus = 3;
        }else if(SysConfig.CANCEL_TYPE_FAIL.equals(type)){
            orderStatus = 4;
        }
        this.body = new RequestBodyType(order.getSourceOrderId(), order.getId().toString(), order.getOrderItems().get(0).getCerts().size(), orderStatus);

        String xml = XMLParseUtil.convertToXml(this);
        String xmlBodyString = StringUtils.formatXml(XMLParseUtil.subBodyStringForXml(xml));

        String xmlBase64 = org.bouncycastle.util.encoders.Base64.toBase64String(xmlBodyString.getBytes("UTF-8"));
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.header.getAccountId());
        buffer.append(this.header.getServiceName());
        buffer.append(this.header.getRequestTime());
        buffer.append(xmlBase64);
        buffer.append(header.getVersion());
        buffer.append(signKey);
        String sign = MD5.getMD5String(buffer.toString().getBytes()).toLowerCase();
        this.header.setSign(sign);

        return this;
    }

}
