package com.app.haiercash.base.bui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/1/9<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public abstract class BaseHeadView<VB extends ViewBinding> {
    protected Context mContext;
    protected BaseGHActivity mActivity;
    protected BaseGHFragment mFragment;
    protected VB binding;

    public BaseHeadView(BaseGHFragment mFragment) {
        this((BaseGHActivity) mFragment.getActivity());
        this.mFragment = mFragment;
    }

    public BaseHeadView(BaseGHActivity activity) {
        mContext = activity;
        mActivity = activity;
        binding = initBinding(LayoutInflater.from(activity));
//        if (viewGroup != null) {
//            mView = activity.getLayoutInflater().inflate(setViewRes(), (ViewGroup) viewGroup.getParent(), false);
//        } else {
//            mView = activity.getLayoutInflater().inflate(setViewRes(), null, false);
//        }
    }

    /**
     * 绑定的LayoutId
     */
    protected abstract VB initBinding(LayoutInflater inflater);

    public abstract void initViewData(Object objData);

    public View getHeadView() {
        return binding.getRoot();
    }

    public void onDestroy() {
        binding = null;
    }
}
