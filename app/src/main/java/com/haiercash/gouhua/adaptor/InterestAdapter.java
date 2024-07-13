package com.haiercash.gouhua.adaptor;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.InterestFreeBean;
import com.haiercash.gouhua.bill.BillDetailsActivity;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * ================================================================
 * 作    者：L14-14<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2021/12/17-17:52<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class InterestAdapter extends BaseQuickAdapter<InterestFreeBean.RepayCouponsBean, ViewHolder> {
    private boolean isChoose = false;//是否需要有选中选项
    private boolean isFromMine = false;//是否从个人中心券包列表来

    public InterestAdapter() {
        this(true, false);
    }

    public InterestAdapter(boolean isChoose) {
        this(false, isChoose);
    }

    public InterestAdapter(boolean isFromMine, boolean isChoose) {
        super(R.layout.item_coupon);
        this.isFromMine = isFromMine;
        this.isChoose = isChoose;
    }

    @Override
    protected void convert(@NonNull ViewHolder holder, InterestFreeBean.RepayCouponsBean item) {
        if (item == null) {
            return;
        }
        String date = "";
        if (!CheckUtil.isEmpty(item.getValidStartDt())) {
            date += TimeUtil.getNeedDate(item.getValidStartDt().replace("-", "."));
        }
        date += "-";
        if (!CheckUtil.isEmpty(item.getValidEndDt())) {
            date += TimeUtil.getNeedDate(item.getValidEndDt().replace("-", "."));
        }

        //通用
        holder.setBackgroundResource(R.id.vBg, item.isShowVipUi() ? R.drawable.bg_item_coupon_vip : R.drawable.bg_item_coupon)
                .setVisible(R.id.ivDiscountLogo, false)
                .setTextColor(R.id.tvMoneyType, item.isShowVipUi() ? 0xFF94612D : 0xFFFF5151)
                .setGone(R.id.tvMoneyType, CheckUtil.isEmpty(item.getKindSign()))
                .setTextColor(R.id.tvMoney, item.isShowVipUi() ? 0xFF94612D : 0xFFFF5151)
                //.setText(R.id.tvCouponType, isFromMine || item.isKindSeven() ? "免息券" : "")
                .setText(R.id.tvLatinosName, CheckUtil.isEmpty(item.getBatchDesc()) ? "" : item.getBatchDesc())
                .setText(R.id.tvLatinosDes, date);

        if (isFromMine) {
            holder.setText(R.id.tvCouponType, "免息券");
            holder.setTextColor(R.id.tvCouponType, getContext().getResources().getColor(R.color.color_606166));
        } else if (item.isKindSeven()) {
            holder.setText(R.id.tvCouponType, "免息");
            holder.setTextColor(R.id.tvCouponType, getContext().getResources().getColor(R.color.color_ff5151));
        }
        holder.setGone(R.id.tvCouponType, !isFromMine && !item.isKindSeven());

        String kindSign = item.getKindSign();//货币符号
        holder.setText(R.id.tvMoneyType, kindSign);

        String kindValue = CheckUtil.isEmpty(kindSign) ? item.getKindVal() + item.getKindUnit() : item.getKindVal();//数值+单位
        String money = !CheckUtil.isEmpty(kindValue) ? kindValue : item.getShowParValue();//兜底方案，若新的没有值则使用旧的
        try {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(money);
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan((int) UiUtil.sp2px(getContext(), 12)), money.indexOf("."), money.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.setText(R.id.tvMoney, spannableStringBuilder);
        } catch (Exception e) {
            if (money.contains("天")) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(money);
                spannableStringBuilder.setSpan(new AbsoluteSizeSpan((int) UiUtil.sp2px(getContext(), 24)), money.indexOf("天"), money.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                holder.setText(R.id.tvMoney, spannableStringBuilder);
            } else {
                holder.setText(R.id.tvMoney, money);
            }
        }

        TextView tvLatinosName = holder.findView(R.id.tvLatinosName);
        if (tvLatinosName != null) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.img_vip_tag);
            if (drawable != null) {
                drawable.setBounds(0, 0, UiUtil.dip2px(getContext(), 18), UiUtil.dip2px(getContext(), 17));
            }
            tvLatinosName.setCompoundDrawablesRelative(item.isShowVipUi() ? drawable : null, null, null, null);
        }
        //是否已绑定区分UI
        holder.setVisible(R.id.ivLeftTopFlag, item.hasBind());
        //已绑定显示"限第*期使用"等
        TextView tvLatinosDes = holder.findView(R.id.tvLatinosDes);
        if (!TextUtils.isEmpty(item.getBindPerNoMsg())) {
            if (isFromMine) {
                holder.setText(R.id.tv_cal, item.getBindPerNoMsg());
                holder.setGone(R.id.tv_cal, false);
                try {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvLatinosDes.getLayoutParams();
                    lp.setMargins(0, UiUtil.dip2px(getContext(), 2), 0, 0);
                    tvLatinosDes.setLayoutParams(lp);
                } catch (Exception e) {
                    Logger.e("InterestAdapter设置lp异常");
                }
            } else {
                holder.setText(R.id.tv_cal, "");
            }
            //不同状态与上间距不一样
            if (tvLatinosDes != null) {
                tvLatinosDes.setPaddingRelative(0, UiUtil.dip2px(getContext(), 2), 0, 0);
            }
        } else {
            //不同状态与上间距不一样
            if (tvLatinosDes != null) {
                tvLatinosDes.setPaddingRelative(0, UiUtil.dip2px(getContext(), 8), 0, 0);
            }
            holder.setGone(R.id.tv_cal, false);
        }
        //个人券包-券-查看绑定借款
        TextView tvLookBindLoan = holder.findView(R.id.tvLookBindLoan);
        if (isFromMine && item.hasBind() && tvLookBindLoan != null) {
            tvLookBindLoan.setVisibility(View.VISIBLE);
            tvLookBindLoan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), BillDetailsActivity.class);
                    intent.putExtra("applSeq", item.getBindApplSeq());
                    intent.putExtra("couponBindPerdNo", item.getBindPerdNo());
                    getContext().startActivity(intent);
                }
            });
        } else if (tvLookBindLoan != null) {
            tvLookBindLoan.setVisibility(View.GONE);
        }

        //还款半弹窗  ----不足使用条件
        TextView tvUnserviceableInfo = holder.findView(R.id.tvUnserviceableInfo);
        if ("N".equals(item.getCanUseState()) && tvUnserviceableInfo != null) {
            tvUnserviceableInfo.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(0.7f);
            if (!isFromMine) {
                holder.setText(R.id.tv_rule, TextUtils.isEmpty(item.getUnUseDesc()) ? getContext().getString(R.string.can_not_use_overdue) : item.getUnUseDesc());
            }
        } else if (tvUnserviceableInfo != null) {
            tvUnserviceableInfo.setVisibility(View.GONE);
            if (!isFromMine) {
                SpannableStringBuilder info = SpannableStringUtils.getBuilder(getContext(), "本笔可减")
                        .append(item.getDiscValue()).setBold()
                        .append("元").create();
                holder.setText(R.id.tv_rule, info);
            }
        }
        //tvUser按钮
        if (item.hasBind()) {
            holder.setText(R.id.tvUser, "去还款")
                    .setBackgroundResource(R.id.tvUser, item.isShowVipUi() ? R.drawable.bg_discount_coupon_btn_vip : R.drawable.bg_discount_coupon_btn);
        } else if ("2".equals(item.getSubState())) {
            holder.setText(R.id.tvUser, "未到使用期")
                    .setBackgroundResource(R.id.tvUser, item.isShowVipUi() ? R.drawable.bg_discount_coupon_btn_vip_alpha : R.drawable.bg_discount_coupon_btn_alpha);
        } else {
            holder.setText(R.id.tvUser, "去使用")
                    .setBackgroundResource(R.id.tvUser, item.isShowVipUi() ? R.drawable.bg_discount_coupon_btn_vip : R.drawable.bg_discount_coupon_btn);
        }
        holder.setGone(R.id.tvUser, !isFromMine && !"4".equals(item.getSubState()) && !"4A".equals(item.getSubState()) &&
                !"3A".equals(item.getSubState()) && !"2".equals(item.getSubState()));
        //券使用状态
        if ("3".equals(item.getState()) ||
                "3".equals(item.getSubState()) ||
                "3B".equals(item.getSubState()) ||
                "USED".equals(item.getState())) {
            //已使用
            holder.setGone(R.id.ivInvalidOverdue, false);
            holder.setImageResource(R.id.ivInvalidOverdue, R.drawable.src_coupon_invalid);
        } else if ("4".equals(item.getState()) ||
                "6".equals(item.getState()) ||
                "5".equals(item.getSubState()) ||
                "5A".equals(item.getSubState()) ||
                "5B".equals(item.getSubState()) ||
                "EXPIRED".equals(item.getState())) {
            //已过期
            holder.setGone(R.id.ivInvalidOverdue, false);
            holder.setImageResource(R.id.ivInvalidOverdue, R.drawable.src_coupon_overdue);
        } else {
            holder.setGone(R.id.ivInvalidOverdue, true);
        }
        //券即将到期标识
        if ("2".equals(item.getExpireState())) {//一天以内
            holder.setText(R.id.tvTipFlag, "今日到期");
            holder.setGone(R.id.tvTipFlag, false);
        } else if ("1".equals(item.getExpireState())) {//三天以内
            holder.setText(R.id.tvTipFlag, "即将到期");
            holder.setGone(R.id.tvTipFlag, false);
        } else {
            holder.setGone(R.id.tvTipFlag, true);
        }
        //是否需要选中
        holder.setGone(R.id.tvInterestChoose, !isChoose);
        if (item.isCheck()) {
            holder.setGone(R.id.tvInterestChoose, false);
            holder.setChecked(R.id.tvInterestChoose, true);
        } else {
            holder.setGone(R.id.tvInterestChoose, true);
        }
    }
}
