package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/28
 * @Version: 1.0
 */
public class PersonCenterBean implements Serializable {
    private MemberInfoBean memberInfo;
    private CouponInfoBean couponInfo;
    private RepayInfoBean repayInfo;
    private String mashLoginMobile;
    private String mashCustName;
    private String mashCertNo;
    private List<PopUpWindowsBean> popUpWindows;

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

    public MemberInfoBean getMemberInfo() {
        return memberInfo;
    }

    public void setMemberInfo(MemberInfoBean memberInfo) {
        this.memberInfo = memberInfo;
    }

    public CouponInfoBean getCouponInfo() {
        return couponInfo;
    }

    public void setCouponInfo(CouponInfoBean couponInfo) {
        this.couponInfo = couponInfo;
    }

    public RepayInfoBean getRepayInfo() {
        return repayInfo;
    }

    public void setRepayInfo(RepayInfoBean repayInfo) {
        this.repayInfo = repayInfo;
    }

    public List<PopUpWindowsBean> getPopUpWindows() {
        return popUpWindows;
    }

    public void setPopUpWindows(List<PopUpWindowsBean> popUpWindows) {
        this.popUpWindows = popUpWindows;
    }
}
