package com.simpletour.gateway.ctrip.rest.pojo.type.transType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * Created by Mario on 2016/1/12.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ResponseBodyTypeForTrans {

    /**
     * 总数
     */
    private Integer count;
    /**
     * 车次列表
     */
    private List<BusNoInfo> busNoInfos;

    ResponseBodyTypeForTrans() {
    }

    public ResponseBodyTypeForTrans(Integer count, List<BusNoInfo> busNoInfos) {
        this.count = count;
        this.busNoInfos = busNoInfos;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<BusNoInfo> getBusNoInfos() {
        return busNoInfos;
    }

    public void setBusNoInfos(List<BusNoInfo> busNoInfos) {
        this.busNoInfos = busNoInfos;
    }
}
