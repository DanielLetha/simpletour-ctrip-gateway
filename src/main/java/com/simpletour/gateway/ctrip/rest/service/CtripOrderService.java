package com.simpletour.gateway.ctrip.rest.service;

import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderResponse;

import java.text.ParseException;

/**
 * Created by Mario on 2016/1/3.
 */
public interface CtripOrderService {
    /**
     * 验证订单接口
     *
     * @param verifyOrderRequest 请求实体
     * @return
     */
    VerifyOrderResponse verifyOrder(VerifyOrderRequest verifyOrderRequest);

    /**
     * 订单下单接口
     *
     * @param verifyOrderRequest 请求实体
     * @return
     */
    VerifyOrderResponse createOrder(VerifyOrderRequest verifyOrderRequest);

    /**
     * 订单取消接口
     *
     * @param verifyOrderRequest 请求实体
     * @return
     */
    VerifyOrderResponse cancelOrder(VerifyOrderRequest verifyOrderRequest);

    /**
     * 订单查询接口
     *
     * @param verifyOrderRequest 请求实体
     * @return
     */
    VerifyOrderResponse queryOrder(VerifyOrderRequest verifyOrderRequest) throws ParseException;

    /**
     * 凭证重发接口
     *
     * @param verifyOrderRequest 请求实体
     * @return
     */
    VerifyOrderResponse resend(VerifyOrderRequest verifyOrderRequest);
}
