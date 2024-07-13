package com.haiercash.gouhua.beans;

/**
 * Created by Limige on 2016/6/22.
 * 6.48. (GET) 信息采集比对参数
 */
public class CollectInfoBean {

    /**
     * msgNum : 3
     * contractsNum : 10
     * callRecordNum : 3
     */

    public String msgNum;
    public String contractsNum;
    public String callRecordNum;

    public String getMsgNum() {
        return msgNum;
    }

    public void setMsgNum(String msgNum) {
        this.msgNum = msgNum;
    }

    public String getContractsNum() {
        return contractsNum;
    }

    public void setContractsNum(String contractsNum) {
        this.contractsNum = contractsNum;
    }

    public String getCallRecordNum() {
        return callRecordNum;
    }

    public void setCallRecordNum(String callRecordNum) {
        this.callRecordNum = callRecordNum;
    }
}
