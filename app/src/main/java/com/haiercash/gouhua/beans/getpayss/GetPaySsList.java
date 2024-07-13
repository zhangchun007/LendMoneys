package com.haiercash.gouhua.beans.getpayss;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by use on 2016/5/16.
 * totalNormInt
 * totalAmt
 * totalFeeAmt
 */
public class GetPaySsList implements Serializable {
    public String psPerdNo;                 //期号
    public String instmAmt;                 //应归还期供         instmAmt =instmAmt+psFeeAmt
    public String dueDt;                    //到期日
    public String disposableReimburseFee;   //一次性还款手续费
    public String prcpAmt;                  //应归还本金
    public String normInt;                  //应归还利息
    public String psFeeAmt;                 //每期手续费
    public String discInstmAmt;                 //优惠后的期供金额
    public boolean isOpened;

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public String getDisposableReimburseFee() {
        return disposableReimburseFee;
    }

    public void setDisposableReimburseFee(String disposableReimburseFee) {
        this.disposableReimburseFee = disposableReimburseFee;
    }

    public String getPrcpAmt() {
        return prcpAmt;
    }

    public void setPrcpAmt(String prcpAmt) {
        this.prcpAmt = prcpAmt;
    }

    public String getNormInt() {
        return normInt;
    }

    public void setNormInt(String normInt) {
        this.normInt = normInt;
    }

    public String getPsPerdNo() {
        return psPerdNo;
    }

    public void setPsPerdNo(String psPerdNo) {
        this.psPerdNo = psPerdNo;
    }

    public String getInstmAmt() {
        return instmAmt;
    }

    public void setInstmAmt(String instmAmt) {
        this.instmAmt = instmAmt;
    }

    public String getDueDt() {
        return dueDt;
    }

    public void setDueDt(String dueDt) {
        this.dueDt = dueDt;
    }

    public String getPsFeeAmt() {
        return psFeeAmt;
    }

    public void setPsFeeAmt(String psFeeAmt) {
        this.psFeeAmt = psFeeAmt;
    }

    public String getDiscInstmAmt() {
        return discInstmAmt;
    }

    public void setDiscInstmAmt(String discInstmAmt) {
        this.discInstmAmt = discInstmAmt;
    }

    public Date getData() {
        String time = getDueDt();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
