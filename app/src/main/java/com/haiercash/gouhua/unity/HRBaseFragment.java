package com.haiercash.gouhua.unity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;


import com.haiercash.gouhua.base.BaseFragment;

import butterknife.ButterKnife;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/8
 * @Version: 1.0
 */
public class HRBaseFragment extends BaseFragment {

    protected View mView;
    protected ViewBinding _binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        initEventAndData();
    }

    /**
     * 初始化数据
     */
    protected void initEventAndData() {
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

    @Override
    public void onDestroyView() {
        _binding = null;
        super.onDestroyView();

    }

}
