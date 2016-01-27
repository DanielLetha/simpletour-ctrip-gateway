package com.simpletour.gateway.ctrip.rest.service.impl;

import com.simpletour.common.security.token.EncryptedToken;
import com.simpletour.common.security.token.Token;
import com.simpletour.common.utils.MD5;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.error.CtripOrderError;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyResponse;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransResponse;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.RequestBodyType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.RequestBodyTypeForTrans;
import com.simpletour.gateway.ctrip.rest.service.CtripOrderService;
import com.simpletour.gateway.ctrip.rest.service.CtripTransService;
import com.simpletour.gateway.ctrip.rest.service.CtripValidator;
import com.simpletour.gateway.ctrip.util.XMLParseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;

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
    public VerifyResponse validatePre(String request, String methodName) {
        if (request == null || request.isEmpty()) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED));
        }
        //去掉xml头信息
        String xmlString = XMLParseUtil.subStringForXML(request);
        if (xmlString == null || xmlString.isEmpty()) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED));
        }

        //将去头的xml转换成可以处理的实体类
        VerifyOrderRequest verifyOrderRequest = null;
        VerifyTransRequest verifyTransRequest = null;
        try {
            if (SysConfig.TOURISM_HANDLER.equals(methodName)) {
                verifyTransRequest = XMLParseUtil.convertToJavaBean(xmlString, VerifyTransRequest.class);
            } else if (SysConfig.ORDER_HANDLER.equals(methodName)) {
                verifyOrderRequest = XMLParseUtil.convertToJavaBean(xmlString, VerifyOrderRequest.class);
            } else {
                return new VerifyResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED));
            }
        } catch (Exception e) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.XML_RESOLVE_FAILED));
        }
        //将body编码为sign
        RequestBodyType requestBodyType = null;
        RequestBodyTypeForTrans requestBodyTypeForTrans = null;
        if (SysConfig.ORDER_HANDLER.equals(methodName)) {
            requestBodyType = verifyOrderRequest.getBody();
            if (requestBodyType == null) {
                return new VerifyTransResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED), null);
            }
        } else if (SysConfig.TOURISM_HANDLER.equals(methodName)) {
            requestBodyTypeForTrans = verifyTransRequest.getBody();
            if (requestBodyTypeForTrans == null) {
                return new VerifyTransResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED), null);
            }
        }

        String xmlBodyNode;
        try {
            xmlBodyNode = XMLParseUtil.convertToXml(SysConfig.ORDER_HANDLER.equals(methodName) ? requestBodyType : (SysConfig.TOURISM_HANDLER.equals(methodName) ? requestBodyTypeForTrans : null));
        } catch (Exception e) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED));
        }
        //将body的xml去头信息
        String bodyString = XMLParseUtil.subStringForXML(xmlBodyNode);

        //取文件头转为requestHeaderType实体类
        RequestHeaderType requestHeaderType = SysConfig.ORDER_HANDLER.equals(methodName) ? verifyOrderRequest.getHeader() : (SysConfig.TOURISM_HANDLER.equals(methodName) ? verifyTransRequest.getHeader() : null);
        StringBuffer signTo = new StringBuffer(requestHeaderType.getAccountId());
        signTo.append(requestHeaderType.getServiceName());
        signTo.append(requestHeaderType.getRequestTime());
        signTo.append(new BASE64Encoder().encode(bodyString.getBytes()));
        signTo.append(requestHeaderType.getVersion());
        signTo.append(signKey);
        //验证签名
        if (!requestHeaderType.getSign().equals(MD5.getMD5String(signTo.toString().getBytes()))) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.SIGN_ERROR));
        }

        //统一设置tenant_id
        //TODO.....设置需要修改
        new EncryptedToken("1", "1", "1", "1", Token.ClientType.BROWSER);

        switch (requestHeaderType.getServiceName()) {
            case SysConfig.VERIFY_ORDER_METHOD:
                return ctripOrderService.verifyOrder(verifyOrderRequest);
            case SysConfig.CREATE_ORDER_METHOD:
                return ctripOrderService.createOrder(verifyOrderRequest);
            case SysConfig.CANCEL_ORDER_METHOD:
                return ctripOrderService.cancelOrder(verifyOrderRequest);
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
