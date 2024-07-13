package com.haiercash.gouhua.adaptor.bean;

import com.chad.library.adapter.base.entity.JSectionEntity;
import com.haiercash.gouhua.beans.borrowmoney.RecordBean;

/**
 * 借款记录分组
 */
public class BorrowRecordSection extends JSectionEntity {
    public RecordBean t;
    public String header;
    public boolean isHeader;

    public BorrowRecordSection(boolean isHeader, String header) {
        this.isHeader = isHeader;
        this.header = header;
    }

    public BorrowRecordSection(RecordBean t) {
        this.t = t;
    }


    @Override
    public boolean isHeader() {
        return isHeader;
    }

}
