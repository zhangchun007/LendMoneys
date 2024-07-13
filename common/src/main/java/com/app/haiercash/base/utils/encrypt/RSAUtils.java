package com.app.haiercash.base.utils.encrypt;

import android.util.Base64;

import com.app.haiercash.base.utils.ArrayUtils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {
    private static String RSA = "RSA/ECB/PKCS1Padding";
    private static String PUBLICKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxy9fNesI46Y1DJbvEAAOT+t2HSCfukxf8L1zNrwHYL2li3O8tKSLrejqgm8b8grBzt4Ogej1RSaPgguzO52MYHyuA1HW6qrpMu2iwagDRPXIirZplk/Bx0sanHXcWujFjU+UpTqSq4L1kP3SuJf2mijnATq0E/DTM8L99JZ2+aToQXe40t/mQzkr6eyNIwJK3v6NmjEM98L69KW4T/qQk1bvjKZS4neadtT2Gj5Ic/MQ6U20MnX3YWqi4OTLQN9pvl6V9Jq2kjTrLuoLLH4pMEWmzHX7lg4OSZWn/S69e7X+tCQTBQX8yNl3RbDLzLG9fzjZkwJKSFoYU6t4mGGUZwIDAQAB";

    /**
     * 获取公钥对象
     *
     * @param base64String
     * @return
     */
    private static PublicKey getPublicKeyFromString(String base64String) {
        PublicKey publicKey = null;
        try {
            byte[] bt = Base64.decode(base64String.getBytes(), Base64.NO_WRAP);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bt);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(publicKeySpec);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;
    }


    /**
     * RSA加密
     * 现在接口不加密，则原字符串输出
     *
     * @param inputStr
     * @return
     */
    public static String encryptByRSA(String inputStr) {
        return inputStr;
    }

    /**
     * RSA加密
     *
     * @param inputStr
     * @return
     */
    public static String encryptByRSANew(String inputStr) {
        String strencryptByRSA = "";
        try {
            PublicKey key = getPublicKeyFromString(PUBLICKEY);
            strencryptByRSA = Base64.encodeToString(encryptByRSA256(inputStr.getBytes(), key), Base64.NO_WRAP);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return strencryptByRSA;
    }


    /**
     * 获取私钥对象
     *
     * @param base64String
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    private static PrivateKey getPrivateKeyFromString(String base64String) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] bt = Base64.decode(base64String, Base64.NO_WRAP);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bt);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

    /**
     * RSA解密
     *
     * @param inputStr
     * @return
     */
    public static String decryptByRSA(String inputStr) {
        try {
            PrivateKey key = getPrivateKeyFromString(PUBLICKEY);
            return new String(decryptByRSA(Base64.decode(inputStr, Base64.NO_WRAP), key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * RSA解密
     *
     * @param input
     * @param key
     * @return
     * @throws Exception
     */
    private static byte[] decryptByRSA(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] output = cipher.doFinal(input);
        return output;
    }


    /**
     * RSA加密
     *
     * @param input
     * @param key
     * @return
     * @throws Exception
     */
    private static byte[] encryptByRSA(byte[] input, PublicKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] output = cipher.doFinal(input);
        return output;
    }


    /**
     * RSA加密
     * 这个方法是rsa分段加密，主要是解决rsa加密时，入参长度超过256时会报错
     *
     * @param input
     * @param key
     * @return
     * @throws Exception
     */
    private static byte[] encryptByRSA256(byte[] input, PublicKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] output = new byte[0];
        for (int i = 0; i < input.length; i += 200) {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(input, i, i + 200));
            output = ArrayUtils.addAll(output, doFinal);
        }
        return output;
    }

}
