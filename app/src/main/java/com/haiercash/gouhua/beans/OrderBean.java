package com.haiercash.gouhua.beans;

/**
 * Created by Limige on 2016/9/23.
 * 录单大流程接口数据类
 */

public class OrderBean {

    /**
     * crdComAvailAnt : 0
     * bankCode : 105
     * custNo : C201601041201219180940
     * bdMobile : 13671958094
     * crdNorAvailAmt : 0
     * crdSts : 00
     * bankName : 中国建设银行
     * custName : 曹立
     * idNo : 452426198212012191
     */

    public String crdComAvailAnt;
    public String bankCode;
    public String custNo;
    public String bdMobile;
    public String crdNorAvailAmt;
    public String crdSts;
    public String bankName;
    public String custName;
    public String idNo;
    public String outSts;
    public int crdSeq;
    public String retMsg;

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retNsg) {
        this.retMsg = retNsg;
    }

    public int getCrdSeq() {
        return crdSeq;
    }

    public void setCrdSeq(int crdSeq) {
        this.crdSeq = crdSeq;
    }

    public String getOutSts() {
        return outSts;
    }

    public void setOutSts(String outSts) {
        this.outSts = outSts;
    }

    public String getCrdComAvailAnt() {
        return crdComAvailAnt;
    }

    public void setCrdComAvailAnt(String crdComAvailAnt) {
        this.crdComAvailAnt = crdComAvailAnt;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getBdMobile() {
        return bdMobile;
    }

    public void setBdMobile(String bdMobile) {
        this.bdMobile = bdMobile;
    }

    public String getCrdNorAvailAmt() {
        return crdNorAvailAmt;
    }

    public void setCrdNorAvailAmt(String crdNorAvailAmt) {
        this.crdNorAvailAmt = crdNorAvailAmt;
    }

    public String getCrdSts() {
        return crdSts;
    }

    public void setCrdSts(String crdSts) {
        this.crdSts = crdSts;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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
}
