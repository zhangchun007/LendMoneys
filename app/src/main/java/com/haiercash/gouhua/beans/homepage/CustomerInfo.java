package com.haiercash.gouhua.beans.homepage;

import java.io.Serializable;

/**
 * 个人信息
 */
public class CustomerInfo implements Serializable {
    private String custNo;//客户编号String
    private String custName;//客户姓名String
    private String certType;//证件类型String
    private String certNo;//证件号码String
    private String cardNo;//银行卡号String
    private String mobile;//手机号String
    private String acctName;//账户名String
    private String acctNo;//账号String
    private String acctBankNo;//银行代码String
    private String acctBankName;//银行名称String
    private String acctProvince;//开户省String
    private String acctCity;//开户市String
    private String acctArea;// 开户区String
    private String accBchCde;//开户机构代码String
    private String accBchName;//开户机构名称String

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

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getAcctBankNo() {
        return acctBankNo;
    }

    public void setAcctBankNo(String acctBankNo) {
        this.acctBankNo = acctBankNo;
    }

    public String getAcctBankName() {
        return acctBankName;
    }

    public void setAcctBankName(String acctBankName) {
        this.acctBankName = acctBankName;
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

    public String getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(String dataFrom) {
        this.dataFrom = dataFrom;
    }

    public CustomerInfo(String custNo, String custName, String certType, String certNo, String cardNo, String mobile, String acctName, String acctNo, String acctBankNo, String acctBankName, String acctProvince, String acctCity, String acctArea, String accBchCde, String accBchName, String dataFrom) {
        this.custNo = custNo;
        this.custName = custName;
        this.certType = certType;
        this.certNo = certNo;
        this.cardNo = cardNo;
        this.mobile = mobile;
        this.acctName = acctName;
        this.acctNo = acctNo;
        this.acctBankNo = acctBankNo;
        this.acctBankName = acctBankName;
        this.acctProvince = acctProvince;
        this.acctCity = acctCity;
        this.acctArea = acctArea;
        this.accBchCde = accBchCde;
        this.accBchName = accBchName;
        this.dataFrom = dataFrom;
    }

    public CustomerInfo() {
    }

    private String dataFrom;//数据来源String
}
