package com.simpletour.gateway.ctrip.rest.pojo.type.transType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by Mario on 2016/1/12.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BusNoInfo {
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
     * 距离
     */
    private Integer distance;
    /**
     * 车次号
     */
    private String no;

    public BusNoInfo(String arrive, String depart, String arriveTime, String departTime, Integer distance, String no) {
        this.arrive = arrive;
        this.depart = depart;
        this.arriveTime = arriveTime;
        this.departTime = departTime;
        this.distance = distance;
        this.no = no;
    }

    /**
     * setter & getter
     */
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

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}
