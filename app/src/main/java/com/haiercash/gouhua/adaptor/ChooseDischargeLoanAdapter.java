package com.haiercash.gouhua.adaptor;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.OpenDischargeBean;

public class ChooseDischargeLoanAdapter extends BaseAdapter<OpenDischargeBean.LoansBean, ViewHolder> {
    private OnItemCheckBoxClictListener onItemCheckBoxClictListener;

    public ChooseDischargeLoanAdapter() {
        super(R.layout.item_choose_discharge_loan);
    }

    @Override
    protected void convert(final ViewHolder helper, final OpenDischargeBean.LoansBean item) {
        helper.setText(R.id.tv_choose_discharge_loan_money, "￥" + CheckUtil.showNewThound(CheckUtil.formattedAmount(item.getOrigPrcp())));
        helper.setText(R.id.tv_choose_discharge_loan_data, item.getLastSetlDt());
        helper.setText(R.id.tv_choose_loan_data, item.getLoanActvDt());
        helper.setText(R.id.tv_loan_order_id, item.getLoanNo());
        helper.getView(R.id.tv_apply_btn).setOnClickListener(v -> {
            if (onItemCheckBoxClictListener != null) {
                onItemCheckBoxClictListener.checkBoxClick(item.getApplSeq());
            }
        });
    }

    public interface OnItemCheckBoxClictListener {
        void checkBoxClick(String applSeq);
    }

    public void setOnItemCheckBoxClictListener(OnItemCheckBoxClictListener onItemCheckBoxClictListener) {
        this.onItemCheckBoxClictListener = onItemCheckBoxClictListener;
    }
}
