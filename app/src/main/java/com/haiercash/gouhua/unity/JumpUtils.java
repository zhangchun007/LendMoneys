package com.haiercash.gouhua.unity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;


import com.app.haiercash.base.utils.router.ARouterUntil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.ProguardInfoActivity;
import com.haiercash.gouhua.activity.bankcard.MyCreditCardActivity;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.activity.personalinfor.PersonalInformationActivity;
import com.haiercash.gouhua.activity.setting.PushManagerActivity;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.unity.ActionBean;
import com.haiercash.gouhua.fragments.mine.MessageListFragment;
import com.haiercash.gouhua.hybrid.H5LinkJumpHelper;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.AIServer;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.DischargeUtil;

import java.util.Map;

/**
 * @Description: 跳转的统一处理
 * @Author: zhangchun
 * @CreateDate: 2023/11/30
 * @Version: 1.0
 */
public class JumpUtils {

    /**
     * 配置跳转
     *
     * @param context
     * @param actionBean
     */
    public static void jumpAction(Context context, ActionBean actionBean) {
        //优先判断登录
        if (actionBean.getNeedLogin() && !UserStateUtils.isLogIn()) {
            AppApplication.setLoginCallback(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    jumNext(context, actionBean);
                }
            });
            jumLogin(context);
        } else {
            jumNext(context, actionBean);
        }
    }

    private static void jumNext(Context context, ActionBean actionBean) {
        //再次判断实名
        if (actionBean.getNeedRealName() && !UserStateUtils.isAuthonName()) {
            H5LinkJumpHelper.INSTANCE().goLoanPage((BaseActivity) context);
            return;
        }
        //判断是否web页面
        if ("jumpH5".equals(actionBean.getActionType())) {
            String jumpUrl = actionBean.getJumpUrl();
            if (!TextUtils.isEmpty(jumpUrl)) {
                jumpJsWebActivity(context, jumpUrl);
            }
        } else {
            jumpNative(context, actionBean.getActionType());
        }
    }

    /**
     * 跳转到nativie
     *
     * @param actionType
     */
    public static void jumpNative(Context context, String actionType) {
        switch (actionType) {
            case "onlineService"://在线客服
                AIServer.showAiWebServer((BaseActivity) context, "在线客服");
                break;
            case "help_center"://帮助中心
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER).navigation();
                break;
            case "settlement"://结清证明
                if (CommomUtils.isRealName()) {
                    DischargeUtil util = new DischargeUtil((BaseActivity) context);
                    util.getDischarge();
                } else {
                    DialogUtils.create(context).setDialogStyle(DialogStyle.STYLE1).setContent("您在过去24个月内暂无结清账单，如需开具24个月之前账单的结清证明,请联系客服人员。").setRightButtoText("我知道了").setShowLeftButton(false).show();
                }
                break;
            case "dataManage"://资料管理
//                context.startActivity(new Intent(context, PerfectDataActivity.class));
                break;
            case "infoProtect"://信息保护
                Intent intent = new Intent(context, PushManagerActivity.class);
                context.startActivity(intent);
                break;
            case "accountSecurity"://账号与安全
                ARouterUntil.getInstance(PagePath.ACTIVITY_SAFETY_SETTING).navigation();
                break;
            case "coupon"://优惠券
                ARouterUntil.getInstance(PagePath.ACTIVITY_COUPON_BAG).navigation();
                break;
            case "bankCard"://银行卡
                context.startActivity(new Intent(context, MyCreditCardActivity.class));
                break;
            case "personInfo"://个人信息页面
                openPersonProfile(context);
                break;
            case "message"://跳转到消息
                openMessage(context);
                break;
            case "setting"://设置
                openSetting(context);
                break;
            case "loanCalculator"://贷款计算器
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_CALCULATION).put("isShowTitle", false).navigation();
                break;
           case "couponbag"://券包
               ARouterUntil.getInstance(PagePath.ACTIVITY_COUPON_BAG).navigation();
                break;

        }
    }

    /**
     * 跳转到web页面
     *
     * @param context
     * @param url
     */
    public static void jumpJsWebActivity(Context context, String url) {
        Intent intent = new Intent();
        intent.setClass(context, JsWebBaseActivity.class);
        intent.putExtra("jumpKey", url);
        context.startActivity(intent);
    }

    /**
     * 跳转到消息
     *
     * @param context
     */
    private static void openMessage(Context context) {
        if (UserStateUtils.isLogIn()) {
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_MESSAGE).navigation();
        } else {
            AppApplication.setLoginCallback(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    ARouterUntil.getContainerInstance(PagePath.FRAGMENT_MESSAGE).navigation();
                }
            });
            jumLogin(context);
        }

    }

    /**
     * 跳转到 设置
     *
     * @param context
     */
    private static void openSetting(Context context) {
        ARouterUntil.getContainerInstance(PagePath.FRAGMENT_SETTING).navigation();
    }

    /**
     * 打开个人资料
     */
    private static void openPersonProfile(Context context) {
        if (context == null) return;
        if (!AppApplication.isLogIn()) {
            AppApplication.setLoginCallback(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    context.startActivity(new Intent(context, PersonalInformationActivity.class));
                }
            });
            jumLogin(context);
        } else {
            context.startActivity(new Intent(context, PersonalInformationActivity.class));
        }
    }

    /**
     * 跳转到登录
     *
     * @param context
     */
    private static void jumLogin(Context context) {
        LoginSelectHelper.staticToGeneralLogin();
    }
}
