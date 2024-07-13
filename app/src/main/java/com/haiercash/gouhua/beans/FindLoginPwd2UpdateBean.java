package com.haiercash.gouhua.beans;

/**
 * Created by use on 2017/6/30.
 */

public class FindLoginPwd2UpdateBean {
    public String	userId;
    public String	newPassword;
    public String	isNeedVerify;
    public String	verifyMobile;
    public String	verifyNo;
    public String	custNo;
    public String	custName;
    public String	idNo;
    public String deviceId;
    public String isRsa;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getIsNeedVerify() {
        return isNeedVerify;
    }

    public void setIsNeedVerify(String isNeedVerify) {
        this.isNeedVerify = isNeedVerify;
    }

    public String getVerifyMobile() {
        return verifyMobile;
    }

    public void setVerifyMobile(String verifyMobile) {
        this.verifyMobile = verifyMobile;
    }

    public String getVerifyNo() {
        return verifyNo;
    }

    public void setVerifyNo(String verifyNo) {
        this.verifyNo = verifyNo;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getIsRsa() {
        return isRsa;
    }

    public void setIsRsa(String isRsa) {
        this.isRsa = isRsa;
    }
}
