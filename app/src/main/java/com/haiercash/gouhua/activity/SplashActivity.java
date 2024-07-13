package com.haiercash.gouhua.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.FileUtils;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.HmacSHA256Utils;
import com.app.haiercash.base.utils.handler.CycleHandlerCallback;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.advert.AdvertActivity;
import com.haiercash.gouhua.activity.comm.ResourceHelper;
import com.haiercash.gouhua.activity.gesture.GesturesPasswordActivity;
import com.haiercash.gouhua.activity.inenter.GuidePageActivity;
import com.haiercash.gouhua.activity.inenter.VerifyBiometricActivity;
import com.haiercash.gouhua.activity.login.LoginNetHelper;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.AppUntil;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.beans.ChaXunKeHuBianHao_get;
import com.haiercash.gouhua.beans.ResourceBean;
import com.haiercash.gouhua.beans.SelectByParams_Bean;
import com.haiercash.gouhua.beans.gesture.ValidateUserBean;
import com.haiercash.gouhua.beans.login.QueryAgreementListBean;
import com.haiercash.gouhua.beans.login.VersionInfo;
import com.haiercash.gouhua.beans.unity.MultComponentBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.service.DownIconWork;
import com.haiercash.gouhua.service.GhNetServer;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.uihelper.PrivacyProtocolPopupWindow;
import com.haiercash.gouhua.uihelper.VersionHelper;
import com.haiercash.gouhua.unity.FlattenJsonUtils;
import com.haiercash.gouhua.utils.AppLockUntil;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.LoginUserHelper;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.stericson.RootShell.RootShell;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Limige on 2017-06-20.
 * 启动页/闪屏页
 */

public class SplashActivity extends BaseActivity {
    private ImageView imgSplash;
    private VersionHelper mVersionHelper;

    /**
     * 要跳转的下一个activity
     */
    private Class className;

