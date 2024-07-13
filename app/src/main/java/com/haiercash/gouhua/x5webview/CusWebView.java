package com.haiercash.gouhua.x5webview;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.image.PhotographUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.networkbench.agent.impl.instrumentation.NBSWebChromeClient;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/4/21<br/>
 * 描    述：采用BridgeHelper集成JsBridge功能示例.定制WebView,可只添加实际需要的JsBridge接口.<br/>
 * 修订历史：与js交互的业务逻辑在WebDataImpl中处理<br/>
 * ================================================================
 */
public class CusWebView extends BridgeWebView {
    private WebInterfaceHelper webIHelper = new WebInterfaceHelper();

    private ProgressBar progress;
    private boolean showProgressBar = true;
    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;
    private boolean isCaptureEnabled = false;
    private String fileType = "*/*";
    private boolean mPageLoadFinished = false;
    private String appendUA;  //需要追加到UA后面的参数

    public CusWebView(Context context) {
        super(context);
        init();
        safeSetting();
    }

    public CusWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        safeSetting();
    }

    public CusWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        safeSetting();
    }

    private void init() {
        init("");
    }

    /**
     * WebView 初始化，设置监听，删除部分Android默认注册的JS接口
     */
    private void init(String userAgent) {
        appendUA = userAgent;
        String realUA = "gouhua_and";
        if (!CheckUtil.isEmpty(appendUA)) {
            realUA = realUA + " " + appendUA;
        }
        setUserAgent(realUA);
        //WebViewClient帮助webview去处理一些页面控制和请求通知
        this.setWebViewClient(new BridgeWebViewClient(this) {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                boolean loadFlag = super.shouldOverrideUrlLoading(view, url);
                if (WebDataImpl.IS_DEBUG_WEB) {
                    Logger.e("shouldOverrideUrlLoading:" + url);
                    Logger.e("shouldOverrideUrlLoading:" + loadFlag);
                }
                if (loadFlag) {
                    return true;
                } else if (webIHelper.getWebViewShouldOverride() != null) {
                    return webIHelper.getWebViewShouldOverride().shouldOverrideUrlLoading(url);
                } else if (URLUtil.isNetworkUrl(url)) {
                    return false;
                } else {
                    ActivityUntil.startOtherApp(getContext(), url);
                    return true;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (webIHelper.getWebViewShouldOverride2() != null) {
                    return webIHelper.getWebViewShouldOverride2().shouldOverrideUrlLoading(request);
                } else {
                    return super.shouldOverrideUrlLoading(view, request);
                }
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedHttpError(WebView webView, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
                super.onReceivedHttpError(webView, webResourceRequest, webResourceResponse);
                if (webIHelper.getWebViewOnReceivedHttpError() != null) {
                    webIHelper.getWebViewOnReceivedHttpError().onReceivedHttpError(webResourceRequest, webResourceResponse);
                }
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                //6.0以上执行
                if (webIHelper.getWebViewOnReceivedError() != null) {
                    webIHelper.getWebViewOnReceivedError().onReceivedError(webResourceRequest, webResourceError);
                }
            }

            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1) {
                super.onReceivedError(webView, i, s, s1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//高版本可能会被调用，防止重复
                    return;
                }
                //6.0以下执行
                if (webIHelper.getWebViewLowOnReceivedError() != null) {
                    webIHelper.getWebViewLowOnReceivedError().onReceivedError(i, s, s1);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //UiUtil.toastLongTime("url=" + url);
                // 网页页面开始加载的时候
                mPageLoadFinished = false;
                if (webIHelper.getWebViewPageStarted() != null) {
                    webIHelper.getWebViewPageStarted().onPageStarted(url);
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mPageLoadFinished = true;
                //加载完成mClearHistoryUrl后清除历史记录,contains是因为load时可能对url有拼接操作
                if (!CheckUtil.isEmpty(mClearHistoryUrl) && url != null && url.contains(mClearHistoryUrl)) {
                    mClearHistoryUrl = null;
                    clearHistory();
                }
                if (webIHelper.getWebViewPageFinished() != null) {
                    webIHelper.getWebViewPageFinished().onPageFinished(url);
                }
                //view.loadUrl(WebDataImpl.Javascript);
            }

            @Override
            public void onLoadResource(WebView webView, String s) {
                //System.out.println("X5WebView -> onLoadResource->" + s);
                super.onLoadResource(webView, s);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (webIHelper.getWebResourceResponse() != null) {
                    WebResourceResponse resourceResponse =
                            webIHelper.getWebResourceResponse().shouldInterceptRequest(view, request);
                    if (resourceResponse != null) {
                        return resourceResponse;
                    }
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (webIHelper.getWebResourceResponse() != null) {
                    WebResourceResponse resourceResponse = webIHelper.getWebResourceResponse().shouldInterceptRequest(view, url);
                    if (resourceResponse != null) {
                        return resourceResponse;
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }
        });

        this.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(view.getContext());
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (webIHelper.getWebViewCreateWindow() != null) {
                            webIHelper.getWebViewCreateWindow().onCreateWindow(url);
                        }
                        return true;
                    }
                });

                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }

            @SuppressLint("WebViewApiAvailability")
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                NBSWebChromeClient.initJSMonitor(view, newProgress);
                super.onProgressChanged(view, newProgress);
                //System.out.println("X5WebView -> onProgressChanged->" + newProgress);
                if (showProgressBar) {
                    if (newProgress <= 2 && progress.getVisibility() == View.GONE) {
                        progress.setVisibility(VISIBLE);
                    } else if (newProgress >= 80 && progress.getVisibility() == View.VISIBLE) {
                        progress.setVisibility(View.GONE);
                    } else if (progress.getVisibility() == View.VISIBLE) {
                        progress.setProgress(newProgress);
                    }
                }
                //增强jsBridge，调用WebViewClient.onPageFinished防止由于js复杂而导致jsBridge注册失败
                if (newProgress >= 100) {
                    try {
                        Method getWebViewClient = WebView.class.getDeclaredMethod("getWebViewClient");
                        if (getWebViewClient != null) {
                            getWebViewClient.setAccessible(true);//低版本是私有方法，要加上此行代码
                            WebViewClient webViewClient = ((WebViewClient) getWebViewClient.invoke(CusWebView.this));
                            if (webViewClient != null) {
                                webViewClient.onPageFinished(CusWebView.this, CusWebView.this.getUrl());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView webView, String s) {
                super.onReceivedTitle(webView, s);
                if (webIHelper.getWebViewOnReceivedTitle() != null && s != null && !webView.getUrl().contains(s)) {
                    webIHelper.getWebViewOnReceivedTitle().onReceivedTitle(s);
                }
            }

            @Override
            public boolean onJsPrompt(WebView webView, String url, String message, String defaultValue, JsPromptResult jsPromptResult) {
                if (webIHelper.getWebViewonJsPrompt() != null) {
                    if (webIHelper.getWebViewonJsPrompt().onJsPrompt(url, message, defaultValue, jsPromptResult)) {
                        return true;
                    } else {
                        return super.onJsPrompt(webView, url, message, defaultValue, jsPromptResult);
                    }
                } else {
                    return super.onJsPrompt(webView, url, message, defaultValue, jsPromptResult);
                }
            }

            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                uploadFile = uploadMsg;
                onFileChoose();
            }

            // For Android 3.0+
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                fileType = acceptType;
                uploadFile = uploadMsg;
                onFileChoose();
            }

            // For Android  > 4.1.1
            //@Override
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                fileType = acceptType;
                uploadFile = uploadMsg;
                onFileChoose();
            }

            // For Android  >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                uploadFiles = filePathCallback;
                if (Build.VERSION.SDK_INT >= 21) {
                    if (fileChooserParams != null && fileChooserParams.getAcceptTypes() != null
                            && fileChooserParams.getAcceptTypes().length > 0) {
                        fileType = fileChooserParams.getAcceptTypes()[0];
                        isCaptureEnabled = fileChooserParams.isCaptureEnabled();
                    } else {
                        fileType = "*/*";
                    }
                }
                System.out.println("----webview 选择文件类型：" + fileType);
                onFileChoose();
                return true;
            }
        });
        initProgressBar();
    }

    /**
     * 重新初始化
     */
    public void reInit() {
        init();
    }

    public void reInit(String ua) {
        init(ua);
    }

    /**
     * 初始化进度条
     */
    private void initProgressBar() {
        progress = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiUtil.dip2px(getContext(), 2), 0);
        progress.setLayoutParams(layoutParams);
        progress.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.webview_progressbar));
        this.addView(progress);
    }

    public void setShowProgressBar(boolean showProgressBar) {
        this.showProgressBar = showProgressBar;
    }

    public void setClearHistoryUrl(String mClearHistoryUrl) {
        this.mClearHistoryUrl = mClearHistoryUrl;
    }

    private void onFileChoose() {
        if (webIHelper.getWebViewFileChoose() != null) {
            webIHelper.getWebViewFileChoose().onFileChoose();
        }
    }

    public String getFileType() {
        return fileType;
    }

    public boolean isCaptureEnabled() {
        return isCaptureEnabled;
    }

    /**
     * 安全性设置
     */
    private void safeSetting() {
        WebSettings webSetting = this.getSettings();
        //webSetting.setJavaScriptEnabled(true);//设置能够解析Javascript
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);// 是否支持通过js打开新窗口
        webSetting.setAllowFileAccess(true);// 是否可以访问文件
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        webSetting.setAllowFileAccessFromFileURLs(true);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        webSetting.setAllowUniversalAccessFromFileURLs(true);

        webSetting.setSupportZoom(true);// 是否支持缩放，默认为true
        webSetting.setBuiltInZoomControls(true);// 是否使用内置的缩放控件
        //webSetting.setSavePassword(false);// 是否保存密码
        // 设置自适应屏幕，两者合用
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        //支持多窗口，复写onCreateWindow
        webSetting.setSupportMultipleWindows(true);
        webSetting.setAppCacheEnabled(true);// 开启 Application Caches 功能
        webSetting.setDomStorageEnabled(true);// 是否启用 DOM storage API
        webSetting.setDatabaseEnabled(true); // 是否启用 database storage API 功能
        webSetting.setGeolocationEnabled(true);
        webSetting.setAllowContentAccess(true);//当设置为true，就可以 使用content://加载文件   eg:webView.loadUrl("content://com.xxx.test_provider/my_files_path/index.html");
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setUserAgentString(webSetting.getUserAgentString() + "gouhua_and");
        System.out.println("缓存路径：" + getWebViewCachePath());
        webSetting.setAppCachePath(getWebViewCachePath()); // 设置缓存目录
        webSetting.setGeolocationDatabasePath(this.getContext().getDir("geolocation", 0)
                .getPath());

        /*
         * 对系统API在19以上的版本作了兼容。
         * 因为4.4以上系统在onPageFinished时再恢复图片加载时,
         * 如果存在多张图片引用的是相同的src时，会只有一个image标签得到加载，因而对于这样的系统我们就先直接加载。
         */
        if (Build.VERSION.SDK_INT >= 19) {
            webSetting.setLoadsImagesAutomatically(true);// 是否支持自动加载图片
        } else {
            webSetting.setLoadsImagesAutomatically(false);
        }
        webSetting.setBlockNetworkImage(false);
        //webSetting.setDefaultTextEncodingName("utf-8");// 设置编码格式
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        /*
         *  Webview在安卓5.0之前默认允许其加载混合网络协议内容
         *  在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
         */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // 配置当安全源试图从不安全源加载资源时WebView的行为
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
//        LOAD_CACHE_ONLY： 不使用网络，只读取本地缓存数据，
//        LOAD_DEFAULT：根据cache-control决定是否从网络上取数据，
//        LOAD_CACHE_NORMAL：API level 17中已经废弃, 从API level 11开始作用同- - LOAD_DEFAULT模式，
//        LOAD_NO_CACHE: 不使用缓存，只从网络获取数据，
//        LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        //CookieManager.getInstance().flush();
        CookieSyncManager.createInstance(this.getContext());
        CookieSyncManager.getInstance().sync();
        this.setDownloadListener(new ApkDownloadListener(this.getContext()));
        setClickable(true);
    }

    private String getWebViewCachePath() {
        String cachePath = getContext().getFilesDir().getAbsolutePath() + "/webCache";
        File cacheDir = new File(cachePath);
        // 缓存目录
        if (!cacheDir.exists() && !cacheDir.isDirectory()) {
            boolean isMkdirs = cacheDir.mkdirs();
            System.out.println("设置缓存目录：" + isMkdirs);
        }
        return cachePath;
    }

    /**
     * 获取特殊字符串头，是一种向访问网站提供你所使用的浏览器类型及版本、操作系统及版本、浏览器内核、等信息的标识
     */
    public void setUserAgent(String type) {
        WebSettings webSetting = this.getSettings();
        webSetting.setUserAgentString(webSetting.getUserAgentString() + type);
    }

    public boolean goWebBack() {
        if (canGoBack()) {
            goBack();
            return true;
        }
        return false;
    }

    /**
     * 跟随activity或fragment的回调，处理相应的逻辑
     *
     * @param requestCode 你懂的
     * @param resultCode  你懂的
     * @param data        你懂的
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotographUtils.THECAMERA ||
                requestCode == PhotographUtils.PHOTOALBUM ||
                requestCode == PhotographUtils.THEEDITOR) {
            Uri resultUri = null;
            if (requestCode == PhotographUtils.THECAMERA) {//拍照的逻辑
                resultUri = PhotographUtils.getCameraUri(getContext(), PhotographUtils.IMAGE_FILE_NAME);
            }

            if (null != uploadFile) {
                if (resultUri == null) {
                    resultUri = data == null || resultCode != RESULT_OK ? null : data.getData();
                }
                uploadFile.onReceiveValue(resultUri);
                uploadFile = null;
            }
            if (null != uploadFiles) {
                if (resultUri == null) {
                    resultUri = data == null || resultCode != RESULT_OK ? null : data.getData();
                }
                uploadFiles.onReceiveValue(resultUri != null ? new Uri[]{resultUri} : null);
                uploadFiles = null;
            }
        }
    }

    private String mClearHistoryUrl;//此mClearHistoryUrl加载完成后需要清除历史记录

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && onBackPressed()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onBackPressed() {
        HashMap<String, String> urlParams = WebHelper.getUrlParams(getUrl());
        String targetUrl;
        if (urlParams.containsKey("targetUrl") && !CheckUtil.isEmpty(targetUrl = urlParams.get("targetUrl"))) {
            //针对特殊url处理手机物理返回键，防止物理返回时重定向
            loadUrl(mClearHistoryUrl = Uri.decode(targetUrl));//需要对targetUrl进行解码处理
            return true;
        } else if (urlParams.containsKey("closeWebview") && "true".equals(urlParams.get("closeWebview"))) {
            //针对特殊url处理手机物理返回键，防止物理返回时重定向
            if (ActivityUntil.findActivity(MainActivity.class) == null && ActivityUntil.getActivitySize() <= 1) {
                getContext().startActivity(new Intent(getContext(), MainActivity.class));
            }
            Context context = getContext();
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
            return true;
        } else if (this.canGoBack()) {
            this.goBack();
            return true;
        }
        return false;
    }

    public WebInterfaceHelper getWebIHelper() {
        return webIHelper;
    }

    /**
     * 滑动监听
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (webIHelper.getScrollChangeListener() != null) {
            webIHelper.getScrollChangeListener().onScrollChanged(l, t, oldl, oldt);
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (webIHelper.getOverScrollChangeListener() != null) {
            webIHelper.getOverScrollChangeListener().onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        }
        //当滑动到最上边界或者最下边界时clampedY为true
        if (mPageLoadFinished && scrollY != 0 && clampedY) {
            if (webIHelper.getScrollPageEndListener() != null) {
                webIHelper.getScrollPageEndListener().onScrollPageEnd();
            }
        }
    }
    //    class ApkDownloadListener implements DownloadListener {
//
//        @Override
//        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//            ActivityUntil.DownloadByBrowser(getContext(), url);
//        }
//    }

    public Map<String, BridgeHandler> myHandlers = new HashMap<>();

    public void registerHandler(String handlerName, BridgeHandler handler) {
        myHandlers.put(handlerName, handler);
        super.registerHandler(handlerName, handler);
    }

    @Override
    public void destroy() {
        webIHelper.destroy();
        uploadFile = null;
        uploadFiles = null;
        try {
            ViewParent parent = progress.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(this);
            }

            ViewParent webParent = getParent();
            if (webParent != null) {
                ((ViewGroup) webParent).removeView(this);
            }
            stopLoading();
            removeAllViews();
            clearCache(true);
            clearHistory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.destroy();
    }

    //4.0.0新增，如果是打开海尔链接，动态新增channelNo=42以及token,为兼容h5，新增appVersion
    @Override
    public void loadUrl(String url) {
        //因为jsBridge的拦截处理url，所以需先判断是否非jsBridge的链接，属于真正的网页链接才需要拼参数，jsBridge的特殊url不要拼，不然会加载出问题
        //部分链接携带参数noNeedParamForApp，当url包含该参数时，直接加载
        if ((url.contains("haiercash.com") || url.contains("goudzi")) && !url.contains("noNeedParamForApp")) {
            //参数去重，取出参数，然后纯净url重新拼接参数
            HashMap<String, String> map = WebHelper.getUrlParams(url);
            if (CheckUtil.isEmpty(appendUA)) {
                String channelNo = SpHp.getLogin(SpKey.CHANNEL_NO);
                map.put("channelNo", !CheckUtil.isEmpty(channelNo) ? channelNo : "42");
            }
            if (!CheckUtil.isEmpty(NetConfig.TD_APP_DOWN_FROM_SMY)) {
                map.put("appDownFrom", NetConfig.TD_APP_DOWN_FROM_SMY);
            }
            map.put("token", TokenHelper.getInstance().getCacheToken());
            map.put("h5token", TokenHelper.getInstance().getH5Token());
            map.put("custNo", SpHp.getUser(SpKey.USER_CUSTNO));
            map.put("appVersion", BuildConfig.VERSION_NAME);
            map.put("business", !CheckUtil.isEmpty(TokenHelper.getInstance().getCacheToken()) ? TokenHelper.getInstance().getSmyParameter("business") : NetConfig.TD_BUSINESS_SMY);
            map.put("appStatusBarHeight", String.valueOf(UiUtil.px2dip(getContext(), UiUtil.getStatusBarHeight(getContext()))));
            url = WebHelper.addUrlParam(url.contains("?") ? url.substring(0, url.indexOf("?")) : url, map);
        }
        super.loadUrl(url);
        if (WebDataImpl.IS_DEBUG_WEB) {
            Logger.e("-----开始加载H5链接" + url);
        }
    }

    //获取最大纵向滚动值
    public int getMaxScrollY() {
        return computeVerticalScrollRange() - getHeight();
    }

    //滚动到最底部,computeVerticalScrollRange()纵向最大滚动距离
    public void scrollToBottom() {
        scrollTo(0, getMaxScrollY());
    }
}
