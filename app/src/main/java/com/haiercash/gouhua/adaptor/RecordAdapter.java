package com.haiercash.gouhua.adaptor;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.bean.BorrowRecordSection;
import com.haiercash.gouhua.base.adapter.BaseSectionAdapter;
import com.app.haiercash.base.utils.system.CheckUtil;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/8/16
 * 描    述：借款记录分组->适配器
 * 修订历史：
 * ================================================================
 */
public class RecordAdapter extends BaseSectionAdapter<BorrowRecordSection, ViewHolder> {

    public RecordAdapter() {
        super(R.layout.item_borrow_record_head, R.layout.item_borrowing_record, null);
    }

    @Override
    protected void convertHeader(@NonNull ViewHolder holder, @NonNull BorrowRecordSection item) {
        if (item.header.contains("-")) {
            holder.setText(R.id.tvBorrowHeadTime, item.header.replace("-", "年") + "月");
        } else {
            if (item.header.startsWith("0")) {
                holder.setText(R.id.tvBorrowHeadTime, item.header.replace("0", "") + "月");
            } else {
                holder.setText(R.id.tvBorrowHeadTime, item.header + "月");
            }
        }
    }

    @Override
    protected void convert(ViewHolder helper, BorrowRecordSection item) {
        helper.setText(R.id.tv_1, "借款");
        helper.setText(R.id.tv_2, CheckUtil.isEmpty(item.t.getCommitTime()) ? item.t.getApplyDt() : item.t.getCommitTime());
        helper.setText(R.id.tv_borrow_money, item.t.getApplyAmt() + "元");
        if ("0".equals(item.t.getBorrowingApplStatus())) {
            if ("OD".equals(item.t.getIfSettled())) {
                helper.setText(R.id.tv_borrow_statue, "还款中");
                helper.setTextColor(R.id.tv_borrow_statue, ContextCompat.getColor(mContext, R.color.fc594c));
            } else if ("SE".equals(item.t.getIfSettled())) {
                helper.setText(R.id.tv_borrow_statue, "已结清");
                helper.setTextColor(R.id.tv_borrow_statue, ContextCompat.getColor(mContext, R.color.text_gray_light));
            } else if ("NS".equals(item.t.getIfSettled())) {
                helper.setText(R.id.tv_borrow_statue, "还款中");
                helper.setTextColor(R.id.tv_borrow_statue, ContextCompat.getColor(mContext, R.color.colorPrimary));
            }
        } else if ("1".equals(item.t.getBorrowingApplStatus())) {
            helper.setText(R.id.tv_borrow_statue, "审核中");
            helper.setTextColor(R.id.tv_borrow_statue, ContextCompat.getColor(mContext, R.color.bank_orange_end));
        } else {
            helper.setText(R.id.tv_borrow_statue, "借款失败");
            helper.setTextColor(R.id.tv_borrow_statue, ContextCompat.getColor(mContext, R.color.text_gray_light));
        }
    }


}
