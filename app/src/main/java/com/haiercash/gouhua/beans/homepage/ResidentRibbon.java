package com.haiercash.gouhua.beans.homepage;

import java.io.Serializable;

/**
 * 金刚区
 */
public class ResidentRibbon implements Serializable {
    private String text;//文本String还款、借款、关注、领券
    private String icon;//图标iconString
    private int showBubble;//气泡是否展示Integer1是0否
    private String bubbleText;//气泡文本String
    private String bubbleStartColor;//气泡颜色起始值String
    private String bubbleEndColor;//气泡颜色结束值String
    private String itemAction;//跳转路由String

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

    public int getShowBubble() {
        return showBubble;
    }

    public void setShowBubble(int showBubble) {
        this.showBubble = showBubble;
    }

    public String getBubbleText() {
        return bubbleText;
    }

    public void setBubbleText(String bubbleText) {
        this.bubbleText = bubbleText;
    }

    public String getBubbleStartColor() {
        return bubbleStartColor;
    }

    public void setBubbleStartColor(String bubbleStartColor) {
        this.bubbleStartColor = bubbleStartColor;
    }

    public String getBubbleEndColor() {
        return bubbleEndColor;
    }

    public void setBubbleEndColor(String bubbleEndColor) {
        this.bubbleEndColor = bubbleEndColor;
    }

    public String getItemAction() {
        return itemAction;
    }

    public void setItemAction(String itemAction) {
        this.itemAction = itemAction;
    }

    public String getClickErrorMsg() {
        return clickErrorMsg;
    }

    public void setClickErrorMsg(String clickErrorMsg) {
        this.clickErrorMsg = clickErrorMsg;
    }

    public ResidentRibbon(String text, String icon, int showBubble, String bubbleText, String bubbleStartColor, String bubbleEndColor, String itemAction, String clickErrorMsg) {
        this.text = text;
        this.icon = icon;
        this.showBubble = showBubble;
        this.bubbleText = bubbleText;
        this.bubbleStartColor = bubbleStartColor;
        this.bubbleEndColor = bubbleEndColor;
        this.itemAction = itemAction;
        this.clickErrorMsg = clickErrorMsg;
    }

    public ResidentRibbon() {
    }

    private String clickErrorMsg;//点击错误文案String与itemAction不能同时存

}
