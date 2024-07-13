package com.haiercash.gouhua.adaptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.borrowmoney.LoanRat;
import com.haiercash.gouhua.beans.login.QueryAgreementListBean;

import java.util.List;

/**
 * 账单详情页弹窗协议列表
 */
public class RepayTypeAdapter extends BaseAdapter<LoanRat, ViewHolder> {
    @SuppressWarnings("unchecked")
    public RepayTypeAdapter(@Nullable Object data) {
        super(R.layout.item_repaytype_type, data instanceof List ? (List<LoanRat>) data : null);
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, LoanRat bean) {
        viewHolder.setText(R.id.tv_content, bean != null && !CheckUtil.isEmpty(bean.getMtdDesc()) ? bean.getMtdDesc() : "");
//        if (getData() != null && getData().size() > 0 && viewHolder.getLayoutPosition() == getData().size() - 1) {
//            viewHolder.setVisible(R.id.viewLine, false);
//        }
    }
}
