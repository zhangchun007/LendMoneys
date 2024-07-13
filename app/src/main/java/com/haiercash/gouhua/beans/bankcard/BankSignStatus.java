package com.haiercash.gouhua.beans.bankcard;

import java.io.Serializable;

/**
 * @Author: Sun
 * @Date :    2018/7/6
 * @FileName: BankSignStatus
 * @Description:
 */
public class BankSignStatus implements Serializable {

    private String custNo; //客户号
    private String certNo; //身份证号
    private String name;   //姓名
    private String mobileNo; //实名手机号
    private String loanNo;  //借据号
    private String cardNo;  //卡号
    private String signStatus;  //卡签约状态
    private String odDays;  //逾期天数
    private String repayDay;  //还款日
    private String repayAmount;  //应还款金额

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getLoanNo() {
        return loanNo;
    }

    public void setLoanNo(String loanNo) {
        this.loanNo = loanNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(String signStatus) {
        this.signStatus = signStatus;
    }

    public String getOdDays() {
        return odDays;
    }

    public void setOdDays(String odDays) {
        this.odDays = odDays;
    }

    public String getRepayDay() {
        return repayDay;
    }

    public void setRepayDay(String repayDay) {
        this.repayDay = repayDay;
    }

    public String getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(String repayAmount) {
        this.repayAmount = repayAmount;
    }
}
