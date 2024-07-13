package com.haiercash.gouhua.beans;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2017/12/15<br/>
 * 描    述：可配置文案:通知Notice<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class CopyWriterBean {
    private String id;                           //序号/主键   内容
    private String content;                      // 内容
    private String startDate;                    //开始时间 yyyy-MM-dd HH:mm:ss
    private String endDate;                      //结束时间 yyyy-MM-dd HH:mm:ss
    private String detail;                       //详情
    private String copyWriterPosition;           //文案位置

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCopyWriterPosition() {
        return copyWriterPosition;
    }

    public void setCopyWriterPosition(String copyWriterPosition) {
        this.copyWriterPosition = copyWriterPosition;
    }
}
