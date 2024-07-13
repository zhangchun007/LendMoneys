package com.haiercash.gouhua.beans.homepage;

/**
 * 首页额度模块
 */
public class MainCardBean {

    private String cardType;// 卡片大小 small normal large
    private String reason;
    private String amount;

    private String amountOld;
    private int showLogo;
    private String btnText;
    private int showWarning;
    private int showTitleWarning;
    private String distance;
    private String subTextColor;
    private String text;
    private String subText;
    private String textColor;
    private String mainInfoText;
    private String mainInfoTextColor;

    private int showBubble;//气泡是否展示Integer1是0否
    private String bubbleText;//气泡文本String
    private String bubbleStartColor;//气泡颜色起始值String
    private String bubbleEndColor;//气泡颜色结束值String
    private BtnClickCheck btnClickCheck;//按钮校验


    //临时额度
    private String extSubText;  // 扩展副文本
    private String extSubTextColor; //扩展副文本颜色
    private String extSubBubbleText;//扩展副文本所属气泡
    private String extSubBubbleTextColor;//扩展副文本所属气泡颜色
    private String extSubBubbleStartColor;//气泡起始色值
    private String extSubBubbleEndColor;//气泡结束色值
    private String extSubBubbleStrokeColor;//描边色值
    private String extraCreditLink;//临时额度链接

    public String getExtSubText() {
        return extSubText;
    }

    public void setExtSubText(String extSubText) {
        this.extSubText = extSubText;
    }

    public String getExtSubTextColor() {
        return extSubTextColor;
    }

    public void setExtSubTextColor(String extSubTextColor) {
        this.extSubTextColor = extSubTextColor;
    }

    public String getExtSubBubbleText() {
        return extSubBubbleText;
    }

    public void setExtSubBubbleText(String extSubBubbleText) {
        this.extSubBubbleText = extSubBubbleText;
    }

    public String getExtSubBubbleStartColor() {
        return extSubBubbleStartColor;
    }

    public void setExtSubBubbleStartColor(String extSubBubbleStartColor) {
        this.extSubBubbleStartColor = extSubBubbleStartColor;
    }

    public String getExtSubBubbleEndColor() {
        return extSubBubbleEndColor;
    }

    public void setExtSubBubbleEndColor(String extSubBubbleEndColor) {
        this.extSubBubbleEndColor = extSubBubbleEndColor;
    }

    public String getExtSubBubbleStrokeColor() {
        return extSubBubbleStrokeColor;
    }

    public void setExtSubBubbleStrokeColor(String extSubBubbleStrokeColor) {
        this.extSubBubbleStrokeColor = extSubBubbleStrokeColor;
    }

    public String getExtraCreditLink() {
        return extraCreditLink;
    }

    public void setExtraCreditLink(String extraCreditLink) {
        this.extraCreditLink = extraCreditLink;
    }

    public MainCardBean(String cardType, String reason, String amount, int showLogo, String btnText, int showWarning, int showTitleWarning, String distance, String subTextColor, String text, String subText, String textColor, String mainInfoText, String mainInfoTextColor, int showBubble, String bubbleText, String bubbleStartColor, String bubbleEndColor, BtnClickCheck btnClickCheck, String extSubText, String extSubTextColor, String extSubBubbleText, String extSubBubbleTextColor, String extSubBubbleStartColor, String extSubBubbleEndColor, String extSubBubbleStrokeColor, String extraCreditLink) {
        this.cardType = cardType;
        this.reason = reason;
        this.amount = amount;
        this.showLogo = showLogo;
        this.btnText = btnText;
        this.showWarning = showWarning;
        this.showTitleWarning = showTitleWarning;
        this.distance = distance;
        this.subTextColor = subTextColor;
        this.text = text;
        this.subText = subText;
        this.textColor = textColor;
        this.mainInfoText = mainInfoText;
        this.mainInfoTextColor = mainInfoTextColor;
        this.showBubble = showBubble;
        this.bubbleText = bubbleText;
        this.bubbleStartColor = bubbleStartColor;
        this.bubbleEndColor = bubbleEndColor;
        this.btnClickCheck = btnClickCheck;
        this.extSubText = extSubText;
        this.extSubTextColor = extSubTextColor;
        this.extSubBubbleText = extSubBubbleText;
        this.extSubBubbleTextColor = extSubBubbleTextColor;
        this.extSubBubbleStartColor = extSubBubbleStartColor;
        this.extSubBubbleEndColor = extSubBubbleEndColor;
        this.extSubBubbleStrokeColor = extSubBubbleStrokeColor;
        this.extraCreditLink = extraCreditLink;
    }

