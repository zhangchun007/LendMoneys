package com.haiercash.gouhua.tplibrary;

import android.os.Handler;
import android.os.Message;

import com.app.haiercash.base.utils.handler.CycleHandlerCallback;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.megvii.idcardlib.utils.Configuration;
import com.megvii.idcardquality.IDCardQualityLicenseManager;
import com.megvii.licensemanager.Manager;
import com.networkbench.agent.impl.NBSAppAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/6/4<br/>
 * 描    述：Face++、OCR 授权辅助类<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class FaceOcrHelper {
    private BaseActivity mActivity;
    private Handler cycleHandler;
    /**
     * true OCR授权，false Face++授权
     */
    private boolean isOcr;
    private int remindNumber = 0;
    private IDCardQualityLicenseManager mIdCardLicenseManager;

    public FaceOcrHelper(BaseActivity mActivity, boolean isOcr, IFaceOrcCallBack callBack) {
        this.mActivity = mActivity;
        this.isOcr = isOcr;
        cycleHandler = new Handler(new CycleHandlerCallback(mActivity) {
            private String message = isOcr ? "身份证扫描授权失败，请检查您的网络环境后重试" : "人脸识别授权失败，请检查您的网络环境后重试";

            @Override
            public void dispatchMessage(Message msg) {
                mActivity.showProgress(false);
                if (msg.what < 0) {
                    if (remindNumber >= 3) {
                        mActivity.showDialog("授权失败", message, "重新授权", "退出重试", (dialog, which) -> {
                            dialog.dismiss();
                            if (which == 1) {
                                authorizeSdk();
                            } else if (which == 2) {
                                mActivity.onBackPressed();
                            }
                        }).setStandardStyle(3);
                    } else {
                        mActivity.showDialog("授权失败", message, "重新授权", null, (dialog, which) -> {
                            dialog.dismiss();
                            authorizeSdk();
                        }).setButtonTextColor(1, R.color.colorPrimary);
                    }
                } else if (callBack != null) {
//                    if (!BuildConfig.IS_RELEASE) {
//                        UiUtil.toast((isOcr ? "身份证" : "人脸识别") + "授权" + (msg.what > 0 ? "成功" : "失败"));
//                    }
                    callBack.callBack(msg.what > 0);
                }
            }
        });
        authorizeSdk();
    }

    /**
     * 启动授权
     */
    private void authorizeSdk() {
        mActivity.showProgress(true);
        remindNumber++;
        mIdCardLicenseManager = new IDCardQualityLicenseManager(mActivity);

        long status = 0;
        try {
            status = mIdCardLicenseManager.checkCachedLicense();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (status > 0) {//大于0，已授权或者授权未过期
            cycleHandler.sendEmptyMessage(1);

        } else {

            ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>());
            executorService.submit(() -> {
                //参数2 是否使用ipv6
                Manager manager = new Manager(mActivity, true);
                manager.registerLicenseManager(mIdCardLicenseManager);
                String uuid = Configuration.getUUID(mActivity);
                String authMsg = mIdCardLicenseManager.getContext(uuid);
                manager.takeLicenseFromNetwork(authMsg);
                if (mIdCardLicenseManager.checkCachedLicense() > 0) {//授权成功
                    cycleHandler.sendEmptyMessage(1);
                } else {//授权失败
                    cycleHandler.sendEmptyMessage(-1);
                    Map<String, Object> map = new HashMap<>();
                    map.put("page", "旷世初始化失败");
                    map.put("message", authMsg);
                    map.put("uuid", uuid);
                    map.put("version",  mIdCardLicenseManager.getVersion());
                    map.put("userId", AppApplication.userid);
                    NBSAppAgent.reportError("发生异常", map);
                }
            });
        }
    }

    public void onDestroy() {
        if (cycleHandler != null) {
            cycleHandler.removeCallbacksAndMessages(null);
        }
    }

    public interface IFaceOrcCallBack {
        void callBack(boolean authorize);
    }
}
