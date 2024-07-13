package com.haiercash.gouhua.beans.repayment;

import java.io.Serializable;

public class LmpmshdBean implements Serializable {
    public String psPerdNo;           //期号	String
    public String psDueDt;            //到期日		String
    public String psOdInd;            //逾期标志	        		Y是N否
    public String setlInd;            //结清标志	String		查询收单系统还款状态接口，如果当期有状态，则用订单系统返回的还款状态，       覆盖字段；只要收单系统有，就覆盖    Y:已结清，    N：未结清，            01：还款处理中，            02：还款成功，            03：还款失败
    public String psInstmAmt;         //期供金额		String
    public String psPrcpAmt;          //本金	String
    public String setlPrcp;           //已还本金	String
    public String psRemPrcp;          //剩余本金		String
    public String psNormInt;          //利息		String
    public String setlNormInt;        //已还利息	String
    public String psOdIntAmt;         //罚息		String
    public String setlOdIntAmt;       //已还罚息		String
    public String penalFeeAmt;        //违约金		String
    public String setlPenalFeeAmt;    //已收违约金		String
    public String psCommOdInt;        //复利		String
    public String setlCommOdInt;      //已还复利		String
    public String lateFeeAmt;         //应还滞纳金		String
    public String setlLateFeeAmt;     //已还滞纳金		String
    public String acctFeeAmt;         //应还账户管理费		String
    public String setlAcctFeeAmt;     //已还账户管理费		String
    public String psFeeAmt;           //应还手续费		String
    public String setlFeeAmt;         //已还手续费		String
    public String advanceFeeAmt;      //应还提前还款手续费	String
    public String setlAdvanceFeeAmt;  //已还提前还款手续费	String
    public String psWvNmInt;          //减免利息		String
    public String psWvOdInt;          //减免罚息	String
    public String psWvCommInt;        //减免复利		String
    public boolean isOpened;

    public String applyDt;
    public String psFeeAmtNew;  //手续费（应还手续费）
    public String odFeeAmt;  //逾期手续费
    public String setlOdFeeAmt;  //已收逾期手续费
    public String setlFeeAmtNew;  //已还手续费

    private String discValue;  //优惠金额

    public String getPsFeeAmtNew() {
        return psFeeAmtNew;
    }

    public void setPsFeeAmtNew(String psFeeAmtNew) {
        this.psFeeAmtNew = psFeeAmtNew;
    }

    public String getOdFeeAmt() {
        return odFeeAmt;
    }

    public void setOdFeeAmt(String odFeeAmt) {
        this.odFeeAmt = odFeeAmt;
    }

    public String getSetlOdFeeAmt() {
        return setlOdFeeAmt;
    }

    public void setSetlOdFeeAmt(String setlOdFeeAmt) {
        this.setlOdFeeAmt = setlOdFeeAmt;
    }

    public String getSetlFeeAmtNew() {
        return setlFeeAmtNew;
    }

    public void setSetlFeeAmtNew(String setlFeeAmtNew) {
        this.setlFeeAmtNew = setlFeeAmtNew;
    }

    public String getDiscValue() {
        return discValue;
    }

