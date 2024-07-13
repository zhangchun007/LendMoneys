package com.haiercash.gouhua.unity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;


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
 * @CreateDate: 2023/11/8
 * @Version: 1.0
 */
public class JinGComponentAdapter extends BaseQuickAdapter<MenusBean, ViewHolder> {
    private Map<String, Object> mPersonMap;

    public JinGComponentAdapter(@Nullable List<MenusBean> data) {
        super(R.layout.item_jin_gang_component, data);
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, MenusBean item) {
        ConstraintLayout layout_root = viewHolder.getView(R.id.layout_root);
        int size = getData().size();
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) layout_root.getLayoutParams();
        if (size >= 4) {
            params.width = (UiUtil.getDeviceWidth() - UiUtil.dip2px(getContext(), 30)) / 4;
            params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        } else {
            params.width = (UiUtil.getDeviceWidth() - UiUtil.dip2px(getContext(), 30)) / 3;
            params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        }
        layout_root.setLayoutParams(params);

        TextView tv_tab_name = viewHolder.getView(R.id.tv_tab_name);
        ImageView img_lab = viewHolder.getView(R.id.img_lab);
        tv_tab_name.setText(ReplaceHolderUtils.replaceKeysWithValues(item.getLabel(), mPersonMap));
        String imgUrl = ReplaceHolderUtils.replaceKeysWithValues(item.getIcon(), mPersonMap);
        ImageLoader.loadImage(getContext(), imgUrl, img_lab, R.drawable.img_jg_default);

        TextView tv_bubble = viewHolder.getView(R.id.tv_bubble);
        if (item.getPop() != null) {
            boolean isShowBubble = ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(item.getPop().getDefaultShow(), mPersonMap));
            if (isShowBubble) {
                tv_bubble.setVisibility(View.VISIBLE);
                tv_bubble.setText(ReplaceHolderUtils.replaceKeysWithValues(item.getPop().getPopText(), mPersonMap));
                if (!TextUtils.isEmpty(item.getPop().getPopStartColor()) && !TextUtils.isEmpty(item.getPop().getPopEndColor())) {
                    setBG(getContext(), tv_bubble, item.getPop().getPopStartColor(), item.getPop().getPopEndColor());
                }
            } else {
                tv_bubble.setVisibility(View.GONE);
            }
        }

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
                0F,//左下角
                0F,//左下角
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
