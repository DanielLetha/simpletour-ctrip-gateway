package com.simpletour.gateway.ctrip.rest.pojo.type.transType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by Mario on 2016/1/12.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TourismInfo {
    /**
     * 行程id
     */
    private Long tourismId;
    /**
     * 到达地
     */
    private String arrive;
    /**
     * 出发地
     */
    private String depart;
    /**
     * 到达时间
     */
    private String arriveTime;
    /**
     * 出发时间
     */
    private String departTime;
    /**
     * 行程名称
     */
    private String name;

    /**
     * constructor
     */
    public TourismInfo(Long tourismId, String arrive, String depart, String arriveTime, String departTime, String name) {
        this.tourismId = tourismId;
        this.arrive = arrive;
        this.depart = depart;
        this.arriveTime = arriveTime;
        this.departTime = departTime;
        this.name = name;
    }


    /**
     * setter & getter
     */
    public Long getTourismId() {
        return tourismId;
    }

    public void setTourismId(Long tourismId) {
        this.tourismId = tourismId;
    }

    public String getArrive() {
        return arrive;
    }

    public void setArrive(String arrive) {
        this.arrive = arrive;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public String getDepartTime() {
        return departTime;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
