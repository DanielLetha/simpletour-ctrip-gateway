package com.simpletour.gateway.ctrip.rest.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.ResponseBodyTypeForTrans;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Mario on 2016/1/12.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "response")
public class VerifyTransResponse {

    /**
     * 返回响应头
     */
    private ResponseHeaderType header;

    /**
     * 返回响应体
     */
    private ResponseBodyTypeForTrans body;

    /**
     * constructor
     */
    VerifyTransResponse(){}

    public VerifyTransResponse(ResponseHeaderType header, ResponseBodyTypeForTrans body) {
        this.header = header;
        this.body = body;
    }

    /**
     * setter & getter
     */
    public ResponseHeaderType getHeader() {
        return header;
    }

    public void setHeader(ResponseHeaderType header) {
        this.header = header;
    }

    public ResponseBodyTypeForTrans getBody() {
        return body;
    }

    public void setBody(ResponseBodyTypeForTrans body) {
        this.body = body;
    }
}
