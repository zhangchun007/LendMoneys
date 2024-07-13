package com.app.haiercash.base.utils.encrypt;

import android.text.TextUtils;

import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 8/22/22
 * @Version: 1.0
 */
public class HmacSHA256Utils {

    public static final String secret = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv8OS2d8xKj9673VEW1/6O7I75b4F3lcE8+j4VXpQLLDCTHZANzcOHDROpKc38H9NklL67USrqFZULqFGh+iz5EGg5T7uOjkuIgGs0y5oYngLJleDaK+c3iE6fvs2Y9Tt1mn90Icu/5mcEpmqvcDhhZboYx7vF5LveXIYo037vJ6TGd7ephWSESrjxWbL8lvldfgv8/dj/SN1ZFUW9rw0+SqU5U8JbJ5x5fcXRbLSKYGYIcDmyK/tWjVcoPCnFOLrAVAyDs0/ntdKTPuKzYT9v/V13qVHxlpWQBlX9tRqZ1FOJNJapRUdkccTzDWRg9NKtqjNPbm57QUwBj5LdRSY0wIDAQAB";


    public static String buildNeedSignValue(Map request) {
        if (request == null) return "";
        StringBuilder builder = new StringBuilder();
        Set<String> keySet = request.keySet();
        ArrayList<String> keys = new ArrayList<>(keySet);
        Collections.sort(keys);
        for (String key : keys) {
            if ("sign".equalsIgnoreCase(key)) continue;
            if (!TextUtils.isEmpty(key) && !("xfList".equals(key) || "isRsa".equals(key))) {
                Object valueObj = request.get(key);
                if (!CheckUtil.isEmpty(valueObj)) {
                    builder.append(key).append("=").append(valueObj.toString().trim()).append("&");
                }
            }
        }
        String needSignValue = builder.toString();
        return sha256_HMAC(needSignValue.substring(0, needSignValue.length() - 1));
    }


    /**
     * sha256_HMAC加密
     *
     * @param message 消息
     * @return 加密后字符串
     */
    public static String sha256_HMAC(String message) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            System.out.println("Error HmacSHA256 ===========" + e.getMessage());
        }
        return hash;
    }

    /**
     * 将加密后的字节数组转换成字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }


}
