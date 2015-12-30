package com.simpletour.gateway.ctrip.rest.ws;

import com.simpletour.common.restful.service.BaseRESTfulService;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderResponse;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by songfujie on 15/10/28.
 */
@Path("ctrip")
@Consumes({MediaType.APPLICATION_XML})
@Produces({MediaType.APPLICATION_XML})
@Component
public class CtripResource extends BaseRESTfulService {


    @POST
    @Path("verifyOrder")
    public VerifyOrderResponse verifyOrder(VerifyOrderRequest request) {
        VerifyOrderResponse response = new VerifyOrderResponse();
        //TODO
        ResponseHeaderType headerType = new ResponseHeaderType();
        headerType.setResultCode("0000");
        response.setHeader(headerType);
        //如果订单验证失败，则构造以下Body返回库存数量，成功则不用构造以下Body
//        ResponseBodyType bodyType = new ResponseBodyType();
//        response.setBody(bodyType);
        return response;
    }
}
