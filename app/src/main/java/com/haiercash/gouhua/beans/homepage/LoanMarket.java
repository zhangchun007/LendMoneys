package com.haiercash.gouhua.beans.homepage;

import java.io.Serializable;

/**
 * 贷超信息
 */
public class LoanMarket implements Serializable {
    private String id;//数据库主键String
    private String type;//类型String
    private String orderBy;//排序String
    private String status;//是否禁用String
    private String effectiveTime;//生效时间Stringyyyy-MM-ddHH:mm:ss
    private String invalidTime;//失效时间Stringyyyy-MM-ddHH:mm:ss
    private String imageAddress;//图片地址String
    private String jumpLink;//跳转链接String
    private String channelName;//渠道名String
    private String channelIntro;//渠道简介String
    private String eduAmt;//额度String
    private String rateInterest;//利率String
    private String timeLimit;//期限范围String
    private String applyCount;//申请人数String
    private int isBlacklist;//是否支持黑名单int1为全量支持黑名单
    private String eduDeniedCauseCodes;//额度拒绝码值String
    private String isUniteLogin;//是否联合登陆StringY是N否
    private String loanDate;//放款时间String
    private String imageAddressBig;//大图片String

    private String isHot;//是否热门推荐StringY是N否
    private String hotOrder;//热门推荐排序String
    private String loanTerms;//贷款条件String
    private String applyData;//申请资料String
    private String others;// 其他String
    private String channelType;//渠道类型String
    private String isGray;//是否灰度String
    private String grayChannel;//灰度渠道

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(String invalidTime) {
        this.invalidTime = invalidTime;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
    }

    public String getJumpLink() {
        return jumpLink;
    }

    public void setJumpLink(String jumpLink) {
        this.jumpLink = jumpLink;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelIntro() {
        return channelIntro;
    }

    public void setChannelIntro(String channelIntro) {
        this.channelIntro = channelIntro;
    }

    public String getEduAmt() {
        return eduAmt;
    }

    public void setEduAmt(String eduAmt) {
        this.eduAmt = eduAmt;
    }

    public String getRateInterest() {
        return rateInterest;
    }

    public void setRateInterest(String rateInterest) {
        this.rateInterest = rateInterest;
    }

    public String getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(String timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(String applyCount) {
        this.applyCount = applyCount;
    }

    public int getIsBlacklist() {
        return isBlacklist;
    }

    public void setIsBlacklist(int isBlacklist) {
        this.isBlacklist = isBlacklist;
    }

    public String getEduDeniedCauseCodes() {
        return eduDeniedCauseCodes;
    }

    public void setEduDeniedCauseCodes(String eduDeniedCauseCodes) {
        this.eduDeniedCauseCodes = eduDeniedCauseCodes;
    }

    public String getIsUniteLogin() {
        return isUniteLogin;
    }

    public void setIsUniteLogin(String isUniteLogin) {
        this.isUniteLogin = isUniteLogin;
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

    public String getIsHot() {
        return isHot;
    }

    public void setIsHot(String isHot) {
        this.isHot = isHot;
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

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getIsGray() {
        return isGray;
    }

    public void setIsGray(String isGray) {
        this.isGray = isGray;
    }

    public String getGrayChannel() {
        return grayChannel;
    }

    public void setGrayChannel(String grayChannel) {
        this.grayChannel = grayChannel;
    }

    public LoanMarket(String id, String type, String orderBy, String status, String effectiveTime, String invalidTime, String imageAddress, String jumpLink, String channelName, String channelIntro, String eduAmt, String rateInterest, String timeLimit, String applyCount, int isBlacklist, String eduDeniedCauseCodes, String isUniteLogin, String loanDate, String imageAddressBig, String isHot, String hotOrder, String loanTerms, String applyData, String others, String channelType, String isGray, String grayChannel) {
        this.id = id;
        this.type = type;
        this.orderBy = orderBy;
        this.status = status;
        this.effectiveTime = effectiveTime;
        this.invalidTime = invalidTime;
        this.imageAddress = imageAddress;
        this.jumpLink = jumpLink;
        this.channelName = channelName;
        this.channelIntro = channelIntro;
        this.eduAmt = eduAmt;
        this.rateInterest = rateInterest;
        this.timeLimit = timeLimit;
        this.applyCount = applyCount;
        this.isBlacklist = isBlacklist;
        this.eduDeniedCauseCodes = eduDeniedCauseCodes;
        this.isUniteLogin = isUniteLogin;
        this.loanDate = loanDate;
        this.imageAddressBig = imageAddressBig;
        this.isHot = isHot;
        this.hotOrder = hotOrder;
        this.loanTerms = loanTerms;
        this.applyData = applyData;
        this.others = others;
        this.channelType = channelType;
        this.isGray = isGray;
        this.grayChannel = grayChannel;
    }

    public LoanMarket() {
    }
}
