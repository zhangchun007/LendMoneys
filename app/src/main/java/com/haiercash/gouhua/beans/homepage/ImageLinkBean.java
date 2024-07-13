package com.haiercash.gouhua.beans.homepage;

public class ImageLinkBean {

    private String imgUrl;
    private String forwardUrl;
    private String cid;//活动id，埋点需要
    private String groupId;//客群id，埋点需要
    private String jumpType;//跳转方式 page： 页面, pup ： 半弹窗
    private String bannerName;
    private String bannerCode;
    private String themeCode;
    private String themeName;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }

    public String getThemeCode() {
        return themeCode;
    }

    public void setThemeCode(String themeCode) {
        this.themeCode = themeCode;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    @Override
    public String toString() {
        return "ImageLinkBean{" +
                "imgUrl='" + imgUrl + '\'' +
                ", forwardUrl='" + forwardUrl + '\'' +
                ", bannerName='" + bannerName + '\'' +
                ", bannerCode='" + bannerCode + '\'' +
                '}';
    }

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public String getBannerCode() {
        return bannerCode;
    }

    public void setBannerCode(String bannerCode) {
        this.bannerCode = bannerCode;
    }

    public ImageLinkBean(String imgUrl, String forwardUrl, String bannerName, String bannerCode) {
        this.imgUrl = imgUrl;
        this.forwardUrl = forwardUrl;
        this.bannerName = bannerName;
        this.bannerCode = bannerCode;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setForwardUrl(String forwardUrl) {
        this.forwardUrl = forwardUrl;
    }

    public String getForwardUrl() {
        return forwardUrl;
    }

}
