package com.haiercash.gouhua.x5webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/5/11<br/>
 * 描    述：android中Scrollview嵌套WebView问题<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class NoScrollWebView extends CusWebView {

    public NoScrollWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NoScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollWebView(Context context) {
        super(context);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void loadWebUrl(String url) {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        //启用应用缓存
        webSettings.setAppCacheEnabled(false);
        webSettings.setDatabaseEnabled(false);
        //开启DOM缓存，关闭的话H5自身的一些操作是无效的
        webSettings.setDomStorageEnabled(true);
        //适应屏幕
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

//        setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                //解决android7.0以后版本加载异常问题
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    view.loadUrl(request.getUrl().toString());
//                } else {
//                    view.loadUrl(request.toString());
//                }
//                return true;
//            }
//        });
        getWebIHelper().setWebViewShouldOverride2(request -> {
            //解决android7.0以后版本加载异常问题
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                loadUrl(((WebResourceRequest) request).getUrl().toString());
            } else {
                loadUrl(request.toString());
            }
            return true;
        });
        loadUrl(url);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
