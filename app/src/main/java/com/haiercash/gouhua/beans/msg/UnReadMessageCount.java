package com.haiercash.gouhua.beans.msg;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 6/11/23
 * @Version: 1.0
 */
public class UnReadMessageCount {
    private int total; //未读消息总数量
    private int noticeCnt; //通知类消息未读数量
    private int activityCnt; //活动类消息未读数量

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNoticeCnt() {
        return noticeCnt;
    }

    public void setNoticeCnt(int noticeCnt) {
        this.noticeCnt = noticeCnt;
    }

    public int getActivityCnt() {
        return activityCnt;
    }

    public void setActivityCnt(int activityCnt) {
        this.activityCnt = activityCnt;
    }
}
