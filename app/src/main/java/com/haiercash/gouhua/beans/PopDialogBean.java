package com.haiercash.gouhua.beans;

import java.util.List;

/**
 * 权益弹窗实体
 */
public class PopDialogBean {
    private String popUpsName;
    private String popUpsCode;
    private String popUpsType;
    private String jumpUrl;
    private String showImageUrl;
    private String topImageUrl;
    private String backgroundColor;
    private String buttonColor;
    private String bottomText;
    private String noticeId;
    private List<Coupon> couponList;

    @Override
    public String toString() {
        return "PopDialogBean{" +
                "popUpsName='" + popUpsName + '\'' +
                ", popUpsCode='" + popUpsCode + '\'' +
                ", popUpsType='" + popUpsType + '\'' +
                ", jumpUrl='" + jumpUrl + '\'' +
                ", showImageUrl='" + showImageUrl + '\'' +
                ", topImageUrl='" + topImageUrl + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", buttonColor='" + buttonColor + '\'' +
                ", bottomText='" + bottomText + '\'' +
                ", noticeId='" + noticeId + '\'' +
                ", couponList=" + couponList +
                '}';
    }

    public String getPopUpsName() {
        return popUpsName;
    }

    public void setPopUpsName(String popUpsName) {
        this.popUpsName = popUpsName;
    }

    public String getPopUpsCode() {
        return popUpsCode;
    }

    public void setPopUpsCode(String popUpsCode) {
        this.popUpsCode = popUpsCode;
    }

    public String getPopUpsType() {
        return popUpsType;
    }

    public void setPopUpsType(String popUpsType) {
        this.popUpsType = popUpsType;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getShowImageUrl() {
        return showImageUrl;
    }

    public void setShowImageUrl(String showImageUrl) {
        this.showImageUrl = showImageUrl;
    }

    public String getTopImageUrl() {
        return topImageUrl;
    }

    public void setTopImageUrl(String topImageUrl) {
        this.topImageUrl = topImageUrl;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(String buttonColor) {
        this.buttonColor = buttonColor;
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public List<Coupon> getCouponList() {
        return couponList;
    }

    public void setCouponList(List<Coupon> couponList) {
        this.couponList = couponList;
    }

    public PopDialogBean(String popUpsName, String popUpsCode, String popUpsType, String jumpUrl, String showImageUrl, String topImageUrl, String backgroundColor, String buttonColor, String bottomText, String noticeId, List<Coupon> couponList) {
        this.popUpsName = popUpsName;
        this.popUpsCode = popUpsCode;
        this.popUpsType = popUpsType;
        this.jumpUrl = jumpUrl;
        this.showImageUrl = showImageUrl;
        this.topImageUrl = topImageUrl;
        this.backgroundColor = backgroundColor;
        this.buttonColor = buttonColor;
        this.bottomText = bottomText;
        this.noticeId = noticeId;
        this.couponList = couponList;
    }

    public PopDialogBean() {
    }
}
