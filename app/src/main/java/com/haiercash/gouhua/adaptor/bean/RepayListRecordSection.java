package com.haiercash.gouhua.adaptor.bean;

import com.chad.library.adapter.base.entity.JSectionEntity;
import com.chad.library.adapter.base.entity.SectionEntity;
import com.haiercash.gouhua.beans.borrowmoney.RepayBean;

public class RepayListRecordSection extends JSectionEntity {
    public RepayBean t;
    public String header;
    public boolean isHeader;

    public RepayListRecordSection(boolean isHeader, String header) {
        this.header = header;
        this.isHeader = isHeader;
    }

    public RepayListRecordSection(RepayBean repayBean) {
        this.t = repayBean;
    }

    @Override
    public boolean isHeader() {
        return isHeader;
    }
}
