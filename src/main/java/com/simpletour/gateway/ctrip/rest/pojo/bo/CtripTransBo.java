package com.simpletour.gateway.ctrip.rest.pojo.bo;

import com.simpletour.domain.product.Tourism;
import com.simpletour.domain.traveltrans.BusNo;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransResponse;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.RequestBodyTypeForTrans;

/**
 * Created by Mario on 2016/1/12.
 */
public class CtripTransBo {
    /**
     * 请求头实体
     */
    private RequestHeaderType requestHeaderType;
    /**
     * 请求体实体
     */
    private RequestBodyTypeForTrans requestBodyTypeForTrans;

    /**
     * constructor
     */
    CtripTransBo() {
    }

    public CtripTransBo(RequestHeaderType requestHeaderType, RequestBodyTypeForTrans requestBodyTypeForTrans) {
        this.requestHeaderType = requestHeaderType;
        this.requestBodyTypeForTrans = requestBodyTypeForTrans;
    }

    public RequestHeaderType getRequestHeaderType() {
        return requestHeaderType;
    }

    public void setRequestHeaderType(RequestHeaderType requestHeaderType) {
        this.requestHeaderType = requestHeaderType;
    }

    public RequestBodyTypeForTrans getRequestBodyTypeForTrans() {
        return requestBodyTypeForTrans;
    }

    public void setRequestBodyTypeForTrans(RequestBodyTypeForTrans requestBodyTypeForTrans) {
        this.requestBodyTypeForTrans = requestBodyTypeForTrans;
    }

    public Tourism asTourism(){
        Tourism tourism = new Tourism();
        tourism.setDepart(this.requestBodyTypeForTrans.getDepart());
        tourism.setArrive(this.requestBodyTypeForTrans.getArrive());
        tourism.setId(this.requestBodyTypeForTrans.getTourismId());

        return tourism;
    }

}

