package com.haiercash.gouhua.adaptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.agreement.SmyAgreementBean;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.List;

/**
 * 协议组其他协议弹窗
 */
public class AgreementsPopAdapter extends BaseAdapter<SmyAgreementBean.AgreementBean, ViewHolder> {
    @SuppressWarnings("unchecked")
    public AgreementsPopAdapter(@Nullable Object data) {
        super(R.layout.item_pop_agreement, data instanceof List ? (List<SmyAgreementBean.AgreementBean>) data : null);
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, SmyAgreementBean.AgreementBean bean) {
        if (bean != null && !CheckUtil.isEmpty(bean.getName())) {
            if (!bean.getName().contains("《") && !bean.getName().contains("》")) {
                viewHolder.setText(R.id.tvAgreement, UiUtil.getStr("《", bean.getName(), "》"));
            } else {
                viewHolder.setText(R.id.tvAgreement, bean.getName());
            }
        } else {
            viewHolder.setText(R.id.tvAgreement, "");
        }
    }
}
