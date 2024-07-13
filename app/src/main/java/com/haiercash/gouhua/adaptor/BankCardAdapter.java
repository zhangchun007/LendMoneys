package com.haiercash.gouhua.adaptor;

import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.beans.bankcard.QueryCardBean;
import com.haiercash.gouhua.view.BankCardView;

/**
 * Created by StarFall on 2016/5/27.
 * 银行卡列表adapter
 */
public class BankCardAdapter extends BaseAdapter<QueryCardBean, ViewHolder> {

    public BankCardAdapter() {
        super(R.layout.listview_my_creditcard_item);
    }

    @Override
    protected void convert(ViewHolder holder, QueryCardBean item) {
        BankCardView bankCardView = holder.getView(R.id.bankcardview);
        bankCardView.updateView(item.getBankName(), item.getCardNo(), item.getCardTypeName());
        bankCardView.initStation(item.getBankCardSupport(), item.getSignStatus(),false);
    }
}
