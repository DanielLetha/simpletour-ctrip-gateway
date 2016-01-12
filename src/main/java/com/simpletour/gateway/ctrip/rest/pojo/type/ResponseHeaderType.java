package com.simpletour.gateway.ctrip.rest.pojo.type;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.simpletour.gateway.ctrip.error.CtripOrderError;
import com.simpletour.gateway.ctrip.error.CtripTransError;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by Jeff.Song on 2015/12/29.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ResponseHeaderType {

    /**
     * 定义码
     */
    private String resultCode;
    /**
     * 错误消息
     */
    private String resultMessage;

    /**
     * 错误枚举,用于封装定义码和错误消息
     */
    private CtripOrderError ctripOrderError;

    private CtripTransError ctripTransError;

    /**
     * 具体参数
     */
    private String var;

    /**
     * constructor
     */
    public ResponseHeaderType() {
    }

    public ResponseHeaderType(String resultCode, String resultMessage) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public ResponseHeaderType(CtripOrderError ctripOrderError) {
        this.ctripOrderError = ctripOrderError;
        this.resultCode = ctripOrderError.getErrorCode();
        this.resultMessage = ctripOrderError.getErrorMessage();
    }

    public ResponseHeaderType(CtripOrderError ctripOrderError, String var) {
        this.ctripOrderError = ctripOrderError;
        this.var = var;
        this.resultCode = ctripOrderError.getErrorCode();
        this.resultMessage = ctripOrderError.getErrorMessage() + "[" + var + "]";
    }

    public ResponseHeaderType(CtripTransError ctripTransError) {
        this.ctripTransError = ctripTransError;
        this.resultCode = ctripTransError.getErrorCode();
        this.resultMessage = ctripTransError.getErrorMessage();
    }

    /**
     * setter & getter
     */
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

    @XmlTransient
    public CtripOrderError getCtripOrderError() {
        return ctripOrderError;
    }

    public void setCtripOrderError(CtripOrderError ctripOrderError) {
        this.ctripOrderError = ctripOrderError;
    }

    @XmlTransient
    public CtripTransError getCtripTransError() {
        return ctripTransError;
    }

    public void setCtripTransError(CtripTransError ctripTransError) {
        this.ctripTransError = ctripTransError;
    }
}
