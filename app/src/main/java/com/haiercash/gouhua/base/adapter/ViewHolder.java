package com.haiercash.gouhua.base.adapter;

import android.view.View;
import android.widget.Checkable;

import androidx.annotation.IdRes;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/3/16<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ViewHolder extends BaseViewHolder {

    public ViewHolder(View view) {
        super(view);
    }

    /**
     * Sets the checked status of a checkable.
     *
     * @param viewId  The view id.
     * @param checked The checked status;
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setChecked(@IdRes int viewId, boolean checked) {
        View view = getView(viewId);
        // View unable cast to Checkable
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(checked);
        }
        return this;
    }
}
