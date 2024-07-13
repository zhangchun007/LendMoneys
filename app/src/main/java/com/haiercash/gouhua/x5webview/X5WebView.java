//package com.haiercash.gouhua.x5webview;
//
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//import android.widget.ProgressBar;
//
//import androidx.core.content.ContextCompat;
//
//import com.app.haiercash.base.utils.router.ActivityUntil;
//import com.haiercash.gouhua.R;
//import com.haiercash.gouhua.utils.UiUtil;
//import com.tencent.smtt.export.external.interfaces.JsPromptResult;
//import com.tencent.smtt.export.external.interfaces.WebResourceError;
//import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
//import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
//import com.tencent.smtt.sdk.CookieSyncManager;
//import com.tencent.smtt.sdk.DownloadListener;
//import com.tencent.smtt.sdk.URLUtil;
//import com.tencent.smtt.sdk.ValueCallback;
//import com.tencent.smtt.sdk.WebChromeClient;
//import com.tencent.smtt.sdk.WebSettings;
//import com.tencent.smtt.sdk.WebView;
//import com.tencent.smtt.sdk.WebViewClient;
//
//import static android.app.Activity.RESULT_OK;
//
///**
// * Created by Sun on 2017/8/12.
// */
//
//public class X5WebView extends WebView {
//
//    private WebViewPageStarted webViewPageStarted;
//    private WebViewPageFinished webViewPageFinished;
//    private WebViewShouldOverride webViewShouldOverride;
//    //6.0以上系统404或500错误码拦截监听
//    private WebViewOnReceivedHttpError webViewOnReceivedHttpError;
//    private WebViewOnReceivedTitle webViewOnReceivedTitle;
//    //6.0以上断网或者网络连接超时监听
//    private WebViewOnReceivedError webViewOnReceivedError;
//    //6.0以下断网或者网络连接超时监听
//    private WebViewLowOnReceivedError webViewLowOnReceivedError;
//    private OnScrollChangeListener mScrollChangeListener;
//    private WebViewFileChoose mWebViewFileChoose;
//    private WebViewonJsPrompt webViewonJsPrompt;
//    private ProgressBar progress;
//
//    private ValueCallback<Uri> uploadFile;
//    private ValueCallback<Uri[]> uploadFiles;
//
//
//    public X5WebView(Context context) {
//        super(context);
//        init();
//    }
//
//    public X5WebView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }
//
//    public void setProgressVisibility(int visibility) {
//        if (progress != null) {
//            if (visibility == View.GONE) {
//                progress = null;
//            }
//        }
//    }
//
//    public X5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//    }
//
//    public void setWebViewonJsPrompt(WebViewonJsPrompt webViewonJsPrompt) {
//        this.webViewonJsPrompt = webViewonJsPrompt;
//    }
//
//    public void setWebViewOnReceivedTitle(WebViewOnReceivedTitle webViewOnReceivedTitle) {
//        this.webViewOnReceivedTitle = webViewOnReceivedTitle;
//    }
//
//    public void setWebViewOnReceivedError(WebViewOnReceivedError webViewOnReceivedError) {
//        this.webViewOnReceivedError = webViewOnReceivedError;
//    }
//
//    public void setWebViewLowOnReceivedError(WebViewLowOnReceivedError webViewLowOnReceivedError) {
//        this.webViewLowOnReceivedError = webViewLowOnReceivedError;
//    }
//
//    public void setWebViewOnReceivedHttpError(WebViewOnReceivedHttpError webViewOnReceivedHttpError) {
//        this.webViewOnReceivedHttpError = webViewOnReceivedHttpError;
//    }
//
//    public void setWebViewPageStarted(WebViewPageStarted webViewPageStarted) {
//        this.webViewPageStarted = webViewPageStarted;
//    }
//
//    public void setWebViewPageFinished(WebViewPageFinished webViewPageFinished) {
//        this.webViewPageFinished = webViewPageFinished;
//    }
//
//    public void setWebViewShouldOverride(WebViewShouldOverride webViewShouldOverride) {
//        this.webViewShouldOverride = webViewShouldOverride;
//    }
//
//    /**
//     * 获取特殊字符串头，是一种向访问网站提供你所使用的浏览器类型及版本、操作系统及版本、浏览器内核、等信息的标识
//     */
//    public void setUserAgent(String type) {
//        WebSettings webSetting = this.getSettings();
//        webSetting.setUserAgent(webSetting.getUserAgentString() + type);
//    }
//
//    /**
//     * WebView 初始化，设置监听，删除部分Android默认注册的JS接口
//     */
//    private void init() {
//        //WebViewClient帮助webview去处理一些页面控制和请求通知
//        this.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if (webViewShouldOverride != null) {
//                    return webViewShouldOverride.shouldOverrideUrlLoading(url);
//                } else if (URLUtil.isNetworkUrl(url)) {
//                    return false;
//                } else {
//                    ActivityUntil.startOtherApp(view.getContext(), url);
//                }
//                return true;
//            }
//
//            @TargetApi(android.os.Build.VERSION_CODES.M)
//            @Override
//            public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
//                super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
//                if (webViewOnReceivedHttpError != null) {
//                    webViewOnReceivedHttpError.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
//                }
//            }
//
//            @Override
//            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
//                super.onReceivedError(webView, webResourceRequest, webResourceError);
//                //6.0以上执行
//                if (webViewOnReceivedError != null) {
//                    webViewOnReceivedError.onReceivedError(webView, webResourceRequest, webResourceError);
//                }
//            }
//
//            @Override
//            public void onReceivedError(WebView webView, int i, String s, String s1) {
//                super.onReceivedError(webView, i, s, s1);
//                //6.0以下执行
//                if (webViewLowOnReceivedError != null) {
//                    webViewLowOnReceivedError.onReceivedError(webView, i, s, s1);
//                }
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                //UiUtil.toastLongTime("url=" + url);
//                // 网页页面开始加载的时候
//                if (webViewPageStarted != null) {
//                    webViewPageStarted.onPageStarted(url);
//                }
//                super.onPageStarted(view, url, favicon);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                if (webViewPageFinished != null) {
//                    webViewPageFinished.onPageFinished(view, url);
//                }
//            }
//
//            @Override
//            public void onLoadResource(WebView webView, String s) {
//                System.out.println("X5WebView -> onLoadResource->" + s);
//                super.onLoadResource(webView, s);
//            }
//        });
//        this.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
//                System.out.println("X5WebView -> onProgressChanged->" + newProgress);
//                if (newProgress <= 2 && progress.getVisibility() == View.GONE) {
//                    progress.setVisibility(VISIBLE);
//                } else if (newProgress >= 80 && progress.getVisibility() == View.VISIBLE) {
//                    progress.setVisibility(View.GONE);
//                } else if (progress.getVisibility() == View.VISIBLE) {
//                    progress.setProgress(newProgress);
//                }
//            }
//
//            @Override
//            public void onReceivedTitle(WebView webView, String s) {
//                super.onReceivedTitle(webView, s);
//                if (webViewOnReceivedTitle != null) {
//                    webViewOnReceivedTitle.onReceivedTitle(webView, s);
//                }
//
//            }
//
//            @Override
//            public boolean onJsPrompt(WebView webView, String url, String message, String defaultValue, JsPromptResult jsPromptResult) {
//                if (webViewonJsPrompt != null) {
//                    if (webViewonJsPrompt.onJsPrompt(webView, url, message, defaultValue, jsPromptResult)) {
//                        return true;
//                    } else {
//                        return super.onJsPrompt(webView, url, message, defaultValue, jsPromptResult);
//                    }
//                } else {
//                    return super.onJsPrompt(webView, url, message, defaultValue, jsPromptResult);
//                }
//            }
//
//            // For Android  > 4.1.1
//            @Override
//            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
//                uploadFile = uploadMsg;
//                onFileChoose();
//            }
//
//            // For Android  >= 5.0
//            @Override
//            public boolean onShowFileChooser(com.tencent.smtt.sdk.WebView webView,
//                                             ValueCallback<Uri[]> filePathCallback,
//                                             WebChromeClient.FileChooserParams fileChooserParams) {
//                uploadFiles = filePathCallback;
//                onFileChoose();
//                return true;
//            }
//        });
//        safeSetting();
//        initProgressBar();
//    }
//
//
//    private void onFileChoose() {
//        if (mWebViewFileChoose != null) {
//            mWebViewFileChoose.onFileChoose();
//        }
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (null != uploadFile) {
//            Uri result = data == null || resultCode != RESULT_OK ? null
//                    : data.getData();
//            uploadFile.onReceiveValue(result);
//            uploadFile = null;
//        }
//        if (null != uploadFiles) {
//            Uri result = data == null || resultCode != RESULT_OK ? null
//                    : data.getData();
//            uploadFiles.onReceiveValue(new Uri[]{result});
//            uploadFiles = null;
//        }
//    }
//
//    private void initProgressBar() {
//        progress = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
//        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiUtil.dip2px(getContext(), 2), 0);
//        progress.setLayoutParams(layoutParams);
//        progress.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.webview_progressbar));
//        this.addView(progress);
//    }
//
//
//    public interface WebViewPageStarted {
//
//        /**
//         * 页面开始加载
//         */
//        void onPageStarted(String url);
//    }
//
//    public interface WebViewShouldOverride {
//
//        /**
//         * 页面开始load
//         */
//        boolean shouldOverrideUrlLoading(String url);
//    }
//
//    public interface WebViewOnReceivedHttpError {
//        /**
//         * webView 加载中Http报错
//         */
//        void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse);
//    }
//
//    public interface WebViewOnReceivedError {
//        /**
//         * webView加载失败
//         */
//        void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError);
//    }
//
//    public interface WebViewLowOnReceivedError {
//        /**
//         * webView加载失败
//         */
//        void onReceivedError(WebView webView, int errorCode, String description, String failingUrl);
//    }
//
//    public interface WebViewOnReceivedTitle {
//        void onReceivedTitle(WebView webView, String title);
//    }
//
//    public interface WebViewonJsPrompt {
//        boolean onJsPrompt(WebView webView, String url, String message, String defaultValue, JsPromptResult jsPromptResult);
//    }
//
//    public interface WebViewPageFinished {
//        void onPageFinished(WebView view, String url);
//    }
//
//    @Override
//    public void destroy() {
//        webViewPageStarted = null;
//        webViewPageFinished = null;
//        webViewShouldOverride = null;
//        mScrollChangeListener = null;
//        mWebViewFileChoose = null;
//        uploadFile = null;
//        uploadFiles = null;
//        try {
//            ViewParent parent = progress.getParent();
//            if (parent != null) {
//                ((ViewGroup) parent).removeView(this);
//            }
//
//            ViewParent webParent = getParent();
//            if (webParent != null) {
//                ((ViewGroup) webParent).removeView(this);
//            }
//            stopLoading();
//            removeAllViews();
//            clearCache(true);
//            clearHistory();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        super.destroy();
//    }
//
//    /**
//     * 安全性设置
//     */
//    private void safeSetting() {
//        WebSettings webSetting = this.getSettings();
//        webSetting.setJavaScriptEnabled(true);//设置能够解析Javascript
//        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSetting.setAllowFileAccess(true);
//        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//        webSetting.setSupportZoom(true);
//        webSetting.setBuiltInZoomControls(true);
//        webSetting.setUseWideViewPort(true);
//        webSetting.setSupportMultipleWindows(true);
//        webSetting.setAppCacheEnabled(true);
//        webSetting.setDomStorageEnabled(true);
//        webSetting.setGeolocationEnabled(true);
//        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
//        webSetting.setUserAgent(webSetting.getUserAgentString() + "gouhua_and");
//        webSetting.setAppCachePath(this.getContext().getDir("appcache", 0).getPath());
//        webSetting.setGeolocationDatabasePath(this.getContext().getDir("geolocation", 0)
//                .getPath());
//
//        webSetting.setLoadWithOverviewMode(true);
//        /*
//         * 对系统API在19以上的版本作了兼容。
//         * 因为4.4以上系统在onPageFinished时再恢复图片加载时,
//         * 如果存在多张图片引用的是相同的src时，会只有一个image标签得到加载，因而对于这样的系统我们就先直接加载。
//         */
//        if (Build.VERSION.SDK_INT >= 19) {
//            webSetting.setLoadsImagesAutomatically(true);
//        } else {
//            webSetting.setLoadsImagesAutomatically(false);
//        }
//        /*
//         *  Webview在安卓5.0之前默认允许其加载混合网络协议内容
//         *  在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
//         */
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
////        LOAD_CACHE_ONLY： 不使用网络，只读取本地缓存数据，
////        LOAD_DEFAULT：根据cache-control决定是否从网络上取数据，
////        LOAD_CACHE_NORMAL：API level 17中已经废弃, 从API level 11开始作用同- - LOAD_DEFAULT模式，
////        LOAD_NO_CACHE: 不使用缓存，只从网络获取数据，
////        LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
//        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        CookieSyncManager.createInstance(this.getContext());
//        CookieSyncManager.getInstance().sync();
//        this.setDownloadListener(new ApkDownloadListener());
//        setClickable(true);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && this.canGoBack()) {
//            this.goBack();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//
//    class ApkDownloadListener implements DownloadListener {
//
//        @Override
//        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//            DownloadByBrowsable(url);
//        }
//    }
//
//    /**
//     * 由浏览器来进行下载安装
//     */
//    private void DownloadByBrowsable(String url) {
//        Uri uri = Uri.parse(url);
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        intent.addCategory(Intent.CATEGORY_BROWSABLE);
//        this.getContext().startActivity(intent);
//    }
//
//    /**
//     * 滑动监听
//     */
//    @Override
//    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        super.onScrollChanged(l, t, oldl, oldt);
//        if (mScrollChangeListener != null) {
//            mScrollChangeListener.onScrollChanged(t);
//        }
//    }
//
//    public void setScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
//        mScrollChangeListener = onScrollChangeListener;
//    }
//
//    public void setWebViewFileChoose(WebViewFileChoose fileChoose) {
//        mWebViewFileChoose = fileChoose;
//    }
//
//    public interface OnScrollChangeListener {
//        void onScrollChanged(int y);
//    }
//
//    public boolean goWebBack() {
//        if (canGoBack()) {
//            goBack();
//            return true;
//        }
//        return false;
//    }
//
//    public interface WebViewJavaScriptFunction {
//        void onJsFunctionCalled(String tag);
//    }
//
//
//    public interface WebViewFileChoose {
//        void onFileChoose();
//    }
//}
