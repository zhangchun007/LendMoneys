package com.haiercash.gouhua.beans.bankcard;

import java.io.Serializable;

/**
 * Created by StarFall on 2016/5/27.
 * 查询银行卡列表
 * 开户省	acctProvince
 * 开户市	acctCity
 * 开户区	acctArea
 * mobile  手机号
 * 单笔代收限额：singleCollLimited  -1表示不限额（用这个判断还款卡限额）
 * 单笔代付限额:singleTransLimited -1表示不限额
 */
public class QueryCardBean implements Serializable {
    public String cardNo;
    public String bankCode;
    public String bankName;
    public String cardTypeName;//卡类型
    public String isDefaultCard;//是否是默认卡
    public String isRealnameCard;//是否客户实名认证时的绑定卡
    public String custName;
    public String acctProvince;
    public String acctCity;
    public String acctArea;
    public String mobile;
    public String singleCollLimited;
    public String singleTransLimited;
    public boolean isChoosed;
    public boolean enable = true;
    /**
     * SIGN_DEALING: 处理中
     * SIGN_SUCCESS:成功
     * SIGN_FAIL:失败
     * UN_SIGN:未签约
     * SIGN_EXPIRE:签约过期
     */
    public String signStatus;
    /**
     * Y支持此卡
     * N不支持此卡
     */
    public String bankCardSupport;
    public String accBchCde;
    private String accBchName;
    private String interId;
    private String signAgreementUrl;

    public String getInterId() {
        return interId;
    }

    public void setInterId(String interId) {
        this.interId = interId;
    }

    public String getSignAgreementUrl() {
        return signAgreementUrl;
    }

    public void setSignAgreementUrl(String signAgreementUrl) {
        this.signAgreementUrl = signAgreementUrl;
    }

    public String getAccBchCde() {
        return accBchCde;
    }

    public void setAccBchCde(String accBchCde) {
        this.accBchCde = accBchCde;
    }

    public String getAccBchName() {
        return accBchName;
    }

    public void setAccBchName(String accBchName) {
        this.accBchName = accBchName;
    }

    public String getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(String signStatus) {
        this.signStatus = signStatus;
    }

    public String getBankCardSupport() {
        return bankCardSupport;
    }

    public void setBankCardSupport(String bankCardSupport) {
        this.bankCardSupport = bankCardSupport;
    }

    public String getSingleTransLimited() {
        return singleTransLimited;
    }

    public void setSingleTransLimited(String singleTransLimited) {
        this.singleTransLimited = singleTransLimited;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getAcctArea() {
        return acctArea;
    }

    public void setAcctArea(String acctArea) {
        this.acctArea = acctArea;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getIsRealnameCard() {
        return isRealnameCard;
    }

    public void setIsRealnameCard(String isRealnameCard) {
        this.isRealnameCard = isRealnameCard;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public String getIsDefaultCard() {
        return isDefaultCard;
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }

    public void setIsDefaultCard(String isDefaultCard) {
        this.isDefaultCard = isDefaultCard;
    }

    public String getSingleCollLimited() {
        return singleCollLimited;
    }

    public void setSingleCollLimited(String singleCollLimited) {
        this.singleCollLimited = singleCollLimited;
    }
}

