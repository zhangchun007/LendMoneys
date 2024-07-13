package com.haiercash.gouhua.beans;

/**
 * Created by use on 2017/8/11.
 */

public class MsgList {

    public String id;
    /**
     * 活动类型
     * 1节点类
     * 2营销类
     * 3核心推送
     * 4消息模板
     * 5 消息推送
     */
    public String activityType;
    public String title;

    public String message;
    public String jumpType;
    public String jumpKey;
    /**
     * 是否显示角标
     * <p>
     * Y-显示角标
     * N-不显示角标
     */
    public String isTip;
    public String insertTime;

    /**
     * 是否已读
     * 入参tagCode为申请进度时且为大订单时有必返
     */
    public String isRead;

    /**
     * 消息类型 入参tagCode为活动时必返
     * 0:未读
     * 1:已读
     */
    public String msgTyp;

    /**
     * 图片
     * 入参tagCode为申请进度时必返
     * 01:贷款
     * 02：额度
     */
    public String picPath;

    /**
     * 活动类型tag
     * 入参tagCode为申请进度时且为额度申请时必返
     * 011 白条
     * 012 现金
     * 021 专项额度
     * <p>
     * 额度类型要与额度子类型对应否则查询不到额度信息
     * 01-->011||012
     * 02-->021
     * 消息类型为额度时返回
     */
    public String activityTypeTag;


    /**
     * 额度子类型
     */
    public String creditDtlTyp;


    /**
     * 额度状态
     */
    public String outSts;


    /**
     * 大订单号
     * 入参tagCode为申请进度时且为大订单时有必返
     */
    public String bigOrderNo;


    /**
     * 入参tagCode为申请进度时且为支用时返
     */
    public String applSeq;

    public String directUrl;

    public String getDirectUrl() {
        return directUrl;
    }

    public void setDirectUrl(String directUrl) {
        this.directUrl = directUrl;
    }

    public String getApplSeq() {
        return applSeq;
    }

    public void setApplSeq(String applSeq) {
        this.applSeq = applSeq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
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

    public String getMsgTyp() {
        return msgTyp;
    }

    public void setMsgTyp(String msgTyp) {
        this.msgTyp = msgTyp;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getActivityTypeTag() {
        return activityTypeTag;
    }

    public void setActivityTypeTag(String activityTypeTag) {
        this.activityTypeTag = activityTypeTag;
    }

    public String getCreditDtlTyp() {
        return creditDtlTyp;
    }

    public void setCreditDtlTyp(String creditDtlTyp) {
        this.creditDtlTyp = creditDtlTyp;
    }

    public String getOutSts() {
        return outSts;
    }

    public void setOutSts(String outSts) {
        this.outSts = outSts;
    }

    public String getBigOrderNo() {
        return bigOrderNo;
    }

    public void setBigOrderNo(String bigOrderNo) {
        this.bigOrderNo = bigOrderNo;
    }
}
