package com.simpletour.gateway.ctrip.rest.pojo.bo;

import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderResponse;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransResponse;

/**
 * Created by Mario on 2016/1/3.
 */
public class VerifyCtripRequestBo {
    /**
     * 订单
     * 需要验证的请求
     */
    private VerifyOrderRequest verifyOrderRequest;

    private VerifyOrderResponse verifyOrderResponse;

    /**
     * 车次
     * 需要验证的请求
     */
    private VerifyTransRequest verifyTransRequest;

    private VerifyTransResponse verifyTransResponse;


    /**
     * constructor
     */
    VerifyCtripRequestBo() {
    }

    public VerifyCtripRequestBo(VerifyOrderRequest verifyOrderRequest, VerifyOrderResponse verifyOrderResponse) {
        this.verifyOrderRequest = verifyOrderRequest;
        this.verifyOrderResponse = verifyOrderResponse;
    }

    public VerifyCtripRequestBo(VerifyTransRequest verifyTransRequest, VerifyTransResponse verifyTransResponse) {
        this.verifyTransRequest = verifyTransRequest;
        this.verifyTransResponse = verifyTransResponse;
    }

    /**
     * setter & getter
     */
    public VerifyOrderRequest getVerifyOrderRequest() {
        return verifyOrderRequest;
    }

    public void setVerifyOrderRequest(VerifyOrderRequest verifyOrderRequest) {
        this.verifyOrderRequest = verifyOrderRequest;
    }

    public VerifyOrderResponse getVerifyOrderResponse() {
        return verifyOrderResponse;
    }

    public void setVerifyOrderResponse(VerifyOrderResponse verifyOrderResponse) {
        this.verifyOrderResponse = verifyOrderResponse;
    }

    public VerifyTransRequest getVerifyTransRequest() {
        return verifyTransRequest;
    }

    public void setVerifyTransRequest(VerifyTransRequest verifyTransRequest) {
        this.verifyTransRequest = verifyTransRequest;
    }

    public VerifyTransResponse getVerifyTransResponse() {
        return verifyTransResponse;
    }

    public void setVerifyTransResponse(VerifyTransResponse verifyTransResponse) {
        this.verifyTransResponse = verifyTransResponse;
    }
}
