package com.haiercash.gouhua.beans;

public class MyLoanBean {
    private MyLoanDetailBean loanAll;
    private MyLoanDetailBean loanSubmit;
    private MyLoanDetailBean loanApproval;
    private MyLoanDetailBean loanRepay;
    private MyLoanDetailBean order;

    @Override
    public String toString() {
        return "MyLoanBean{" +
                "loanAll=" + loanAll +
                ", loanSubmit=" + loanSubmit +
                ", loanApproval=" + loanApproval +
                ", loanRepay=" + loanRepay +
                ", order=" + order +
                '}';
    }

    public MyLoanDetailBean getLoanAll() {
        return loanAll;
    }

    public void setLoanAll(MyLoanDetailBean loanAll) {
        this.loanAll = loanAll;
    }

    public MyLoanDetailBean getLoanSubmit() {
        return loanSubmit;
    }

    public void setLoanSubmit(MyLoanDetailBean loanSubmit) {
        this.loanSubmit = loanSubmit;
    }

    public MyLoanDetailBean getLoanApproval() {
        return loanApproval;
    }

    public void setLoanApproval(MyLoanDetailBean loanApproval) {
        this.loanApproval = loanApproval;
    }

    public MyLoanDetailBean getLoanRepay() {
        return loanRepay;
    }

    public void setLoanRepay(MyLoanDetailBean loanRepay) {
        this.loanRepay = loanRepay;
    }

    public MyLoanDetailBean getOrder() {
        return order;
    }

    public void setOrder(MyLoanDetailBean order) {
        this.order = order;
    }

    public MyLoanBean(MyLoanDetailBean loaAll, MyLoanDetailBean loanSubmit, MyLoanDetailBean loanApproval, MyLoanDetailBean loanRepay, MyLoanDetailBean order) {
        this.loanAll = loanAll;
        this.loanSubmit = loanSubmit;
        this.loanApproval = loanApproval;
        this.loanRepay = loanRepay;
        this.order = order;
    }

    public MyLoanBean() {
    }
}
