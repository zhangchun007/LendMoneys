package com.haiercash.gouhua.activity.gesture;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.utils.UMengUtil;
import com.hunofox.gestures.interfaces.ISetPassword;
import com.hunofox.gestures.widget.GestureContentView;
import com.hunofox.gestures.widget.GestureDrawLine;
import com.hunofox.gestures.widget.LockIndicator;

/**
 * 项目名称：手势密码设置界面
 * 项目作者：胡玉君
 * 创建日期：2016/4/5 10:31.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：未进行md5加密
 * ----------------------------------------------------------------------------------------------------
 */
public abstract class GestureEditActivity extends BaseActivity {
    private boolean mIsFirstInput = true;
    private TextView mTextTip;
    private FrameLayout mGestureContainer;
    private GestureContentView mGestureContentView;
    private String mFirstPassword = null;

    private int count = 4;

    protected ImageView ivBack;
    protected TextView tvSkip;

    @Override
    protected int getLayout() {
        return R.layout.activity_gesture_edit;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        count = getCount();
        if (count < 3 || count >= 9) {
            count = 4;
        }
        setUpViews();
    }

    protected void showHeader(boolean show) {
        try {
            if (show) {
                setTitle(R.string.gesture_verify_set);
                setBarLeftImage(0, v -> finish());
            }
            findViewById(R.id.bar_header).setVisibility(show ? View.VISIBLE : View.GONE);
            findViewById(R.id.v_skip).setVisibility(show ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            //
        }
    }

    /**
     * 得到最少连接个数
     */
    protected abstract int getCount();

    private void setUpViews() {
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> reSetPass());
        tvSkip = findViewById(R.id.tv_skip);
        tvSkip.setOnClickListener(this);

        mTextTip = findViewById(R.id.text_tip);
        mGestureContainer = findViewById(R.id.gesture_container);

        listener = getPasswordListener();

        // 初始化一个显示各个点的viewGroup
        mGestureContentView = new GestureContentView(this, false, "", LoginSelectHelper.showGestureWay(), new MyCallBack());
        // 设置手势解锁显示到哪个布局里面
        mGestureContentView.setParentView(mGestureContainer);
        updateCodeList("");
    }

    /**
     * 设置监听
     */
    private ISetPassword listener;

    protected abstract ISetPassword getPasswordListener();

    class MyCallBack implements GestureDrawLine.GestureCallBack {
        @Override
        public void onGestureCodeInput(String inputCode) {
            if (!isInputPassValidate(inputCode)) {
//                reSetPass();
                mTextTip.setTextColor(0xffff5454);
                mTextTip.setText("请至少连接" + count + "个点");
                Animation shakeAnimation = AnimationUtils.loadAnimation(mContext, R.anim.shake);
                mTextTip.startAnimation(shakeAnimation);
                mGestureContentView.clearDrawlineState(0L);
                if (isSetPage() && mIsFirstInput) {
                    UMengUtil.eventSimpleCompleteWithPageName("SetGesturePasswd1st", getPageCode(), "设置手势密码页", "false", "手势连接错误");
                } else if (isSetPage()) {
                    UMengUtil.eventSimpleCompleteWithPageName("SetGesturePasswd2nd", getPageCode(), "设置手势密码页", "false", "手势连接错误");
                }
                return;
            }
            if (mIsFirstInput) {
                ivBack.setVisibility(View.VISIBLE);
                mFirstPassword = inputCode;
                updateCodeList(inputCode);
                mGestureContentView.clearDrawlineState(0L);
                if (isSetPage()) {
                    UMengUtil.eventSimpleCompleteWithPageName("SetGesturePasswd1st", getPageCode(), "设置手势密码页", "true", "");
                }
            } else {
                if (inputCode.equals(mFirstPassword)) {
                    mGestureContentView.clearDrawlineState(0L);

                    if (listener != null) {
                        listener.success(inputCode);
                    }
                } else {
//                    reSetPass();
                    mTextTip.setTextColor(0xffff5454);
                    mTextTip.setText("两次绘制图形不一致，请重新绘制");
                    // 左右移动动画
                    Animation shakeAnimation = AnimationUtils.loadAnimation(GestureEditActivity.this, R.anim.shake);
                    mTextTip.startAnimation(shakeAnimation);
                    // 保持绘制的线，1.5秒后清除
                    mGestureContentView.clearDrawlineState(1000L);
                    if (isSetPage()) {
                        UMengUtil.eventSimpleCompleteWithPageName("SetGesturePasswd2nd", getPageCode(), "设置手势密码页", "false", "两次绘制图形不一致");
                    }
                    return;
                }
            }
            ivBack.setVisibility(View.VISIBLE);
            mTextTip.setTextColor(0xff666666);
            mTextTip.setText("请再次设置手势密码");
            mIsFirstInput = false;
        }

        @Override
        public void checkedSuccess(String inputCode) {

        }

        @Override
        public void checkedFail(String inputCode) {

        }
    }

    /**
     * 设置上面的小圆点
     */
    private LockIndicator mLockIndicator;

    protected void updateCodeList(String inputCode) {
        mLockIndicator = findViewById(R.id.lock_indicator);
        // 更新选择的图案
        mLockIndicator.setPath(inputCode);
    }

    // 重新设置密码
    protected void reSetPass() {
        mIsFirstInput = true;
        updateCodeList("");
        ivBack.setVisibility(View.INVISIBLE);
        mTextTip.setTextColor(0xff666666);
        mTextTip.setText(getString(R.string.setup_gesture_code));
    }

    //输入密码个数是否正确
    private boolean isInputPassValidate(String inputPassword) {
        return !(TextUtils.isEmpty(inputPassword) || inputPassword.length() < count);
    }

    protected boolean isSetPage() {
        return "setGestures".equals(getIntent().getStringExtra("pageType"));
    }
}
