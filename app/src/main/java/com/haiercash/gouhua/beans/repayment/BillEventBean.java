package com.haiercash.gouhua.beans.repayment;

import java.util.List;

/**
 * 待还列表详情外层实体---埋点用
 */
public class BillEventBean {
    private List<BillEventDetail> notrepaydetail;

    public List<BillEventDetail> getNotrepaydetail() {
        return notrepaydetail;
    }

    public void setNotrepaydetail(List<BillEventDetail> notrepaydetail) {
        this.notrepaydetail = notrepaydetail;
    }
}
