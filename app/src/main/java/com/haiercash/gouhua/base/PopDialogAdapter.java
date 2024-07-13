package com.haiercash.gouhua.base;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.Coupon;
import com.haiercash.gouhua.utils.GlideUtils;

/**
 * Author: Sun<br/>
 * Date :    2019/4/19<br/>
 * FileName: PopDialogAdapter<br/>
 * Description:弹窗里面的权益Adapter
 */
public class PopDialogAdapter extends BaseAdapter<Coupon, ViewHolder> {
    private String buttonColor;

    public PopDialogAdapter(String buttonColor) {
        super(R.layout.item_dialog_rais);
        this.buttonColor = buttonColor;
    }

    @Override
    protected void convert(ViewHolder helper, Coupon item) {
        if ("COUPON".equals(item.getCouponType())) {//免息券
            helper.setGone(R.id.iv_logo, true);
            helper.setGone(R.id.ll_cash, false);
            helper.setGone(R.id.tv_condition, false);
            helper.setText(R.id.tv_rais, item.getNum());
            helper.setText(R.id.tv_condition, "免息券");
        } else if ("PLATFORM_COUPON".equals(item.getCouponType())) {//满减券
            String condition;
            if (CheckUtil.isEmpty(item.getCondition()) || "0".equals(item.getCondition())) {
                condition = "无门槛";
            } else {
                condition = "满" + item.getCondition() + "可用";
            }
            helper.setGone(R.id.iv_logo, true);
            helper.setGone(R.id.ll_cash, false);
            helper.setGone(R.id.tv_condition, false);
            helper.setText(R.id.tv_rais, item.getNum());
            helper.setText(R.id.tv_condition, condition);
        } else {
            helper.setGone(R.id.iv_logo, false);
            helper.setGone(R.id.ll_cash, true);
            helper.setGone(R.id.tv_rais, true);
            helper.setGone(R.id.tv_condition, true);
            GlideUtils.loadCircle(getContext(), helper.getView(R.id.iv_logo), item.getLogo());
        }
        helper.setText(R.id.tv_info, item.getTitle());
        helper.setText(R.id.tv_date, item.getValidDt());
        TextView tvGo = helper.getView(R.id.tv_go);
        GradientDrawable gd = (GradientDrawable) tvGo.getBackground();
        try {
            gd.setColor(Color.parseColor(buttonColor));
        } catch (Exception e) {
            Logger.e("PopDialogAdapter解析背景色失败");
        }

    }
}
