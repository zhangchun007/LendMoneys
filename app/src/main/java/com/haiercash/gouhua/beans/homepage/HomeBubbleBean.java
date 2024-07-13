package com.haiercash.gouhua.beans.homepage;

//首页气泡的值
public class HomeBubbleBean {
    private String quotaCardControl; // 额度卡片控件 值为气泡文案
    private String nextControl; //下一步控件 String值为气泡文案

    @Override
    public String toString() {
        return "HomeBubbleBean{" +
                "quotaCardControl='" + quotaCardControl + '\'' +
                ", nextControl='" + nextControl + '\'' +
                '}';
    }

    public String getQuotaCardControl() {
        return quotaCardControl;
    }

    public void setQuotaCardControl(String quotaCardControl) {
        this.quotaCardControl = quotaCardControl;
    }

    public String getNextControl() {
        return nextControl;
    }

    public void setNextControl(String nextControl) {
        this.nextControl = nextControl;
    }

    public HomeBubbleBean(String quotaCardControl, String nextControl) {
        this.quotaCardControl = quotaCardControl;
        this.nextControl = nextControl;
    }

    public HomeBubbleBean() {
    }
}
