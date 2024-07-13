package com.haiercash.gouhua.beans.repayment;

import java.io.Serializable;
import java.util.List;

/**
 * Author: Sun<br/>
 * Date :    2018/1/11<br/>
 * FileName: CashierInfo<br/>
 * Description:<br/>
 */

public class CashierInfo implements Serializable {

    /* **正常还款** */
    private String stayAmount;// 还款总金额
    private String prcpAmt;//还款总本金
    private String normInt;//还款总利息0
    private String psFeeAmt;//还款总手续费
    private String lateFeeAmt;//还款总罚息
    private String lateAmt;//总滞纳金
    private String odIntAmt;//总逾期利息
    private String penaltyAmt;//总违约金

    private String odAmount;//逾期总金额   逾期还款试算返回

    private List<Repayment> list;  //还款参数
    private boolean mutilOverdueNum = false;//是否多笔逾期勾选
    private String coupUseAmt;//优惠券减免金额  使用优惠券还款试算时返回
    private String setlTotalAmtCr;//已还款未入账金额   如做过CR还款再次做FS还款会返回该参数
    private String settleAwardAmt;//结清奖励金额

    public String psFeeAmtNew;//手续费（贷款）
    public String odFeeAmt;//逾期手续费（滞纳金+违约金）
    public String earlyRepayAmt;//提前还款手续费

    public String getCoupUseAmt() {
        return coupUseAmt;
    }

    public void setCoupUseAmt(String coupUseAmt) {
        this.coupUseAmt = coupUseAmt;
    }

    public boolean getMutilOverdueNum() {
        return mutilOverdueNum;
    }

    public void setMutilOverdueNum(boolean mutilOverdueNum) {
        this.mutilOverdueNum = mutilOverdueNum;
    }

    public String getOdAmount() {
        return odAmount;
    }

    public void setOdAmount(String odAmount) {
        this.odAmount = odAmount;
    }

    public String getStayAmount() {
        return stayAmount;
    }

    public void setStayAmount(String stayAmount) {
        this.stayAmount = stayAmount;
    }

    public String getPrcpAmt() {
        return prcpAmt;
    }

    public void setPrcpAmt(String prcpAmt) {
        this.prcpAmt = prcpAmt;
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


    public List<Repayment> getList() {
        return list;
    }

    public void setList(List<Repayment> list) {
        this.list = list;
    }

    public String getLateAmt() {
        return lateAmt;
    }

    public void setLateAmt(String lateAmt) {
        this.lateAmt = lateAmt;
    }

    public String getOdIntAmt() {
        return odIntAmt;
    }

    public void setOdIntAmt(String odIntAmt) {
        this.odIntAmt = odIntAmt;
    }

    public String getPenaltyAmt() {
        return penaltyAmt;
    }

    public void setPenaltyAmt(String penaltyAmt) {
        this.penaltyAmt = penaltyAmt;
    }

    public boolean isMutilOverdueNum() {
        return mutilOverdueNum;
    }

    public String getSetlTotalAmtCr() {
        return setlTotalAmtCr;
    }

    public void setSetlTotalAmtCr(String setlTotalAmtCr) {
        this.setlTotalAmtCr = setlTotalAmtCr;
    }

    public String getSettleAwardAmt() {
        return settleAwardAmt;
    }

    public void setSettleAwardAmt(String settleAwardAmt) {
        this.settleAwardAmt = settleAwardAmt;
    }
}
