package com.haiercash.gouhua.beans;

import java.io.Serializable;

/**
 * Created by use on 2016/9/26.
 * 银行代码	cardNo
 * 银行名称	bankName
 * 卡类型名称	cardType
 */

public class BankInfoBean implements Serializable {
    public String cardNo;  //卡号
    public String actName;//持卡人姓名
    public String bankName;//银行名称  开户行简称
    public String bankCode;//银行代码

    public String cardType; //卡类型
    public String cardTypeName;//卡类型名称
    public String interId; //签约渠道接口ID
    public String interCode; //签约渠道接口数字编码
    public String interName; //签约渠道名称
    //SIGN_DEALING: 处理中
    //SIGN_SUCCESS:成功
    //SIGN_FAIL:失败
    //UN_SIGN:未签约
    //SIGN_EXPIRE:签约过期
    public String signStatus; //签约状态
    public String bankSignNo; //银行签约协议号 已经签约才有
    /**
     * true ：需要发起签约流程
     * false：不需要
     */
    public boolean needToSign; //是否需要发起签约 已经签约才有

    public String signAgreementUrl;// 协议地址

    public String cardMobile;//手机号码

    public String acctProvince;
    public String acctCity;

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getCardMobile() {
        return cardMobile;
    }

    public void setCardMobile(String cardMobile) {
        this.cardMobile = cardMobile;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getAcctProvince() {
        return acctProvince;
    }

    public void setAcctProvince(String acctProvince) {
        this.acctProvince = acctProvince;
    }

    public String getAcctCity() {
        return acctCity;
    }

    public void setAcctCity(String acctCity) {
        this.acctCity = acctCity;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardType() {
        return cardType;
    }


    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getInterId() {
        return interId;
    }

    public void setInterId(String interId) {
        this.interId = interId;
    }

    public String getInterCode() {
        return interCode;
    }

    public void setInterCode(String interCode) {
        this.interCode = interCode;
    }

    public String getInterName() {
        return interName;
    }

    public void setInterName(String interName) {
        this.interName = interName;
    }

    public String getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(String signStatus) {
        this.signStatus = signStatus;
    }

    public String getBankSignNo() {
        return bankSignNo;
    }

    public void setBankSignNo(String bankSignNo) {
        this.bankSignNo = bankSignNo;
    }

    public boolean isNeedToSign() {
        return needToSign;
    }

    public void setNeedToSign(boolean needToSign) {
        this.needToSign = needToSign;
    }

    public String getSignAgreementUrl() {
        return signAgreementUrl;
    }

    public void setSignAgreementUrl(String signAgreementUrl) {
        this.signAgreementUrl = signAgreementUrl;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }
}
