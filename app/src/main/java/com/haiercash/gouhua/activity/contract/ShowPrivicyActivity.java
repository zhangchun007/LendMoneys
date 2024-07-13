package com.haiercash.gouhua.activity.contract;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.utils.UiUtil;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 8/29/22
 * @Version: 1.0
 */
public class ShowPrivicyActivity extends BaseActivity {
    private static final int MAX_CACHE_SIZE = 1024 * 1024 * 4;
    @BindView(R.id.llyt_warning)
    LinearLayout mLlytWarning;
    @BindView(R.id.tv_agree)
    TextView mTvAgree;
    @BindView(R.id.tv_know)
    TextView mTvKnow;
    @BindView(R.id.wv_contract_treaty)
    protected WebView x5WebView;
    @BindView(R.id.llyt_button)
    LinearLayout llyt_button;

    protected static final String WEB_TITLE = "title";
    protected static final String WEB_URL = "url";
    protected static final String STYLE = "style";
    protected static final String LEFTIMAGECLOSE = "leftCloseShow";
    public static final String STYLE_NORMAL = "normal";
    public static final String STYLE_COMPLEX = "complex";
    public static final String STYLE_COMPLEX2 = "complex2";
    public static final String STYLE_COMPLEX3 = "complex3";
    public static final String STYLE_OTHERS = "others";
    protected String mUrl;
    protected String mTitle;
    protected String mStyle = STYLE_NORMAL;
    private boolean IMAGEVISIBLITY = false, isShowWebViewTitle = false;

    @Override
    protected int getLayout() {
        return R.layout.activity_show_privicy;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getStringExtra(WEB_URL);
            mTitle = intent.getStringExtra(WEB_TITLE);//海尔消费金融
            IMAGEVISIBLITY = intent.getBooleanExtra(LEFTIMAGECLOSE, false);
            isShowWebViewTitle = intent.getBooleanExtra("isShowWebViewTitle", false);
        }

        //标题
        setTitle(CheckUtil.formatTitle(mTitle));
        setLeftImageCloseVisibility(IMAGEVISIBLITY);

        //设置webview
        setWebViewSetting(x5WebView);
        x5WebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (isShowWebViewTitle) {
                    setTitle(x5WebView.getTitle());
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        x5WebView.loadUrl(mUrl);


        //样式  普通样式
        switch (mStyle) {
            case STYLE_NORMAL:
                mTvAgree.setVisibility(View.GONE);
                mTvKnow.setVisibility(View.VISIBLE);
                mLlytWarning.setVisibility(View.GONE);
                break;
            case STYLE_COMPLEX:
                // 特殊样式
                mTvAgree.setVisibility(View.VISIBLE);
                mTvKnow.setVisibility(View.GONE);
                mLlytWarning.setVisibility(View.VISIBLE);
                break;
            case STYLE_COMPLEX2:
                //删除webViewClient的layout_above属性
                if (x5WebView != null) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) x5WebView.getLayoutParams();
                    lp.addRule(RelativeLayout.ABOVE, 0);
                    llyt_button.setBackgroundResource(R.color.transparent);
                    mLlytWarning.setVisibility(View.GONE);
                    mTvAgree.setText("我知道了");
                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) mTvAgree.getLayoutParams();
                    lp2.setMargins(7, 0, 7, 7);
                }
                break;
            case STYLE_COMPLEX3:
                //删除webViewClient的layout_above属性
                if (x5WebView != null) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) x5WebView.getLayoutParams();
                    lp.addRule(RelativeLayout.ABOVE, 0);
                    llyt_button.setBackgroundResource(R.color.transparent);
                    mLlytWarning.setVisibility(View.GONE);
                    mTvAgree.setText("同意并继续");
                    LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) mTvAgree.getLayoutParams();
                    lp2.setMargins(UiUtil.dip2px(this, 15), 0, UiUtil.dip2px(this, 15), UiUtil.dip2px(this, 20));
                }
                break;
            default:
                mTvAgree.setVisibility(View.GONE);
                mTvKnow.setVisibility(View.GONE);
                mLlytWarning.setVisibility(View.GONE);
                break;
        }

    }

    public static void setWebViewSetting(WebView webView) {
        WebSettings webSetting = webView.getSettings();
        //支持js交互
        webSetting.setJavaScriptEnabled(true);
        //支持内置的缩放控件。 这个取决于setSupportZoom(), 若setSupportZoom(false)，则该WebView不可缩放。
        webSetting.setBuiltInZoomControls(true);
        //支持缩放
        webSetting.setSupportZoom(true);
        //支持通过JS打开新窗口缩放处理
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持文件访问
        webSetting.setAllowFileAccess(true);
        //支持内容重新布局
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        //设置将图片调整到适合webview的大小
        webSetting.setUseWideViewPort(true);
        //支持多屏窗口
        webSetting.setSupportMultipleWindows(true);
        //允许webView混合网络协议访问（支持https里加载http图片）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //开启DOM储存
        webSetting.setDomStorageEnabled(true);
        //设置H5的缓存是否打开
        webSetting.setAppCacheEnabled(true);
        //开启 database storage API 功能
        webSetting.setDatabaseEnabled(true);
        //设置缓存最大容量
        webSetting.setAppCacheMaxSize(MAX_CACHE_SIZE);
        //设置缓存的目录
//        webSetting.setAppCachePath(appCachePath(webView.getContext()));
        //设置定位是否可用
        webSetting.setGeolocationEnabled(true);
        //设置是否支持flash插件
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        //提高渲染的优先级
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //设置WebView是否支持多屏窗口
        webSetting.setSupportMultipleWindows(false);
    }

    public static void skip(Context context, String url, String title) {
        Intent intent = new Intent(context, ShowPrivicyActivity.class);
        intent.putExtra(WEB_URL, url);
        intent.putExtra(WEB_TITLE, title);
        intent.putExtra(LEFTIMAGECLOSE, false);
        context.startActivity(intent);
    }


    @Override
    public void onDestroy() {
        if (x5WebView != null) {
            x5WebView.destroy();
            x5WebView = null;
        }
        super.onDestroy();
    }

    @OnClick({R.id.tv_agree, R.id.tv_know})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_agree:
            case R.id.tv_know:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (x5WebView != null && x5WebView.canGoBack()) {
            x5WebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


}
