package com.haiercash.gouhua.beans;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 3/20/23
 * @Version: 1.0
 */
public class PersonFloatingInfo {
    //文本
    private String text;
    //图标
    private String icon;
    //进度文本
    private String progressText;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getProgressText() {
        return progressText;
    }

    public void setProgressText(String progressText) {
        this.progressText = progressText;
    }
}
