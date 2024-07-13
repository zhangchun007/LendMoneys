package com.haiercash.gouhua.adaptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.repayment.ConList;

import java.util.List;

/**
 * 账单详情页弹窗协议列表
 */
public class ContractPopAdapter extends BaseAdapter<ConList, ViewHolder> {
    @SuppressWarnings("unchecked")
    public ContractPopAdapter(@Nullable Object data) {
        super(R.layout.item_pop_agreement, data instanceof List ? (List<ConList>) data : null);
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, ConList bean) {
        viewHolder.setText(R.id.tvAgreement, bean != null && !CheckUtil.isEmpty(bean.getDocDesc()) ? bean.getDocDesc() : "");
    }
}
