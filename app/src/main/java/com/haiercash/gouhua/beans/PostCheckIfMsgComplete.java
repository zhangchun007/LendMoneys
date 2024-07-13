package com.haiercash.gouhua.beans;

/**
 * Created by use on 2016/11/22.
 * 标签	tag
 * 业务类型	businessType
 * 是否为订单	isOrder
 * 订单流水号	appseq
 * 用户id	userId
 * 用户编号	custNo
 * 贷款品种代码	typCde
 */

public class PostCheckIfMsgComplete {
    public String channel;
    public String tag;
    public String businessType;
    public String isOrder;
    public String applSeq;
    public String userId;
    public String custNo;
    public String typCde;
    public String custName;
    public String idNo;
    public String orderNo;
    public String isRsa;
    public String noEduLocal;

    public String getNoEduLocal() {
        return noEduLocal;
    }

    public void setNoEduLocal(String noEduLocal) {
        this.noEduLocal = noEduLocal;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getApplSeq() {
        return applSeq;
    }

    public void setApplSeq(String applSeq) {
        this.applSeq = applSeq;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(String isOrder) {
        this.isOrder = isOrder;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getTypCde() {
        return typCde;
    }

    public void setTypCde(String typCde) {
        this.typCde = typCde;
    }

    public String getIsRsa() {
        return isRsa;
    }

    public void setIsRsa(String isRsa) {
        this.isRsa = isRsa;
    }
}
