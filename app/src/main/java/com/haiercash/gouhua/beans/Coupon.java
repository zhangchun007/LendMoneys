package com.haiercash.gouhua.beans;

import java.io.Serializable;

/**
 * 权益列表
 */
public class Coupon implements Serializable {
    private String title;
    private String link;
    private String logo;
    private String num;
    private String couponType;
    private String validDt;
    private String condition;
    private String couponId;


    @Override
    public String toString() {
        return "Coupon{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", logo='" + logo + '\'' +
                ", num=" + num +
                ", couponType='" + couponType + '\'' +
                ", validDt='" + validDt + '\'' +
                ", condition='" + condition + '\'' +
                ", couponId='" + couponId + '\'' +
                '}';
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getValidDt() {
        return validDt;
    }

    public void setValidDt(String validDt) {
        this.validDt = validDt;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Coupon(String title, String link, String logo, String num, String couponType) {
        this.title = title;
        this.link = link;
        this.logo = logo;
        this.num = num;
        this.couponType = couponType;
    }

    public Coupon() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getCouponType() {
        return couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }
}
