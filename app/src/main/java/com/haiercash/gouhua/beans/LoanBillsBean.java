package com.haiercash.gouhua.beans;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/6/30<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class LoanBillsBean {

    private String cardChangeFlag;//换卡标识 Y 已换卡  N 未换卡
    private String loanNo;//借据号
    private String applyDate;//贷款申请时间
    private String applyAmount;//贷款金额
    private String bankName;//已关联卡名称
    private String cardNo;//已关联卡号
    private boolean isChoose = true;

    public String getCardChangeFlag() {
        return cardChangeFlag;
    }

    public void setCardChangeFlag(String cardChangeFlag) {
        this.cardChangeFlag = cardChangeFlag;
    }

    public String getLoanNo() {
        return loanNo;
    }

    public void setLoanNo(String loanNo) {
        this.loanNo = loanNo;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(String applyAmount) {
        this.applyAmount = applyAmount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }
}
