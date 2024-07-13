package com.haiercash.gouhua.beans.repayment;

/**
 * 待还列表详情实体---埋点用
 * 还款借据 loan_id
 * 借据待还总额 loannotrepay_sum
 * 借据剩余期数loannotrepay_periods
 */
public class BillEventDetail {
    private String loan_id;
    private double loannotrepay_sum;
    private double loannotrepay_periods;

    @Override
    public String toString() {
        return "BillEventDetail{" +
                "loan_id='" + loan_id + '\'' +
                ", loannotrepay_sum='" + loannotrepay_sum + '\'' +
                ", loannotrepay_periods='" + loannotrepay_periods + '\'' +
                '}';
    }

    public String getLoan_id() {
        return loan_id;
    }

    public void setLoan_id(String loan_id) {
        this.loan_id = loan_id;
    }

    public double getLoannotrepay_sum() {
        return loannotrepay_sum;
    }

    public void setLoannotrepay_sum(double loannotrepay_sum) {
        this.loannotrepay_sum = loannotrepay_sum;
    }

    public double getLoannotrepay_periods() {
        return loannotrepay_periods;
    }

    public void setLoannotrepay_periods(double loannotrepay_periods) {
        this.loannotrepay_periods = loannotrepay_periods;
    }

    public BillEventDetail(String loan_id, double loannotrepay_sum, double loannotrepay_periods) {
        this.loan_id = loan_id;
        this.loannotrepay_sum = loannotrepay_sum;
        this.loannotrepay_periods = loannotrepay_periods;
    }

    public BillEventDetail() {
    }
}
