package com.haiercash.gouhua.beans;

import java.util.List;

/**
 * 项目名称：goHuaAND
 * 项目作者：胡玉君
 * 创建日期：2017/6/28 13:46.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class EduBean {
    public String userState;               //01未授信02借款还款03逾期04不允许提交额度申请
    public String outSts;                  //审核状态：00 待提交 01 审批中 03 已取消 22 审批退回 25 额度申请被拒 26 额度申请已取消 27 已通过 99 未申请过额度只有 userState 为01/04 时才返回
    public String totalLimit;              //总额度：UserState为02/03时才返回
    public String availLimit;              //可用额度：serState为02/03时才返回
    public String odCount;                 //逾期比数：userState为 03 时才返回
    public String lastSetlDt;              //最近还款日：userState为 02 时才返回
    public String stayAmout;               //应还款：userState为 02 时才返回
    public String crdSeq;                  //在途的申请流水号：userState 为 01时才返回
    //public String remainDay;               //剩余多少天后才能再次提交  userState 为 04时才返回
    //public String unit;                     //时间单位  userState 为 04时才返回天/月
    public String tips;                     //不允许提交额度申请是的友好提示 userState 为04时才返回
    public String rejectCount;             //被拒绝次数 userState 为04时才返回
    public String odStatus;                 //userState 为03时才返回“Y” 有逾期，首页展示逾期“N”无逾期，首页展示冻结
    public List<ApplReturnInfo> applReturnInfo;          //申请退回信息
    private String preCreditLimit;          //预授信额度
    private String speSeq;                  //额度编号
    private int le90;  //逾期多少天
    private int limit;  //判断逾期类型

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLe90() {
        return le90;
    }

    public void setLe90(int le90) {
        this.le90 = le90;
    }

    public String getUserState() {
        return userState;
    }

    public List<ApplReturnInfo> getApplReturnInfo() {
        return applReturnInfo;
    }

    public void setApplReturnInfo(List<ApplReturnInfo> applReturnInfo) {
        this.applReturnInfo = applReturnInfo;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getOutSts() {
        return outSts;
    }

    public void setOutSts(String outSts) {
        this.outSts = outSts;
    }

    public String getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(String totalLimit) {
        this.totalLimit = totalLimit;
    }

    public String getAvailLimit() {
        return availLimit;
    }

    public void setAvailLimit(String availLimit) {
        this.availLimit = availLimit;
    }

    public String getOdCount() {
        return odCount;
    }

    public void setOdCount(String odCount) {
        this.odCount = odCount;
    }

    public String getLastSetlDt() {
        return lastSetlDt;
    }

    public void setLastSetlDt(String lastSetlDt) {
        this.lastSetlDt = lastSetlDt;
    }

    public String getStayAmout() {
        return stayAmout;
    }

    public void setStayAmout(String stayAmout) {
        this.stayAmout = stayAmout;
    }

    public String getCrdSeq() {
        return crdSeq;
    }

    public void setCrdSeq(String crdSeq) {
        this.crdSeq = crdSeq;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getRejectCount() {
        return rejectCount;
    }

    public void setRejectCount(String rejectCount) {
        this.rejectCount = rejectCount;
    }

    public String getOdStatus() {
        return odStatus;
    }

    public void setOdStatus(String odStatus) {
        this.odStatus = odStatus;
    }

    public String getPreCreditLimit() {
        return preCreditLimit;
    }

    public void setPreCreditLimit(String preCreditLimit) {
        this.preCreditLimit = preCreditLimit;
    }

    public String getSpeSeq() {
        return speSeq;
    }

    public void setSpeSeq(String speSeq) {
        this.speSeq = speSeq;
    }
}
