package com.haiercash.gouhua.beans.borrowmoney;

import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/1/10<br/>
 * 描    述：还款记录<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class RepayRecordBean{
    private String repayRecordTime;//分组时间
    private List<RepayBean> repayRecordList;

    public String getRepayRecordTime() {
        return repayRecordTime;
    }

    public void setRepayRecordTime(String repayRecordTime) {
        this.repayRecordTime = repayRecordTime;
    }

    public List<RepayBean> getRepayRecordList() {
        return repayRecordList;
    }

    public void setRepayRecordList(List<RepayBean> repayRecordList) {
        this.repayRecordList = repayRecordList;
    }
}
