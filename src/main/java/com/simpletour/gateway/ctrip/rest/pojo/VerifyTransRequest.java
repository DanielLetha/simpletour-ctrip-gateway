package com.simpletour.gateway.ctrip.rest.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.RequestBodyTypeForTrans;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Mario on 2016/1/12.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "request")
public class VerifyTransRequest {

    private RequestHeaderType header;

    private RequestBodyTypeForTrans body;

    public RequestHeaderType getHeader() {
        return header;
    }

    public void setHeader(RequestHeaderType header) {
        this.header = header;
    }

    public RequestBodyTypeForTrans getBody() {
        return body;
    }

    public void setBody(RequestBodyTypeForTrans body) {
        this.body = body;
    }
}
