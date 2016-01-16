package com.simpletour.gateway.ctrip.rest.service.impl;

import com.simpletour.common.core.dao.query.condition.AndConditionSet;
import com.simpletour.domain.product.Tourism;
import com.simpletour.gateway.ctrip.error.CtripTransError;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransResponse;
import com.simpletour.gateway.ctrip.rest.pojo.bo.CtripTransBo;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.ResponseBodyTypeForTrans;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.TourismInfo;
import com.simpletour.gateway.ctrip.rest.service.CtripTransService;
import com.simpletour.service.product.IProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mario on 2016/1/12.
 */
@Service
public class CtripTransServiceImpl implements CtripTransService {

    @Resource
    private IProductService productService;

    @Override
    public VerifyTransResponse queryTourism(VerifyTransRequest verifyTransRequest) {

        //获取转化为实体后的数据,并对数据进行组装
        CtripTransBo ctripTransBo = new CtripTransBo(verifyTransRequest.getHeader(), verifyTransRequest.getBody());
        //传入tourism模块进行业务单元处理
        Tourism tourism = ctripTransBo.asTourism();

        List<Tourism> tourisms;
        try {
            AndConditionSet andConditionSet = new AndConditionSet();
            if (tourism.getId() != null) {
                andConditionSet.addCondition("id", tourism.getId());
            }
            if (!(tourism.getDepart() == null || tourism.getDepart().isEmpty())) {
                andConditionSet.addCondition("depart", tourism.getDepart());
            }
            if (!(tourism.getArrive() == null || tourism.getArrive().isEmpty())) {
                andConditionSet.addCondition("arrive", tourism.getArrive());
            }
            andConditionSet.addCondition("online", true);
            tourisms = productService.getTourismByCondition(andConditionSet);
        } catch (IllegalArgumentException e) {
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.BUS_NO_FIND_FAILD), null);
        }
        if (tourisms == null || tourisms.isEmpty()) {
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.BUS_NO_FIND_FAILD), null);
        }
        //转化数据
        List<TourismInfo> tourismInfos = tourisms.stream().map(tourism1 -> new TourismInfo(tourism1.getId(), tourism1.getArrive(), tourism1.getDepart(), tourism1.getArriveTime(), tourism1.getDepartTime(), tourism1.getName())).collect(Collectors.toList());
        return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS), new ResponseBodyTypeForTrans(tourismInfos.size(), tourismInfos));
    }
}
