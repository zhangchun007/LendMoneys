package com.haiercash.gouhua.adaptor;

import androidx.annotation.NonNull;

import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.LoanBillsBean;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/6/30<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class LoanAssociationAdapter extends BaseAdapter<LoanBillsBean, ViewHolder> {

    public LoanAssociationAdapter() {
        super(R.layout.adapter_loan_association);
    }

    @Override
    protected void convert(@NonNull ViewHolder holder, LoanBillsBean bean) {
        String content = bean.getApplyDate() + "\n" + bean.getApplyAmount() + "\n";
        content += bean.getBankName() + "(" + bean.getCardNo().substring(bean.getCardNo().length() - 4) + ")";
        holder.setBackgroundResource(R.id.rl_content, "Y".equals(bean.getCardChangeFlag()) ? R.color.ffeaeaea : R.color.white)
                .setVisible(R.id.cb, "N".equals(bean.getCardChangeFlag()))
                .setText(R.id.tv_loan_no, "借据号：" + bean.getLoanNo())
                .setText(R.id.tv_more_content, content);
        holder.setGone(R.id.v_line, holder.getLayoutPosition() == 0);
        holder.setChecked(R.id.cb, bean.isChoose());
    }
}
