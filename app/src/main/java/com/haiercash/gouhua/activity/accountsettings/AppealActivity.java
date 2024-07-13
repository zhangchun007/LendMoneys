package com.haiercash.gouhua.activity.accountsettings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.databinding.ActivityAppealBinding;
import com.haiercash.gouhua.fragments.mine.AppealRealNameFragment;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 账号申诉
 */
public class AppealActivity extends BaseActivity {
    /**
     * 来自哪里的申诉流程入口
     * 登录后新设备/超三个月登录设备绑定接口错误2次以上弹窗申诉入口："NewDeviceError"
     */
    public static final String FROM = "APPEAL_FROM";
    public static final String NEW_DEVICE_ERROR = "NewDeviceError";
    private String mFrom;//对应FROM携带的参数
    private String mCertName, mCertNo;//步骤一所填写的姓名和身份证号码

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return ActivityAppealBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.appeal_title);
        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            mFrom = extraIntent.getStringExtra(FROM);
        }
        setFragment(new AppealRealNameFragment());
    }

    public String getFrom() {
        return mFrom;
    }

    public void setRealNameInfo(String certName, String certNo) {
        this.mCertName = certName;
        this.mCertNo = certNo;
    }

    public String getCertName() {
        return mCertName;
    }

    public String getCertNo() {
        return mCertNo;
    }

    public void setFragment(@NonNull BaseFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_container, fragment).commitAllowingStateLoss();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            //点击editText控件外部，软键盘消失且editText失焦
            if (isShouldHideInput(v, ev)) {
                UiUtil.hideKeyBord(this);
                if (editText != null) {
                    editText.clearFocus();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    EditText editText = null;

    private boolean isShouldHideInput(View v, MotionEvent event) {
        //点击editText控件外部，且清空类按钮之外
        if (v instanceof EditText) {
            editText = (EditText) v;
            if (mTouchOutOfViewList != null && mTouchOutOfViewList.size() > 0) {
                for (View view : mTouchOutOfViewList) {
                    if (!UiUtil.isTouchOutOfV(view, event)) {
                        return false;
                    }
                }
            }
            return UiUtil.isTouchOutOfV(v, event);
        }
        return false;
    }

    public ArrayList<View> mTouchOutOfViewList;

    public void addTouchOutOfViewList(View... v) {
        if (mTouchOutOfViewList == null) {
            mTouchOutOfViewList = new ArrayList<>();
        } else {
            mTouchOutOfViewList.clear();
        }
        if (v != null && v.length > 0) {
            mTouchOutOfViewList.addAll(Arrays.asList(v));
        }
    }
}
