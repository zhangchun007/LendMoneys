package com.haiercash.gouhua.beans.repayment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Sun<br/>
 * Date :    2018/1/11<br/>
 * FileName: Repayment<br/>
 * Description:<br/>
 */
public class Repayment implements Serializable {

    private String applSeq;//贷款申请流水号  两者不能同时为空！

    private String loanNo; //借据号
    /**
     * 01：信贷还款   02：充值还款
     */
    private String setlTyp; //还款方式
    /**
     * FS（全部还款） NM（归还欠款）ER（提前还款） 信贷还款时必传
     * NF(当期还款)
     */
    private String setlMode; //还款类型
    private String repayAmt; //还款总金额

    private String psPerdNo; //多个期号以“|”分隔。随借随还传“1”

    private String acCardNo; //还款卡号

    private String acProvince; //开户省

    private String acCity;     //开户市
    private String acBch;     //联行号
    /**
     * Y：使用   N：不使用
     */
    private String useCoup;     //是否使用优惠券
    private String coupNo;     //优惠券代码  使用优惠券时不可空
    private String coupUseAmt;     //优惠券减免金额  使用优惠券时不可空
    private String custNo;     //客户编号
    private String excessMoney;     //溢缴款
    /**
     * A:总额模式  P:本金模式
     */
    private String actvPayInd;     //提前还款类型
    /**
     * 提前还款本金模式时必输
     */
    private String actvPrcp;     //提前还款本金
    private String setlValDt; // 到期日
    private String isNeedPayNo;//是否需要同步还款   微信支付宝还款，返回余额不足状态需上送Y     仅支持信贷还款
    private String repayWay;    //若上送该字段，则isNeedPayNo必传 Y        01支付宝 02微信      2.6.0版本新增字段
    private String expRecCde;   //微信支付必传

    private String setlCurPrcp;//应还本金   CR模式必传
    private String setlCurInt;//应还利息   CR模式必传
    private String setlCurFee;//应还费用   CR模式必传
    private String setlTotalAmtCr;//已还款未入账金额  做过CR还款，再次做FS时必传，客户端可判断相关传输是否为空，为空则不传
    private String setlCurDt;//应还款日期  做过CR还款，再次做FS时必传，客户端可判断相关传输是否为空，为空则不传
    private List<HashMap<String, String>> xfList;   //冲账列表
    private String xfFlag;  //是否需要冲账功能
    private String cfFlag;  //是否使用新接口
    private String contNo;  //合同号

    private String sign;//签名数据

    public String getContNo() {
        return contNo;
    }

    public void setContNo(String contNo) {
        this.contNo = contNo;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSetlValDt() {
        return setlValDt;
    }

    public void setSetlValDt(String setlValDt) {
        this.setlValDt = setlValDt;
    }

    /**
     * 若需要使用指定卡还款则该参数必填
     */
    private String acName; //持卡人姓名

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

    public String getSetlTyp() {
        return setlTyp;
    }

    public void setSetlTyp(String setlTyp) {
        this.setlTyp = setlTyp;
    }

    public String getSetlMode() {
        return setlMode;
    }

    public void setSetlMode(String setlMode) {
        this.setlMode = setlMode;
    }

    public String getRepayAmt() {
        return repayAmt;
    }

    public void setRepayAmt(String repayAmt) {
        this.repayAmt = repayAmt;
    }

    public String getPsPerdNo() {
        return psPerdNo;
    }

    public void setPsPerdNo(String psPerdNo) {
        this.psPerdNo = psPerdNo;
    }

    public String getAcCardNo() {
        return acCardNo;
    }

    public void setAcCardNo(String acCardNo) {
        this.acCardNo = acCardNo;
    }

    public String getAcProvince() {
        return acProvince;
    }

    public void setAcProvince(String acProvince) {
        this.acProvince = acProvince;
    }

    public String getAcCity() {
        return acCity;
    }

    public void setAcCity(String acCity) {
        this.acCity = acCity;
    }

    public String getAcBch() {
        return acBch;
    }

    public void setAcBch(String acBch) {
        this.acBch = acBch;
    }

    public String getUseCoup() {
        return useCoup;
    }

    public void setUseCoup(String useCoup) {
        this.useCoup = useCoup;
    }

    public String getCoupNo() {
        return coupNo;
    }

    public void setCoupNo(String coupNo) {
        this.coupNo = coupNo;
    }

    public String getCoupUseAmt() {
        return coupUseAmt;
    }

    public void setCoupUseAmt(String coupUseAmt) {
        this.coupUseAmt = coupUseAmt;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getExcessMoney() {
        return excessMoney;
    }

    public void setExcessMoney(String excessMoney) {
        this.excessMoney = excessMoney;
    }

    public String getActvPayInd() {
        return actvPayInd;
    }

    public void setActvPayInd(String actvPayInd) {
        this.actvPayInd = actvPayInd;
    }

    public String getActvPrcp() {
        return actvPrcp;
    }

    public void setActvPrcp(String actvPrcp) {
        this.actvPrcp = actvPrcp;
    }

    public String getAcName() {
        return acName;
    }

    public void setAcName(String acName) {
        this.acName = acName;
    }

    public String getIsNeedPayNo() {
        return isNeedPayNo;
    }

    public void setIsNeedPayNo(String isNeedPayNo) {
        this.isNeedPayNo = isNeedPayNo;
    }

    public String getRepayWay() {
        return repayWay;
    }

    public void setRepayWay(String repayWay) {
        this.repayWay = repayWay;
    }

    public String getExpRecCde() {
        return expRecCde;
    }

    public void setExpRecCde(String expRecCde) {
        this.expRecCde = expRecCde;
    }

    public String getSetlCurPrcp() {
        return setlCurPrcp;
    }

    public void setSetlCurPrcp(String setlCurPrcp) {
        this.setlCurPrcp = setlCurPrcp;
    }

    public String getSetlCurInt() {
        return setlCurInt;
    }

    public void setSetlCurInt(String setlCurInt) {
        this.setlCurInt = setlCurInt;
    }

    public String getSetlCurFee() {
        return setlCurFee;
    }

    public void setSetlCurFee(String setlCurFee) {
        this.setlCurFee = setlCurFee;
    }

    public String getSetlTotalAmtCr() {
        return setlTotalAmtCr;
    }

    public void setSetlTotalAmtCr(String setlTotalAmtCr) {
        this.setlTotalAmtCr = setlTotalAmtCr;
    }

    public String getSetlCurDt() {
        return setlCurDt;
    }

    public void setSetlCurDt(String setlCurDt) {
        this.setlCurDt = setlCurDt;
    }

    public List<HashMap<String, String>> getXfList() {
        return xfList;
    }

    public void setXfList(List<HashMap<String, String>> xfList) {
        this.xfList = xfList;
    }

    public String getXfFlag() {
        return xfFlag;
    }

    public void setXfFlag(String xfFlag) {
        this.xfFlag = xfFlag;
    }

    public String getCfFlag() {
        return cfFlag;
    }

    public void setCfFlag(String cfFlag) {
        this.cfFlag = cfFlag;
    }
}
