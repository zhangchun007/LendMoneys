package com.haiercash.gouhua.activity.accountsettings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.gesture.GestureVerifyActivity;
import com.haiercash.gouhua.activity.gesture.GesturesSettingActivity;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.databinding.ActivityGestureManagerBinding;

/**
 * 手势开关信息页面
 */
public class GestureManagerActivity extends BaseActivity {
    private ActivityGestureManagerBinding mGestureManagerBinding;

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return mGestureManagerBinding = ActivityGestureManagerBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.gesture_manager_title);

        mGestureManagerBinding.vGestureSwitch.setOnClickListener(this);
        mGestureManagerBinding.vUpdateGesture.setOnClickListener(this);
        mGestureManagerBinding.vGestureWaySwitch.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LoginSelectHelper.hasSetGesture()) {
            mGestureManagerBinding.ivGestureSwitch.setImageResource(R.drawable.togglebutton_on);
            mGestureManagerBinding.vUpdateGesture.setVisibility(View.VISIBLE);
            mGestureManagerBinding.lineGestureWay.lineRoot.setVisibility(View.VISIBLE);
            mGestureManagerBinding.ivGestureWaySwitch.setImageResource(LoginSelectHelper.showGestureWay() ? R.drawable.togglebutton_on : R.drawable.togglebutton_off);
            mGestureManagerBinding.vGestureWaySwitch.setVisibility(View.VISIBLE);
        } else {
            mGestureManagerBinding.ivGestureSwitch.setImageResource(R.drawable.togglebutton_off);
            mGestureManagerBinding.vGestureWaySwitch.setVisibility(View.GONE);
            mGestureManagerBinding.lineGestureWay.lineRoot.setVisibility(View.GONE);
            mGestureManagerBinding.vUpdateGesture.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mGestureManagerBinding.vGestureSwitch) {
            //手势开关
            if (LoginSelectHelper.hasSetGesture()) {
                Intent intent = new Intent(this, GestureVerifyActivity.class);
                intent.putExtra(GestureVerifyActivity.KEY_TAG, GestureVerifyActivity.KEY_TAG_CLOSE_GESTURE);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, GesturesSettingActivity.class);
                intent.putExtra("pageType", "changeGestures");
                startActivity(intent);
            }
        } else if (v == mGestureManagerBinding.vUpdateGesture) {
            //修改手势
            startActivity(new Intent(this, GestureVerifyActivity.class));
        } else if (v == mGestureManagerBinding.vGestureWaySwitch) {
            //手势轨迹开关
            LoginSelectHelper.saveGestureWayOpenState(!LoginSelectHelper.showGestureWay());
            mGestureManagerBinding.ivGestureWaySwitch.setImageResource(LoginSelectHelper.showGestureWay() ? R.drawable.togglebutton_on : R.drawable.togglebutton_off);
        } else {
            super.onClick(v);
        }
    }
}
