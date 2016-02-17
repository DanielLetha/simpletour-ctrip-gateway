package com.simpletour.gateway.ctrip.rest.service.impl;

import com.simpletour.common.utils.MD5;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.error.CtripOrderError;
import com.simpletour.gateway.ctrip.rest.pojo.*;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.RequestBodyType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.RequestBodyTypeForTrans;
import com.simpletour.gateway.ctrip.rest.service.CtripOrderService;
import com.simpletour.gateway.ctrip.rest.service.CtripTransService;
import com.simpletour.gateway.ctrip.rest.service.CtripValidator;
import com.simpletour.gateway.ctrip.util.StringUtils;
import com.simpletour.gateway.ctrip.util.XMLParseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

/**
 * Created by Mario on 2016/1/3.
 */
@Component
public class CtripValidatorImpl implements CtripValidator {

    @Resource
    private CtripTransService ctripTransService;

    @Resource
    private CtripOrderService ctripOrderService;

    @Value("${xiecheng.signkey}")
    private String signKey;

    /**
     * 校验请求
     *
     * @param request
     * @return
     */
    public VerifyResponse validatePre(String request, String methodName) throws ParseException {
        if (request == null || request.isEmpty()) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED));
        }

        //将去头的xml转换成可以处理的实体类
        VerifyOrderRequest verifyOrderRequest = null;
        VerifyTransRequest verifyTransRequest = null;
        try {
            if (SysConfig.TOURISM_HANDLER.equals(methodName)) {
                verifyTransRequest = XMLParseUtil.convertToJavaBean(request, VerifyTransRequest.class);
            } else if (SysConfig.ORDER_HANDLER.equals(methodName)) {
                verifyOrderRequest = XMLParseUtil.convertToJavaBean(request, VerifyOrderRequest.class);
            } else {
                return new VerifyResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED));
            }
        } catch (Exception e) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.XML_RESOLVE_FAILED));
        }

        //过滤出body信息
        String bodyString = StringUtils.replaceBlank(XMLParseUtil.subBodyStringForXml(request));

        //取文件头转为requestHeaderType实体类
        RequestHeaderType requestHeaderType = SysConfig.ORDER_HANDLER.equals(methodName) ? verifyOrderRequest.getHeader() : (SysConfig.TOURISM_HANDLER.equals(methodName) ? verifyTransRequest.getHeader() : null);
        StringBuffer signTo = new StringBuffer(requestHeaderType.getAccountId());
        signTo.append(requestHeaderType.getServiceName());
        signTo.append(requestHeaderType.getRequestTime());
        try {
            signTo.append(org.bouncycastle.util.encoders.Base64.toBase64String(bodyString.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED));
        }
        signTo.append(requestHeaderType.getVersion());
        signTo.append(signKey);
        //验证签名
        if (!requestHeaderType.getSign().equals(MD5.getMD5String(signTo.toString().getBytes()).toLowerCase())) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.SIGN_ERROR));
        }

        switch (requestHeaderType.getServiceName()) {
            case SysConfig.VERIFY_ORDER_METHOD:
                return ctripOrderService.verifyOrder(verifyOrderRequest);
            case SysConfig.CREATE_ORDER_METHOD:
                return ctripOrderService.createOrder(verifyOrderRequest);
//            case SysConfig.CANCEL_ORDER_METHOD:
//                return ctripOrderService.cancelOrder(verifyOrderRequest);
            case SysConfig.QUERY_ORDER_METHOD:
                return ctripOrderService.queryOrder(verifyOrderRequest);
            case SysConfig.RESEND_METHOD:
                return ctripOrderService.resend(verifyOrderRequest);
            case SysConfig.QUERY_TOURISM_METHOD:
                return ctripTransService.queryTourism(verifyTransRequest);
            default:
                return new VerifyResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED));
        }
    }
}
