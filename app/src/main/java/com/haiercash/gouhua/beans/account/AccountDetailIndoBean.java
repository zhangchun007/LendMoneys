package com.haiercash.gouhua.beans.account;

/**
 * 账户与安全页面接口信息
 */
public class AccountDetailIndoBean {
    private String mashLoginMobile;//掩码登录手机号
    private String mashCustName;//掩码姓名
    private String mashCertNo;//掩码身份证号
    private String certImgExist;//身份证影像是否存在，Y 是  N 否
    private String faceImgExist;//人脸影像是否存在，Y 是  N 否
    private String custInfoExist;//个人资料是否存在，Y 是  N 否
    private String bankCardNumber;//银行卡数量
    private String certInfoState;//身份证有效期状态，N正常，E即将过期，Y已过期
    private String isRealInfo;//是否实名,Y 是  N 否
    private String pwdState;//密码是否设置,Y 是  N 否
    private String gestureState;//手势密码是否设置,Y 是  N 否
    private String payPwdState;//交易密码是否设置,Y 是  N 否
    private String wechatBindState;//微信绑定状态,Y 是  N 否

    public String getMashLoginMobile() {
        return mashLoginMobile;
    }

    public void setMashLoginMobile(String mashLoginMobile) {
        this.mashLoginMobile = mashLoginMobile;
    }

    public String getMashCustName() {
        return mashCustName;
    }

    public void setMashCustName(String mashCustName) {
        this.mashCustName = mashCustName;
    }

    public String getMashCertNo() {
        return mashCertNo;
    }

    public void setMashCertNo(String mashCertNo) {
        this.mashCertNo = mashCertNo;
    }

    public String getCertImgExist() {
        return certImgExist;
    }

    public void setCertImgExist(String certImgExist) {
        this.certImgExist = certImgExist;
    }

    public String getFaceImgExist() {
        return faceImgExist;
    }

    public void setFaceImgExist(String faceImgExist) {
        this.faceImgExist = faceImgExist;
    }

    public String getCustInfoExist() {
        return custInfoExist;
    }

    public void setCustInfoExist(String custInfoExist) {
        this.custInfoExist = custInfoExist;
    }

    public String getBankCardNumber() {
        return bankCardNumber;
    }

    public void setBankCardNumber(String bankCardNumber) {
        this.bankCardNumber = bankCardNumber;
    }

    public String getCertInfoState() {
        return certInfoState;
    }

    public void setCertInfoState(String certInfoState) {
        this.certInfoState = certInfoState;
    }

    public String getIsRealInfo() {
        return isRealInfo;
    }

    public void setIsRealInfo(String isRealInfo) {
        this.isRealInfo = isRealInfo;
    }

    public String getPwdState() {
        return pwdState;
    }

    public void setPwdState(String pwdState) {
        this.pwdState = pwdState;
    }

    public String getGestureState() {
        return gestureState;
    }

    public void setGestureState(String gestureState) {
        this.gestureState = gestureState;
    }

    public String getPayPwdState() {
        return payPwdState;
    }

    public void setPayPwdState(String payPwdState) {
        this.payPwdState = payPwdState;
    }

    public String getWechatBindState() {
        return wechatBindState;
    }

    public void setWechatBindState(String wechatBindState) {
        this.wechatBindState = wechatBindState;
    }
}
