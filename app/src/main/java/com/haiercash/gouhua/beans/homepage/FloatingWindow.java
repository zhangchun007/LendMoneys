package com.haiercash.gouhua.beans.homepage;

import java.io.Serializable;

/**
 * 浮窗数据
 */
public class FloatingWindow implements Serializable {
    private String text;//文本
    private String icon;//图标
    private String type;//返回类型，前端根据类型决定显示哪个json

    @Override
    public String toString() {
        return "FloatingWindow{" +
                "text='" + text + '\'' +
                ", icon='" + icon + '\'' +
                ", type='" + type + '\'' +
                ", progressText='" + progressText + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public FloatingWindow(String text, String icon, String type, String progressText) {
        this.text = text;
        this.icon = icon;
        this.type = type;
        this.progressText = progressText;
    }

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

    public FloatingWindow(String text, String icon, String progressText) {
        this.text = text;
        this.icon = icon;
        this.progressText = progressText;
    }

    public FloatingWindow() {
    }

    private String progressText;//进度文本
}
