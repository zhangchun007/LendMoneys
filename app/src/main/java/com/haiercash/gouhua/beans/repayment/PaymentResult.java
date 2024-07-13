package com.haiercash.gouhua.beans.repayment;

import java.io.Serializable;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/1/4<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class PaymentResult implements Serializable {
    private String loanNo;//借据号
    private String applSeq;//贷款申请流水号
    //01：还款处理中
    //02：还款成功
    //03：还款失败
    private String setlSts;//还款状态
    private String repaySts;//微信还款状态
    private String failReason;//还款失败原因
    private String failDesc;//微信还款失败原因
    //00：处理成功
    //01：serno校验失败
    //02:必输项及业务校验失败
    //03:申请流水查询是啊比
    //04：存在溢缴款
    private String excessCode;//溢缴款标志位
    //还款主键    微信支付使用
    private String repaySeq;
    //收单还款流水号
    private String repayFormExceedingSeq;
//    授权号
    private String prepayOrderId;
    //微信支付扩展参数
    private Map<String, Object> wxPayExtMap;

    public String getRepaySts() {
        return repaySts;
    }

    public void setRepaySts(String repaySts) {
        this.repaySts = repaySts;
    }

    public String getFailDesc() {
        return failDesc;
    }

    public void setFailDesc(String failDesc) {
        this.failDesc = failDesc;
    }

    public String getRepayFormExceedingSeq() {
        return repayFormExceedingSeq;
    }

    public void setRepayFormExceedingSeq(String repayFormExceedingSeq) {
        this.repayFormExceedingSeq = repayFormExceedingSeq;
    }

    public String getPrepayOrderId() {
        return prepayOrderId;
    }

    public void setPrepayOrderId(String prepayOrderId) {
        this.prepayOrderId = prepayOrderId;
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

    public String getSetlSts() {
        return setlSts;
    }

    public void setSetlSts(String setlSts) {
        this.setlSts = setlSts;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getExcessCode() {
        return excessCode;
    }

    public void setExcessCode(String excessCode) {
        this.excessCode = excessCode;
    }

    public Map<String, Object> getWxPayExtMap() {
        return wxPayExtMap;
    }

    public void setWxPayExtMap(Map<String, Object> wxPayExtMap) {
        this.wxPayExtMap = wxPayExtMap;
    }

    public String getRepaySeq() {
        return repaySeq;
    }

    public void setRepaySeq(String repaySeq) {
        this.repaySeq = repaySeq;
    }
}
