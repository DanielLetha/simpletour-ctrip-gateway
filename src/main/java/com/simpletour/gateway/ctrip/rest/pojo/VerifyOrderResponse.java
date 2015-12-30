package com.simpletour.gateway.ctrip.rest.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseBodyType;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Jeff.Song on 2015/12/28.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "response")
public class VerifyOrderResponse {

    private ResponseHeaderType header;


    /**
     * 库存信息，不是必须字段，但当库存不足时，需要反馈库存数量
     */
    private ResponseBodyType body;

    public ResponseHeaderType getHeader() {
        return header;
    }

    public void setHeader(ResponseHeaderType header) {
        this.header = header;
    }

    public ResponseBodyType getBody() {
        return body;
    }

    public void setBody(ResponseBodyType body) {
        this.body = body;
    }
}
