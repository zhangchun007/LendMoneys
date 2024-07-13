package com.haiercash.gouhua.jsweb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.URLUtil;

import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Web相关帮助类
 */
public class WebHelper {
    /**
     * 根据url来区别跳转页面逻辑
     *
     * @param activity 本身Activity
     * @param url      http/https链接或者scheme路由(跳转第三方app或者自身app某个页面，需预先配置)
     * @return 能跳转成功就是true，不成功就是false，方便处理跳转是否成功的逻辑
     */
    public static boolean startActivityForUrl(BaseActivity activity, String url, Bundle bundle) {
        final String urlKey = "jumpKey";
        if (CheckUtil.isEmpty(url)) {
            if (bundle == null || !bundle.containsKey(urlKey) || !(bundle.get(urlKey) instanceof String) || CheckUtil.isEmpty(bundle.getString(urlKey))) {
                return false;
            } else {
                url = bundle.getString(urlKey);
            }
        }
        if (url.contains("ghContainer://WXPublic")) {
            //banner跳转特殊处理url，跳转到app页面“微信公众号”
            WebHelper.jumpWxPublic(activity);
        } else if (url.contains("gouhua://")) {
            url = url.substring(url.indexOf("gouhua://"));
            ActivityUntil.startOtherApp(activity, url);
        } else if (URLUtil.isNetworkUrl(url)) {
            Intent intent = new Intent();
            intent.setClass(activity, JsWebBaseActivity.class);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.putExtra(urlKey, url);//放在最后put，最终url覆盖bundle的可能含有的url
            activity.startActivity(intent);
        } else {
            return ActivityUntil.startOtherApp(activity, url);
        }
        return true;
    }

    public static boolean startActivityForUrl(BaseActivity activity, String url) {
        return startActivityForUrl(activity, url, null);
    }

    /**
     * 给链接拼接参数
     */
    public static String addUrlParam(String url, Map<String, String> keyValues) {
        if (CheckUtil.isEmpty(url)) {
            return "";
        }
        if (keyValues == null || CheckUtil.isEmpty(keyValues.keySet())) {
            return url;
        }
        String paramStr = "";
        for (String key : keyValues.keySet()) {
            if (CheckUtil.isEmpty(key)) {
                continue;
            }
            paramStr = UiUtil.getStr(paramStr, (url.contains("?") || paramStr.contains("?") ? "&" : "?"), key, "=", keyValues.get(key));
        }
        return UiUtil.getStr(url, paramStr);
    }

    /**
     * 去掉path前方自带的目录符号，因为前方拼接的baseUrl最后有携带
     */
    public static String formatMyUrlPath(String urlPath) {
        return CheckUtil.isEmpty(urlPath) || !urlPath.startsWith("/") ? urlPath : urlPath.substring(1);
    }

    /**
     * 根据url获取携带参数，均已字符串形式存储在map中
     */
    public static HashMap<String, String> getUrlParams(String url) {
        HashMap<String, String> map = new HashMap<>();
        if (CheckUtil.isEmpty(url) || !url.contains("?")) {
            return map;
        }
        try {
            String tmp = "";
            String paramStr = url.substring(url.indexOf("?") + 1);
            if (paramStr.contains("?")) {
                tmp = paramStr.substring(paramStr.indexOf("?"));
                paramStr = paramStr.substring(0, paramStr.indexOf("?"));
            }
            String[] splits = paramStr.split("&", -2);
            for (int i = 0; i < splits.length; i++) {
                String s = splits[i];
                try {
                    String[] split = s.split("=", 2);
                    map.put(split[0], split[1] + (i == splits.length - 1 ? tmp : ""));
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 跳转微信公众号
     */
    public static void jumpWxPublic(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            Intent intent = new Intent(activity, JsWebBaseActivity.class);
            intent.putExtra("title", activity.getString(R.string.setting_wx));
            intent.putExtra("jumpKey", BuildConfig.SPECIAL_H5_URL + activity.getString(R.string.wx_public));
            activity.startActivity(intent);
        } catch (Exception e) {
            //
        }
    }
}
