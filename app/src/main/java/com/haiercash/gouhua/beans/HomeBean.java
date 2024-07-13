package com.haiercash.gouhua.beans;

import com.haiercash.gouhua.beans.borrowmoney.LoanRat;

import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/3/26<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class HomeBean {
    private EduBean homePageInfo;
    private List<LoanRat> info;
    private String rateDesc;    //费率信息

    public EduBean getHomePageInfo() {
        return homePageInfo;
    }

    public void setHomePageInfo(EduBean homePageInfo) {
        this.homePageInfo = homePageInfo;
    }

    public List<LoanRat> getInfo() {
        return info;
    }

    public void setInfo(List<LoanRat> info) {
        this.info = info;
    }

    public String getRateDesc() {
        return rateDesc;
    }

    public void setRateDesc(String rateDesc) {
        this.rateDesc = rateDesc;
    }
}
