package com.haiercash.gouhua.activity.inenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.HmacSHA256Utils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.beans.gesture.ValidateUserBean;
import com.haiercash.gouhua.biometriclib.BiometricUntil;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.BrAgentUtils;
import com.haiercash.gouhua.utils.LoginUserHelper;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/6/11<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.ACTIVITY_VERIFY_BIOMETRIC)
public class VerifyBiometricActivity extends BaseVerifyActivity implements BiometricUntil.BiometricUntilCallBack {

    private BiometricUntil mBiometricUntil;

    @Override
    protected int getContentResId() {
        return R.layout.activity_verify_biometric;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        mTextTip.setVisibility(View.GONE);
        findViewById(R.id.tv_biometric_check).setOnClickListener(v -> checkBiometric());
    }

    @Override
    protected void versionCancel() {
        //如果不等于lock，就相当于从启动页的启动程序，那么就自动启动生物识别
        if (!"lock".equals(pageTag)) {
            checkBiometric();
        }
    }

    private void checkBiometric() {
        if (mBiometricUntil != null) {
            mBiometricUntil.cancelAuthenticate();
            mBiometricUntil = null;
        }
        if (!AppApplication.isLogIn()) {
            //指纹登录页面停留过久，并且切换到后台，如果出现长token失效，触发跳转登录时会跳转不成功，但此时登录信息已被清空，
            // 再次触发指纹时就需要跳转登录
            Bundle bundle = new Bundle();
            bundle.putString("reason", NetConfig.TOKEN_INVALID);
            if (ActivityUntil.findActivity(MainActivity.class) == null) {//防止没有首页
                ARouterUntil.getInstance(PagePath.ACTIVITY_MAIN).navigation();
            }
            LoginSelectHelper.staticToGeneralLogin(bundle);
        } else {
            mBiometricUntil = new BiometricUntil(this, BiometricUntil.BIOMETRIC_CHECKED, this);
            mBiometricUntil.checkBiometric();
        }
    }

    private boolean mLoginSuccess;//验证成功为true，验证失败时不loading，静默调接口

    //成功和失败都调，但是只有验证成功后调接口成功才算登录成功
    private void requestUserInfoById(boolean success) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", EncryptUtil.simpleEncrypt(userId));
        //server登记登录方式
        map.put("loginFlag", success ? "success" : "fail");
        map.put("loginMethod", "fingerprint");
        //必须放在map最后一行，是对整个map参数进行签名对
        map.put("sign", HmacSHA256Utils.buildNeedSignValue(map));
        mLoginSuccess = success;
        netHelper.getService(ApiUrl.URL_GET_USER_INFO_BY_ID, map, ValidateUserBean.class, true);
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.URL_GET_USER_INFO_BY_ID.equals(url)) {
            if (mLoginSuccess) {
                BrAgentUtils.logInBrAgent(this);//百融风险采集登录
                LoginUserHelper.saveLoginInfo((ValidateUserBean) success);
                postUmEventWithFingerprintResult(true, "");
                RiskNetServer.startRiskServer1(this, "lock_screen_fingerprint_login", "", 2);
                showProgress(false);
                if ("lock".equals(pageTag)) {
                    String webUrl = getIntent().getStringExtra("webUrl");
                    String webDoType = getIntent().getStringExtra("webDoType");
                    if (!CheckUtil.isEmpty(webDoType) && !CheckUtil.isEmpty(webUrl)) {
                        Intent intent = new Intent(this, JsWebBaseActivity.class);
                        intent.putExtra("jumpKey", webUrl);
                        intent.putExtra("webDoType", webDoType);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    setResult(Activity.RESULT_OK, intent);
                }
                finish();
            }
        } else {
            showProgress(false);
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (mLoginSuccess) {
            String errorReason = error != null && error.getHead() != null ? error.getHead().getRetMsg() : "";
            postUmEventWithFingerprintResult(false, errorReason);
            RiskNetServer.startRiskServer1(this, "lock_screen_fingerprint_login", "", 2);
            RiskInfoUtils.updateRiskInfoByNode("BR012", "NO");
            showProgress(false);
        }
    }

    @Override
    protected String getPageName() {
        return "指纹密码登录页";
    }

    @Override
    protected String getPageCode() {
        return "FingerprintLoginPage";
    }

    /*指纹设置成功/失败埋点*/
    private void postUmEventWithFingerprintResult(boolean isSuccess, String failReason) {
        Map<String, Object> map = new HashMap<>();
        map.put("is_success", isSuccess ? "true" : "false");
        map.put("fail_reason", !CheckUtil.isEmpty(failReason) ? failReason : "无");
        map.put("page_name_cn", getPageName());
        UMengUtil.onEventObject("FingerprintLogin_Result", map, getPageCode());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppApplication.doLoginCallback();
    }

    @Override
    public void biometricSwitch(boolean isOpening) {//指纹回调

    }

    @Override
    public void onSuccess(boolean isOpenFingerprint) {//指纹回调
        showProgress(true);
        RiskInfoUtils.updateRiskInfoByNode("BR012", "YES");
        requestUserInfoById(true);
    }

    @Override
    public void onFailed(Integer errorCode, String errorReason) {//指纹回调
        postUmEventWithFingerprintResult(false, errorReason);
        RiskInfoUtils.updateRiskInfoByNode("BR012", "NO");
        requestUserInfoById(false);
    }

    @Override
    public void onErrorForMoreFailed() {//指纹回调
        //5次及以上指纹验证失败
        UiUtil.toast("验证失败次数过多，请使用其他方式登录");
    }

    @Override
    public void onCancel() {//指纹回调

    }
}
