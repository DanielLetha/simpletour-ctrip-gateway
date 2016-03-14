package com.simpletour.gateway.ctrip.rest.service.impl;

import com.simpletour.biz.inventory.IStockBiz;
import com.simpletour.common.core.dao.query.condition.AndConditionSet;
import com.simpletour.domain.inventory.Stock;
import com.simpletour.domain.order.Source;
import com.simpletour.domain.product.Tourism;
import com.simpletour.gateway.ctrip.config.SysConfig;
import com.simpletour.gateway.ctrip.error.CtripTransError;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransRequest;
import com.simpletour.gateway.ctrip.rest.pojo.VerifyTransResponse;
import com.simpletour.gateway.ctrip.rest.pojo.type.ResponseHeaderType;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.ResponseBodyTypeForTrans;
import com.simpletour.gateway.ctrip.rest.pojo.type.transType.TourismInfo;
import com.simpletour.gateway.ctrip.rest.service.CtripTransService;
import com.simpletour.gateway.ctrip.util.DateUtil;
import com.simpletour.service.order.IOrderService;
import com.simpletour.service.product.IProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Mario on 2016/1/12.
 */
@Service
public class CtripTransServiceImpl implements CtripTransService {

    @Resource
    private IProductService productService;

    @Resource
    private IStockBiz stockBiz;

    @Resource
    private IOrderService iOrderService;


    @Override
    public VerifyTransResponse queryTourism(VerifyTransRequest verifyTransRequest) {

        if (verifyTransRequest.getHeader() == null || verifyTransRequest.getHeader().getAccountId() == null)
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OTA_SOURCE_ID_NULL), null);

        if (!verifyTransRequest.getHeader().getAccountId().trim().equals(SysConfig.XIECHENG_CP_SOURCE_ID.toString()))
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OTA_SOURCE_ID_WRONG), null);
        //查询是否有该source
        Optional<Source> sourceOptional = iOrderService.findSourceById(Long.parseLong(verifyTransRequest.getHeader().getAccountId()));

        if (!sourceOptional.isPresent())
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OTA_SOURCE_NOT_EXISTED), null);

        if (verifyTransRequest.getBody() == null)
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OTA_BUS_SEARCH_PARAM_WRONG), null);

        Date dateForStock = null;
        if (!(verifyTransRequest.getBody().getDate() == null || verifyTransRequest.getBody().getDate().isEmpty())) {
            try {
                dateForStock = DateUtil.convertStrToDate(verifyTransRequest.getBody().getDate(), "yyyy-MM-dd");
            } catch (ParseException e) {
                return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OTA_BUS_SEARCH_PARAM_WRONG), null);
            }
        }

        List<Tourism> tourisms;
        try {
            AndConditionSet andConditionSet = new AndConditionSet();
            if (verifyTransRequest.getBody().getTourismId() != null) {
                andConditionSet.addCondition("id", verifyTransRequest.getBody().getTourismId());
            }
            if (!(verifyTransRequest.getBody().getDepart() == null || verifyTransRequest.getBody().getDepart().isEmpty())) {
                andConditionSet.addCondition("depart", verifyTransRequest.getBody().getDepart());
            }
            if (!(verifyTransRequest.getBody().getArrive() == null || verifyTransRequest.getBody().getArrive().isEmpty())) {
                andConditionSet.addCondition("arrive", verifyTransRequest.getBody().getArrive());
            }
            andConditionSet.addCondition("online", true);
            andConditionSet.addCondition("shuttle", false);
            andConditionSet.addCondition("productNum", 0);
            tourisms = productService.getTourismByCondition(andConditionSet);
        } catch (IllegalArgumentException e) {
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.BUS_NO_FIND_FAILD), null);
        }
        if (tourisms == null || tourisms.isEmpty()) {
            return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.BUS_NO_FIND_FAILD), null);
        }
        //转化数据
        final Date finalDateForStock = dateForStock;
        List<TourismInfo> tourismInfos = tourisms.stream().map(tourism1 -> {
            //获取库存以及价格
            Optional<Stock> stockOptional = stockBiz.getStock(tourism1, (verifyTransRequest.getBody().getDate() == null || verifyTransRequest.getBody().getDate().isEmpty()) ? new Date() : finalDateForStock, true);
            if (!stockOptional.isPresent()) {
                return null;
            }
            return new TourismInfo(tourism1.getId(), tourism1.getArrive(), tourism1.getDepart(), tourism1.getArriveTime(), tourism1.getDepartTime(), tourism1.getName(), tourism1.getDays(), stockOptional.get().getAvailableQuantity(), stockOptional.get().getPrice());
        }).filter(tourism -> tourism != null).collect(Collectors.toList());
        return new VerifyTransResponse(new ResponseHeaderType(CtripTransError.OPERATION_SUCCESS), new ResponseBodyTypeForTrans(tourismInfos.size(), tourismInfos));
    }
}
