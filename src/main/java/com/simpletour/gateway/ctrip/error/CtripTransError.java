package com.simpletour.gateway.ctrip.error;

/**
 * Created by Mario on 2016/1/12.
 */
public enum CtripTransError {

    /**
     * ==================操作成功====================
     **/
    OPERATION_SUCCESS("0000", "操作成功"),

    /**
     * ==================账户、系统相关====================
     */
    JSON_RESOLVE_FAILED("0001", "JSON解析失败"),

    XML_RESOLVE_FAILED("0002", "XML解析失败"),

    SIGN_ERROR("0003", "签名错误"),

    OTA_ACCOUNT_WRONG("0004", "OTA账户信息不正确"),

    REQUEST_METHOD_WRONG("0005", "请求方法错误"),

    DATA_PARSE_EXCEPTION("0006", "参数错误,请检查请求参数是否正常"),
    /**
     * ==================查询车次相关====================
     */

    BUS_NO_FIND_FAILD("1001", "车次查询失败"),

    OTA_SOURCE_ID_NULL("1002", "OTA渠道ID不存在"),

    OTA_SOURCE_NOT_EXISTED("1003", "OTA频道不存在"),

    OTA_BUS_SEARCH_PARAM_WRONG("1004", "参数不正确"),

    /**
     * ==================验证订单相关====================
     */
    ORDER_VALIDATE_FAIL("2001", "订单验证失败"),

    /**
     * ==================创建订单相关====================
     */
    ORDER_HAS_BEEN_EXSITED("3001", "该订单已经存在,请勿重复下单"),

    ORDER_USER_CREATE_FAIL("3002", "创建用户失败"),

    ORDER_CREATE_FAIL("3003", "订单创建失败"),

    /**
     * ==================创建订单相关====================
     */
    ORDER_QUERY_BY_ID_FAIL("4001", "根据ID查询订单失败"),


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

    public CtripTransError custom(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }
}
