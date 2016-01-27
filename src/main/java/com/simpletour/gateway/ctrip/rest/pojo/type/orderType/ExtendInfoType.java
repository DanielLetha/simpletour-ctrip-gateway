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
    private String productType;

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}
