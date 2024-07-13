package com.haiercash.gouhua.activity.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.utils.system.PermissionPageUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.databinding.ActivityMsgSettingBinding;
import com.haiercash.gouhua.utils.NotificationsUtils;

/**
 * 设置-消息通知管理
 */
public class MsgSettingActivity extends BaseActivity {
    private ActivityMsgSettingBinding mMsgSettingBinding;

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return mMsgSettingBinding = ActivityMsgSettingBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.msg_manager_title);
        mMsgSettingBinding.llMsgSwitch.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionPageUtils.isNotificationEnable(this)) {
            mMsgSettingBinding.ivMsgSwitch.setImageResource(R.drawable.togglebutton_on);
        } else {
            mMsgSettingBinding.ivMsgSwitch.setImageResource(R.drawable.togglebutton_off);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mMsgSettingBinding.llMsgSwitch) {
            if (PermissionPageUtils.isNotificationEnable(this)) {
                showDialog(getString(R.string.care_notice), getString(R.string.msg_manager_dialog_tip),
                        getString(R.string.msg_manager_to_set),
                        getString(R.string.msg_manager_no_close),
                        (dialog, which) -> {
                            if (which == 1) {
                                NotificationsUtils.toSetting(this);
                            }
                        }).setStandardStyle(3);
            } else {
                NotificationsUtils.toSetting(this);
            }
        } else {
            super.onClick(v);
        }
    }
}
