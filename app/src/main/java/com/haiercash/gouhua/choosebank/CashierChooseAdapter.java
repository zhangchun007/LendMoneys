package com.haiercash.gouhua.choosebank;

import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.beans.bankcard.QueryCardBean;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.List;

/**
 * Author: Sun<br/>
 * Date :    2018/1/10<br/>
 * FileName: CashierChooseAdapter<br/>
 * Description:<br/>
 */
public class CashierChooseAdapter extends BaseAdapter<QueryCardBean, ViewHolder> {

    CashierChooseAdapter(List<QueryCardBean> data) {
        super(R.layout.item_recycleview_bankcard, data);
    }

    @Override
    protected void convert(ViewHolder holder, QueryCardBean cardBean) {
        holder.setText(R.id.tv_name_bankcard, cardBean.bankName + "(" + cardBean.cardNo.substring(cardBean.cardNo.length() - 4) + ")");
        if (CheckUtil.isEmpty(cardBean.singleCollLimited) || "-1".equals(cardBean.singleCollLimited) || "-1.0".equals(cardBean.singleCollLimited)) {
            cardBean.setSingleCollLimited("20000");
        }
        holder.setText(R.id.tv_limit_bankcard, "单笔限额" + CheckUtil.showThounds(String.valueOf(cardBean.singleCollLimited)) + "元");
        ((ImageView) holder.getView(R.id.iv_icon_bankcard)).setImageResource(UiUtil.getBankCardImageRes(cardBean.bankName));
        holder.setImageResource(R.id.iv_choose, cardBean.isChoosed() ? R.drawable.icon_select_cashier_true : R.drawable.cb_single_enable72);//icon_select_cashier_false
        View view = holder.getView(R.id.ll_root);
        if (!cardBean.isEnable()) {
            view.setBackgroundColor(ContextCompat.getColor(view.getContext(),R.color.divider));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(view.getContext(),R.color.white));
        }
    }

}
