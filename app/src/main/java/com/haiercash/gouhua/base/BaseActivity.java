package com.haiercash.gouhua.base;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.bui.BaseGHActivity;
import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.ScreenShotListenManager;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.permission.PermissionUtils;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.KeyBordUntil;
import com.app.haiercash.base.utils.system.PermissionPageUtils;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.sms.SmsTimePresenter;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.ScreenshotsPop;
import com.haiercash.gouhua.utils.AppLockUntil;
import com.haiercash.gouhua.utils.ControlDialogUtil;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.view.StatusView;
import com.haiercash.gouhua.widget.LoadingProgress;
import com.tbruyelle.rxpermissions2.Permission;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * 项目名称：所有Activity的基类
 * 项目作者：胡玉君
 * 创建日期：2017/4/11 18:26.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * <p>
 * 泛型参数为：该Activity的逻辑处理类
 * ----------------------------------------------------------------------------------------------------
 */
public abstract class BaseActivity extends BaseGHActivity implements View.OnClickListener, INetResult {
    private BaseDialog adToast, mGhDialog;
    protected DialogHelper dHelper;
    private ScreenShotListenManager mScreenShotListenManager;
    private boolean isHasScreenShotListener = false;
    private LoadingProgress mLoadingProgress;
    //短信验证码模块
    public SmsTimePresenter presenter;
    protected NetHelper netHelper;
    protected ControlDialogUtil controlDialogUtil;

//    public BaseActivity() {
//    }
//
//    @ContentView
//    public BaseActivity(@LayoutRes int contentLayoutId) {
//        this();
//        System.out.println("资源文件ID：" + contentLayoutId);
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dHelper = new DialogHelper(this);
        netHelper = new NetHelper(this);
        controlDialogUtil = new ControlDialogUtil(this);
        setOrientation();
        initBarView(R.id.bar_header);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//键盘弹出时自适应屏幕
        //设置沉浸式状态栏
        setStatusBar(Color.TRANSPARENT);
        mScreenShotListenManager = ScreenShotListenManager.newInstance(this);
        onViewCreated(savedInstanceState);
    }

    private void setOrientation() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏写死
        }
    }

    /**
     * 权限申请优化
     */
    public void requestPermission(Consumer<Boolean> consumer, int notice, String... strings) {
        //如果申请的权限已经授权，直接返回 true
        if (PermissionUtils.getRequestPermission(this, strings)) {
            Observable.just(true).subscribe(consumer);
        } else if (notice <= 0) {
            //如果申请的权限不需要提示，则直接申请
            disposablePermissionRequest(consumer, strings);
        } else {
            //如果需要提示，则先进行提示
            noticePermission(consumer, notice, strings);
        }
    }

    /**
     * 权限申请，并将其加入disposable，activity销毁时一起销毁
     */
    private void disposablePermissionRequest(Consumer<Boolean> consumer, String... strings) {
        netHelper.addDisposable(PermissionUtils.requestPermission(this, strings).subscribe(consumer));
        for (String permission : strings) {
            SpHelper.getInstance().saveMsgToSp(SpKey.PERMISSION_STATE, permission, "Y"); //标识曾经申请过权限
        }
    }

    /**
     * 获取权限状态
     */
    public static int getAuthorizeStatus(Activity activity, String authorize) {
        boolean isShould = ActivityCompat.shouldShowRequestPermissionRationale(activity, authorize);
        if (isShould) {
            return PermissionUtils.STATUS_REFUSE;
        }
        if (PermissionUtils.getRequestPermission(activity, authorize)) {
            //获取到权限
            return PermissionUtils.STATUS_SUCCESS;
        }
        if (CheckUtil.isEmpty(SpHelper.getInstance().readMsgFromSp(SpKey.PERMISSION_STATE, authorize))) {
            return PermissionUtils.STATUS_DEFAULT;
        }
        return PermissionUtils.STATUS_REFUSE_PERMANENT;
    }

    /**
     * 权限申请提示
     */
    public void noticePermission(Consumer<Boolean> consumer, int notice, String... strings) {
        if (notice == R.string.permission_phone_statue) {
            SpHelper.getInstance().saveMsgToSp(SpKey.READ_PHONE_STATE, SpKey.LAST_SHOW_DIALOG_TIME, TimeUtil.calendarToString());
        }
        if (needShowDeniedDialog(strings)) {
            try {
                consumer.accept(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        showDialog("温馨提示", getString(notice), "拒绝", "开启", (dialog, which) -> {
            //如果无权限且 暂不授权，直接返回false
            dialog.dismiss();
            if (which == 2) {
                 /* //sd或者位置或者相机，若不能弹出则直接到系统权限页
                for (String str : strings) {
                    if (Manifest.permission.CAMERA.equals(str)
                            || Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(str)
                            || Manifest.permission.READ_EXTERNAL_STORAGE.equals(str)
                            || Manifest.permission.ACCESS_FINE_LOCATION.equals(str)
                            || Manifest.permission.ACCESS_COARSE_LOCATION.equals(str)) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, str)
                                && ActivityCompat.checkSelfPermission(this, str) == PackageManager.PERMISSION_DENIED) {
                            PermissionPageUtils.jumpPermissionPage(this, BuildConfig.APPLICATION_ID);
                            return;
                        }
                    }
                }*/
                //如果同意授权，则进行权限申请
                disposablePermissionRequest(consumer, strings);

            } else {
                try {
                    consumer.accept(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setStandardStyle(2);
    }

    /**
     * 是否永久拒绝状态，也就是拒绝时勾选了不再询问
     */
    public boolean isRefusePermanent(String... strings) {
        for (String permission : strings) {
            int status = getAuthorizeStatus(this, permission);
            if (PermissionUtils.STATUS_REFUSE_PERMANENT == status) {
                return true;
            }
        }
        return false;
    }

    private boolean needShowDeniedDialog(String... strings) {
        String title = "";
        String info = "";
        for (String permission : strings) {
            if (PermissionUtils.STATUS_REFUSE_PERMANENT == getAuthorizeStatus(this, permission)) {
                switch (permission) {
                    case Manifest.permission.CAMERA:
                        title = "相机权限未开启";
                        info = "中开启，开启后即可使用身份验证、上传头像、扫码、意见反馈服务";
                        showDeniedDialog(title, info);
                        return true;
                    case Manifest.permission.READ_EXTERNAL_STORAGE:
                        title = "存储权限未开启";
                        info = "中开启，开启后即可使用上传头像、意见反馈服务";
                        showDeniedDialog(title, info);
                        return true;
                    case Manifest.permission.ACCESS_FINE_LOCATION:
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
                        title = "定位服务未开启";
                        info = "中开启，开启后即可使用申额、借款服务";
                        showDeniedDialog(title, info);
                        return true;
                    case Manifest.permission.READ_CONTACTS:
                    case Manifest.permission.READ_CALL_LOG:
                        title = "通讯录未授权";
                        info = "中开启，开启后即可使用通讯录服务";
                        showDeniedDialog(title, info);
                        return true;
                    default:
                        return false;
                }

            }
        }
        return false;
    }

    private void showDeniedDialog(String title, String info) {
        showDialog(title, getPermissionDialogInfo(info), "取消", "去设置", (dialog, which) -> {
            //如果无权限且 暂不授权，直接返回false
            dialog.dismiss();
            if (which == 2) {
                //如果同意授权，则进行权限申请
                PermissionPageUtils.jumpPermissionPage(this, BuildConfig.APPLICATION_ID);
            }
        }).setStandardStyle(2);
    }

    private SpannableStringBuilder getPermissionDialogInfo(String info) {

        return SpannableStringUtils.getBuilder(this, "你可到")
                .setForegroundColor(Color.parseColor("#606166"))
                .append("设置")
                .setForegroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .append(info)
                .setForegroundColor(Color.parseColor("#606166"))
                .create();
    }

    /**
     * 权限申请优化
     * Observable<Permission> 是否获取到权限，关心是否勾选不在询问
     * * permission.granted  true 获取权限成功
     * * permission.shouldShowRequestPermissionRationale  true   获取权限失败，但是用户没有勾选”不再询问“
     * * permission.granted fale && permission.shouldShowRequestPermissionRationale fale  权限申请失败，用户勾选了“不再询问”
     */
    public void requestPermissionEachCombined(Consumer<Permission> consumer, int notice, String... strings) {
        //如果申请的权限已经授权，直接返回 true
        if (PermissionUtils.getRequestPermission(this, strings)) {
            List<Permission> list = new ArrayList<>();
            for (String string : strings) {
                list.add(new Permission(string, true, true));
            }
            Observable.just(new Permission(list)).subscribe(consumer);
        } else if (notice <= 0) {
            //如果申请的权限不需要提示，则直接申请
            disposablePermissionRequestEachCombined(consumer, strings);
        } else {
            //如果需要提示，则先进行提示
            noticePermissionEachCombined(consumer, notice, strings);
        }
    }

    /**
     * 权限申请，并将其加入disposable，activity销毁时一起销毁
     * Observable<Permission> 是否获取到权限，关心是否勾选不在询问
     * * permission.granted  true 获取权限成功
     * * permission.shouldShowRequestPermissionRationale  true   获取权限失败，但是用户没有勾选”不再询问“
     * * permission.granted fale && permission.shouldShowRequestPermissionRationale fale  权限申请失败，用户勾选了“不再询问”
     */
    protected void disposablePermissionRequestEachCombined(Consumer<Permission> consumer, String... strings) {
        netHelper.addDisposable(PermissionUtils.requestEachCombined(this, strings).subscribe(consumer));
        for (String permission : strings) {
            SpHelper.getInstance().saveMsgToSp(SpKey.PERMISSION_STATE, permission, "Y"); //标识曾经申请过权限
        }
    }

    /**
     * 权限申请提示
     * Observable<Permission> 是否获取到权限，关心是否勾选不在询问
     * * permission.granted  true 获取权限成功
     * * permission.shouldShowRequestPermissionRationale  true   获取权限失败，但是用户没有勾选”不再询问“
     * * permission.granted fale && permission.shouldShowRequestPermissionRationale fale  权限申请失败，用户勾选了“不再询问”
     */
    public void noticePermissionEachCombined(Consumer<Permission> consumer, int notice, String... strings) {
        if (needShowDeniedDialog(strings)) {
            try {
                List<Permission> list = new ArrayList<>();
                for (String string : strings) {
                    list.add(new Permission(string, false, true));
                }
                consumer.accept(new Permission(list));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        showDialog("温馨提示", getString(notice), "暂不授权", "开启", (dialog, which) -> {
            //如果无权限且 暂不授权，直接返回false
            dialog.dismiss();
            if (which == 2) {
                //如果同意授权，则进行权限申请
                disposablePermissionRequestEachCombined(consumer, strings);
            } else {
                try {
                    List<Permission> list = new ArrayList<>();
                    for (String string : strings) {
                        list.add(new Permission(string, false, true));
                    }
                    consumer.accept(new Permission(list));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setStandardStyle(2);
    }


    /**
     * 设置沉浸式的颜色，为了使4.4以上系统上显示一致，
     * 采用30%透明状态栏的方式
     */
    public void setStatusBar(@ColorInt int color) {
        StatusView.setTransparentBar(this, color, 40);
    }

    /**
     * 设置状态栏是否黑色字体
     */
    public void setStatusBarTextColor(boolean isDark) {
        StatusView.setStatusBarTextColor(this, isDark);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 统一设置界面上View的点击事件
     *
     * @param ids 所有View的id
     */
    protected void setonClickByViewId(int... ids) {
        if (ids != null) {
            for (int resId : ids) {
                findViewById(resId).setOnClickListener(this);
            }
        }
    }

    /**
     * 统一设置界面上View的点击事件
     *
     * @param views 所有View
     */
    protected void setonClickByView(View... views) {
        if (views != null) {
            for (View view : views) {
                view.setOnClickListener(this);
            }
        }
    }

    /*START***********************************接口事件********************************************/
    @Override
    public void onClick(View v) {

    }

    public boolean isShowingDialog() {
        boolean isShowingAdToast = false;
        boolean isShowingGhDialog = false;
        if (adToast != null) {
            isShowingAdToast = adToast.isShowing();
        }
        if (mGhDialog != null) {
            isShowingGhDialog = mGhDialog.isShowing();
        }
        return isShowingGhDialog || isShowingAdToast;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (AppApplication.isLogIn() && AppLockUntil.isNeedLock(this)) {
                AppLockUntil.resetTime(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否使用baseActivity处理友盟页面统计
     * 子类可以自己处理
     */
    protected boolean useBaseToUmPage() {
        return true;
    }

    /**
     * 当前页面code，友盟埋点用
     */
    protected String getPageCode() {
        return "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        String pageCode = getPageCode();
        if (useBaseToUmPage() && !CheckUtil.isEmpty(pageCode)) {
            UMengUtil.pageStart(pageCode);
        }
        try {
            if (AppApplication.isLogIn() && AppLockUntil.isNeedLock(this)) {
                if (PagePath.ACTIVITY_CONTAINER.contains(getClass().getSimpleName()) && currentSupportFragment != null && PagePath.FRAGMENT_FEEDBACK.contains(currentSupportFragment.getClass().getSimpleName())) {
                    System.out.println("当前停留在意见反馈页面,不需要截屏反馈");
                } else {
                    startScreenShot();
                }
                AppLockUntil.initTimes(this);
            }
        } catch (Exception e) {
            Logger.e(e);
        }
    }

    @Override
    protected void onPause() {
        String pageCode = getPageCode();
        if (useBaseToUmPage() && !CheckUtil.isEmpty(pageCode)) {
            UMengUtil.pageEnd(pageCode);
        }
        super.onPause();
        if (AppApplication.isLogIn()) {
            if (isHasScreenShotListener && mScreenShotListenManager != null) {
                mScreenShotListenManager.stopListen();
                isHasScreenShotListener = false;
            }
            AppLockUntil.resetTimes();
        }
    }

    private void startScreenShot() {
        if (!isHasScreenShotListener && mScreenShotListenManager != null) {
            mScreenShotListenManager.setListener(new ScreenShotListenManager.OnScreenShotListener() {
                @Override
                public void requestPermissionThenShot(final Uri contentUri) {
                    requestPermission(aBoolean -> {
                        if (aBoolean) {
                            String imagePath = mScreenShotListenManager.getShotImageAndShow(contentUri);
                            onShot(imagePath);
                        } else {
                            showDialog("请授权手机存储权限");
                        }
                    }, R.string.permission_storage, Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                private ScreenshotsPop popScreen;

                @Override
                public void onShot(String imagePath) {
                    if (popScreen == null) {
                        popScreen = new ScreenshotsPop(BaseActivity.this, imagePath);
                    } else {
                        popScreen.resetImageData(imagePath);
                    }
                    if (popScreen.isShowing()) {
                        System.out.println("ScreenshotsPop 已经显示");
                    } else {
                        popScreen.showAtLocation(getTitleBarView() == null ? getWindow().getDecorView() : getTitleBarView());
//                        popScreen.showAtLocation(getWindow().getDecorView());
                    }
                }
            });
            mScreenShotListenManager.startListen();
            isHasScreenShotListener = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (ActivityUntil.findActivity(MainActivity.class) == null && ActivityUntil.getActivitySize() <= 1) {
            startActivity(new Intent(this, MainActivity.class));
        }
        super.onBackPressed();
    }

    /*点击空白区域时，隐藏软键盘*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            //点击空白位置 隐藏软键盘
            KeyBordUntil.hideKeyBord2(this);
        }
        return super.onTouchEvent(event);
    }

    /* ****************************************Dialog******************************************* */

    @Override
    public void showProgress(boolean flag) {
        showProgress(flag, null);
    }

    @Override
    public void showProgress(boolean flag, String msg) {
        if (flag) {
            if (mLoadingProgress == null) {
                mLoadingProgress = new LoadingProgress(this);
            }
            mLoadingProgress.showLoadingDialog(msg);
        } else if (mLoadingProgress != null) {
            mLoadingProgress.cancelLoadingDialog();
        }
    }

    /**
     * 初始化按钮
     */
    private void initDialog(CharSequence title, CharSequence msg, CharSequence btn1, CharSequence btn2, DialogInterface.OnClickListener listener) {
        if (mGhDialog == null) {
            mGhDialog = BaseDialog.getDialog(this, title, msg, btn1, btn2, listener);
        } else {
            mGhDialog.setTitle(title);
            mGhDialog.setMessage(msg);
            mGhDialog.setButton1(btn1, null);
            mGhDialog.setButton2(btn2, null);
            mGhDialog.setOnClickListener(listener);
        }
    }

    /**
     * 展示一个Dialog提示，每个页面使用一个AlertDialog，防止多层覆盖
     */
    @Override
    public BaseDialog showDialog(String msg) {
        if (adToast != null) {
            adToast.dismiss();
            adToast = null;
        }
        adToast = BaseDialog.getDialog(this, "提示", msg, "我知道了", null).setButtonTextColor(1, R.color.colorPrimary);
        adToast.show();
        return adToast;
    }

    /**
     * 展示一个Dialog提示，每个页面使用一个AlertDialog，防止多层覆盖
     */
    public BaseDialog showDialog(CharSequence msg) {
        if (adToast != null) {
            adToast.dismiss();
            adToast = null;
        }
        adToast = BaseDialog.getDialog(this, "提示", msg, "我知道了", null).setButtonTextColor(1, R.color.colorPrimary);
        adToast.show();
        return adToast;
    }

    /**
     * 展示一个Dialog提示，每个页面使用一个AlertDialog，防止多层覆盖
     */
    public BaseDialog showDialog(CharSequence title, CharSequence msg, DialogInterface.OnClickListener listener) {
        if (adToast != null) {
            adToast.dismiss();
            adToast = null;
        }
        adToast = BaseDialog.getDialog(this, title, msg, "我知道了", listener).setButtonTextColor(1, R.color.colorPrimary);
        adToast.show();
        return adToast;
    }

    /**
     * 显示第二个按钮
     */
    public BaseDialog showBtn2Dialog(CharSequence msg, CharSequence btn2, DialogInterface.OnClickListener listener) {
        return showDialog("提示", msg, null, btn2, listener);
    }

    /**
     * 两个按钮
     */
    public BaseDialog showDialog(CharSequence title, CharSequence msg, CharSequence btn1, CharSequence btn2, DialogInterface.OnClickListener listener) {
        initDialog(title, msg, btn1, btn2, listener);
        mGhDialog.show();
        return mGhDialog;
    }

    /**
     * 两个按钮无title
     */
    public BaseDialog showDialog(CharSequence msg, CharSequence btn1, CharSequence btn2, DialogInterface.OnClickListener listener) {
        return showDialog(null, msg, btn1, btn2, listener);
    }

    @Override
    protected void onDestroy() {
        if (mScreenShotListenManager != null) {
            mScreenShotListenManager.stopListen();
            mScreenShotListenManager = null;
        }
        if (adToast != null) {
            adToast.dismiss();
            adToast = null;
        }
        showProgress(false);
        if (mGhDialog != null) {
            mGhDialog.dismiss();
            mGhDialog = null;
        }
        if (dHelper != null) {
            dHelper.dismiss();
            dHelper = null;
        }
        if (controlDialogUtil != null) {
            controlDialogUtil.onDestroy();
        }
        //停止短信验证码倒计时
        if (presenter != null) {
            presenter.stopTime();
        }
        //注销所有的网络请求
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
        super.onDestroy();
    }

    public SmsTimePresenter registerSmsTime(int smsViewId) {
        presenter = SmsTimePresenter.getSmsTime(this, findViewById(smsViewId));
        return presenter;
    }


    @Override
    public void onSuccess(Object success, String url) {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        System.out.println("BaseActivity onSuccess->" + url);
    }

    public void onError(String error) {
        showProgress(false);
        showDialog(error);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        String errorMsg = error == null || error.getHead() == null ? NetConfig.DATA_PARSER_ERROR : error.getHead().getRetMsg();
        showProgress(false);
        showDialog(errorMsg);
    }

    /**
     * 进场动画效果，调用startActivity启动activity之后调用，跳转的目标activity的theme里一定要设置
     * <item name="android:windowIsTranslucent">true</item>
     * 不然跳转时会出现黑屏
     */
    protected void startInTransition() {
        overridePendingTransition(R.anim.slide_right_in, 0);
    }

    /**
     * 回退动画效果，调用finish之后调用，谨慎用于直接写在finish()方法里，因为有的是先启动其他页面然后finish
     */
    protected void backOutTransition() {
        overridePendingTransition(0, R.anim.slide_right_out);
    }
}
