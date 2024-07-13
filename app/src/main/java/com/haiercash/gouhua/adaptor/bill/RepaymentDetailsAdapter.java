package com.haiercash.gouhua.adaptor.bill;

import android.widget.TextView;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.getpayss.GetPaySsList;
import com.haiercash.gouhua.beans.getpayss.LoanCoupon;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * 还款计划
 */
public class RepaymentDetailsAdapter extends BaseAdapter<GetPaySsList, ViewHolder> {

    //注意新数据时需要手动初始化，保存的展开“收起来View”的位置，默认第一个位置初始展开
    private volatile int mCurrentOpenPosition = 0;
    private final LoanCoupon selectLoanCoupon;

    public RepaymentDetailsAdapter(LoanCoupon selectLoanCoupon) {
        super(R.layout.item_repayment_details);
        this.selectLoanCoupon = selectLoanCoupon;
    }

    public int getCurrentOpenPosition() {
        return mCurrentOpenPosition;
    }

    public void setCurrentOpenPosition(int mCurrentOpenPosition) {
        this.mCurrentOpenPosition = mCurrentOpenPosition;
    }

    @Override
    protected void convert(ViewHolder helper, GetPaySsList item) {
        int position = getItemPosition(item);
        helper.setGone(R.id.vBottomLine, position == getDefItemCount() - 1);
        helper.setText(R.id.tv_limit, "第" + (position + 1) + "期");
        helper.setGone(R.id.ll_cost, mCurrentOpenPosition != position && !item.isOpened);
        helper.setText(R.id.tv_repayment_date, item.getDueDt().replace(".", "-"));
        helper.setText(R.id.tv_repayment_money, CheckUtil.showThound(item.getInstmAmt()));
        helper.setText(R.id.tvRepaymentDetail, "本金:" + CheckUtil.showThound(item.prcpAmt) +
                "\u2000" + "利息:" + CheckUtil.showThound(item.normInt) +
                (CheckUtil.isZero(item.psFeeAmt) ? "" : "\u2000" + "手续费:" + CheckUtil.showThound(item.psFeeAmt)));
        TextView tvRepaymentDetail = helper.findView(R.id.tvRepaymentDetail);
        //免息券指定期数,如果为空，则默认显示在第一期
        if (selectLoanCoupon != null && (position + 1 + "").equals(CheckUtil.isEmpty(selectLoanCoupon.getCalVol()) ? "1" : selectLoanCoupon.getCalVol())) {
            if (tvRepaymentDetail != null) {
                tvRepaymentDetail.setPadding(0, 0, 0, 0);
            }
            helper.setText(R.id.tvRepaymentCouponTip2, "预计可优惠" + CheckUtil.showThound(selectLoanCoupon.getDiscValue()));
            helper.setGone(R.id.tvRepaymentCouponTip2, false);
        } else {
            if (tvRepaymentDetail != null) {
                tvRepaymentDetail.setPadding(0, 0, 0, UiUtil.dip2px(mContext, 6));
            }
            helper.setGone(R.id.tvRepaymentCouponTip2, true);
        }
    }
}
