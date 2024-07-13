package com.haiercash.gouhua.activity.gesture;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.beans.gesture.GestureBean;
import com.haiercash.gouhua.beans.gesture.ValidateUserBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.hunofox.gestures.interfaces.ISetPassword;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/7/31<br/>
 * 描    述：<br/>
 * 修订历史：入参：pageType->setGestures 设置手势密码
 * ->changeGestures 修改手势密码<br/>
 * ================================================================
 */
public class GesturesSettingActivity extends GestureEditActivity implements INetResult {
    /**
     * setGestures 设置手势密码
     * changeGestures 修改手势密码
     */
    private String pageType;
    private String userId;
    private String gesture;

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        full();
        pageType = getIntent().getStringExtra("pageType");
        showHeader(!isFromFirstRegister());
    }

    //从注册登录后来
    private boolean isFromFirstRegister() {
        return "setGestures".equals(pageType);
    }

    @Override
    protected int getCount() {
        return 4;
    }

    @Override
    protected ISetPassword getPasswordListener() {
        return new SetPasswordListener();
    }

    @Override
    public void onClick(View v) {
        //跳过
        finish();
        if (isFromFirstRegister()) {
            AppApplication.loginSuccessToDo();
        }
    }

    /*是否显示或隐藏状态栏*/
    private void full() {
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attr);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /*手势密码设置修改*/
    private void requestGesture() {
        if (CheckUtil.isEmpty(userId)) {
            showProgress(false);
            showDialog("账号异常，请退出重试");
            return;
        }
        GestureBean gestureBean = new GestureBean();
        gestureBean.setUserId(EncryptUtil.simpleEncrypt(userId));
        gestureBean.setGesture(EncryptUtil.simpleEncrypt(gesture));
        netHelper.putService(ApiUrl.urlSetGesture, gestureBean, ValidateUserBean.class, true);
    }

    @Override
    public void onSuccess(Object response, String flag) {
        if (ApiUrl.urlSetGesture.equals(flag)) {
            showProgress(false);
            if (null != response) {
                LoginSelectHelper.saveGestureOpenState();
                Log.e(getClass().getSimpleName(), "手势上传成功");
                if (isFromFirstRegister()) {
                    UiUtil.toast("手势识别开启成功");
                } else {
                    UiUtil.toast("手势密码设置成功");
                }
            } else {
                Log.e(getClass().getSimpleName(), "手势密码设置接收数据异常");
                UiUtil.toast("数据异常");
            }
            if ("changeGestures".equals(pageType)) {
                RiskNetServer.startRiskServer1(this, "gesture_password_modify_complete", "", 0);
                RiskInfoUtils.updateRiskInfoByNode("BR07", "YES");
                finish();
            } else if ("setGestures".equals(pageType)) {//第一次设置手势密码-》默认为新用户,进入首页后立即跳转额度申请前置页面
                UMengUtil.eventSimpleComplete("SetGesturePasswd2nd", getPageCode(), "true", "");
                RiskNetServer.startRiskServer1(this, "gesture_password_set_success", "", 0);
                RiskInfoUtils.updateRiskInfoByNode("BR04", "YES");
                finish();
                AppApplication.loginSuccessToDo();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.urlSetGesture.equals(url)) {
            if ("changeGestures".equals(pageType)) {
                RiskNetServer.startRiskServer1(this, "gesture_password_modify_complete", "", 0);
                RiskInfoUtils.updateRiskInfoByNode("BR07", "NO");
            } else if ("setGestures".equals(pageType)) {
                UMengUtil.eventSimpleComplete("SetGesturePasswd2nd", getPageCode(), "false", error != null && error.getHead() != null ? error.getHead().getRetMsg() : "");
                RiskNetServer.startRiskServer1(this, "gesture_password_set_success", "", 0);
            }
        }
        super.onError(error, url);
    }

    private class SetPasswordListener implements ISetPassword {
        @Override
        public void success(String password) {
            gesture = EncryptUtil.string2MD5(password);
            //上传手势密码
            showProgress(true);
            requestGesture();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode != KeyEvent.KEYCODE_BACK && super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
        super.onDestroy();
    }

    @Override
    protected String getPageCode() {
        return "SetGesturePasswdPage";
    }
}