package com.haiercash.gouhua.adaptor.bill;

import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.repayment.AllRePay;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * Author: Sun
 * Date :    2018/6/15
 * FileName: AllBillAdapter
 * Description:
 */
public class AllBillAdapter extends BaseAdapter<AllRePay, ViewHolder> {

    public AllBillAdapter() {
        super(R.layout.item_peroid_bills);
    }

    @Override
    protected void convert(ViewHolder helper, AllRePay item) {
        helper.setGone(R.id.line_bottom, getDefItemCount() - 1 == getItemPosition(item));
        helper.setText(R.id.tv_periods_pay, "待还总额");
        helper.setBackgroundResource(R.id.cb_select, R.drawable.cb_check_selector);
        helper.setChecked(R.id.cb_select, item.isSelected());
        helper.setText(R.id.tv_amount, item.getStayAmount());
        helper.setGone(R.id.tvCouponUseDis, !item.isUseCoupon() || CheckUtil.isEmpty(item.getDiscRepayAmt()))
                .setText(R.id.tvCouponUseDis, UiUtil.getStr("优惠后¥", item.getDiscRepayAmt()));
        helper.setText(R.id.tv_bill_details, item.getApplyDt() + " 借"
                + item.getPrcpAmt() + "元  共"
                + item.getApprvTnr() + "期");

        helper.setGone(R.id.tv_repaymenting, true);
        if ("01".equals(item.getLoanRepayStatus())) {//还款处理中
            helper.setTextColor(R.id.tv_last_time, ContextCompat.getColor(mContext, R.color.color_909199));
            helper.setText(R.id.tv_last_time, "剩余" + item.getUnRepayedNum() + "期");
            helper.getView(R.id.cb_select).setEnabled(false);
            helper.setGone(R.id.tv_repaymenting, false);
        } else if (!"normal".equals(item.getLoanTyp())) {
            helper.setTextColor(R.id.tv_last_time, Color.parseColor("#FFFF3202"));
            helper.setText(R.id.tv_last_time, "已逾期");
            helper.getView(R.id.cb_select).setEnabled(true);
        } else {
            helper.setTextColor(R.id.tv_last_time, ContextCompat.getColor(mContext, R.color.color_909199));
            helper.setText(R.id.tv_last_time, "剩余" + item.getUnRepayedNum() + "期");
            helper.getView(R.id.cb_select).setEnabled(true);
        }
    }
}
