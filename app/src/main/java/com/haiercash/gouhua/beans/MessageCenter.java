package com.haiercash.gouhua.beans;

import java.util.List;

/**
 * *@Author:    Sun
 * *@Date  :    2020/8/28
 * *@FileName: MessageCenter
 * *@Description: 消息中心还款提醒和申请进度列表数据
 */
public class MessageCenter {


    /**
     * 系统时间
     */
    public String sysTime;

    /**
     * 未读通知数量
     */
    public int unReadAdviceCount;

    /**
     * 未读活动数量
     */
    public int unReadActCount;

    /**
     * 未读还款提醒数量
     */
    public int unReadRepayCount;

    /**
     * 未读申请进度数量
     */
    public int unReadProgressCount;

    /**
     * 消息列表
     */
    public List<MsgList> msgList;

    /**
     *
     */
    public String directUrlForCreditRejection;

    public String getDirectUrlForCreditRejection() {
        return directUrlForCreditRejection;
    }

    public void setDirectUrlForCreditRejection(String directUrlForCreditRejection) {
        this.directUrlForCreditRejection = directUrlForCreditRejection;
    }


    public String getSysTime() {
        return sysTime;
    }

    public void setSysTime(String sysTime) {
        this.sysTime = sysTime;
    }

    public int getUnReadAdviceCount() {
        return unReadAdviceCount;
    }

    public void setUnReadAdviceCount(int unReadAdviceCount) {
        this.unReadAdviceCount = unReadAdviceCount;
    }

    public int getUnReadActCount() {
        return unReadActCount;
    }

    public void setUnReadActCount(int unReadActCount) {
        this.unReadActCount = unReadActCount;
    }

    public int getUnReadRepayCount() {
        return unReadRepayCount;
    }

    public void setUnReadRepayCount(int unReadRepayCount) {
        this.unReadRepayCount = unReadRepayCount;
    }

    public int getUnReadProgressCount() {
        return unReadProgressCount;
    }

    public void setUnReadProgressCount(int unReadProgressCount) {
        this.unReadProgressCount = unReadProgressCount;
    }

    public List<MsgList> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<MsgList> msgList) {
        this.msgList = msgList;
    }
}
