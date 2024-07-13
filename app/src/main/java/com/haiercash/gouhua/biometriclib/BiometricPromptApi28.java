package com.haiercash.gouhua.biometriclib;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;

import com.app.haiercash.base.utils.log.Logger;
import com.haiercash.gouhua.base.BaseActivity;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

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
@RequiresApi(Build.VERSION_CODES.P)
class BiometricPromptApi28 implements IBiometricPromptImpl {

    private static final String KEY_NAME = "BiometricPromptApi28";
    private BaseActivity mActivity;
    private FingerprintManagerCompat mFingerprintManagerCompat;
    private BiometricPromptDialog mDialog;
    private BiometricPromptManager.OnBiometricIdentifyCallback mManagerIdentifyCallback;
    private CancellationSignal mCancellationSignal;
    private Signature mSignature;

    BiometricPromptApi28(BaseActivity activity) {
        mActivity = activity;
        mFingerprintManagerCompat = getFingerprintManager(activity);
        //BiometricPrompt biometricPrompt = .from(activity);
        //BiometricPrompt builder = new BiometricPrompt.Builder(activity).build();
    }

    @Override
    public void authenticate(Object cancel, @NonNull BiometricPromptManager.OnBiometricIdentifyCallback callback) {
        //指纹识别的回调
        mManagerIdentifyCallback = callback;
        try {
            mSignature = initSignature(KEY_NAME);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
            //Logger.e(e.getMessage());
        }
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
        mDialog.show(mActivity.getSupportFragmentManager(), "BiometricPromptApi28");
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
                //Log.d(KEY_NAME, "setOnCancelListener() onCancel");
                mDialog.dismiss();
            }
        });
        getFingerprintManager(mActivity).authenticate(new FingerprintManagerCompat.CryptoObject(mSignature),//用于通过指纹验证取出AndroidKeyStore中key的值
                0,//系统建议为0,其他值，谷狗占位用于其他生物验证
                mCancellationSignal,//强制取消指纹验证
                authenticationCallback, null);// 内部验证消息通过Handler传递，不需要，传空
    }

    @Override
    public void cancelAuthenticate() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }


    private FingerprintManagerCompat.AuthenticationCallback authenticationCallback = new FingerprintManagerCompat.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            //Log.d(KEY_NAME, "onAuthenticationError() called with: errMsgId = [" + errMsgId + "], errString = [" + errString + "]");
            mDialog.setState(BiometricPromptDialog.STATE_ERROR, errMsgId, errString.toString());
            mManagerIdentifyCallback.onError(errMsgId, errString.toString());
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            //Log.d(KEY_NAME, "onAuthenticationHelp() called with: helpMsgId = [" + helpMsgId + "], helpString = [" + helpString + "]");
            Logger.d("onAuthenticationHelp() called with: helpMsgId = [" + helpMsgId + "], helpString = [" + helpString + "]");
            //mDialog.setState(BiometricPromptDialog.STATE_FAILED);
            //mManagerIdentifyCallback.onFailed();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            //指纹验证成功
            //result这里的result是指纹数据，需要相应的key才能拿出来用
            //Log.i(KEY_NAME, "onAuthenticationSucceeded: ");
            mDialog.setState(BiometricPromptDialog.STATE_SUCCEED);
            mManagerIdentifyCallback.onSucceeded();
        }

        @Override
        public void onAuthenticationFailed() {
            //多次指纹验证错误后，回调此方法；
            //并且，（第一次错误）由系统锁定30s
            //Log.d(KEY_NAME, "onAuthenticationFailed() called");
            mDialog.setState(BiometricPromptDialog.STATE_FAILED);
            mManagerIdentifyCallback.onFailed();
        }
    };

    private FingerprintManagerCompat getFingerprintManager(Activity activity) {
        if (mFingerprintManagerCompat == null) {
            mFingerprintManagerCompat = FingerprintManagerCompat.from(activity);
        }
        return mFingerprintManagerCompat;
    }

    @Nullable
    private KeyPair getKeyPair(String keyName) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        if (keyStore.containsAlias(keyName)) {
            // Get public key
            PublicKey publicKey = keyStore.getCertificate(keyName).getPublicKey();
            // Get private key
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyName, null);
            // Return a key pair
            return new KeyPair(publicKey, privateKey);
        }
        return null;
    }


    @Nullable
    private Signature initSignature(String keyName) throws Exception {
        KeyPair keyPair = getKeyPair(keyName);
        if (keyPair != null) {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(keyPair.getPrivate());
            return signature;
        }
        return null;
    }
}
