package com.simpletour.gateway.ctrip.rest.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.RequestBodyTypeForTransOrder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Mario on 2016/3/18.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "request")
public class VerifyTransOrderRequest extends VerifyRequest {

    private RequestBodyTypeForTransOrder body;

    public RequestBodyTypeForTransOrder getBody() {
        return body;
    }

    public void setBody(RequestBodyTypeForTransOrder body) {
        this.body = body;
    }
}
