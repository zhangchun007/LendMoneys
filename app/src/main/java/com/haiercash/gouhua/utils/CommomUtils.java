package com.haiercash.gouhua.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebStorage;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.app.haiercash.base.net.config.CommonSpKey;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.SplashActivity;
import com.haiercash.gouhua.activity.accountsettings.ChangePhoneNumActivity;
import com.haiercash.gouhua.activity.edu.NameAuthStartActivity;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.FuncBeans;
import com.haiercash.gouhua.hybrid.H5ConfigHelper;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Sun
 * Date :    2018/3/23
 * FileName: CommomUtils
 * Description:
 */

public class CommomUtils {


    /**
     * 清空所有sp信息
     * 用户输入的登录名不清空
     * 调用情况：
     * 1、点击 退出登录 时调用，清空缓存
     * 2、登录页面点击 登录 时调用，防止用户切换账号后取到老数据
     * 其他情况要谨慎调用，需要的话要一起磋商，防止产生丢值问题
     * isSign//避免在app进入时友盟埋点不能用
     */
    public static void clearSp() {
        clearSp(true);
    }

    public static void clearSp(boolean isSign) {
        List<String> list = new ArrayList<>();
        list.add(SpKey.LOGIN);
        list.add(SpKey.USER);
        list.add(SpKey.LOCATION);
        list.add(SpKey.STATE);
        list.add(CommonSpKey.TAG_COMMON);
        //list.add(SpKey.FLAG);
        //list.add(SpKey.CONFIGURE);
        //list.add(SpKey.LOGIN_MOBILE);
        AppApplication.userid = "";
        H5ConfigHelper.h5CreditUrl = "";
        H5ConfigHelper.h5SceneCreditUrl = "";
        H5ConfigHelper.h5LoanUrl = "";
        H5ConfigHelper.h5SceneLoanUrl = "";
        H5ConfigHelper.h5RepayJumpUrl = "";
        H5ConfigHelper.h5RepayJumpUrlY = "";
        SpHelper.getInstance().deleteAllMsgFromSpList(list);
        if (isSign) {
            UMengUtil.registerGlobalProperty("false", "");
            UMengUtil.signInOrOut();
        }
        CookieSyncManager.createInstance(AppApplication.CONTEXT);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        WebStorage.getInstance().deleteAllData(); //清空WebView的localStorage
    }

