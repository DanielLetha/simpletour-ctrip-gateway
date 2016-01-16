package com.simpletour.gateway.ctrip.rest.pojo;

import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;

/**
 * Created by Mario on 2016/1/15.
 */
public class VerifyResponse {

    /**
     * 返回响应头
     */
    protected ResponseHeaderType header;

    /**
     * constructor
     */
    VerifyResponse() {
    }

    public VerifyResponse(ResponseHeaderType responseHeaderType) {
    }

    public ResponseHeaderType getHeader() {
        return header;
    }

    public void setHeader(ResponseHeaderType header) {
        this.header = header;
    }
}
