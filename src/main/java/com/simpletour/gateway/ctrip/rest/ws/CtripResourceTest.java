package com.simpletour.gateway.ctrip.rest.ws;

import com.simpletour.common.restful.service.BaseRESTfulService;
import com.simpletour.common.utils.MD5;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderResponse;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransResponse;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.ExtendInfoType;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.PassengerInfo;
import com.simpletour.gateway.ctrip.rest.pojo.type.orderType.RequestBodyType;
import com.simpletour.gateway.ctrip.rest.pojo.type.RequestHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.RequestBodyTypeForTrans;
import com.simpletour.gateway.ctrip.rest.service.CtripOrderService;
import com.simpletour.gateway.ctrip.rest.service.CtripTransService;
import com.simpletour.gateway.ctrip.util.XMLParseUtil;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by songfujie on 15/10/28.
 */
@Path("test")
@Consumes({MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_XML})
@Component
public class CtripResourceTest extends BaseRESTfulService {

    @Resource
    private CtripOrderService ctripOrderService;

    @Resource
    private CtripTransService ctripTransService;

    /**
     * 针对验证订单和下单接口构造数据
     *
     * @return
     */
    private String buildString() {
        //构造数据
        //1.构造body信息
        RequestBodyType bodyType = new RequestBodyType();
        ExtendInfoType extendInfoType = new ExtendInfoType();
        extendInfoType.setType("product");
        bodyType.setExtendInfo(extendInfoType);
        bodyType.setProductId("1");
        bodyType.setPrice("100");
        bodyType.setCount(2);
        bodyType.setContactName("偏分偏出三分");
        bodyType.setContactMobile("130111111111");
        bodyType.setUseDate("2016-01-12");

        List<PassengerInfo> passengerInfos = new ArrayList<>();
        PassengerInfo passengerInfo = new PassengerInfo();
        passengerInfo.setName("大毛");
        passengerInfo.setMobile("010101010101010");
        passengerInfo.setCardType("1");
        passengerInfo.setCardNo("511102199107200011");
        passengerInfos.add(passengerInfo);
        bodyType.setPassengerInfos(passengerInfos);

        String xml = XMLParseUtil.convertToXml(bodyType);
        String xmlBody = XMLParseUtil.subStringForXML(xml);

        //2.构造header信息
        RequestHeaderType headerType = new RequestHeaderType();
        headerType.setAccountId("1");
        headerType.setServiceName("verifyOrder");
        headerType.setRequestTime("2015-10-19 16:05:31");
        headerType.setVersion("2.0");

        //3.编码sign
        String xmlBase64 = new BASE64Encoder().encode(xmlBody.getBytes());
        StringBuffer buffer = new StringBuffer();
        buffer.append(headerType.getAccountId());
        buffer.append(headerType.getServiceName());
        buffer.append(headerType.getRequestTime());
        buffer.append(xmlBase64);
        buffer.append(headerType.getVersion());
        buffer.append("signKey");
        String sign = MD5.getMD5String(buffer.toString().getBytes());
        headerType.setSign(sign);

        //组装最后的数据
        VerifyOrderRequest request = new VerifyOrderRequest();
        request.setHeader(headerType);
        request.setBody(bodyType);
        return XMLParseUtil.convertToXml(request);
    }

    private String buildStringForCancelOrder() {
        //构造数据
        //1.构造body信息
        RequestBodyType bodyType = new RequestBodyType();
        bodyType.setOtaOrderId("zxqfafjiqlfaqp9");
        bodyType.setVendorOrderId("33217845248");

        String xml = XMLParseUtil.convertToXml(bodyType);
        String xmlBody = XMLParseUtil.subStringForXML(xml);

        //2.构造header信息
        RequestHeaderType headerType = new RequestHeaderType();
        headerType.setAccountId("1");
        headerType.setServiceName("cancelOrder");
        headerType.setRequestTime("2015-10-19 16:05:31");
        headerType.setVersion("2.0");

        //3.编码sign
        String xmlBase64 = new BASE64Encoder().encode(xmlBody.getBytes());
        StringBuffer buffer = new StringBuffer();
        buffer.append(headerType.getAccountId());
        buffer.append(headerType.getServiceName());
        buffer.append(headerType.getRequestTime());
        buffer.append(xmlBase64);
        buffer.append(headerType.getVersion());
        buffer.append("signKey");
        String sign = MD5.getMD5String(buffer.toString().getBytes());
        headerType.setSign(sign);

        //组装最后的数据
        VerifyOrderRequest request = new VerifyOrderRequest();
        request.setHeader(headerType);
        request.setBody(bodyType);
        return XMLParseUtil.convertToXml(request);
    }

