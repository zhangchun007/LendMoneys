package com.haiercash.gouhua.beans.repayment;

import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/1/3<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class PartialBean {
    private String lastSetlDt; //最近还款日
    private String stayAmount;//近七日待还总金额
    //ON 开关打开，允许多选
    //    OFF 不允许多选， 需单选
    private String multiSelectSwitch;//多选开关
    //是返回1， 否返回0
    private String sixNum;//六位密码设置状态
    private String dailySettleNotAllowRepayErrorMsg;//日结期间不能还款文案
    private List<LoanBean> orders;//贷款详情列表

    public String getLastSetlDt() {
        return lastSetlDt;
    }

    public void setLastSetlDt(String lastSetlDt) {
        this.lastSetlDt = lastSetlDt;
    }

    public String getStayAmount() {
        return stayAmount;
    }

    public void setStayAmount(String stayAmount) {
        this.stayAmount = stayAmount;
    }

    public String getMultiSelectSwitch() {
        return multiSelectSwitch;
    }

    public void setMultiSelectSwitch(String multiSelectSwitch) {
        this.multiSelectSwitch = multiSelectSwitch;
    }

    public String getSixNum() {
        return sixNum;
    }

    public void setSixNum(String sixNum) {
        this.sixNum = sixNum;
    }

    public String getDailySettleNotAllowRepayErrorMsg() {
        return dailySettleNotAllowRepayErrorMsg;
    }

    public void setDailySettleNotAllowRepayErrorMsg(String dailySettleNotAllowRepayErrorMsg) {
        this.dailySettleNotAllowRepayErrorMsg = dailySettleNotAllowRepayErrorMsg;
    }

    public List<LoanBean> getOrders() {
        return orders;
    }

    public void setOrders(List<LoanBean> orders) {
        this.orders = orders;
    }
}
