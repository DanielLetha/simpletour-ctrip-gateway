package com.simpletour.gateway.ctrip.rest.pojo.bo;

/**
 * Created by Mario on 2016/2/29.
 */
public class CtripOrderCallBackBo {

    private Long id;
    private String type;

    public CtripOrderCallBackBo() {
    }

    public CtripOrderCallBackBo(Long id, String type) {
        this.id = id;
        this.type = type;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
