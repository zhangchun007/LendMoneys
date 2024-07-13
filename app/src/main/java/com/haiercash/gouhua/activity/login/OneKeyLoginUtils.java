package com.haiercash.gouhua.activity.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.geetest.onelogin.OneLoginHelper;
import com.geetest.onelogin.activity.OneLoginActivity;
import com.geetest.onelogin.config.AuthRegisterViewConfig;
import com.geetest.onelogin.config.OneLoginThemeConfig;
import com.geetest.onelogin.config.UserInterfaceStyle;
import com.geetest.onelogin.listener.AbstractOneLoginListener;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseDialog;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.beans.login.QueryAgreementListBean;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.widget.LoadingProgress;
import com.networkbench.agent.impl.NBSAppAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description: 极验一键登录
 * @Author: zhangchun
 * @CreateDate: 11/24/22
 * @Version: 1.0
 */
public class OneKeyLoginUtils implements INetResult {


    /**
     * 请求成功的响应值
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * 一键登录 浮窗式
     */
    public static final int ONELOGIN_FLOAT_MODE = 0;
    /**
     * 一键登录 弹窗式
     */
    public static final int ONELOGIN_DIALOG_MODE = 1;
    /**
     * 一键登录 沉浸式
     */
    public static final int ONELOGIN_FULLSCREEN_MODE = 2;
    /**
     * 一键登录 横屏
     */
    public static final int ONELOGIN_LANDSCAPE_MODE = 3;


    public volatile static OneKeyLoginUtils mInstance;

    private OneKeyLoginListener oneKeyLoginListener;
    private Activity mActivity;

    private LoadingProgress mLoadingProgress;
    private WeakReference<Activity> authActivityWeakReference;

    protected NetHelper netHelper;

    //构造函数私有
    private OneKeyLoginUtils() {
    }

    public static OneKeyLoginUtils getInstance() {
        return SingletonHolder.mInstance;
    }

    /**
     * 静态内部类
     */
    private static class SingletonHolder {
        private static final OneKeyLoginUtils mInstance = new OneKeyLoginUtils();
    }


    /**
     * 拉起授权页
     */
    public void login(Activity activity, OneKeyLoginListener listener) {
        oneKeyLoginListener = listener;
        OneLoginHelper.with().register(CommonConfig.ONE_KEY_SECRET);
        if (netHelper == null)
            netHelper = new NetHelper(this);
        //取号是否有效
        if (OneLoginHelper.with().isPreGetTokenResultValidate()) {
            showOneKeyLoginDialog(activity);
        } else { //预取号失败
            if (activity != null && (activity.getClass().getSimpleName().equals(SmsWayLoginActivity.class.getSimpleName()) || activity.getClass().getSimpleName().equals(PasswordWayLoginActivity.class.getSimpleName()))) {
                UiUtil.toast("网络异常，请打开数据流量后重试");
            }
            Map<String, Object> map = new HashMap<>();
            map.put("page", "一键登录页面");
            map.put("message", "预取号失败");
            NBSAppAgent.reportError("预取号失败", map);
            oneKeyLoginListener.oneKeyLoginError();
        }


    }

    public void showPrivacyDialog(Activity activity, String type, String title, String content, Bundle bundle) {
        showPrivacyDialog(activity, type, title, content, bundle, "");
    }

    /**
     * 协议隐私为勾选弹框
     *
     * @param activity
     */
    public void showPrivacyDialog(Activity activity, String type, String title, String content, Bundle bundle, String permissionName) {
        if (activity == null || activity.isFinishing()) return;
        Intent intent = new Intent(activity, ProtocolActivity.class);
        intent.putExtra("type", type)
                .putExtra("content", content)
                .putExtra("title", title)
                .putExtra("permissionName", permissionName);

        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
    }

