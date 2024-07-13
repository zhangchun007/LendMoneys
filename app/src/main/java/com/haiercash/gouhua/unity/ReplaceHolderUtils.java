package com.haiercash.gouhua.unity;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 替换字符串中带有${占位符}的字符串
 *
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/12/3
 * @Version: 1.0
 */
public class ReplaceHolderUtils {

    /**
     * 测试代码
     *
     * @param arg
     */
    public static void main(String arg[]) {
//        String defaultStr = "https://standardpay-p2.haiercash.com/couponList?processId=${processId}&channelNo=${channelNo}&firstSpell=${firstSpell}";
        String defaultStr = "${imgSourceApi}/csmp-manager/iconapp/vipTag_chuangKe.png";
        HashMap<String, Object> mp = new HashMap<>();
        mp.put("imgSourceApi", "https://test-oss-s3.haiercash.com");
        mp.put("channelNo", "10");
        mp.put("firstSpell2", "HD");
        String replaceKeysWithValues = replaceKeysWithValues(defaultStr, mp);
        System.out.println("ReplaceHolderUtils--" + replaceKeysWithValues);
    }


    /**
     * 替换字符串中带有${占位符}的字符串
     *
     * @param str 模版字符串
     * @param map 占位符对应的map
     * @return
     */
    public static String replaceKeysWithValues(String str, Map<String, Object> map) {
        try {
            if (TextUtils.isEmpty(str)) {
                return "";
            }
            if (!str.contains("${")){
                return str;
            }
            Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
            Matcher matcher = pattern.matcher(str);

            if (map == null) {
                return "";
            }
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String key = matcher.group(1);
                String value = getPropertyValue(map, key);
                if (!TextUtils.isEmpty(value)) {
                    matcher.appendReplacement(sb, value);
                } else {
                    matcher.appendReplacement(sb, "");
                }
            }
            matcher.appendTail(sb);
            return sb.toString();
        } catch (Exception e) {
        }

        return "";
    }


    /**
     * 是否展示
     *
     * @param str
     * @return
     */
    public static boolean isShowView(String str) {
        return "1".equals(str);
    }

    /**
     * 替换defaultShow字符串中带有${占位符}的字符串 替换不到则未0
     *
     * @param str 模版字符串
     * @param map 占位符对应的map
     * @return
     */
    public static String defaultShowWithValues(String str, Map<String, Object> map) {
        try {
            if ("0".equals(str) || "1".equals(str)) {
                return str;
            }
            if (TextUtils.isEmpty(str)) {
                return "0";
            }
            Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
            Matcher matcher = pattern.matcher(str);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                String key = matcher.group(1);
                String value = getPropertyValue(map, key);

                if (!TextUtils.isEmpty(value)) {
                    matcher.appendReplacement(sb, value);
                } else {
                    matcher.appendReplacement(sb, "0");
                }
            }
            matcher.appendTail(sb);
            return sb.toString();
        } catch (Exception e) {
        }
        return "0";
    }

    private static String getPropertyValue(Map<String, Object> map, String propertyName) {
        if (map != null && map.get(propertyName) != null) {
            return map.get(propertyName).toString();
        }
        return "";
    }


}
