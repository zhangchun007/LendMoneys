package com.haiercash.gouhua.unity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.unity.MenusBean;
import com.haiercash.gouhua.utils.UiUtil;


import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/15
 * @Version: 1.0
 */
public class HRDataBoardAdapter extends BaseQuickAdapter<MenusBean, ViewHolder> {

    private Map<String, Object> mPersonMap;

    public HRDataBoardAdapter(@Nullable List<MenusBean> data) {
        super(R.layout.item_data_board_component, data);
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, MenusBean item) {
        TextView text_content = viewHolder.getView(R.id.text_content);
        //数量
        String content = ReplaceHolderUtils.replaceKeysWithValues(item.getContent(), mPersonMap);
        if (!TextUtils.isEmpty(content)) {
            text_content.setText(content);
        } else {
            text_content.setText("-");
        }
        TextView tv_bubble = viewHolder.getView(R.id.tv_bubble);
        if (ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(item.getPop().getDefaultShow(), mPersonMap))) {
            tv_bubble.setVisibility(View.VISIBLE);
            tv_bubble.setText(ReplaceHolderUtils.replaceKeysWithValues(item.getPop().getPopText(), mPersonMap));
            if (!TextUtils.isEmpty(item.getPop().getPopStartColor()) && !TextUtils.isEmpty(item.getPop().getPopEndColor())) {
                setBG(getContext(), tv_bubble, item.getPop().getPopStartColor(), item.getPop().getPopEndColor());
            }
        } else {
            tv_bubble.setVisibility(View.GONE);
        }
        TextView tv_tab_name = viewHolder.getView(R.id.tv_tab_name);
        tv_tab_name.setText(ReplaceHolderUtils.replaceKeysWithValues(item.getLabel(), mPersonMap));
    }

    /**
     * 设置气泡bg
     *
     * @param context
     * @param view
     * @param startColor
     * @param endColor
     */
    private void setBG(Context context, TextView view, String startColor, String endColor) {
        int colors[] = {Color.parseColor(startColor), Color.parseColor(endColor)};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);

        //顺时针
        float[] radius = {UiUtil.dip2px(context, 9F),//左上
                UiUtil.dip2px(context, 9F),//左上
                UiUtil.dip2px(context, 9F),//右上
                UiUtil.dip2px(context, 9F),//右上

                UiUtil.dip2px(context, 9F),
                UiUtil.dip2px(context, 9F),
                UiUtil.dip2px(context, 9F),//左下角
                UiUtil.dip2px(context, 9F),//左下角
        };

        gradientDrawable.setCornerRadii(radius);
        view.setBackgroundDrawable(gradientDrawable);
    }

    /**
     * 接受业务数据
     */
    public void setPersonCenterData(Map<String, Object> personMap) {
        this.mPersonMap = personMap;
    }

}
