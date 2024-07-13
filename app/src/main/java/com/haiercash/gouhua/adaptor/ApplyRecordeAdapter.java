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

public class ApplyRecordeAdapter extends BaseAdapter<CreditLifeBorrowBean, ViewHolder> {
    private boolean isHidden = false;

    public ApplyRecordeAdapter() {
        super(R.layout.item_apply_recorde);
    }

    public ApplyRecordeAdapter(boolean isHidden) {
        super(R.layout.item_apply_recorde);
        this.isHidden = isHidden;
    }

    @Override
    protected void convert(ViewHolder helper, CreditLifeBorrowBean item) {//贷款期限:
        helper.setText(R.id.tv_apply_name, item.getChannelName());
        helper.setText(R.id.tv_credit_apply_num, (CheckUtil.isEmpty(item.getApplyCount()) ? 0 : item.getApplyCount()) + "人已申请");
        helper.setText(R.id.tv_apply_rate, "额利率：" + (CheckUtil.isEmpty(item.getRateInterest()) ? "0" : item.getRateInterest()));
        helper.setText(R.id.tv_credit_date, "放款时间：" + (CheckUtil.isEmpty(item.getLoanDate()) ? "0" : item.getLoanDate()));
        helper.setText(R.id.tv_apply_time, "贷款期限：" + (CheckUtil.isEmpty(item.getTimeLimit()) ? "0" : item.getTimeLimit()));
        helper.setText(R.id.tv_credit_start, CheckUtil.isEmpty(item.getEduAmt()) ? "0" : item.getEduAmt().replace("，", ""));
        TextView tv_credit_start = helper.getView(R.id.tv_credit_start);
        tv_credit_start.setTypeface(FontCustom.getMediumFont(mContext));
        TextView tv_apply_name = helper.getView(R.id.tv_apply_name);
        tv_apply_name.setTypeface(FontCustom.getMediumFont(mContext));
        GlideUtils.loadFit(mContext, helper.getView(R.id.iv_apply_credit), ApiUrl.urlAdPic + item.getImageAddress());
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
        helper.setVisible(R.id.view_line, !isHidden);
    }
}
