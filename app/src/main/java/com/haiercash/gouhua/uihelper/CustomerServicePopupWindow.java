package com.haiercash.gouhua.uihelper;

import android.view.Gravity;
import android.view.View;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;

import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/9/25<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class CustomerServicePopupWindow extends BasePopupWindow {
    public static final int TYPE_ONLINE = 1;
    public static final int TYPE_PHONE = 2;
    private OnclickListener listener;

    public interface OnclickListener {
        void onClickPosition(int pos);
    }

    public CustomerServicePopupWindow(BaseActivity activity, Object data, OnclickListener listener) {
        super(activity, data);
        this.listener = listener;
    }

    @Override
    protected int getLayout() {
        return R.layout.pop_customer_service_protocol;
    }

    @Override
    protected void onViewCreated(Object data) {
    }

    @OnClick({R.id.rl_online_service, R.id.rl_phone_service, R.id.rl_back})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.rl_online_service:
                listener.onClickPosition(TYPE_ONLINE);
                break;
            case R.id.rl_phone_service:
                listener.onClickPosition(TYPE_PHONE);
                break;
            case R.id.rl_back:
                dismiss();
                break;
        }
        dismiss();
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }
}
