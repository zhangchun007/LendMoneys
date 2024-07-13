package com.haiercash.gouhua.biometriclib;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.haiercash.gouhua.base.BaseActivity;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/3/26<br/>
 * 描    述：引用:https://github.com/gaoyangcr7/BiometricPromptDemo<br/>
 * 修订历史：类的内部通过Api版本的判断，来分别实现Api23和Api28的适配<br/>
 * ================================================================
 */
class BiometricPromptManager {

    private IBiometricPromptImpl mImpl;
    private Activity mActivity;

    public interface OnBiometricIdentifyCallback {
        void onUsePassword();

        void onSucceeded();

        void onFailed();

        void onError(int code, String reason);

        void onCancel();
    }

    public static BiometricPromptManager from(BaseActivity activity) {
        return new BiometricPromptManager(activity);
    }

    private BiometricPromptManager(BaseActivity activity) {
        mActivity = activity;
        if (isAboveApi28()) {
            mImpl = new BiometricPromptApi28(activity);
        } else if (isAboveApi23()) {
            mImpl = new BiometricPromptApi23(activity);
        }
    }

    /**
     * 判断版本号
     */
    private boolean isAboveApi28() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }

    /**
     * 判断版本号
     */
    private boolean isAboveApi23() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public void authenticate(@NonNull OnBiometricIdentifyCallback callback) {
        if (isAboveApi28()) {
            mImpl.authenticate(new CancellationSignal(), callback);
        } else if (isAboveApi23()) {
            mImpl.authenticate(new CancellationSignal(), callback);
        }
    }

    /**
     * @param cancel android.os.CancellationSignal 或 android.support.v4.os.CancellationSigna
     */
    public void authenticate(@NonNull Object cancel, @NonNull OnBiometricIdentifyCallback callback) {
        mImpl.authenticate(cancel, callback);
    }

    public void cancelAuthenticate() {
        if (mImpl != null && isAboveApi28()) {
            mImpl.cancelAuthenticate();
        } else if (mImpl != null && isAboveApi23()) {
            mImpl.cancelAuthenticate();
        }
    }


    /**
     * Determine if there is at least one fingerprint enrolled.
     * 这个方法是用来判断你的设备在系统设置里面是否设置了指纹。
     * 如果用户没有设置，这时候你可以引导他去设置。不过，我查了一下，各个厂家的设置指纹的页面Activity名都不是统一的，
     * 所以这里要一一做适配能累成狗。所以如果要引导的话，引导到安全设置页面就可以了，安全设置页面系统有统一的Intent，
     * 是【Settings.ACTION_SECURITY_SETTINGS】
     *
     * @return true if at least one fingerprint is enrolled, false otherwise
     */
    public boolean hasEnrolledFingerprints() {
        if (isAboveApi28()) {
            //这是Api23的判断方法，也许以后有针对Api28的判断方法
            FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(mActivity);
            return managerCompat.hasEnrolledFingerprints();
            //final FingerprintManager manager = mActivity.getSystemService(FingerprintManager.class);
            //return manager != null && manager.hasEnrolledFingerprints();
        } else if (isAboveApi23()) {
            return ((BiometricPromptApi23) mImpl).hasEnrolledFingerprints();
        } else {
            return false;
        }
    }

    /**
     * Determine if fingerprint hardware is present and functional.
     * 这是用来判断系统硬件是否支持指纹识别，这里也是分情况判断，但是AndroidP还不知道用什么确切的办法来判断，所以暂时用与AndroidM一样的方式
     *
     * @return true if hardware is present and functional, false otherwise.
     */
    public boolean isHardwareDetected() {
        if (isAboveApi28()) {
            ////这是Api23的判断方法，也许以后有针对Api28的判断方法
            //final FingerprintManager fm = mActivity.getSystemService(FingerprintManager.class);
            //return fm != null && fm.isHardwareDetected();
            FingerprintManagerCompat managerCompat = FingerprintManagerCompat.from(mActivity);
            return managerCompat.isHardwareDetected();
        } else if (isAboveApi23()) {
            return ((BiometricPromptApi23) mImpl).isHardwareDetected();
        } else {
            return false;
        }
    }

    /**
     * 这个方法是判断系统有没有设置锁屏。
     * 这个方法我认为是个鸡肋，因为现在如果你设置了指纹的话，肯定要让你先设置一种密码（PIN/Password/Pattern），
     * 那么锁屏肯定也就随之设置了，不理解为啥还要判断一下这个
     */
    public boolean isKeyguardSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) mActivity.getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager != null && keyguardManager.isKeyguardSecure()) {
            return true;
        }
        return false;
    }

    /**
     * Whether the device support biometric.
     * 系统是否支持指纹识别
     *
     * @return true 支持
     */
    public boolean isBiometricPromptEnable() {
        return isAboveApi23()
                && isHardwareDetected()
                && hasEnrolledFingerprints()
                && isKeyguardSecure();
    }

//    /**
//     * Whether fingerprint identification is turned on in app setting.
//     *
//     * @return
//     */
//    public boolean isBiometricSettingEnable() {
//        return SPUtils.getBoolean(mActivity, SPUtils.KEY_BIOMETRIC_SWITCH_ENABLE, false);
//    }
//
//
//    /**
//     * Set fingerprint identification enable in app setting.
//     *
//     * @return
//     */
//    public void setBiometricSettingEnable(boolean enable) {
//        SPUtils.put(mActivity, SPUtils.KEY_BIOMETRIC_SWITCH_ENABLE, enable);
//    }

}