    private boolean isCheckVersion = false;
    private String jumpKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否从推送通知栏打开的
        XGPushClickedResult click = XGPushManager.onActivityStarted(this);
        if (click != null) {
            //从推送通知栏打开-Service打开Activity会重新执行Laucher流程
            //查看是不是全新打开的面板
            if (isTaskRoot()) {
                return;
            }
            //如果有面板存在则关闭当前的面板
            finish();
        }
        //闪屏页主动刷新token
        TokenHelper.getInstance().refreshTokenFromSplash();
        //保存用户数据
        FlattenJsonUtils.saveUserInfo();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        imgSplash = findViewById(R.id.img_splash);
    }

    /**
     * 同意隐私权限过后
     */
    public void afterPrivacyDialogOk() {
        //判断是否hook和root
        checkAPPHookAndRoot();
        //将onViewCreated()方法后面的操作放在同意隐私权限弹框后执行
        if (TextUtils.isEmpty(SpHelper.getInstance().readMsgFromSp(SpKey.TEMP, SpKey.TEMP_KEY))) {
            CommomUtils.clearSp(false);
            SpHelper.getInstance().saveMsgToSp(SpKey.TEMP, SpKey.TEMP_KEY, "Y");
        }
        if (xgPushHandler()) {
            return;
        }
        // 优化从桌面图标启动
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        runMyService();
        //启动页定义为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mVersionHelper = new VersionHelper(this, imgSplash, onVersionBackListener);
        AppLockUntil.resetTimes();
        //获取系统参数
        getSystemInfo();
        //启动页调用的接口
        afterAllowPrivacyDialogApi();
    }

    /**
     * 检查app是否被 hook&root
     */
    private void checkAPPHookAndRoot() {
        try {
            if (RootShell.isRootAvailable()) {
                UiUtil.toastLongTime("您的手机已root，请注意手机操作安全!");
            }
        } catch (Exception e) {
            CrashReport.postCatchedException(new Exception("Root检测异常:" + e.toString()));
        }
    }

    /**
     * 同意隐私权限后的api网络请求
     */
    private void afterAllowPrivacyDialogApi() {
        RiskInfoUtils.postGioData(this, "TURNON");
        GhNetServer.startGhNetServer(this, GhNetServer.AREA_CODE, null);
        RiskNetServer.startRiskServer1(this, "open", "", 0);
        //检测版本号
        mVersionHelper.startCheckVersionService();
    }

    public boolean xgPushHandler() {
        // 判断是否从推送通知栏打开的
        XGPushClickedResult click = XGPushManager.onActivityStarted(this);
        //Logger.file("信鸽推送消息SplashActivity start");
        //Logger.file(JsonUtils.toJson(click));
        //Logger.file("信鸽推送消息SplashActivity end");
        if (click != null) {
            //UiUtil.toast("SplashActivity xgPushHandler：" + click.getTitle());
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        imgSplash.setImageResource(R.drawable.background_splash);
    }

    /**
     * 根据错误类型显示不同的dialog
     */
    private void onVersionCheckedError(BasicResponse basicResponse) {
        if ("isFix".equals(basicResponse.getHead().retFlag)) {
            showBtn2Dialog(basicResponse.getHead().getRetMsg(), "我知道了", (dialog, which) -> finish()).setButtonTextColor(2, R.color.colorPrimary);

        } else {
            showBtn2Dialog(basicResponse.getHead().getRetMsg(), "重试", (dialog, which) -> {
                //检测版本号
                mVersionHelper.startCheckVersionService();
            });
        }
    }

    //禁止物理键返回
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private VersionHelper.OnVersionBackListener onVersionBackListener = new VersionHelper.OnVersionBackListener() {
        @Override
        public void onVersionBack(int status, Object response) {
            switch (status) {
                case VersionHelper.VERSION_CHECKED_ERROR:
                    onVersionCheckedError((BasicResponse) response);
                    break;
                case VersionHelper.VERSION_CHECKED_SUCC:
                    mVersionHelper.showNewUpdateDialog(SplashActivity.this, (VersionInfo) response);
                    break;
                case VersionHelper.VERSION_CHECKED_ERROR_NO_BLOCK:
                case VersionHelper.VERSION_SUCCEED_CANCLE:
                case VersionHelper.VERSION_SUCCEED_NOUPDATE:
                    //需要进引导页就进引导页
                    if (!"N".equals(SpHp.getOther(SpKey.OTHER_GUIDE_PAGE))) {
                        getAdvert(GuidePageActivity.class);
                        return;
                    }
                    ///查询客户信息,后期可以去掉启动页调用该接口
                    String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
                    String refreshToken = TokenHelper.getInstance().getCacheToken();
                    if (CheckUtil.isEmpty(userId) || CheckUtil.isEmpty(refreshToken)) {
                        initAdvertClass();
                    } else {
                        Map<String, String> map1 = new HashMap<>();
                        map1.put("userId", userId);
                        netHelper.getService(ApiUrl.url_kehubianhao, map1, ChaXunKeHuBianHao_get.class, true);
                    }
                    break;
                case VersionHelper.VERSION_SUCCEED_UPDATE:
                    showProgress(true, "正在下载新版本,请稍候");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 设置启动页之后可进入哪个页面
     */
    private void initAdvertClass() {
        if (!AppApplication.isLogIn()) {
            CommomUtils.clearSp();
            getAdvert(MainActivity.class);
            return;
        }
        if (LoginSelectHelper.hasSetBiometric()) {
            //生物识别
            getAdvert(VerifyBiometricActivity.class);
        } else if (LoginSelectHelper.hasSetGesture()) {
            //手势密码
            getAdvert(GesturesPasswordActivity.class);
        } else {//没有设置过快捷登录方式时保持登录态进入首页，调接口获取登录信息
            Map<String, String> map = new HashMap<>();
            map.put("userId", EncryptUtil.simpleEncrypt(AppApplication.userid));
            //必须放在map最后一行，是对整个map参数进行签名对
            map.put("sign", HmacSHA256Utils.buildNeedSignValue(map));
            netHelper.getService(ApiUrl.URL_GET_USER_INFO_BY_ID, map, ValidateUserBean.class, true);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !isCheckVersion) {
            isCheckVersion = true;
            showPrivacyDialogOrCheckVersion();
        }
    }

    private void showPrivacyDialogOrCheckVersion() {
        if (!AppUntil.isAllow() && !AppUntil.isTourist()) {
            getAgreementList();
        } else {
            afterPrivacyDialogOk();
        }
    }

    /**
     * 获取协议列表----登录+注册
     */
    private void getAgreementList() {
        showProgress(true);
        HashMap<String, String> map = new HashMap<>();
        map.put("sceneType", "register");
        map.put("showType", "privacy");
        netHelper.postService(ApiUrl.URL_GET_QUERY_AGREEMENT_LIST, map);
    }

    //展示隐私弹框
    private void showPrivacyDialog(List<QueryAgreementListBean> agreementListBeanList) {
        PrivacyProtocolPopupWindow popupWindow = new PrivacyProtocolPopupWindow(this, agreementListBeanList);
        popupWindow.showAtLocation(imgSplash, (view, flagTag, obj) -> {
            if (PrivacyProtocolPopupWindow.BUTTON_AGREE_FLAG == flagTag) {
                SpHp.saveSpOther(SpKey.OTHER_PRIVACY, "Y");
                SpHp.saveSpOther(SpKey.OTHER_TOURIST_MODE, "N");
                afterPrivacyDialogOk();
                AppUntil.initAPP(getApplication(), null);
            } else if (PrivacyProtocolPopupWindow.BUTTON_DISAGREE_FLAG == flagTag) {
                SpHp.saveSpOther(SpKey.OTHER_TOURIST_MODE, "Y");
                afterPrivacyDialogOk();
            }
        });
    }

    private String mAdName, mCid, mGroupId;

    @Override
    public void onSuccess(Object response, String flag) {
        if (ApiUrl.URL_GET_USER_INFO_BY_ID.equals(flag)) {
            LoginUserHelper.saveLoginInfo((ValidateUserBean) response);
            getAdvert(MainActivity.class);
        } else if (ApiUrl.url_kehubianhao.equals(flag)) {
            ChaXunKeHuBianHao_get getCustInfoBeanRtn = (ChaXunKeHuBianHao_get) response;
            SpHp.saveUser(SpKey.USER_CUSTNAME, getCustInfoBeanRtn.getCustName());//客户姓名
            SpHp.saveUser(SpKey.USER_CUSTNO, getCustInfoBeanRtn.getCustNo());//客户编号
            SpHp.saveUser(SpKey.USER_CERTNO, getCustInfoBeanRtn.getCertNo());//证件号
            SpHp.saveUser(SpKey.USER_MOBILE, getCustInfoBeanRtn.getMobile());//实名认证手机号
            Logger.e("实名认证：已实名" + getCustInfoBeanRtn.getCustNo());
            initAdvertClass();
        } else if (ApiUrl.POST_QUERY_RESOURCE_BY_PAGE.equals(flag)) {
            if (response != null) {
                ResourceBean resourceBean = (ResourceBean) response;
                List<ResourceBean.ContentBean> list = resourceBean.getContents();
                //是否配置 开屏广告
                if (!CheckUtil.isEmpty(list)) {
                    mAdName = resourceBean.getResourceName();
                    mCid = resourceBean.getCid();
                    mGroupId = resourceBean.getGroupId();
                    String picUrl = list.get(0).getPicUrl();
                    jumpKey = list.get(0).getH5Url();
                    UiUtil.toastDeBug("图片链接：\n" + picUrl);
                    startDownLoadImage(picUrl);
                    return;
                }
            }
            startActivity();
        } else if (ApiUrl.url_selectByParams.equals(flag)) {
            List<SelectByParams_Bean> list = JsonUtils.fromJsonArray(response, SelectByParams_Bean.class);
            if (list.size() == 0) {
                return;
            }
            for (SelectByParams_Bean beanRtn : list) {
                String paramValue = beanRtn.getParamValue();
                String paramCode = beanRtn.getParamCode();
                if (!CheckUtil.isEmpty(paramValue)) {
                    //信用卡开关
                    if ("creditCardSwitch".equals(paramCode)) {
                        SpHp.saveSpOther(SpKey.CREDIT_CARD_SWITCH, paramValue);
                    } else if ("FeedbackSwitch".equals(paramCode)) {
                        SpHp.saveSpOther(SpKey.FEEDBACK_SWITCH, paramValue);
                    } else if ("BRSwitch".equals(paramCode)) {
                        SpHp.saveSpOther(SpKey.OTHER_BR_SWITCH, paramValue);
                    } else if ("LogoutSwitch".equals(paramCode)) {
                        SpHp.saveSpOther(SpKey.OTHER_CANCELLATION_SWITCH, paramValue);
                    } else if ("BillBearSwitch".equals(paramCode)) {
                        SpHp.saveSpOther(SpKey.OTHER_BILL_BEAR_SWITCH, paramValue);
                    } else if ("dunSliderSwitch".equals(paramCode)) { //是否开启网易滑块验证
                        SpHp.saveSpOther(SpKey.CONFIGURE_SWITCH_WY_SLIDER, paramValue);
                    } else if ("personalizedRecommendSwitch".equals(paramCode)) {
                        SpHp.saveSpOther(SpKey.CONFIGURE_SWITCH_PERSONAL_RECOMMEND, paramValue);
                    } else if ("MIXDEV_CREDIT_SWITCH".equals(paramCode)) {
                        AppApplication.enableCredit = "Y".equals(paramValue) || "y".equals(paramValue);
                    } else if ("MIXDEV_LOAN_SWITCH".equals(paramCode)) {
                        AppApplication.enableLoan = "Y".equals(paramValue) || "y".equals(paramValue);
                    } else if ("MIXDEV_REPAY_SWITCH".equals(paramCode)) {
                        //AppApplication.enableRepay = "Y".equals(paramValue) || "y".equals(paramValue);
                    } else if ("redBagUrl".equals(paramCode)) {
                        SpHp.saveSpOther(SpKey.RED_BAG_URL, paramValue);
                    } else if ("redBagSwtich".equals(paramCode)) {
                        SpHp.saveSpOther(SpKey.RED_BAG_SWTICH, paramValue);
                    }
                }
            }
        } else if (ApiUrl.URL_GET_QUERY_AGREEMENT_LIST.equals(flag)) {
            showProgress(false);
            if (response != null) {
                List<QueryAgreementListBean> agreementListBeanList = JsonUtils.fromJsonArray(response, QueryAgreementListBean.class);
                SpHelper.getInstance().saveMsgToSp(SpKey.LATEST_AGREEMENT, SpKey.LATEST_AGREEMENT_LIST, JsonUtils.toJson(agreementListBeanList));
                showPrivacyDialog(agreementListBeanList);
            } else {
                showPrivacyDialog(LoginNetHelper.getDefaultAgreementList());
            }
        }
    }

    /**
     * 如果存在该url的文件则直接跳转至广告页，否则下载广告图片
     */
    private void startDownLoadImage(String url) {
        String path = FileUtils.getExternalFilesDir() + FileUtils.PATH_ADVERT;
        FileUtils.downLoadFile(cycleHandler, path, ".jpg", url);
        if (cycleHandler != null) {
            //等待3s的下载时间
            cycleHandler.sendEmptyMessageDelayed(0, 3 * 1000);
        }
    }


    private void gotoAdvert(String filePath) {
        AdvertActivity.getAdvertActivity(this, filePath, className, mAdName, mCid, mGroupId, jumpKey);
    }

    /**
     * 跳转至制定页面
     */
    private void startActivity() {
        //新用户引导
        startActivity(new Intent(this, className));
        finish();
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.url_kehubianhao.equals(url)) {
            initAdvertClass();
        } else if (ApiUrl.URL_GET_USER_INFO_BY_ID.equals(url)) {
            getAdvert(MainActivity.class);
        } else if (ApiUrl.POST_QUERY_RESOURCE_BY_PAGE.equals(url)) {
            startActivity();
        } else if (ApiUrl.URL_GET_QUERY_AGREEMENT_LIST.equals(url)) {
            showProgress(false);
            showPrivacyDialog(LoginNetHelper.getDefaultAgreementList());
        }
    }

    @Override
    protected void onDestroy() {
        if (mVersionHelper != null) {
            mVersionHelper.clearnHelper();
            mVersionHelper = null;
        }
        if (cycleHandler != null) {
            cycleHandler.removeCallbacksAndMessages(null);
            cycleHandler = null;
        }
        super.onDestroy();
    }

    /**
     * 获取广告
     */
    private void getAdvert(Class className) {
        this.className = className;
        ResourceHelper.requestOpenResource(netHelper);
    }

    /**
     * 3秒倒计时仍未成功则跳过
     */
    private Handler cycleHandler = new Handler(new CycleHandlerCallback(this) {
        @Override
        public void dispatchMessage(Message msg) {
            if (cycleHandler != null) {
                cycleHandler.removeCallbacksAndMessages(null);
            }
            if (msg.what == FileUtils.DOWN_LOAD_SUCC) {
                //跳转广告页
                gotoAdvert((String) msg.obj);
            } else {
                //图片下载失败或者3秒下载时间到
                UiUtil.toastDeBug("图片加载失败");
                startActivity();
            }
        }
    });

    /**
     * 获取系统参数
     */
    public void getSystemInfo() {
        SpHp.deleteOther(SpKey.CREDIT_CARD_SWITCH);
        Map<String, String> map = new HashMap<>();
        map.put("sysTyp", "app_gh");
        netHelper.getService(ApiUrl.url_selectByParams, map);
    }

    public void runMyService() {
        Constraints myConstraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest httpwork = new OneTimeWorkRequest.Builder(DownIconWork.class).setConstraints(myConstraints).build();
        WorkManager.getInstance().enqueue(httpwork);
    }
}