    /**
     * 一键登录dialog
     *
     * @param activity
     */
    private void showOneKeyLoginDialog(Activity activity) {
        if (activity == null) return;
        initThirdLogin(activity);
        OneLoginHelper.with().requestToken(generateUiConfig(activity, ONELOGIN_FLOAT_MODE), new AbstractOneLoginListener() {
            @Override
            public void onResult(JSONObject jsonObject) {
                try {
                    int status = jsonObject.getInt("status");
                    //status=200 表示取号成功，可携带 process_id、token、authcode 去服务端换取手机号
                    if (status == SUCCESS_CODE) {
                        String process_id = jsonObject.getString("process_id");
                        String token = jsonObject.getString("token");
                        /**
                         * authcode值在电信卡通过 token 换取手机号时必要参数，为了避免服务端校验出错，尽量三网都传
                         */
                        String authCode = jsonObject.optString("authcode");

                        if (oneKeyLoginListener != null && !CheckUtil.isEmpty(process_id) && !CheckUtil.isEmpty(token) && !CheckUtil.isEmpty(authCode))
                            oneKeyLoginListener.onLoginButtonClick(true, process_id, token, authCode);
                    } else {
                        closeLoading();
                        String errorCode = jsonObject.getString("errorCode");
                        String process_id = jsonObject.getString("process_id");
                        Logger.e("onResult---" + errorCode);
                        if (errorCode.equals("-20301") || errorCode.equals("-20302")) {
                            Logger.e("用户点击返回键关闭了授权页面");
                            UMengUtil.pageEnd("OnetouchloginPop");
                            postDismissButtonEvent();
                            return;
                        }

                        String failrReason = jsonObject.getJSONObject("metadata").getString("error_data");
                        //上报一键登录获取token失败场景
                        postErrorMsgToServer(errorCode, "极验获取token失败", "requestToken()", failrReason,process_id);
                        if (mActivity != null) {
                            showDialog(mActivity, "提示", "网络开小差了，请稍后重试～", (dialog, which) -> {
                                OneLoginHelper.with().dismissAuthActivity();
                                UMengUtil.pageEnd("OnetouchloginPop");
                                loginEvent(false, failrReason);
                            });
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //登录按钮点击
            @Override
            public void onLoginButtonClick() {
                super.onLoginButtonClick();
                if (!isPrivacyCheckBoxChecked()) { //协议框是选中
                    if (oneKeyLoginListener != null)
                        oneKeyLoginListener.onLoginButtonClick(false, "", "", "");
                }

                loginButtonEvent();
            }

            //隐私条款点击
            @Override
            public void onPrivacyClick(String name, String url) {
                super.onPrivacyClick(name, url);
                Logger.e("onPrivacyClick---" + name + "---url==" + url);
                if (oneKeyLoginListener != null) {
                    if (url.startsWith("http")) {
                        oneKeyLoginListener.onPrivacyClick(name, url);
                    } else {
                        oneKeyLoginListener.onPrivacyClick(name, ApiUrl.API_SERVER_URL + url);
                    }
                }

            }

            @Override
            public void onLoginLoading() {
                super.onLoginLoading();
                if (mLoadingProgress == null) {
                    mLoadingProgress = new LoadingProgress(authActivityWeakReference.get());
                }
                mLoadingProgress.showLoadingDialog();

            }

            @Override
            public void onAuthActivityCreate(Activity activity) {
                super.onAuthActivityCreate(activity);
                mActivity = activity;
                authActivityWeakReference = new WeakReference<>(activity);
                UMengUtil.pageStart("OnetouchloginPop");
                Logger.e("onAuthActivityCreate---" + activity.getClass().getSimpleName());
                if (oneKeyLoginListener != null) {
                    oneKeyLoginListener.onAuthActivityCreate(activity);
                }
            }

            @Override
            public void onPrivacyCheckBoxClick(boolean isChecked) {
                super.onPrivacyCheckBoxClick(isChecked);
                postPrivacyCheckBoxEvent(isChecked, activity);
                if (isChecked) {
                    if (!CommomUtils.noNeedPhoneStateDialog()) {
                        showPrivacyDialog(activity, ProtocolActivity.TYPE_PERMISSION, "提示", activity.getString(R.string.permission_phone_statue), null, Manifest.permission.READ_PHONE_STATE);
                    }
                }
            }

            @Override
            public void onBackButtonClick() {
                super.onBackButtonClick();
                //取消动作（关闭或者回退）
                UMengUtil.pageEnd("OnetouchloginPop");
                AppApplication.doLoginCallback();
            }
        });
        activity.overridePendingTransition(R.anim.activity_dialog_in, 0);

    }

    /**
     * 关闭一键登录页面
     */
    public static void dismissOneKeyLoginPage() {
        Activity oneLoginA;
        if ((oneLoginA = ActivityUntil.findActivity(OneLoginActivity.class)) != null && !oneLoginA.isFinishing()) {
            OneLoginHelper.with().dismissAuthActivity();
            UMengUtil.pageEnd("OnetouchloginPop");
        }
        AppApplication.doLoginCallback();
    }

    public void postPrivacyCheckBoxEvent(boolean isChecked, Activity activity) {
        Map map = new HashMap();
        map.put("page_name_cn", "一键登录弹窗");
        if (isChecked) {
            map.put("agreement", setAgreement((FragmentActivity) activity, "").toString());
            map.put("button_name", "勾选协议");
            UMengUtil.onEventObject("OtpAgreementCheck_Click", map, "OnetouchloginPop");
        } else {
            map.put("button_name", "取消勾选协议");
            UMengUtil.onEventObject("OtpAgreementUncheck_Click", map, "OnetouchloginPop");
        }

    }

    private void postDismissButtonEvent() {
        Map map = new HashMap();
        map.put("button_name", "弹窗关闭按钮");
        map.put("page_name_cn", "一键登录弹窗");
        UMengUtil.onEventObject("OtpClose_Click", map, "OnetouchloginPop");
    }

    private void loginButtonEvent() {
        Map map = new HashMap();
        map.put("button_name", "登录");
        map.put("page_name_cn", "一键登录弹窗");
        UMengUtil.onEventObject("OtpLogin_Click", map, "OnetouchloginPop");
    }

    public void loginEvent(boolean isSuccess, String reaseon) {
        Map map = new HashMap();
        if (isSuccess) {
            map.put("is_success", "true");
        } else {
            map.put("is_success", "false");
            map.put("fail_reason", reaseon);
        }
        map.put("button_name", "一键登录按钮");
        map.put("page_name_cn", "一键登录弹窗");
        UMengUtil.onEventObject("OtpLogin_Result", map, "OnetouchloginPop");
    }

    public void thirdLoginPhoneEvent() {
        Map map = new HashMap();
        map.put("button_name", "验证码登录");
        map.put("page_name_cn", "一键登录弹窗");
        UMengUtil.onEventObject("OtpMessageLogin_Click", map, "OnetouchloginPop");
    }


    public void thirdLoginWechatEvent() {
        Map map = new HashMap();
        map.put("button_name", "微信登录");
        map.put("page_name_cn", "一键登录弹窗");
        UMengUtil.onEventObject("OtpWechatLogin_Click", map, "OnetouchloginPop");
    }


    /**
     * 是否预取号成功
     *
     * @return
     */
    private boolean isPrivacyCheckBoxChecked() {
        return OneLoginHelper.with().isPrivacyChecked();
    }

    /**
     * 设置隐私政策选中状态
     *
     * @param checked
     */
    public void setPrivacyCheck(boolean checked, BaseActivity activity) {
        OneLoginHelper.with().setProtocolCheckState(checked);
        if (checked) {
            if (!CommomUtils.noNeedPhoneStateDialog()) {
                showPrivacyDialog(activity, ProtocolActivity.TYPE_PERMISSION, "提示", activity.getString(R.string.permission_phone_statue), null, Manifest.permission.READ_PHONE_STATE);
            }
        }
    }


    /**
     * 获取运营商协议
     */
    public QueryAgreementListBean getMobilePrivacyUrl(Context context) {
        QueryAgreementListBean agreementListBean = new QueryAgreementListBean();
        JSONObject jsonObject = OneLoginHelper.with().getCurrentNetworkInfo(context);
        String operatorType = jsonObject.optString("operatorType");
        switch (operatorType) {
            case "1"://移动
                agreementListBean.setContName("《中国移动认证服务条款》");
                agreementListBean.setContUrl("https://wap.cmpassport.com/resources/html/contract.html");
                break;
            case "2"://联通
                agreementListBean.setContName("《联通统一认证服务条款》");
                agreementListBean.setContUrl("https://opencloud.wostore.cn/authz/resource/html/disclaimer.html?fromsdk=true\n");
                break;
            case "3"://电信
                agreementListBean.setContName("《天翼账号服务与隐私协议》");
                agreementListBean.setContUrl("https://e.189.cn/sdk/agreement/detail.do?hidetop=true\n");
                break;
            default:
                break;
        }
        agreementListBean.setContType("third_mobile_operator");
        ;
        return agreementListBean;
    }


    /**
     * 获取隐私协议文案
     *
     * @return
     */

    private String[] getPrivacyTextString() {
        List<String> list = new ArrayList<String>();
        list.add("我已阅读并同意够花APP");
        List<QueryAgreementListBean> agreementList = LoginNetHelper.getDefaultAgreementList();
        for (int i = 0; i < agreementList.size(); i++) {
            list.add(agreementList.get(i).getContName());
            list.add(agreementList.get(i).getContUrl());
            list.add("");
            if (i == agreementList.size() - 1) {
                list.add("及");
            } else {
                list.add(" ");
            }
        }

        list.add("");
        list.add("");
        list.add("");
        return list.toArray(new String[list.size()]);
    }


    /**
     * 设置注册协议样式
     */
    public SpannableStringBuilder setAgreement(FragmentActivity mActivity, String text) {
        List<QueryAgreementListBean> list = LoginNetHelper.getDefaultAgreementList();
        list.add(getMobilePrivacyUrl(mActivity));
        SpannableStringUtils.Builder builder = SpannableStringUtils.getBuilder(mActivity, text);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (i != 0) {
                    builder.append("，").setForegroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                }
                int finalI = i;
                if (i == list.size() - 1) builder.append("及");
                builder.append(list.get(i).getContName()).setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if ("third_mobile_operator".equals(list.get(finalI).getContType())) { //第三方运营商协议
                            WebSimpleFragment.WebService(mActivity, list.get(finalI).getContUrl(), list.get(finalI).getContName());
                        } else {
                            WebSimpleFragment.WebService(mActivity, ApiUrl.API_SERVER_URL + list.get(finalI).getContUrl(), list.get(finalI).getContName());
                        }
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                        ds.setUnderlineText(false);
                        ds.clearShadowLayer();
                    }
                });
            }
        }

