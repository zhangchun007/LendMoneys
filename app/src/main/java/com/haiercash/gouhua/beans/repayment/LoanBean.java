package com.haiercash.gouhua.beans.repayment;

import java.io.Serializable;

/**
 * 部分应还
 */
public class LoanBean implements Serializable {
    private String loanNo;//借据号
    private String applSeq;//申请流水号
    private String applyDt;//申请日期
    private String psPerdNo;//期号
    //private String applyTnr;//期数
    private String apprvTnr;//期数
    private String applyTnrTyp;//期数类型
    private String psDueDt;//到期日
    private String remainDays;//剩余天数  天数小于0，表示“逾期”
    private String loanTypeCode;//贷款品种
    private String apprvAmt; //贷款金额
    private String amount;//单期非逾期期供金额（应还金额）
    private String odAmount;//单期逾期应还金额   若无逾期该字段返回
    private String psPrcpAmt;//单期应还本金
    private String psNormInt;//单期应还利息
    private String psOdIntAmt;//单期应还罚息
    private String psFeeAmt;//单期应还手续费
    //不良：bad、
    //逾期：overdue、
    //正常：normal
    private String loanTyp;//借据状态
    private String prcpAmt;//总应还本金
    private String mtdCde;//还款方式
    //00 ： 可发起主动还款
    //01 ： 还款日当天不能主动还款
    //02 ： 信贷日结不能进行主动还款
    //03 ： 其他异常原因导致未能得到准确的是否可以还款的状态
    private String canActiveRepayStatus;//能否进行主动还款的状态
    //Y 是 N 否
    private String loanMode;//是否联合放款
    //01：还款处理中
    //02：还款成功
    //03：还款失败
    private String repayStatus;//还款状态

    private String hasCoupon;//是否存在绑定免息券,Y存在，N不存在
    private String couponNo;//券号,hasCoupon为Y时返回
    private String discValue;//优惠金额,hasCoupon为Y时返回
    private String discRepayAmt;//优惠后金额,hasCoupon为Y时返回,后面可能本地计算获取并设置

    //本地设置属性
    private boolean isChecked;//是否被选中
    private boolean isEnable;
    private boolean useCoupon;//是否使用券，单选才能使用（显示）

    public boolean isUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(boolean useCoupon) {
        this.useCoupon = useCoupon;
    }

    public String getHasCoupon() {
        return hasCoupon;
    }

    public void setHasCoupon(String hasCoupon) {
        this.hasCoupon = hasCoupon;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public String getDiscValue() {
        return discValue;
    }

    public void setDiscValue(String discValue) {
        this.discValue = discValue;
    }

    public String getLoanNo() {
        return loanNo;
    }

    public void setLoanNo(String loanNo) {
        this.loanNo = loanNo;
    }

    public String getApplSeq() {
        return applSeq;
    }

    public void setApplSeq(String applSeq) {
        this.applSeq = applSeq;
    }

    public String getApplyDt() {
        return applyDt;
    }

    public void setApplyDt(String applyDt) {
        this.applyDt = applyDt;
    }

    public String getPsPerdNo() {
        return psPerdNo;
    }

    public String getDiscRepayAmt() {
        return discRepayAmt;
    }

    public void setDiscRepayAmt(String discRepayAmt) {
        this.discRepayAmt = discRepayAmt;
    }

    public void setPsPerdNo(String psPerdNo) {
        this.psPerdNo = psPerdNo;
    }

    public String getApprvTnr() {
        return apprvTnr;
    }

    public void setApprvTnr(String apprvTnr) {
        this.apprvTnr = apprvTnr;
    }

    public String getApplyTnrTyp() {
        return applyTnrTyp;
    }

    public void setApplyTnrTyp(String applyTnrTyp) {
        this.applyTnrTyp = applyTnrTyp;
    }

    public String getPsDueDt() {
        return psDueDt;
    }

    public void setPsDueDt(String psDueDt) {
        this.psDueDt = psDueDt;
    }

    public String getRemainDays() {
        return remainDays;
    }

    public void setRemainDays(String remainDays) {
        this.remainDays = remainDays;
    }

    public String getLoanTypeCode() {
        return loanTypeCode;
    }

    public void setLoanTypeCode(String loanTypeCode) {
        this.loanTypeCode = loanTypeCode;
    }

    public String getApprvAmt() {
        return apprvAmt;
    }

    public void setApprvAmt(String apprvAmt) {
        this.apprvAmt = apprvAmt;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOdAmount() {
        return odAmount;
    }

    public void setOdAmount(String odAmount) {
        this.odAmount = odAmount;
    }

    public String getPsPrcpAmt() {
        return psPrcpAmt;
    }

    public void setPsPrcpAmt(String psPrcpAmt) {
        this.psPrcpAmt = psPrcpAmt;
    }

    public String getPsNormInt() {
        return psNormInt;
    }

    public void setPsNormInt(String psNormInt) {
        this.psNormInt = psNormInt;
    }

    public String getPsOdIntAmt() {
        return psOdIntAmt;
    }

    public void setPsOdIntAmt(String psOdIntAmt) {
        this.psOdIntAmt = psOdIntAmt;
    }

    public String getPsFeeAmt() {
        return psFeeAmt;
    }

    public void setPsFeeAmt(String psFeeAmt) {
        this.psFeeAmt = psFeeAmt;
    }

    public String getLoanTyp() {
        return loanTyp;
    }

    public void setLoanTyp(String loanTyp) {
        this.loanTyp = loanTyp;
    }

    public String getPrcpAmt() {
        return prcpAmt;
    }

    public void setPrcpAmt(String prcpAmt) {
        this.prcpAmt = prcpAmt;
    }

    public String getMtdCde() {
        return mtdCde;
    }

    public void setMtdCde(String mtdCde) {
        this.mtdCde = mtdCde;
    }

    public String getCanActiveRepayStatus() {
        return canActiveRepayStatus;
    }

    public void setCanActiveRepayStatus(String canActiveRepayStatus) {
        this.canActiveRepayStatus = canActiveRepayStatus;
    }

    public String getLoanMode() {
        return loanMode;
    }

    public void setLoanMode(String loanMode) {
        this.loanMode = loanMode;
    }

    public String getRepayStatus() {
        return repayStatus;
    }

    public void setRepayStatus(String repayStatus) {
        this.repayStatus = repayStatus;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
