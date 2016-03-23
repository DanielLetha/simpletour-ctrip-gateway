package com.simpletour.gateway.ctrip.rest.service;

import com.simpletour.gateway.ctrip.rest.pojo.VerifyResponse;

import java.text.ParseException;

/**
 * Created by Mario on 2016/1/15.
 */
public interface CtripValidator {
    /**
     * 校验公共接口:针对携程门票
     *
     * @param request 请求文
     * @return
     */
    VerifyResponse validatePre(String request) throws ParseException;

    /**
     * 检验公共接口:针对携程车票查询行程
     *
     * @param request
     * @return
     */
    VerifyResponse validatePreForTrans(String request);

    /**
     * 检验公共接口:针对携程车票订单
     *
     * @param request
     * @return
     */
    VerifyResponse validatePreForTransOrder(String request) throws ParseException;
}
