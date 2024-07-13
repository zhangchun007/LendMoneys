package com.haiercash.gouhua.x5webview;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.URLUtil;

import com.alipay.sdk.app.OpenAuthTask;
import com.alipay.sdk.app.PayTask;
import com.app.haiercash.base.interfaces.SaveImageResult;
import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.FileUtils;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.image.ImageUtils;
import com.app.haiercash.base.utils.image.PhotographUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.permission.PermissionUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.PermissionPageUtils;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.baidu.location.Address;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.ScanQrCodeActivity;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.activity.comm.UserLoanStatusHelper;
import com.haiercash.gouhua.activity.edu.PerfectInfoHelper;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.activity.login.SmsCodeActivity;
import com.haiercash.gouhua.activity.login.SmsWayLoginActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.risk.GeoBean;
import com.haiercash.gouhua.beans.risk.RiskBean;
import com.haiercash.gouhua.bill.AllBillsFragment;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.jsweb.JsWebPopActivity;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.tplibrary.FaceOcrHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.CallPhoneNumberHelper;
import com.haiercash.gouhua.utils.AIServer;
import com.haiercash.gouhua.utils.CalendarUtils;
import com.haiercash.gouhua.utils.ClickCouponToUseUtil;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.DischargeUtil;
import com.haiercash.gouhua.utils.GhLocation;
import com.haiercash.gouhua.utils.RiskKfaUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.WyDeviceIdUtils;
import com.haiercash.gouhua.wxapi.WxUntil;
import com.megvii.idcardlib.IDCardScanActivity;
import com.megvii.idcardlib.utils.Configuration;
import com.megvii.livenesslib.FaceUntil;
import com.megvii.livenesslib.IFaceCallBack;
import com.megvii.livenesslib.baiduface.BaiduFaceUtils;
import com.megvii.livenesslib.baiduface.utils.IBaiduFaceCallBack;
import com.tbruyelle.rxpermissions2.Permission;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/4/28<br/>
 * 描    述：统一注册JsBridge<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class WebDataImpl implements INetResult, IFaceCallBack, IBaiduFaceCallBack {
    public static boolean IS_DEBUG_WEB = false;
    public static final String METHOD_USERINFO = "getUserInfo";
    public static final int CardBack = 0x01;   //身份证正面
    public static final int CardFront = 0x00;  //身份证反面
    public static final int START_VIP_COUPON = 0x02;  //打开会员页
    public static final int PHONE_NUM_REQUEST_CODE = 0x03; //打开通讯录

    private final BaseActivity mActivity;
    private final NetHelper mNetHelper;
    private final CusWebView webView;
    /**
     * true：当前页面为开屏广告
     */
    private final boolean isAdvert;
    /**
     * 如果从开屏广告页-》锁屏-》当前网页->需要做的lend或者cash
     */
    private final String webDoType;
    private boolean isBorrow;
    private final ClickCouponToUseUtil couponToUseUtil;
    private static RiskBean riskBean;
    private FaceUntil faceUntil;

    private CallBackFunction currentFunction;//记录当前H5传递的callback
    private String cardType;  //H5调用原生OCR类型，0：正反连拍 1：身份证正面 2：国徽面
    private final Map<String, String> ocrMap = new HashMap<>();//h5调用ocr需要的callback
    private String currentBizToken;  //调用人脸h5传递的bizToken
    private GhLocation ghLocation;
    private GeoBean bean1;

    public static WebDataImpl getInstance(BaseActivity mActivity, CusWebView webView) {
        WebDataImpl webData = new WebDataImpl(mActivity, webView);
        webData.initWebMethod();
        return webData;
    }

    private WebDataImpl(BaseActivity mActivity, CusWebView webView) {
        this.mActivity = mActivity;
        this.webView = webView;
        isAdvert = mActivity.getIntent().getBooleanExtra("isAdvert", false);
        webDoType = mActivity.getIntent().getStringExtra("webDoType");
        mNetHelper = new NetHelper(this);
        couponToUseUtil = new ClickCouponToUseUtil(mActivity);
    }

    private void initWebMethod() {
        /* *** 自定义JS和Native方法联动  *** */
        webView.setDefaultHandler((data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("DefaultHandler " + data);
            }
            if (function != null) {
                function.onCallBack("Native get Success!");
            }
        });
        //javascript调用 登录方法 login
        webView.registerHandler("login", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->login, data from web = " + data);
            }
            //登录成功后打开的H5页面
            //URL不为空，则登录成功后打开URL页面
            //URL为空，则登录成功后跳转当前H5页面
            if (!AppApplication.isLogIn()) {
                if (!CheckUtil.isEmpty(data)) {
                    gotoLoginIn(data, "login");
                } else {
                    gotoLoginIn(webView.getUrl(), "login");
                }
            } else if (!CheckUtil.isEmpty(data)) {
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HOME_OTHER_WEB).put("isShowTitle", false).put("url", data).navigation();
            } else {
                function.onCallBack("true");
            }
        });
        //javascript调用 用户信息 getUserInfo
        webView.registerHandler(METHOD_USERINFO, (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getUserInfo, data from web = " + data);
            }
            //够花4.0修改，为兼容h5地址与App开头不一致问题
            if (webView.getUrl().contains(".haiercash.com")) {
                Map<String, String> map = new HashMap<>();
                if (!CheckUtil.isEmpty(data)) {
                    String value = "userId".equals(data) ? SpHp.getLogin(SpKey.LOGIN_USERID) : ("custNo".equals(data) ? SpHp.getUser(SpKey.USER_CUSTNO) : ("mobile".equals(data) ? SpHp.getLogin(SpKey.LOGIN_MOBILE) : ("access_token".equals(data) ? TokenHelper.getInstance().getCacheToken() : "")));
                    map.put(data, value);
                } else {
                    // APP需要判断当前页面域名是否够花域名host
                    map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
                    map.put("custNo", SpHp.getUser(SpKey.USER_CUSTNO));
                    map.put("mobile", SpHp.getLogin(SpKey.LOGIN_MOBILE));
                    map.put("access_token", TokenHelper.getInstance().getCacheToken());
                }
                function.onCallBack(JsonUtils.toJson(map));
            } else {
                mActivity.showDialog("无相关权限", "确定", null, null);
            }
        });
        //javascript调用 分享: share
        webView.registerHandler("share", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->share, data from web = " + data);
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String title = map.get("title");
            String desc = map.get("desc");
            String url = map.get("url");
            String img = map.get("img");
            ARouterUntil.getInstance(PagePath.ACTIVITY_SHARE).put("title", title).put("desc", desc).put("url", url).put("img", img).navigation();
        });
        //javascript调用 额度申请：lend
        webView.registerHandler("lend", (data, function) -> {
            lend();
            function.onCallBack("Native lend ....");
        });
        //javascript调用 打开在线客服：lend
        webView.registerHandler("openAi", (data, function) -> {
            openAi();
            function.onCallBack("Native openAi ....");
        });
        //javascript调用 额度支用：cash
        webView.registerHandler("cash", (data, function) -> {
            cash();
            function.onCallBack("Native cash ....");
        });

        //javascript调用 getToken 获取App的token
        webView.registerHandler("getToken", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getToken, data from web = " + data);
            }
            //登录成功后打开的H5页面
            //URL不为空，则登录成功后打开URL页面
            //URL为空，则登录成功后跳转当前H5页面
            Map<String, String> map = new HashMap<>();
            if (!AppApplication.isLogIn()) {
                if (!CheckUtil.isEmpty(data)) {
                    gotoLoginIn(data, "login");
                } else {
                    gotoLoginIn(webView.getUrl(), "login");
                }
            } else {
                map.put("access_token", TokenHelper.getInstance().getCacheToken());
            }
            function.onCallBack(JsonUtils.toJson(map));
        });

        //javascript调用 scanQrCode 开启扫码
        webView.registerHandler("scanQrCode", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->scanQrCode, data from web = " + data);
            }
            //登录成功后打开的H5页面
            //URL不为空，则登录成功后打开URL页面
            //URL为空，则登录成功后跳转当前H5页面
            if (!AppApplication.isLogIn()) {
                gotoLoginIn(webView.getUrl(), "login");
            } else {
                mActivity.startActivity(new Intent(mActivity, ScanQrCodeActivity.class));
            }
        });

        //javascript调用 在一级页面需要调用此方法打开新的activity
        webView.registerHandler("openNewWebView", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->openNewWebView, data from web = " + data);
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            if (map.containsKey("jumpUrl") && !CheckUtil.isEmpty(map.get("jumpUrl"))) {
                String url = map.get("jumpUrl");
                Intent intent = new Intent(mActivity, JsWebBaseActivity.class);
                intent.putExtra("jumpKey", url);
                mActivity.startActivity(intent);
            }
            if (map.containsKey("needClose") && "Y".equals(map.get("needClose"))) {
                if (mActivity != null) {
                    mActivity.finish();
                }
            }
        });

        //javascript调用 跳转结清证明
        webView.registerHandler("getSettlement", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getSettlement, data from web = " + data);
            }
            if (CommomUtils.isRealName()) {
                DischargeUtil util = new DischargeUtil(mActivity);
                util.getDischarge();
            } else {
                mActivity.showBtn2Dialog("您在过去24个月内暂无结清账单，如需开具24个月之前账单的结清证明,请联系客服人员。", "我知道了", ((dialog, which) -> dialog.dismiss()));
            }
        });
        //javascript调用 原生全部待还，带过来流水号
        webView.registerHandler("billDetail", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->billDetail, data from web = " + data);
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String applSeq = map.get("applSeq");
            //String desc = map.get("loanNo");
            Bundle extra = new Bundle();
            extra.putString("applSeq", applSeq);
            ContainerActivity.to(mActivity, AllBillsFragment.ID, extra);
        });
        //javascript调用原生 关闭webView页，萨摩耶中收项目v4.1.6新增
        webView.registerHandler("closeWeb", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->closeWeb, data from web = " + data);
            }
            mActivity.finish();
        });
        //javascript调用原生 点击券“去使用”按钮，萨摩耶中收项目v4.1.6新增，带券号过来
        webView.registerHandler("clickCouponToUse", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->clickCouponToUse, data from web = " + data);
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String couponNo = map.get("couponNo");
            couponToUseUtil.clickCouponToUse(couponNo);
        });
        //javascript调用原生返回，萨摩耶中收项目v4.1.6新增
        webView.registerHandler("webGoBack", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->webGoBack, data from web = " + data);
            }
            mActivity.onBackPressed();
            function.onCallBack("Success");
        });
        //javascript通知web已经加载H5完毕，萨摩耶中收项目v4.1.6新增
        webView.registerHandler("webLoaded", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->webLoaded, data from web = " + data);
            }
            if (mActivity instanceof JsWebBaseActivity) {
                ((JsWebBaseActivity) mActivity).onH5LoadFinished();
            }
            function.onCallBack("Success");
        });
        /*
         * 提供方法”setTitleBarStyle“，
         * 参数：iconId，title，clickAction，
         * APP根据H5提供的iconId做映射获取到本地写死的图片，
         * 或者设置title设置文本，根据clickAction映射。
         */
        webView.registerHandler("setTitleBarStyle", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->setTitleBarStyle, data from web = " + data);
            }
            if (CheckUtil.isEmpty(data)) {
                return;
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String iconName = map.get("iconId");
            String title = map.get("title");
            String clickAction = map.get("clickAction");
            String showBackground = map.get("showBackground");
            String backColor = map.get("backColor");

            if (mActivity != null && mActivity instanceof JsWebBaseActivity) {
                if ("Y".equals(showBackground)) {
                    ((JsWebBaseActivity) mActivity).setTitleBarBackground(true, R.drawable.icon_title_bar, "");

                } else if (!CheckUtil.isEmpty(backColor)) {
                    ((JsWebBaseActivity) mActivity).setTitleBarBackground(false, 0, backColor);
                } else {
                    ((JsWebBaseActivity) mActivity).setTitleBarBackground(false, 0, "");
                }
                if (!TextUtils.isEmpty(iconName)) {
                    ((JsWebBaseActivity) mActivity).setTitleBarRightText("", null);
                    if ("apply_amount_help".equals(iconName)) {
                        ((JsWebBaseActivity) mActivity).setTitleBarRightImage(true, R.drawable.iv_blue_details, v -> {
                            if ("applyAmountHelp".equals(clickAction)) { //申额右上角问号
                                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER).navigation();
                            }
                        });
                    }
                } else {
                    ((JsWebBaseActivity) mActivity).setTitleBarRightImage(false, 0, null);
                }
                if (!TextUtils.isEmpty(title)) {
                    ((JsWebBaseActivity) mActivity).setTitleBarRightImage(false, 0, null);
                    ((JsWebBaseActivity) mActivity).setTitleBarRightText(title, v -> {
                        if ("applyAmountFinish".equals(clickAction)) { //申额右上角完成
                            ARouterUntil.getInstance(PagePath.ACTIVITY_MAIN).navigation();
                            ActivityUntil.finishOthersActivityByPageKey(PagePath.ACTIVITY_MAIN);
                        } else if ("closeWeb".equals(clickAction)) {
                            mActivity.finish();
                        } else if ("backPress".equals(clickAction)) {
                            ((JsWebBaseActivity) mActivity).onBackPressed();
                        } else if (TextUtils.isEmpty(clickAction)) {
                            function.onCallBack("SUCCESS");
                        }
                    });
                } else {
                    ((JsWebBaseActivity) mActivity).setTitleBarRightText("", null);
                }
                //function.onCallBack("Success");
            }
        });
        webView.registerHandler("setTitleBarBack", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->setTitleBarBack, data from web = " + data);
            }
            if (CheckUtil.isEmpty(data)) {
                return;
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String backColor = map.get("backColor");
            String titleColor = map.get("titleColor");
            String isWhiteBack = map.get("isWhiteBack");

            if (mActivity != null && mActivity instanceof JsWebBaseActivity) {
                if (!CheckUtil.isEmpty(backColor)) {
                    ((JsWebBaseActivity) mActivity).setTitleBarBackground(false, 0, backColor);
                } else {
                    ((JsWebBaseActivity) mActivity).setTitleBarBackground(false, 0, "");
                }
                if (!CheckUtil.isEmpty(titleColor)) {
                    ((JsWebBaseActivity) mActivity).setTitleTextColor(titleColor);
                } else {
                    ((JsWebBaseActivity) mActivity).setTitleTextColor("");
                }
                if (!CheckUtil.isEmpty(isWhiteBack)) {
                    ((JsWebBaseActivity) mActivity).setHeadLeftIcon(!CheckUtil.isEmpty(isWhiteBack) && "Y".equals(isWhiteBack));
                } else {
                    ((JsWebBaseActivity) mActivity).setHeadLeftIcon(false);
                }
            }
        });

        //跳转到借款页
        webView.registerHandler("goBorrowPage", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->goBorrowPage, data from web = " + data);
            }
            ClickCouponToUseUtil couponToUseUtil = new ClickCouponToUseUtil(mActivity);
            couponToUseUtil.requestUserLoanStatus(false);
            function.onCallBack("Success");
        });

        //跳转到首页
        webView.registerHandler("goHomePage", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->goHomePage, data from web = " + data);
            }
            Map<String, String> messageMap = JsonUtils.getRequest(data);
            if (!CheckUtil.isEmpty(messageMap.get("message"))) {
                RxBus.getInstance().post(new ActionEvent(ActionEvent.HOMEPAGE_TOAST_MESSAGE, messageMap.get("message")));
            }
            if (ActivityUntil.findActivity(PagePath.ACTIVITY_MAIN) == null) {
                ARouterUntil.getInstance(PagePath.ACTIVITY_MAIN).put("mainShowIconId", R.id.rbHome).navigation();
            }
            ActivityUntil.finishOthersActivityByPageKey(PagePath.ACTIVITY_MAIN);
        });

        //跳转到设置页面
        webView.registerHandler("openSysSetting", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->openSysSetting, data from web = " + data);
            }
            mActivity.startActivity(new Intent(Settings.ACTION_SETTINGS));
            function.onCallBack("Success");
        });

        //H5提供base64图片给App保存
        webView.registerHandler("saveImage", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->saveImage, data from web = " + data);
            }
            Map<String, String> messageMap = JsonUtils.getRequest(data);
            String imageData = messageMap.get("imageData");
            if (!CheckUtil.isEmpty(imageData)) {
                Bitmap imageBitmap = ImageUtils.base64ToBitmap(imageData);
                if (imageBitmap != null) {
                    mActivity.requestPermission((Consumer<Boolean>) aBoolean -> {
                        if (aBoolean) {
                            FileUtils.saveImage(imageBitmap, "code.jpg", new SaveImageResult() {
                                @Override
                                public void onSaveFailed(String retFlag, String retMsg) {
                                    UiUtil.toast(retMsg);
                                }

                                @Override
                                public void onSaveSuccess(File file, String fileName) {
                                    UiUtil.toast("图片已保存到本地");
                                    FileUtils.updatePhotoAlbum(mActivity, file);
                                }
                            });
                        } else {
                            UiUtil.toast("保存失败，请授权存储权限");
                        }
                    }, R.string.permission_storage, Manifest.permission.WRITE_EXTERNAL_STORAGE);


                } else {
                    UiUtil.toast("保存失败，请稍后重试");
                }
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("errMsg", "传递的base64数据不能为空");
                function.onCallBack(JsonUtils.toJson(map));
            }
        });


        //H5获取缓存的
        webView.registerHandler("getH5UserInfo", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getH5UserInfo, data from web = " + data);
            }
            function.onCallBack(TokenHelper.getInstance().getH5LoginInfo());
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getH5UserInfo, data from App = " + TokenHelper.getInstance().getH5LoginInfo());
            }
        });

        //H5调用退出登录，只保持首页，并跳转到登录页
        webView.registerHandler("logout", (data, function) -> {
            Map<String, String> logoutMap = JsonUtils.getRequest(data);
            if (!CheckUtil.isEmpty(AppApplication.userid)) {
                logoutMap.put("userId", EncryptUtil.simpleEncrypt(AppApplication.userid));
                logoutMap.put("h5Token", TokenHelper.getInstance().getH5Token());
                mNetHelper.postService(ApiUrl.LOGOUT_URL, logoutMap);
            }
            CommomUtils.clearSp();
            Intent intent = new Intent();
            if (!CheckUtil.isEmpty(logoutMap.get("mobile"))) {
                intent.putExtra("mobile", logoutMap.get("mobile"));
            }
            if (!CheckUtil.isEmpty(logoutMap.get("changePhone"))) {
                intent.putExtra("changePhone", logoutMap.get("changePhone"));
            }
            SmsWayLoginActivity.startDialogActivity(mActivity, SmsWayLoginActivity.class, SmsWayLoginActivity.ANIM_BOTTOM_IN_RIGHT_OUT, intent);
            if (CheckUtil.isEmpty(logoutMap.get("changePhone"))) {
                ActivityUntil.finishOthersActivity(MainActivity.class, SmsWayLoginActivity.class);
            }
        });

        //H5调用退出登录，直接跳转到发送验证码页
        webView.registerHandler("goSmsLoginPage", (data, function) -> {
            Map<String, String> logoutMap = JsonUtils.getRequest(data);
            if (!CheckUtil.isEmpty(AppApplication.userid)) {
                logoutMap.put("userId", EncryptUtil.simpleEncrypt(AppApplication.userid));
                logoutMap.put("h5Token", TokenHelper.getInstance().getH5Token());
                mNetHelper.postService(ApiUrl.LOGOUT_URL, logoutMap);
            }
            CommomUtils.clearSp();
            Intent intent = new Intent();
            if (!CheckUtil.isEmpty(logoutMap.get("mobile"))) {
                intent.putExtra("mobileNo", logoutMap.get("mobile"));
            }

            intent.putExtra("changePhone", "Y");

            intent.putExtra("needHide", true);
            AppApplication.setLoginCallback(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    function.onCallBack(TokenHelper.getInstance().getH5LoginInfo());

                }
            });
            SmsCodeActivity.startDialogActivity(mActivity, SmsCodeActivity.class, SmsCodeActivity.ANIM_BOTTOM_IN_RIGHT_OUT, intent);
        });


        //H5调用人脸
        webView.registerHandler("getFaceData", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getFaceData, data from web = " + data);
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            Map<String, String> callbackMap = new HashMap<>();
            String bizToken = map.get("bizToken");
            currentFunction = function;
            currentBizToken = bizToken;
            //厂商 06代表旷世 10代表百度
            String organization = map.get("organization");
            if ("06".equals(organization)) {//旷世
                //旷世需要BizToken
                if (CheckUtil.isEmpty(bizToken)) {
                    callbackMap.put("errMsg", "bizToken不能为空");
                    function.onCallBack(JsonUtils.toJson(callbackMap));
                    return;
                }
                startOcrOrFaceCheck(false, callbackMap);
            } else if ("10".equals(organization)) {//百度
                startBaiDuFaceCheck(callbackMap);
            }


        });

        //H5调用支付宝
        webView.registerHandler("gouhuaAlipay", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->gouhuaAlipay, data from web = " + data);
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String orderInfo = map.get("orderInfo");
            if (CheckUtil.isEmpty(orderInfo)) {
                Logger.e("orderInfo", "orderInfo==" + orderInfo);
                return;
            }
            final Runnable payRunnable = new Runnable() {
                @Override
                public void run() {
                    PayTask alipay = new PayTask(mActivity);
                    Map<String, String> result = alipay.payV2(orderInfo, true);
                    Log.i("msp", result.toString());
                }
            };

            // 必须异步调用
            Thread payThread = new Thread(payRunnable);
            payThread.start();

        });

        //H5调用支付宝
        webView.registerHandler("gouhuaSingleAlipay", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->gouhuaSingleAlipay, data from web = " + data);
            }
            Map<String, String> sign_params = JsonUtils.getRequest(data);
            OpenAuthTask openAuthTask = new OpenAuthTask(mActivity);
            openAuthTask.execute("gouhuaalipay", OpenAuthTask.BizType.Deduct, sign_params, new OpenAuthTask.Callback() {
                @Override
                public void onResult(int resultCode, String s, Bundle bundle) {
                    Map<String, Object> errMap = new HashMap<>();
                    if (resultCode == OpenAuthTask.OK) {
                        errMap.put("signResult", true);
                    } else {
                        errMap.put("signResult", false);
                        errMap.put("resultCode", resultCode);
                    }
                    Logger.i("registerHandler->gouhuaSingleAlipay, result = " + JsonUtils.toJson(errMap));
                    Logger.i("registerHandler->gouhuaSingleAlipay, bundle = " + bundle.toString());
                    function.onCallBack(JsonUtils.toJson(errMap));
                }
            }, false);
        });

        //H5调用OCR
        //0：正反连拍
        //1：身份证正面
        //2：国徽面
        webView.registerHandler("getIdCardData", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getIdCardData, data from web = " + data);
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            cardType = map.get("cardType");
            Map<String, String> callbackMap = new HashMap<>();
            if (CheckUtil.isEmpty(cardType)) {
                callbackMap.put("errMsg", "cardType不能为空");
                function.onCallBack(JsonUtils.toJson(callbackMap));
                return;
            }
            currentFunction = function;
            startOcrOrFaceCheck(true, callbackMap);
        });

        //上送风险数据
        webView.registerHandler("postRiskData", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->postRiskData, data from web = " + data);
            }
            Map<String, String> errMap = new HashMap<>();
            if (CheckUtil.isEmpty(data)) {
                errMap.put("errMsg", "入参不能为空");
                function.onCallBack(JsonUtils.toJson(errMap));
                return;
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String isDelayPost = map.get("isDelayPost");
            String doType = map.get("doType");
            String eventId = map.get("eventId");
            String applySeq = map.get("applySeq");
            String brType = map.get("brType");
            String channelNo = map.get("channel_no");
            if (CheckUtil.isEmpty(eventId) || CheckUtil.isEmpty(doType) || CheckUtil.isEmpty(brType)) {
                errMap.put("errMsg", "eventId、doType、brType参数不能为空");
                function.onCallBack(JsonUtils.toJson(errMap));
                return;
            }

            if ("Y".equals(isDelayPost)) {
                if ("-2".equals(doType)) {
                    errMap.put("errMsg", "参数doType==-2与isDelayPost==Y互斥");
                    function.onCallBack(JsonUtils.toJson(errMap));
                } else {
                    getAndPostRiskData(true, doType, eventId, applySeq, brType, channelNo, function);
                }

            } else {
                getAndPostRiskData(false, doType, eventId, applySeq, brType, channelNo, function);
            }

        });

        //获取定位
        webView.registerHandler("getLocationData", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getLocationData, data from web = " + data);
            }
            bean1 = new GeoBean();
            mActivity.requestPermissionEachCombined(new Consumer<Permission>() {
                @Override
                public void accept(Permission permission) throws Exception {
                    if (permission.granted) {
                        bean1.permissionState = "1";
                        if (ghLocation == null) {//有权限自动定位
                            ghLocation = new GhLocation(mActivity, null);
                        }
                        ghLocation.setCallBack(new GhLocation.ILocationCallBack() {
                            @Override
                            public void callBack(boolean isSuccess, String reason) {
                                if (isSuccess) {
                                    getLocationData(bean1);
                                } else {
                                    bean1.errMsg = "定位失败";
                                }
                                function.onCallBack(JsonUtils.toJson(bean1));
                            }
                        });
                        ghLocation.requestLocationNoPermission();

                    } else {
                        if (!permission.shouldShowRequestPermissionRationale) {
                            goSystemSetting("定位");
                        }
                        bean1.permissionState = "0";
                        bean1.errMsg = "未授权位置权限";
                        function.onCallBack(JsonUtils.toJson(bean1));
                    }
                }
            }, R.string.permission_location, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        });
        //H5申请权限
        webView.registerHandler("requestPermission", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->requestPermission, data from web = " + data);
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String permissionName = map.get("permissionName");
            Map<String, String> callbackMap = new HashMap<>();
            if (CheckUtil.isEmpty(permissionName)) {
                callbackMap.put("errMsg", "permissionName不能为空");
                callbackMap.put("permissionState", "0");
                function.onCallBack(JsonUtils.toJson(callbackMap));
            } else {
                callbackMap.put("permissionName", permissionName);
                if ("camera".equals(permissionName)) {
                    mActivity.requestPermissionEachCombined(((Consumer<Permission>) permission -> {
                        if (permission.granted) {
                            callbackMap.put("permissionState", "1");
                        } else if (!permission.shouldShowRequestPermissionRationale) {
                            goSystemSetting("相机");
                            callbackMap.put("permissionState", "0");
                            callbackMap.put("errMsg", "没有相机权限");
                        } else {
                            callbackMap.put("permissionState", "0");
                            callbackMap.put("errMsg", "没有相机权限");
                        }
                        function.onCallBack(JsonUtils.toJson(callbackMap));

                    }), R.string.permission_camera, Manifest.permission.CAMERA);
                } else if ("sdCard".equals(permissionName)) {
                    mActivity.requestPermissionEachCombined(((Consumer<Permission>) permission -> {
                        if (permission.granted) {
                            callbackMap.put("permissionState", "1");
                        } else if (!permission.shouldShowRequestPermissionRationale) {
                            goSystemSetting("存储");
                            callbackMap.put("permissionState", "0");
                            callbackMap.put("errMsg", "没有存储权限");
                        } else {
                            callbackMap.put("permissionState", "0");
                            callbackMap.put("errMsg", "没有存储权限");
                        }
                        function.onCallBack(JsonUtils.toJson(callbackMap));

                    }), R.string.permission_storage, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    callbackMap.put("errMsg", "不支持的permissionName");
                    callbackMap.put("permissionState", "0");
                    function.onCallBack(JsonUtils.toJson(callbackMap));
                }
            }
        });

        //保存实名信息
        webView.registerHandler("saveCustInfo", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->saveCustInfo, data from web = " + data);
            }
            Map<String, String> errMap = new HashMap<>();
            if (CheckUtil.isEmpty(data)) {
                errMap.put("errMsg", "入参不能为空");
                function.onCallBack(JsonUtils.toJson(errMap));
                return;
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String custNo = map.get("custNo");
            String custName = map.get("custName");
            String certNo = map.get("certNo");
            String mobile = map.get("mobile");
            if (CheckUtil.isEmpty(custNo) || CheckUtil.isEmpty(custName) || CheckUtil.isEmpty(certNo) || CheckUtil.isEmpty(mobile)) {
                errMap.put("errMsg", "custNo、custName、certNo、mobile参数不能为空");
                function.onCallBack(JsonUtils.toJson(errMap));
                return;
            }
            SpHp.saveUser(SpKey.USER_CUSTNAME, custName);//客户姓名
            SpHp.saveUser(SpKey.USER_CUSTNO, custNo);//客户编号
            SpHp.saveUser(SpKey.USER_CERTNO, certNo);//证件号
            SpHp.saveUser(SpKey.USER_MOBILE, mobile);//实名认证手机号
        });

        //拨打电话
        webView.registerHandler("callPhone", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->callPhone, data from web = " + data);
            }
            Map<String, String> errMap = new HashMap<>();
            if (CheckUtil.isEmpty(data)) {
                errMap.put("errMsg", "入参不能为空");
                function.onCallBack(JsonUtils.toJson(errMap));
                return;
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String mobileNo = map.get("mobileNo");
            if (CheckUtil.isEmpty(mobileNo)) {
                errMap.put("errMsg", "mobileNo参数不能为空");
                function.onCallBack(JsonUtils.toJson(errMap));
                return;
            }
            CallPhoneNumberHelper.callPhone(mActivity, mobileNo);
        });

        //提供给h5 deviceId
        webView.registerHandler("appDeviceId", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->appDeviceId, data from web = " + data);
            }
            Map<String, String> map = new HashMap<>();
            map.put("deviceId", SystemUtils.getDeviceID(mActivity));
            function.onCallBack(JsonUtils.toJson(map));
        });

        //提供给h5 隐藏返回键
        webView.registerHandler("leftBarButtonHidden", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->leftBarButtonHidden, data from web = " + data);
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String isHidden = map.get("hidden");
            if (mActivity != null && mActivity instanceof JsWebBaseActivity) {
                ((JsWebBaseActivity) mActivity).setLeftImageVisible(CheckUtil.isEmpty(isHidden) || !"Y".equals(isHidden));
            }
        });

        //提供给h5 跳转原生免息券
        webView.registerHandler("goCouponPage", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->goCouponPage, data from web = " + data);
            }
            AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    ARouterUntil.getInstance(PagePath.ACTIVITY_COUPON_BAG).navigation();
                }
            });
        });

        //提供给h5，选择联系人
        webView.registerHandler("getContact", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getContact, data from web = " + data);
            }
            currentFunction = function;
            Map<String, String> map = new HashMap<>();
            mActivity.requestPermission((Consumer<Boolean>) aBoolean -> {
                if (aBoolean) {
                    try {
                        mActivity.startActivityForResult(new Intent(Intent.ACTION_PICK,
                                ContactsContract.Contacts.CONTENT_URI), PHONE_NUM_REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        map.put("permissionState", "0");
                        map.put("phoneNo", "");
                        function.onCallBack(JsonUtils.toJson(map));
                    }
                } else {
                    map.put("permissionState", "0");
                    map.put("phoneNo", "");
                    function.onCallBack(JsonUtils.toJson(map));
                }
            }, R.string.permission_read_contact, Manifest.permission.READ_CONTACTS);

        });

        //提供给h5，获取通话记录权限，无需回调给H5
        webView.registerHandler("requestCallLogPermission", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->requestCallLogPermission, data from web = " + data);
            }
            mActivity.requestPermission((Consumer<Boolean>) aBoolean -> {

            }, R.string.permission_read_call_log, Manifest.permission.READ_CALL_LOG);

        });


        //提供给h5，开启关闭防截屏
        webView.registerHandler("setScreenShot", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getContact, data from web = " + data);
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String noScreenShot = map.get("noScreenShot");
            if ("Y".equals(noScreenShot)) {
                SystemUtils.setWindowSecure(mActivity);
            } else {
                SystemUtils.clearWindowSecure(mActivity);
            }

        });

        //
        //提供给h5 deviceId
        webView.registerHandler("getYdunDeviceToken", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getYdunDeviceToken, data from web = " + data);
            }
            Map<String, String> map = new HashMap<>();
            //设置网易设备指纹数据
            WyDeviceIdUtils.getInstance().getWyDeviceIDTokenFromNative(AppApplication.CONTEXT, (token, code, msg) -> {
                if (!TextUtils.isEmpty(token)) {
                    map.put("ydunToken", token);
                }
                function.onCallBack(JsonUtils.toJson(map));
            });
        });

        //提供给h5 红包微信授权
        webView.registerHandler("getWeChatUserInfo", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getWeChatUserInfo, data from web = " + data);
            }
            if (WxUntil.isReady(mActivity)) {
                currentFunction = function;
                Bundle bundle = new Bundle();
                bundle.putString("fromRedBag", "Y");
                WxUntil.sendAuthForLogin(mActivity, bundle);
            } else {
                UiUtil.toast("请先安装微信应用");
            }
        });


        //提供给h5 跳转原生加载的会员页
        webView.registerHandler("goVipCouponPage", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->goVipCouponPage, data from web = " + data);
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String url = map.get("url");
            String couponValue = map.get("couponDisValue");
            if (CheckUtil.isEmpty(url)) {
                function.onCallBack("链接不能为空");
                return;
            }
            if (URLUtil.isNetworkUrl(url)) {
                currentFunction = function;
                HashMap<String, String> urlMap = new HashMap<>();
                urlMap.put("couponDisValue", couponValue);
                urlMap.put("borrowBannerHasJump", SpHp.getOther(SpKey.BORROW_BANNER_HAS_JUMP, "false"));
                Intent intent = new Intent();
                intent.setClass(mActivity, JsWebPopActivity.class);
                intent.putExtra("jumpKey", WebHelper.addUrlParam(url, urlMap));
                SpHp.saveSpOther(SpKey.BORROW_BANNER_HAS_JUMP, "true");
                mActivity.startActivityForResult(intent, START_VIP_COUPON);
            } else {
                WebHelper.startActivityForUrl(mActivity, url);
            }

        });

        webView.registerHandler("openExternalBrowser", (data, function) -> {
            Map<String, String> map = JsonUtils.getRequest(data);
            Map<String, String> callbackMap = new HashMap<>();
            String url = map.get("jumpUrl");
            if (CheckUtil.isEmpty(url)) {
                callbackMap.put("isSuccess","0");
                function.onCallBack(JsonUtils.toJson(callbackMap));
                return;
            }
            Intent intent= new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            mActivity.startActivity(intent);
            callbackMap.put("isSuccess","1");
            function.onCallBack(JsonUtils.toJson(callbackMap));
        });

        webView.registerHandler("getImageData", ((data, function) -> {
            currentFunction = function;
            Map<String, String> callbackMap = new HashMap<>();
            mActivity.requestPermissionEachCombined(((Consumer<Permission>) permission -> {
                if (permission.granted) {
                    PhotographUtils.startPhotoAlbum(mActivity, true);
                    return;
                } else if (!permission.shouldShowRequestPermissionRationale) {
                    goSystemSetting("存储");
                }
                callbackMap.put("permissionState", "0");
                callbackMap.put("errMsg", "没有存储权限");
                function.onCallBack(JsonUtils.toJson(callbackMap));

            }), R.string.permission_storage, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }));

        webView.registerHandler("setCalendarReminder", ((data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->setCalendarReminder, data from web = " + data);
            }
            Map<String, String> map = JsonUtils.getRequest(data);
            String title = map.get("title");
            String description = map.get("description");
            String startTime = map.get("startTime");
            String endTime = map.get("endTime");
            String previousDate = map.get("previousDate");

            Map<String, String> callbackMap = new HashMap<>();
            mActivity.requestPermissionEachCombined(((Consumer<Permission>) permission -> {
                if (permission.granted) {
                    boolean addCalendarEvent = CalendarUtils.addCalendarEvent(mActivity, title, description, startTime, endTime, previousDate);
                    callbackMap.put("addCalendarEvent", addCalendarEvent? "1" : "0");
                    callbackMap.put("permissionState", "1");
                } else if (!permission.shouldShowRequestPermissionRationale) {
                    goSystemSetting("日历");
                    callbackMap.put("permissionState", "0");
                    callbackMap.put("errMsg", "没有访问日历权限");
                }
                function.onCallBack(JsonUtils.toJson(callbackMap));

            }), R.string.permission_read_and_write_calender, Manifest.permission.READ_CALENDAR,Manifest.permission.WRITE_CALENDAR);
        }));

        webView.registerHandler("openCalendar", ((data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->openCalendar, data from web = " + data);
            }

            Map<String, String> callbackMap = new HashMap<>();
            mActivity.requestPermissionEachCombined(((Consumer<Permission>) permission -> {
                if (permission.granted) {
                    boolean openCalendar = CalendarUtils.openCalendar(mActivity);
                    callbackMap.put("openCalendar", openCalendar? "1" : "0");
                    callbackMap.put("permissionState", "1");
                } else if (!permission.shouldShowRequestPermissionRationale) {
                    goSystemSetting("日历");
                    callbackMap.put("permissionState", "0");
                    callbackMap.put("errMsg", "没有访问日历权限");
                }
                function.onCallBack(JsonUtils.toJson(callbackMap));

            }), R.string.permission_read_and_write_calender, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        }));

        //提供给h5 获取配置的参数
        webView.registerHandler("getHttpHeader", (data, function) -> {
            if (IS_DEBUG_WEB) {
                Logger.i("registerHandler->getHttpHeader, data from web = " + data);
            }
            if (CheckUtil.isEmpty(NetConfig.TD_HTTP_HEADERS)) {
                function.onCallBack("");
            } else {
                function.onCallBack(JsonUtils.toJson(NetConfig.TD_HTTP_HEADERS));
            }
        });

        //调用javascript注册处理程序
        webView.callHandler("functionInJs", "data from Java-Android", data -> {
            //Log.i(TAG, "reponse data from js " + data);
            if (IS_DEBUG_WEB) {
                Logger.i("reponse data from js " + data);
            }
        });
        if (!CheckUtil.isEmpty(webDoType)) {
            if ("lend".equals(webDoType)) {
                lend();
            } else if ("cash".equals(webDoType)) {
                cash();
            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == START_VIP_COUPON) {
                Map<String, String> map = new HashMap<>();
                map.put("vipCouponPageClosed", "Y");
                currentFunction.onCallBack(JsonUtils.toJson(map));
            } else if (requestCode == PhotographUtils.PHOTOALBUM_WEB_JS) {
                Map<String, String> map = new HashMap<>();
                map.put("permissionState", "1");
                try {
                    Uri uri = data.getData();
                    Bitmap result = ImageUtils.decodeUri(mActivity, uri, 720, 1280);
                    Bitmap compressResult = ImageUtils.compressByQuality(result, 30);
                    String imageData = ImageUtils.bitmapToBase64(compressResult);
                    map.put("imageData", imageData);
                    result.recycle();
                } catch (Exception e) {
                    map.put("imageData", "");
                    map.put("errMsg", e.getMessage());
                }
                currentFunction.onCallBack(JsonUtils.toJson(map));
            } else if (requestCode == PHONE_NUM_REQUEST_CODE) {
                Map<String, String> map = new HashMap<>();
                if (data != null) {
                    final List<String> userNumber = PerfectInfoHelper.getPhoneMailList(mActivity, data);
                    if (userNumber.isEmpty()) {
                        map.put("permissionState", "1");
                        map.put("phoneNo", "");
                        currentFunction.onCallBack(JsonUtils.toJson(map));
                        return;
                    }
                    if (userNumber.size() == 1) {
                        map.put("permissionState", "1");
                        map.put("phoneNo", userNumber.get(0));
                        currentFunction.onCallBack(JsonUtils.toJson(map));
                    } else if (userNumber.size() > 0) {
                        AlertDialog.Builder singleChoiceDialog = new AlertDialog.Builder(mActivity);
                        singleChoiceDialog.setTitle("请选择手机号码");
                        singleChoiceDialog.setSingleChoiceItems(userNumber.toArray(new String[userNumber.size()]), 0,
                                (dialog, which) -> {
                                    dialog.dismiss();
                                    String phone = userNumber.get(which);
                                    if (!TextUtils.isEmpty(phone)) {
                                        map.put("permissionState", "1");
                                        map.put("phoneNo", phone);
                                        currentFunction.onCallBack(JsonUtils.toJson(map));
                                    }
                                });
                        singleChoiceDialog.show();

                    }
                } else {
                    map.put("permissionState", "1");
                    map.put("phoneNo", "");
                    currentFunction.onCallBack(JsonUtils.toJson(map));
                }
            } else {
                byte[] portraitImg = data.getByteArrayExtra("portraitimg_bitmap");
                byte[] iDCardImg = data.getByteArrayExtra("idcardimg_bitmap");
                ocrMap.put("permissionState", "1");
                ocrMap.put("cardType", cardType);

                if (requestCode == CardBack) {
                    if (CheckUtil.isEmpty(iDCardImg)) {
                        ocrMap.put("errMsg", "身份证照片识别错误");
                    } else {
                        ocrMap.put("idCardBackData", Base64.encodeToString(iDCardImg, Base64.DEFAULT));
                    }
                    currentFunction.onCallBack(JsonUtils.toJson(ocrMap));
                } else if (requestCode == CardFront) {
                    if (CheckUtil.isEmpty(iDCardImg) || CheckUtil.isEmpty(portraitImg)) {
                        ocrMap.put("errMsg", "身份证照片识别错误");
                        currentFunction.onCallBack(JsonUtils.toJson(ocrMap));
                    } else {
                        ocrMap.put("idCardFrontData", Base64.encodeToString(iDCardImg, Base64.DEFAULT));
                        if ("0".equals(cardType)) {
                            getIdCardInfo(CardBack);
                        } else {
                            currentFunction.onCallBack(JsonUtils.toJson(ocrMap));
                        }

                    }
                }
            }
        }
    }

    /*
        //注册方法刷新H5的token
    //    public void refreshH5Token(String token) {
    //        //调用javascript注册处理程序
    //        webView.callHandler("refreshToken", token, data -> {
    //
    //        });
    //    }*/
    private void lend() {
        if (!AppApplication.isLogIn()) {
            AppApplication.setLoginCallback(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    lend();
                }
            });
            gotoLoginIn();
        } else {
            if (isAdvert) {
                gotoLockPage("lend");
            } else {
                isBorrow = false;
                UserLoanStatusHelper.lendOrBorrow(mActivity, mNetHelper, false);
            }
        }
    }

    /**
     * 个人中心 -- 红包--微信授权
     *
     * @param code
     */
    public void wxCodeInfoCallBack(String code) {
        if (currentFunction != null) {
            currentFunction.onCallBack(code);
        }
    }

    private void openAi() {
        if (!AppApplication.isLogIn()) {
            AppApplication.setLoginCallback(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    openAi();
                }
            });
            gotoLoginIn();
        } else {
            AIServer.showAiWebServer(mActivity, "我的");
        }
    }

    private void cash() {
        if (!AppApplication.isLogIn()) {
            AppApplication.setLoginCallback(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    cash();
                }
            });
            gotoLoginIn();
        } else {
            if (isAdvert) {
                gotoLockPage("cash");
            } else {
                isBorrow = true;
                UserLoanStatusHelper.requestUserLendStatus(mActivity, mNetHelper, true, (dialog, which) -> {
                    ARouterUntil.getInstance(PagePath.ACTIVITY_MAIN).navigation();
                    ActivityUntil.finishOthersActivityByPageKey(PagePath.ACTIVITY_MAIN);
                });
            }
        }
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
            return;
        }
        mActivity.showProgress(false);
        if (ApiUrl.POST_USER_LEND_STATUS.equals(url)) {
            UserLoanStatusHelper.doForLoanStatus(mActivity, isBorrow, (Map<?, ?>) t);
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        mActivity.onError(error, url);
    }

    /**
     * 跳转登陆页面
     */
    private void gotoLoginIn(String url, String webDoType) {
        CommomUtils.clearSp();
        TokenHelper.initTokenRefresh();
        Bundle argc = new Bundle();
        if (!CheckUtil.isEmpty(url)) {
            argc.putString("webUrl", url);
        }
        if (!CheckUtil.isEmpty(webDoType)) {
            argc.putString("webDoType", webDoType);
        }
        LoginSelectHelper.staticToGeneralLogin(argc);
    }

    /**
     * 跳转登陆页面
     */
    private void gotoLoginIn() {
        CommomUtils.clearSp();
        TokenHelper.initTokenRefresh();
        LoginSelectHelper.staticToGeneralLogin();
    }

    public void onDestroy() {
        if (couponToUseUtil != null) {
            couponToUseUtil.onDestroy();
        }
        if (mNetHelper != null) {
            mNetHelper.recoveryNetHelper();
        }
    }

    /**
     * 进入手势密码
     */
    private void gotoLockPage(String webDoType) {
        String webUrl = webView.getUrl();
        if (LoginSelectHelper.hasSetBiometric()) {
            //("进入指纹验证过程");
            ARouterUntil.getInstance(PagePath.ACTIVITY_VERIFY_BIOMETRIC).put("webUrl", webUrl).put("webDoType", webDoType).put("tag", "lock").navigation();
        } else if (LoginSelectHelper.hasSetGesture()) {
            //("进入手势验证过程");
            ARouterUntil.getInstance(PagePath.ACTIVITY_GESTURES_SECRET).put("webUrl", webUrl).put("webDoType", webDoType).put("tag", "lock").navigation();
        } else {
            //没有设置解锁功能，保持登录态直接跳转
            if (!CheckUtil.isEmpty(webDoType) && !CheckUtil.isEmpty(webUrl)) {
                Intent intent = new Intent(mActivity, JsWebBaseActivity.class);
                intent.putExtra("jumpKey", webUrl);
                intent.putExtra("webDoType", webDoType);
                mActivity.startActivity(intent);
            }
        }
        mActivity.finish();
    }

    //isGet 是否是获取数据
    private void getAndPostRiskData(boolean isGet, String doType, String eventId, String applySeq, String brType, String channelNo, CallBackFunction function) {
        Map<String, String> errMap = new HashMap<>();
        int realBrType;
        switch (brType) {
            case "1":
                realBrType = 1;
                break;
            case "2":
                realBrType = 2;
                break;
            case "3":
                realBrType = 3;
                break;
            case "4":
                realBrType = 4;
                break;
            default:
                realBrType = 0;
        }
        int realDoType;
        switch (doType) {
            case "1":
                realDoType = 1;
                break;
            case "2":
                realDoType = 2;
                break;
            case "-2":
                realDoType = -2;
                break;
            default:
                realDoType = 0;
        }
        try {
            if (isGet) {
                RiskKfaUtils.getRiskBean(mActivity, realDoType, realBrType, eventId, channelNo, obj -> riskBean = (RiskBean) obj);
            } else {
                if (realDoType == -2) {
                    if (riskBean == null || CheckUtil.isEmpty(riskBean.getEvent_id())) {
                        errMap.put("errMsg", "调用doType = -2之前未调用postRiskData且将isDelayPost设置为Y，" + "或者两次调用时间过短，App还未组装好数据");
                        function.onCallBack(JsonUtils.toJson(errMap));
                        return;
                    } else if (!eventId.equals(riskBean.getEvent_id())) {
                        errMap.put("errMsg", "调用doType = -2时需确保两次调用postRiskData方法时的eventId一致");
                        function.onCallBack(JsonUtils.toJson(errMap));
                        return;
                    }
                    if (bean1 != null && !CheckUtil.isEmpty(bean1.longitude)) {
                        riskBean.setGeoData(bean1);
                    } else {
                        GeoBean bean = new GeoBean();
                        getLocationData(bean);
                        riskBean.setGeoData(bean);
                    }
                    if (PermissionUtils.getRequestPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION) || (!CheckUtil.isEmpty(riskBean.getGeo_data()) && !CheckUtil.isEmpty(riskBean.getGeo_data().latitude))) {
                        String authorize = riskBean.getAuthorize_items();
                        if (!authorize.contains("gps")) {
                            if (authorize.endsWith(",")) {
                                authorize += "gps";
                            } else {
                                authorize += ",gps";
                            }
                            riskBean.setAuthorize_items(authorize);
                        }
                    }
                    RiskNetServer.startService(mActivity, riskBean, applySeq);
                } else {
                    RiskNetServer.startService(mActivity, eventId, applySeq, realDoType, realBrType, channelNo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errMap.put("errMsg", "采集或者上送数据出现异常： " + e.getMessage());
            function.onCallBack(JsonUtils.toJson(errMap));
            UiUtil.toastDeBug("Exception  postRiskData  " + e.getMessage());
        }

    }

    /**
     * 开启百度人脸
     */
    private void startBaiDuFaceCheck(Map<String, String> callbackMap) {
        mActivity.requestPermissionEachCombined(((Consumer<Permission>) permission -> {
            if (permission.granted) {
                BaiduFaceUtils faceUntil = new BaiduFaceUtils(mActivity, this);
                faceUntil.startFace();
                return;
            } else if (!permission.shouldShowRequestPermissionRationale) {
                goSystemSetting("相机");
            }
            callbackMap.put("permissionState", "0");
            callbackMap.put("errMsg", "没有相机权限");
            currentFunction.onCallBack(JsonUtils.toJson(callbackMap));
        }), R.string.permission_camera, Manifest.permission.CAMERA);
    }

    private void startOcrOrFaceCheck(boolean isOcr, Map<String, String> callbackMap) {
        mActivity.requestPermissionEachCombined(((Consumer<Permission>) permission -> {
            if (permission.granted) {
                startFaceOrOcr(isOcr);
                return;
            } else if (!permission.shouldShowRequestPermissionRationale) {
                goSystemSetting("相机");
            }
            callbackMap.put("permissionState", "0");
            callbackMap.put("errMsg", "没有相机权限");
            currentFunction.onCallBack(JsonUtils.toJson(callbackMap));
        }), R.string.permission_camera, Manifest.permission.CAMERA);

    }

    //ocr或者人脸是否初始化成功
    private void startFaceOrOcr(boolean isOcr) {
        if (!isOcr) {
            faceUntil = new FaceUntil(mActivity, this);
        }
        new FaceOcrHelper(mActivity, isOcr, authorize -> {
            if (authorize) {
                if (isOcr) {
                    if ("2".equals(cardType)) {
                        getIdCardInfo(CardBack);
                    } else {
                        getIdCardInfo(CardFront);
                    }
                } else {
                    if (faceUntil != null) {
                        faceUntil.startLiveDetect(currentBizToken);
                    }
                }

            } else {
                Map<String, String> errMap = new HashMap<>();
                errMap.put("errMsg", "SDK授权失败");
                currentFunction.onCallBack(JsonUtils.toJson(errMap));
            }
        });
    }

    //获取身份证信息
    private void getIdCardInfo(int side) {
        Intent intent = new Intent(mActivity, IDCardScanActivity.class);
        if (side == CardFront) {
            Configuration.setCardType(mActivity, 1);
        } else if (side == CardBack) {
            Configuration.setCardType(mActivity, 2);
        }
        intent.putExtra("side", side);
        mActivity.startActivityForResult(intent, side);
    }

    //百度人脸回掉
    @Override
    public void faceCallBack(boolean isSuccess, int errorCode, Object data, String methodName) {
        Map<String, Object> faceMap = new HashMap<>();
        faceMap.put("permissionState", "1");
        if (isSuccess) {
            faceMap.put("bizToken", currentBizToken);
            faceMap.put("faceData", data);
        } else {
            faceMap.put("errorCode", errorCode + "");
            faceMap.put("errMsg", data);
            //上传错误信息
            upErrorMethodInfo(errorCode + "", methodName, data.toString());
        }
        Logger.e("faceCallBack----isSuccess--" + isSuccess + "+---errorCode=" + errorCode + "--data==" + data);
        currentFunction.onCallBack(JsonUtils.toJson(faceMap));
    }

    /**
     * 上传百度错误信息
     *
     * @param code
     * @param errorMethod
     * @param msg
     */
    private void upErrorMethodInfo(String code, String errorMethod, String msg) {
        if (mNetHelper != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("code", code);
            map.put("errorMethod", errorMethod);
            map.put("msg", code + ":" + msg);
            mNetHelper.postService(ApiUrl.POST_APP_ACTION_LOG, map);
        }

    }

    //人脸获取到的数据
    @Override
    public void faceBack(boolean isSuccess, String errorOrToken, String data) {
        Map<String, String> faceMap = new HashMap<>();
        faceMap.put("permissionState", "1");
        if (isSuccess) {
            faceMap.put("bizToken", currentBizToken);
            faceMap.put("faceData", data);
        } else {
            faceMap.put("errMsg", errorOrToken);

        }
        currentFunction.onCallBack(JsonUtils.toJson(faceMap));
    }

    //跳转到设置页面
    private void goSystemSetting(String permission) {
        String msg = "\"够花\"无法获取" + permission + "访问权限，请您在系统权限设置中开启";
        String title = "开启" + permission + "访问权限";
        mActivity.showDialog(title, msg, "以后再说", "去设置", (dialog, which) -> {
            //如果无权限且 暂不授权，直接返回false
            dialog.dismiss();
            if (which == 2) {
                PermissionPageUtils.jumpPermissionPage(mActivity, BuildConfig.APPLICATION_ID);
            }
        }).setStandardStyle(2);
    }

    //组装位置信息
    private void getLocationData(GeoBean bean) {
        SpHelper baiduSp = SpHelper.getInstance();
        //判断更新时间,因为定位失败或者没有权限等情况也会走此回调，所以判断是否能用缓存里的定位信息
        String time = baiduSp.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_UPDATE);
        if (!CheckUtil.isEmpty(time)) {
            String json = baiduSp.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_JSON);
            Address address = JsonUtils.fromJson(json, Address.class);
            if (address != null) {
                bean.adcode = address.adcode;
                bean.address = address.address;
                bean.country = address.country;
                bean.district = address.district;
                bean.city_name = address.city;
                bean.province_name = address.province;
                bean.street = address.street;
                bean.street_number = address.streetNumber;
                bean.town = address.town;
                bean.longitude = baiduSp.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_LON);
                bean.latitude = baiduSp.readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_LAT);
                bean.provinceId = SpHelper.getInstance().readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_PROVINCECODE);//省代码
                bean.cityId = SpHelper.getInstance().readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_CITYCODE);//市代码
                bean.areaId = SpHelper.getInstance().readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_AREACODE);//区代码
            }
        }
    }


    ;

    /*  样例代码
    // 自定义JS和Native方法联动
        webView.setDefaultHandler((data, function) -> {
        Logger.i("DefaultHandler " + data);
        if (function != null) {
            function.onCallBack("Native get Success!");
        }
    });
    //注册处理程序，以便javascript可以调用它
        webView.registerHandler("submitFromWeb", new BridgeHandler() {

        @Override
        public void handler(String data, CallBackFunction function) {
            Logger.i("handler = submitFromWeb, data from web = " + data);
            function.onCallBack("submitFromWeb exe, response data 中文 from Java");
        }
    });
    //调用javascript注册处理程序
        webView.callHandler("functionInJs", "data from Java", new CallBackFunction() {

        @Override
        public void onCallBack(String data) {
            //Log.i(TAG, "reponse data from js " + data);
            Logger.i("reponse data from js " + data);
        }
    });
    */

