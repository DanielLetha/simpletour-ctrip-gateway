package com.simpletour.gateway.ctrip.rest.service.impl;

import com.simpletour.common.utils.MD5;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.error.CtripOrderError;
import com.simpletour.gateway.ctrip.error.CtripTransError;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyResponse;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransRequest;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import com.simpletour.gateway.ctrip.rest.service.CtripOrderService;
import com.simpletour.gateway.ctrip.rest.service.CtripTransService;
import com.simpletour.gateway.ctrip.rest.service.CtripValidator;
import com.simpletour.gateway.ctrip.util.StringUtils;
import com.simpletour.gateway.ctrip.util.XMLParseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

/**
 * Created by Mario on 2016/1/3.
 */
@Component
public class CtripValidatorImpl implements CtripValidator {

    @Resource
    private CtripOrderService ctripOrderService;

    @Resource
    private CtripTransService ctripTransService;

    @Value("${xiecheng.mp.signkey}")
    private String mpSignKey;

    @Value("${xiecheng.cp.signkey}")
    private String cpSignKey;


    /**
     * 校验请求:针对携程门票
     *
     * @param request
     * @return
     */
    public VerifyResponse validatePre(String request) throws ParseException {
        if (request == null || request.isEmpty()) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED));
        }

        //将去头的xml转换成可以处理的实体类
        VerifyOrderRequest verifyOrderRequest = null;
        try {
            verifyOrderRequest = XMLParseUtil.convertToJavaBean(request, VerifyOrderRequest.class);
        } catch (Exception e) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.XML_RESOLVE_FAILED));
        }

        //过滤出body信息
        String bodyString = StringUtils.formatXml(XMLParseUtil.subBodyStringForXml(request));

        //取文件头转为requestHeaderType实体类
        RequestHeaderType requestHeaderType = verifyOrderRequest.getHeader();

        //校验文件头是否转化为实体正确,并且确定请求是针对携程门票
        if (requestHeaderType == null)
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.XML_RESOLVE_FAILED));
        if (requestHeaderType.getAccountId() != null && !SysConfig.XIECHENG_MP_SOURCE_ID.toString().equals(requestHeaderType.getAccountId().trim()))
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.OTA_ACCOUNT_WRONG));

        StringBuffer signTo = new StringBuffer(requestHeaderType.getAccountId());
        signTo.append(requestHeaderType.getServiceName());
        signTo.append(requestHeaderType.getRequestTime());
        try {
            signTo.append(org.bouncycastle.util.encoders.Base64.toBase64String(bodyString.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED));
        }
        signTo.append(requestHeaderType.getVersion());
        signTo.append(mpSignKey);
        //验证签名
        if (!requestHeaderType.getSign().equals(MD5.getMD5String(signTo.toString().getBytes()).toLowerCase())) {
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.SIGN_ERROR));
        }

        try {
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
                default:
                    return new VerifyResponse(new ResponseHeaderType(CtripOrderError.JSON_RESOLVE_FAILED));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.SYSTEM_EXCPTION));
        }
    }

    @Override
    public VerifyResponse validatePreForTrans(String request) {
        if (request == null || request.isEmpty()) {
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.JSON_RESOLVE_FAILED));
        }
        //将去头的xml转换成可以处理的实体类
        VerifyTransRequest verifyTransRequest = null;
        try {
            verifyTransRequest = XMLParseUtil.convertToJavaBean(request, VerifyTransRequest.class);
        } catch (Exception e) {
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.XML_RESOLVE_FAILED));
        }

        //过滤出body信息
        String bodyString = StringUtils.formatXml(XMLParseUtil.subBodyStringForXml(request));

        //取文件头转为requestHeaderType实体类
        RequestHeaderType requestHeaderType = verifyTransRequest.getHeader();

        //校验文件头是否转化为实体正确,并且确定请求是针对携程车票
        if (requestHeaderType == null)
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.XML_RESOLVE_FAILED));
        if (requestHeaderType.getAccountId() != null && !SysConfig.XIECHENG_CP_SOURCE_ID.toString().equals(requestHeaderType.getAccountId().trim()))
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.OTA_ACCOUNT_WRONG));

        StringBuffer signTo = new StringBuffer(requestHeaderType.getAccountId());
        signTo.append(requestHeaderType.getServiceName());
        signTo.append(requestHeaderType.getRequestTime());
        try {
            signTo.append(org.bouncycastle.util.encoders.Base64.toBase64String(bodyString.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.JSON_RESOLVE_FAILED));
        }
        signTo.append(requestHeaderType.getVersion());
        signTo.append(cpSignKey);
        //验证签名
        if (!requestHeaderType.getSign().equals(MD5.getMD5String(signTo.toString().getBytes()).toLowerCase())) {
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.SIGN_ERROR));
        }

        //验证方法名
        if (!requestHeaderType.getServiceName().equals(SysConfig.QUERY_TOURISM_METHOD))
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.REQUEST_METHOD_WRONG));

        return ctripTransService.queryTourism(verifyTransRequest);
    }

    @Override
    public VerifyResponse validatePreForTransOrder(String request) throws ParseException {
        if (request == null || request.isEmpty()) {
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.JSON_RESOLVE_FAILED));
        }
        //将去头的xml转换成可以处理的实体类
        VerifyTransOrderRequest verifyTransOrderRequest = null;
        try {
            verifyTransOrderRequest = XMLParseUtil.convertToJavaBean(request, VerifyTransOrderRequest.class);
        } catch (Exception e) {
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.XML_RESOLVE_FAILED));
        }

        //过滤出body信息
        String bodyString = StringUtils.formatXml(XMLParseUtil.subBodyStringForXml(request));

        //取文件头转为requestHeaderType实体类
        RequestHeaderType requestHeaderType = verifyTransOrderRequest.getHeader();

        //校验文件头是否转化为实体正确,并且确定请求是针对携程车票
        if (requestHeaderType == null)
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.XML_RESOLVE_FAILED));
        if (requestHeaderType.getAccountId() != null && !SysConfig.XIECHENG_CP_SOURCE_ID.toString().equals(requestHeaderType.getAccountId().trim()))
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.OTA_ACCOUNT_WRONG));

        StringBuffer signTo = new StringBuffer(requestHeaderType.getAccountId());
        signTo.append(requestHeaderType.getServiceName());
        signTo.append(requestHeaderType.getRequestTime());
        try {
            signTo.append(org.bouncycastle.util.encoders.Base64.toBase64String(bodyString.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.JSON_RESOLVE_FAILED));
        }
        signTo.append(requestHeaderType.getVersion());
        signTo.append(cpSignKey);
        //验证签名
        if (!requestHeaderType.getSign().equals(MD5.getMD5String(signTo.toString().getBytes()).toLowerCase())) {
            return new VerifyResponse(new ResponseHeaderType(CtripTransError.SIGN_ERROR));
        }

        try {
            switch (requestHeaderType.getServiceName()) {
                case SysConfig.TRANS_VERIFY_ORDER_METHOD:
                    return ctripTransService.transVerifyOrder(verifyTransOrderRequest);
                case SysConfig.TRANS_CREATE_ORDER_METHOD:
                    return ctripTransService.transCreateOrder(verifyTransOrderRequest);
                case SysConfig.TRANS_QUERY_ORDER_ID_METHOD:
                    return ctripTransService.transQueryOrderById(verifyTransOrderRequest);
                case SysConfig.TRANS_QUERY_ORDER_LIST_METHOD:
                    return ctripTransService.transQueryOrderList(verifyTransOrderRequest);
                default:
                    return new VerifyResponse(new ResponseHeaderType(CtripTransError.JSON_RESOLVE_FAILED));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new VerifyResponse(new ResponseHeaderType(CtripOrderError.SYSTEM_EXCPTION));
        }
    }

}
