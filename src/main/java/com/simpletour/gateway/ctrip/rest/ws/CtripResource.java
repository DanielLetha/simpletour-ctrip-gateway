package com.simpletour.gateway.ctrip.rest.ws;

import com.simpletour.common.restful.service.BaseRESTfulService;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransResponse;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderResponse;
import com.simpletour.gateway.ctrip.rest.service.CtripOrderService;
import com.simpletour.gateway.ctrip.rest.service.CtripTransService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    @Resource
    private CtripOrderService ctripOrderService;

    @Resource
    private CtripTransService ctripTransService;

    /**
     * 下单验证接口
     *
     * @param request 请求文
     * @return
     */
    @POST
    @Path("verifyOrder")
    public VerifyOrderResponse verifyOrder(String request) {
        return ctripOrderService.verifyOrder(request);
    }

    /**
     * 订单下单接口
     *
     * @param request 请求文
     * @return
     */
    @POST
    @Path("createOrder")
    public VerifyOrderResponse createOrder(String request) {
        return ctripOrderService.createOrder(request);
    }

    /**
     * 订单取消接口
     *
     * @param request 请求文
     * @return
     */
    @POST
    @Path("cancelOrder")
    public VerifyOrderResponse cancelOrder(String request) {
        return ctripOrderService.cancelOrder(request);
    }

    /**
     * 订单查询接口
     *
     * @param request 请求文
     * @return
     */
    @POST
    @Path("queryOrder")
    public VerifyOrderResponse queryOrder(String request) {
        return ctripOrderService.queryOrder(request);
    }

    /**
     * 凭证重发接口
     *
     * @param request 请求文
     * @return
     */
    @POST
    @Path("resend")
    public VerifyOrderResponse resend(String request) {
        return ctripOrderService.resend(request);
    }

    /**
     * 查询车次接口
     *
     * @param request
     * @return
     */
    @POST
    @Path("queryBusNo")
    public VerifyTransResponse queryBusNo(String request) {
        return ctripTransService.queryBusNo(request);
    }

}
