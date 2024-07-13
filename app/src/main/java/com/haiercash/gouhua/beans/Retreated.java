package com.haiercash.gouhua.beans;

/**
 * @Author: Sun
 * @Date :    2018/8/29
 * @FileName: Retreated
 * @Description: 被退回
 */
public class Retreated {

    private String errorFlag; //原因退回标识码
    private String errorMsg;  //导致退回的错误信息

    public String getErrorFlag() {
        return errorFlag;
    }

    public void setErrorFlag(String errorFlag) {
        this.errorFlag = errorFlag;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
