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
public class VerifyTransResponse extends VerifyResponse {


    /**
     * 返回响应体
     */
    private ResponseBodyTypeForTrans body;

    /**
     * constructor
     */
    VerifyTransResponse() {
    }

    public VerifyTransResponse(ResponseHeaderType header, ResponseBodyTypeForTrans body) {
        this.header = header;
        this.body = body;
    }

    /**
     * setter & getter
     */
    public ResponseBodyTypeForTrans getBody() {
        return body;
    }

    public void setBody(ResponseBodyTypeForTrans body) {
        this.body = body;
    }
}
