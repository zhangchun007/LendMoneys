package com.haiercash.gouhua.uihelper;

import android.Manifest;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.Downloader;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.login.VersionInfo;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/10/13
 * 描    述：
 * 修订历史：
 * ================================================================
 */
public class VersionHelper {
    private static final int REQUEST_PERMISSION = 0x10;
    private BaseActivity mActivity;
    private View popView;
    private OnVersionBackListener onVersionBackListener;
    private NetHelper netHelper;

    //判断是否忽略一天之内不提醒
    private boolean isShowRemind = true;

    public static final int VERSION_CHECKED_ERROR = 0x01; //检查版本失败
    public static final int VERSION_CHECKED_SUCC = 0x02; //检查版本失败
    public static final int VERSION_SUCCEED_NOUPDATE = 0x03;  //版本检查成功
    public static final int VERSION_SUCCEED_CANCLE = 0x04;  //取消下载
    public static final int VERSION_SUCCEED_UPDATE = 0x05;  //开始下载

    public static final int VERSION_CHECKED_ERROR_NO_BLOCK = 0x06;  //版本检测接口非阻塞性报错,放开强校验,进入首页
    public static boolean HAS_SHOW_UPDATE = true;


    public VersionHelper(BaseActivity mActivity, View view, OnVersionBackListener onVersionBackListener) {
        this(mActivity, view, false, onVersionBackListener);
    }

    public VersionHelper(BaseActivity mActivity, View view, boolean isShowRemind, OnVersionBackListener onVersionBackListener) {
        this.mActivity = mActivity;
        this.isShowRemind = isShowRemind;
        this.popView = view;
        this.onVersionBackListener = onVersionBackListener;
        netHelper = new NetHelper(netResultCallBack);
    }


    public void startCheckVersionService() {
        SpHp.deleteOther(SpKey.OTHER_VERSION_NUMBER);
        Map<String, String> map = new HashMap<>();
        map.put("sysVersion", "android");//系统分类
        map.put("versionType", SystemUtils.metaDataValueForTDChannelId(mActivity));//版本分类
        map.put("version", SystemUtils.getAppVersion(mActivity));//当前版本号
        netHelper.postService(ApiUrl.APP_MANAGE_VERSION_CHECK, map, VersionInfo.class);
    }

    @SuppressWarnings("FieldCanBeLocal")
    private INetResult netResultCallBack = new INetResult() {
        @Override
        public void onSuccess(final Object response, final String flag) {
            final VersionInfo vInfo = (VersionInfo) response;
            if (vInfo == null) {
                if (onVersionBackListener != null) {
                    onVersionBackListener.onVersionBack(VERSION_CHECKED_ERROR_NO_BLOCK,null);
                }
                return;
            }
            if ("Y".equals(vInfo.getIsFix())) {
                if (onVersionBackListener != null) {
                    onVersionBackListener.onVersionBack(VERSION_CHECKED_ERROR, new BasicResponse("isFix", "系统维护中，系统维护时间为" + vInfo.getBeginTime() + "至" + vInfo.getEndTime()));
                }
                return;
            }
            if (!vInfo.isHasNewer()) {//是否有更新版本
                // 无新版本
                if (onVersionBackListener != null) {
                    onVersionBackListener.onVersionBack(VERSION_SUCCEED_NOUPDATE, null);
                }
                return;
            }
            SpHp.saveSpOther(SpKey.OTHER_VERSION_NUMBER, ((VersionInfo) response).getLastVersion());
            if (vInfo.isForcedUpdate()) {//强制版本更新
                onVersionBackListener.onVersionBack(VERSION_CHECKED_SUCC, vInfo);
            } else if (isGoVersionUpdate() || isShowRemind) {//非强制更新
                onVersionBackListener.onVersionBack(VERSION_CHECKED_SUCC, vInfo);
            } else if (onVersionBackListener != null) {
                onVersionBackListener.onVersionBack(VERSION_SUCCEED_CANCLE, vInfo.getLastVersion());
            }
        }

        @Override
        public void onError(BasicResponse error, String url) {
            if (onVersionBackListener != null) {
                onVersionBackListener.onVersionBack(VERSION_CHECKED_ERROR_NO_BLOCK, error);
            }
        }
    };

