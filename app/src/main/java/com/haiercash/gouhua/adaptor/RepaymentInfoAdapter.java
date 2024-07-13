package com.haiercash.gouhua.adaptor;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.getpayss.GetPaySsList;
import com.haiercash.gouhua.beans.getpayss.LoanCoupon;

import java.util.Calendar;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 7/27/22
 * @Version: 1.0
 */
public class RepaymentInfoAdapter extends BaseAdapter<GetPaySsList, ViewHolder> {


    private final LoanCoupon selectLoanCoupon;

    public RepaymentInfoAdapter(LoanCoupon selectLoanCoupon) {
        super(R.layout.item_repayment_info);
        this.selectLoanCoupon = selectLoanCoupon;
    }

    @Override
    protected void convert(ViewHolder viewHolder, GetPaySsList item) {
        if (item == null) return;
        int position = getItemPosition(item);
        Log.e("position", "position==" + position);
        View view1 = viewHolder.getView(R.id.view1);
        View view2 = viewHolder.getView(R.id.view2);
        View view3 = viewHolder.getView(R.id.view3);
        if (position == 0) {
            view1.setVisibility(View.VISIBLE);
            view2.setVisibility(View.GONE);
            view3.setVisibility(View.GONE);
        } else if (position == getDefItemCount() - 1) {
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
            view3.setVisibility(View.VISIBLE);
        } else {
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.VISIBLE);
            view3.setVisibility(View.GONE);
        }
        viewHolder.setText(R.id.tv_time, "第" + (position + 1) + "期");
        if (item.getData() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(item.getData());
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            viewHolder.setText(R.id.tv_date, month + "/" + day);
        }

        viewHolder.setText(R.id.tv_total_money, "¥" + CheckUtil.showThound(item.getInstmAmt()));
        viewHolder.setText(R.id.tv_capital_money, "本金:" + CheckUtil.showThound(item.prcpAmt));
        viewHolder.setText(R.id.tv_interest_money, "利息:" + CheckUtil.showThound(item.normInt));

        //免息券指定期数,如果为空，则默认显示在第一期
        if (isTheBestCouponCanUse() && !TextUtils.isEmpty(selectLoanCoupon.getDiscInstmAmt()) && (position + 1 + "").equals(CheckUtil.isEmpty(selectLoanCoupon.getCalVol()) ? "-1" : selectLoanCoupon.getCalVol())) {
            viewHolder.setText(R.id.tv_discount_tips, "（优惠后" + CheckUtil.showThound(selectLoanCoupon.getDiscInstmAmt()) + "）");
            viewHolder.setGone(R.id.tv_discount_tips, false);
        } else {
            viewHolder.setGone(R.id.tv_discount_tips, true);
        }
    }

    /**
     * 当前最优优惠券能否可用
     *
     * @return
     */
    private boolean isTheBestCouponCanUse() {
        if (selectLoanCoupon != null && "Y".equals(selectLoanCoupon.getCanUseState()))
            return true;
        return false;
    }
}
