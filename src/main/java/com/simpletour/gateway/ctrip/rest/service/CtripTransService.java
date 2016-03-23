package com.simpletour.gateway.ctrip.rest.service;

import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransOrderResponse;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransResponse;

import java.text.ParseException;

/**
 * Created by Mario on 2016/1/12.
 */
public interface CtripTransService {
    /**
     * 查询车次接口
     *
     * @param verifyTransRequest 请求实体
     * @return
     */
    VerifyTransResponse queryTourism(VerifyTransRequest verifyTransRequest);

    /**
     * @param verifyTransOrderRequest 请求实体
     * @return
     */
    VerifyTransOrderResponse transVerifyOrder(VerifyTransOrderRequest verifyTransOrderRequest);

    /**
     * 车票创建订单
     *
     * @param verifyTransOrderRequest 请求实体
     * @return
     */
    VerifyTransOrderResponse transCreateOrder(VerifyTransOrderRequest verifyTransOrderRequest);

    /**
     * 根据供应商订单id查询订单
     *
     * @param verifyTransOrderRequest 请求实体
     * @return
     */
    VerifyTransOrderResponse transQueryOrderById(VerifyTransOrderRequest verifyTransOrderRequest) throws ParseException;

    /**
     * 根据日期查询订单列表
     *
     * @param verifyTransOrderRequest 请求实体
     * @return
     */
    VerifyTransOrderResponse transQueryOrderList(VerifyTransOrderRequest verifyTransOrderRequest) throws ParseException;

}
