package com.simpletour.gateway.ctrip.rest.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestType;

/**
 * Created by Jeff.Song on 2015/12/28.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class VerifyOrderRequest {

    private RequestType request;
}
