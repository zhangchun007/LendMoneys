package com.haiercash.gouhua.beans.login;

import java.io.Serializable;
import java.util.HashMap;

/**
 * H5信息相关
 */
public class H5LoginInfo implements Serializable {

    private String custNo;
    private String business;
    private String channelNo;
    private String firstSpell;
    private String mobile;
    private String userId;
    private String retFlag;
    private HashMap<String, Object> token;
    private String appDownFrom;
    private String processId;
    private boolean success;
    private String regisChannel;
    private String isRealInfo;
    private String registerVector;

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getBusiness() {
        return business;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setFirstSpell(String firstSpell) {
        this.firstSpell = firstSpell;
    }

    public String getFirstSpell() {
        return firstSpell;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setRetFlag(String retFlag) {
        this.retFlag = retFlag;
    }

    public String getRetFlag() {
        return retFlag;
    }

    public void setToken(HashMap<String, Object> token) {
        this.token = token;
    }

    public HashMap<String, Object> getToken() {
        return token;
    }

    public void setAppDownFrom(String appDownFrom) {
        this.appDownFrom = appDownFrom;
    }

    public String getAppDownFrom() {
        return appDownFrom;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setRegisChannel(String regisChannel) {
        this.regisChannel = regisChannel;
    }

    public String getRegisChannel() {
        return regisChannel;
    }

    public void setIsRealInfo(String isRealInfo) {
        this.isRealInfo = isRealInfo;
    }

    public String getIsRealInfo() {
        return isRealInfo;
    }

    public void setRegisterVector(String registerVector) {
        this.registerVector = registerVector;
    }

    public String getRegisterVector() {
        return registerVector;
    }

}
