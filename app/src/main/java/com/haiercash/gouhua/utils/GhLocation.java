package com.haiercash.gouhua.utils;

import android.Manifest;
import android.content.Context;

import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.permission.PermissionUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;

import io.reactivex.functions.Consumer;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/5/20<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class GhLocation {
    private BaseActivity mActivity;
    private Context mContext;
    private LocationUtils locationUtils;
    /**
     * 定位后（无论成功或者失败），停止定位
     */
    private boolean isLocationThenStop;
    private ILocationCallBack callBack;

    public GhLocation(BaseActivity mActivity, ILocationCallBack callBack) {
        this.mActivity = mActivity;
        this.mContext = mActivity;
        this.callBack = callBack;
    }

    public GhLocation(BaseActivity mActivity, boolean isLocationThenStop, ILocationCallBack callBack) {
        this.mActivity = mActivity;
        this.mContext = mActivity;
        this.isLocationThenStop = isLocationThenStop;
        this.callBack = callBack;
    }

    public GhLocation(Context context, boolean isLocationThenStop, ILocationCallBack callBack) {
        this.mContext = context;
        this.isLocationThenStop = isLocationThenStop;
        this.callBack = callBack;
    }

    public void setCallBack(ILocationCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 在首页获取定位权限 SD卡权限
     */
    public void requestLocation() {
        mActivity.requestPermission(((Consumer<Boolean>) aBoolean -> {
            Logger.e(mActivity.getClass().getSimpleName() + ":系统权限回调。。。");
            if (aBoolean) {
                location();
            } else {
                if (callBack != null) {
                    callBack.callBack(false, LocationUtils.LOCATION_ERROR_PERMISSIN);
                } else {
                    if (!mActivity.isShowingDialog()) {
                        mActivity.showDialog(LocationUtils.LOCATION_ERROR_PERMISSIN);
                    }
                }
            }
        }), R.string.permission_location, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }


    public void requestLocationNoPermission() {
        if (PermissionUtils.getRequestPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            location();
        } else if (callBack != null) {
            callBack.callBack(false, LocationUtils.LOCATION_ERROR_PERMISSIN);
        }
    }

    /**
     * 进行定位
     */
    private void location() {
        locationUtils = new LocationUtils(mContext);
        locationUtils.startLocation(new LocationUtils.OnLocationListener() {
            @Override
            public void onErr(String reason) {
                if (callBack != null) {
                    callBack.callBack(false, reason);
                } else if (mActivity != null) {
                    mActivity.showDialog(reason);
                }
                if (isLocationThenStop) {
                    locationUtils.stopLocation();
                }
            }

            @Override
            public void onSuccess() {
                if (callBack != null) {
                    callBack.callBack(true, "定位成功");
                }
                Logger.e("定位成功");
                if (isLocationThenStop) {
                    locationUtils.stopLocation();
                }
            }
        });
    }

    public interface ILocationCallBack {
        void callBack(boolean isSuccess, String reason);
    }

    public void onDestroy() {
        if (locationUtils != null) {
            locationUtils.stopLocation();
        }
    }
}
