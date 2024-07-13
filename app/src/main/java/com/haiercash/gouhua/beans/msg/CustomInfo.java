package com.haiercash.gouhua.beans.msg;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/6/27<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class CustomInfo {
    private String bizType;
    private String eventId;
    private String jumpType;
    private String jumpKey;
    private String imgUrl;
    private String msgStartTime;
    private String msgEndTime;
    private String startTime;
    private String endTime;
    private String showTitle;
    private String eventType;//simple 简易模式（自定义内容模式）    oldType 旧版本模式（活动链接模式）
    private String detailMessage; //自定义内容详情

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }

    public String getJumpKey() {
        return jumpKey;
    }

    public void setJumpKey(String jumpKey) {
        this.jumpKey = jumpKey;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMsgStartTime() {
        return msgStartTime;
    }

    public void setMsgStartTime(String msgStartTime) {
        this.msgStartTime = msgStartTime;
    }

    public String getMsgEndTime() {
        return msgEndTime;
    }

    public void setMsgEndTime(String msgEndTime) {
        this.msgEndTime = msgEndTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }
}
