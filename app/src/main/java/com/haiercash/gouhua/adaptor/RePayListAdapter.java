package com.haiercash.gouhua.adaptor;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.bean.RepayListRecordSection;
import com.haiercash.gouhua.base.adapter.BaseSectionAdapter;
import com.app.haiercash.base.utils.system.CheckUtil;

public class RePayListAdapter extends BaseSectionAdapter<RepayListRecordSection, ViewHolder> {

    public RePayListAdapter() {
        super(R.layout.item_borrow_record_head, R.layout.item_borrowing_record, null);
    }

    @Override
    protected void convertHeader(@NonNull ViewHolder helper, @NonNull RepayListRecordSection item) {
        if (item.header.contains("-")) {
            helper.setText(R.id.tvBorrowHeadTime, item.header.replace("-", "年") + "月");
        } else {
            helper.setText(R.id.tvBorrowHeadTime, item.header + "月");
//            if(item.header.startsWith("0")){
//                helper.setText(R.id.tvBorrowHeadTime, item.header.replace("0","") + "月");
//            }else{
//                helper.setText(R.id.tvBorrowHeadTime, item.header + "月");
//            }
        }
    }

    @Override
    protected void convert(@NonNull ViewHolder helper, RepayListRecordSection item) {
        String time = item.t.getRepayFinalStatusTime();
        time = TimeUtil.formatCalendar(time, "yyyy-MM-dd HH:mm");
        if ("01".equals(item.t.getBizType())) {
            helper.setText(R.id.tv_borrow_money, CheckUtil.formattedAmount1(item.t.getAmount()) + "元");
            helper.setText(R.id.tv_1, "自动还款");
            helper.setText(R.id.tv_2, time);
            if ("FAIL".equals(item.t.getStatus())) {
                helper.setText(R.id.tv_borrow_statue, "失败");
                helper.setTextColor(R.id.tv_borrow_statue, ContextCompat.getColor(mContext, R.color.color_FF696A));
            } else if ("SUCCESS".equals(item.t.getStatus())) {
                helper.setText(R.id.tv_borrow_statue, "成功");
                helper.setTextColor(R.id.tv_borrow_statue, ContextCompat.getColor(mContext, R.color.color_01B2B4));
            } else {
                helper.setText(R.id.tv_borrow_statue, "还款中");
                helper.setTextColor(R.id.tv_borrow_statue, ContextCompat.getColor(mContext, R.color.colorPrimary));
            }
        } else if ("02".equals(item.t.getBizType())) {
            if ("FAIL".equals(item.t.getStatus())) {
                helper.setText(R.id.tv_borrow_statue, "失败");
                helper.setTextColor(R.id.tv_borrow_statue, ContextCompat.getColor(mContext, R.color.color_FF696A));
            } else if ("SUCCESS".equals(item.t.getStatus())) {
                helper.setText(R.id.tv_borrow_statue, "成功");
                helper.setTextColor(R.id.tv_borrow_statue, ContextCompat.getColor(mContext, R.color.color_01B2B4));
            } else {
                helper.setText(R.id.tv_borrow_statue, "还款中");
                helper.setTextColor(R.id.tv_borrow_statue, ContextCompat.getColor(mContext, R.color.colorPrimary));
            }
            helper.setText(R.id.tv_1, "主动还款");
            helper.setText(R.id.tv_borrow_money, "¥" + CheckUtil.formattedAmount1(item.t.getAmount()));
            helper.setText(R.id.tv_2, time);
        }
    }
}