    /**
     * 弹窗新公告
     */
    public void showNewUpdateDialog(final BaseActivity activity, final VersionInfo vInfo) {
        UpdatePopupWindow updatePopupWindow = new UpdatePopupWindow(activity, vInfo, (view, flagTag, obj) -> {
            if (flagTag == UpdatePopupWindow.UPDATE_CANCLE) {
                if (onVersionBackListener != null) {
                    onVersionBackListener.onVersionBack(VERSION_SUCCEED_CANCLE, vInfo.getLastVersion());
                }
            } else if (flagTag == UpdatePopupWindow.UPDATE_CONFIRM) {
                requestPermissionAndDownLoadApk(activity);
            }
        });
        updatePopupWindow.showAtLocation(popView);
    }


    private void requestPermissionAndDownLoadApk(BaseActivity activity) {
        activity.requestPermission((Consumer<Boolean>) aBoolean -> {
            if (aBoolean) {
                downLoadApk();
            } else {
                //因各厂商权限设置位置不同，目前只做提示，不做跳转
                onVersionBackListener.onVersionBack(VERSION_CHECKED_ERROR, new BasicResponse("", "请授权手机存储权限"));
            }
        }, R.string.permission_storage, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 显示下载提示框
     */
    public void showUpdateDialog(final VersionInfo versionInfo) {
        mActivity.showDialog(mActivity.getString(R.string.remindupdate), "取消", "立即升级", (dialog, which) -> {
            if (which == 1) {
                if (onVersionBackListener != null) {
                    onVersionBackListener.onVersionBack(VERSION_SUCCEED_CANCLE, versionInfo.getLastVersion());
                }
            } else {
                requestPermissionAndDownLoadApk(mActivity);
            }

        });
    }


    private void downLoadApk() {
        if (onVersionBackListener != null) {
            onVersionBackListener.onVersionBack(VERSION_SUCCEED_UPDATE, null);
        }
        String app_update_url = ApiUrl.url_app_update + "?sysVersion=android&versionType=" +
                SystemUtils.metaDataValueForTDChannelId(mActivity) + "&version=" + SystemUtils.getAppVersion(mActivity);
        Downloader.enqueueOnly(mActivity, app_update_url, () -> {
        });
    }


    /**
     * 判断当前版本是否最新版本
     */
    public static boolean isLastVersion(Context context) {
        String lastVersion = SpHp.getOther(SpKey.OTHER_VERSION_NUMBER);
        return CheckUtil.isEmpty(lastVersion) || lastVersion.equals(SystemUtils.getAppVersion(context));
    }

    /**
     * 当前Activity不在存活
     */
    private boolean isActivityNotAlive() {
        return mActivity == null || mActivity.isDestroyed() || mActivity.isFinishing();
    }

    public interface OnVersionBackListener {
        /**
         * 版本检查回调
         *
         * @param versionStatus 检查状态 见VersionHelper
         * @param response      描述，错误时用
         */
        void onVersionBack(int versionStatus, Object response);
    }


    public static void cancelVersionUpdate() {
        String currentTimestamp = String.valueOf(TimeUtil.currentTimestamp());
        SpHp.saveSpOther(SpKey.VERSION_CANCEL_UPDATE_TIME, currentTimestamp);
    }

    private static boolean isGoVersionUpdate() {
        String time = SpHp.getOther(SpKey.VERSION_CANCEL_UPDATE_TIME);
        if (TextUtils.isEmpty(time)) {
            return true;
        }
        try {
            long lastTime = Long.parseLong(time);
            long currentTime = TimeUtil.currentTimestamp();
            long diff = currentTime - lastTime;
            if (diff < 86400000)  //一天之内去不更新
            {
                return false;
            }
        } catch (NumberFormatException e) {
            Log.e("NumberFormatException", "isGoVersionUpdate: ", e);
        }
        return true;
    }

    public void clearnHelper() {
        mActivity = null;
        netResultCallBack = null;
        onVersionBackListener = null;
    }
}
