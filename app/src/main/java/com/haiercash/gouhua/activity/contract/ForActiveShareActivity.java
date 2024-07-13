package com.haiercash.gouhua.activity.contract;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.x5webview.CusWebView;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/1/7<br/>
 * 描    述：<br/>
 * 修订历史：活动页面，用于分享的页面<br/>
 * ================================================================
 */
public class ForActiveShareActivity extends BaseActivity {
    private static final String WEB_TITLE = "title";
    private static final String WEB_URL = "url";

    @BindView(R.id.wv_contract_treaty)
    CusWebView x5WebView;

    private String mUrl;
    private String mTitle;

    @Override
    protected int getLayout() {
        return R.layout.activity_for_share;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        mUrl = getIntent().getStringExtra(WEB_URL);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        mTitle = getIntent().getStringExtra(WEB_TITLE);//海尔消费金融
        if (CheckUtil.isEmpty(mUrl)) {
            if (getIntent().getData() != null) {
                mUrl = getIntent().getData().getQueryParameter(WEB_URL);
                mTitle = getIntent().getData().getQueryParameter(WEB_TITLE);
            }
        }
        mTitle = CheckUtil.isEmpty(mTitle) ? "春节活动" : mTitle;
        //标题
        setTitle(mTitle);
        setCookie();
        //内容
        if (x5WebView != null && !TextUtils.isEmpty(mUrl)) {
            x5WebView.setUserAgent("gouhuaapp");
            if (mUrl.contains("?")) {
                mUrl = mUrl + "&isLatestVersion=true";
            } else {
                mUrl = mUrl + "?isLatestVersion=true";
            }
            x5WebView.loadUrl(mUrl);
            x5WebView.getWebIHelper().setWebViewPageFinished((url) -> {
                if (url.equals(mUrl)) {
                    x5WebView.clearHistory();
                }
            });
        }

        setRightImage(R.drawable.icon_share, v -> {
            String url = x5WebView.getUrl();
            if (url.contains("&sharePage=")) {
                try {
                    url = url.substring(url.indexOf("&sharePage=")).replace("&sharePage=", "");
                    url = URLDecoder.decode(url, "UTF-8");
                    SharePageActivity.showSharePage(mContext, url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                SharePageActivity.showSharePage(mContext, x5WebView.getUrl());
            }
        });

        RxBus.getInstance().addSubscription(this, RxBus.getInstance().register(ActionEvent.class, actionEvent -> {
            if (actionEvent.getActionType() == ActionEvent.WxShareSuccess) {
                if (!CheckUtil.isEmpty(AppApplication.userid)) {
                    showProgress(true);
                    Map<String, String> map = new HashMap<>();
                    map.put("userId", EncryptUtil.simpleEncrypt(AppApplication.userid));
                    netHelper.getService(ApiUrl.USER_SHARE_RECORD, map);
                }
            }
        }));
    }

    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        x5WebView.loadUrl(mUrl);
        showProgress(false);
    }

    @Override
    protected void onDestroy() {
        RxBus.getInstance().unSubscribe(this);
        if (x5WebView != null) {
            x5WebView.destroy();
            x5WebView = null;
        }
        super.onDestroy();
    }

    public static void showSharePage(Context context, String url, String title) {
        Intent intent = new Intent(context, ForActiveShareActivity.class);
        intent.putExtra(WEB_URL, url);
        intent.putExtra(WEB_TITLE, title);
        //ContainerActivity.toForResult(baseActivity, ID, bundle, REQUEST_CODE);
        context.startActivity(intent);
    }


    private void setCookie() {
        String strCookie = "userId=" + EncryptUtil.simpleEncrypt(AppApplication.userid) + ";path=/";
        String strCookieToken = "token=" + TokenHelper.getInstance().getCacheToken() + ";path=/";
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(null);
            cookieManager.flush();
        } else {
            cookieManager.removeSessionCookie();
            CookieSyncManager.getInstance().sync();
        }
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(mUrl, strCookie);
        cookieManager.setCookie(mUrl, strCookieToken);
    }

    @Override
    public void onBackPressed() {
        if (x5WebView != null && x5WebView.canGoBack()) {
            x5WebView.goWebBack();
        } else {
            super.onBackPressed();
        }
    }
}
