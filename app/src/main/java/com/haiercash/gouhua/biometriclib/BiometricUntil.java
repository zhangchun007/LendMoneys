package com.haiercash.gouhua.biometriclib;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/4/3<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BiometricUntil extends BiometricIdentifyCallback {
    /**
     * 单纯的认证启动指纹识别
     */
    public static final int BIOMETRIC_CHECKED = 0x001;
    /**
     * 指纹识别功能切换开启和关闭状态
     */
    public static final int BIOMETRIC_SWITCH = 0x002;
    private int doType;
    private final BiometricPromptManager mManager;
    private final BaseActivity mActivity;
    private BiometricUntilCallBack callback;

    public BiometricUntil(BaseActivity activity, BiometricUntilCallBack callback) {
        mActivity = activity;
        this.callback = callback;
        mManager = BiometricPromptManager.from(mActivity);
    }

    public BiometricUntil(BaseActivity activity, int doType, BiometricUntilCallBack callback) {
        mActivity = activity;
        this.callback = callback;
        this.doType = doType;
        mManager = BiometricPromptManager.from(mActivity);
    }

    public BiometricUntil setCallback(BiometricUntilCallBack callback) {
        this.callback = callback;
        return this;
    }

    /**
     * 版本检测成后判断是否可以指纹或者面部登录
     */
    public void checkBiometric() {
        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
            return;
        }
        if (LoginSelectHelper.hasSetBiometric()) {
            if (isHardwareDetected() && !hasEnrolledFingerprints()) {//支持指纹 && 系统没有指纹了
                mActivity.showDialog("您尚未设置指纹或未开启本应用指纹密码权限，请在手机系统“设置”中开启");
            } else {
                showBiometricLibPop();
            }
        } else {
            if (AppApplication.isLogIn()) {
                mActivity.showDialog("您尚未设置指纹");
            } else {
                //指纹登录页面停留过久，并且切换到后台，如果出现长token失效，触发跳转登录时会跳转不成功，但此时登录信息已被清空，
                // 再次触发指纹时就需要跳转登录
                Bundle bundle = new Bundle();
                bundle.putString("reason", NetConfig.TOKEN_INVALID);
                if (ActivityUntil.findActivity(MainActivity.class) == null) {//防止没有首页
                    ARouterUntil.getInstance(PagePath.ACTIVITY_MAIN).navigation();
                }
                LoginSelectHelper.staticToGeneralLogin(bundle);
            }
        }
    }

    /**
     * 引导设置指纹开启设置
     */
    public void showOpenOrSetBiometricLib() {
        if (!mManager.hasEnrolledFingerprints()) {
            mActivity.showDialog("您尚未设置指纹，请在手机系统--设置中开启", "取消", "去设置", (dialog, which) -> {
                if (which == 2) {
                    mActivity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            }).setButtonTextColor(1, R.color.colorPrimary).setButtonTextColor(2, R.color.colorPrimary);
        } else {
            showBiometricLibPop();
        }
    }

    /**
     * 显示指纹验证弹窗
     */
    public void showBiometricLibPop() {
        if (mManager.isBiometricPromptEnable()) {
            if (callback != null) {
                callback.biometricSwitch(true);
            }
            //启动指纹验证弹窗
            mManager.authenticate(this);
        }
    }

    /**
     * 开启或者关闭指纹登录
     */
    public void openOrCloseFingerprint() {
        if (isHardwareDetected()) {//支持指纹
            if (hasEnrolledFingerprints()) {//设备系统存在指纹
                showBiometricLibPop();
            } else {
                mActivity.showDialog("您尚未设置指纹，请在手机系统--设置中开启", "取消", "去设置", (dialog, which) -> {
                    if (which == 2) {
                        mActivity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                }).setButtonTextColor(1, R.color.colorPrimary).setButtonTextColor(2, R.color.colorPrimary);
            }
        }
    }

    public void cancelAuthenticate() {
        if (mManager != null) {
            mManager.cancelAuthenticate();
        }
    }

    public boolean isHardwareDetected() {//系统硬件是否支持指纹识别
        return mManager.isHardwareDetected();
    }

    private boolean hasEnrolledFingerprints() {//设备在系统设置里面是否设置了指纹
        return mManager.hasEnrolledFingerprints();
    }

    public boolean isBiometricPromptEnable() {//系统是否支持指纹识别
        return mManager.isBiometricPromptEnable();
    }

    public boolean isBiometricPromptEnableAndSwitchOpen() {
        return LoginSelectHelper.hasSetBiometric() && mManager.isBiometricPromptEnable();
    }

    @Override
    public void onSucceeded() {
        String bioSwitch = SpHelper.getInstance().readMsgFromSp(SpHp.getOther(SpKey.LAST_LOGIN_SUCCESS_USERID), SpKey.STATE_HAS_BIOMETRIC, "N");
        if ("N".equals(bioSwitch)) {
            //从关闭到开启
            LoginSelectHelper.saveBiometricOpenState();
            UiUtil.toast("指纹登录开启成功");
            if (callback != null) {
                callback.onSuccess(true);
            }
        } else {
            if (doType == BIOMETRIC_CHECKED) {
                LoginSelectHelper.saveBiometricOpenState();
                if (callback != null) {
                    callback.onSuccess(true);
                }
            } else if (doType == BIOMETRIC_SWITCH) {
                LoginSelectHelper.saveBiometricOpenState(!"Y".equals(bioSwitch));
                if (callback != null) {
                    callback.onSuccess(!"Y".equals(bioSwitch));
                }
            } else {
                Logger.e("BiometricUntil :: doType 不符合指定的值，无法确认行为，无任何操作");
            }
        }
    }

    @Override
    public void onFailed() {
        super.onFailed();
        //失败前4次走此
        if (callback != null) {
            callback.onFailed(null, "指纹识别或验证失败");
        }
    }

    @Override
    public void onError(int code, String reason) {
        super.onError(code, reason);
        //取消识别会走此，此时code=5
        //失败第五次开始走此，并且code=7
        //9-失败过多导致传感器停用
        if (callback != null) {
            callback.onFailed(code, reason);
            if (5 == code) {
                callback.onCancel();
            } else {
                callback.onErrorForMoreFailed();
            }
        }
    }

    public interface BiometricUntilCallBack {
        void biometricSwitch(boolean isOpening);

        void onSuccess(boolean isOpenFingerprint);

        void onFailed(Integer errorCode, String errorReason);

        void onErrorForMoreFailed();//7-5次及以上失败

        void onCancel();//取消识别
    }
}
