package com.haiercash.gouhua.adaptor.homepage;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.bumptech.glide.Glide;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.homepage.ResidentRibbon;

/**
 * 首页金刚区适配器
 */

public class HomeKumgangAdapter extends BaseAdapter<ResidentRibbon, HomeKumgangAdapter.KumgangHolder> {


    public HomeKumgangAdapter() {
        super(R.layout.item_kumgang);
    }

    @Override
    protected void convert(KumgangHolder holder, ResidentRibbon bean) {
        holder.tvName.setText(bean.getText());
        Glide.with(getContext())
                .load(bean.getIcon())
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(holder.ivLogo);
        if (bean.getShowBubble() == 1 && !CheckUtil.isEmpty(bean.getBubbleText())) {
            holder.tvBubble.setVisibility(View.VISIBLE);
            holder.tvBubble.setText(bean.getBubbleText());
            try {
                GradientDrawable drawable = (GradientDrawable) holder.tvBubble.getBackground();
                int[] colors = {Color.parseColor(bean.getBubbleStartColor()), Color.parseColor(bean.getBubbleEndColor())};
                drawable.setColors(colors);
                holder.tvBubble.setBackground(drawable);
            } catch (Exception e) {
                Logger.e("HomeKumgangAdapter转换颜色异常");
            }

        } else {
            holder.tvBubble.setVisibility(View.GONE);
        }
    }

    protected static final class KumgangHolder extends ViewHolder {

        public ImageView ivLogo;
        public TextView tvBubble;
        public TextView tvName;

        public KumgangHolder(View v) {
            super(v);
            ivLogo = v.findViewById(R.id.iv_logo);
            tvBubble = v.findViewById(R.id.tv_bubble);
            tvName = v.findViewById(R.id.tv_name);
        }
    }

}
