package com.haiercash.gouhua.base.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/1/8<br/>
 * 描    述：多个layout分等级<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public abstract class BaseMultiItemAdapter<T extends MultiItemEntity, K extends ViewHolder> extends BaseMultiItemQuickAdapter<T, K> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public BaseMultiItemAdapter(List<T> data) {
        super(data);
    }
}
