package com.haiercash.gouhua.fragments;

import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.AIServer;

import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/2/6<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_CANCELLATION)
public class CancellationFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cancellation;
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle("注销说明");
        try {
            TextView btn_next = mView.findViewById(R.id.btn_next);
            btn_next.setTypeface(FontCustom.getMediumFont(mActivity));
        } catch (Exception e) {
            //
        }
    }

    @OnClick({R.id.btn_next})
    public void onViewClick(View view) {
        //同意注销进入 智能客服页面
        AIServer.showCloseCountWebServer(mActivity);
    }
}
