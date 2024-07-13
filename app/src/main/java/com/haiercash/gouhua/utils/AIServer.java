package com.haiercash.gouhua.utils;

import android.net.Uri;

import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.InfoEncryptUtil;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.interfaces.SpKey;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Author: Sun
 * Date :    2019/1/8
 * FileName: AIServer
 * Description:
 */
public class AIServer {

    private static final String AISERVICEURL = "https://kf.haiercash.com/kf/chatClient/chatbox.jsp";
    //private static final String AISERVICEURL = "http://testpm.haiercash.com:30002/kf/chatClient/chatbox.jsp";

    /**
     * 跳转客服界面
     */
    public static void showAiWebServer(BaseActivity mActivity, String enterUrl) {
        try {
            String url = AIServer.getUrl(enterUrl);
            WebSimpleFragment.WebService(mActivity, url, "在线客服", WebSimpleFragment.STYLE_OTHERS);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 账户注销
     */
    public static void showCloseCountWebServer(BaseActivity mActivity) {
        try {
            String url = AIServer.getCloseUrl();
            WebSimpleFragment.WebService(mActivity, url, "账户注销", WebSimpleFragment.STYLE_OTHERS);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 获取智能客服url
     */
    private static String getUrl(String enterurl) throws Exception {
        Uri.Builder builder = Uri.parse(AISERVICEURL).buildUpon();
        builder.appendQueryParameter("companyID", "9068");
        builder.appendQueryParameter("configID", "13");
        builder.appendQueryParameter("labels", "gh");
        builder.appendQueryParameter("enterurl", enterurl);
        builder.appendQueryParameter("pagereferrer", "够花APP");
        builder.appendQueryParameter("info", getUserInfo());
        return builder.toString();
    }

   /**
     * 获取智能客服url
     */
    private static String getCloseUrl() throws Exception {
        Uri.Builder builder = Uri.parse(BuildConfig.CLOSE_ACCOUNT_URL).buildUpon();
        return builder.toString();
    }

    private static String getUserInfo() throws Exception {
        String userId = SpHp.getLogin(SpKey.LOGIN_MOBILE);
        String name = SpHp.getUser(SpKey.USER_CUSTNAME, "未设置姓名");
        String timestamp = System.currentTimeMillis() + "";
        String key = "live800_key";
        if (CheckUtil.isEmpty(userId)) {
            LoginSelectHelper.staticToGeneralLogin();
            throw new Exception("AIServer : Online Service userId is NuLL");
        }
        String hashCode = userId + name + timestamp + key;
        try {
            hashCode = URLEncoder.encode(hashCode, "utf-8").toUpperCase();
            hashCode = EncryptUtil.string2MD5(hashCode).toUpperCase();
            String info = "userId=" + userId + "&name=" + name + "&timestamp=" + timestamp + "&hashCode=" + hashCode;
            info = URLEncoder.encode(info, "UTF-8");
            return InfoEncryptUtil.encrypt(info);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
