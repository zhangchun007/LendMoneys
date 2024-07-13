package com.haiercash.gouhua.beans;

import com.haiercash.gouhua.beans.homepage.HomeRepayBean;

import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 3/20/23
 * @Version: 1.0
 */
public class PersonRepayCardInfo {
    //主卡内容距离顶部的距离
    private String distance;

    //是否显示logo 1 是 0 否
    private int showLogo;
    //金额上方的文本
    private String text;
    //Text的颜色
    private String textColor;

    //显示的金额
    private String amount;
    //副文本
    private String subText;
    //副文本颜色
    private String subTextColor;

    //按钮文本
    private String btnText;
    //气泡是否展示 1 是 0 否
    private int showBubble;
    //Text对应的气泡文本
    private String bubbleText;
    //气泡颜色类型
    private String bubbleColorType;
    //气泡颜色结束值
    private String bubbleStartColor;
    private String bubbleEndColor;

    //Subtext气泡是否展示 1 是 0 否
    private int showSubBubble;

    //Subtext气泡是否展示 1 是 0 否
    private String subBubbleText;
    //Subtext气泡颜色类型
    private String subBubbleTextColorType;
    //Sub气泡颜色起始值
    private String subBubbleStartColor;
    private String subBubbleEndColor;
    private String subBubbleStrokeColor;
    private String loanIsOd;//借据是否逾期 Y 是  N 否
    private String remainDay;//借据是否逾期天数

    private String repayDesc; //待还描述

    private List<HomeRepayBean> repayList;

    public List<HomeRepayBean> getRepayList() {
        return repayList;
    }

    public void setRepayList(List<HomeRepayBean> repayList) {
        this.repayList = repayList;
    }

    public PersonRepayCardInfo(String distance, int showLogo, String text, String textColor, String amount, String subText, String subTextColor, String btnText, int showBubble, String bubbleText, String bubbleColorType, String bubbleStartColor, String bubbleEndColor, int showSubBubble, String subBubbleText, String subBubbleTextColorType, String subBubbleStartColor, String subBubbleEndColor, String subBubbleStrokeColor, String loanIsOd, String remainDay, String repayDesc, List<HomeRepayBean> repayList) {
        this.distance = distance;
        this.showLogo = showLogo;
        this.text = text;
        this.textColor = textColor;
        this.amount = amount;
        this.subText = subText;
        this.subTextColor = subTextColor;
        this.btnText = btnText;
        this.showBubble = showBubble;
        this.bubbleText = bubbleText;
        this.bubbleColorType = bubbleColorType;
        this.bubbleStartColor = bubbleStartColor;
        this.bubbleEndColor = bubbleEndColor;
        this.showSubBubble = showSubBubble;
        this.subBubbleText = subBubbleText;
        this.subBubbleTextColorType = subBubbleTextColorType;
        this.subBubbleStartColor = subBubbleStartColor;
        this.subBubbleEndColor = subBubbleEndColor;
        this.subBubbleStrokeColor = subBubbleStrokeColor;
        this.loanIsOd = loanIsOd;
        this.remainDay = remainDay;
        this.repayDesc = repayDesc;
        this.repayList = repayList;
    }

    public PersonRepayCardInfo() {
    }

    public PersonRepayCardInfo(String distance, int showLogo, String text, String textColor, String amount, String subText, String subTextColor, String btnText, int showBubble, String bubbleText, String bubbleColorType, String bubbleStartColor, String bubbleEndColor, int showSubBubble, String subBubbleText, String subBubbleTextColorType, String subBubbleStartColor, String subBubbleEndColor, String subBubbleStrokeColor, String loanIsOd, String remainDay, String repayDesc) {
        this.distance = distance;
        this.showLogo = showLogo;
        this.text = text;
        this.textColor = textColor;
        this.amount = amount;
        this.subText = subText;
        this.subTextColor = subTextColor;
        this.btnText = btnText;
        this.showBubble = showBubble;
        this.bubbleText = bubbleText;
        this.bubbleColorType = bubbleColorType;
        this.bubbleStartColor = bubbleStartColor;
        this.bubbleEndColor = bubbleEndColor;
        this.showSubBubble = showSubBubble;
        this.subBubbleText = subBubbleText;
        this.subBubbleTextColorType = subBubbleTextColorType;
        this.subBubbleStartColor = subBubbleStartColor;
        this.subBubbleEndColor = subBubbleEndColor;
        this.subBubbleStrokeColor = subBubbleStrokeColor;
        this.loanIsOd = loanIsOd;
        this.remainDay = remainDay;
        this.repayDesc = repayDesc;
    }

    public String getRepayDesc() {
        return repayDesc;
    }

    public void setRepayDesc(String repayDesc) {
        this.repayDesc = repayDesc;
    }

    public String getRemainDay() {
        return remainDay;
    }

    public void setRemainDay(String remainDay) {
        this.remainDay = remainDay;
    }

    public String getLoanIsOd() {
        return loanIsOd;
    }

    public void setLoanIsOd(String loanIsOd) {
        this.loanIsOd = loanIsOd;
    }

    public String getSubBubbleStrokeColor() {
        return subBubbleStrokeColor;
    }

    public void setSubBubbleStrokeColor(String subBubbleStrokeColor) {
        this.subBubbleStrokeColor = subBubbleStrokeColor;
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

    public String getSubBubbleStartColor() {
        return subBubbleStartColor;
    }

    public void setSubBubbleStartColor(String subBubbleStartColor) {
        this.subBubbleStartColor = subBubbleStartColor;
    }

    public String getSubBubbleEndColor() {
        return subBubbleEndColor;
    }

    public void setSubBubbleEndColor(String subBubbleEndColor) {
        this.subBubbleEndColor = subBubbleEndColor;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getShowLogo() {
        return showLogo;
    }

    public void setShowLogo(int showLogo) {
        this.showLogo = showLogo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public String getSubTextColor() {
        return subTextColor;
    }

    public void setSubTextColor(String subTextColor) {
        this.subTextColor = subTextColor;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
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

    public int getShowSubBubble() {
        return showSubBubble;
    }

    public void setShowSubBubble(int showSubBubble) {
        this.showSubBubble = showSubBubble;
    }

    public String getSubBubbleText() {
        return subBubbleText;
    }

    public void setSubBubbleText(String subBubbleText) {
        this.subBubbleText = subBubbleText;
    }

    public String getSubBubbleTextColorType() {
        return subBubbleTextColorType;
    }

    public void setSubBubbleTextColorType(String subBubbleTextColorType) {
        this.subBubbleTextColorType = subBubbleTextColorType;
    }
}
