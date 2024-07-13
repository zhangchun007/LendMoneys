package com.haiercash.gouhua.activity.contract;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.SoftHideKeyBoardUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.x5webview.CusWebView;
import com.haiercash.gouhua.x5webview.WebDataImpl;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: Sun<br/>
 * Date :    2017/12/5<br/>
 * FileName: WebSimpleFragment<br/>
 * Description: 用于WebView的简单显示 如协议展示<br/>
 */
@Route(path = PagePath.FRAGMENT_WEB_SIMPLE)
public class WebSimpleFragment extends BaseFragment {
    public static final int REQUEST_CODE = 0x14;

    @BindView(R.id.llyt_warning)
    LinearLayout mLlytWarning;
    @BindView(R.id.tv_agree)
    TextView mTvAgree;
    @BindView(R.id.tv_know)
    TextView mTvKnow;
    @BindView(R.id.wv_contract_treaty)
    protected CusWebView x5WebView;
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
    public static final int ID = WebSimpleFragment.class.hashCode();
    protected String mUrl;
    protected String mTitle;
    protected String mStyle = STYLE_NORMAL;
    private boolean IMAGEVISIBLITY = false, isShowWebViewTitle = false;
    private WebDataImpl webImpl;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contract_treaty;
    }

    public static void WebService(Activity activity, String url, String title, String style) {
        Bundle bundle = new Bundle();
        bundle.putString(WEB_URL, url);
        bundle.putString(WEB_TITLE, title);
        bundle.putString(STYLE, style);
        bundle.putBoolean(LEFTIMAGECLOSE, false);
        ContainerActivity.toForResult(activity, ID, bundle, REQUEST_CODE);
    }

    public static void WebService(Activity activity, String url, String title) {
        WebService(activity, url, title, STYLE_NORMAL);
    }

    public static void WebService(BaseFragment baseFragment, String url, String title, String style) {
        Bundle bundle = new Bundle();
        bundle.putString(WEB_URL, url);
        bundle.putString(WEB_TITLE, title);
        bundle.putString(STYLE, style);
        bundle.putBoolean(LEFTIMAGECLOSE, false);
        //ContainerActivity.toForResult(baseFragment, ID, bundle, REQUEST_CODE);
        WebService(baseFragment, bundle);
    }

    public static void WebService(BaseFragment baseFragment, String url, String title) {
        WebService(baseFragment, url, title, STYLE_NORMAL);
    }

    public static void WebService(BaseFragment baseFragment, Bundle bundle) {
        ContainerActivity.toForResult(baseFragment, ID, bundle, REQUEST_CODE);
    }

    public static WebSimpleFragment newInstance(Bundle bd) {
        final WebSimpleFragment f = new WebSimpleFragment();
        if (bd != null) {
            f.setArguments(bd);
        }
        return f;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (webImpl != null) {
            webImpl.onActivityResult(requestCode, resultCode, data);
        }
        if (x5WebView != null) {
            x5WebView.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initEventAndData() {
        getIntentData();
        showWebView();
        //new WebDataImpl(mActivity, x5WebView).initWebMethod();
        webImpl = WebDataImpl.getInstance(mActivity, x5WebView);
    }


    /*获取前一页面传过来的信息*/
    private void getIntentData() {
        if (null != getArguments()) {
            Bundle bundle = getArguments();
            mUrl = bundle.getString(WEB_URL);
            mTitle = bundle.getString(WEB_TITLE, "");//海尔消费金融
            mStyle = bundle.getString(STYLE);
            IMAGEVISIBLITY = bundle.getBoolean(LEFTIMAGECLOSE);
            isShowWebViewTitle = bundle.getBoolean("isShowWebViewTitle");
        }
    }

    private void showWebView() {
        //标题
        mActivity.setTitle(mTitle);
        mActivity.setLeftImageCloseVisibility(IMAGEVISIBLITY);
        //内容
        if (x5WebView != null && !TextUtils.isEmpty(mUrl)) {
            x5WebView.loadUrl(mUrl);
            x5WebView.getWebIHelper().setWebViewFileChoose(() -> ActivityUntil.openFileChooseProcess(mActivity));
            x5WebView.getWebIHelper().setWebViewPageFinished((url) -> {
                if (isShowWebViewTitle) {
                    mActivity.setTitle(x5WebView.getTitle());
                }
            });
            x5WebView.getWebIHelper().setWebViewonJsPrompt((url, message, defaultValue, jsPromptResult) -> false);

            x5WebView.getWebIHelper().setWebViewCreateWindow((url -> {
                //打开新的页面
                WebSimpleFragment.WebService(mActivity, url, mTitle, STYLE_OTHERS);
            }));
        }

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
                    lp2.setMargins(UiUtil.dip2px(mActivity, 15), 0, UiUtil.dip2px(mActivity, 15), UiUtil.dip2px(mActivity, 20));
                }
                break;
            default:
                mTvAgree.setVisibility(View.GONE);
                mTvKnow.setVisibility(View.GONE);
                mLlytWarning.setVisibility(View.GONE);
                break;
        }
        SoftHideKeyBoardUtil.assistActivity(mActivity);
    }


    @Override
    public void onDestroyView() {
        if (webImpl != null) {
            webImpl.onDestroy();
            webImpl = null;
        }
        if (x5WebView != null) {
            x5WebView.destroy();
            x5WebView = null;
        }
        super.onDestroyView();
    }

    @OnClick({R.id.tv_agree, R.id.tv_know})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_agree:
            case R.id.tv_know:
                mActivity.setResult(Activity.RESULT_OK);
                mActivity.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (x5WebView != null && x5WebView.canGoBack()) {
            return x5WebView.goWebBack();
        }
        return super.onBackPressed();
    }
}
