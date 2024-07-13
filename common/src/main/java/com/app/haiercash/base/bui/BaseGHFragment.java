package com.app.haiercash.base.bui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.utils.log.Logger;
import com.trello.rxlifecycle3.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/3/17<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public abstract class BaseGHFragment extends RxFragment {
    protected View mView;
    protected Fragment currentSupportFragment;
    private Unbinder mUnBinder;
    private List<Fragment> listFragments;
    protected ViewBinding _binding;

//    public BaseGHFragment() {
//        super();
//    }
//
//    @ContentView
//    public BaseGHFragment(@LayoutRes int contentLayoutId) {
//        super(contentLayoutId);
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.e("BaseFragment-进入Fragment", getClass().getSimpleName() + "\n" + getArguments());
        if (getLayoutId() > 0) {
            mView = inflater.inflate(getLayoutId(), container, false);
        } else {
            _binding = initBinding(inflater, container);
            if (_binding != null) {
                mView = _binding.getRoot();
            } else {
                mView = super.onCreateView(inflater, container, savedInstanceState);
            }
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUnBinder = ButterKnife.bind(this, view);
    }

    /**
     * 获取布局文件Id
     */
    protected int getLayoutId() {
        return 0;
    }

    /**
     * 绑定的LayoutId
     */
    protected ViewBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return null;
    }

    /**
     * 执行onViewCreated
     */
    protected abstract void initEventAndData();

    /**
     * 获取当前fragmentName
     */
    protected abstract String getFragmentName();

    /**
     * 用Fragment替换视图
     *
     * @param resView        将要被替换掉的视图
     * @param targetFragment 用来替换的Fragment
     */
    protected void changeFragment(int resView, Fragment targetFragment) {
        if (targetFragment.equals(currentSupportFragment)) {
            return;
        }
        if (listFragments == null) {
            listFragments = new ArrayList<>();
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction.add(resView, targetFragment, targetFragment.getClass().getName());
        }
        try {
            for (Fragment fragment : listFragments) {
                transaction.hide(fragment);
            }
            if (!listFragments.contains(targetFragment)) {
                listFragments.add(targetFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (targetFragment.isHidden()) {
            transaction.show(targetFragment);
        }
        if (currentSupportFragment != null && currentSupportFragment.isVisible()) {
            transaction.hide(currentSupportFragment);
        }
        currentSupportFragment = targetFragment;
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onDestroyView() {
        //防止输入框的 setOnFocusChangeListener事件中继续持有view而导致空指针异常，故需各自回调事件中处理异常
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        _binding = null;
        super.onDestroyView();
    }
}
