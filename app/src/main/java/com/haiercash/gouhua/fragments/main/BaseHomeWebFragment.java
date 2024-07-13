package com.haiercash.gouhua.fragments.main;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.log.Logger;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.adaptor.ListRefreshHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.databinding.FragmentDiscoveryBinding;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.x5webview.WebDataImpl;

/**
 * Author: Sun
 * Date :    2018/12/17
 * FileName: BaseHomeWebFragment
 * Description:
 */
public class BaseHomeWebFragment extends BaseFragment {
    protected FragmentDiscoveryBinding getBinding() {
        return (FragmentDiscoveryBinding) _binding;
    }

    String mCurrentUrl;
    private WebDataImpl webImpl;
    /*
     * 当前四种样式
     * 1，无导航title处于webview的上方
     * 2，无导航title悬浮于webview的上方
     * 3，有导航title处于webview的上方
     * 4，有导航title悬浮于webview的上方
     * 若 getArguments().getString(TAG_URL)为空则 无导航；
     */
    /**
     * 无导航title处于webview的上方
     */
    protected final int VIEW_VERTICAL = 0x01;

    /**
     * 无导航title悬浮于webview的上方
     */
    protected final int VIEW_ABOVE = 0x02;

    int viewPageType;

//    public BaseHomeWebFragment() {
//        super(R.layout.fragment_discovery);
//    }

    @Override
    protected FragmentDiscoveryBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentDiscoveryBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initEventAndData() {
        setonClickByViewId(R.id.headLeft, R.id.headClose);
        /* 初始化当前页面  */
        ListRefreshHelper.initSwipeRefresh(mActivity, getBinding().swipeRefreshLayout);
        int start = UiUtil.dip2px(mActivity, 75);
        getBinding().llTitle.setPadding(0, UiUtil.getStatusBarHeight(getContext()) + UiUtil.dip2px(mActivity, 10), 0, UiUtil.dip2px(mActivity, 10));
        getBinding().swipeRefreshLayout.setProgressViewOffset(true, 0, start);
        getBinding().swipeRefreshLayout.setOnRefreshListener(() -> {
            getBinding().swipeRefreshLayout.setRefreshing(false);
            getBinding().wvcView.reload();
        });
        if (webImpl == null) {
            webImpl = WebDataImpl.getInstance(mActivity, getBinding().wvcView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("BaseHomeWebFragment:webImpl --" + (webImpl == null) + "--" + getBinding().wvcView.myHandlers.size());
        if (getBinding().wvcView.myHandlers.get(WebDataImpl.METHOD_USERINFO) == null) {
            UiUtil.toastDeBug("jsBridge网页回调已经不存在了需要重新注册");
            Logger.e("BaseHomeWebFragment:webImpl -- jsBridge回调已经不存在了需要重新注册");
            webImpl = WebDataImpl.getInstance(mActivity, getBinding().wvcView);
        }
    }

    /**
     * 设置样式
     */
    void setStyle(String url) {
        if ((!url.startsWith(ApiUrl.API_SERVER_URL) || viewPageType == VIEW_VERTICAL) ||
                (url.startsWith(ApiUrl.API_SERVER_URL) && url.contains("coupon"))) {
            setStyleVertical();
            getBinding().headLeft.setImageResource(R.drawable.iv_back_arrow_blues);
            getBinding().headClose.setImageResource(R.drawable.ic_web_close);
            getBinding().tvTitle.setTextColor(ContextCompat.getColor(mActivity, R.color.text_black));
            mActivity.setStatusBarTextColor(true);
        } else {
            setStyleSuspend();
            getBinding().headLeft.setImageResource(R.drawable.iv_back_white);
            getBinding().headClose.setImageResource(R.drawable.iv_close_white);
            getBinding().tvTitle.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
            mActivity.setStatusBarTextColor(false);
        }
    }

    /**
     *
     */
    protected void loadUrl() {
        if (!TextUtils.isEmpty(mCurrentUrl)) {
            //if (isNeedAddMoreParameter) {
            //    mCurrentUrl = WebUntil.addUseIdUrlAndReload(getBinding().wvcView, mCurrentUrl);
            //} else {
            getBinding().wvcView.loadUrl(mCurrentUrl);
//            getBinding().wvcView.loadUrl(mCurrentUrl, data -> {
//                System.out.println("loadUrl - callback:" + data);
//            });
            //}
        }
    }


    /**
     * 设置样式
     */
    private void setStyleVertical() {
        getBinding().llTitle.post(new Runnable() {
            @Override
            public void run() {
                int height = getBinding().llTitle.getHeight();
                getBinding().llWebContent.setPadding(0, height, 0, 0);

            }
        });
        //getBinding().llWebContent.setPadding(0, getBinding().llTitle.getHeight(), 0, 0);
//        getBinding().wvcView.loadUrl("javascript:document.body.style.paddingTop=\"" + getBinding().llTitle.getHeight()/4 + "px\"; void 0");
        //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getBinding().wvcView.getLayoutParams();
        //params.topMargin = getBinding().llTitle.getHeight();
        //params.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
        //params.addRule(RelativeLayout.BELOW, R.id.view_line);

        getBinding().llTitle.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.white));
    }

    /**
     * 设置样式为悬浮
     */
    private void setStyleSuspend() {
        getBinding().llWebContent.setPadding(0, 0, 0, 0);
//        getBinding().wvcView.loadUrl("javascript:document.body.style.paddingTop=\"0px\"; void 0");
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getBinding().wvcView.getLayoutParams();
//        params.topMargin = 0;
//        params.removeRule(RelativeLayout.BELOW);
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        getBinding().llTitle.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.transparent));
    }

    /**
     * 跳转登陆页面
     */
    void gotoLoginIn() {
        CommomUtils.clearSp();
        TokenHelper.initTokenRefresh();
        LoginSelectHelper.staticToGeneralLogin();
    }

    @Override
    public void onDestroyView() {
        getBinding().wvcView.destroy();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (webImpl != null) {
            webImpl.onDestroy();
            webImpl = null;
        }
        super.onDestroy();
    }

    /**
     * 返回
     */
    private boolean goBack() {
        return getBinding().wvcView.goWebBack();
    }


    protected void setTitle(String title) {
        getBinding().tvTitle.setText(title);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.headLeft) {
            mActivity.onBackPressed();
        } else if (view.getId() == R.id.headClose) {
            mActivity.finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (webImpl != null) {
            webImpl.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onBackPressed() {
        return goBack();
    }
}
