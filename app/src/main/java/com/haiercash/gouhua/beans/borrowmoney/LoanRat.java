package com.haiercash.gouhua.beans.borrowmoney;

import java.io.Serializable;

/**
 * Created by Limige on 2017-07-14.
 * 112、(GET)查询已认证客户的贷款品种及利率费率
 */
public class LoanRat implements Serializable {
    /**
     * tnrMinDays : 3
     * typLvlDesc : 嗨客贷（线上渠道）
     * typLvlCde : 110001
     * intRat : 0.00027
     * typSts : A
     * maxAmt : 200000.00
     * minAmt : 3000.00
     * tnrOpt : 12
     * feeDesc : 现金贷手续费-按贷款周期-个人
     * feeTnrTyp : 03
     * tnrMaxDays :
     * mtdDesc : 等额本息
     * typCde : 16097a
     * mtdCde : 01
     * feePct :
     * typVer : 2
     * typSeq : 14553997
     */

    private String tnrMinDays;
    private String typLvlDesc;
    private String typLvlCde;
    private String intRate;
    private String intRateStr;//日利率，带%直接用于显示的
    private String typSts;
    private String maxAmt;
    private String minAmt;
    private String tnrOpt;
    private String feeDesc;
    private String feeTnrTyp;
    private String tnrMaxDays;
    private String mtdDesc;
    private String typCde;
    private String mtdCde;
    private String feePct;
    private String typVer;
    private String typSeq;
    private String yearRateDesc;

    public String getYearRateDesc() {
        return yearRateDesc;
    }

    public void setYearRateDesc(String yearRateDesc) {
        this.yearRateDesc = yearRateDesc;
    }

    public String getIntRateStr() {
        return intRateStr;
    }

    public void setIntRateStr(String intRateStr) {
        this.intRateStr = intRateStr;
    }

    public String getTnrMinDays() {
        return tnrMinDays;
    }

    public void setTnrMinDays(String tnrMinDays) {
        this.tnrMinDays = tnrMinDays;
    }

    public String getTypLvlDesc() {
        return typLvlDesc;
    }

    public void setTypLvlDesc(String typLvlDesc) {
        this.typLvlDesc = typLvlDesc;
    }

    public String getTypLvlCde() {
        return typLvlCde;
    }

    public void setTypLvlCde(String typLvlCde) {
        this.typLvlCde = typLvlCde;
    }

    public String getIntRate() {
        return intRate;
    }

    public void setIntRate(String intRate) {
        this.intRate = intRate;
    }

    public String getTypSts() {
        return typSts;
    }

    public void setTypSts(String typSts) {
        this.typSts = typSts;
    }

    public String getMaxAmt() {
        return maxAmt;
    }

    public void setMaxAmt(String maxAmt) {
        this.maxAmt = maxAmt;
    }

    public String getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(String minAmt) {
        this.minAmt = minAmt;
    }

    public String getTnrOpt() {
        return tnrOpt;
    }

    public void setTnrOpt(String tnrOpt) {
        this.tnrOpt = tnrOpt;
    }

    public String getFeeDesc() {
        return feeDesc;
    }

    public void setFeeDesc(String feeDesc) {
        this.feeDesc = feeDesc;
    }

    public String getFeeTnrTyp() {
        return feeTnrTyp;
    }

    public void setFeeTnrTyp(String feeTnrTyp) {
        this.feeTnrTyp = feeTnrTyp;
    }

    public String getTnrMaxDays() {
        return tnrMaxDays;
    }

    public void setTnrMaxDays(String tnrMaxDays) {
        this.tnrMaxDays = tnrMaxDays;
    }

    public String getMtdDesc() {
        return mtdDesc;
    }

    public void setMtdDesc(String mtdDesc) {
        this.mtdDesc = mtdDesc;
    }

    public String getTypCde() {
        return typCde;
    }

    public void setTypCde(String typCde) {
        this.typCde = typCde;
    }

    public String getMtdCde() {
        return mtdCde;
    }

    public void setMtdCde(String mtdCde) {
        this.mtdCde = mtdCde;
    }

    public String getFeePct() {
        return feePct;
    }

    public void setFeePct(String feePct) {
        this.feePct = feePct;
    }

    public String getTypVer() {
        return typVer;
    }

    public void setTypVer(String typVer) {
        this.typVer = typVer;
    }

    public String getTypSeq() {
        return typSeq;
    }

    public void setTypSeq(String typSeq) {
        this.typSeq = typSeq;
    }
}
