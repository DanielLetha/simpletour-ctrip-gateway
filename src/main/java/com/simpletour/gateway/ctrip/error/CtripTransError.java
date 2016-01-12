package com.simpletour.gateway.ctrip.error;

/**
 * Created by Mario on 2016/1/12.
 */
public enum CtripTransError {

    /**
     * ==================操作成功====================
     **/
    OPERATION_SUCCESS("0000", "操作成功"),

    BUS_NO_FIND_FAILD("0001", "车次查询失败"),

    DELETE_FAILD("0005", "delete data failed");

    String errorCode;
    String errorMessage;

    CtripTransError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
