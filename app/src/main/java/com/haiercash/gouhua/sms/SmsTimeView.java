package com.haiercash.gouhua.sms;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * Author: Sun<br/>
 * Date :    2018/1/25<br/>
 * FileName: SMSTimeView<br/>
 * Description:<br/>
 */

public class SmsTimeView extends AppCompatTextView {

    private SmSCountDownTimer countDownTimer;
    private String pre = "发送验证码";
    private String resetText = "重新获取";
    private int highTextColor = 0xff1555ff;
    private int enableTextColor = 0xff999999;
    private int mTimeCount = 60;
    private String afterTimerText = " s";//倒计时后文案
    private OnSmsFinish onSmsFinish;
    private OnSmsTick onSmsTick;

    public SmsTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SmsTimeView);
            String normalText = ta.getString(R.styleable.SmsTimeView_normalText);
            if (!CheckUtil.isEmpty(normalText)) {
                pre = normalText;
            }
            String refreshText = ta.getString(R.styleable.SmsTimeView_refreshText);
            if (!CheckUtil.isEmpty(refreshText)) {
                resetText = refreshText;
            }
            String afterTimerText = ta.getString(R.styleable.SmsTimeView_afterTimerText);
            if (!CheckUtil.isEmpty(afterTimerText)) {
                this.afterTimerText = afterTimerText;
            }
            mTimeCount = ta.getInt(R.styleable.SmsTimeView_timeCount, mTimeCount);
            highTextColor = ta.getColor(R.styleable.SmsTimeView_highTextColor, highTextColor);
            enableTextColor = ta.getColor(R.styleable.SmsTimeView_enableTextColor, enableTextColor);
            ta.recycle();
        }

        init(pre);
    }


    private void init(CharSequence text) {
        this.setEnabled(true);
        setText(text);
        setTextColor(highTextColor);
    }

    public void onFinish() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        init(pre);
    }

    public void startTime() {
        if (countDownTimer == null) {
            long millisInFuture = mTimeCount * 1000L;
            long countDownInterval = 1000;
            countDownTimer = new SmSCountDownTimer(millisInFuture, countDownInterval);
        } else {
            countDownTimer.cancel();
            init(pre);
        }
        countDownTimer.start();
    }

    public void setOnSmsFinish(OnSmsFinish smsFinish) {
        onSmsFinish = smsFinish;
    }

    public void setOnSmsTick(OnSmsTick smsTick) {
        onSmsTick = smsTick;
    }

    private void setTimeText(long lastTime) {
        this.setEnabled(false);
        setText(UiUtil.getStr(lastTime / 1000, afterTimerText));

        setTextColor(enableTextColor);
    }

    class SmSCountDownTimer extends CountDownTimer {

        SmSCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (0 == millisUntilFinished / 1000) {
                onFinish();
            } else {
                setTimeText(millisUntilFinished);
                if (onSmsTick != null) {
                    onSmsTick.onTick(millisUntilFinished);
                }
            }
        }

        @Override
        public void onFinish() {
            init(resetText);
            if (onSmsFinish != null) {
                onSmsFinish.onFinish();
            }
        }
    }

    public interface OnSmsFinish {
        /**
         * 短信倒计数结束
         */
        void onFinish();
    }

    public interface OnSmsTick {
        /**
         * 短信倒计数走动中,millisUntilFinished剩余时间
         */
        void onTick(long millisUntilFinished);
    }
}
