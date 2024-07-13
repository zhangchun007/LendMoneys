package com.haiercash.gouhua.fragments.mine;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.PermissionPageUtils;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.geetest.onelogin.OneLoginHelper;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.setting.MsgSettingActivity;
import com.haiercash.gouhua.activity.setting.PermissionsActivity;
import com.haiercash.gouhua.activity.setting.PrivateDescActivity;
import com.haiercash.gouhua.activity.setting.PushManagerActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.beans.PersonalRecommendBean;
import com.haiercash.gouhua.databinding.FragmentSettingBinding;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.VersionHelper;
import com.haiercash.gouhua.unity.FlattenJsonUtils;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 设置
 */
@Route(path = PagePath.FRAGMENT_SETTING)
public class SettingFragment extends BaseFragment {
    private FragmentSettingBinding mSettingBinding;
    //个性化开关结果
    private String switchConfig;

    @Override
    protected ViewBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return mSettingBinding = FragmentSettingBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle(R.string.setting_title);
        boolean isRecommendOpen = isRecommendOpen();
        mSettingBinding.linePush.lineRoot.setVisibility(isRecommendOpen ? View.VISIBLE : View.GONE);
        mSettingBinding.llPush.setVisibility(isRecommendOpen ? View.VISIBLE : View.GONE);
        if (!VersionHelper.isLastVersion(mActivity) && VersionHelper.HAS_SHOW_UPDATE) {
            mSettingBinding.tvVersion.setText("");
            mSettingBinding.ivVersionUpdate.setVisibility(View.VISIBLE);
        } else {
            mSettingBinding.ivVersionUpdate.setVisibility(View.GONE);
            mSettingBinding.tvVersion.setText(getString(R.string.setting_version, SystemUtils.getAppVersion(mActivity)));
        }
        mSettingBinding.llSecurityCenter.setOnClickListener(this);
        mSettingBinding.llPermission.setOnClickListener(this);
        mSettingBinding.llMsg.setOnClickListener(this);
        mSettingBinding.llPush.setOnClickListener(this);
        mSettingBinding.llPrivateDesc.setOnClickListener(this);
        mSettingBinding.llHelp.setOnClickListener(this);
        mSettingBinding.llFeedback.setOnClickListener(this);
        mSettingBinding.llWx.setOnClickListener(this);
        mSettingBinding.llAboutUs.setOnClickListener(this);
        mSettingBinding.llTmp.setOnClickListener(this);
        mSettingBinding.tvLogout.setOnClickListener(this);

