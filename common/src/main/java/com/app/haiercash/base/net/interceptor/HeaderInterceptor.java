package com.app.haiercash.base.net.interceptor;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.retrofit.RetrofitFactory;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.system.CheckUtil;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: Sun
 * Date :    2018/2/8
 * FileName: HeaderInterceptor
 * Description: 拦截方式加入请求头
 */

public class HeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Headers commonHeaders = NetConfig.initRequestHeader();

        Request originalRequest = chain.request();
        //如果公共请求头不为空,则构建新的请求
        if (commonHeaders != null) {
            Request.Builder requestBuilder = originalRequest.newBuilder();
            //先添加固定头，使用 headers 比header循环可大幅度提升速度
            requestBuilder.headers(commonHeaders);
            requestBuilder.addHeader("ideaCode", NetConfig.TD_IDEA_SMY);
            requestBuilder.addHeader("appDownFrom", NetConfig.TD_APP_DOWN_FROM_SMY);//投放渠道,APPdownfrom为中文的用户在够花报错，现在只要求在注册接口增加

            String url = getCurrentUrl(originalRequest.url().toString());
            String token = TokenHelper.getInstance().getCacheToken();
            //是否添加token进行判断
            if (!TextUtils.isEmpty(token) && RetrofitFactory.getInstance().getServiceValue(url)) {
                requestBuilder.addHeader("Authorization", "Bearer" + token)
                        .addHeader("access_token", token);
            }
            if (!CheckUtil.isEmpty(token) && RetrofitFactory.getInstance().getBusinessValue(url)) {
                requestBuilder//创意code
                        .addHeader("registerVector", TokenHelper.getInstance().getSmyParameter("registerVector"))//注册载体
                        .addHeader("business", TokenHelper.getInstance().getSmyParameter("business"))//运营业务线
                        .addHeader("regisChannel", TokenHelper.getInstance().getSmyParameter("regisChannel"));//注册渠道
            } else {
                requestBuilder
                        .addHeader("registerVector", NetConfig.TD_REGIS_VECTOR_SMY)//注册载体
                        .addHeader("business", NetConfig.TD_BUSINESS_SMY)//运营业务线
                        .addHeader("regisChannel", "");//注册渠道
            }

            requestBuilder.method(originalRequest.method(), originalRequest.body());
            Request newRequest = requestBuilder.build();
            return chain.proceed(newRequest);
        }
        return chain.proceed(originalRequest);
    }

    private String getCurrentUrl(String url) {
        if (!CheckUtil.isEmpty(url) && url.contains(NetConfig.API_SERVER_URL)) {
            int start = NetConfig.API_SERVER_URL.length();
            int end = url.indexOf("?");
            if (end == -1) {
                end = url.length();
            }
            return url.substring(start, end);
        } else {
            return url;
        }
    }
}
