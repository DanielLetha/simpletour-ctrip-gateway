package com.simpletour.gateway.ctrip.rest.service;

import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransResponse;

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
}
