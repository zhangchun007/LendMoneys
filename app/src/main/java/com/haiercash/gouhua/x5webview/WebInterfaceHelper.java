package com.haiercash.gouhua.x5webview;

import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/5/21<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class WebInterfaceHelper {
    private WebViewPageStarted webViewPageStarted;
    private WebViewPageFinished webViewPageFinished;
    private WebViewShouldOverride webViewShouldOverride;
    private WebViewShouldOverride2 webViewShouldOverride2;
    //6.0以上系统404或500错误码拦截监听
    private WebViewOnReceivedHttpError webViewOnReceivedHttpError;
    private WebViewOnReceivedTitle webViewOnReceivedTitle;
    //6.0以上断网或者网络连接超时监听
    private WebViewOnReceivedError webViewOnReceivedError;
    //6.0以下断网或者网络连接超时监听
    private WebViewLowOnReceivedError webViewLowOnReceivedError;
    private OnScrollChangeListener mScrollChangeListener;
    private onOverScrollChangeListener mOverScrollChangeListener;
    //加载完成后滚动到最底部时监听
    private onScrollPageEndListener mScrollPageEndListener;
    private WebViewFileChoose mWebViewFileChoose;
    private WebViewonJsPrompt webViewonJsPrompt;
    private WebViewCreateWindow mWebViewCreateWindow;
    private WebViewResourceResponse mWebResourceResponse;


    public WebViewPageStarted getWebViewPageStarted() {
        return webViewPageStarted;
    }

    public void setWebViewPageStarted(WebViewPageStarted webViewPageStarted) {
        this.webViewPageStarted = webViewPageStarted;
    }

    public WebViewCreateWindow getWebViewCreateWindow() {
        return mWebViewCreateWindow;
    }

    public void setWebViewCreateWindow(WebViewCreateWindow mWebViewCreateWindow) {
        this.mWebViewCreateWindow = mWebViewCreateWindow;
    }

    public WebViewPageFinished getWebViewPageFinished() {
        return webViewPageFinished;
    }

    public void setWebViewPageFinished(WebViewPageFinished webViewPageFinished) {
        this.webViewPageFinished = webViewPageFinished;
    }

    public WebViewShouldOverride getWebViewShouldOverride() {
        return webViewShouldOverride;
    }

    public void setWebViewShouldOverride(WebViewShouldOverride webViewShouldOverride) {
        this.webViewShouldOverride = webViewShouldOverride;
    }

    public WebViewShouldOverride2 getWebViewShouldOverride2() {
        return webViewShouldOverride2;
    }

    public void setWebViewShouldOverride2(WebViewShouldOverride2 webViewShouldOverride2) {
        this.webViewShouldOverride2 = webViewShouldOverride2;
    }

    public WebViewOnReceivedHttpError getWebViewOnReceivedHttpError() {
        return webViewOnReceivedHttpError;
    }

    public void setWebViewOnReceivedHttpError(WebViewOnReceivedHttpError webViewOnReceivedHttpError) {
        this.webViewOnReceivedHttpError = webViewOnReceivedHttpError;
    }

    public WebViewOnReceivedTitle getWebViewOnReceivedTitle() {
        return webViewOnReceivedTitle;
    }

    public void setWebViewOnReceivedTitle(WebViewOnReceivedTitle webViewOnReceivedTitle) {
        this.webViewOnReceivedTitle = webViewOnReceivedTitle;
    }

    public WebViewOnReceivedError getWebViewOnReceivedError() {
        return webViewOnReceivedError;
    }

    public void setWebViewOnReceivedError(WebViewOnReceivedError webViewOnReceivedError) {
        this.webViewOnReceivedError = webViewOnReceivedError;
    }

    public WebViewLowOnReceivedError getWebViewLowOnReceivedError() {
        return webViewLowOnReceivedError;
    }

    public void setWebViewLowOnReceivedError(WebViewLowOnReceivedError webViewLowOnReceivedError) {
        this.webViewLowOnReceivedError = webViewLowOnReceivedError;
    }

    public OnScrollChangeListener getScrollChangeListener() {
        return mScrollChangeListener;
    }

    public void setScrollChangeListener(OnScrollChangeListener mScrollChangeListener) {
        this.mScrollChangeListener = mScrollChangeListener;
    }

    public onOverScrollChangeListener getOverScrollChangeListener() {
        return mOverScrollChangeListener;
    }

    public void setOverScrollChangeListener(onOverScrollChangeListener mScrollChangeListener) {
        this.mOverScrollChangeListener = mScrollChangeListener;
    }

    public onScrollPageEndListener getScrollPageEndListener() {
        return mScrollPageEndListener;
    }

    public void setScrollPageEndListener(onScrollPageEndListener mScrollPageEndListener) {
        this.mScrollPageEndListener = mScrollPageEndListener;
    }

    public WebViewFileChoose getWebViewFileChoose() {
        return mWebViewFileChoose;
    }

    public void setWebViewFileChoose(WebViewFileChoose mWebViewFileChoose) {
        this.mWebViewFileChoose = mWebViewFileChoose;
    }

    public WebViewonJsPrompt getWebViewonJsPrompt() {
        return webViewonJsPrompt;
    }

    public void setWebViewonJsPrompt(WebViewonJsPrompt webViewonJsPrompt) {
        this.webViewonJsPrompt = webViewonJsPrompt;
    }

    public WebViewResourceResponse getWebResourceResponse() {
        return mWebResourceResponse;
    }

    public void setWebResourceResponse(WebViewResourceResponse mWebResourceResponse) {
        this.mWebResourceResponse = mWebResourceResponse;
    }

    public interface WebViewJavaScriptFunction {
        @JavascriptInterface
        void onJsFunctionCalled(String data);
    }


    public interface WebViewFileChoose {
        void onFileChoose();
    }

    public interface OnScrollChangeListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    public interface onOverScrollChangeListener {
        //当滑动到最上边界或者最下边界时clampedY为true
        void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY);
    }

    public interface onScrollPageEndListener {
        /**
         * 当滑动到最下边界时（网页已加载完成后才会监听）
         */
        void onScrollPageEnd();
    }

    public interface WebViewPageStarted {

        /**
         * 页面开始加载
         */
        void onPageStarted(String url);
    }

    public interface WebViewShouldOverride {
        /**
         * 页面开始load
         *
         * @return true 说明也被消费，不会调用super
         */
        boolean shouldOverrideUrlLoading(String url);
    }

    public interface WebViewShouldOverride2 {
        boolean shouldOverrideUrlLoading(Object request);
    }

    public interface WebViewOnReceivedHttpError {
        /**
         * webView 加载中Http报错
         */
        void onReceivedHttpError(Object webResourceRequest, Object webResourceResponse);
    }

    public interface WebViewOnReceivedError {
        /**
         * webView加载失败
         */
        void onReceivedError(Object webResourceRequest, Object webResourceError);
    }

    public interface WebViewLowOnReceivedError {
        /**
         * webView加载失败
         */
        void onReceivedError(int errorCode, String description, String failingUrl);
    }

    public interface WebViewOnReceivedTitle {
        void onReceivedTitle(String title);
    }

    public interface WebViewonJsPrompt {
        boolean onJsPrompt(String url, String message, String defaultValue, Object jsPromptResult);
    }

    public interface WebViewPageFinished {
        void onPageFinished(String url);
    }


    public interface WebViewCreateWindow {

        /**
         * 打开新窗口
         *
         * @param url
         */
        void onCreateWindow(String url);
    }

    public interface WebViewResourceResponse {

        WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request);

        WebResourceResponse shouldInterceptRequest(WebView view, String url);
    }


    public void destroy() {
        webViewPageStarted = null;
        webViewPageFinished = null;
        webViewShouldOverride = null;
        webViewOnReceivedHttpError = null;
        webViewOnReceivedTitle = null;
        webViewOnReceivedError = null;
        webViewLowOnReceivedError = null;
        mScrollChangeListener = null;
        mOverScrollChangeListener = null;
        mScrollPageEndListener = null;
        mWebViewFileChoose = null;
        mWebViewCreateWindow = null;
    }
}
