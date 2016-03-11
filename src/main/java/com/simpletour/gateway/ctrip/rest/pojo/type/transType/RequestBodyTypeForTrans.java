package com.simpletour.gateway.ctrip.rest.pojo.type.transType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by Mario on 2016/1/12.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement(name = "body")
public class RequestBodyTypeForTrans {

    /**
     * 行程id
     */
    private Long tourismId;
    /**
     * 出发地
     */
    private String depart;
    /**
     * 目的地
     */
    private String arrive;

    /**
     * 指定哪一天:默认是今天
     */
    private Date date = new Date();

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getArrive() {
        return arrive;
    }

    public void setArrive(String arrive) {
        this.arrive = arrive;
    }

    public Long getTourismId() {
        return tourismId;
    }

    public void setTourismId(Long tourismId) {
        this.tourismId = tourismId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
