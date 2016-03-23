package com.simpletour.gateway.ctrip.rest.ws;

import com.simpletour.common.restful.service.BaseRESTfulService;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyOrderRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyResponse;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransRequest;
import com.simpletour.gateway.ctrip.rest.service.CtripCallBackUrl;
import com.simpletour.gateway.ctrip.rest.service.CtripOrderService;
import com.simpletour.gateway.ctrip.rest.service.CtripValidator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;

/**
 * Created by songfujie on 15/10/28.
 */
@Path("ctrip")
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Component
public class CtripResource extends BaseRESTfulService {

    @Resource
    private CtripValidator ctripValidator;

    @Resource
    private CtripCallBackUrl ctripCallBackUrl;

    /**
     * 订单模块处理接口
     *
     * @param request 请求文
     * @return
     */
    @POST
    @Path(SysConfig.ORDER_HANDLER)
    public VerifyResponse orderHandler(String request) throws ParseException {
        return ctripValidator.validatePre(request);
    }

    /**
     * 行程模块处理车次查询接口
     *
     * @param request 请求文
     * @return
     */
    @POST
    @Path(SysConfig.TOURISM_HANDLER)
    public VerifyResponse tourismHandler(String request) throws ParseException {
        return ctripValidator.validatePreForTrans(request);
    }

    /**
     * 行程模块订单处理接口
     *
     * @param request 请求文
     * @return
     * @throws ParseException
     */
    @POST
    @Path(SysConfig.TOURISM_ORDER_HANDLER)
    public VerifyResponse tourismOrderHanlder(String request) throws ParseException {
        return ctripValidator.validatePreForTransOrder(request);
    }

    /**
     * 取消回调接口
     *
     * @param request
     * @return
     */
    @POST
    @Path(SysConfig.CALL_BACK_URL)
    public VerifyResponse callBackHandler(String request) {
        return ctripCallBackUrl.getCancelOrderCallBack(request);
    }
}
