package com.haiercash.gouhua.adaptor;

import android.content.Context;
import android.util.TypedValue;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.view.CustomLoadMoreView;
import com.haiercash.gouhua.widget.ListEmptyOrErrorView;

import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/1/10<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ListRefreshHelper {
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private BaseQuickAdapter mAdapter;
    private ListEmptyOrErrorView mEmptyOrErrorView;

    private int mCurrentPage = 1;//请求页数
    private int PAGE_SIZE = 500;
    private boolean isLoadMore = false;

    private ListRefreshListener listRefreshListener;

    public ListRefreshHelper(Context mContext, SwipeRefreshLayout mSwipeRefreshLayout, RecyclerView mRecyclerView, ListRefreshListener listRefreshListener) {
        this.mContext = mContext;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
        this.mRecyclerView = mRecyclerView;
        this.listRefreshListener = listRefreshListener;
        mAdapter = (BaseQuickAdapter) mRecyclerView.getAdapter();
        if (mAdapter == null) {
            throw new RuntimeException("ListRefreshHelper cannot find RecyclerView's Adapter!");
        }
    }


    /**
     * 初始化对象
     *
     * @param isCanRefresh 是否允许下拉刷新和加载更多
     */
    public ListRefreshHelper build(boolean isCanRefresh) {
        return build(isCanRefresh, true);
    }

    /**
     * 初始化对象
     *
     * @param isCanRefresh 是否允许下拉刷新和加载更多
     */
    public ListRefreshHelper build(boolean isCanRefresh, boolean isFirstCanRefresh) {
        return init(isCanRefresh, isFirstCanRefresh);
    }

    /**
     * @param isCanRefresh      是否支持刷新
     * @param isFirstCanRefresh 是否支持第一次刷新----仅仅单纯控制mSwipeRefreshLayout的属性
     */
    private ListRefreshHelper init(boolean isCanRefresh, boolean isFirstCanRefresh) {
        initSwipeRefresh(mContext, mSwipeRefreshLayout);
        if (isCanRefresh) {
            mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
//            mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
//                @Override
//                public void onLoadMoreRequested() {
//                    loadMore();
//                }
//            }, mRecyclerView);
            if (mAdapter.getLoadMoreModule() != null) {
                mAdapter.getLoadMoreModule().setOnLoadMoreListener(this::loadMore);
                mAdapter.getLoadMoreModule().setLoadMoreView(new CustomLoadMoreView());
            }
        }
        if (isFirstCanRefresh) {
            mSwipeRefreshLayout.setRefreshing(isCanRefresh);
            mSwipeRefreshLayout.setEnabled(isCanRefresh);
        }
        refresh();
        return this;
    }

    /**
     * 加载更多
     */
    private void loadMore() {
        isLoadMore = true;
        listRefreshListener.loadSourceData(mCurrentPage, PAGE_SIZE);
    }

    /**
     * 刷新
     */
    private void refresh() {
        if (mAdapter.getLoadMoreModule() != null) {
            mAdapter.getLoadMoreModule().setEnableLoadMore(false);
            //mAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        }
        mCurrentPage = 1;
        isLoadMore = false;
        listRefreshListener.loadSourceData(mCurrentPage, PAGE_SIZE);
    }

    /**
     * 主动刷新
     */
    public void refreshActive() {
        refresh();
    }

    /**
     * 更新数据
     *
     * @param data 原始数据源
     */
    public <T> void updateData(List<T> data) {
        final int size = data == null ? 0 : data.size();
        updateData(data, size);
    }

    /**
     * 更新数据
     *
     * @param data 处理之后的数据源
     * @param size 原始数据源的大小
     */
    @SuppressWarnings("unchecked")
    public <T> void updateData(List<T> data, int size) {
        if (mAdapter == null) {
            return;
        }
        mCurrentPage++;
        if (isLoadMore) {
            if (size > 0) {
                mAdapter.addData(data);
            }
        } else {
            mAdapter.setNewData(data);
        }
        if (size < getPAGE_SIZE()) {
//            setLoadMoreEnd(isLoadMore);//第一页如果不够一页就显示没有更多数据布局
            if (mAdapter.getLoadMoreModule() != null) {
                mAdapter.getLoadMoreModule().loadMoreEnd();
            }
        } else {
            if (mAdapter.getLoadMoreModule() != null) {
                mAdapter.getLoadMoreModule().loadMoreComplete();
                //mAdapter.loadMoreComplete();
            }
        }
        if (!isLoadMore) {//刷新、下拉刷新重置
            if (mAdapter.getLoadMoreModule() != null) {
                mAdapter.getLoadMoreModule().setEnableLoadMore(true);
//                mAdapter.setEnableLoadMore(true);
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public int getPAGE_SIZE() {
        return PAGE_SIZE;
    }

    public void setPAGE_SIZE(int PAGE_SIZE) {
        this.PAGE_SIZE = PAGE_SIZE;
    }

    /**
     * 服务数据加载失败
     */
    public void errorData() {
        if (isLoadMore) {
            if (mAdapter.getLoadMoreModule() != null) {
                //mAdapter.loadMoreFail();
                mAdapter.getLoadMoreModule().loadMoreFail();
            }
        } else {
            if (mAdapter.getLoadMoreModule() != null) {
                //mAdapter.setEnableLoadMore(true);
                mAdapter.getLoadMoreModule().setEnableLoadMore(true);
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void setEmptyData(int resId) {
        setEmptyView();
        mEmptyOrErrorView.setEmptyData(resId);
    }

    public void setEmptyData(String msg) {
        setEmptyView();
        mEmptyOrErrorView.setEmptyData(msg);
    }

    public void setEmptyData(int resId, String msg) {
        setEmptyView();
        mEmptyOrErrorView.setEmptyData(resId, msg);
    }

    public void setEmptyViewBgResource(int resId) {
        mEmptyOrErrorView.setBgResource(resId);
    }

    public void setEmptyViewLineGone(boolean isGone) {
        mEmptyOrErrorView.setLineGone(isGone);
    }

    public ListEmptyOrErrorView getEmptyOrErrorView() {
        return mEmptyOrErrorView;
    }

    private void setEmptyView() {
        //if (mAdapter.getEmptyView() == null) {
        if (mAdapter.getEmptyLayout() == null) {
            mEmptyOrErrorView = new ListEmptyOrErrorView(mContext);
            mAdapter.setEmptyView(mEmptyOrErrorView);
        }
    }

    public void setLoadMoreEnd(boolean isLoadMore) {
        if (mAdapter.getLoadMoreModule() != null) {
            mAdapter.getLoadMoreModule().loadMoreEnd(isLoadMore);
        }
    }

    /**
     * 初始化SwipeRefreshLayout的样式
     */
    public static void initSwipeRefresh(Context mContext, SwipeRefreshLayout mSwipeRefreshLayout) {
        //mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        //设置进度动画的颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary); //没转一圈换一个颜色
//        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);//设置进度圈的大小，只有两个值：DEFAULT、LARGE
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white); //圈的背景色

        // 设置位置，设置后swipeRefreshLayout.setRefreshing(true);才会显示
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, mContext.getResources()  //applyDimension 该处意思是获取24dip
                        .getDisplayMetrics()));
    }


    public interface ListRefreshListener {
        /**
         * 访问数据源
         */
        void loadSourceData(int page, int pageSize);
    }
}
