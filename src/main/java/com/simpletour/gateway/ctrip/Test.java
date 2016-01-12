package com.simpletour.gateway.ctrip;

import com.simpletour.common.utils.MD5;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.RequestBodyType;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.util.XMLParseUtil;
import sun.misc.BASE64Encoder;

import javax.xml.bind.JAXBException;

/**
 * Created by Jeff.Song on 2015/12/30.
 */
public class Test {

    public static void main(String[] args) throws JAXBException {
        //构造body数据
        RequestBodyType bodyType = new RequestBodyType();
        bodyType.setCount(2);
        bodyType.setContactName("张三");
        String xml = XMLParseUtil.convertToXml(bodyType);
        System.out.println(xml);

        //去掉xml头信息
        int pos = xml.lastIndexOf("?>");
        String bodyString = xml.substring(pos + 2, xml.length());
        System.out.println(bodyString);

        //将body进行base64编码，再和携程要求的数据一起做MD5摘要运算
        String xmlBase64 = new BASE64Encoder().encode(bodyString.getBytes());
        StringBuilder builder = new StringBuilder();
        builder.append("account");
        builder.append("verifyOrder");
        builder.append("2015-10-19 16:05:31");
        builder.append(xmlBase64);
        builder.append("2.0");
        builder.append("signKey");
        String sign = MD5.getMD5String(builder.toString().getBytes());
        System.out.println(sign);

        RequestHeaderType headerType = new RequestHeaderType();
        headerType.setAccountId("account");
        headerType.setServiceName("verifyOrder");
        headerType.setRequestTime("2015-10-19 16:05:31");
        headerType.setSign(sign);
        headerType.setVersion("2.0");

        //组装最后的数据
        VerifyOrderRequest request = new VerifyOrderRequest();
        request.setHeader(headerType);
        request.setBody(bodyType);
        String ret = XMLParseUtil.convertToXml(request);
        System.out.println();
        System.out.println("-------------打印最后数据--------------");
        System.out.println();
        System.out.println(ret);

    }
}
