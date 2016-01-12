package com.simpletour.gateway.ctrip.rest.service;

import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderResponse;

/**
 * Created by Mario on 2016/1/3.
 */
public interface CtripOrderService {
    /**
     * 验证订单接口
     *
     * @param request 请求文
     * @return
     */
    VerifyOrderResponse verifyOrder(String request);

    /**
     * 订单下单接口
     *
     * @param request 请求文
     * @return
     */
    VerifyOrderResponse createOrder(String request);

    /**
     * 订单取消接口
     *
     * @param request 请求文
     * @return
     */
    VerifyOrderResponse cancelOrder(String request);

    /**
     * 订单查询接口
     *
     * @param request 请求文
     * @return
     */
    VerifyOrderResponse queryOrder(String request);

    /**
     * 凭证重发接口
     *
     * @param request 请求文
     * @return
     */
    VerifyOrderResponse resend(String request);
}
