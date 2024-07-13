package com.haiercash.gouhua.beans;

import java.io.Serializable;
import java.util.List;

public class OpenDischargeBean implements Serializable {
    private String hasOdLoan;//是否存在逾期 Y 是 N 否
    private String odLoanMsg;//逾期展示文案
    private String hasSettlementLoan;//是否有结清借据  Y 是 N 否
    private String settlementLoanMsg;//无结清借据展示文案
    private String hasEmail;//个人资料是否有邮箱  Y 是 N 否
    private String noEmailMsg;//无邮箱时展示文案
    private List<LoansBean> loans;

    public String getHasEmail() {
        return hasEmail;
    }

    public void setHasEmail(String hasEmail) {
        this.hasEmail = hasEmail;
    }

    public String getNoEmailMsg() {
        return noEmailMsg;
    }

    public void setNoEmailMsg(String noEmailMsg) {
        this.noEmailMsg = noEmailMsg;
    }

    public String getHasOdLoan() {
        return hasOdLoan;
    }

    public void setHasOdLoan(String hasOdLoan) {
        this.hasOdLoan = hasOdLoan;
    }

    public String getOdLoanMsg() {
        return odLoanMsg;
    }

    public void setOdLoanMsg(String odLoanMsg) {
        this.odLoanMsg = odLoanMsg;
    }

    public String getHasSettlementLoan() {
        return hasSettlementLoan;
    }

    public void setHasSettlementLoan(String hasSettlementLoan) {
        this.hasSettlementLoan = hasSettlementLoan;
    }

    public String getSettlementLoanMsg() {
        return settlementLoanMsg;
    }

    public void setSettlementLoanMsg(String settlementLoanMsg) {
        this.settlementLoanMsg = settlementLoanMsg;
    }

    public List<LoansBean> getLoans() {
        return loans;
    }

    public void setLoans(List<LoansBean> loans) {
        this.loans = loans;
    }

    public static class LoansBean implements Serializable {
        private String applSeq;//申请流水号
        private String loanNo;//借据号
        private String loanContNo;//贷款合同号
        private String custId;//客户编号
        private String custName;//借款人姓名

        private String origPrcp;//贷款金额
        private String apprvTnr;//贷款期数
        private String loanActvDt;//放款日期
        private String lastDueDt;//贷款到期日
        private String lastSetlDt;//贷款结清日期
        private String loanMode;//是否联合放款
        private String dueDay;//还款日
        private String loanIntRate;//贷款执行利率
        private String loanOdIntRate;//罚息执行利率

        private boolean selected = false;

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getApplSeq() {
            return applSeq;
        }

        public void setApplSeq(String applSeq) {
            this.applSeq = applSeq;
        }

        public String getLoanNo() {
            return loanNo;
        }

        public void setLoanNo(String loanNo) {
            this.loanNo = loanNo;
        }

        public String getLoanContNo() {
            return loanContNo;
        }

        public void setLoanContNo(String loanContNo) {
            this.loanContNo = loanContNo;
        }

        public String getCustId() {
            return custId;
        }

        public void setCustId(String custId) {
            this.custId = custId;
        }

        public String getCustName() {
            return custName;
        }

        public void setCustName(String custName) {
            this.custName = custName;
        }

        public String getOrigPrcp() {
            return origPrcp;
        }

        public void setOrigPrcp(String origPrcp) {
            this.origPrcp = origPrcp;
        }

        public String getApprvTnr() {
            return apprvTnr;
        }

        public void setApprvTnr(String apprvTnr) {
            this.apprvTnr = apprvTnr;
        }

        public String getLoanActvDt() {
            return loanActvDt;
        }

        public void setLoanActvDt(String loanActvDt) {
            this.loanActvDt = loanActvDt;
        }

        public String getLastDueDt() {
            return lastDueDt;
        }

        public void setLastDueDt(String lastDueDt) {
            this.lastDueDt = lastDueDt;
        }

        public String getLastSetlDt() {
            return lastSetlDt;
        }

        public void setLastSetlDt(String lastSetlDt) {
            this.lastSetlDt = lastSetlDt;
        }

        public String getLoanMode() {
            return loanMode;
        }

        public void setLoanMode(String loanMode) {
            this.loanMode = loanMode;
        }

        public String getDueDay() {
            return dueDay;
        }

        public void setDueDay(String dueDay) {
            this.dueDay = dueDay;
        }

        public String getLoanIntRate() {
            return loanIntRate;
        }

        public void setLoanIntRate(String loanIntRate) {
            this.loanIntRate = loanIntRate;
        }

        public String getLoanOdIntRate() {
            return loanOdIntRate;
        }

        public void setLoanOdIntRate(String loanOdIntRate) {
            this.loanOdIntRate = loanOdIntRate;
        }
    }
}
