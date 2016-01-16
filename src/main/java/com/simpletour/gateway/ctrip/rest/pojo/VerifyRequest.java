package com.simpletour.gateway.ctrip.rest.pojo;

import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;

/**
 * Created by Mario on 2016/1/16.
 */
public class VerifyRequest {

    /**
     * 请求头
     */
    protected RequestHeaderType header;

    public RequestHeaderType getHeader() {
        return header;
    }

    public void setHeader(RequestHeaderType header) {
        this.header = header;
    }
}
