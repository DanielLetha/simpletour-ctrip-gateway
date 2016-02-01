package com.simpletour.gateway.ctrip.rest.pojo;

import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Mario on 2016/1/15.
 */
@XmlRootElement(name = "response")
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

    public VerifyResponse(ResponseHeaderType header) {
        this.header = header;
    }

    public ResponseHeaderType getHeader() {
        return header;
    }

    public void setHeader(ResponseHeaderType header) {
        this.header = header;
    }
}
