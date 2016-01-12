package com.simpletour.gateway.ctrip.validator;

import com.simpletour.common.utils.MD5;
import com.simpletour.gateway.ctrip.error.CtripOrderError;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderResponse;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransResponse;
import com.simpletour.gateway.ctrip.rest.pojo.bo.VerifyCtripRequestBo;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.RequestBodyType;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.RequestBodyTypeForTrans;
import com.simpletour.gateway.ctrip.util.XMLParseUtil;
import sun.misc.BASE64Encoder;

/**
 * Created by Mario on 2016/1/3.
 */
public class CtripValidator {

    /**
     * 校验订单模块的请求
     *
     * @param request
     * @return
     */
    public VerifyCtripRequestBo validateOrderPre(String request) {
        if (request == null || request.isEmpty()) {
            return new VerifyCtripRequestBo(null, new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED), null));
        }
        //去掉xml头信息
        String xmlString = XMLParseUtil.subStringForXML(request);
        if (xmlString == null || xmlString.isEmpty()) {
            return new VerifyCtripRequestBo(null, new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED), null));
        }
        //将去头的xml转换成可以处理的实体类
        VerifyOrderRequest verifyOrderRequest;
        try {
            verifyOrderRequest = XMLParseUtil.convertToJavaBean(xmlString, VerifyOrderRequest.class);
        } catch (Exception e) {
            return new VerifyCtripRequestBo(null, new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.XML_RESOLVE_FAILED), null));
        }
        //将body编码为sign
        RequestBodyType requestBodyType = verifyOrderRequest.getBody();
        if (requestBodyType == null) {
            return new VerifyCtripRequestBo(null, new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED), null));
        }
        String xmlBodyNode;
        try {
            xmlBodyNode = XMLParseUtil.convertToXml(requestBodyType);
        } catch (Exception e) {
            return new VerifyCtripRequestBo(null, new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED), null));
        }
        //将body的xml去头信息
        String bodyString = XMLParseUtil.subStringForXML(xmlBodyNode);

        //取文件头转为requestHeaderType实体类
        RequestHeaderType requestHeaderType = verifyOrderRequest.getHeader();
        StringBuffer signTo = new StringBuffer(requestHeaderType.getAccountId());
        signTo.append(requestHeaderType.getServiceName());
        signTo.append(requestHeaderType.getRequestTime());
        signTo.append(new BASE64Encoder().encode(bodyString.getBytes()));
        signTo.append(requestHeaderType.getVersion());
        signTo.append("signKey");
        //验证签名
        if (!requestHeaderType.getSign().equals(MD5.getMD5String(signTo.toString().getBytes()))) {
            return new VerifyCtripRequestBo(null, new VerifyOrderResponse(new ResponseHeaderType(CtripOrderError.SIGN_ERROR), null));
        }
        return new VerifyCtripRequestBo(verifyOrderRequest, null);
    }

    /**
     * 校验车次模块的请求
     *
     * @param request
     * @return
     */
    public VerifyCtripRequestBo validateTransPre(String request) {
        if (request == null || request.isEmpty()) {
            return new VerifyCtripRequestBo(null, new VerifyTransResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED), null));
        }
        //去掉xml头信息
        String xmlString = XMLParseUtil.subStringForXML(request);
        if (xmlString == null || xmlString.isEmpty()) {
            return new VerifyCtripRequestBo(null, new VerifyTransResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED), null));
        }
        //将去头的xml转换成可以处理的实体类
        VerifyTransRequest verifyTransRequest;
        try {
            verifyTransRequest = XMLParseUtil.convertToJavaBean(xmlString, VerifyTransRequest.class);
        } catch (Exception e) {
            return new VerifyCtripRequestBo(null, new VerifyTransResponse(new ResponseHeaderType(CtripOrderError.XML_RESOLVE_FAILED), null));
        }
        //将body编码为sign
        RequestBodyTypeForTrans requestBodyTypeForTrans = verifyTransRequest.getBody();
        if (requestBodyTypeForTrans == null) {
            return new VerifyCtripRequestBo(null, new VerifyTransResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED), null));
        }
        String xmlBodyNode;
        try {
            xmlBodyNode = XMLParseUtil.convertToXml(requestBodyTypeForTrans);
        } catch (Exception e) {
            return new VerifyCtripRequestBo(null, new VerifyTransResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED), null));
        }
        //将body的xml去头信息
        String bodyString = XMLParseUtil.subStringForXML(xmlBodyNode);

        //取文件头转为requestHeaderType实体类
        RequestHeaderType requestHeaderType = verifyTransRequest.getHeader();
        StringBuffer signTo = new StringBuffer(requestHeaderType.getAccountId());
        signTo.append(requestHeaderType.getServiceName());
        signTo.append(requestHeaderType.getRequestTime());
        signTo.append(new BASE64Encoder().encode(bodyString.getBytes()));
        signTo.append(requestHeaderType.getVersion());
        signTo.append("signKey");
        //验证签名
        if (!requestHeaderType.getSign().equals(MD5.getMD5String(signTo.toString().getBytes()))) {
            return new VerifyCtripRequestBo(null, new VerifyTransResponse(new ResponseHeaderType(CtripOrderError.SIGN_ERROR), null));
        }
        return new VerifyCtripRequestBo(verifyTransRequest, null);
    }
}
