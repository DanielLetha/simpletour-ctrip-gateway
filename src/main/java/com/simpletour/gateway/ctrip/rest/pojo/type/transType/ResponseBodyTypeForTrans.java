package com.simpletour.gateway.ctrip.rest.pojo.type.transType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
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
    private List<TourismInfo> tourismInfos;

    ResponseBodyTypeForTrans() {
    }

    public ResponseBodyTypeForTrans(Integer count, List<TourismInfo> tourismInfos) {
        this.count = count;
        this.tourismInfos = tourismInfos;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }


    @XmlElementWrapper(name = "tourismInfos")
    @XmlElement(name = "tourismInfo",required = true)
    public List<TourismInfo> getTourismInfos() {
        return tourismInfos;
    }

    public void setTourismInfos(List<TourismInfo> tourismInfos) {
        this.tourismInfos = tourismInfos;
    }

}
