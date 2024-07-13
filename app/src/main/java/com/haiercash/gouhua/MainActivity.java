package com.haiercash.gouhua;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.activity.edu.EduCommon;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.AppUntil;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.beans.msg.MessageInfoNew;
import com.haiercash.gouhua.beans.unity.MultComponentBean;
import com.haiercash.gouhua.databinding.ActivityMainBinding;
import com.haiercash.gouhua.fragments.main.HomeLeaguerFragment;
import com.haiercash.gouhua.fragments.main.LoanMarketFragment;
import com.haiercash.gouhua.fragments.main.MainEduBorrowUntil;
import com.haiercash.gouhua.fragments.main.MainFragmentNew;
import com.haiercash.gouhua.fragments.main.MineFragmentNew;
import com.haiercash.gouhua.interfaces.OnPopClickListener;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.tplibrary.receiver.XGUntil;
import com.haiercash.gouhua.uihelper.OpenMsgPushPopupWindow;
import com.haiercash.gouhua.uihelper.UpdateProtocolPopupWindow;
import com.haiercash.gouhua.unity.FlattenJsonUtils;
import com.haiercash.gouhua.unity.HRPersonFragment;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/7/31<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.ACTIVITY_MAIN)
public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private final Map<String, BaseFragment> fragmentMap = new HashMap<>();
    private MainHomeHelper homeHelper;
    private BaseFragment mCurrentFragment;
    private boolean hasShowLogin = false; //push消息只打开一次登录页面
    /**
     * 仅加载一次渠道导流数据
     */
    private boolean isLoadChannelSettingFlag = true;
    private String userStateType;
    private String userStatus;  //当前用户状态
    private String lastContentValue; //记录当前推送的值
    private ImageView mIvRbHome;

    public BaseFragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    protected ActivityMainBinding initBinding(LayoutInflater inflater) {
        return binding = ActivityMainBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        homeHelper = new MainHomeHelper(this);
        if (AppApplication.isLogIn()) {
            UMengUtil.registerGlobalProperty("true", AppApplication.userid);
        } else {
            UMengUtil.registerGlobalProperty("false", "");

        }
        int iconId = getIntent().getIntExtra("mainShowIconId", R.id.rbHome);
        if (isCanPageIn()) {
            replaceContentFragment(iconId, null);
        } else {
            setViewSelect(findViewById(iconId));
        }
        mIvRbHome = findViewById(R.id.rbHome);
        setonClickByViewId(R.id.rbHome, R.id.rbFind, R.id.rbMine, R.id.iv_loan_market_home);
        homeHelper.initHome();
        onCreateIntent();
        MainEduBorrowUntil.INSTANCE(this);
        homeHelper.showNewIcon(binding.rgMenu, true);
        RxBus.getInstance().addSubscription(this, RxBus.getInstance().register(ActionEvent.class, (actionEvent -> {
            if (actionEvent.getActionType() == ActionEvent.MainShowIcon) {
                homeHelper.showNewIcon(binding.rgMenu, !"default".equals(actionEvent.getActionMsg()));
            } else if (actionEvent.getActionType() == ActionEvent.MainFragmentReset) {
                try {
                    //退出登录后重置数据或者借款结果页、额度申请结果页回到首页
                    for (BaseFragment fragment : fragmentMap.values()) {
                        fragment.resetData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (actionEvent.getActionType() == ActionEvent.HOMEPAGE_TOAST_MESSAGE) {
                UiUtil.toast(actionEvent.getActionMsg());
            } else if (actionEvent.getActionType() == ActionEvent.DEAL_WITH_PUSH_INFO) {
                dealWithPushInfo();
            } else if (actionEvent.getActionType() == ActionEvent.SELECT_MAIN_TAB) {
                setFragment();
            }
        })));
        Map<String, Object> map = new HashMap<>();
        map.put("is_success", SystemUtils.isNotifyEnabled(this) + "");
        map.put("page_name_cn", "够花-首页");
        UMengUtil.onEventObject("HomePageNotice_Authorization", map, "HomePage");


        //保存用户数据
        FlattenJsonUtils.saveUserInfo();
        //获取模版数据
        if (netHelper != null) {
            Map<String, String> mapModel = new HashMap<>();
            mapModel.put("modelNo", CommonConfig.PERSON_MODEL_UNITY);
            netHelper.postService(ApiUrl.POST_MODEL_DATA, mapModel, MultComponentBean.class);
        }

    }

    //处理推送消息
    private void dealWithPushInfo() {
        String contentType = SpHelper.getInstance().readMsgFromSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_TYPE);
        String contentValue = SpHelper.getInstance().readMsgFromSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_VALUE);
        if (!CheckUtil.isEmpty(contentType)
                && !CheckUtil.isEmpty(contentValue)
                && !AppApplication.isLogIn()) {
            if (!hasShowLogin || !contentValue.equals(lastContentValue)) {
                LoginSelectHelper.staticToGeneralLogin();
                hasShowLogin = true;
            } else {
                hasShowLogin = false;
                CommomUtils.clearPushSp();
            }
            this.lastContentValue = contentValue;
        } else if (!CheckUtil.isEmpty(contentType)
                && !CheckUtil.isEmpty(contentValue)
                && ("H5Link".equals(contentType))
                && URLUtil.isNetworkUrl(contentValue)) {
            Intent i = new Intent(this, JsWebBaseActivity.class);
            i.putExtra("jumpKey", contentValue);
            i.putExtra("fromPush", true);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityUntil.finishOthersActivityByPageKey(PagePath.ACTIVITY_MAIN);
            startActivity(i);
            hasShowLogin = false;
            CommomUtils.clearPushSp();
        } else if (!CheckUtil.isEmpty(contentValue)) {
            Map<String, String> map = new HashMap<>();
            map.put("pushId", contentValue);
            map.put("processId", TokenHelper.getInstance().getH5ProcessId());
            map.put("deviceId", SystemUtils.getDeviceID(this));
            netHelper.postService(ApiUrl.POST_PUSH_DETAIL_INFO, map, MessageInfoNew.class);
            hasShowLogin = false;
            CommomUtils.clearPushSp();
        }
    }

    private void onCreateIntent() {
        XGUntil.registerPushAndAccount(this.getApplicationContext(), AppApplication.userid);
        if (AppApplication.isLogIn()) {
            binding.vLine.post(new Runnable() {
                @Override
                public void run() {
                    OpenMsgPushPopupWindow.showNoticePermission(MainActivity.this, binding.vLine);
                    binding.vLine.removeCallbacks(this);
                }
            });
        }

    }

    @Override
    public synchronized void onClick(View view) {
        replaceContentFragment(view.getId(), null);
    }

    private void setViewSelect(View view) {
        for (int i = 0; i < binding.rgMenu.getChildCount(); i++) {
            binding.rgMenu.getChildAt(i).setSelected(false);
        }
        view.setSelected(true);
        view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.img_anim));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isLoadChannelSettingFlag = true;
        setIntent(intent);
        onCreateIntent();
    }

    /**
     * 是否登录
     *
     * @return true:已登陆；false：未登录
     */
    private boolean isCanPageIn() {
        if (!AppApplication.isLogIn()) {
            setViewSelect(binding.rbHome);
            replaceContentFragment(R.id.rbHome, null);
            return false;
        }
        return true;
    }

    @Override
    protected boolean useBaseToUmPage() {
        return false;
    }

    @Override
    protected String getPageCode() {
        return "TabBar";
    }

    /**
     * 替换Fragment内容对象
     *
     * @param resId menu Id
     */
    private synchronized void replaceContentFragment(int resId, Bundle bundle) {
        Class<? extends BaseFragment> cls;
        if (resId == R.id.rbHome) {
            binding.ivLoanMarketHome.setVisibility(View.GONE);
            binding.rbHome.setVisibility(View.VISIBLE);
            UMengUtil.commonClickEvent("TabBarHome_Click", "够花", getPageCode());
            cls = MainFragmentNew.class;
        } else if (resId == R.id.rbFind) {
            if (AppUntil.touristIntercept(mIvRbHome, this)) {
                return;
            }
            UMengUtil.commonClickEvent("TabBarMember_Click", "会员", getPageCode());
            cls = HomeLeaguerFragment.class;
        } else if (resId == R.id.rbMine) {
            if (AppUntil.touristIntercept(mIvRbHome, this)) {
                return;
            }
            UMengUtil.commonClickEvent("TabBarMe_Click", "我的", getPageCode());
            cls = HRPersonFragment.class;
//            cls = MineFragmentNew.class;
        } else if (resId == R.id.iv_loan_market_home) {
            if (AppUntil.touristIntercept(mIvRbHome, this)) {
                return;
            }
            binding.ivLoanMarketHome.setVisibility(View.VISIBLE);
            binding.rbHome.setVisibility(View.GONE);
            cls = LoanMarketFragment.class;
        } else {
            return;
        }
        try {
            setViewSelect(findViewById(resId));
            String tagFragmentName = cls.getSimpleName();
            if (!fragmentMap.containsKey(tagFragmentName)) {
                fragmentMap.put(tagFragmentName, cls.newInstance());
            }
            mCurrentFragment = fragmentMap.get(tagFragmentName);
            if (bundle != null) {
                mCurrentFragment.setArguments(bundle);
            }
            changeFragment(R.id.fl_main_content, mCurrentFragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        RxBus.getInstance().unSubscribe(this);
        homeHelper.onDestroy();
        MainEduBorrowUntil.INSTANCE(this).destroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 111) {//onViewClick(rb_home);
                setFragment();
            }
        } else if (mCurrentFragment != null) {
            mCurrentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setFragment() {
        setFragment(R.id.rbHome);
        binding.ivLoanMarketHome.setVisibility(View.GONE);
        binding.rbHome.setVisibility(View.VISIBLE);
    }

    public void setFragment(int viewId) {
        replaceContentFragment(viewId, null);
    }

    public void setFragment(int viewId, Bundle bundle) {
        replaceContentFragment(viewId, bundle);
    }

    public void showLoanMarketFragment(Bundle bundle) {
        binding.ivLoanMarketHome.setVisibility(View.VISIBLE);
        binding.rbHome.setVisibility(View.GONE);
        setFragment(R.id.iv_loan_market_home, bundle);
    }


    /**
     * 再按一次退出程序
     */
    private long touchTime = 0;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - touchTime) >= 2000) {
            UiUtil.toast("再按一次退出程序");
            touchTime = currentTime;
        } else {
            ActivityUntil.finishOthersActivity();
            postPageEndEvent();
            finish();
            UMengUtil.onKillProcess();
            System.exit(0);
        }
    }

    private void postPageEndEvent() {
        if (mCurrentFragment != null) {
            if (mCurrentFragment instanceof MainFragmentNew) {
                UMengUtil.pageEnd("HomePage");
            } else if (mCurrentFragment instanceof HomeLeaguerFragment) {
                UMengUtil.pageEnd("MemberPage");
            } else if (mCurrentFragment instanceof MineFragmentNew) {
                UMengUtil.pageEnd("MePage");

            }
        }
    }

    //提供当前的类型到个人中心页面从而判断是否逾期
    public String getIsOverdue() {
        boolean isOverdue = !CheckUtil.isEmpty(userStateType) && userStateType.endsWith("3");
        return isOverdue ? "true" : "false";
    }

    @Override
    public void onSuccess(Object success, String url) {
        showProgress(false);
        if (ApiUrl.POST_PUSH_DETAIL_INFO.equals(url)
                || ApiUrl.MESSAGE_DETAIL_INFO.equals(url)) {
            MessageInfoNew data = (MessageInfoNew) success;
            if ("CustomContent".equals(data.getPushContentType())) {
                ActivityUntil.finishOthersActivityByPageKey(PagePath.ACTIVITY_MAIN);
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_NOTICE)
                        .put("noticeTitle", data.getPushTitle())
                        .put("pushSubTitle", data.getPushSubTitle())
                        .put("noticeTime", data.getCreateTimeStr())
                        .put("noticeContent", data.getContent())
                        .put("fromPush", true)
                        .navigation();
            } else if (ApiUrl.POST_MODEL_DATA.equals(url)) {
                MultComponentBean personCenterInfo = (MultComponentBean) success;
                String modelJson = JsonUtils.toJson(personCenterInfo);
                FlattenJsonUtils.setPersonModelDataJson(modelJson);

            } else if ("HomePage".equals(data.getPushContentType())) {
                CommomUtils.goHomePage(true);
            } else {
                String jumpUrl = data.getJumpUrl();
                if (!CheckUtil.isEmpty(jumpUrl) && jumpUrl.contains("gouhua://")) {
                    jumpUrl = jumpUrl.substring(jumpUrl.indexOf("gouhua://"));
                    ActivityUntil.startOtherApp(this, jumpUrl);
                } else if (URLUtil.isNetworkUrl(jumpUrl)) {
                    if (CheckUtil.isEmpty(jumpUrl)) {
                        return;
                    }

                    Intent intent = new Intent();
                    intent.setClass(this, JsWebBaseActivity.class);
                    intent.putExtra("jumpKey", jumpUrl);
                    intent.putExtra("fromPush", true);
                    ActivityUntil.finishOthersActivityByPageKey(PagePath.ACTIVITY_MAIN);
                    startActivity(intent);
                } else {
                    ActivityUntil.startOtherApp(this, jumpUrl);
                }
            }

        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        showProgress(false);
        if (!ApiUrl.MESSAGE_READ.equals(url)) {
            super.onError(error, url);
        }
    }

    public void updateReadStatus(String inmailId) {
        Map<String, String> map = new HashMap<>();
        if (!CheckUtil.isEmpty(inmailId)) {
            map.put("inmailId", inmailId);
        }
        netHelper.postService(ApiUrl.MESSAGE_READ, map);
        getMessageDetail(inmailId);
    }

    /**
     * 获取消息详情
     */
    private void getMessageDetail(String inmailId) {
        if (CheckUtil.isEmpty(inmailId)) {
            return;
        }
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("inmailId", inmailId);
        map.put("processId", TokenHelper.getInstance().getH5ProcessId());
        map.put("deviceId", SystemUtils.getDeviceID(this));
        netHelper.postService(ApiUrl.MESSAGE_DETAIL_INFO, map, MessageInfoNew.class);

    }

    @Override
    protected void onResume() {
        super.onResume();
        EduCommon.resetSaveEdHasBackStatus();
        dealWithPushInfo();
    }

    private UpdateProtocolPopupWindow updateProtocolPopupWindow;

    public void queryNeedResignAgreements(OnPopClickListener listener) {
        if (updateProtocolPopupWindow == null) {
            updateProtocolPopupWindow = new UpdateProtocolPopupWindow(this, binding.rgMenu);
        }
        updateProtocolPopupWindow.queryNeedResignAgreements(listener);
    }
}