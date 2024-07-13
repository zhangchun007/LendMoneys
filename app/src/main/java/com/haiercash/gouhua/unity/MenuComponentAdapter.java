package com.haiercash.gouhua.unity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.unity.MenusBean;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/8
 * @Version: 1.0
 */
public class MenuComponentAdapter extends BaseQuickAdapter<MenusBean, ViewHolder> {
    private Map<String, Object> mPersonMap;

    public MenuComponentAdapter(@Nullable List<MenusBean> data) {
        super(R.layout.item_menu_component, data);
    }

    @Override
    protected void convert(@NonNull ViewHolder viewHolder, MenusBean item) {
        TextView tv_tab_name = viewHolder.getView(R.id.tv_tab_name);
        TextView tv_bubble = viewHolder.getView(R.id.tv_bubble);
        if (ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(item.getPop().getDefaultShow(), mPersonMap))) {
            tv_bubble.setVisibility(View.VISIBLE);
            tv_bubble.setText(ReplaceHolderUtils.replaceKeysWithValues(item.getPop().getPopText(), mPersonMap));
        } else {
            tv_bubble.setVisibility(View.GONE);
        }
        ImageView img_lab = viewHolder.getView(R.id.img_lab);
        tv_tab_name.setText(ReplaceHolderUtils.replaceKeysWithValues(item.getLabel(), mPersonMap));
        String imgUrl=ReplaceHolderUtils.replaceKeysWithValues(item.getIcon(), mPersonMap);
        ImageLoader.loadImage(getContext(), imgUrl, img_lab, R.drawable.img_default_tools);
    }

    /**
     * 接受业务数据
     */
    public void setPersonCenterData(Map<String, Object> personMap) {
        this.mPersonMap = personMap;
    }
}