//    public static String Javascript = "<script type=\"text/javascript\">function connectWebViewJavascriptBridge(e) {\n" +
//            "    window.WebViewJavascriptBridge ? e(WebViewJavascriptBridge) : document.addEventListener(\"WebViewJavascriptBridgeReady\", function() {\n" +
//            "        e(WebViewJavascriptBridge)\n" +
//            "    }, !1)\n" +
//            "}\n" +
//            "function setupWebViewJavascriptBridge(e) {\n" +
//            "    if (window.WebViewJavascriptBridge)\n" +
//            "        return e(WebViewJavascriptBridge);\n" +
//            "    if (window.WVJBCallbacks)\n" +
//            "        return window.WVJBCallbacks.push(e);\n" +
//            "    window.WVJBCallbacks = [e];\n" +
//            "    var i = document.createElement(\"iframe\");\n" +
//            "    i.style.display = \"none\",\n" +
//            "    i.src = \"https://__bridge_loaded__\",\n" +
//            "    document.documentElement.appendChild(i),\n" +
//            "    setTimeout(function() {\n" +
//            "        document.documentElement.removeChild(i)\n" +
//            "    }, 0)\n" +
//            "}\n" +
//            "function isAndroid() {\n" +
//            "    var e = navigator.userAgent;\n" +
//            "    return e.indexOf(\"Android\") > -1 || e.indexOf(\"Linux\") > -1\n" +
//            "}\n" +
//            "function isIos() {\n" +
//            "    var e = navigator.userAgent;\n" +
//            "    return e.indexOf(\"iPhone\") > -1 || e.indexOf(\"iOS\") > -1\n" +
//            "}\n" +
//            "isIos() ? setupWebViewJavascriptBridge(function(e) {\n" +
//            "    e.registerHandler(\"functionInJs\", function(e, i) {\n" +
//            "        i(\"Javascript Says Right back aka!\")\n" +
//            "    })\n" +
//            "}) : isAndroid() && connectWebViewJavascriptBridge(function(e) {\n" +
//            "    e.init(function(e, i) {\n" +
//            "        console.log(\"JS got a message\", e),\n" +
//            "        i({\n" +
//            "            \"Javascript Responds\": \"测试中文!\"\n" +
//            "        })\n" +
//            "    }),\n" +
//            "    e.registerHandler(\"functionInJs\", function(e, i) {\n" +
//            "        i(\"Javascript Says Right back aka!\")\n" +
//            "    })\n" +
//            "});</script>";
}
