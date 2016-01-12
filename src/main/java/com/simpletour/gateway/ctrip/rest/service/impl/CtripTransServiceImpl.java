package com.simpletour.gateway.ctrip.rest.service.impl;

import com.simpletour.common.core.dao.IBaseDao;
import com.simpletour.domain.traveltrans.BusNo;
import com.simpletour.gateway.ctrip.error.CtripTransError;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransResponse;
import com.simpletour.gateway.ctrip.rest.pojo.bo.CtripTransBo;
import com.simpletour.gateway.ctrip.rest.pojo.bo.VerifyCtripRequestBo;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.BusNoInfo;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.ResponseBodyTypeForTrans;
import com.simpletour.gateway.ctrip.rest.service.CtripTransService;
import com.simpletour.gateway.ctrip.util.DateUtil;
import com.simpletour.gateway.ctrip.validator.CtripValidator;
import com.simpletour.service.traveltrans.ITravelTransportService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Mario on 2016/1/12.
 */
@Service
public class CtripTransServiceImpl implements CtripTransService {

    @Resource
    private ITravelTransportService travelTransportService;

    @Override
    public VerifyTransResponse queryBusNo(String request) {
        //对请求做初步验证
        CtripValidator ctripValidator = new CtripValidator();
        VerifyCtripRequestBo verifyCtripRequestBo = ctripValidator.validateTransPre(request);
        if (verifyCtripRequestBo.getVerifyTransResponse() != null) {
            return verifyCtripRequestBo.getVerifyTransResponse();
        }

        //获取转化为实体后的数据,并对数据进行组装
        CtripTransBo ctripTransBo = new CtripTransBo(verifyCtripRequestBo.getVerifyTransRequest().getHeader(), verifyCtripRequestBo.getVerifyTransRequest().getBody());
        //传入busNo模块进行业务单元处理
        BusNo busNo = ctripTransBo.asBusNo();

        List<BusNo> busNos;
        try {
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("depart", busNo.getDepart());
            conditions.put("arrive", busNo.getArrive());
            conditions.put("status", BusNo.Status.valueOf("normal"));
            busNos = travelTransportService.findBusNoByConditions(conditions, "id", IBaseDao.SortBy.ASC);
        } catch (IllegalArgumentException e) {
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.BUS_NO_FIND_FAILD), null);
        }
        if (busNos == null || busNos.isEmpty()) {
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.BUS_NO_FIND_FAILD), null);
        }
        //转化数据
        List<BusNoInfo> busNoInfos = busNos.stream().map(busNo1 -> new BusNoInfo(busNo1.getArrive(), busNo1.getDepart(), DateUtil.convertLongToTime(Long.parseLong(busNo1.getArriveTime().toString())), DateUtil.convertLongToTime(Long.parseLong(busNo1.getDepartTime().toString())), busNo1.getDistance(), busNo1.getNo())).collect(Collectors.toList());
        return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS), new ResponseBodyTypeForTrans(busNoInfos.size(), busNoInfos));
    }
}
