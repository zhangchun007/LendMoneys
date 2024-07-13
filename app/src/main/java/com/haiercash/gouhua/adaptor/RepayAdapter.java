package com.haiercash.gouhua.adaptor;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.homepage.HomeRepayBean;

/**
 * 消息中心列表还款提醒和申请进度消息适配器
 */

public class RepayAdapter extends BaseQuickAdapter<HomeRepayBean, ViewHolder> {


    public RepayAdapter() {
        super(R.layout.item_repay_list);
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, HomeRepayBean homeRepayBean) {
        //设置标题
        ((TextView) viewHolder.getView(R.id.tv_name)).setText(homeRepayBean.getTitle());
        //设置内容
        ((TextView) viewHolder.getView(R.id.tv_amount)).setText(homeRepayBean.getRepayAmt());
        ((TextView) viewHolder.getView(R.id.tv_date)).setText(homeRepayBean.getLastDay());
        if (CheckUtil.isZero(homeRepayBean.getRepayAmt())) {
            viewHolder.getView(R.id.iv_arrow).setVisibility(View.VISIBLE);
            viewHolder.getView(R.id.tv_repay).setVisibility(View.GONE);
        } else {
            viewHolder.getView(R.id.iv_arrow).setVisibility(View.GONE);
            viewHolder.getView(R.id.tv_repay).setVisibility(View.VISIBLE);
        }
    }

/*
    public static final class RepayHolder extends BaseViewHolder {

        public TextView tvName;
        public TextView tvAmount;
        public TextView tvDate;
        public TextView tvRepay;
        public TextView ivArrow;

        public RepayHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_name);
            tvAmount = v.findViewById(R.id.tv_amount);
            tvDate = v.findViewById(R.id.tv_date);
            tvRepay = v.findViewById(R.id.tv_repay);
            ivArrow = v.findViewById(R.id.iv_arrow);

        }
    }
*/

}