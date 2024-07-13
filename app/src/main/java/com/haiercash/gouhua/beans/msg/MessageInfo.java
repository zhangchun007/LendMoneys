package com.haiercash.gouhua.beans.msg;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/6/8<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class MessageInfo {
    private String id;//消息id
    private String bizType;//业务类型
    private String title;//标题
    private String message;//消息内容
    private String jumpType;//跳转类型
    private String jumpKey;//跳转关键字
    private String isTip;//是否显示角标  Y-        显示角标 N-        不显示角标
    private String insertTime;//消息时间
    private String isRead;//是否已读
    private CustomInfo custom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getIsTip() {
        return isTip;
    }

    public void setIsTip(String isTip) {
        this.isTip = isTip;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public CustomInfo getCustom() {
        return custom;
    }

    public void setCustom(CustomInfo custom) {
        this.custom = custom;
    }
}
