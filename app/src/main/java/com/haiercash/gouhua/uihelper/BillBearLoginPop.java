package com.haiercash.gouhua.uihelper;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/6/28<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BillBearLoginPop extends BasePopupWindow implements INetResult {
    @BindView(R.id.cb_agree)
    CheckBox cbAgree;
    @BindView(R.id.tv_protocol)
    TextView tvProtocol;
    private NetHelper netHelper;
    private View checkView;

    public BillBearLoginPop(BaseActivity context, Object data) {
        super(context, data);
        netHelper = new NetHelper(this);
    }
    @Override
    protected int getLayout() {
        return R.layout.pop_bill_bear_login;
    }

    @Override
    protected void onViewCreated(Object data) {
        SpannableStringBuilder builder = SpannableStringUtils.getBuilder(mActivity, "我已阅读并同意")
                .setForegroundColor(Color.parseColor("#666666"))
                .append(" 《联合登录授权书》、")
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        WebSimpleFragment.WebService(mActivity, ApiUrl.URL_LINK_UNION_LOGIN, "联合登录授权书", WebSimpleFragment.STYLE_OTHERS);
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(UiUtil.getColor(R.color.colorPrimary));
                        ds.setUnderlineText(false);
                        ds.clearShadowLayer();
                    }
                }).append(" 《烈熊服务协议》")
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        WebSimpleFragment.WebService(mActivity, ApiUrl.URL_LINK_BILL_BEAR, "烈熊服务协议", WebSimpleFragment.STYLE_OTHERS);
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(UiUtil.getColor(R.color.colorPrimary));
                        ds.setUnderlineText(false);
                        ds.clearShadowLayer();
                    }
                })
                .create();
        tvProtocol.setText(builder);
        tvProtocol.setMovementMethod(LinkMovementMethod.getInstance());
        tvProtocol.setHighlightColor(Color.TRANSPARENT);
    }

    public void checkLoginStatus(View view) {
        this.checkView = view;
        mActivity.showProgress(true);
        Map<String, String> map = new HashMap<>();
        if (CheckUtil.isEmpty(SpHp.getLogin(SpKey.LOGIN_MOBILE))
                || CheckUtil.isEmpty(SpHp.getLogin(SpKey.LOGIN_USERID))) {
            LoginSelectHelper.staticToGeneralLogin();
        } else {
            map.put("mobile", EncryptUtil.simpleEncrypt(SpHp.getLogin(SpKey.LOGIN_MOBILE)));
            map.put("userId", EncryptUtil.simpleEncrypt(SpHp.getLogin(SpKey.LOGIN_USERID)));
            netHelper.getService(ApiUrl.URL_GET_CHECK_BILL_BEAR_LOGIN, map);
        }
    }

    @OnClick({R.id.btn_cancel, R.id.btn_next})
    public void viewOnClick(View view) {
        if (view.getId() == R.id.btn_next) {
            if (!cbAgree.isChecked()) {
                UiUtil.toast("请仔细阅读协议内容");
                return;
            }
            mActivity.showProgress(true);
            Map<String, String> map = new HashMap<>();
            map.put("mobile", EncryptUtil.simpleEncrypt(SpHp.getLogin(SpKey.LOGIN_MOBILE)));
            map.put("userId", EncryptUtil.simpleEncrypt(SpHp.getLogin(SpKey.LOGIN_USERID)));
            netHelper.getService(ApiUrl.URL_GET_UNION_BILL_BEAR_LOGIN, map);
        } else {
            cbAgree.setChecked(false);
            dismiss();
        }
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        if (ApiUrl.URL_GET_CHECK_BILL_BEAR_LOGIN.equals(url)) {
            Map map = (Map) t;
            mActivity.showProgress(false);
            if (map != null) {
                if (map.containsKey("needLogin") && !CheckUtil.isEmpty(map.get("needLogin"))) {
                    String needLogin = String.valueOf(map.get("needLogin"));
                    if ("Y".equals(needLogin)) {
                        showAtLocation(checkView);
                        return;
                    }
                }
                String jumpUrl = String.valueOf(map.get("jumpUrl"));
                if (CheckUtil.isEmpty(jumpUrl)) {
                    return;
                }
                //WebSimpleFragment.WebService(mActivity, jumpUrl, "", WebSimpleFragment.STYLE_OTHERS);
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HOME_OTHER_WEB)
                        .put("isShowTitle", false).put("url", jumpUrl).navigation();
            }
        } else if (ApiUrl.URL_GET_UNION_BILL_BEAR_LOGIN.equals(url)) {
            mActivity.showProgress(false);
            Map map = (Map) t;
            String jumpUrl = String.valueOf(map.get("jumpUrl"));
            if (CheckUtil.isEmpty(jumpUrl)) {
                return;
            }
            //WebSimpleFragment.WebService(mActivity, jumpUrl, "", WebSimpleFragment.STYLE_OTHERS);
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HOME_OTHER_WEB)
                    .put("isShowTitle", false).put("url", jumpUrl).navigation();
            dismiss();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        mActivity.showProgress(false);
        if (error == null || error.getHead() == null) {
            showDialog("服务器开小差了，请稍后再试");
        } else {
            showDialog(error.getHead().getRetMsg());
        }
    }
}