    //清除push的sp
    public static void clearPushSp() {
        SpHelper.getInstance().deleteMsgFromSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_TYPE);
        SpHelper.getInstance().deleteMsgFromSp(SpKey.OTHER, SpKey.TAG_PUSH_CONTENT_VALUE);
    }

    /**
     * 当前用户是否实名
     *
     * @return true 已经实名
     */
    public static boolean isRealName() {
        return !CheckUtil.isEmpty(SpHp.getUser(SpKey.USER_CERTNO));
    }

    public static boolean noNeedPhoneStateDialog() {
        String currentTime = TimeUtil.calendarToString();
        String lastTime = SpHelper.getInstance().readMsgFromSp(SpKey.READ_PHONE_STATE, SpKey.LAST_SHOW_DIALOG_TIME);
        return !CheckUtil.isEmpty(lastTime) && !TimeUtil.isOver48H(lastTime, currentTime);
    }

    /**
     * 点击修改绑定手机号
     */
    public static void clickToUpdateBindMobile(BaseActivity activity) {
        if (activity == null) {
            return;
        }
        if (isRealName()) {
            activity.startActivity(new Intent(activity, ChangePhoneNumActivity.class));
        } else {
            if (activity.isFinishing()) {
                return;
            }
            activity.showDialog("提示", "您尚未完成实名认证，暂不支持修改手机号", "取消", "去认证", (dialog, which) -> {
                if (which == 2) {
                    ActivityUntil.finishOthersActivity(MainActivity.class);
                    activity.startActivity(new Intent(activity, NameAuthStartActivity.class));
                }
            }).setStandardStyle(3);
        }
    }

    /**
     * 给glide  url 设置token
     *
     * @param url
     * @return
     */
    public static GlideUrl reGlideHead(String url) {
        String token = TokenHelper.getInstance().getCacheToken();
        //是否添加token进行判断
        if (!TextUtils.isEmpty(token)) {
            return new GlideUrl(url, new LazyHeaders.Builder().addHeader("Authorization", "Bearer" + token).addHeader("access_token", token).build());
        } else {
            return new GlideUrl(url, new LazyHeaders.Builder().build());
        }
    }

    /**
     * 获取个人中心 常用功能的默认数据
     *
     * @return
     */
    public static List<FuncBeans> getDefaultAlwaysFunsList() {
        List<FuncBeans> alwaysFuncList = new ArrayList<>();
        FuncBeans shopOrderBeans = new FuncBeans();
        shopOrderBeans.setImgPathDefault(R.drawable.img_jingang_default);
        alwaysFuncList.add(shopOrderBeans);

        FuncBeans bankCardManageBeans = new FuncBeans();
        bankCardManageBeans.setImgPathDefault(R.drawable.img_jingang_default);
        alwaysFuncList.add(bankCardManageBeans);

        FuncBeans clearCertificateBeans = new FuncBeans();
        clearCertificateBeans.setImgPathDefault(R.drawable.img_jingang_default);
        alwaysFuncList.add(clearCertificateBeans);

        FuncBeans accountSafeBeans = new FuncBeans();
        accountSafeBeans.setImgPathDefault(R.drawable.img_jingang_default);
        alwaysFuncList.add(accountSafeBeans);

        FuncBeans messageGuardBeans = new FuncBeans();
        messageGuardBeans.setImgPathDefault(R.drawable.img_jingang_default);
        alwaysFuncList.add(messageGuardBeans);

        FuncBeans loanCalBeans = new FuncBeans();
        loanCalBeans.setImgPathDefault(R.drawable.img_jingang_default);
        alwaysFuncList.add(loanCalBeans);

        FuncBeans helpCenterBeans = new FuncBeans();
        helpCenterBeans.setImgPathDefault(R.drawable.img_jingang_default);
        alwaysFuncList.add(helpCenterBeans);

        FuncBeans aliveServiceBeans = new FuncBeans();
        aliveServiceBeans.setImgPathDefault(R.drawable.img_jingang_default);
        alwaysFuncList.add(aliveServiceBeans);
        return alwaysFuncList;
    }


    /**
     * 获取个人中心 金刚位置的默认数据
     *
     * @return
     */
    public static List<FuncBeans> getDefaultResidentTibbonList() {
        List<FuncBeans> alwaysFuncList = new ArrayList<>();
        FuncBeans LoanRecordBeans = new FuncBeans();
        LoanRecordBeans.setImgPathDefault(R.drawable.img_jingang_default);
        alwaysFuncList.add(LoanRecordBeans);


        FuncBeans repaymentRecordBeans = new FuncBeans();
        repaymentRecordBeans.setImgPathDefault(R.drawable.img_jingang_default);
        alwaysFuncList.add(repaymentRecordBeans);

        FuncBeans couponBeans = new FuncBeans();
        couponBeans.setImgPathDefault(R.drawable.img_jingang_default);
        alwaysFuncList.add(couponBeans);

        FuncBeans redPackageBeans = new FuncBeans();
        redPackageBeans.setImgPathDefault(R.drawable.img_jingang_default);
        alwaysFuncList.add(redPackageBeans);


        return alwaysFuncList;
    }

    /**
     * 设置气泡bg 左下没有圆角
     *
     * @param context
     * @param view
     * @param startColor
     * @param endColor
     */
    public static void setBG(Context context, TextView view, String startColor, String endColor, String strokeColor, float radius, float strokeWith) {
        int colors[] = {Color.parseColor(startColor), Color.parseColor(endColor)};
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);

        //顺时针
        float[] radiusArray = {UiUtil.dip2px(context, radius),//左上
                UiUtil.dip2px(context, radius),//左上
                UiUtil.dip2px(context, radius),//右上
                UiUtil.dip2px(context, radius),//右上

                UiUtil.dip2px(context, radius),
                UiUtil.dip2px(context, radius),
                0F,//左下角
                0F,//左下角
        };

        if (!TextUtils.isEmpty(strokeColor)) {
            gradientDrawable.setStroke(UiUtil.dip2px(context, strokeWith), Color.parseColor(strokeColor)); // 设置边框宽度和颜色
        }

        gradientDrawable.setCornerRadii(radiusArray);
        view.setBackgroundDrawable(gradientDrawable);
    }


    private static AlertDialog alertDialog;
    //配置切环境
    private static final String[] environmentList = {"https://shop-p2.haiercash.com/", "https://shop-stg.haiercash.com/", "https://shop.haiercash.com/"};
    private static int savedIndex = 0;
    private static int checkedIndex = 0;

    /**
     * 长按设置切换环境
     */
    public static void changeEnvironment(Activity activity) {
        final String[] items = {"测试B", "封测", "生产"};
        String saveUrl = SpHp.getOther(SpKey.OTHER_CURRENT_URL, "");
        if (!TextUtils.isEmpty(saveUrl)) {
            for (int i = 0; i < environmentList.length; i++) {
                if (environmentList[i].equals(saveUrl)) {
                    savedIndex = i;
                }
            }
        } else {
            String url = BuildConfig.API_SERVER_URL;
            if (!TextUtils.isEmpty(url)) {
                for (int i = 0; i < environmentList.length; i++) {
                    if (environmentList[i].equals(url)) {
                        savedIndex = i;
                    }
                }
            }
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setTitle("环境切换");
        alertBuilder.setSingleChoiceItems(items, savedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkedIndex = i;
            }
        });

        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                if (savedIndex != checkedIndex) {
                    SpHp.saveSpOther(SpKey.OTHER_CURRENT_URL, environmentList[checkedIndex]);
                    Toast.makeText(activity, "环境切换，正在重启", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AppApplication.CONTEXT, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AppApplication.CONTEXT.startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());

                }
            }
        });

        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    //回首页   isSelectMainTab是否回首页后选中首页按钮
    public static void goHomePage() {
        goHomePage(false);
    }

    public static void goHomePage(boolean isSelectMainTab) {
        if (ActivityUntil.findActivity(PagePath.ACTIVITY_MAIN) == null) {
            ARouterUntil.getInstance(PagePath.ACTIVITY_MAIN).put("mainShowIconId", R.id.rbHome).navigation();
        }
        ActivityUntil.finishOthersActivityByPageKey(PagePath.ACTIVITY_MAIN);
        if (isSelectMainTab) {
            RxBus.getInstance().post(new ActionEvent(ActionEvent.SELECT_MAIN_TAB));
        }

    }

    /**
     * 是否已经申请过设备权限
     */
    public static boolean hasApplyPhoneStatePermission() {
        return "Y".equals(SpHelper.getInstance().readMsgFromSp(SpKey.PERMISSION_STATE, Manifest.permission.READ_PHONE_STATE));
    }
}
