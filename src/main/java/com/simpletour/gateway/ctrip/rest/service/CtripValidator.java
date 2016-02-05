package com.simpletour.gateway.ctrip.rest.service;

import com.simpletour.gateway.ctrip.rest.pojo.VerifyRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyResponse;

/**
 * Created by Mario on 2016/1/15.
 */
public interface CtripValidator {
    /**
     * 校验公共接口
     *
     * @param request    请求文
     * @param methodName 方法名
     * @return
     */
    VerifyResponse validatePre(String request, String methodName);
}
