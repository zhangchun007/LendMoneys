package com.haiercash.gouhua.beans.homepage;

import com.app.haiercash.base.utils.system.CheckUtil;

/**
 * 首页额度模块，下半部分数据
 */
public class SubCardBean {
    private String reason;
    private int showLogo;
    private int showWarning;
    private String subTextColor;
    private String text;
    private String subText;
    private String textColor;
    private String subInfoText;
    private String subInfoTextColor;

    public String getSubInfoText() {
        return subInfoText;
    }

    public void setSubInfoText(String subInfoText) {
        this.subInfoText = subInfoText;
    }

    public String getSubInfoTextColor() {
        return subInfoTextColor;
    }

    public void setSubInfoTextColor(String subInfoTextColor) {
        this.subInfoTextColor = subInfoTextColor;
    }

    @Override
    public String toString() {
        return "SubCardBean{" +
                "reason='" + reason + '\'' +
                ", showLogo=" + showLogo +
                ", showWaring=" + showWarning +
                ", subTextColor='" + subTextColor + '\'' +
                ", text='" + text + '\'' +
                ", subText='" + subText + '\'' +
                ", textColor='" + textColor + '\'' +
                ", showArrow=" + showArrow +
                '}';
    }

    public int getShowArrow() {
        return showArrow;
    }

    public void setShowArrow(int showArrow) {
        this.showArrow = showArrow;
    }

    public SubCardBean(String reason, int showLogo, int showWarning, String subTextColor, String text, String subText, String textColor, int showArrow) {
        this.reason = reason;
        this.showLogo = showLogo;
        this.showWarning = showWarning;
        this.subTextColor = subTextColor;
        this.text = text;
        this.subText = subText;
        this.textColor = textColor;
        this.showArrow = showArrow;
    }

    private int showArrow;

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setShowLogo(int showLogo) {
        this.showLogo = showLogo;
    }

    public int getShowLogo() {
        return showLogo;
    }

    public void setShowWarning(int showWarning) {
        this.showWarning = showWarning;
    }

    public int getShowWarning() {
        return showWarning;
    }

    public void setSubTextColor(String subTextColor) {
        this.subTextColor = subTextColor;
    }

    public String getSubTextColor() {
        return subTextColor;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public String getSubText() {
        return subText;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getTextColor() {
        return textColor;
    }

    //server返回的是个空对象，解析出来会有默认值
    public boolean isNull() {
        return CheckUtil.isEmpty(text) && CheckUtil.isEmpty(subText);
    }

}