    public void setDiscValue(String discValue) {
        this.discValue = discValue;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    private String couponNo;  //券号

    public String getApplyDt() {
        return applyDt;
    }

    public void setApplyDt(String applyDt) {
        this.applyDt = applyDt;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public String getPsWvOdInt() {
        return psWvOdInt;
    }

    public void setPsWvOdInt(String psWvOdInt) {
        this.psWvOdInt = psWvOdInt;
    }

    public String getSetlPenalFeeAmt() {
        return setlPenalFeeAmt;
    }

    public void setSetlPenalFeeAmt(String setlPenalFeeAmt) {
        this.setlPenalFeeAmt = setlPenalFeeAmt;
    }

    public String getSetlLateFeeAmt() {
        return setlLateFeeAmt;
    }

    public void setSetlLateFeeAmt(String setlLateFeeAmt) {
        this.setlLateFeeAmt = setlLateFeeAmt;
    }

    public String getPsDueDt() {
        return psDueDt;
    }

    public void setPsDueDt(String psDueDt) {
        this.psDueDt = psDueDt;
    }

    public String getPsCommOdInt() {
        return psCommOdInt;
    }

    public void setPsCommOdInt(String psCommOdInt) {
        this.psCommOdInt = psCommOdInt;
    }

    public String getAdvanceFeeAmt() {
        return advanceFeeAmt;
    }

    public void setAdvanceFeeAmt(String advanceFeeAmt) {
        this.advanceFeeAmt = advanceFeeAmt;
    }

    public String getLateFeeAmt() {
        return lateFeeAmt;
    }

    public void setLateFeeAmt(String lateFeeAmt) {
        this.lateFeeAmt = lateFeeAmt;
    }

    public String getPsInstmAmt() {
        return psInstmAmt;
    }

    public void setPsInstmAmt(String psInstmAmt) {
        this.psInstmAmt = psInstmAmt;
    }

    public String getPsOdIntAmt() {
        return psOdIntAmt;
    }

    public void setPsOdIntAmt(String psOdIntAmt) {
        this.psOdIntAmt = psOdIntAmt;
    }

    public String getPsPerdNo() {
        return psPerdNo;
    }

    public void setPsPerdNo(String psPerdNo) {
        this.psPerdNo = psPerdNo;
    }

    public String getSetlFeeAmt() {
        return setlFeeAmt;
    }

    public void setSetlFeeAmt(String setlFeeAmt) {
        this.setlFeeAmt = setlFeeAmt;
    }

    public String getPenalFeeAmt() {
        return penalFeeAmt;
    }

    public void setPenalFeeAmt(String penalFeeAmt) {
        this.penalFeeAmt = penalFeeAmt;
    }

    public String getSetlOdIntAmt() {
        return setlOdIntAmt;
    }

    public void setSetlOdIntAmt(String setlOdIntAmt) {
        this.setlOdIntAmt = setlOdIntAmt;
    }

    public String getPsWvCommInt() {
        return psWvCommInt;
    }

    public void setPsWvCommInt(String psWvCommInt) {
        this.psWvCommInt = psWvCommInt;
    }

    public String getSetlAdvanceFeeAmt() {
        return setlAdvanceFeeAmt;
    }

    public void setSetlAdvanceFeeAmt(String setlAdvanceFeeAmt) {
        this.setlAdvanceFeeAmt = setlAdvanceFeeAmt;
    }

    public String getSetlPrcp() {
        return setlPrcp;
    }

    public void setSetlPrcp(String setlPrcp) {
        this.setlPrcp = setlPrcp;
    }

    public String getPsWvNmInt() {
        return psWvNmInt;
    }

    public void setPsWvNmInt(String psWvNmInt) {
        this.psWvNmInt = psWvNmInt;
    }

    public String getPsFeeAmt() {
        return psFeeAmt;
    }

    public void setPsFeeAmt(String psFeeAmt) {
        this.psFeeAmt = psFeeAmt;
    }

    public String getSetlNormInt() {
        return setlNormInt;
    }

    public void setSetlNormInt(String setlNormInt) {
        this.setlNormInt = setlNormInt;
    }

    public String getSetlCommOdInt() {
        return setlCommOdInt;
    }

    public void setSetlCommOdInt(String setlCommOdInt) {
        this.setlCommOdInt = setlCommOdInt;
    }

    public String getSetlInd() {
        return setlInd;
    }

    public void setSetlInd(String setlInd) {
        this.setlInd = setlInd;
    }

    public String getPsPrcpAmt() {
        return psPrcpAmt;
    }

    public void setPsPrcpAmt(String psPrcpAmt) {
        this.psPrcpAmt = psPrcpAmt;
    }

    public String getPsRemPrcp() {
        return psRemPrcp;
    }

    public void setPsRemPrcp(String psRemPrcp) {
        this.psRemPrcp = psRemPrcp;
    }

    public String getPsNormInt() {
        return psNormInt;
    }

    public void setPsNormInt(String psNormInt) {
        this.psNormInt = psNormInt;
    }

    public String getAcctFeeAmt() {
        return acctFeeAmt;
    }

    public void setAcctFeeAmt(String acctFeeAmt) {
        this.acctFeeAmt = acctFeeAmt;
    }

    public String getSetlAcctFeeAmt() {
        return setlAcctFeeAmt;
    }

    public void setSetlAcctFeeAmt(String setlAcctFeeAmt) {
        this.setlAcctFeeAmt = setlAcctFeeAmt;
    }

    public String getPsOdInd() {
        return psOdInd;
    }

    public void setPsOdInd(String psOdInd) {
        this.psOdInd = psOdInd;
    }
}
