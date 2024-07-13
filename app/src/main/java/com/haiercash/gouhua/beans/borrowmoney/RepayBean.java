package com.haiercash.gouhua.beans.borrowmoney;

import java.io.Serializable;

/**
 * 还款记录
 */
public class RepayBean implements Serializable {

    private String certNo;                 //证件号码       String
    private String custName;                //客户姓名       String
    private String typeCode;                //业务类型编码     String            crm业务编码
    private String typeAbstract;            //业务类型简述     String    通过crm中菜单“还款类型与展示信息配置”配置
    private String typeDesc;                //业务类型描述     String    通过crm中菜单“还款类型与展示信息配置”配置
    private String businessPayNo;           //业务流水号      String
    private String amount;                 //金额         String    金额返回的数据带有两位小数,不足的部分0补位.如果返回的金额不是数字会返回错误信息;如果返回金额<0.005,返回0.00,如果返回金额>=0.005,返回0.01
    private String status;                 //交易状态       String    还款交易是否成功通过交易状态进行判断：("PROCESSING", "处理中"),("FAIL", "失败"),("SUCCESS", "成功");
    private String statusMsg;               //交易状态描述     String            交易状态描述
    private String errorCode;               //错误码        String    错误码、错误描述为当前还款记录的交易错误信息，还款失败时为银行返回失败原因。
    private String errorDesc;               //错误描述       String
    private String tradeTime;               //交易时间       String    格式：2017-06-29 18:46:43
    private String repayFinalStatusTime;               //还款成功/失败时间      String    格式：2017-06-29 18:46:43
    private String repayCardNo;//还款卡号
    private String serno;//还款流水号
    private String bizType;//还款类型   系统代扣 01 主动还款 02  其他 03
    private String bankName;//银行名称
    private String interId;//还款渠道  扣款渠道（区分微信、支付宝、银行卡支付）    非微信，支付宝渠道可以认定为银行卡支付
    //    微信: WECHAT_C_A_01    支付宝: ALI_C_A_01    宝付易宝：BAOFOO_C_A_01

    public String getRepayCardNo() {
        return repayCardNo;
    }

    public void setRepayCardNo(String repayCardNo) {
        this.repayCardNo = repayCardNo;
    }

    public String getSerno() {
        return serno;
    }

    public void setSerno(String serno) {
        this.serno = serno;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getRepayFinalStatusTime() {
        return repayFinalStatusTime;
    }

    public void setRepayFinalStatusTime(String repayFinalStatusTime) {
        this.repayFinalStatusTime = repayFinalStatusTime;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeAbstract() {
        return typeAbstract;
    }

    public void setTypeAbstract(String typeAbstract) {
        this.typeAbstract = typeAbstract;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getBusinessPayNo() {
        return businessPayNo;
    }

    public void setBusinessPayNo(String businessPayNo) {
        this.businessPayNo = businessPayNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getInterId() {
        return interId;
    }

    public void setInterId(String interId) {
        this.interId = interId;
    }
}
