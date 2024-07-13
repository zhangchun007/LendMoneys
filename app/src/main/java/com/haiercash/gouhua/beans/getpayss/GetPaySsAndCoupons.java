package com.haiercash.gouhua.beans.getpayss;

import java.util.List;

/**
 * 借款页面试算
 */
public class GetPaySsAndCoupons {
    private GetPaySsBeanRtn paySsResult;
    private List<LoanCoupon> loanCoupons;
    private String needShowRes;//是否需要展示会员卡资源位,Y ：需要展示,N ：不需要展示
    private String batchFrom;//券列表归属account:账户内,vip:vip卡内
    private String discValue;//优惠金额
    private String calVol;//优惠期数
    private String discRepayAmt;//优惠后金额


    public String getDiscValue() {
        return discValue;
    }
    public void setDiscValue(String discValue) {
        this.discValue = discValue;
    }

    public String getCalVol() {
        return calVol;
    }

    public void setCalVol(String calVol) {
        this.calVol = calVol;
    }

    public String getDiscRepayAmt() {
        return discRepayAmt;
    }

    public void setDiscRepayAmt(String discRepayAmt) {
        this.discRepayAmt = discRepayAmt;
    }

    public String getNeedShowRes() {
        return needShowRes;
    }

    public void setNeedShowRes(String needShowRes) {
        this.needShowRes = needShowRes;
    }

    public String getBatchFrom() {
        return batchFrom;
    }

    public void setBatchFrom(String batchFrom) {
        this.batchFrom = batchFrom;
    }

    public GetPaySsBeanRtn getPaySsResult() {
        return paySsResult;
    }

    public void setPaySsResult(GetPaySsBeanRtn paySsResult) {
        this.paySsResult = paySsResult;
    }

    public List<LoanCoupon> getLoanCoupons() {
        return loanCoupons;
    }

    public void setLoanCoupons(List<LoanCoupon> loanCoupons) {
        this.loanCoupons = loanCoupons;
    }
}
