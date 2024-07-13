package com.haiercash.gouhua.beans.borrowmoney;

import java.util.List;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/8/16
 * 描    述：借款记录
 * 修订历史：
 * ================================================================
 */
public class BorrowRecordBen {
    private String applyMonth;
    private List<RecordBean> applyList;

    public String getApplyMonth() {
        return applyMonth;
    }

    public void setApplyMonth(String applyMonth) {
        this.applyMonth = applyMonth;
    }

    public List<RecordBean> getApplyList() {
        return applyList;
    }

    public void setApplyList(List<RecordBean> applyList) {
        this.applyList = applyList;
    }
}
