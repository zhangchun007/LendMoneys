package com.haiercash.gouhua.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.loadmore.BaseLoadMoreView;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.haiercash.gouhua.R;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 6/13/23
 * @Version: 1.0
 */
public class CustomLoadMoreView extends BaseLoadMoreView {
    @Override
    public View getLoadComplete(BaseViewHolder holder) {
        return holder.findView(R.id.load_more_load_complete_view);
    }

    @Override
    public View getLoadEndView(BaseViewHolder holder) {
        return holder.findView(R.id.load_more_load_end_view);
    }

    @Override
    public View getLoadFailView(BaseViewHolder holder) {
        return holder.findView(R.id.load_more_load_fail_view);
    }

    @Override
    public View getLoadingView(BaseViewHolder holder) {
        return holder.findView(R.id.load_more_loading_view);
    }

    @Override
    public View getRootView(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.view_load_more, parent, false);
    }
}
