package com.simpletour.gateway.ctrip.rest.service;

import com.simpletour.gateway.ctrip.rest.pojo.VerifyResponse;

/**
 * Created by Mario on 2016/2/19.
 */
public interface CtripCallBackUrl {
    /**
     * 或许从携程那边返回取消订单成功的回调接口信息
     * @param request
     * @return
     */
    VerifyResponse getCancelOrderCallBack(String request);
}
