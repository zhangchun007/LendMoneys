package com.haiercash.gouhua.base.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.entity.SectionEntity;

import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/1/11<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public abstract class BaseSectionAdapter<T extends SectionEntity, K extends ViewHolder> extends BaseSectionQuickAdapter<T, K> {
    protected Context mContext;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param sectionHeadResId The section head layout id for each item
     * @param layoutResId      The layout resource id of each item.
     * @param data             A new list is created out of this one to avoid mutable list
     */
    public BaseSectionAdapter(int sectionHeadResId, int layoutResId, List<T> data) {
        super(sectionHeadResId, layoutResId, data);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        if (mContext == null) {
            mContext = recyclerView.getContext();
        }
        super.onAttachedToRecyclerView(recyclerView);
    }
}
