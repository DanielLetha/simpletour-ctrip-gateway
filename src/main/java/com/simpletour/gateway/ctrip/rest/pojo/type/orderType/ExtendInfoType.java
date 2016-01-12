package com.simpletour.gateway.ctrip.rest.pojo.type.orderType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by Jeff.Song on 2015/12/28.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ExtendInfoType {

    /**
     * 产品类型：tourism，product
     */
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
