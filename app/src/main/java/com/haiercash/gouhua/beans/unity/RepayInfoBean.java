package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/28
 * @Version: 1.0
 */
public class RepayInfoBean implements Serializable {
    private String amount;
    private String btnText;
    private String bubbleText;
    private String loanIsOd;
    private String showBubble;
    private String remainDay;
    private String topText;
    private String subText;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getBubbleText() {
        return bubbleText;
    }

    public void setBubbleText(String bubbleText) {
        this.bubbleText = bubbleText;
    }

    public String getLoanIsOd() {
        return loanIsOd;
    }

    public void setLoanIsOd(String loanIsOd) {
        this.loanIsOd = loanIsOd;
    }

    public String getShowBubble() {
        return showBubble;
    }

    public void setShowBubble(String showBubble) {
        this.showBubble = showBubble;
    }

    public String getRemainDay() {
        return remainDay;
    }

    public void setRemainDay(String remainDay) {
        this.remainDay = remainDay;
    }

    public String getTopText() {
        return topText;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }
}
