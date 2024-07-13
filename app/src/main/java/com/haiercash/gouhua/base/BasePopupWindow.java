package com.haiercash.gouhua.base;

import android.content.Context;
import android.view.View;

import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.bui.BaseGHPopupWindow;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/15
 * 描    述：
 * 修订历史：
 * ================================================================
 */
public abstract class BasePopupWindow extends BaseGHPopupWindow implements View.OnClickListener {
    protected BaseActivity mActivity;

    public BasePopupWindow(BaseActivity context, Object data) {
        super(context, data);
    }

    @Override
    protected void init(Context context, Object data) {
        mActivity = (BaseActivity) context;
        super.init(context, data);
    }

    @Override
    public void onClick(View v) {

    }
}
