package com.haiercash.gouhua.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.FuncBeans;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 3/15/23
 * @Version: 1.0
 */
public class PersonJinGangAdapter extends BaseAdapter<FuncBeans, ViewHolder> {

    public PersonJinGangAdapter(@Nullable Object data) {
        super(R.layout.item_person_jingang, data instanceof List ? (List<FuncBeans>) data : null);
    }

    @Override
    protected void convert(ViewHolder viewHolder, FuncBeans funcBeans) {

        if (funcBeans != null) {
            if (!TextUtils.isEmpty(funcBeans.getItemAction())) {
                viewHolder.setVisible(R.id.img_default, false);
                viewHolder.setVisible(R.id.layout_item, true);
                viewHolder.setText(R.id.tv_tab_name, funcBeans.getText());
                //图标
                if (TextUtils.isEmpty(funcBeans.getIcon())) {
                    viewHolder.setImageResource(R.id.img_lab, R.drawable.img_jingang_default);
                } else {
                    ImageView iv = viewHolder.getView(R.id.img_lab);
                    GlideUtils.loadFit(mContext, iv, funcBeans.getIcon(), R.drawable.img_jingang_default);
                }
                //文本
                viewHolder.setText(R.id.tv_tab_name, funcBeans.getText());
                //气泡
                TextView tv_bubble = viewHolder.getView(R.id.tv_bubble);
                if (1 == funcBeans.getShowBubble() && !TextUtils.isEmpty(funcBeans.getBubbleText())) {
                    tv_bubble.setVisibility(View.VISIBLE);
                    tv_bubble.setText(funcBeans.getBubbleText());
                    setBG(getContext(), tv_bubble, funcBeans.getBubbleStartColor(), funcBeans.getBubbleEndColor());
                } else {
                    tv_bubble.setVisibility(View.GONE);
                }
            } else {
                viewHolder.setVisible(R.id.img_default, true);
                viewHolder.setVisible(R.id.layout_item, false);
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
}
