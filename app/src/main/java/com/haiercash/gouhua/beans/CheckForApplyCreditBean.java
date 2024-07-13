package com.haiercash.gouhua.beans;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2022/5/19
 * @Version: 1.0
 */
public class CheckForApplyCreditBean {
    public String allowApply; //是否可再次申请 Y可再次申请N不可
    public String allowDays; //可再次申请的间隔天数 如：50
    public String allowDate; //可再次申请时间 格式：MM-DD

    public String getAllowApply() {
        return allowApply;
    }

    public void setAllowApply(String allowApply) {
        this.allowApply = allowApply;
    }

    public String getAllowDays() {
        return allowDays;
    }

    public void setAllowDays(String allowDays) {
        this.allowDays = allowDays;
    }

    public String getAllowDate() {
        return allowDate;
    }

    public void setAllowDate(String allowDate) {
        this.allowDate = allowDate;
    }
}
