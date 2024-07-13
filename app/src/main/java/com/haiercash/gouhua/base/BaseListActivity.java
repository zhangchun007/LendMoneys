package com.haiercash.gouhua.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.ListRefreshHelper;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/1/15<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public abstract class BaseListActivity extends BaseActivity implements ListRefreshHelper.ListRefreshListener, OnItemClickListener {
    protected SwipeRefreshLayout mScrollRefreshLayout;
    protected RecyclerView mRecyclerView;

    protected ListRefreshHelper mRefreshHelper;
    protected BaseQuickAdapter mAdapter;

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        mScrollRefreshLayout = findViewById(R.id.srl_refresh);
        mRecyclerView = findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = getAdapter());
        mRefreshHelper = new ListRefreshHelper(this, mScrollRefreshLayout, mRecyclerView, this);
        mAdapter.setOnItemClickListener(this);
    }

    public abstract BaseQuickAdapter getAdapter();


    public void startRefresh(boolean isCanRefresh, boolean isFirstCanRefresh) {
        startRefresh(isCanRefresh, isFirstCanRefresh, false, -1, null);
    }

    public void startRefresh(boolean isCanRefresh, int resId, String emptyMessage) {
        startRefresh(isCanRefresh, true, true, resId, emptyMessage);
    }

    public void startRefresh(boolean isCanRefresh, boolean isFirstCanRefresh, boolean isNeedEmpty, int resId, String emptyMessage) {
        mRefreshHelper.build(isCanRefresh, isFirstCanRefresh);
        if (isNeedEmpty) {
            mRefreshHelper.setEmptyData(resId, emptyMessage);
        }
    }

    @Override
    public void loadSourceData(int page, int pageSize) {

    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        onItemClick(mAdapter.getItem(position));
    }

    public void onItemClick(Object item) {
        System.out.println(" BaseListActivity onItemClick->" + item);
    }

    public void setRefreshing(boolean refreshing) {
        mScrollRefreshLayout.setRefreshing(refreshing);
    }
}
