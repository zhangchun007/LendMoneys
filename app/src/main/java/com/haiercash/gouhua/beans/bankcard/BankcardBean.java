package com.haiercash.gouhua.beans.bankcard;

/**
 * Created by Administrator on 2017/6/21.
 * 银行卡信息
 */

public class BankcardBean {
    /**
     * bankCode : 102
     * issuerShort : 工商银行
     * singleTransLimited : -1
     * areaId :
     * bizSupport : ON
     * bankName : 中国工商银行
     * egCode : ICBC
     * singleCollLimited : 50000
     * id : 0001
     * status : ON
     */

    private String bankCode;
    private String issuerShort;
    private String singleTransLimited;
    private String areaId;
    private String bizSupport;
    private String bankName;
    private String egCode;
    public String singleCollLimited;
    private String id;
    private String status;

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getIssuerShort() {
        return issuerShort;
    }

    public void setIssuerShort(String issuerShort) {
        this.issuerShort = issuerShort;
    }

    public String getSingleTransLimited() {
        return singleTransLimited;
    }

    public void setSingleTransLimited(String singleTransLimited) {
        this.singleTransLimited = singleTransLimited;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getBizSupport() {
        return bizSupport;
    }

    public void setBizSupport(String bizSupport) {
        this.bizSupport = bizSupport;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getEgCode() {
        return egCode;
    }

    public void setEgCode(String egCode) {
        this.egCode = egCode;
    }

    public String getSingleCollLimited() {
        return singleCollLimited;
    }

    public void setSingleCollLimited(String singleCollLimited) {
        this.singleCollLimited = singleCollLimited;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
