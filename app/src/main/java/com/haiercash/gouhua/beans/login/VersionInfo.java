package com.haiercash.gouhua.beans.login;

/**
 * Created by use on 2016/6/17.
 * <p>
 * 最新版本号	lastVersion
 * 是否强制更新	isForcedUpdate
 * 是否有更新版本	hasNewer
 * 开始时间	beginTime
 * 结束时间	endTime
 * 是否是在维护 isFix
 */
public class VersionInfo {
    private String  lastVersion;      //最新版本号             String
    private boolean isForcedUpdate;   //是否强制更新           boolean		true-是（最新版需要强制更新或现有版本已停用） false-否
    private boolean hasNewer;         //是否有更新版本         boolean		true-是 false-否
    private String  updateAddress;    //更新地址              String	否	仅ios返回
    private String  beginTime;        //开始时间              String
    private String  endTime;          //结束时间              String
    private String  isFix;            //是否系统维护           String		Y –是 N –否
    private String  xcd;              //星巢贷用户服务开启开关  String 否	Y –是 N –否    备注：只有星巢贷订单才会返回此字段
    private String  gh;               //够花服务开启开关       String	否	Y –是 N –否
    private String  hasNotice;        //是否有版本升级公告     String	是	Y - 是 N – 否
    private String  titleImage;       //公告标题图片地址       String	否	hasNotice为Y时返回
    private String  noticeTitle;      //版本升级公告标题       String	否	hasNotice为Y时返回
    private String  noticeContent;    //版本升级公告内容       String	否	hasNotice为Y时返回

    public String getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    public boolean isForcedUpdate() {
        return isForcedUpdate;
    }

    public void setForcedUpdate(boolean forcedUpdate) {
        isForcedUpdate = forcedUpdate;
    }

    public boolean isHasNewer() {
        return hasNewer;
    }

    public void setHasNewer(boolean hasNewer) {
        this.hasNewer = hasNewer;
    }

    public String getUpdateAddress() {
        return updateAddress;
    }

    public void setUpdateAddress(String updateAddress) {
        this.updateAddress = updateAddress;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getIsFix() {
        return isFix;
    }

    public void setIsFix(String isFix) {
        this.isFix = isFix;
    }

    public String getXcd() {
        return xcd;
    }

    public void setXcd(String xcd) {
        this.xcd = xcd;
    }

    public String getGh() {
        return gh;
    }

    public void setGh(String gh) {
        this.gh = gh;
    }

    public String getHasNotice() {
        return hasNotice;
    }

    public void setHasNotice(String hasNotice) {
        this.hasNotice = hasNotice;
    }

    public String getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(String titleImage) {
        this.titleImage = titleImage;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }
}