        if (!BuildConfig.IS_RELEASE) {
            mSettingBinding.llTmp.setVisibility(View.VISIBLE);
        }
    }

    //个性化开关
    public static boolean isRecommendOpen() {
        return TextUtils.equals("Y", SpHp.getOther(SpKey.CONFIGURE_SWITCH_PERSONAL_RECOMMEND, "N"));
    }

    /**
     * 获取定向推送开关状态
     */
    private void queryRecommendState() {
        HashMap<String, String> map = new HashMap<>();
        String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        map.put("userId", userId);
        netHelper.postService(ApiUrl.POST_QUERY_RECOMMEND_STATE, map, PersonalRecommendBean.class);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (PermissionPageUtils.isNotificationEnable(mActivity)) {
            mSettingBinding.tvMsgStatus.setText(R.string.setting_status_yes);
        } else {
            mSettingBinding.tvMsgStatus.setText(R.string.setting_status_no);
        }
        mSettingBinding.tvLogout.setVisibility(AppApplication.isLogIn() ? View.VISIBLE : View.GONE);
        //开关开的状态下
        if (AppApplication.isLogIn() && isRecommendOpen()) {
            queryRecommendState();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mSettingBinding.llSecurityCenter) {//账号与安全
            AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    ARouterUntil.getInstance(PagePath.ACTIVITY_SAFETY_SETTING).navigation();
                }
            });
        } else if (v == mSettingBinding.llPermission) {//权限管理
            startActivity(new Intent(mActivity, PermissionsActivity.class));
        } else if (v == mSettingBinding.llMsg) {//消息通知
            startActivity(new Intent(mActivity, MsgSettingActivity.class));
        } else if (v == mSettingBinding.llPush) {//定向推送-个性化信息
            AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    Intent intent = new Intent(mActivity, PushManagerActivity.class);
                    if (switchConfig != null) {
                        intent.putExtra("switchConfig", switchConfig);
                    }
                    startActivity(intent);
                }
            });
        } else if (v == mSettingBinding.llPrivateDesc) {//隐私说明
            startActivity(new Intent(mActivity, PrivateDescActivity.class));
        } else if (v == mSettingBinding.llHelp) {//帮助中心
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER).navigation();
        } else if (v == mSettingBinding.llFeedback) {//意见反馈
            AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    showProgress(true);
                    Map<String, String> map = new HashMap<>();
                    if (!CheckUtil.isEmpty(AppApplication.userid)) {
                        map.put("userId", EncryptUtil.simpleEncrypt(AppApplication.userid));
                    }
                    netHelper.getService(ApiUrl.URL_IS_FEEDBACK_ALLOW, map);
                }
            });
        } else if (v == mSettingBinding.llWx) {//微信公众号
            WebHelper.jumpWxPublic(mActivity);
        } else if (v == mSettingBinding.llAboutUs) {//关于我们
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_ABOUT_US).put("switchConfig", switchConfig).navigation();
        } else if (v == mSettingBinding.llTmp) {//自定义工具入口
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_TMP).navigation();
        } else if (v == mSettingBinding.tvLogout) {//退出登录
            showDialog("提示", "是否确认退出登录？", "取消", "退出", (dialog, which) -> {
                if (which != 2) {
                    return;
                }
                Map<String, String> logoutMap = new HashMap<>();
                if (!CheckUtil.isEmpty(AppApplication.userid)) {
                    logoutMap.put("userId", EncryptUtil.simpleEncrypt(AppApplication.userid));
                    logoutMap.put("h5Token", TokenHelper.getInstance().getH5Token());
                    netHelper.postService(ApiUrl.LOGOUT_URL, logoutMap);
                } else {
                    clearLoginState();
                }

            });

            //一键登录需要重新注册下
            OneLoginHelper.with().register(CommonConfig.ONE_KEY_SECRET);
        } else {
            super.onClick(v);
        }
    }

    @Override
    public void onSuccess(Object response, String flag) {
        if (ApiUrl.URL_IS_FEEDBACK_ALLOW.equals(flag)) {
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_FEEDBACK).navigation();
            showProgress(false);
        } else if (ApiUrl.LOGOUT_URL.equals(flag)) {
            showProgress(false);
            //推出时删除数据
            FlattenJsonUtils.getMap().clear();
            FlattenJsonUtils.getHomeMap().clear();
            clearLoginState();
        } else if (ApiUrl.POST_QUERY_RECOMMEND_STATE.equals(flag)) {
            PersonalRecommendBean personalRecommendBean = (PersonalRecommendBean) response;
            if (personalRecommendBean != null && !CheckUtil.isEmpty(personalRecommendBean.getSwitchConfig())) {
                switchConfig = personalRecommendBean.getSwitchConfig();
                setPushStatus();
            }
            showProgress(false);
        }
    }

    private void setPushStatus() {
        //只有接口请求成功且是N才显示关闭状态
        try {
            mSettingBinding.tvPushStatus.setText(getString("N".equals(switchConfig) ? R.string.setting_status_no : R.string.setting_status_yes));
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.LOGOUT_URL.equals(url)) {
            showProgress(false);
            UiUtil.toast(TextUtils.isEmpty(error.head.retMsg) ? "退出登录失败" : error.head.retMsg);
        } else {
            super.onError(error, url);
        }
    }

    //退出登录成功后，清除相应的登录状态
    private void clearLoginState() {
        CommomUtils.clearSp();
        RxBus.getInstance().post(new ActionEvent(ActionEvent.MainFragmentReset));
        RxBus.getInstance().post(new ActionEvent(ActionEvent.REFRESHUSERINFO));
        MainHelper.backToMainHome();
    }
}