    private String buildStringForQueryOrder() {
        //构造数据
        //1.构造body信息
        RequestBodyType bodyType = new RequestBodyType();
        bodyType.setOtaOrderId("zxqfafjiqlfaqp9");
        bodyType.setVendorOrderId("33246312448");

        String xml = XMLParseUtil.convertToXml(bodyType);
        String xmlBody = XMLParseUtil.subStringForXML(xml);

        //2.构造header信息
        RequestHeaderType headerType = new RequestHeaderType();
        headerType.setAccountId("1");
        headerType.setServiceName("cancelOrder");
        headerType.setRequestTime("2015-10-19 16:05:31");
        headerType.setVersion("2.0");

        //3.编码sign
        String xmlBase64 = new BASE64Encoder().encode(xmlBody.getBytes());
        StringBuffer buffer = new StringBuffer();
        buffer.append(headerType.getAccountId());
        buffer.append(headerType.getServiceName());
        buffer.append(headerType.getRequestTime());
        buffer.append(xmlBase64);
        buffer.append(headerType.getVersion());
        buffer.append("signKey");
        String sign = MD5.getMD5String(buffer.toString().getBytes());
        headerType.setSign(sign);

        //组装最后的数据
        VerifyOrderRequest request = new VerifyOrderRequest();
        request.setHeader(headerType);
        request.setBody(bodyType);
        return XMLParseUtil.convertToXml(request);
    }

    /**
     * 对车次构造数据
     *
     * @return
     */
    private String buildStringForBusNo() {
        //构造数据
        //1.构造body信息
        RequestBodyTypeForTrans bodyType = new RequestBodyTypeForTrans();
        bodyType.setDepart("出发点");
        bodyType.setArrive("到达点");

        String xml = XMLParseUtil.convertToXml(bodyType);
        String xmlBody = XMLParseUtil.subStringForXML(xml);

        //2.构造header信息
        RequestHeaderType headerType = new RequestHeaderType();
        headerType.setAccountId("1");
        headerType.setServiceName("queryBusNo");
        headerType.setRequestTime("2015-10-19 16:05:31");
        headerType.setVersion("2.0");

        //3.编码sign
        String xmlBase64 = new BASE64Encoder().encode(xmlBody.getBytes());
        StringBuffer buffer = new StringBuffer();
        buffer.append(headerType.getAccountId());
        buffer.append(headerType.getServiceName());
        buffer.append(headerType.getRequestTime());
        buffer.append(xmlBase64);
        buffer.append(headerType.getVersion());
        buffer.append("signKey");
        String sign = MD5.getMD5String(buffer.toString().getBytes());
        headerType.setSign(sign);

        //组装最后的数据
        VerifyTransRequest request = new VerifyTransRequest();
        request.setHeader(headerType);
        request.setBody(bodyType);
        return XMLParseUtil.convertToXml(request);
    }

    /**
     * 下单验证接口
     *
     * @return
     */
    @POST
    @Path("verifyOrder")
    public VerifyOrderResponse verifyOrder() {
        return ctripOrderService.verifyOrder(this.buildString());
    }

    /**
     * 创建订单接口
     *
     * @return
     */
    @POST
    @Path("createOrder")
    public VerifyOrderResponse createOrder() {
        return ctripOrderService.createOrder(this.buildString());
    }

    /**
     * 订单取消接口
     *
     * @return
     */
    @POST
    @Path("cancelOrder")
    public VerifyOrderResponse cancelOrder() {
        return ctripOrderService.cancelOrder(this.buildStringForCancelOrder());
    }

    /**
     * 订单查询接口
     *
     * @return
     */
    @POST
    @Path("queryOrder")
    public VerifyOrderResponse queryOrder() {
        return ctripOrderService.queryOrder(this.buildStringForQueryOrder());
    }

    /**
     * 凭证重发接口
     *
     * @return
     */
    @POST
    @Path("resend")
    public VerifyOrderResponse resend() {
        return ctripOrderService.resend(this.buildStringForQueryOrder());
    }

    /**
     * 查询车次接口
     *
     * @return
     */
    @POST
    @Path("queryBusNo")
    public VerifyTransResponse queryBusNo() {
        return ctripTransService.queryBusNo(this.buildStringForBusNo());
    }
}
