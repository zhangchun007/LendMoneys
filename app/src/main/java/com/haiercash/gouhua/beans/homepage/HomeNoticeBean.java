package com.haiercash.gouhua.beans.homepage;

import java.io.Serializable;

public class HomeNoticeBean implements Serializable {
    private String id;                           //序号/主键   内容
    private String title;                      // 公告标题
    private String summary;                    //黄条滚动展示内容(客户端、h5黄条展示)
    private String type;                      //APP、h5
    private String contentText;                       //h5页面展示的内容
    private String effectiveTime;           //公告展示时间
    private String invalidTime;//公告失效时间
    private String noticeStatus;//公告状态，0：生效 1：失效
    private String created;//创建时间
    private String updated;//更新时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
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

    public String getNoticeStatus() {
        return noticeStatus;
    }

    public void setNoticeStatus(String noticeStatus) {
        this.noticeStatus = noticeStatus;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
