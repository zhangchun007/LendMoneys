package com.haiercash.gouhua.adaptor;

import android.content.Context;

import androidx.annotation.NonNull;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;

import java.util.Map;

public class CalculationAdapter extends BaseAdapter<Map<String, String>, ViewHolder> {
    private final String repayWay;

    public CalculationAdapter(Context context, String repayWay) {
        super(R.layout.item_calculation);
        this.repayWay = repayWay;
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, Map<String, String> map) {
        viewHolder.setText(R.id.tvPerNO, "第" + map.get("perNo") + "期");
        viewHolder.setText(R.id.tvAmt, map.get("preLoan"));
        viewHolder.setText(R.id.tvRem, "本金:" + map.get("principal")
                + ("1".equals(repayWay) ? " 分期利息:" : " 利息:") + map.get("interest"));
    }
}
