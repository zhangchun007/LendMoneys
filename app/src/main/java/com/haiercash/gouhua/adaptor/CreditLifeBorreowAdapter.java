package com.haiercash.gouhua.adaptor;

import android.widget.TextView;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.CreditLifeBorrowBean;
import com.haiercash.gouhua.utils.GlideUtils;

public class CreditLifeBorreowAdapter extends BaseAdapter<CreditLifeBorrowBean, ViewHolder> {
    public CreditLifeBorreowAdapter() {
        super(R.layout.item_credit_life_borrow);
    }

    @Override
    protected void convert(ViewHolder helper, CreditLifeBorrowBean item) {
        helper.setText(R.id.tv_credit_name, item.getChannelName());
        helper.setText(R.id.tv_credit_apply_num, (CheckUtil.isEmpty(item.getApplyCount()) ? 0 : item.getApplyCount()) + "人申请");
        helper.setText(R.id.tv_credit_time, CheckUtil.isEmpty(item.getTimeLimit()) ? "0" : item.getTimeLimit());
        helper.setText(R.id.tv_credit_rate, CheckUtil.isEmpty(item.getRateInterest()) ? "0" : item.getRateInterest());
        helper.setText(R.id.tv_credit_start, item.getEduAmt().replace("，", ""));
        TextView tv_credit_name = helper.getView(R.id.tv_credit_name);
        tv_credit_name.setTypeface(FontCustom.getMediumFont(mContext));
        TextView tv_credit_start = helper.getView(R.id.tv_credit_start);
        tv_credit_start.setTypeface(FontCustom.getMediumFont(mContext));
        GlideUtils.loadCenterCrop(mContext, helper.getView(R.id.iv_credit), ApiUrl.urlAdPic + item.getImageAddress(), R.drawable.iv_head_img);
        if (CheckUtil.isEmpty(item.getChannelIntro())) {
            helper.setGone(R.id.tv_credit_des1, true);
            helper.setGone(R.id.tv_credit_des2, true);
            helper.setGone(R.id.tv_credit_des3, true);
            helper.setGone(R.id.tv_credit_des4, true);
        } else if (item.getChannelIntro().contains("+")) {
            String[] channelIntos = item.getChannelIntro().split("\\+");
            if (channelIntos.length == 2) {
                helper.setGone(R.id.tv_credit_des1, false);
                helper.setGone(R.id.tv_credit_des2, false);
                helper.setGone(R.id.tv_credit_des3, true);
                helper.setGone(R.id.tv_credit_des4, true);
                helper.setText(R.id.tv_credit_des1, channelIntos[0]);
                helper.setText(R.id.tv_credit_des2, channelIntos[1]);
            } else if (channelIntos.length == 3) {
                helper.setGone(R.id.tv_credit_des1, false);
                helper.setGone(R.id.tv_credit_des2, false);
                helper.setGone(R.id.tv_credit_des3, false);
                helper.setGone(R.id.tv_credit_des4, true);
                helper.setText(R.id.tv_credit_des1, channelIntos[0]);
                helper.setText(R.id.tv_credit_des2, channelIntos[1]);
                helper.setText(R.id.tv_credit_des3, channelIntos[2]);
            } else if (channelIntos.length == 4) {
                helper.setGone(R.id.tv_credit_des1, false);
                helper.setGone(R.id.tv_credit_des2, false);
                helper.setGone(R.id.tv_credit_des3, false);
                helper.setGone(R.id.tv_credit_des4, false);
                helper.setText(R.id.tv_credit_des1, channelIntos[0]);
                helper.setText(R.id.tv_credit_des2, channelIntos[1]);
                helper.setText(R.id.tv_credit_des3, channelIntos[2]);
                helper.setText(R.id.tv_credit_des4, channelIntos[3]);
            } else if (channelIntos.length == 1) {
                helper.setGone(R.id.tv_credit_des1, false);
                helper.setGone(R.id.tv_credit_des2, true);
                helper.setGone(R.id.tv_credit_des3, true);
                helper.setGone(R.id.tv_credit_des4, true);
                helper.setText(R.id.tv_credit_des1, item.getChannelIntro());
            } else {
                helper.setGone(R.id.tv_credit_des1, true);
                helper.setGone(R.id.tv_credit_des2, true);
                helper.setGone(R.id.tv_credit_des3, true);
                helper.setGone(R.id.tv_credit_des4, true);
            }
        } else {
            helper.setGone(R.id.tv_credit_des1, false);
            helper.setGone(R.id.tv_credit_des2, true);
            helper.setGone(R.id.tv_credit_des3, true);
            helper.setGone(R.id.tv_credit_des4, true);
            helper.setText(R.id.tv_credit_des1, item.getChannelIntro());
        }
    }
}
