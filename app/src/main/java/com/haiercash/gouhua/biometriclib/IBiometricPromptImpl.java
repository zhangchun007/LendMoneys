package com.haiercash.gouhua.biometriclib;

import androidx.annotation.NonNull;
//import android.os.CancellationSignal;
//import android.support.v4.os.CancellationSignal;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/3/26<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
interface IBiometricPromptImpl {
    /**
     * @param cancel   android.os.CancellationSignal 或 android.support.v4.os.CancellationSignal
     * @param callback 回调
     */
    void authenticate(Object cancel, @NonNull BiometricPromptManager.OnBiometricIdentifyCallback callback);

    void cancelAuthenticate();
}
