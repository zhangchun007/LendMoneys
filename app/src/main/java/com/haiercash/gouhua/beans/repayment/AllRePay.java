package com.haiercash.gouhua.beans.repayment;

import com.app.haiercash.base.utils.system.CheckUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class AllRePay implements Serializable {
    private String currentTnr;//当前期数,存在当期数据时才返回
    private String interest;//当前期数利息,存在当期数据时才返回
    private String loanNo;                   //	String              借据号
    private String prcpAmt;                  //	String              应还本金
    private String normInt;                  //	String              应还利息
    private String psFeeAmt;                 //	String              应还手续费 该手续费包含提前还款手续费
    private String lateFeeAmt;               //	String              滞纳金
    private String mtdCde;                   //	String              还款方式
    private String mtdDesc;                  //	String              还款方式描述
    private String loanSetlInd;              //	String              借据结清标志			Y:已结清，N：未结清
    private String repayMent;                //	String              还款期限
    private String loanActvDt;               //	String              借据放款日期
    private String stayAmount;               //	String              借据未还总金额
    private String totalPrcpAmt;              // 借款本金
    private String applyDt;              // 申请时间
    /**
     * 不良：bad（超过90天）、
     * 逾期：overdue(未超过90天)、
     * 正常：normal
     */
    private String loanTyp;                  //	String              借据状态
    //private int listNum;                  //Integer               总期数
    //private String unSetlNum;      //	Intege  未结清期数  未结清期数计数：    N：未结清，    01：还款处理中，    03：还款失败
    //private String activeRepayAllow;       //是否允许主动还款    String    Y：允许    N：不允许
    private List<LmpmshdBean> lmpmshdlist;//":Array[13],
    //private String totleAmt;
    private String applCardNo;  //放款银行卡卡号
    private String applBankName;  //放款银行卡银行名
    private String applBankCode;  //放款银行卡银行号
    private String repayApplAcNo;  //还款银行卡卡号
    private String repayBankCode;  //还款银行卡银行号
    private String repayBankName;  //还款银行卡银行名
    private String repayDate;  //还款日
    private String odBadPsPerdNo;  //逾期超过90天的期数 格式为逗号分隔，如：1，3，9
    private String odCount;  //逾期未超过90天的总期数
    /**
     * 00 ： 可发起主动还款
     * 01 ： 还款日/放款日当天不能主动还款
     * 02 ： 信贷日结不能进行主动还款
     * 03 ： 其他异常原因导致未能得到准确的是否可以还款的状态
     */
    private String canActiveRepayStatus;  //能否进行主动还款的状态
    private String totalOdIntAmt;  //当前借据总罚息
    private String psPrcpAmtDec;  //当前借据总应还本金
    private String psNormIntDec;  //当前借据总利息
    private String psFeeAmtDec;  //当前借据总手续费 不包含提前还款手续费
    private String advanceFeeAmtDec;  //当前借据总提前还款手续费
    //private String lateFeeAmtDec;  //当前借据总滞纳金
    //private String totalOdAmtDec;  //当前借据总逾期金额  逾期还款的金额取该字段
    private String totalPsInstmAmtDec;  //当前借据总期供金额(全部应还)
    //private String waitPayPsPerdNo;  //未还的所有期数 格式为|分割如：2|3|5|7
    //private String odWaitPayPsPerdNo;  //还逾期的期号参数 格式为|分割如：2|3|5|7
    private String applSeq; //申请流水号

    //private String currentRepayMent; //当前应还款期数
    /**
     * Y:已结清，N：未结清 01：还款处理中 2.6.0新增
     */
    private String loanRepayStatus;//借据还款状态
    private String isOnlyOd;// 是否仅含逾期       Y 是  N 否
    private String allowChangeCard; //是否允许换卡        Y 是  N 否
    private String allowChangeCardTime;// 允许换卡的时间段      如 18:00-24:00
    private String repayedNum; //已还期数和      包含已结清，还款成功，还款处理中
    private String unRepayedNum; //未还期数
    private String apprvTnr;// 借款期数
    private String apprvAmt;//借款金额
    private String loanTypeCode;//贷款品种
    //private String loanOdInd;// 是否逾期
    //还款方式描述    payModeDesc
    //本金是否展示    principalShow   是 Y 否 N
    //利息是否展示    interestShow    是 Y 否 N
    //手续费是否展示  feeShow          是 Y 否 N
    //违约金是否展示   penaltyShow    是 Y 否 N
    //罚息是否展示    odIntShow       是 Y 否 N
    private Object billDetailPageShow; //账单详情页面开关
    //Y 是 N 否
    private String loanMode;//是否联合放款

    private HashMap<String, Object> interests;

    public HashMap<String, Object> getInterests() {
        return interests;
    }

    public void setInterests(HashMap<String, Object> interests) {
        this.interests = interests;
    }

    public String getCurrentTnr() {
        return currentTnr;
    }

    public void setCurrentTnr(String currentTnr) {
        this.currentTnr = currentTnr;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getDiscValue() {
        return discValue;
    }

    public void setDiscValue(String discValue) {
        this.discValue = discValue;
    }

    public String getApprvAmt() {
        return apprvAmt;
    }

    public void setApprvAmt(String apprvAmt) {
        this.apprvAmt = apprvAmt;
    }

    public String getLoanTypeCode() {
        return loanTypeCode;
    }

    public void setLoanTypeCode(String loanTypeCode) {
        this.loanTypeCode = loanTypeCode;
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

    //本地设置的属性
    private boolean selected = false;
    private String discValue;//优惠金额
    private String discRepayAmt;//优惠后金额
    private boolean useCoupon;//是否使用券，单选才能使用（显示）

    //计算出的当前待还期数
    public String getCurrentRepayNum() {
        return !CheckUtil.isEmpty(unRepayedNum) ? Integer.valueOf(repayedNum) + 1 + "" : null;
    }

    public boolean isUseCoupon() {
        return useCoupon;
    }

    public void setUseCoupon(boolean useCoupon) {
        this.useCoupon = useCoupon;
    }

    public String getDiscRepayAmt() {
        return discRepayAmt;
    }

    public void setDiscRepayAmt(String discRepayAmt) {
        this.discRepayAmt = discRepayAmt;
    }

    public Object getBillDetailPageShow() {
        return billDetailPageShow;
    }

    public void setBillDetailPageShow(Object billDetailPageShow) {
        this.billDetailPageShow = billDetailPageShow;
    }

    public String getApprvTnr() {
        return apprvTnr;
    }

    public void setApprvTnr(String apprvTnr) {
        this.apprvTnr = apprvTnr;
    }

    public String getApplyDt() {
        return applyDt;
    }

    public void setApplyDt(String applyDt) {
        this.applyDt = applyDt;
    }

    public String getAllowChangeCard() {
        return allowChangeCard;
    }

    public String getUnRepayedNum() {
        return unRepayedNum;
    }

    public void setUnRepayedNum(String unRepayedNum) {
        this.unRepayedNum = unRepayedNum;
    }

    public void setAllowChangeCard(String allowChangeCard) {
        this.allowChangeCard = allowChangeCard;
    }

    public String getLoanRepayStatus() {
        return loanRepayStatus;
    }

    public void setLoanRepayStatus(String loanRepayStatus) {
        this.loanRepayStatus = loanRepayStatus;
    }

    public String getRepayDate() {
        return repayDate;
    }

    public void setRepayDate(String repayDate) {
        this.repayDate = repayDate;
    }

    public String getAllowChangeCardTime() {
        return allowChangeCardTime;
    }

    public void setAllowChangeCardTime(String allowChangeCardTime) {
        this.allowChangeCardTime = allowChangeCardTime;
    }

    public String getIsOnlyOd() {
        return isOnlyOd;
    }

    public void setIsOnlyOd(String isOnlyOd) {
        this.isOnlyOd = isOnlyOd;
    }

    public String getApplBankName() {
        return applBankName;
    }

    public void setApplBankName(String applBankName) {
        this.applBankName = applBankName;
    }

    public String getRepayApplAcNo() {
        return repayApplAcNo;
    }

    public void setRepayApplAcNo(String repayApplAcNo) {
        this.repayApplAcNo = repayApplAcNo;
    }

    public String getRepayBankName() {
        return repayBankName;
    }

    public void setRepayBankName(String repayBankName) {
        this.repayBankName = repayBankName;
    }

    public String getApplSeq() {
        return applSeq;
    }

    public void setApplSeq(String applSeq) {
        this.applSeq = applSeq;
    }


    public String getApplCardNo() {
        return applCardNo;
    }

    public void setApplCardNo(String applCardNo) {
        this.applCardNo = applCardNo;
    }

    public String getRepayedNum() {
        return repayedNum;
    }

    public void setRepayedNum(String repayedNum) {
        this.repayedNum = repayedNum;
    }

    public String getLoanNo() {
        return loanNo;
    }

    public void setLoanNo(String loanNo) {
        this.loanNo = loanNo;
    }

    public List<LmpmshdBean> getLmpmshdlist() {
        return lmpmshdlist;
    }

    public void setLmpmshdlist(List<LmpmshdBean> lmpmshdlist) {
        this.lmpmshdlist = lmpmshdlist;
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

    public String getStayAmount() {
        return stayAmount;
    }

    public void setStayAmount(String stayAmount) {
        this.stayAmount = stayAmount;
    }

    public String getMtdDesc() {
        return mtdDesc;
    }

    public void setMtdDesc(String mtdDesc) {
        this.mtdDesc = mtdDesc;
    }

    public String getLoanSetlInd() {
        return loanSetlInd;
    }

    public void setLoanSetlInd(String loanSetlInd) {
        this.loanSetlInd = loanSetlInd;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getNormInt() {
        return normInt;
    }

    public void setNormInt(String normInt) {
        this.normInt = normInt;
    }

    public String getPsFeeAmt() {
        return psFeeAmt;
    }

    public void setPsFeeAmt(String psFeeAmt) {
        this.psFeeAmt = psFeeAmt;
    }

    public String getLateFeeAmt() {
        return lateFeeAmt;
    }

    public void setLateFeeAmt(String lateFeeAmt) {
        this.lateFeeAmt = lateFeeAmt;
    }

    public String getMtdCde() {
        return mtdCde;
    }

    public void setMtdCde(String mtdCde) {
        this.mtdCde = mtdCde;
    }

    public String getRepayMent() {
        return repayMent;
    }

    public void setRepayMent(String repayMent) {
        this.repayMent = repayMent;
    }

    public String getLoanActvDt() {
        return loanActvDt;
    }

    public void setLoanActvDt(String loanActvDt) {
        this.loanActvDt = loanActvDt;
    }

    public String getTotalPrcpAmt() {
        return totalPrcpAmt;
    }

    public void setTotalPrcpAmt(String totalPrcpAmt) {
        this.totalPrcpAmt = totalPrcpAmt;
    }

    public String getApplBankCode() {
        return applBankCode;
    }

    public void setApplBankCode(String applBankCode) {
        this.applBankCode = applBankCode;
    }

    public String getRepayBankCode() {
        return repayBankCode;
    }

    public void setRepayBankCode(String repayBankCode) {
        this.repayBankCode = repayBankCode;
    }

    public String getOdBadPsPerdNo() {
        return odBadPsPerdNo;
    }

    public void setOdBadPsPerdNo(String odBadPsPerdNo) {
        this.odBadPsPerdNo = odBadPsPerdNo;
    }

    public String getOdCount() {
        return odCount;
    }

    public void setOdCount(String odCount) {
        this.odCount = odCount;
    }

    public String getTotalOdIntAmt() {
        return totalOdIntAmt;
    }

    public void setTotalOdIntAmt(String totalOdIntAmt) {
        this.totalOdIntAmt = totalOdIntAmt;
    }

    public String getPsPrcpAmtDec() {
        return psPrcpAmtDec;
    }

    public void setPsPrcpAmtDec(String psPrcpAmtDec) {
        this.psPrcpAmtDec = psPrcpAmtDec;
    }

    public String getPsNormIntDec() {
        return psNormIntDec;
    }

    public void setPsNormIntDec(String psNormIntDec) {
        this.psNormIntDec = psNormIntDec;
    }

    public String getPsFeeAmtDec() {
        return psFeeAmtDec;
    }

    public void setPsFeeAmtDec(String psFeeAmtDec) {
        this.psFeeAmtDec = psFeeAmtDec;
    }

    public String getAdvanceFeeAmtDec() {
        return advanceFeeAmtDec;
    }

    public void setAdvanceFeeAmtDec(String advanceFeeAmtDec) {
        this.advanceFeeAmtDec = advanceFeeAmtDec;
    }

    public String getTotalPsInstmAmtDec() {
        return totalPsInstmAmtDec;
    }

    public void setTotalPsInstmAmtDec(String totalPsInstmAmtDec) {
        this.totalPsInstmAmtDec = totalPsInstmAmtDec;
    }
}
