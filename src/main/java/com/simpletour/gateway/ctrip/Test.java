package com.simpletour.gateway.ctrip;

import com.simpletour.common.utils.MD5;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.ExtendInfoType;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.PassengerInfo;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.RequestBodyType;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.RequestBodyTypeForTransOrder;
import com.simpletour.gateway.ctrip.util.StringUtils;
import com.simpletour.gateway.ctrip.util.XMLParseUtil;
import sun.misc.BASE64Encoder;

import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jeff.Song on 2015/12/30.
 */
public class Test {

    public static void main(String[] args) throws JAXBException, UnsupportedEncodingException, InstantiationException, IllegalAccessException {
//        //构造body数据
//        RequestBodyType bodyType = new RequestBodyType();
//        bodyType.setCount(2);
//        bodyType.setContactName("张三");
//        String xml = XMLParseUtil.convertToXml(bodyType);
//        System.out.println(xml);
//
//        //去掉xml头信息
//        int pos = xml.lastIndexOf("?>");
//        String bodyString = xml.substring(pos + 2, xml.length());
//        System.out.println(bodyString);
//
//        //将body进行base64编码，再和携程要求的数据一起做MD5摘要运算
//        String xmlBase64 = new BASE64Encoder().encode(bodyString.getBytes());
//        StringBuilder builder = new StringBuilder();
//        builder.append("account");
//        builder.append("verifyOrder");
//        builder.append("2015-10-19 16:05:31");
//        builder.append(xmlBase64);
//        builder.append("2.0");
//        builder.append("signKey");
//        String sign = MD5.getMD5String(builder.toString().getBytes());
//        System.out.println(sign);
//
//        RequestHeaderType headerType = new RequestHeaderType();
//        headerType.setAccountId("account");
//        headerType.setServiceName("verifyOrder");
//        headerType.setRequestTime("2015-10-19 16:05:31");
//        headerType.setSign(sign);
//        headerType.setVersion("2.0");
//
//        //组装最后的数据
//        VerifyOrderRequest request = new VerifyOrderRequest();
//        request.setHeader(headerType);
//        request.setBody(bodyType);
//        String ret = XMLParseUtil.convertToXml(request);
//        System.out.println();
//        System.out.println("-------------打印最后数据--------------");
//        System.out.println();
//        System.out.println(ret);

        /**
         *行程:557 ,type:0
         *产品:10000396,type1
         */

        for (int i = 1; i < 300; i++) {
            buildString(SysConfig.CREATE_ORDER_METHOD);
        }
        System.out.println(buildString(SysConfig.CREATE_ORDER_METHOD));


        /**
         * 行程,不包含产品。
         * 565
         */
//        for (int i = 1; i < 300; i++) {
//            buildStringForCp(SysConfig.TRANS_CREATE_ORDER_METHOD);
//        }
//        System.out.println(buildStringForCp(SysConfig.TRANS_CREATE_ORDER_METHOD));
    }

    private static String buildString(String serviceName) throws UnsupportedEncodingException, IllegalAccessException, InstantiationException {
        //构造数据
        //1.构造body信息
        RequestBodyType bodyType = new RequestBodyType();
        ExtendInfoType extendInfoType = new ExtendInfoType();
        extendInfoType.setProductType("0");
        bodyType.setExtendInfo(extendInfoType);
        bodyType.setProductId("568");
        bodyType.setPrice("1.00");
        bodyType.setCount(2);
        bodyType.setContactName("偏分偏出三分");
        bodyType.setContactMobile("13011111111");
        bodyType.setUseDate("2016-03-04");
        bodyType.setUseEndDate("2016-03-04");
        bodyType.setOtaOrderId("234356s" + Random.class.newInstance().nextInt() + "3aa44" + Random.class.newInstance().nextInt() + "aa34dd");

        List<PassengerInfo> passengerInfos = new ArrayList<>();
        PassengerInfo passengerInfo = new PassengerInfo();
        passengerInfo.setName("马大叔");
        passengerInfo.setMobile("18516232422");
        passengerInfo.setCardType("1");
        passengerInfo.setCardNo("511102199107200011");
        passengerInfos.add(passengerInfo);

        PassengerInfo passengerInfo1 = new PassengerInfo();
        passengerInfo1.setName("马大叔他大叔");
        passengerInfo1.setMobile("18516232422");
        passengerInfo1.setCardType("1");
        passengerInfo1.setCardNo("511102199107200011");
        passengerInfos.add(passengerInfo1);
        bodyType.setPassengerInfos(passengerInfos);


        //2.构造header信息
        RequestHeaderType headerType = new RequestHeaderType();
        headerType.setAccountId("71");
        headerType.setServiceName(serviceName);
        headerType.setRequestTime("2016-02-29 16:54:52");
        headerType.setVersion("2.0");

        //组装最后的数据
        VerifyOrderRequest request = new VerifyOrderRequest();
        request.setHeader(headerType);
        request.setBody(bodyType);

        String xml = XMLParseUtil.convertToXml(request);
        String xmlBodyString = StringUtils.formatXml(XMLParseUtil.subBodyStringForXml(xml));

        //3.编码sign
        String xmlBase64 = org.bouncycastle.util.encoders.Base64.toBase64String(xmlBodyString.getBytes("UTF-8"));
        StringBuffer buffer = new StringBuffer();
        buffer.append(headerType.getAccountId());
        buffer.append(headerType.getServiceName());
        buffer.append(headerType.getRequestTime());
        buffer.append(xmlBase64);
        buffer.append(headerType.getVersion());
        buffer.append("9C1012E99067AA970A972103B2CD3D0C");
        String sign = MD5.getMD5String(buffer.toString().getBytes()).toLowerCase();
        headerType.setSign(sign);

        System.out.println("sign:" + sign + ",otaId:" + bodyType.getOtaOrderId());

        return XMLParseUtil.convertToXml(request);
    }


