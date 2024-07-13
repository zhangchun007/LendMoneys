package com.haiercash.gouhua.beans.homepage;

import java.io.Serializable;

public class HomeRepayBean implements Serializable {
    /// 额度提供商
    private String title;//额度提供商
    private String repayAmt;//还款金额
    private String lastDay; // 最近还款日
    private String jumpH5Url;// 还款跳转链接

    public HomeRepayBean() {
    }

    public HomeRepayBean(String title, String repayAmt, String lastDay, String jumpH5Url) {
        this.title = title;
        this.repayAmt = repayAmt;
        this.lastDay = lastDay;
        this.jumpH5Url = jumpH5Url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRepayAmt() {
        return repayAmt;
    }

    public void setRepayAmt(String repayAmt) {
        this.repayAmt = repayAmt;
    }

    public String getLastDay() {
        return lastDay;
    }

    public void setLastDay(String lastDay) {
        this.lastDay = lastDay;
    }

    public String getJumpH5Url() {
        return jumpH5Url;
    }

    public void setJumpH5Url(String jumpH5Url) {
        this.jumpH5Url = jumpH5Url;
    }
}
