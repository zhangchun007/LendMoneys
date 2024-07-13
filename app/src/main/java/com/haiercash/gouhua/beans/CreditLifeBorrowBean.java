package com.haiercash.gouhua.beans;

import java.io.Serializable;

public class CreditLifeBorrowBean implements Serializable {

    /**
     * channelIntro : 银行系产品，更安全，除日息外无任何费用
     * channelName : 小额贷款王
     * created : 2019-01-04 15:19:21
     * eduAmt : 500-200，000元
     * eduDeniedCauseCodes : cdCheat,cdCondition,cdScore
     * id : 99d3a044ee884542be33c6ef69fe709f
     * imageAddress : /testshare01/app/img/recommendChannel/d6b7fd0d842041b79a2e7376ec6eb907.png
     * isBlacklist : 0
     * isHot : N
     * isUniteLogin : Y
     * jumpLink : http://10.164.194.121/static/gh/yznbdys.html?source=
     * orderBy : 5
     * rateInterest : 0.83%
     * status : 1
     * timeLimit : 12月-18月
     * updated : 2019-01-25 14:47:42
     */

    private String channelIntro;//渠道介绍
    private String channelName;// 渠道名称
    private String userId;
    private String created;
    private String eduAmt;// 额度金额
    private String eduDeniedCauseCodes;
    private String productId;//产品id
    private String id;
    private String applyDate;
    private String imageAddress;//图片地址
    private int isBlacklist;
    private String isHot;//是否热门推荐0：否，1：是
    private String isUniteLogin;//是否联合登录 N：否，Y：是
    private String jumpLink;//跳转地址
    private String orderBy;
    private String rateInterest;//利率
    private String status;
    private String timeLimit;
    private String updated;
    private String rateCost;//费率
    private String applyCount;//申请人数
    private String loanDate;//放款时间
    private String imageAddressBig;//大图片地址
    private String hotOrder;//热门推荐排序
    private String loanTerms;//贷款条件
    private String applyData;//申请资料
    private String others;//其他
    private String channelType;//渠道类型
    private String effectiveTime;
    private String grayChannel;//灰度渠道名称
    private String isGray;//是否是灰度渠道

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getGrayChannel() {
        return grayChannel;
    }

    public void setGrayChannel(String grayChannel) {
        this.grayChannel = grayChannel;
    }

    public String getIsGray() {
        return isGray;
    }

    public void setIsGray(String isGray) {
        this.isGray = isGray;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getRateCost() {
        return rateCost;
    }

    public void setRateCost(String rateCost) {
        this.rateCost = rateCost;
    }

    public String getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(String applyCount) {
        this.applyCount = applyCount;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    public String getImageAddressBig() {
        return imageAddressBig;
    }

    public void setImageAddressBig(String imageAddressBig) {
        this.imageAddressBig = imageAddressBig;
    }

    public String getHotOrder() {
        return hotOrder;
    }

    public void setHotOrder(String hotOrder) {
        this.hotOrder = hotOrder;
    }

    public String getLoanTerms() {
        return loanTerms;
    }

    public void setLoanTerms(String loanTerms) {
        this.loanTerms = loanTerms;
    }

    public String getApplyData() {
        return applyData;
    }

    public void setApplyData(String applyData) {
        this.applyData = applyData;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getChannelIntro() {
        return channelIntro;
    }

    public void setChannelIntro(String channelIntro) {
        this.channelIntro = channelIntro;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }


    public String getEduAmt() {
        return eduAmt;
    }

    public void setEduAmt(String eduAmt) {
        this.eduAmt = eduAmt;
    }

    public String getEduDeniedCauseCodes() {
        return eduDeniedCauseCodes;
    }

    public void setEduDeniedCauseCodes(String eduDeniedCauseCodes) {
        this.eduDeniedCauseCodes = eduDeniedCauseCodes;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
    }

    public int getIsBlacklist() {
        return isBlacklist;
    }

    public void setIsBlacklist(int isBlacklist) {
        this.isBlacklist = isBlacklist;
    }

    public String getIsHot() {
        return isHot;
    }

    public void setIsHot(String isHot) {
        this.isHot = isHot;
    }

    public String getIsUniteLogin() {
        return isUniteLogin;
    }

    public void setIsUniteLogin(String isUniteLogin) {
        this.isUniteLogin = isUniteLogin;
    }

    public String getJumpLink() {
        return jumpLink;
    }

    public void setJumpLink(String jumpLink) {
        this.jumpLink = jumpLink;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getRateInterest() {
        return rateInterest;
    }

    public void setRateInterest(String rateInterest) {
        this.rateInterest = rateInterest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
