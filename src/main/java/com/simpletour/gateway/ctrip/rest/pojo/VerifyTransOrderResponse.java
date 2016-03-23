package com.simpletour.gateway.ctrip.rest.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.ResponseBodyTypeForTransOrder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Mario on 2016/3/18.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "response")
public class VerifyTransOrderResponse extends VerifyResponse {

    /**
     * 返回响应体
     */
    private ResponseBodyTypeForTransOrder body;

    public VerifyTransOrderResponse() {
    }

    public VerifyTransOrderResponse(ResponseHeaderType header) {
        super(header);
    }

    public VerifyTransOrderResponse(ResponseHeaderType header, ResponseBodyTypeForTransOrder body) {
        super(header);
        this.body = body;
    }

    public ResponseBodyTypeForTransOrder getBody() {
        return body;
    }

    public void setBody(ResponseBodyTypeForTransOrder body) {
        this.body = body;
    }
}
