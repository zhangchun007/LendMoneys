package com.haiercash.gouhua.adaptor;

import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.DiscountCouponsBean;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.UiUtil;

public class DiscountCouponAdapter extends BaseQuickAdapter<DiscountCouponsBean.CouponsBean, ViewHolder> {

    public DiscountCouponAdapter() {
        super(R.layout.item_coupon);
    }

    @Override
    protected void convert(final ViewHolder holder, final DiscountCouponsBean.CouponsBean item) {
        holder.setText(R.id.tvLatinosName, item.latinosName);
        //COUPON:优惠券 MJQ满减券  (MJQ相当于自有，COUPON相当于三方券)
//        if (getItemPosition(item) == 0) {
//            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
//            lp.topMargin = UiUtil.dip2px(getContext(), 15);
//            holder.itemView.setLayoutParams(lp);
//        }
        holder.setTextColor(R.id.tvMoneyType, item.isShowVipUi() ? 0xFF94612D : 0xFFFF5151)
                .setTextColor(R.id.tvMoney, item.isShowVipUi() ? 0xFF94612D : 0xFFFF5151)
                .setBackgroundResource(R.id.tvUser, item.isShowVipUi() ? R.drawable.bg_discount_coupon_btn_vip : R.drawable.bg_discount_coupon_btn);
        TextView tvLatinosName = holder.findView(R.id.tvLatinosName);
        if (tvLatinosName != null) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.img_vip_tag);
            if (drawable != null) {
                drawable.setBounds(0, 0, UiUtil.dip2px(getContext(), 18), UiUtil.dip2px(getContext(), 17));
            }
            tvLatinosName.setCompoundDrawablesRelative(item.isShowVipUi() ? drawable : null, null, null, null);
        }
        if ("MJQ".equals(item.latinosType)) {
            String date = "";
            if (!CheckUtil.isEmpty(item.latinosStartDt)) {
                date += TimeUtil.getNeedDate(item.latinosStartDt.replace("-", "."));
            }
            date += "-";
            if (!CheckUtil.isEmpty(item.latinosEndDt)) {
                date += TimeUtil.getNeedDate(item.latinosEndDt.replace("-", "."));
            }
            String condition;
            if (CheckUtil.isEmpty(item.usedCondition) || "0".equals(item.usedCondition)) {
                condition = "无门槛";
            } else {
                condition = "满" + item.usedCondition + "可用";
            }
            holder.setVisible(R.id.ivDiscountLogo, false)//不显示，但是要占位
                    .setBackgroundResource(R.id.vBg, item.isShowVipUi() ? R.drawable.bg_item_coupon_vip : R.drawable.bg_item_coupon)
                    .setGone(R.id.llBottom, false)
                    .setVisible(R.id.llMoneyContent, true)
                    .setText(R.id.tvCouponType, condition)
                    .setText(R.id.tvLatinosDes, date);
            String money = item.latinosAmt;
            try {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(money);
                spannableStringBuilder.setSpan(new AbsoluteSizeSpan((int) UiUtil.sp2px(getContext(), 12)), money.indexOf("."), money.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                holder.setText(R.id.tvMoney, spannableStringBuilder);
            } catch (Exception e) {
                holder.setText(R.id.tvMoney, money);
            }
            holder.getView(R.id.vBg).getLayoutParams().height = UiUtil.dip2px(getContext(), 74);
            holder.getView(R.id.rlTop).getLayoutParams().height = UiUtil.dip2px(getContext(), 74);
        } else if ("COUPON".equals(item.latinosType)) {
            String date = "";
            if (!CheckUtil.isEmpty(item.latinosStartDt)) {
                date += TimeUtil.getNeedDate(item.latinosStartDt.replace("-", "."));
            }
            date += "-";
            if (!CheckUtil.isEmpty(item.latinosEndDt)) {
                date += TimeUtil.getNeedDate(item.latinosEndDt.replace("-", "."));
            }
            holder.setVisible(R.id.ivDiscountLogo, true)
                    .setBackgroundResource(R.id.vBg, item.isShowVipUi() ? R.drawable.bg_item_other_coupon_vip : R.drawable.bg_white_radius5)
                    .setGone(R.id.llBottom, true)
                    .setVisible(R.id.llMoneyContent, false)//不显示，但是要占位
                    .setText(R.id.tvCouponType, item.usedCondition)
                    .setText(R.id.tvLatinosDes, date);
            String money = item.latinosAmt;
            try {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(money);
                spannableStringBuilder.setSpan(new AbsoluteSizeSpan((int) UiUtil.sp2px(getContext(), 12)), money.indexOf("."), money.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                holder.setText(R.id.tvMoney, spannableStringBuilder);
            } catch (Exception e) {
                holder.setText(R.id.tvMoney, money);
            }
            //String imgUrl = "manage".equals(item.getDataFrom()) ? ApiUrl.urlAdPic + item.latinosIconPic : item.latinosIconPic;
//            GlideUtils.loadHeadPortrait(getContext(), item.latinosIconPic, holder.getView(R.id.ivDiscountLogo), false);
            GlideUtils.loadCircle(getContext(), holder.getView(R.id.ivDiscountLogo), item.latinosIconPic);
            holder.getView(R.id.vBg).getLayoutParams().height = UiUtil.dip2px(getContext(), 80);
            holder.getView(R.id.rlTop).getLayoutParams().height = UiUtil.dip2px(getContext(), 80);
        }
        //valid 生效、invalid 过期，used 已用
        if ("invalid".equals(item.state)) {
            holder.setGone(R.id.ivInvalidOverdue, false)
                    .setImageResource(R.id.ivInvalidOverdue, R.drawable.src_coupon_overdue)
                    .setGone(R.id.tvUser, true)
                    .getView(R.id.rlItemRoot).setAlpha(0.7F);
        } else if ("used".equals(item.state)) {
            holder.setGone(R.id.ivInvalidOverdue, false)
                    .setImageResource(R.id.ivInvalidOverdue, R.drawable.src_coupon_invalid)
                    .setGone(R.id.tvUser, true)
                    .getView(R.id.rlItemRoot).setAlpha(0.7F);
        } else {// ("valid".equals(item.state))
            holder.setGone(R.id.ivInvalidOverdue, true)
                    .setGone(R.id.tvUser, false)
                    .getView(R.id.rlItemRoot).setAlpha(1F);
        }
    }
//
//
//    public interface OnItemCheckBoxClickListener {
//        void checkBoxClick(int position, DiscountCouponsBean.CouponsBean bean);
//    }
//
//    public void setOnItemCheckBoxClickListener(OnItemCheckBoxClickListener onItemCheckBoxClickListener) {
//        this.onItemCheckBoxClickListener = onItemCheckBoxClickListener;
//    }
}