        return builder.create();
    }


    /**
     * 配置页面布局(默认竖屏)
     * 注意事项：
     * 1. 配置项的图片，地址都应为`drawable`目录下资源文件名称，支持配置静态 png/jpg 图片，或者带状态选择的 xml 文件
     * 2. OffsetY 与 OffsetY_B 两者必须有一个值为 0,偏移量以不为 0 的方向作为基准。
     * 3. 颜色为int类型，尽量使用res中的colorId，例如 R.color.colorPrimary。也可以使用0xFFFFFFFF。
     * sdk会优先按照colorId去查找资源，若查找不到则按照固定颜色值使用，传0会认为无效值，将使用默认颜色
     *
     * @return OneLoginThemeConfig
     */
    private OneLoginThemeConfig generateUiConfig(Activity context, int mode) {//宽高比0.75
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        int width = DensityUtils.px2dip(context, screenWidth);
        int height = (int) (width / 0.75);
        int navigationHeight = DensityUtils.px2dip(context, getNavigationBarHeight(context));
        return new OneLoginThemeConfig.Builder().
                setDialogTheme(true, width, height, 0, 0, true, true)
                .setAuthBGImgPath("img_one_key_login_bg").setNavigationBar(Color.BLACK, UserInterfaceStyle.UNSPECIFIED, false)
                .setAuthNavLayout(R.color.colorPrimary, 49, true, false)
                .setAuthNavTextView("", 0xFFFFFFFF, 17, false, "服务条款", 0xFF000000, 17)
                .setAuthNavReturnImgView("img_one_key_login_dismiss", 15, 15, false, width - 33, 20)
                .setLogoImgView("img_one_key_login_logo", 75, 75, false, 0, (int) (height * 0.71), 0)
                .setSlogan(false)
                .setNumberView(R.color.color_verify_identity, 35, 0, (int) (height * 0.58), 0)
                .setNumberViewTypeface(FontCustom.getDinAlternateBoldFont(context))
                .setSwitchView("切换账号", R.color.colorPrimary, 14, true, 249 - navigationHeight, 0, 0)
                .setLogBtnLayout("shape_one_key_login_btn_select_bg", "shape_one_key_login_btn_select_bg", 335, 48, 0, (int) (height * 0.274), 0)
                .setLogBtnTextView("本机号码一键登录", 0xFFFFFFFF, 18).setLogBtnLoadingView("", 20, 20, 12)
                .setPrivacyUnCheckedToastText(false, "") //可以关闭SDK提供的toast提示，在点击授权的回调中自定义提示
                .setPrivacyCheckBox("img_one_key_login_unchecked_box", "img_one_key_login_checked_box", false, 14, 14, 0)
                .setPrivacyLayout(316, 0, (int) (height * 0.408), 0, false)
                .setPrivacyClauseView(R.color.text_gray_light, R.color.colorPrimary, 12)
                .setPrivacyClauseTextStrings(getPrivacyTextString())
                .setPrivacyAddFrenchQuotes(true)
                .build();
    }


    /**
     * 自定义的第三方登录设置
     */
    private void initThirdLogin(Activity context) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int width = DensityUtils.px2dip(context, screenWidth);
        int height = (int) (width / 0.75);
        LayoutInflater inflater1 = LayoutInflater.from(context);
        RelativeLayout relativeLayout = (RelativeLayout) inflater1.inflate(R.layout.layout_one_key_third_login, null);
        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOther.setMargins(0, DensityUtils.dip2px(context, (int) (height * 0.714)), 0, 0);
        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
        relativeLayout.setLayoutParams(layoutParamsOther);
        relativeLayout.findViewById(R.id.wechat).setOnClickListener(v -> {

            if (oneKeyLoginListener != null) oneKeyLoginListener.thirdLogin("wechat");
            thirdLoginWechatEvent();

        });
        relativeLayout.findViewById(R.id.phone).setOnClickListener(v -> {

            if (oneKeyLoginListener != null) oneKeyLoginListener.thirdLogin("phone");

            thirdLoginPhoneEvent();
        });

        /**
         * 添加自定义控件，addOneLoginRegisterViewConfig 调用多次可添加多个自定义控件，用不同的 id 区分
         * 注意添加的控件不要覆盖下面原有的控件，不然可能导致无法点击的问题
         */
        OneLoginHelper.with().addOneLoginRegisterViewConfig("third_login_button", new AuthRegisterViewConfig.Builder().setView(relativeLayout).setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY).setCustomInterface(context1 -> {
        }).build());
    }


    /**
     * 获得NavigationBar的高度
     */
    public int getNavigationBarHeight(Activity activity) {
        int result = 0;
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && checkHasNavigationBar(activity)) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }


    /**
     * 判断是否有NavigationBar
     */
    public boolean checkHasNavigationBar(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * 展示一个Dialog提示，每个页面使用一个AlertDialog，防止多层覆盖
     */

    private BaseDialog adToast;

    public BaseDialog showDialog(Context context, CharSequence title, CharSequence msg, DialogInterface.OnClickListener listener) {
        if (adToast != null) {
            adToast.dismiss();
            adToast = null;
        }
        adToast = BaseDialog.getDialog(context, title, msg, "我知道了", listener).setButtonTextColor(1, R.color.colorPrimary);
        adToast.show();
        return adToast;
    }


    public void closeLoading() {
        if (mLoadingProgress != null) {
            mLoadingProgress.cancelLoadingDialog();
        }
    }

    /**
     * 上报异常数据
     *
     * @param code        错误码
     * @param type        类型（哪个sdk）
     * @param errorMethod 问题方法
     * @param msg         详细信息
     */
    public void postErrorMsgToServer(String code, String type, String errorMethod, String msg, String process_id) {
        if (netHelper != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("code", code);
            map.put("type", type);
            map.put("errorMethod", errorMethod);
            map.put("msg", code + ":" + msg);
            map.put("process_id", process_id);
            netHelper.postService(ApiUrl.POST_APP_ACTION_LOG, map);
        }

    }

    @Override
    public <T> void onSuccess(T t, String url) {

    }

    @Override
    public void onError(BasicResponse error, String url) {

    }


}
