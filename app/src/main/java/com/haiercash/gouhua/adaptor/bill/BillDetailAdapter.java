package com.haiercash.gouhua.adaptor.bill;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.repayment.LmpmshdBean;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/1/9<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BillDetailAdapter extends BaseAdapter<LmpmshdBean, ViewHolder> {

    private final String couponBindPerdNo;//券绑定期数

    public BillDetailAdapter(String couponBindPerdNo) {
        super(R.layout.item_the_billing_details);
        this.couponBindPerdNo = couponBindPerdNo;
    }

    @Override
    protected void convert(@NonNull ViewHolder holder, LmpmshdBean item) {
        int position = getData().indexOf(item) + 1;
        String state = item.getSetlInd();
        String isYQ = item.getPsOdInd();
        String ssss = "";
        String discountValue = item.getDiscValue();
        if ("Y".equals(state)) {//结清标志
            ssss = "已还";
            holder.setTextColor(R.id.tvState, ContextCompat.getColor(mContext, R.color.text_gray));
            holder.setTextColor(R.id.tvTime, ContextCompat.getColor(mContext, R.color.text_gray));
            holder.setTextColor(R.id.tvTime1, ContextCompat.getColor(mContext, R.color.text_gray));
            holder.setTextColor(R.id.tvMoney, ContextCompat.getColor(mContext, R.color.text_gray));
            holder.setImageResource(R.id.iv_info, R.drawable.icon_tips);
            holder.setGone(R.id.tvCouponUseDesc, true);
            if (!CheckUtil.isZero(discountValue)) {
                holder.setGone(R.id.tv_discount_info, false);
                holder.setText(R.id.tv_discount_info, mContext.getString(R.string.disc_value, discountValue));
            }
        } else if ("Y".equals(isYQ)) {
            ssss = "逾期";
            holder.setTextColor(R.id.tvState, ContextCompat.getColor(mContext, R.color.fffc594c));
            holder.setTextColor(R.id.tvTime, ContextCompat.getColor(mContext, R.color.fffc594c));
            holder.setTextColor(R.id.tvTime1, ContextCompat.getColor(mContext, R.color.fffc594c));
            holder.setTextColor(R.id.tvMoney, ContextCompat.getColor(mContext, R.color.fffc594c));
            holder.setImageResource(R.id.iv_info, R.drawable.icon_red_tips);
            holder.setGone(R.id.tvCouponUseDesc, true);
        } else {
            ssss = "待还";
            holder.setTextColor(R.id.tvState, ContextCompat.getColor(mContext, R.color.text_gray_dark));
            holder.setTextColor(R.id.tvTime, ContextCompat.getColor(mContext, R.color.text_gray_dark));
            holder.setTextColor(R.id.tvTime1, ContextCompat.getColor(mContext, R.color.text_gray_dark));
            holder.setTextColor(R.id.tvMoney, ContextCompat.getColor(mContext, R.color.text_gray_dark));
            holder.setImageResource(R.id.iv_info, R.drawable.icon_tips);
            holder.setGone(R.id.tvCouponUseDesc, !TextUtils.equals(position + "", couponBindPerdNo));
            if (!CheckUtil.isZero(discountValue)) {
                holder.setGone(R.id.tv_discount_info, false);
                holder.setGone(R.id.tv_tag, false);
                holder.setText(R.id.tv_discount_info, mContext.getString(R.string.after_disc_value, discountValue));
            }
        }
        holder.setText(R.id.tvState, ssss);
        int size = getData().size();
        holder.setText(R.id.tvTime, position + "/" + size);
        holder.setText(R.id.tvTime1, item.getPsDueDt());
        holder.setText(R.id.tvMoney, CheckUtil.formattedAmount1(item.getPsInstmAmt()) + "元");
    }
}
