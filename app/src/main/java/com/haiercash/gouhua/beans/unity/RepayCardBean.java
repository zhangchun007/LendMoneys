package com.haiercash.gouhua.beans.unity;

import java.util.HashMap;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/21
 * @Version: 1.0
 */
public class RepayCardBean {
    private String text;//标题
    private PopBean bubbleText;//主气泡
    private String amount;//金额
    private String subText;//副标题
    private List<ShowConditionBean> subTextColor;//副标题颜色
    private PopBean subBubbleText;//副气泡
    private String btnText;//按钮文字
    private ActionBean action;
    private HashMap<String, Object> exposure;

    private String remainDay;
    private String loanIsOd;
    private String repayStatusName;

    public RepayCardBean() {
    }

    public RepayCardBean(String text, PopBean bubbleText, String amount, String subText, List<ShowConditionBean> subTextColor, PopBean subBubbleText, String btnText, ActionBean action, String remainDay, String loanIsOd, String repayStatusName) {
        this.text = text;
        this.bubbleText = bubbleText;
        this.amount = amount;
        this.subText = subText;
        this.subTextColor = subTextColor;
        this.subBubbleText = subBubbleText;
        this.btnText = btnText;
        this.action = action;
        this.remainDay = remainDay;
        this.loanIsOd = loanIsOd;
        this.repayStatusName = repayStatusName;
    }

    public HashMap<String, Object> getExposure() {
        return exposure;
    }

    public void setExposure(HashMap<String, Object> exposure) {
        this.exposure = exposure;
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

    public String getRepayStatusName() {
        return repayStatusName;
    }

    public void setRepayStatusName(String repayStatusName) {
        this.repayStatusName = repayStatusName;
    }

    public ActionBean getAction() {
        return action;
    }

    public void setAction(ActionBean action) {
        this.action = action;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PopBean getBubbleText() {
        return bubbleText;
    }

    public void setBubbleText(PopBean bubbleText) {
        this.bubbleText = bubbleText;
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

    public List<ShowConditionBean> getSubTextColor() {
        return subTextColor;
    }

    public void setSubTextColor(List<ShowConditionBean> subTextColor) {
        this.subTextColor = subTextColor;
    }

    public PopBean getSubBubbleText() {
        return subBubbleText;
    }

    public void setSubBubbleText(PopBean subBubbleText) {
        this.subBubbleText = subBubbleText;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }
}