    private static String buildStringForCp(String serviceName) throws UnsupportedEncodingException, IllegalAccessException, InstantiationException {
        //构造数据
        //1.构造body信息
        RequestBodyTypeForTransOrder bodyType = new RequestBodyTypeForTransOrder();

        bodyType.setOtaOrderId("33636" + Random.class.newInstance().nextInt() + "3636ss5" + Random.class.newInstance().nextInt() + "k1w3k");
        bodyType.setProductId("565");
        bodyType.setPrice("0.01");
        bodyType.setCount(2);
        bodyType.setContactName("偏分偏出三分");
        bodyType.setContactMobile("13011111111");
        bodyType.setUseDate("2016-03-06");

        List<com.simpletour.gateway.ctrip.rest.pojo.type.transType.PassengerInfo> passengerInfos = new ArrayList<>();
        com.simpletour.gateway.ctrip.rest.pojo.type.transType.PassengerInfo passengerInfo = new com.simpletour.gateway.ctrip.rest.pojo.type.transType.PassengerInfo();
        passengerInfo.setName("马大叔");
        passengerInfo.setMobile("13111111111");
        passengerInfo.setCardType("1");
        passengerInfo.setCardNo("511102199107200011");
        passengerInfos.add(passengerInfo);

        com.simpletour.gateway.ctrip.rest.pojo.type.transType.PassengerInfo passengerInfo1 = new com.simpletour.gateway.ctrip.rest.pojo.type.transType.PassengerInfo();
        passengerInfo1.setName("马大叔他大叔");
        passengerInfo1.setMobile("13111111111");
        passengerInfo1.setCardType("1");
        passengerInfo1.setCardNo("511102199107200011");
        passengerInfos.add(passengerInfo1);
        bodyType.setPassengerInfos(passengerInfos);


        //2.构造header信息
        RequestHeaderType headerType = new RequestHeaderType();
        headerType.setAccountId(SysConfig.XIECHENG_CP_SOURCE_ID + "");
        headerType.setServiceName(serviceName);
        headerType.setRequestTime("2016-3-22 11:16:31");
        headerType.setVersion("2.0");

        //组装最后的数据
        VerifyTransOrderRequest request = new VerifyTransOrderRequest();
        request.setHeader(headerType);
        request.setBody(bodyType);

        String xml = XMLParseUtil.convertToXml(request);
        String xmlBodyString = StringUtils.formatXml(XMLParseUtil.subBodyStringForXml(xml));

        //3.编码sign
        String xmlBase64 = org.bouncycastle.util.encoders.Base64.toBase64String(xmlBodyString.getBytes("UTF-8"));
        StringBuffer buffer = new StringBuffer();
        buffer.append(headerType.getAccountId());
        buffer.append(headerType.getServiceName());
        buffer.append(headerType.getRequestTime());
        buffer.append(xmlBase64);
        buffer.append(headerType.getVersion());
        buffer.append("9C1012E99067AA970A972103B2CD3D0C");
        String sign = MD5.getMD5String(buffer.toString().getBytes()).toLowerCase();
        headerType.setSign(sign);

        System.out.println("sign:" + sign + ",otaId:" + bodyType.getOtaOrderId());

        return XMLParseUtil.convertToXml(request);
    }
}
