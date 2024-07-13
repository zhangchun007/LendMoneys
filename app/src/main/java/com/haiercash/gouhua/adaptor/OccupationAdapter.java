package com.haiercash.gouhua.adaptor;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.ModelBean;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.List;

/**
 * 职业选择弹窗
 */
public class OccupationAdapter extends BaseQuickAdapter<ModelBean, ViewHolder> {
    private String selectName;

    public OccupationAdapter(List<ModelBean> list) {
        super(R.layout.item_occupation, list);
    }

    public void setSelectName(String selectName) {
        this.selectName = selectName;
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, ModelBean bean) {
        TextView tvOccupation = viewHolder.findView(R.id.tvOccupation);
        if (tvOccupation != null) {
            viewHolder.setText(R.id.tvOccupation, bean != null ? bean.name : "");
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_gou);
            int w = UiUtil.dip2px(getContext(), 12);
            if (drawable != null) {
                drawable.setBounds(0, 0, w, w);
            }
            tvOccupation.setCompoundDrawables(null, null, bean != null && !CheckUtil.isEmpty(selectName) && TextUtils.equals(selectName, bean.name) ? drawable : null, null);
        }
    }
}
