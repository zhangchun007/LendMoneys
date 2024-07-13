package com.haiercash.gouhua.beans.borrowmoney;

/**
 * 借款记录
 */
public class RecordBean {
    private String orderNo;//订单号
    private String applSeq;//申请流水号
    private String custId;//客户编号（信贷）
    private String applyAmt;//申请金额
    private String apprvAmt;//审批金额
    private String applyTnr;//期数
    private String applyDt;//申请日期  “YYYY-MM-dd”
    private String typGrp;//贷款类型
    private String borrowingApplStatus;//借款状态  0：借款成功 1：借款处理中 2：借款失败
    private String ifSettled;//还款状态 OD：逾期 SE：已结清  NS：还款中
    private String commitTime;//借款提交时间

    private String bankCode;                 //银行编码
    private String bankName;                 //银行名称
    private String cardLast4No;//卡后四位
    private String applyTime;//申请时间	 yyyy-MM-dd HH:mm:ss
    private String applyTimeWithYear;//申请时间
    private String processingDate;//审核中时间	 yyyy-MM-dd HH:mm:ss。
    private String processingDateWithYear;//审核中时间
    private String finalStatusTime;//最终成功/失败时间	 yyyy-MM-dd HH:mm:ss 该字段只有在borrowingApplStatus为0或2时会有
    private String finalStatusTimeWithYear; //Yyyy-MM-dd  最终成功/失败时间 该字段只有在borrowingApplStatus为0或2时会有
    private String loanSuccessTime;//放款到卡成功时间
    public String getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(String commitTime) {
        this.commitTime = commitTime;
    }

    public String getLoanSuccessTime() {
        return loanSuccessTime;
    }

    public void setLoanSuccessTime(String loanSuccessTime) {
        this.loanSuccessTime = loanSuccessTime;
    }


    public String getIfSettled() {
        return ifSettled;
    }

    public void setIfSettled(String ifSettled) {
        this.ifSettled = ifSettled;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getApplSeq() {
        return applSeq;
    }

    public void setApplSeq(String applSeq) {
        this.applSeq = applSeq;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getApplyAmt() {
        return applyAmt;
    }

    public void setApplyAmt(String applyAmt) {
        this.applyAmt = applyAmt;
    }

    public String getApprvAmt() {
        return apprvAmt;
    }

    public void setApprvAmt(String apprvAmt) {
        this.apprvAmt = apprvAmt;
    }

    public String getApplyTnr() {
        return applyTnr;
    }

    public void setApplyTnr(String applyTnr) {
        this.applyTnr = applyTnr;
    }

    public String getApplyDt() {
        return applyDt;
    }

    public void setApplyDt(String applyDt) {
        this.applyDt = applyDt;
    }

    public String getTypGrp() {
        return typGrp;
    }

    public void setTypGrp(String typGrp) {
        this.typGrp = typGrp;
    }

    public String getBorrowingApplStatus() {
        return borrowingApplStatus;
    }

    public void setBorrowingApplStatus(String borrowingApplStatus) {
        this.borrowingApplStatus = borrowingApplStatus;
    }
    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardLast4No() {
        return cardLast4No;
    }

    public void setCardLast4No(String cardLast4No) {
        this.cardLast4No = cardLast4No;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getApplyTimeWithYear() {
        return applyTimeWithYear;
    }

    public void setApplyTimeWithYear(String applyTimeWithYear) {
        this.applyTimeWithYear = applyTimeWithYear;
    }

    public String getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(String processingDate) {
        this.processingDate = processingDate;
    }

    public String getProcessingDateWithYear() {
        return processingDateWithYear;
    }

    public void setProcessingDateWithYear(String processingDateWithYear) {
        this.processingDateWithYear = processingDateWithYear;
    }

    public String getFinalStatusTime() {
        return finalStatusTime;
    }

    public void setFinalStatusTime(String finalStatusTime) {
        this.finalStatusTime = finalStatusTime;
    }

    public String getFinalStatusTimeWithYear() {
        return finalStatusTimeWithYear;
    }

    public void setFinalStatusTimeWithYear(String finalStatusTimeWithYear) {
        this.finalStatusTimeWithYear = finalStatusTimeWithYear;
    }

}
