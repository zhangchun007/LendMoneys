package com.haiercash.gouhua.beans;

import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 3/20/23
 * @Version: 1.0
 */
public class PersonCenterInfo {
    //金刚区信息
    private List<FuncBeans> residentRibbon;
    //常驻功能区
    private List<FuncBeans> commonRibbon;
    //底部banner
    private PersonBottomInfo bottom;
    //会员资源位
    private PersonVipInfo midRes;
    //还款卡片
    private PersonRepayCardInfo repayCard;
    //掩码登录手机号
    private String mashLoginMobile;
    //掩码姓名
    private String mashCustName;
    //掩码身份证号
    private String mashCertNo;
    //会员开通状态 Y 是  N  否
    private String hyOpenState;

    //是否展示浮窗 1 是 0 否
    private String showFloatingWindow;
    //浮窗数据
    private PersonFloatingInfo floatingWindow;

    public List<FuncBeans> getResidentRibbon() {
        return residentRibbon;
    }

    public void setResidentRibbon(List<FuncBeans> residentRibbon) {
        this.residentRibbon = residentRibbon;
    }

    public List<FuncBeans> getCommonRibbon() {
        return commonRibbon;
    }

    public void setCommonRibbon(List<FuncBeans> commonRibbon) {
        this.commonRibbon = commonRibbon;
    }

    public PersonBottomInfo getBottom() {
        return bottom;
    }

    public void setBottom(PersonBottomInfo bottom) {
        this.bottom = bottom;
    }

    public PersonVipInfo getMidRes() {
        return midRes;
    }

    public void setMidRes(PersonVipInfo midRes) {
        this.midRes = midRes;
    }

    public PersonRepayCardInfo getRepayCard() {
        return repayCard;
    }

    public void setRepayCard(PersonRepayCardInfo repayCard) {
        this.repayCard = repayCard;
    }

    public String getMashLoginMobile() {
        return mashLoginMobile;
    }

    public void setMashLoginMobile(String mashLoginMobile) {
        this.mashLoginMobile = mashLoginMobile;
    }

    public String getMashCustName() {
        return mashCustName;
    }

    public void setMashCustName(String mashCustName) {
        this.mashCustName = mashCustName;
    }

    public String getMashCertNo() {
        return mashCertNo;
    }

    public void setMashCertNo(String mashCertNo) {
        this.mashCertNo = mashCertNo;
    }

    public String getHyOpenState() {
        return hyOpenState;
    }

    public void setHyOpenState(String hyOpenState) {
        this.hyOpenState = hyOpenState;
    }

    public String getShowFloatingWindow() {
        return showFloatingWindow;
    }

    public void setShowFloatingWindow(String showFloatingWindow) {
        this.showFloatingWindow = showFloatingWindow;
    }

    public PersonFloatingInfo getFloatingWindow() {
        return floatingWindow;
    }

    public void setFloatingWindow(PersonFloatingInfo floatingWindow) {
        this.floatingWindow = floatingWindow;
    }
}
