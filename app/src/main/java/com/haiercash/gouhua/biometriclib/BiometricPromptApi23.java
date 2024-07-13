package com.haiercash.gouhua.biometriclib;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.app.haiercash.base.utils.log.Logger;
import com.haiercash.gouhua.base.BaseActivity;

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
@RequiresApi(Build.VERSION_CODES.M)
class BiometricPromptApi23 implements IBiometricPromptImpl {

    //private static final String TAG = "BiometricPromptApi23";
    private BaseActivity mActivity;
    private BiometricPromptDialog mDialog;
    private FingerprintManager mFingerprintManager;
    private CancellationSignal mCancellationSignal;
    private BiometricPromptManager.OnBiometricIdentifyCallback mManagerIdentifyCallback;
    private FingerprintManager.AuthenticationCallback mFmAuthCallback = new FingerprintManageCallbackImpl();

    BiometricPromptApi23(BaseActivity activity) {
        mActivity = activity;
        mFingerprintManager = getFingerprintManager(activity);
    }


    @Override
    public void authenticate(Object cancel, @NonNull BiometricPromptManager.OnBiometricIdentifyCallback callback) {
        //指纹识别的回调
        mManagerIdentifyCallback = callback;
        /*
         * 我实现了一个自定义dialog，
         * BiometricPromptDialog.OnBiometricPromptDialogActionCallback是自定义dialog的回调
         */
        mDialog = BiometricPromptDialog.newInstance();
        mDialog.setOnBiometricPromptDialogActionCallback(new BiometricPromptDialog.OnBiometricPromptDialogActionCallback() {
            @Override
            public void onDialogDismiss() {
                //当dialog消失的时候，包括点击userPassword、点击cancel、和识别成功之后
                if (mCancellationSignal != null && !mCancellationSignal.isCanceled()) {
                    mCancellationSignal.cancel();
                }
            }

            @Override
            public void onUsePassword() {
                //一些情况下，用户还可以选择使用密码
                if (mManagerIdentifyCallback != null) {
                    mManagerIdentifyCallback.onUsePassword();
                }
            }

            @Override
            public void onCancel() {
                //点击cancel键
                if (mManagerIdentifyCallback != null) {
                    mManagerIdentifyCallback.onCancel();
                }
            }
        });
        mDialog.show(mActivity.getSupportFragmentManager(), "BiometricPromptApi23");
        if (cancel == null) {
            mCancellationSignal = new CancellationSignal();
        } else if (cancel instanceof CancellationSignal) {
            mCancellationSignal = (CancellationSignal) cancel;
        }
        if (mCancellationSignal == null) {
            mCancellationSignal = new CancellationSignal();
        }
        mCancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                //Log.d(TAG, "setOnCancelListener() onCancel");
                mDialog.dismiss();
            }
        });
        try {
            CryptoObjectHelper cryptoObjectHelper = new CryptoObjectHelper();
            getFingerprintManager(mActivity).authenticate(cryptoObjectHelper.buildCryptoObject(), mCancellationSignal, 0, mFmAuthCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelAuthenticate() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


    private class FingerprintManageCallbackImpl extends FingerprintManager.AuthenticationCallback {

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            //Log.d(TAG, "onAuthenticationError() called with: errorCode = [" + errorCode + "], errString = [" + errString + "]");
            mDialog.setState(BiometricPromptDialog.STATE_ERROR, errorCode, errString.toString());
            mManagerIdentifyCallback.onError(errorCode, errString.toString());
        }


        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            //Log.d(TAG, "onAuthenticationFailed() called");
            mDialog.setState(BiometricPromptDialog.STATE_FAILED);
            mManagerIdentifyCallback.onFailed();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
            Logger.d("onAuthenticationHelp() called with: helpCode = [" + helpCode + "], helpString = [" + helpString + "]");
            //mDialog.setState(BiometricPromptDialog.STATE_FAILED);
            //mManagerIdentifyCallback.onFailed();
        }


        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            //Log.i(TAG, "onAuthenticationSucceeded: ");
            mDialog.setState(BiometricPromptDialog.STATE_SUCCEED);
            mManagerIdentifyCallback.onSucceeded();
        }
    }

    private FingerprintManager getFingerprintManager(Context context) {
        if (mFingerprintManager == null) {
            mFingerprintManager = context.getSystemService(FingerprintManager.class);
        }
        return mFingerprintManager;
    }


    boolean isHardwareDetected() {
        if (mFingerprintManager != null) {
            return mFingerprintManager.isHardwareDetected();
        }
        return false;
    }


    boolean hasEnrolledFingerprints() {
        if (mFingerprintManager != null) {
            return mFingerprintManager.hasEnrolledFingerprints();
        }
        return false;
    }
}
