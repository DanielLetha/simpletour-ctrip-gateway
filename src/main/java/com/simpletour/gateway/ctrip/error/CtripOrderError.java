package com.simpletour.gateway.ctrip.error;

/**
 * Created by Mario on 2016/1/6.
 */
public enum CtripOrderError {

    /**
     * ==================操作成功====================
     **/
    OPERATION_SUCCESS("0000", "操作成功"),

    /**
     * ==================账户、系统相关====================
     **/
    JSON_RESOLVE_FAILED("0001", "JSON解析失败"),
    XML_RESOLVE_FAILED("0002", "XML解析失败"),
    SIGN_ERROR("0003", "签名错误"),
    OTA_ACCOUNT_WRONG("0004", "OTA账户信息不正确"),
    VALIDATE_FAILED("0005", "订单验证失败"),

    /**
     * ==================下单相关====================
     **/
    PRODUCT_ID_WRONG_OR_NOT_EXISTED("1001", "产品id不存在或者错误"),
    PRODUCT_OFFLINE("1002", "产品已经下架"),
    STOCK_NOT_ENOUGH("1003", "库存不足"),
    PURCHASE_LIMITATION("1004", "限购"),
    ORDER_BOOK_FAILD("1100", "下单失败"),

    /**
     * ==================取消相关====================
     **/
    ORDER_ID_NOT_EXISTED("2001", "该订单号不存在"),
    ORDER_HAS_BEEN_USED("2002", "该订单已经使用"),
    ORDER_STATUS_UPDATE_FAILD("2005", "订单取消失败"),
    ORDER_NULL_BY_ID("2100", "订单不存在"),
    ORDER_CALL_BACK_NULL("2101", "回调请求异常"),
    /**
     * ==================重发相关====================
     **/
    ORDER_ID_EMPTY("3001", "订单号不存在"),
    ORDER_NULL("3100", "订单不存在"),
    MESSAGE_SEND_FAILED("3101", "短信凭证发送失败"),
    /**
     * ==================查询相关====================
     **/
    ORDER_ID_NULL("4001", "订单号不存在"),
    ORDER_QUERY_FAILD("4002", "订单查询失败"),
    /**
     * ===================自定义异常===================
     **/
    ORDER_TRANSFER_TO_REQUEST_FAILED("9001", "构造请求异常"),

    OPERATE_EXCEPTION("10000", "parse exception"),
    DATA_PARSE_EXCEPTION("10001", "参数错误,请检查请求参数是否正常");

    String errorCode;
    String errorMessage;

    CtripOrderError(String errorCode, String errorMessage) {
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

    public CtripOrderError custom(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

}
