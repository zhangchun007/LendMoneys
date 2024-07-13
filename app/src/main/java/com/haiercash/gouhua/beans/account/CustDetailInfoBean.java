package com.haiercash.gouhua.beans.account;

/**
 * 个人中心信息详情
 */
public class CustDetailInfoBean {
    private String mashLoginMobile;//掩码登录手机号
    private String mashCustName;//掩码姓名
    private String mashCertNo;//掩码身份证号
    private String certImgExist;//身份证影像是否存在，Y 是  N 否
    private String faceImgExist;//人脸影像是否存在，Y 是  N 否
    private String custInfoExist;//个人资料是否存在，Y 是  N 否
    private String bankCardNumber;//银行卡数量
    private String certInfoState;//身份证有效期状态，N正常，E即将过期，Y已过期
    private String personDetailUrl;//个人资料H5链接
    private String errorMsg;//个人资料入口提示错误文案，有就弹窗拦截

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getPersonDetailUrl() {
        return personDetailUrl;
    }

    public void setPersonDetailUrl(String personDetailUrl) {
        this.personDetailUrl = personDetailUrl;
    }

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
}
