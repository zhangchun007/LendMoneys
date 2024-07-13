package com.haiercash.gouhua.biometriclib;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import androidx.annotation.RequiresApi;

import java.security.Key;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

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
class CryptoObjectHelper {
    // This can be key name you want. Should be unique for the app.
    static final String KEY_NAME = "com.haiercash.gouhua.biometriclib.CryptoObjectHelper";
    // We always use this keystore on Android.
    static final String KEYSTORE_NAME = "AndroidKeyStore";
    // Should be no need to change these values.
    static final String KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    static final String TRANSFORMATION = KEY_ALGORITHM + "/" +
            BLOCK_MODE + "/" +
            ENCRYPTION_PADDING;
    final KeyStore keystore;

    CryptoObjectHelper() throws Exception {
        keystore = KeyStore.getInstance(KEYSTORE_NAME);
        keystore.load(null);
    }


    FingerprintManager.CryptoObject buildCryptoObject() throws Exception {
        Cipher cipher = createCipher(true);
        return new FingerprintManager.CryptoObject(cipher);
    }

    Cipher createCipher(boolean retry) throws Exception {
        Key key = GetKey();
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        try {
            cipher.init(Cipher.ENCRYPT_MODE | Cipher.DECRYPT_MODE, key);
        } catch (KeyPermanentlyInvalidatedException e) {
            keystore.deleteEntry(KEY_NAME);
            if (retry) {
                createCipher(false);
            } else {
                throw new Exception("Could not create the cipher for fingerprint authentication.", e);
            }
        }
        return cipher;
    }

    Key GetKey() throws Exception {
        Key secretKey;
        if (!keystore.isKeyEntry(KEY_NAME)) {
            CreateKey();
        }
        secretKey = keystore.getKey(KEY_NAME, null);
        return secretKey;
    }

    void CreateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME);
        KeyGenParameterSpec keyGenSpec =
                new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(ENCRYPTION_PADDING)
                        .setUserAuthenticationRequired(true)
                        .build();
        keyGen.init(keyGenSpec);
        keyGen.generateKey();
    }
}
