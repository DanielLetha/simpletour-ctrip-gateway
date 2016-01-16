package com.simpletour.gateway.ctrip.rest.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.ResponseBodyType;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Jeff.Song on 2015/12/28.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "response")
public class VerifyOrderResponse extends VerifyResponse {

    /**
     * 库存信息，不是必须字段，但当库存不足时，需要反馈库存数量
     */
    private ResponseBodyType body;

    /**
     * constructor
     */
    public VerifyOrderResponse() {
    }

    public VerifyOrderResponse(ResponseHeaderType header, ResponseBodyType body) {
        this.header = header;
        this.body = body;
    }

    /**
     * setter & getter
     */
    public ResponseBodyType getBody() {
        return body;
    }

    public void setBody(ResponseBodyType body) {
        this.body = body;
    }
}
