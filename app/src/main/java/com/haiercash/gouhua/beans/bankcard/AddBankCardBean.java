package com.haiercash.gouhua.beans.bankcard;

/**
 * Created by StarFall on 2016/5/30.
 * 添加银行卡
 * 开户省	acctProvince
 * 开户市	acctCity
 */
public class AddBankCardBean {
    public String custNo;
    public String custName;
    public String cardNo;
    public String certNo;
    public String phonenumber;
    public String isRealnameCard;
    public String isDefaultCard;
    public String acctProvince;
    public String acctCity;
    public String verifyMobile;
    public String checkCode;
    private String isRsa;

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getInterId() {
        return interId;
    }

    public void setInterId(String interId) {
        this.interId = interId;
    }

    public String requestNo; //签约请求号
    public String interId; //签约渠道接口ID

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

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }


    public String getIsRealnameCard() {
        return isRealnameCard;
    }

    public void setIsRealnameCard(String isRealnameCard) {
        this.isRealnameCard = isRealnameCard;
    }

    public String getIsDefaultCard() {
        return isDefaultCard;
    }

    public void setIsDefaultCard(String isDefaultCard) {
        this.isDefaultCard = isDefaultCard;
    }

    public String getVerifyMobile() {
        return verifyMobile;
    }

    public void setVerifyMobile(String verifyMobile) {
        this.verifyMobile = verifyMobile;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getIsRsa() {
        return isRsa;
    }

    public void setIsRsa(String isRsa) {
        this.isRsa = isRsa;
    }
}
