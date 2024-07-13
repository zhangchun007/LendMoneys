package com.haiercash.gouhua.beans;

/**
 * 项目名称：goHuaAND
 * 项目作者：胡玉君
 * 创建日期：2017/6/29 11:11.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class ProcessBean {
    /**
     * 10    同意
     * 11    转办
     * 20    拒绝
     * 21    协办
     * 30    打回
     * 31    催办
     * 40    退回
     * 50    补件
     * 60    拿回
     * 61    追回
     * 70    撤办
     * 80    挂起
     * 90    唤醒
     * 91    激活
     * 92    取消
     * 93    疑似欺诈
     * 99    转人工审批
     */
    public String appConclusion;
    //审批状态
    private String wfiNodeName;
    //办理时间
    private String operateTime;
    //审批结论描述 名称，用于显示（参考appConclusion字段描述）
    private String appConclusionDesc;
    //外部意见
    private String appOutAdvice;
    //是否贷款  是Y  否N
    private String isOrder;
    /**
     * 01 审批中 02成功 03失败
     */
    public String appStatus;
    //额度
    // 如果是贷款表示本金
    //如果是额度申请表示额度
    private String applyAmt;
    //额度申请类型 01 普通 02 提额
    private String creditApplyTyp;

    public String getWfiNodeName() {
        return wfiNodeName;
    }

    public void setWfiNodeName(String wfiNodeName) {
        this.wfiNodeName = wfiNodeName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getAppConclusionDesc() {
        return appConclusionDesc;
    }

    public void setAppConclusionDesc(String appConclusionDesc) {
        this.appConclusionDesc = appConclusionDesc;
    }

    public String getAppOutAdvice() {
        return appOutAdvice;
    }

    public void setAppOutAdvice(String appOutAdvice) {
        this.appOutAdvice = appOutAdvice;
    }

    public String getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(String isOrder) {
        this.isOrder = isOrder;
    }

    public String getCreditApplyTyp() {
        return creditApplyTyp;
    }

    public void setCreditApplyTyp(String creditApplyTyp) {
        this.creditApplyTyp = creditApplyTyp;
    }

    public String getApplyAmt() {
        return applyAmt;
    }

    public void setApplyAmt(String applyAmt) {
        this.applyAmt = applyAmt;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }

    public String getAppConclusion() {
        return appConclusion;
    }

    public void setAppConclusion(String appConclusion) {
        this.appConclusion = appConclusion;
    }
}
