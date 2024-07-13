package com.haiercash.gouhua.fragments.main;

import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.BillBearLoginPop;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/3/28<br/>
 * 描    述：首页会员
 * 修订历史：<br/>
 * ================================================================
 */
public class HomeLeaguerFragment extends BaseHomeWebFragment {
    private BillBearLoginPop bearLoginPop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUrl = ApiUrl.URL_LEAGUER;
        //"file:///android_asset/h5AndAppDemo.html";
        //"http://101.132.174.132/ceshi/h5AndAppDemo.html";
//        mCurrentUrl = "file:///android_asset/login.htm";
    }

    @Override
    protected void initEventAndData() {
        viewPageType = VIEW_VERTICAL;
        super.initEventAndData();
        setStatusBarTextColor(false);
        getBinding().headClose.setVisibility(View.GONE);
        getBinding().headLeft.setVisibility(View.GONE);
        setTitle("精选");
        getBinding().wvcView.getWebIHelper().setScrollChangeListener((l, h, oldl, oldt) -> {
            Logger.e("x5WebView.setScrollChangeListener：" + h);
            getBinding().swipeRefreshLayout.setEnabled(h == 0);
        });
        getBinding().wvcView.getWebIHelper().setWebViewShouldOverride((url) -> {
            Logger.e("HomeLeaguerFragment->WebViewShouldOverride-> 加载的URL=" + url);
            return jmpWebUrl(url);
        });
        getBinding().swipeRefreshLayout.setEnabled(false);
//        x5WebView.addJavascriptInterface(new X5WebView.WebViewJavaScriptFunction() {
//            @Override
//            public void onJsFunctionCalled(String tag) {
//            }
//
//            @JavascriptInterface
//            public void isLogin(String url) {
//                jmpWebUrl(url);
//            }
//        }, "gouhua");
        setStyle(mCurrentUrl);
        //x5WebView.getWebIHelper().setWebViewPageStarted(url -> System.out.println("当前加载的地址setWebViewPageStarted：" + url));
//        x5WebView.setWebViewInterceptRequest((url) -> {
//            if (url != null && url.startsWith(mCurrentUrl)) {
//                return WebUntil.getParameterUrl(mCurrentUrl, AppApplication.isLogIn());
//            }
//            return url;
//        });
//        x5WebView.getWebIHelper().setWebViewPageFinished((url) -> {
//            System.out.println("mCurrentUrl = url：" + url);
        //if (url.equals(mCurrentUrl)) {
        //} else {
        //pageFinishedUrl = url;
        //x5WebView.goBack();
        //jmpWebUrl(url);
        //System.out.println("mCurrentUrl != url：" + url);
        //}
//        });
        loadUrl();
    }

    /**
     * 处理跳转链接
     */
    private boolean jmpWebUrl(String s) {
        if (!AppApplication.isLogIn()) {
            AppApplication.setLoginCallback(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    jmpWebUrl(s);
                }
            });
            gotoLoginIn();
        } else if (!mCurrentUrl.equals(s)) {
            if (s.contains("ghscheme://com.haiercash.gouhua/billbear_login")) {
                if (bearLoginPop == null) {
                    bearLoginPop = new BillBearLoginPop(mActivity, null);
                }
                bearLoginPop.checkLoginStatus(getBinding().llWebContent);
            } else if (URLUtil.isNetworkUrl(s)) {
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HOME_OTHER_WEB)
                        .put("isShowTitle", false).put("url", s).navigation();
            } else {
                UiUtil.toast("非系统处理的操作");
            }
        }
        UiUtil.toastDeBug("测试响应：" + s);
        return true;
    }

    @Override
    public void onDestroy() {
        if (bearLoginPop != null) {
            bearLoginPop.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        UMengUtil.pageStart("MemberPage");
        if (mActivity instanceof MainActivity && !mActivity.isShowingDialog()) {
            controlDialogUtil.checkDialog("member");
        }
        setStyle(mCurrentUrl);
    }

    @Override
    public void onPause() {
        super.onPause();
        UMengUtil.pageEnd("MemberPage");

    }

}
