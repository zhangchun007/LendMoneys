package com.app.haiercash.base.utils.encrypt;


import android.util.Base64;

import com.alibaba.android.arouter.utils.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * info信息 加密与解密工具
 * <p>
 * User: pengqiang
 * Date: 2017/7/14.
 */
public class InfoEncryptUtil {

    /**
     * 算法
     */
    private static final String ALGORITHM = "AES";

    /**
     * 密钥
     */
    private static String KEY;


    /**
     * 初始向量
     */
    private static String IV;


    /**
     * 加密、解密算法 / 工作模式 / 填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String ENCODING = "UTF-8";
    static {
        KEY = "zcrNys69zbvOz8zPvs-8yQ==";
        IV = "zsrNu766zL7Hz7zGurq5yQ==";
    }

    /**
     * 加密
     *
     * @param plainText 明文
     * @return
     */
    public static String encrypt(String plainText) {
        if (TextUtils.isEmpty(plainText)) {
            return "";
        }
        try {
            //1. 字符串反转
            plainText = reverse(plainText);

            //2. 首尾添加10个随机数
            plainText = addRandom(plainText, 10);

            // 3. aes加密
            byte[] cipherByte = aesEncrypt(plainText);
            // 4. BASE64编码
            String result = Base64.encodeToString(cipherByte, Base64.NO_WRAP);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     *
     * @param encryptString
     * @return
     */
    public static String decrypt(String encryptString) {
        if (TextUtils.isEmpty(encryptString)) {
            return "";
        }

        try {
            // 1. BASE64解码
            byte[] cipherByte = Base64.decode(encryptString, Base64.NO_WRAP);

            // 2. aes解密
            String result = aesDecrypt(cipherByte);

            // 3. 首尾删除10个随机数
            result = deleteRandom(result, 10);

            //4. 字符串反转
            result = reverse(result);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *  加密
     * @param sSrc 铭文
     * @param sKey 加密的key
     * @return
     */
    public static String aesEncryptWithKey(String sSrc, String sKey,String ivParams) {
        try {
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            } else if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            } else {
                byte[] raw = sKey.getBytes(ENCODING);
                SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);
                Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
                IvParameterSpec iv = new IvParameterSpec(ivParams.getBytes(ENCODING));
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec,iv);
                byte[] encrypted = cipher.doFinal(sSrc.getBytes(ENCODING));
                return Base64.encodeToString(encrypted, Base64.NO_WRAP);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     * @param sSrc 明文
     * @param sKey 加密的key
     * @return
     */
    public static String aesDecrypt(String sSrc, String sKey,String ivParams) {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes(ENCODING);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(ivParams.getBytes(ENCODING));
            cipher.init(Cipher.DECRYPT_MODE, skeySpec,iv);
            byte[] encrypted1 = Base64.decode(sSrc, Base64.NO_WRAP);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, ENCODING);
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }


    /**
     * aes 加密
     *
     * @param src 明文
     * @return
     */
    public static byte[] aesEncrypt(String src) {

        try {
            SecretKeySpec key = new SecretKeySpec(EncryptUtil.simpleDecrypt(KEY).getBytes("ASCII"), ALGORITHM);//生成一个密钥

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);// 创建密码器 算法/模式/补码方式

            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(EncryptUtil.simpleDecrypt(IV).getBytes("utf-8")));// 以加密的方式用密钥初始化此 Cipher

            byte[] result = cipher.doFinal(src.getBytes());//加密

            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * aes 解密
     *
     * @param src 密文
     * @return
     */
    public static String aesDecrypt(byte[] src) {

        try {
            SecretKeySpec key = new SecretKeySpec(EncryptUtil.simpleDecrypt(KEY).getBytes("ASCII"), ALGORITHM);//生成一个密钥

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);// 创建密码器 算法/模式/补码方式

            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(EncryptUtil.simpleDecrypt(IV).getBytes("utf-8")));// 以解密的方式用密钥初始化此 Cipher

            byte[] result = cipher.doFinal(src);//解密

            return new String(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 在首尾增加指定长度的随机数
     *
     * @param source
     * @param num
     * @return
     */
    private static String addRandom(String source, int num) {
        return random(num) + source + random(num);
    }

    /**
     * 去掉首尾指定长度的随机数
     *
     * @param source
     * @param num
     * @return
     */
    private static String deleteRandom(String source, int num) {
        return source.substring(num, source.length() - num);
    }

    /**
     * 字符串反转
     *
     * @param source
     * @return
     */
    private static String reverse(String source) {
        StringBuffer buffer = new StringBuffer(source);
        buffer.reverse();
        return buffer.toString();
    }

    /**
     * 获取指定长度随机数
     */
    private static String random(int tokenLen) {
        if (tokenLen < 1) {
            tokenLen = 1;
        }
        StringBuilder buffer = new StringBuilder("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuilder sb = new StringBuilder();
        SecureRandom r = new SecureRandom();
        int range = buffer.length();
        for (int i = 0; i < tokenLen; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }

    //随机16位key
    public static String getSixteenRandomKey() {
        String val = "";
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 16; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                //取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val.toLowerCase();
    }

    //随机16位偏移量
    public static String getSixteenRandomIV() {
        String val = "";
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 16; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "num" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                //取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val.toLowerCase();
    }


//    public static void example() {
//        String info = "userId=123456&loginname=登录名&grade=5&name=真实姓名&gender=&memo=备注&hashCode=0466da02d5e8393d29ba6c2e0c2f3bfd&timestamp=1500369104022";
//       // info = URLDecoder.decode(info);
//
//        String ens = encrypt(info);
//
//        System.out.println("原始:" + info);
//        System.out.println("加密:" + URLEncoder.encode(ens));
//        System.out.println("解密:" + decrypt(ens));
//    }
//
//
//    public static void main(String[] args) {
//
//        example();
//        testDecrypt();
//    }
//    public static void testDecrypt(){
//        String content="9zBvTwmVxEA%2FbBk2IyaLeWR8vAcby8QJwZvxZmPPzmS4Ttp5a4SPVsqLdA5IHIr5pd8dKrCRSWEpWL5ZefAsvKvAf3x69VNp8yu2t2pWzU0YbaKnD3vleG%2FrB%2Fl2yirVlSgfFLbunPz3jj0aLpTLvVu5wmghKGEQ8PfrzgQ7%2FzMsV65p6gBc%2FSzfn70bXcRDTuSWUNNOyOyxFwkiocJa1BCMv6FQWgiUA%2F4greO7toc%3D";
//        content = URLDecoder.decode(content);
//        String res = decrypt(content);
//        System.out.println("解密后："+ res);
//        System.out.println("解密后："+ URLDecoder.decode(res));
//    }

}
