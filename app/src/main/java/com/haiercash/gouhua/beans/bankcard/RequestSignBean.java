package com.haiercash.gouhua.beans.bankcard;

/**
 * @Author: Sun
 * @Date :    2018/5/15
 * @FileName: RequestSignBean
 * @Description:
 */
public class RequestSignBean {

    private String requestNo;

    /**
     * app/appserver/bank/card/signing/request
     * 签约接口返回的报错信息
     */
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }
}
