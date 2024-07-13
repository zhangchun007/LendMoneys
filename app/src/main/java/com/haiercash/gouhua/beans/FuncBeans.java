package com.haiercash.gouhua.beans;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 3/15/23
 * @Version: 1.0
 */
public class FuncBeans {
    // 图标icon
    private String icon;
    //默认图片
    private int imgPathDefault;
    //文本
    private String text;
    //跳转路由
    private String itemAction;
    //气泡是否展示 1 是 0 否
    private int showBubble;
    //气泡文本
    private String bubbleText;
    //气泡颜色起始值
    private String bubbleEndColor;
    private String bubbleColorType;
    //气泡颜色类型
    private String bubbleStartColor;
    //点击错误文案 与itemAction不能同时存在
    private String clickErrorMsg;


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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getImgPathDefault() {
        return imgPathDefault;
    }

    public void setImgPathDefault(int imgPathDefault) {
        this.imgPathDefault = imgPathDefault;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getItemAction() {
        return itemAction;
    }

    public void setItemAction(String itemAction) {
        this.itemAction = itemAction;
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

    public String getBubbleColorType() {
        return bubbleColorType;
    }

    public void setBubbleColorType(String bubbleColorType) {
        this.bubbleColorType = bubbleColorType;
    }

    public String getClickErrorMsg() {
        return clickErrorMsg;
    }

    public void setClickErrorMsg(String clickErrorMsg) {
        this.clickErrorMsg = clickErrorMsg;
    }
}
