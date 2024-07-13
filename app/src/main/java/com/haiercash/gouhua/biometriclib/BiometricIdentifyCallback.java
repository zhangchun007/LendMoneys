package com.haiercash.gouhua.biometriclib;

import com.app.haiercash.base.utils.log.Logger;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/3/27<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
abstract class BiometricIdentifyCallback implements BiometricPromptManager.OnBiometricIdentifyCallback {
    private static final String TAG = "BiometricIdentifyCallback";

    @Override
    public void onUsePassword() {
        Logger.d(TAG, "onUsePassword");
    }

    @Override
    public void onFailed() {
        Logger.d(TAG, "onFailed");
    }

    @Override
    public void onError(int code, String reason) {
        Logger.d(TAG, "onError：" + code + "<->" + reason);
    }

    @Override
    public void onCancel() {
        Logger.d(TAG, "onCancel");
    }
}
