package com.haiercash.gouhua.uihelper;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.AIServer;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/5/6<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ScreenshotsPop extends BasePopupWindow implements INetResult {
    @BindView(R.id.iv_screen)
    ImageView ivScreen;
    private String filePath;
    private NetHelper netHelper;

    public ScreenshotsPop(BaseActivity context, Object data) {
        super(context, data);
    }

    @Override
    protected int getLayout() {
        return R.layout.pop_screenshots;
    }

    @Override
    protected void onViewCreated(Object data) {
        int n = Color.parseColor("#00000000");
        ColorDrawable dw = new ColorDrawable(n);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        setOutTouchClickDismiss();
        ivScreen.setImageDrawable(null);
        filePath = String.valueOf(data);
        resetImageData(filePath);
    }

    public void resetImageData(String filePath) {
        String fSwitch = SpHp.getOther(SpKey.FEEDBACK_SWITCH, "Y");
        if ("Y".equals(fSwitch) && !CheckUtil.isEmpty(filePath)) {
            ivScreen.setVisibility(View.VISIBLE);
            GlideUtils.loadPicFormLocal(mActivity, filePath, ivScreen, 0);
        } else {
            ivScreen.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_feedback, R.id.iv_screen, R.id.tv_customer_service})
    public void onViewClick(View view) {
        if (view.getId() == R.id.tv_feedback || view.getId() == R.id.iv_screen) {
            if (netHelper == null) {
                netHelper = new NetHelper(this);
            }
            mActivity.showProgress(true);
            Map<String, String> map = new HashMap<>();
            if (!CheckUtil.isEmpty(AppApplication.userid)) {
                map.put("userId", EncryptUtil.simpleEncrypt(AppApplication.userid));
            }
            netHelper.getService(ApiUrl.URL_IS_FEEDBACK_ALLOW, map);
        } else if (AppApplication.isLogIn()) {
            AIServer.showAiWebServer(mActivity, "帮助中心");
        }
        dismiss();
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        mActivity.showProgress(false);
        ARouterUntil.getContainerInstance(PagePath.FRAGMENT_FEEDBACK)
                .put("feedbackPath", filePath)
                .navigation();
    }

    @Override
    public void onError(BasicResponse error, String url) {
        mActivity.showProgress(false);
        UiUtil.toast(error.getHead().getRetMsg());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
    }
}
