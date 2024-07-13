package com.app.haiercash.base.net.bean;

/**
 * @Author: Sun
 * @Date :    2017/12/19
 * @FileName: ResultHead
 * @Description: 请求结果头
 */

public class  ResultHead {

    public String retFlag; //请求结果标识
    public String retMsg;  //请求结果描述

    public ResultHead() {
    }

    public ResultHead(String retFlag, String retMsg) {
        this.retFlag = retFlag;
        this.retMsg = retMsg;
    }

    public String getRetFlag() {
        return retFlag;
    }

    public void setRetFlag(String retFlag) {
        this.retFlag = retFlag;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }
}
