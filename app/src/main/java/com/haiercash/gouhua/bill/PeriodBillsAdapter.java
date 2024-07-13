package com.haiercash.gouhua.bill;

import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.repayment.LoanBean;
import com.haiercash.gouhua.utils.UiUtil;

public class PeriodBillsAdapter extends BaseAdapter<LoanBean, ViewHolder> {

    PeriodBillsAdapter() {
        super(R.layout.item_peroid_bills);
    }

    @Override
    protected void convert(ViewHolder helper, LoanBean item) {
        helper.setGone(R.id.line_bottom, getDefItemCount() - 1 == getItemPosition(item));
        String loanTyp = item.getLoanTyp();
        helper.setText(R.id.tv_periods_pay, "第" + item.getPsPerdNo() + "期待还");
        helper.setText(R.id.tv_amount, item.getAmount());
        helper.setGone(R.id.tvCouponUseDis, !item.isUseCoupon() || CheckUtil.isEmpty(item.getDiscRepayAmt()))
                .setText(R.id.tvCouponUseDis, UiUtil.getStr("优惠后¥", item.getDiscRepayAmt()));
        if (!"normal".equals(loanTyp)) {
            helper.setText(R.id.tv_last_time, "逾期" + Math.abs(CheckUtil.mIntegerParseInt(item.getRemainDays())) + "天");
        } else {
            helper.setText(R.id.tv_last_time, "剩余" + item.getRemainDays() + "天");
        }
        helper.setText(R.id.tv_bill_details, item.getApplyDt() + " 借" + item.getPrcpAmt() + "元  共" + item.getApprvTnr() + "期");
        //重置控件状态
        helper.setChecked(R.id.cb_select, false);
        helper.setGone(R.id.tv_repaymenting, true);

        if ("01".equals(item.getRepayStatus())) {//还款处理中
            helper.setTextColor(R.id.tv_last_time, ContextCompat.getColor(mContext, R.color.color_909199));
            helper.getView(R.id.cb_select).setEnabled(false);
            helper.setGone(R.id.tv_repaymenting, false);
            return;
        }

        helper.setChecked(R.id.cb_select, item.isChecked());
        helper.getView(R.id.cb_select).setEnabled(item.isEnable());
        if (item.isEnable()) {
            if ("normal".equals(loanTyp)) {//尚未逾期
                helper.setTextColor(R.id.tv_last_time, ContextCompat.getColor(mContext, R.color.color_909199));
            } else {//已经逾期或不良
                helper.setTextColor(R.id.tv_last_time, Color.parseColor("#FFFF3202"));
            }
        } else {
            helper.setTextColor(R.id.tv_last_time, ContextCompat.getColor(mContext, R.color.color_909199));
        }
    }
}
