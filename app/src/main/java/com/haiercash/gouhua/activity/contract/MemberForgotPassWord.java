package com.haiercash.gouhua.activity.contract;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.x5webview.CusWebView;

import butterknife.BindView;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/10/11<br/>
 * 描    述：会员找回密码<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Deprecated
public class MemberForgotPassWord extends BaseFragment {

    @BindView(R.id.webview)
    CusWebView x5WebView;
    @BindView(R.id.view_background)
    View view;

    private String alterPwdIn;
    private String alterPwdOut;

    public static final int ID = MemberForgotPassWord.class.hashCode();

    private static final String TAG_alterPwdIn = "alterPwdIn";
    private static final String TAG_alterPwdOut = "alterPwdOut";

    public static MemberForgotPassWord newInstance(Bundle bd) {
        final MemberForgotPassWord f = new MemberForgotPassWord();
        if (bd != null) {
            f.setArguments(bd);
        }
        return f;
    }

    public static void forgotNumber(BaseActivity baseActivity, String alterPwdIn, String alterPwdOut) {
        Bundle bundle = new Bundle();
        bundle.putString(TAG_alterPwdIn, alterPwdIn);
        bundle.putString(TAG_alterPwdOut, alterPwdOut);
        ContainerActivity.to(baseActivity, ID, bundle);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_member_forgotpassword;
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle("找回密码");
        Bundle bundle = getArguments();
        if (bundle != null) {
            alterPwdIn = bundle.getString(TAG_alterPwdIn);
            alterPwdOut = bundle.getString(TAG_alterPwdOut);
        }
        if (CheckUtil.isEmpty(alterPwdIn) || CheckUtil.isEmpty(alterPwdOut)) {
            UiUtil.toast("未找到页面资源");
            return;
        }
        x5WebView.loadUrl(alterPwdIn);
        x5WebView.setOnLongClickListener(view -> true);
        //禁止y方向滑动
        x5WebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        x5WebView.setScrollContainer(false);
        x5WebView.setVerticalScrollBarEnabled(false);
        x5WebView.setHorizontalScrollBarEnabled(false);
        x5WebView.getWebIHelper().setWebViewPageStarted(url -> {
            Log.e("打开WebView", url);
            if (alterPwdOut.equals(url)) {
                ActivityUntil.finishOthersActivity(MainActivity.class);
                LoginSelectHelper.staticToGeneralLogin();
            }
        });
        view.setOnClickListener(v -> System.out.println("---> 空白处点击事件"));
        // 设置遮罩层的高度
        int height = SystemUtils.getDeviceHeight(mActivity) - UiUtil.dip2px(mActivity, 350);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.height = height;
        view.setLayoutParams(params);
    }

    @Override
    public void onDestroy() {
        if (x5WebView != null) {
            x5WebView.destroy();
            x5WebView = null;
        }
        super.onDestroy();
    }
}
