package com.simpletour.gateway.ctrip.rest.pojo.type;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by Jeff.Song on 2015/12/29.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ResponseHeaderType {

    /**
     * 0000成功
     */
    private String resultCode;

    private String resultMessage;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
