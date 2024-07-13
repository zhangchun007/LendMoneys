package com.haiercash.gouhua.uihelper;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.beans.PopupWindowBean;

import butterknife.BindView;
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
public class NormalNewPopWindow extends BasePopupWindow {
    @BindView(R.id.tv_pop_title)
    TextView tv_pop_title;
    @BindView(R.id.tv_next)
    TextView tv_next;
    @BindView(R.id.sllv)
    ScrollView sllv;
    @BindView(R.id.tv_content_interest)
    TextView tvContentInterest;
    private ClickButtonCallback callback;

    public NormalNewPopWindow(BaseActivity activity, Object data) {
        this(activity, data, null);
    }

    public NormalNewPopWindow(BaseActivity activity, Object data, ClickButtonCallback callback) {
        super(activity, data);
        this.callback = callback;
        setClippingEnabled(false);
    }

    public interface ClickButtonCallback {
        void clickButton();
    }

    @Override
    protected int getLayout() {
        return R.layout.pop_normal_new_protocol;
    }

    @Override
    protected void onViewCreated(Object data) {
        PopupWindowBean bean = (PopupWindowBean) data;
        tv_pop_title.setText(bean.getTitle());
        sllv.setVisibility(View.VISIBLE);
        tvContentInterest.setText(SpannableStringUtils.getBuilder(mActivity, TextUtils.isEmpty(bean.getContent()) ? "暂无说明" : bean.getContent()).create());
        tv_next.setText(bean.getButtonTv());
    }

    @OnClick({R.id.tv_next, R.id.tv_close})
    public void onViewClick(View v) {
        dismiss();
        if (v.getId() == R.id.tv_next) {
            if (callback != null) {
                callback.clickButton();
            }
        }
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.CENTER, 0, 0);
    }

}
