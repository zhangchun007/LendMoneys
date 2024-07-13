package com.haiercash.gouhua.adaptor.homepage;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.CreditLifeBorrowBean;
import com.haiercash.gouhua.utils.GlideUtils;

/**
 * 首页贷超适配器
 */

public class HomeLoanMarketAdapter extends BaseAdapter<CreditLifeBorrowBean, HomeLoanMarketAdapter.LoanMarketHolder> {


    public HomeLoanMarketAdapter() {
        super(R.layout.item_loan_market_layout);
    }

    @Override
    protected void convert(LoanMarketHolder holder, CreditLifeBorrowBean bean) {
        holder.tvTitle.setText(bean.getChannelName());
        holder.tvTotal.setText(bean.getEduAmt());
        holder.tvInfo.setText(bean.getRateInterest());
        GlideUtils.loadCircle(getContext(), holder.ivLogo, ApiUrl.urlAdPic + bean.getImageAddress());
    }

    public static final class LoanMarketHolder extends ViewHolder {

        public ImageView ivLogo;
        public TextView tvTitle;
        public TextView tvTotal;
        public TextView tvInfo;
        public TextView tvApply;

        public LoanMarketHolder(View v) {
            super(v);
            ivLogo = v.findViewById(R.id.iv_logo);
            tvTitle = v.findViewById(R.id.tv_title);
            tvTotal = v.findViewById(R.id.tv_total);
            tvInfo = v.findViewById(R.id.tv_info);
            tvApply = v.findViewById(R.id.tv_apply);

        }
    }

}
