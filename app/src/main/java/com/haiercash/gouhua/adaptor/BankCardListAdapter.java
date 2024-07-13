package com.haiercash.gouhua.adaptor;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.beans.bankcard.BankcardBean;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/5/11<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BankCardListAdapter extends BaseAdapter<BankcardBean, ViewHolder> {

    public BankCardListAdapter() {
        super(R.layout.bankcard_item);
    }

    @Override
    protected void convert(ViewHolder holder, BankcardBean item) {
        holder.setText(R.id.tv_name, item.getBankName());
        if (CheckUtil.isEmpty(item.singleCollLimited) || "-1".equals(item.singleCollLimited) || "-1.0".equals(item.singleCollLimited)) {
            holder.setText(R.id.tv_money, "200000");
        } else {
            holder.setText(R.id.tv_money, String.valueOf(item.getSingleCollLimited()));
        }
        holder.setImageResource(R.id.icon_bank, UiUtil.getBankCardImageRes(item.getBankName()));
    }
}
