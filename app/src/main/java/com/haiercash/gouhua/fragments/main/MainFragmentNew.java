package com.haiercash.gouhua.fragments.main;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.ScanQrCodeActivity;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.activity.edu.EduProgressHelper;
import com.haiercash.gouhua.activity.edu.NameAuthIdCardActivity;
import com.haiercash.gouhua.adaptor.bean.LoanMarketBean;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.adaptor.bean.ScenePopupBean;
import com.haiercash.gouhua.adaptor.homepage.HomepageConfigAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.AppUntil;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.base.ScenePopupDialog;
import com.haiercash.gouhua.beans.SmartH5Bean;
import com.haiercash.gouhua.beans.homepage.Configs;
import com.haiercash.gouhua.beans.homepage.Credit;
import com.haiercash.gouhua.beans.homepage.CustomerInfo;
import com.haiercash.gouhua.beans.homepage.FloatingWindow;
import com.haiercash.gouhua.beans.homepage.HomeBubbleBean;
import com.haiercash.gouhua.beans.homepage.HomeConfig;
import com.haiercash.gouhua.beans.homepage.HomeQuotaBean;
import com.haiercash.gouhua.beans.msg.UnReadMessageCount;
import com.haiercash.gouhua.beans.unity.ComponentBean;
import com.haiercash.gouhua.beans.unity.MultComponentBean;
import com.haiercash.gouhua.databinding.FragmentMainNewBinding;
import com.haiercash.gouhua.homepageview.HomepageQuotaCardNew;
import com.haiercash.gouhua.hybrid.H5ConfigHelper;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.BillBearLoginPop;
import com.haiercash.gouhua.unity.FlattenJsonUtils;
import com.haiercash.gouhua.unity.HRCommonAdapter;
import com.haiercash.gouhua.utils.HomePersistenceUtil;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：4.0<br/>
 * 创建日期：2019/8/5<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class MainFragmentNew extends BaseFragment {

    private HomepageQuotaCardNew homepageQuotaCardNew;
    private Credit credit;
    private int dyTop; //滑动距离
    private FloatingWindow floatingWindow;
    private HomeQuotaBean homeQuotaBean;
    private ObjectAnimator objectAnimator;
    private boolean isScroll; //是否在滑动
    private boolean loginStatusChange; //登录态发生变化也得刷新数据
    private String cardBubbleData = "";//气泡数据
    private String lastUserId = "000000";//上次登录的userId

    private FragmentMainNewBinding getBinding() {
        return (FragmentMainNewBinding) _binding;
    }

    //    private PointView pointView;
    String status; //用户额度状态，对应A1/A2.....

    private BillBearLoginPop bearLoginPop;
    private List<Configs> homeConfigs = new ArrayList<>();
    //    private HomepageConfigAdapter adapter;
    private LinearLayoutManager manager;
    private static String HOMEM0DELNO = "homeGH";
    private HRCommonAdapter hrCommonAdapter;
    private List<ComponentBean> mList = new ArrayList<>();
    private MultComponentBean multComponentBean;

    @Override
    protected FragmentMainNewBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentMainNewBinding.inflate(inflater, container, false);
    }

    @RequiresApi(23)
    @Override
    protected void initEventAndData() {
        setStatusBarTextColor(true);
        intRxBus();
        setonClickByViewId(R.id.ivMessage, R.id.llUpAmount, R.id.rlVipVideo, R.id.ivVipClose, R.id.ivScan, R.id.ivFloat, R.id.tv_tourist);
        //初始化部分控件隐藏
        //setonClickByViewId(R.id.ivMessage, R.id.llEmptyView, R.id.llUpAmount, R.id.rlVipVideo, R.id.ivVipClose, R.id.ivScan);
        getBinding().llUpAmount.setVisibility(View.GONE);
        getBinding().rlVipVideo.setVisibility("Y".equals(SpHp.getOther(SpKey.OTHER_BILL_BEAR_SWITCH)) ? View.VISIBLE : View.GONE);

        if (AppApplication.isLogIn()) {
            //重置:用户当前的状态
            SpHp.saveSpLogin(SpKey.LOGIN_STATUS, String.valueOf(EduProgressHelper.NORMAL_PROGRESS));
        }

        initHomeList();
        loadHomeData();
        //获取未读消息数量
        getUnReadMessageNum();
    }

    //接收广播
    private void intRxBus() {
        RxBus.getInstance().addSubscription(this, RxBus.getInstance().register(ActionEvent.class, (actionEvent -> {
            if (actionEvent.getActionType() == ActionEvent.MainRefreshHomePage) {
                loadHomeData();
            } else if (actionEvent.getActionType() == ActionEvent.MAINFRAGMENT_REFRESH_CREDIT) {
                loadCreditInfo();
            }
        })));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initHomeList() {
        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        getBinding().rvHomepage.setLayoutManager(manager);
//        adapter = new HomepageConfigAdapter(mActivity, homeConfigs, getPageCode());
        hrCommonAdapter = new HRCommonAdapter(getActivity(), mList);
//        View view = getLayoutInflater().inflate(R.layout.item_homepage_footer, null);
//        adapter.addFooterView(view);
        getBinding().rvHomepage.setAdapter(hrCommonAdapter);
        getBinding().rvHomepage.setNestedScrollingEnabled(true);
        /*if (!AppApplication.isLogIn()){
            View view1 = new View(mActivity);
            RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams)getBinding().rvHomepage.getLayoutParams();
            lp2.setMargins(0, UiUtil.dip2px(mActivity, -180), 0,0);
            getBinding().rvHomepage.setLayoutParams(lp2);
        }
*/
        homepageQuotaCardNew = new HomepageQuotaCardNew(mActivity);
        homepageQuotaCardNew.startRefreshState();
        homepageQuotaCardNew.setPageCode(getPageCode());
        hrCommonAdapter.addHeaderView(homepageQuotaCardNew);
        getBinding().rvHomepage.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                isScroll = false;
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (AppApplication.isLogIn() && getBinding().ivFloat.getVisibility() == View.VISIBLE) {
                        if (homepageQuotaCardNew.isCardInVisible() || manager.findFirstVisibleItemPosition() > 0) {
                            floatEnterAnimation();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                floatExitAnimation();
                isScroll = true;
                dyTop += dy;
                int firstCompletelyVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                if (dyTop <= 0 || firstCompletelyVisibleItemPosition == 0) {//未滑动
                    dyTop = 0;
                    getBinding().viewHeadBack.setAlpha(0);
                    setHeadView();
                } else if (dyTop <= UiUtil.dip2px(mActivity, 90)) {
                    float alpha = (float) dyTop / UiUtil.dip2px(mActivity, 90);
                    getBinding().viewHeadBack.setAlpha(alpha);
                    setHeadView();

                } else {
                    getBinding().viewHeadBack.setAlpha(1);
                    setHeadView();
                }
            }
        });

        getBinding().srlRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadHomeData(false);
                //模版数据
                getPersonModelInfo();
                //用户数据
                getUserInfo(false);
            }
        });
        //用户数据
        getUserInfo(true);
        getPersonModelInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
            Logger.d("MainBaseFragment  : onResume  :  loadHomeData");
            loadHomeData();
            setHeadView();
            if (!AppApplication.isLogIn()) {
                getBinding().ivFloat.setVisibility(View.GONE);
                MainEduBorrowUntil.status = "";
                MainEduBorrowUntil.oldStatus = "";
            }
            //获取未读消息数量
            getUnReadMessageNum();
        }
        //第一次安装不请求mot弹窗
        if (AppUntil.checkIsFirstInstall()) {
            if (!AppUntil.isTourist()) {
                LoginSelectHelper.staticToGeneralLogin();
            }
        } else {
            requestHomePopupInfo();
        }
        initMessageCenter();

        //获取smartH5数据
        getUserInfo(false);
    }

    /**
     * 获取用户数据
     */
    private void getUserInfo(boolean showPrograss) {
        if (netHelper != null) {
            showProgress(showPrograss);
            Map<String, String> map = new HashMap<>();
            map.put("modelNo", HOMEM0DELNO);
            netHelper.postService(ApiUrl.POST_URL_PSERSON_CENTER_NEW2, map);
        }
    }

    //获取模版数据
    private void getPersonModelInfo() {
        if (netHelper != null) {
            Map<String, String> map = new HashMap<>();
            map.put("modelNo", HOMEM0DELNO);
            netHelper.postService(ApiUrl.POST_MODEL_DATA, map, MultComponentBean.class);
        }
    }

    /**
     * 获取首页营销弹窗
     */
    public void requestHomePopupInfo() {
        if (controlDialogUtil != null && lastUserId != AppApplication.userid & !AppApplication.isLogIn() && !AppUntil.isTourist()) {
            Observable<FragmentEvent> observable = lifecycle().compose(this.bindUntilEvent(FragmentEvent.DESTROY));
            controlDialogUtil.setTimerSchedule(observable);
            controlDialogUtil.setUmParam(getPageName(), getPageCode());
            controlDialogUtil.getPopupInfo("home", "enter", "fpage");
        }
        lastUserId = AppApplication.userid;
    }

    private void setHeadView() {
        if (!AppApplication.isLogIn() && dyTop <= 0) {
            getBinding().ivHeadImg.setImageResource(R.drawable.icon_logo_white);
            getBinding().tvLogoInfo.setTextColor(getResources().getColor(R.color.color_C3D6FF));
            getBinding().ivScan.setImageResource(R.drawable.icon_scan_white);
            getBinding().ivMessage.setImageResource(R.drawable.icon_message_white);
        } else {
            getBinding().ivHeadImg.setImageResource(R.drawable.icon_logo_black);
            getBinding().tvLogoInfo.setTextColor(Color.BLACK);
            getBinding().ivScan.setImageResource(R.drawable.icon_scan_black);
            getBinding().ivMessage.setImageResource(R.drawable.img_message);
        }
    }

    //同时请求两个接口
    private void loadHomeData() {
        loadHomeData(true);

    }

    /**
     * 未读消息数量
     */
    public void getUnReadMessageNum() {
        if (AppApplication.isLogIn()) {
            Map<String, String> map = new HashMap<>();
            netHelper.postService(ApiUrl.GET_UNREAD_MESSAGE_COUNT, map, UnReadMessageCount.class);
        } else {
            getBinding().layoutNum.setVisibility(View.GONE);
        }
    }


    //同时请求两个接口
    private void loadHomeData(boolean isLoading) {
        loadCreditInfo(isLoading);
        loadConfigInfo();
        resetData();

    }


    //获取额度卡片数据
    private void loadCreditInfo() {
        loadCreditInfo(true);
    }

    //获取额度卡片数据
    private void loadCreditInfo(boolean isLoading) {
        if (homepageQuotaCardNew != null && isLoading) {
            homepageQuotaCardNew.startRefreshState();
        }
        if (AppApplication.isLogIn()) {
            Map<String, String> map = new HashMap();
            map.put("deviceId", SystemUtils.getDeviceID(getActivity()));
            map.put("h5token", TokenHelper.getInstance().getH5Token());
            map.put("processId", TokenHelper.getInstance().getH5ProcessId());
            if (AppUntil.isTourist()) {
                map.put("mode", "browse");
            }

            netHelper.postService(ApiUrl.POST_HOME_CREDIT_INFO, map, HomeQuotaBean.class);
        } else {
            MainEduBorrowUntil.INSTANCE(mActivity).postHomePageButtonEvent("QuotaElement_Exposure", "未登录-立即登录");
        }
    }

    //获取首页配置信息
    private void loadConfigInfo() {
        Map<String, String> param = new HashMap<>();
        param.put("effectCarrier", "GH");//生效载体
        param.put("touchPage", "home");//触发页面
        String processId = SpHp.getLogin(SpKey.LOGIN_PROCESS_ID);
        if (!TextUtils.isEmpty(processId)) {
            param.put("processId", processId);
        }
//        netHelper.postService(ApiUrl.POST_HOME_CONFIG_INFO, param, HomeConfig.class);
    }

    private void jumpToMessage() {
        SpHp.saveSpLogin(SpKey.NOTICE_POINT_OPERATE, "0");
        ARouterUntil.getContainerInstance(PagePath.FRAGMENT_MESSAGE).navigation();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivMessage) {
            Map<String, Object> map = new HashMap();
            map.put("is_read", getBinding().noticeNum != null && getBinding().noticeNum.getVisibility() == View.VISIBLE ? "是" : "否");
            UMengUtil.commonClickEvent("MessageCenter_Click", "消息中心", "够花-首页", map, getPageCode());
            AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    jumpToMessage();
                }
            });
        } else if (view.getId() == R.id.llUpAmount) {
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_PROMOTE_LIMIT).navigation();
        } else if (view.getId() == R.id.rlVipVideo) {
            if (AppUntil.touristIntercept(getBinding().tvTourist, mActivity)) {
                return;
            }
            AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    clickRvVipVideoAction();
                }
            });
        } else if (view.getId() == R.id.ivVipClose) {
            getBinding().rlVipVideo.setVisibility(View.GONE);
        } else if (view.getId() == R.id.ivScan) {  //扫码
            UMengUtil.commonClickEvent("ScanCode_Click", "扫码", "够花-首页", getPageCode());
            AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    jumpToScanQrCode();
                }
            });
        } else if (view.getId() == R.id.ivFloat) {
            MainEduBorrowUntil.INSTANCE(mActivity).startEvent(credit, getBinding().ivFloat);
            HashMap<String, Object> map = new HashMap<>();
            map.put("page_name_cn", "够花-首页");
            map.put("banner_name", floatingWindow.getText());
            UMengUtil.onEventObject("Gh_Home_EduFloating_Click", map, getPageCode());
        } else if (view.getId() == R.id.tv_tourist) {
            AppUntil.touristIntercept(getBinding().tvTourist, mActivity);
        }
    }

    private void clickRvVipVideoAction() {
        if (bearLoginPop == null) {
            bearLoginPop = new BillBearLoginPop(mActivity, null);
        }
        bearLoginPop.checkLoginStatus(getBinding().rlVipVideo);
    }

    private void jumpToScanQrCode() {
        //跳转扫码
        startActivity(new Intent(mActivity, ScanQrCodeActivity.class));
    }

    /**
     * 初始化界面数据
     */
    private void initHomeView() {
        //更新完首页UI后调用首页弹窗整个逻辑
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).queryNeedResignAgreements((view, flagTag, obj) -> {
                if (!mActivity.isShowingDialog()) {
                    if (homeQuotaBean != null && homeQuotaBean.getSceneOrderPopup() != null) {
                        ScenePopupBean sceneOrderPopup = homeQuotaBean.getSceneOrderPopup();
                        ScenePopupDialog popupDialog = ScenePopupDialog.getInstance(mActivity);
                        popupDialog.showScenePopup(sceneOrderPopup);
                    } else {
                        controlDialogUtil.setUmParam(getPageName(), getPageCode());
                        controlDialogUtil.setCountDownTimerStatus(AppApplication.isLogIn());
                        controlDialogUtil.checkDialog();
                    }
                }
            });
        }
    }

    private String getPageName() {
        return "够花-首页";
    }

    //保存部分额度相关信息
    private void dealCredit(Credit credit) {
        if (CheckUtil.isEmpty(credit)) {
            return;
        }
        SpHp.saveUser(SpKey.USER_EDU_SPE_SEQ, credit.getSpeSeq());
        MainHelper.saveMoneyState(credit.getAvailLimit(), credit.getTotalLimit());
        //贷款品种
        MainEduBorrowUntil.INSTANCE(mActivity).setMinAmtAndTypeLevelTwo(credit.getTypCde(), credit.getMinAmt(), credit.getTypLvlCde());
        MainEduBorrowUntil.status = credit.getStatus();
        MainEduBorrowUntil.oldStatus = credit.getOldStatus();
        MainEduBorrowUntil.INSTANCE(mActivity).postHomePageButtonEvent("QuotaElement_Exposure", CheckUtil.isEmpty(credit.getMain()) ? "" : credit.getMain().getBtnText(), true);
        status = credit.getUserStatus();
    }

    //处理实名信息
    private void dealRealNameInfo(CustomerInfo bean) {
        if (CheckUtil.isEmpty(bean)) {
            return;
        }
        SpHp.saveUser(SpKey.USER_CUSTNAME, bean.getCustName());//客户姓名
        SpHp.saveUser(SpKey.USER_CUSTNO, bean.getCustNo());//客户编号
        SpHp.saveUser(SpKey.USER_CERTNO, bean.getCertNo());//证件号
        SpHp.saveUser(SpKey.USER_MOBILE, bean.getMobile());//实名认证手机号
    }

    //处理浮窗数据
    private void dealFloatData(FloatingWindow bean) {
        this.floatingWindow = bean;
        String type = bean.getType();
        String jsonPath = "";
        switch (type) {
            case "type30":
                jsonPath = "home_30.json";
                break;
            case "type60":
                jsonPath = "home_60.json";
                break;
            case "type90":
                jsonPath = "home_90.json";
                break;
            case "typeLoan":
                jsonPath = "home_loan.json";
                break;
            default:
                return;
        }
        getBinding().ivFloat.setVisibility(View.VISIBLE);
        getBinding().ivFloat.setImageAssetsFolder("images/");
        getBinding().ivFloat.setAnimation(jsonPath);
        getBinding().ivFloat.setRepeatCount(Integer.MAX_VALUE);
        getBinding().ivFloat.playAnimation();
    }

    ;

    //浮窗进入动画
    private void floatEnterAnimation() {
        if (getBinding().ivFloat != null && getBinding().ivFloat.getVisibility() == View.VISIBLE) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(getBinding().ivFloat, "translationX", UiUtil.dip2px(mActivity, -75));
            objectAnimator.setDuration(300);
            objectAnimator.start();
            HashMap<String, Object> map = new HashMap<>();
            map.put("page_name_cn", "够花-首页");
            map.put("banner_name", floatingWindow.getText());
            UMengUtil.onEventObject("Gh_Home_EduFloating_Exposure", map, getPageCode());
        }

    }

    //浮窗退出动画
    private void floatExitAnimation() {
        if (getBinding().ivFloat != null && getBinding().ivFloat.getVisibility() == View.VISIBLE) {
            if (!isScroll) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(getBinding().ivFloat, "translationX", 0f);
                objectAnimator.setDuration(300);
                objectAnimator.start();
            }
        }
    }


    @Override
    public void onSuccess(Object response, final String flag) {
        showProgress(false);
        finishRefresh();
        if (ApiUrl.POST_HOME_CREDIT_INFO.equals(flag)) {
            homeQuotaBean = (HomeQuotaBean) response;
            credit = homeQuotaBean.getCredit();
            changeLoanMarket();

            homepageQuotaCardNew.setData(homeQuotaBean, mActivity);
            homepageQuotaCardNew.setBubbleData(cardBubbleData);

            showCreditLimitPromotion("ON".equals(homeQuotaBean.getCreditIncreaseSwitch()));
            H5ConfigHelper helper = new H5ConfigHelper(credit.getOldStatus(), credit.getApplSeq(), null);
            helper.getH5LinkData();

            Credit creditBean = homeQuotaBean.getCredit();
            dealCredit(creditBean);

            CustomerInfo customerInfo = homeQuotaBean.getCustomerInfo();
            dealRealNameInfo(customerInfo);

            if (homeQuotaBean.getShowFloatingWindow() == 1
                    && !CheckUtil.isEmpty(homeQuotaBean.getFloatingWindow())
                    && !CheckUtil.isEmpty(homeQuotaBean.getFloatingWindow().getType())) {
                dealFloatData(homeQuotaBean.getFloatingWindow());
            } else {
                getBinding().ivFloat.setVisibility(View.GONE);
            }

            initHomeView();


        } else if (ApiUrl.POST_HOME_CONFIG_INFO.equals(flag)) {

            HomeConfig homeConfig = (HomeConfig) response;
            HomeBubbleBean homeBubbleBean = homeConfig.getBubbleInfoMap();
            if (homeBubbleBean != null) {
                cardBubbleData = homeBubbleBean.getQuotaCardControl();
                if (homepageQuotaCardNew != null) {
                    homepageQuotaCardNew.setBubbleData(cardBubbleData);
                }
            }
            if (response != null && homeConfigs != null &&
                    !CheckUtil.isEmpty(homeConfig.getConfigs())) {
                String newData = JsonUtils.toJson(homeConfig.getConfigs());
                if (loginStatusChange == AppApplication.isLogIn() && !CheckUtil.isEmpty(newData) && newData.equals(JsonUtils.toJson(homeConfigs))) {
                    Logger.e("首页接口加载的数据未发生变化，不更新UI");
                } else if (!CheckUtil.isEmpty(newData)) {
                    loginStatusChange = AppApplication.isLogIn();
                    homeConfigs = homeConfig.getConfigs();
//                    if (adapter != null) {
//                        adapter.setNewData(homeConfigs);
//                    }
                }
            } else if (CheckUtil.isEmpty(homeConfig) && CheckUtil.isEmpty(homeConfigs)) {
                onError(new BasicResponse("-1", NetConfig.DATA_PARSER_ERROR), ApiUrl.POST_HOME_CONFIG_INFO);
            }
            finishRefresh();


        } else if (ApiUrl.URL_CREDIT_PROMOTION.equals(flag)) {
            showCreditLimitPromotion(true);
        } else if (ApiUrl.GET_UNREAD_MESSAGE_COUNT.equals(flag)) {
            UnReadMessageCount data = (UnReadMessageCount) response;
            if (data != null) {
                if (data.getTotal() <= 0) {
                    getBinding().layoutNum.setVisibility(View.GONE);
                } else if (data.getTotal() < 10) {
                    getBinding().layoutNum.setVisibility(View.VISIBLE);
                    getBinding().noticeNum.setText(data.getTotal() + "");
                    getBinding().noticeNum.setBackgroundResource(R.drawable.shape_circle12_ff5151);
                    getBinding().layoutNum.setBackgroundResource(R.drawable.bg_circle12_white);
                } else if (data.getTotal() >= 10) {
                    getBinding().layoutNum.setVisibility(View.VISIBLE);
                    if (data.getTotal() < 100) {
                        getBinding().noticeNum.setText(data.getTotal() + "");
                    } else {
                        getBinding().noticeNum.setText("99+");
                    }
                    getBinding().noticeNum.setBackgroundResource(R.drawable.shape_messge_notice_num_bg);
                    getBinding().layoutNum.setBackgroundResource(R.drawable.shape_messge_notice_num_white_bg);
                }
            }
        } else if (ApiUrl.POST_MODEL_DATA.equals(flag)) { //模版数据
            MultComponentBean personCenterInfo = (MultComponentBean) response;
            if (personCenterInfo == null || personCenterInfo.getComponentList() == null) {
                loadLocalModelData();
            } else {
                String modelJson = JsonUtils.toJson(personCenterInfo);
                multComponentBean = com.haiercash.gouhua.unity.JsonUtils.json2Class(modelJson, MultComponentBean.class);
                hrCommonAdapter.setNewData(multComponentBean.getComponentList());
            }
        } else if (ApiUrl.POST_URL_PSERSON_CENTER_NEW2.equals(flag)) {//smarH5
            try {
                String responseStr = JsonUtils.toJson(response);
                SmartH5Bean smartH5Bean = JsonUtils.fromJson(responseStr, SmartH5Bean.class);
                showSceneLoan(smartH5Bean);
                Map<String, Object> map = com.haiercash.gouhua.unity.JsonUtils.getRequestObj(responseStr);
                JSONObject object = new JSONObject(map);
                Map<String, Object> flattenJson = FlattenJsonUtils.flattenHomeJson(object, "");
                hrCommonAdapter.setPersonCenterData(flattenJson);
                hrCommonAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void showSceneLoan(SmartH5Bean smartH5Bean) {
        if (smartH5Bean != null && smartH5Bean.getCredit() != null) {
            com.haiercash.gouhua.beans.Credit smartH5BeanCredit = smartH5Bean.getCredit();
            homepageQuotaCardNew.setSceneLogoInfo(smartH5BeanCredit.getCreditTitleForScene());
            String applyAmount = smartH5BeanCredit.getApplyAmount();
            String loanJumpUrl = smartH5BeanCredit.getLoanJumpUrl();
            String nodeJumpUrl = smartH5BeanCredit.getNodeJumpUrl();
            H5ConfigHelper.setH5SceneInfo(loanJumpUrl, nodeJumpUrl);
            if (!CheckUtil.isEmpty(applyAmount) && !CheckUtil.isEmpty(loanJumpUrl)) {
                controlDialogUtil.setPopupGInfo(smartH5BeanCredit);
                controlDialogUtil.checkDialog();
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        finishRefresh();
        showProgress(false);
        if (ApiUrl.POST_HOME_CREDIT_INFO.equals(url)) {
            if (CheckUtil.isEmpty(homeQuotaBean)) {
                homepageQuotaCardNew.setData(null, mActivity);
                showErrorPage();
            } else {
                homepageQuotaCardNew.setData(homeQuotaBean, mActivity);
            }
            //super.onError(error, url);
        } else if (ApiUrl.POST_HOME_CONFIG_INFO.equals(url)) {
            finishRefresh();
            showErrorPage();
        } else if (ApiUrl.POST_MODEL_DATA.equals(url)) {
            loadLocalModelData();
        }

    }

    public void changeLoanMarket() {
        if (TextUtils.equals(credit.getStatus(), "M")) {
            ArrayList<LoanMarketBean> loanExcess = homeQuotaBean.getLoanExcess();
            MainActivity activity = ActivityUntil.findActivity(MainActivity.class);
            if (activity != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("LoanMarketData", loanExcess);
                activity.showLoanMarketFragment(bundle);
            }
        }
    }

    //首页接口请求失败，显示错误页
    private void showErrorPage() {
        if (CheckUtil.isEmpty(homeConfigs)) {
            HomePersistenceUtil util = new HomePersistenceUtil(mActivity);
            HomeConfig config = util.readJson();
//            adapter.setNewData(config.getConfigs());
//            adapter.notifyDataSetChanged();
        }
        showDialog("连接似乎有问题，请检查网络或刷新重试", "我知道了", "刷新重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == 2) {
                    loadHomeData();
                }
            }
        }).setStandardStyle(2);
    }

    /**
     * 显示提额入口
     */
    void showCreditLimitPromotion(boolean isShow) {
        getBinding().llUpAmount.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 111) {
            Logger.d("MainBaseFragment  : onActivityResult  :  loadHomeData");
            loadHomeData();
        } else if (requestCode == WebSimpleFragment.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            startActivity(new Intent(mActivity, NameAuthIdCardActivity.class));
        }
    }

    @Override
    public void onDestroyView() {
        if (bearLoginPop != null) {
            bearLoginPop.onDestroy();
        }
        super.onDestroyView();
    }

    @Override
    public void resetData() {
        super.resetData();
        showCreditLimitPromotion(false);
    }

    private void finishRefresh() {
        getBinding().srlRefresh.finishRefresh();
    }

    @Override
    protected String getPageCode() {
        return "HomePage";
    }

    private void initMessageCenter() {
        boolean isTourist = AppUntil.isTourist();
        getBinding().clScanMessageContainer.setVisibility(isTourist ? View.GONE : View.VISIBLE);
        getBinding().tvTourist.setVisibility(isTourist ? View.VISIBLE : View.GONE);
    }

    /**
     * 加载本地数据
     */
    private void loadLocalModelData() {
        //模拟接口请求
        try {
            AssetManager assetManager = getActivity().getAssets();
            InputStream inputStream = assetManager.open("home.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            inputStream.close();
            String jsonContent = builder.toString();
            multComponentBean = com.haiercash.gouhua.unity.JsonUtils.json2Class(jsonContent, MultComponentBean.class);
            hrCommonAdapter.setNewData(multComponentBean.getComponentList());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
