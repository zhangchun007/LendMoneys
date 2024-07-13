package com.haiercash.gouhua.adaptor;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.getpayss.LoanCoupon;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 免息券adapter
 */
public class PopLoanCouponAdapter extends BaseAdapter<LoanCoupon, ViewHolder> {
    private volatile LoanCoupon mCurrSelectCoupon;//当前选中
    private final String pageCode;
    private final OnCouponClickListener onCouponClickListener;

    public PopLoanCouponAdapter(LoanCoupon selectCoupon, String pageCode, OnCouponClickListener onCouponClickListener) {
        super(R.layout.item_coupon);
        this.mCurrSelectCoupon = selectCoupon;
        this.pageCode = pageCode;
        this.onCouponClickListener = onCouponClickListener;
    }

    public LoanCoupon getCurrSelectCoupon() {
        return mCurrSelectCoupon;
    }

    @Override
    protected void convert(@NonNull ViewHolder holder, LoanCoupon item) {
        if (item == null) {
            return;
        }
        holder.setText(R.id.tvLatinosName, item.getBatchDesc());
        String date = "";
        if (!CheckUtil.isEmpty(item.getBindStartDt())) {
            date += TimeUtil.getNeedDate(item.getBindStartDt().replace("-", "."));
        }
        date += "-";
        if (!CheckUtil.isEmpty(item.getBindEndDt())) {
            date += TimeUtil.getNeedDate(item.getBindEndDt().replace("-", "."));
        }
        holder.setBackgroundResource(R.id.vBg, item.isShowVipUi() ? R.drawable.bg_item_coupon_vip : R.drawable.bg_item_coupon)
                .setVisible(R.id.ivDiscountLogo, false)
                .setTextColor(R.id.tvMoneyType, item.isShowVipUi() ? 0xFF94612D : 0xFFFF5151)
                .setGone(R.id.tvMoneyType, item.isFixedValue())
                .setTextColor(R.id.tvMoney, item.isShowVipUi() ? 0xFF94612D : 0xFFFF5151)
                .setText(R.id.tvCouponType, item.getCouponTypeName())
                .setTextColor(R.id.tvCouponType, item.isKindSeven() ? 0xFFFF5151 : 0xFF606166)
                .setText(R.id.tvLatinosDes, date)
                .setGone(R.id.tvUser, true);
        holder.setGone(R.id.tvCouponType, !item.isKindSeven());
        String money = item.getShowParValue();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(money);
        if (money.contains("天")) {
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan((int) UiUtil.sp2px(getContext(), 24)), money.indexOf("天"), money.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.setText(R.id.tvMoney, spannableStringBuilder);
        } else if (money.contains("折")) {
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan((int) UiUtil.sp2px(getContext(), 24)), money.indexOf("折"), money.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.setText(R.id.tvMoney, spannableStringBuilder);
        } else {
            try {
                spannableStringBuilder.setSpan(new AbsoluteSizeSpan((int) UiUtil.sp2px(getContext(), 12)), money.indexOf("."), money.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                holder.setText(R.id.tvMoney, spannableStringBuilder);
            } catch (Exception e) {
                holder.setText(R.id.tvMoney, money);
            }
        }

        TextView tvLatinosName = holder.findView(R.id.tvLatinosName);
        if (tvLatinosName != null && item.isShowVipUi()) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.img_vip_tag);
            if (drawable != null) {
                drawable.setBounds(0, 0, UiUtil.dip2px(getContext(), 18), UiUtil.dip2px(getContext(), 17));
            }
            tvLatinosName.setCompoundDrawablesRelative(drawable, null, null, null);
        }
        holder.setGone(R.id.ivInvalidOverdue, true);
        holder.setGone(R.id.tvInterestChoose, !isChoose(item));
        holder.setChecked(R.id.tvInterestChoose, true);
        if ("2".equals(item.getExpireState())) {//一天以内
            holder.setText(R.id.tvTipFlag, "今日到期");
            holder.setGone(R.id.tvTipFlag, false);
        } else if ("1".equals(item.getExpireState())) {//三天以内
            holder.setText(R.id.tvTipFlag, "即将到期");
            holder.setGone(R.id.tvTipFlag, false);
        } else {
            holder.setGone(R.id.tvTipFlag, true);
        }
        //还款半弹窗  ----不足使用条件
        TextView tvUnserviceableInfo = holder.findView(R.id.tvUnserviceableInfo);
        if ("N".equals(item.getCanUseState()) && tvUnserviceableInfo != null) {
            tvUnserviceableInfo.setVisibility(View.VISIBLE);
            holder.setText(R.id.tv_rule, TextUtils.isEmpty(item.getUnUseDesc()) ? getContext().getString(R.string.can_not_use_overdue) : item.getUnUseDesc());

        } else if (tvUnserviceableInfo != null) {
            tvUnserviceableInfo.setVisibility(View.GONE);
            SpannableStringBuilder info = SpannableStringUtils.getBuilder(mContext, "本笔可减")
                    .append(item.getDiscValue()).setBold()
                    .append("元").create();
            holder.setText(R.id.tv_rule, info);

        }
        View btnRule = holder.findView(R.id.tv_rule);
        if (btnRule != null) {
            btnRule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postUmClickEvent("FreeCardDirection_Click", item, btnRule);
                    if (onCouponClickListener != null) {
                        onCouponClickListener.onRuleClick(v, item.getBatchDetailDesc());
                    }
                }
            });
        }
        View content = holder.findView(R.id.vBg);
        if (content != null) {
            content.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View v) {
                    if (mCurrSelectCoupon != null && isChoose(item)) {
                        mCurrSelectCoupon = null;
                    } else {
                        mCurrSelectCoupon = item;
                        postUmClickEvent("FreeCard_Click", item, null);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    private boolean isChoose(LoanCoupon loanCoupon) {
        return mCurrSelectCoupon != null && mCurrSelectCoupon.getCouponNo() != null && mCurrSelectCoupon.getCouponNo().equals(loanCoupon.getCouponNo());
    }

    private void postUmClickEvent(String clickEventId, LoanCoupon bean, View view) {
        if (bean == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "借款环节");
        map.put("free_card_No", UiUtil.getEmptyStr(bean.getBatchNo()));
        map.put("free_card_id", UiUtil.getEmptyStr(bean.getCouponNo()));
        map.put("free_card_type", UiUtil.getEmptyStr(bean.getKindName()));
        //具体可在哪一期使用
        map.put("free_card_certain_period", UiUtil.getEmptyStr(bean.getCalVol()));
        //可使用在最低总期数为多少的产品上使用（期数类型3期、6期等）
//        map.put("free_card_loan_periods", bean.getCalVol());
        String date = "";
        if (!CheckUtil.isEmpty(bean.getBindStartDt())) {
            date += TimeUtil.getNeedDate(bean.getBindStartDt());
        }
        date += "-";
        if (!CheckUtil.isEmpty(bean.getBindEndDt())) {
            date += TimeUtil.getNeedDate(bean.getBindEndDt());
        }
        map.put("free_card_timelimit", date);
        map.put("free_card_name", bean.getCouponTypeName());
        UMengUtil.commonClickEvent(clickEventId, view instanceof TextView ? ((TextView) view).getText().toString() : "", map, getPageCode());
    }

    private String getPageCode() {
        return this.pageCode;
    }

    public interface OnCouponClickListener {
        void onRuleClick(View view, String rule);
    }
}
