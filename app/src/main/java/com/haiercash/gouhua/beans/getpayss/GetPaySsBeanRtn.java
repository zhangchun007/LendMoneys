package com.haiercash.gouhua.beans.getpayss;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by use on 2016/5/16.
 * 总利息金额	totalNormInt
 * 总还款额	totalAmt
 * 费用总额	totalFeeAmt
 * 期号	psPerdNo
 * 应归还期供	instmAmt
 * 斩头息 deductAmt
 * 实际到账金额 actualArriveAmt
 * 总息费金额 totalFees（包含 利息、手续费和斩头息）
 * 应还总额 repaymentTotalAmt （应还的总额度，直接在UI上显示）
 */
public class GetPaySsBeanRtn {
    public String totalNormInt;                 //总利息金额
    public String totalAmt;                     //总还款额
    public String totalFeeAmt;                  //费用总额
    //    public String deductAmt;                    //斩头息费          第0期的息费
    public String actualArriveAmt;              //实际到账金额        本金-第0期的费用
    public String totalFees;                    //总息费金额         totalNormInt+ totalFeeAmt+ deductAmt
    public String repaymentTotalAmt;            //还款总额（UI 展示）       总还款额+费用总额
    public String disposableReimburseFee;       //一次性还款手续费
    public String feeRateByPeriods;             //期手续费率
    public String discTotalNormInt;             //优惠后的总利息
    public List<GetPaySsList> mx;


    public String getRepaymentTotalAmt() {
        return repaymentTotalAmt;
    }

    public void setRepaymentTotalAmt(String repaymentTotalAmt) {
        this.repaymentTotalAmt = repaymentTotalAmt;
    }

//    public String getDeductAmt() {
//        return deductAmt;
//    }
//
//    public void setDeductAmt(String deductAmt) {
//        this.deductAmt = deductAmt;
//    }

    public String getActualArriveAmt() {
        return actualArriveAmt;
    }

    public void setActualArriveAmt(String actualArriveAmt) {
        this.actualArriveAmt = actualArriveAmt;
    }

    public String getTotalFees() {
        return totalFees;
    }

    public void setTotalFees(String totalFees) {
        this.totalFees = totalFees;
    }

    public String getTotalNormInt() {
        return totalNormInt;
    }

    public void setTotalNormInt(String totalNormInt) {
        this.totalNormInt = totalNormInt;
    }

    public String getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(String totalAmt) {
        this.totalAmt = totalAmt;
    }

    public String getTotalFeeAmt() {
        return totalFeeAmt;
    }

    public void setTotalFeeAmt(String totalFeeAmt) {
        this.totalFeeAmt = totalFeeAmt;
    }

    public String getFeeRateByPeriods() {
        return feeRateByPeriods;
    }

    public void setFeeRateByPeriods(String feeRateByPeriods) {
        this.feeRateByPeriods = feeRateByPeriods;
    }
//    public String getPsPerdNo() {
//        return psPerdNo;
//    }
//
//    public void setPsPerdNo(String psPerdNo) {
//        this.psPerdNo = psPerdNo;
//    }
//
//    public String getInstmAmt() {
//        return instmAmt;
//    }
//
//    public void setInstmAmt(String instmAmt) {
//        this.instmAmt = instmAmt;
//    }

    public List<GetPaySsList> getMx() {
        return mx;
    }

    public void setMx(List<GetPaySsList> mx) {
        this.mx = mx;
    }

    public String getDisposableReimburseFee() {
        return disposableReimburseFee;
    }

    public void setDisposableReimburseFee(String disposableReimburseFee) {
        this.disposableReimburseFee = disposableReimburseFee;
    }

    public String getDiscTotalNormInt() {
        return discTotalNormInt;
    }

    public void setDiscTotalNormInt(String discTotalNormInt) {
        this.discTotalNormInt = discTotalNormInt;
    }

    /**
     * 构建假的数据用于测试
     */
    public GetPaySsBeanRtn setTestBean() {
        totalNormInt = "500";
        totalAmt = "5500";
        totalFeeAmt = "100";
        actualArriveAmt = "4900";
        totalFees = "510";
        repaymentTotalAmt = "5600";
        disposableReimburseFee = "200";
        feeRateByPeriods = "0.0008";
        mx = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            GetPaySsList getPaySsList = new GetPaySsList();
            getPaySsList.psPerdNo = i + 1 + "";
            getPaySsList.prcpAmt = 416.67 + "";
            getPaySsList.normInt = 120 + "";
            getPaySsList.instmAmt = 450 + "";
            getPaySsList.psFeeAmt = 10 + "";
            getPaySsList.dueDt = "2022-01-1"+(i+5);
            mx.add(getPaySsList);
        }
        return this;
    }
}