    public int getShowTitleWarning() {
        return showTitleWarning;
    }

    public void setShowTitleWarning(int showTitleWarning) {
        this.showTitleWarning = showTitleWarning;
    }

    public String getMainInfoText() {
        return mainInfoText;
    }

    public void setMainInfoText(String mainInfoText) {
        this.mainInfoText = mainInfoText;
    }

    public String getMainInfoTextColor() {
        return mainInfoTextColor;
    }

    public void setMainInfoTextColor(String mainInfoTextColor) {
        this.mainInfoTextColor = mainInfoTextColor;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "MainCardBean{" +
                "cardType='" + cardType + '\'' +
                ", reason='" + reason + '\'' +
                ", amount='" + amount + '\'' +
                ", amountOld='" + amountOld + '\'' +
                ", showLogo=" + showLogo +
                ", btnText='" + btnText + '\'' +
                ", showWarning=" + showWarning +
                ", showTitleWarning=" + showTitleWarning +
                ", distance='" + distance + '\'' +
                ", subTextColor='" + subTextColor + '\'' +
                ", text='" + text + '\'' +
                ", subText='" + subText + '\'' +
                ", textColor='" + textColor + '\'' +
                ", mainInfoText='" + mainInfoText + '\'' +
                ", mainInfoTextColor='" + mainInfoTextColor + '\'' +
                ", showBubble=" + showBubble +
                ", bubbleText='" + bubbleText + '\'' +
                ", bubbleStartColor='" + bubbleStartColor + '\'' +
                ", bubbleEndColor='" + bubbleEndColor + '\'' +
                ", btnClickCheck=" + btnClickCheck +
                ", extSubText='" + extSubText + '\'' +
                ", extSubTextColor='" + extSubTextColor + '\'' +
                ", extSubBubbleText='" + extSubBubbleText + '\'' +
                ", extSubBubbleTextColor='" + extSubBubbleTextColor + '\'' +
                ", extSubBubbleStartColor='" + extSubBubbleStartColor + '\'' +
                ", extSubBubbleEndColor='" + extSubBubbleEndColor + '\'' +
                ", extSubBubbleStrokeColor='" + extSubBubbleStrokeColor + '\'' +
                ", extraCreditLink='" + extraCreditLink + '\'' +
                '}';
    }

    public String getAmountOld() {
        return amountOld;
    }

    public void setAmountOld(String amountOld) {
        this.amountOld = amountOld;
    }

    public void setShowLogo(int showLogo) {
        this.showLogo = showLogo;
    }

    public int getShowLogo() {
        return showLogo;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setShowWarning(int showWarning) {
        this.showWarning = showWarning;
    }

    public int getShowWarning() {
        return showWarning;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
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

    public BtnClickCheck getBtnClickCheck() {
        return btnClickCheck;
    }

    public void setBtnClickCheck(BtnClickCheck btnClickCheck) {
        this.btnClickCheck = btnClickCheck;
    }

    public String getExtSubBubbleTextColor() {
        return extSubBubbleTextColor;
    }

    public void setExtSubBubbleTextColor(String extSubBubbleTextColor) {
        this.extSubBubbleTextColor = extSubBubbleTextColor;
    }

    public MainCardBean(String cardType, String reason, String amount, int showLogo, String btnText, int showWarning, int showTitleWarning, String distance, String subTextColor, String text, String subText, String textColor, String mainInfoText, String mainInfoTextColor, int showBubble, String bubbleText, String bubbleStartColor, String bubbleEndColor, BtnClickCheck btnClickCheck, String extSubBubbleTextColor) {
        this.cardType = cardType;
        this.reason = reason;
        this.amount = amount;
        this.showLogo = showLogo;
        this.btnText = btnText;
        this.showWarning = showWarning;
        this.showTitleWarning = showTitleWarning;
        this.distance = distance;
        this.subTextColor = subTextColor;
        this.text = text;
        this.subText = subText;
        this.textColor = textColor;
        this.mainInfoText = mainInfoText;
        this.mainInfoTextColor = mainInfoTextColor;
        this.showBubble = showBubble;
        this.bubbleText = bubbleText;
        this.bubbleStartColor = bubbleStartColor;
        this.bubbleEndColor = bubbleEndColor;
        this.btnClickCheck = btnClickCheck;
        this.extSubBubbleTextColor = extSubBubbleTextColor;
    }


}
