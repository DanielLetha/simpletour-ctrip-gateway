package com.simpletour.gateway.ctrip.rest.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.RequestBodyType;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Jeff.Song on 2015/12/28.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "request")
public class VerifyOrderRequest extends VerifyRequest{

    private RequestBodyType body;

    public RequestBodyType getBody() {
        return body;
    }

    public void setBody(RequestBodyType body) {
        this.body = body;
    }
}
