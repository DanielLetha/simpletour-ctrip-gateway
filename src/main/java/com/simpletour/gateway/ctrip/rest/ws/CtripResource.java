package com.simpletour.gateway.ctrip.rest.ws;

import com.simpletour.common.restful.service.BaseRESTfulService;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyResponse;
import com.simpletour.gateway.ctrip.rest.service.CtripValidator;
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
    private CtripValidator ctripValidator;

    /**
     * 订单模块处理接口
     *
     * @param request 请求文
     * @return
     */
    @POST
    @Path(SysConfig.ORDER_HANDLER)
    public VerifyResponse orderHandler(String request) {
        return ctripValidator.validatePre(request, SysConfig.ORDER_HANDLER);
    }

    /**
     * 行程模块处理接口
     *
     * @param request 请求文
     * @return
     */
    @POST
    @Path(SysConfig.TOURISM_HANDLER)
    public VerifyResponse tourismHandler(String request) {
        return ctripValidator.validatePre(request, SysConfig.TOURISM_HANDLER);
    }

}
