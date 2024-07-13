package com.haiercash.gouhua.activity.setting;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.utils.permission.PermissionUtils;
import com.app.haiercash.base.utils.system.PermissionPageUtils;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.databinding.ActivityPermissionsBinding;

/**
 * 设置-权限管理
 */
public class PermissionsActivity extends BaseActivity {
    private ActivityPermissionsBinding mPermissionsBinding;

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return mPermissionsBinding = ActivityPermissionsBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.permissions_manager_title);
        mPermissionsBinding.vLocation.setOnClickListener(this);
        mPermissionsBinding.vCamera.setOnClickListener(this);
        mPermissionsBinding.vContacts.setOnClickListener(this);
        mPermissionsBinding.vCallLog.setOnClickListener(this);
        mPermissionsBinding.vStorage.setOnClickListener(this);
        mPermissionsBinding.vDevice.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPermissionStatus(mPermissionsBinding.tvLocationStatus, PermissionUtils.getRequestPermission(this, getLocationPermissions()));
        setPermissionStatus(mPermissionsBinding.tvCameraStatus, PermissionUtils.getRequestPermission(this, getCameraPermissions()));
        setPermissionStatus(mPermissionsBinding.tvContactsStatus, PermissionUtils.getRequestPermission(this, getContactsPermissions()));
        setPermissionStatus(mPermissionsBinding.tvCallLogStatus, PermissionUtils.getRequestPermission(this, getCallLogPermissions()));
        setPermissionStatus(mPermissionsBinding.tvStorageStatus, PermissionUtils.getRequestPermission(this, getStoragePermissions()));
        setPermissionStatus(mPermissionsBinding.tvDeviceStatus, PermissionUtils.getRequestPermission(this, getDevicePermissions()));
    }

    @Override
    public void onClick(View v) {
        if (v == mPermissionsBinding.vLocation) {
            //定位权限
            clickPermissionV(getLocationPermissions());
        } else if (v == mPermissionsBinding.vCamera) {
            //相机权限
            clickPermissionV(getCameraPermissions());
        } else if (v == mPermissionsBinding.vContacts) {
            //读取通讯录权限
            clickPermissionV(getContactsPermissions());
        } else if (v == mPermissionsBinding.vCallLog) {
            //读取通话记录权限
            clickPermissionV(getCallLogPermissions());
        } else if (v == mPermissionsBinding.vStorage) {
            //相册/存储权限
            clickPermissionV(getStoragePermissions());
        } else if (v == mPermissionsBinding.vDevice) {
            //设备权限
            clickPermissionV(getDevicePermissions());
        } else {
            super.onClick(v);
        }
    }

    private void clickPermissionV(String... permissions) {
        //权限已授权并且点击按钮去系统设置页面，或者已拒绝授权且不再询问，也是去系统设置页面
        if (PermissionUtils.getRequestPermission(this, permissions) || isRefusePermanent(permissions)) {
            PermissionPageUtils.jumpPermissionPage(this, BuildConfig.APPLICATION_ID);
        } else {
            disposablePermissionRequestEachCombined(permission -> {
            }, permissions);
        }
    }

    private void setPermissionStatus(TextView statusTv, boolean hasPermission) {
        try {
            statusTv.setText(hasPermission ? R.string.permissions_manager_allow : R.string.permissions_manager_set);
            statusTv.setTextColor(hasPermission ? 0xff909199 : 0xff303133);
        } catch (Exception e) {
            //
        }
    }

    private String[] getLocationPermissions() {
        return new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    }

    private String[] getCameraPermissions() {
        return new String[]{Manifest.permission.CAMERA};
    }

    private String[] getContactsPermissions() {
        return new String[]{Manifest.permission.READ_CONTACTS};
    }

    private String[] getCallLogPermissions() {
        return new String[]{Manifest.permission.READ_CALL_LOG};
    }

    private String[] getStoragePermissions() {
        return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private String[] getDevicePermissions() {
        return new String[]{Manifest.permission.READ_PHONE_STATE};
    }
}
