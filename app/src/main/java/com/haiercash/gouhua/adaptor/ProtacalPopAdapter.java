package com.haiercash.gouhua.adaptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.login.QueryAgreementListBean;
import com.haiercash.gouhua.beans.repayment.ConList;

import java.util.List;

/**
 * 账单详情页弹窗协议列表
 */
public class ProtacalPopAdapter extends BaseAdapter<QueryAgreementListBean, ViewHolder> {
    @SuppressWarnings("unchecked")
    public ProtacalPopAdapter(@Nullable Object data) {
        super(R.layout.item_protacal_pop, data instanceof List ? (List<QueryAgreementListBean>) data : null);
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, QueryAgreementListBean bean) {
        viewHolder.setText(R.id.tv_content, bean != null && !CheckUtil.isEmpty(bean.getContName()) ? bean.getContName() : "");
        if (getData() != null && getData().size() > 0 && viewHolder.getLayoutPosition() == getData().size() - 1) {
            viewHolder.setVisible(R.id.viewLine, false);
        }
    }
}
