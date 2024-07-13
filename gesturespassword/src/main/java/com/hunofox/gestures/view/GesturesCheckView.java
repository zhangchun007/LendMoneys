package com.hunofox.gestures.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.haiercash.base.utils.handler.CycleHandlerCallback;
import com.hunofox.gestures.interfaces.IAccount;
import com.hunofox.gestures.utils.AccountHelper;
import com.hunofox.gestures.widget.GestureContentView;
import com.hunofox.gestures.widget.GestureDrawLine;
import com.hunofox.gesturesPassword.R;

/**
 * 项目名称：手势密码页面
 * 项目作者：胡玉君
 * 创建日期：2016/4/5 14:42.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class GesturesCheckView {
    private AccountHelper helper = AccountHelper.getInstance();

    private Activity context;
    private GestureContentView gestureContentView;

    public GestureContentView getGestureContentView() {
        return gestureContentView;
    }

    /**
     * 记录输入次数
     */
    private int count = 0;
    private String password;//手势密码

    /**
     * 超出输入次数输入时间延迟
     */
    private int avaliableCount;
    private long delayTime;
    private long increaseTime;
    private long longestTime;
    long coolingTime;
    private boolean isFreeze;//超出输入次数后是否冻结

    private boolean showGestureWay;//显示轨迹
    /**
     * 用显示输入状态文字的TextView
     */
    TextView tv;

    /**
     * 接口判断回调
     */
    private IAccount accountCallBack;

    @SuppressLint("HandlerLeak")
    private Handler hd = new Handler(new CycleHandlerCallback(context) {
        @Override
        public void dispatchMessage(Message msg) {
            setCoolingTime();
        }
    });

    public static boolean freeze_count = true;//该账户是否已被冻结

    public void freeHandler() {
        if (hd != null) {
            hd.removeCallbacksAndMessages(null);
            hd = null;
        }
    }


    /**
     * 构造器
     *
     * @param context        上下文对象
     * @param password       手势密码
     * @param tv             显示密码输入提示的TextView，可以不传
     * @param availableCount 允许输入的次数
     * @param delayTime      超出输入次数后再次输入等待时间
     * @param increaseTime   等待时间增长
     * @param longestTime    最长的等待时间
     * @param isFreeze       超出允许次数后是否冻结
     */
    public GesturesCheckView(Activity context, String password, FrameLayout container,
                             IAccount accountCallBack, TextView tv,
                             int availableCount, long delayTime, long increaseTime, long longestTime,
                             boolean isFreeze, boolean showGestureWay) {
        this.context = context;
        this.tv = tv;
        this.avaliableCount = availableCount;
        this.password = password;
        this.delayTime = delayTime;
        this.coolingTime = delayTime;
        this.increaseTime = increaseTime;
        this.longestTime = longestTime;
        this.isFreeze = isFreeze;
        this.showGestureWay = showGestureWay;
        this.accountCallBack = accountCallBack;

        initView(container);
    }

    public void initView(FrameLayout container) {
        // 初始化一个显示各个点的viewGroup
        MyGestureCallBack callBack = new MyGestureCallBack();
        gestureContentView = new GestureContentView(context, true, password, showGestureWay, callBack);
        // 设置手势解锁显示到哪个布局里面
        gestureContentView.setParentView(container);

        //检查用户账户是否被冻结
        checkAccountIsFreeze();
    }

    /**
     * 检查用户账户是否被冻结
     */
    private void checkAccountIsFreeze() {
        freeze_count = helper.isFreeze(context);
        if (accountCallBack != null) {
            freeze_count = accountCallBack.isFreeze(freeze_count);
        }
        if (freeze_count) {
            freezeCount("账号已被冻结");
        }
    }

    /**
     * 手势密码设置成功
     */
    public void setGestureCheckSuccess(String success) {
        gestureContentView.clearDrawlineState(0L);
        freeze_count = true;
        count = 0;

        setText(true, success);
        count = 0;
        freeze_count = false;
        helper.setCount(context, 0);
        helper.setIsFreeze(context, false);

        if (accountCallBack != null) {
            accountCallBack.setCount(0);
            accountCallBack.freezeCount(false);
        }
    }

    /**
     * 手势密码校验失败
     */
    public void setGestureCheckFailed(String failed) {
        gestureContentView.clearDrawlineState(200L);
        setText(false, failed);
        // 左右移动动画
        if (tv != null) {
            Animation shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.shake);
            tv.startAnimation(shakeAnimation);
        }
    }

    /**
     * 密码输入错误
     */
    public void setGestureInputWrong() {
        count = helper.getCount(context);

        /* 若输入错误次数为0可能是用户清理了缓存数据，因此要请求网络判断 */
        if (accountCallBack != null) {
            count = accountCallBack.getCount(count);
        }
        count++;

        /* 将输入错误的次数上传至网络 */
        if (accountCallBack != null) {
            accountCallBack.setCount(count);
        }

        helper.setCount(context, count);
        gestureContentView.clearDrawlineState(200L);

        int lastCount = avaliableCount - count;
        if (lastCount == 0) {
            coolingTime = delayTime;
        } else {
            coolingTime = delayTime * (0 - lastCount) + increaseTime;
        }

        if (lastCount <= 0) {
            //输入错误次数超出
            gestureContentView.setDrawEnable(false);
            freeze_count = false;
            if (!isFreeze) {
                //超出读秒
                hd.sendEmptyMessage(0);
            } else {
                freezeCount("密码输入错误！");
            }
        } else {
            setText(false, "密码输入错误！");
            freeze_count = false;

            // 左右移动动画
            if (tv != null) {
                Animation shakeAnimation = AnimationUtils.loadAnimation(context, R.anim.shake);
                tv.startAnimation(shakeAnimation);
            }
        }
    }

    /**
     * 手势密码验证后回调
     */
    private class MyGestureCallBack implements GestureDrawLine.GestureCallBack {
        @Override
        public void onGestureCodeInput(String inputCode) {
        }

        @Override
        public void checkedSuccess(String inputCode) {
            if (accountCallBack != null) {
                accountCallBack.success(inputCode);
            }
        }

        @Override
        public void checkedFail(String inputCode) {
            if (accountCallBack != null) {
                accountCallBack.success(inputCode);
            }
        }
    }

    /**
     * 设置TextView
     */
    public void setText(boolean success, String text) {
        if (tv != null) {
            if (text != null && !"".equals(text.trim())) {
                if (success) {
                    tv.setTextColor(0xff666666);
                } else {
                    tv.setTextColor(0xffff5454);
                }
                tv.setVisibility(View.VISIBLE);
                tv.setText(text);
            } else {
                tv.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 设置输入冷却时间，超出最大时间限制冻结账号
     */
    private void setCoolingTime() {
        if (coolingTime > longestTime) {
            freezeCount("密码输入错误，账号已被冻结");
            return;
        }

        if (coolingTime <= 0) {
            gestureContentView.setDrawEnable(true);
            setText(false, null);
            return;
        }
        setText(false, "密码输入错误，请" + (coolingTime / 1000) + "s后重试");
        coolingTime -= 1000;
        hd.sendEmptyMessageDelayed(0, 1000);
    }

    /**
     * 冻结账户
     */
    private void freezeCount(String text) {
        setText(false, text);
        gestureContentView.setDrawEnable(false);
        helper.setCount(context, 0);
        helper.setIsFreeze(context, true);

        if (accountCallBack != null) {
            accountCallBack.freezeCount(true);
        }
    }
}
