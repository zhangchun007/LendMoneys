package com.app.haiercash.base.utils.encrypt;

import android.text.TextUtils;
import android.util.Base64;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/21
 * 描    述：加解密工具类
 * 修订历史：
 * ================================================================
 */
public class EncryptUtil {
    /*S*******************************************MD5***********************************************/
    public static String MD5(byte[] md) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String string2MD5(String inStr) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder buffer = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                buffer.append("0");
            }
            buffer.append(Integer.toHexString(val));
        }
        return buffer.toString();
    }

    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuilder strBuf = new StringBuilder();
            for (byte anEncryption : encryption) {
                if (Integer.toHexString(0xff & anEncryption).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & anEncryption));
                } else {
                    strBuf.append(Integer.toHexString(0xff & anEncryption));
                }
            }
            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "error";
        }
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 给字符串加密
     */
    public static String convertMD5(String inStr) {
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        return new String(a);
    }

    public static String md5sum(String filename) {
        InputStream fis = null;
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try {
            fis = new FileInputStream(filename);
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            return MD5(md5.digest());
        } catch (Exception e) {
            System.out.println("error");
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param fileInputStream
     * @return
     */
    public static String md5sum(FileInputStream fileInputStream) {
        byte[] buffer = new byte[1024];
        int numRead = 0;
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            while ((numRead = fileInputStream.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }
            fileInputStream.close();
//            return toHexString(md5.digest());
            return MD5(md5.digest());
        } catch (Exception e) {
            System.out.println("error");
            return null;
        }
    }

    /*E*******************************************MD5***********************************************/
//    // 加密
//    public static String AesEncrypt(String sSrc, String sKey) throws Exception {
//        if (sKey == null) {
//            System.out.print("Key为空null");
//            return null;
//        }
//        // 判断Key是否为16位
//        if (sKey.length() != 16) {
//            System.out.print("Key长度不是16位");
//            return null;
//        }
//        byte[] raw = sKey.getBytes("utf-8");
//        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
////        IvParameterSpec iv = new IvParameterSpec(sKey.getBytes());//CBC模式使用
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
//
////        return new Base64().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
//        return  Base64();
//    }
//
//    // 解密
//    public static String AesDecrypt(String sSrc, String sKey) throws Exception {
//        try {
//            // 判断Key是否正确
//            if (sKey == null) {
//                System.out.print("Key为空null");
//                return null;
//            }
//            // 判断Key是否为16位
//            if (sKey.length() != 16) {
//                System.out.print("Key长度不是16位");
//                return null;
//            }
//            byte[] raw = sKey.getBytes("utf-8");
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
////            IvParameterSpec iv = new IvParameterSpec(sKey.getBytes());//CBC模式使用
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
//            try {
//                byte[] original = cipher.doFinal(encrypted1);
//                String originalString = new String(original,"utf-8");
//                return originalString;
//            } catch (Exception e) {
//                System.out.println(e.toString());
//                return null;
//            }
//        } catch (Exception ex) {
//            System.out.println(ex.toString());
//            return null;
//        }
//    }

    /**
     * 加密
     */
    public static String simpleEncrypt(String sSrc) {
        if (TextUtils.isEmpty(sSrc)) {
            return "";
        }
        byte[] bas = sSrc.getBytes();
        //按位取反
        for (int i = 0; i < bas.length; i++) {
            bas[i] = (byte) ~bas[i];
        }
        int half = bas.length / 2;
        for (int i = 0; i < half; i++) {
            if (i % 2 == 1) {
                //奇数位调换
                byte b = bas[i];
                bas[i] = bas[i + half];
                bas[i + half] = b;
            }
        }
        String wrapStr = Base64.encodeToString(bas, Base64.URL_SAFE);
        wrapStr = wrapStr.replaceAll("\\n", "");
        return wrapStr;
    }

    /**
     * 解密
     */
    public static String encrypt(String sSrc) {
        byte[] bas = sSrc.getBytes();
        String wrapStr = Base64.encodeToString(bas, Base64.DEFAULT);
        wrapStr = wrapStr.replace("+", "%2B");
        return wrapStr;
    }


    /**
     * 加密
     */
    public static String base64(String sSrc) {
        byte[] bas = sSrc.getBytes();
        return Base64.encodeToString(bas, Base64.DEFAULT);
    }


    // 加密
   /* public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }*/

    /**
     * 解密
     */
    public static String Decrypt(String sSrc) {
        byte[] bas = Base64.decode(sSrc, Base64.URL_SAFE);
        return new String(bas);
    }

    /**
     * 解密
     */
    public static String simpleDecrypt(String sSrc) {
        if (TextUtils.isEmpty(sSrc)) {
            return "";
        }
        byte[] bas = Base64.decode(sSrc, Base64.URL_SAFE);
        int half = bas.length / 2;
        for (int i = 0; i < half; i++) {
            if (i % 2 == 1) {
                //奇数位调换
                byte b = bas[i];
                bas[i] = bas[i + half];
                bas[i + half] = b;
            }
        }
        //按位取反
        for (int i = 0; i < bas.length; i++) {
            bas[i] = (byte) ~bas[i];
        }
        return new String(bas);
    }

    /**
     * 解密用的Base64.DEFAULT
     */
    public static String simpleDefaultDecrypt(String sSrc) {
        if (TextUtils.isEmpty(sSrc)) {
            return "";
        }
        byte[] bas = Base64.decode(sSrc, Base64.DEFAULT);
        int half = bas.length / 2;
        for (int i = 0; i < half; i++) {
            if (i % 2 == 1) {
                //奇数位调换
                byte b = bas[i];
                bas[i] = bas[i + half];
                bas[i + half] = b;
            }
        }
        //按位取反
        for (int i = 0; i < bas.length; i++) {
            bas[i] = (byte) ~bas[i];
        }
        return new String(bas);
    }


    /**
     * 根据图片地址转换为base64编码字符串
     */
    public static String getImageStr(String imgFile) {
        if (TextUtils.isEmpty(imgFile)) {
            return null;
        }
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (data == null) {
            return null;
        }
        // 加密
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * base64Str 进行保存
     *
     * @param path   要保存的路径
     * @param base64 base64字符
     */
    public static void saveImage(String path, String base64) {
        byte[] b = Base64.decode(base64, Base64.DEFAULT);
        OutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(b);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成base64编码
     *
     * @param binaryData
     * @return
     */
    public static String base64Encode(byte[] binaryData) {
        String encodedstr = Base64.encodeToString(binaryData, Base64.DEFAULT);
        return encodedstr;
    }

    /**
     * 生成hmacsha1签名
     *
     * @param binaryData
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] HmacSha1(byte[] binaryData, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        mac.init(secretKey);
        byte[] HmacSha1Digest = mac.doFinal(binaryData);
        return HmacSha1Digest;
    }

    /**
     * 生成hmacsha1签名
     *
     * @param plainText
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] HmacSha1(String plainText, String key) throws Exception {
        return HmacSha1(plainText.getBytes(), key);
    }

    /**
     * 获取指定长度的随机数
     * AES加密必须16位长度的key
     */
    public static String random(int length) {
        StringBuilder buffer = new StringBuilder("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuilder sb = new StringBuilder();
        SecureRandom r = new SecureRandom();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }

}
