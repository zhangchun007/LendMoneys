package com.haiercash.gouhua.base;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
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
 * 创建日期：2018/6/14<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public abstract class BaseListFragment extends BaseFragment implements ListRefreshHelper.ListRefreshListener, OnItemClickListener {
    protected SwipeRefreshLayout mScrollRefreshLayout;
    protected RecyclerView mRecyclerView;

    protected ListRefreshHelper mRefreshHelper;
    protected BaseQuickAdapter mAdapter;

//    public BaseListFragment() {
//    }
//
//    public BaseListFragment(int contentLayoutId) {
//        super(contentLayoutId);
//    }

    @Override
    protected void initEventAndData() {
        mScrollRefreshLayout = mView.findViewById(R.id.srl_refresh);
        mRecyclerView = mView.findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter = getAdapter());
        mRecyclerView.setItemAnimator(null);
        mRefreshHelper = new ListRefreshHelper(mActivity, mScrollRefreshLayout, mRecyclerView, this);
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

    }

    protected void setRefreshing(boolean refreshing) {
        mScrollRefreshLayout.setRefreshing(refreshing);
    }

    /**
     * 设置是否允许下拉刷新
     *
     * @param refreshing false 不允许下拉刷新
     */
    protected void setRefreshEnable(boolean refreshing) {
        mScrollRefreshLayout.setEnabled(refreshing);
    }

    protected void setRefreshViewBg(int resId) {
        mScrollRefreshLayout.setBackgroundResource(resId);
        mRecyclerView.setBackgroundResource(resId);
    }

    protected void setRecyclerViewNestedScroll() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setFocusable(false);
    }
}