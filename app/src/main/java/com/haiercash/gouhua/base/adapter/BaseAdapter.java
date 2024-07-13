package com.haiercash.gouhua.base.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>createBaseViewHolder
 * 创建日期：2018/1/8<br/>
 * 描    述：基本的适配器<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public abstract class BaseAdapter<T, K extends ViewHolder> extends BaseQuickAdapter<T, K> {
    protected Context mContext;

    public BaseAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    public BaseAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        if (mContext == null) {
            mContext = recyclerView.getContext();
        }
        super.onAttachedToRecyclerView(recyclerView);
    }
}
