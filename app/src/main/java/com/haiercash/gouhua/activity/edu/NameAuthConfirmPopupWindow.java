package com.haiercash.gouhua.activity.edu;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.login.OnInputListener;
import com.haiercash.gouhua.activity.login.SplitEditTextView;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.haiercash.gouhua.sms.SmsTimePresenter;
import com.haiercash.gouhua.sms.SmsTimeView;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.DIYKeyboardView;
import com.haiercash.gouhua.view.NumBerKeyBoard;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: Sun<br/>
 * Date :    2018/6/12<br/>
 * FileName: NameAuthConfirmPopupWindow<br/>
 * Description:<br/>
 */
public class NameAuthConfirmPopupWindow extends BasePopupWindow implements SmsTimeView.OnSmsFinish, SmsTimeView.OnSmsTick, OnInputListener {
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.ll_tips)
    LinearLayout llTips;
    @BindView(R.id.splitEdit)
    SplitEditTextView splitEdit;

    @BindView(R.id.tv_tips)
    TextView tvTips;

    @BindView(R.id.tv_countdown)
    SmsTimeView tvCountdown;    //倒计时

    @BindView(R.id.tv_error_message)
    TextView tvErrorMessage;
    @BindView(R.id.keyboard_view)
    DIYKeyboardView keyboardView;
    @BindView(R.id.tv_inter_name)
    TextView tvInterName;

    public static long lastTime = -1;
    private NameAuthCallBack authCallBack;
    private NumBerKeyBoard numBerKeyBoard;
    private SmsTimePresenter presenter;

    public NameAuthConfirmPopupWindow(BaseActivity context, Object data) {
        super(context, data);
    }

    @Override
    protected int getLayout() {
        return R.layout.pop_nameauth_confirm;
    }

    @Override
    protected void onViewCreated(Object data) {
        setClippingEnabled(false);//取消限制边框，使其与屏幕一样（扩展到包含状态栏和导航栏位置）
        setPopupOutsideTouchable(true);
        tv_title.setTypeface(FontCustom.getMediumFont(mActivity));
        numBerKeyBoard = new NumBerKeyBoard(mActivity, 1);
        numBerKeyBoard.setHighEditCount(6);
        numBerKeyBoard.setKeyboardView(keyboardView);
        numBerKeyBoard.attachTo(splitEdit);
        splitEdit.setOnClickListener(v -> numBerKeyBoard.attachTo(splitEdit));
        splitEdit.setOnInputListener(this);
        //确定键
        numBerKeyBoard.setOnOkClick(() -> {
            String password = splitEdit.getText() != null ? splitEdit.getText().toString() : "";
            if (password.length() != 6) {
                UiUtil.toast("请填写正确的验证码");
            } else if (authCallBack != null) {
                authCallBack.updateSmsCode(password);
            }
        });
        //加载验证码模块
        presenter = SmsTimePresenter.getSmsTime(mActivity, tvCountdown)
                .setOnSmsTickListener(this);
        presenter.setSmsFinish(this);
        presenter.setAutoSendSms(false).setOnClick(() -> {
            if (tvCountdown != null) {//初始化倒计时
                tvCountdown.onFinish();
            }
            clearKeyNumber();
            try {//此聚焦，是为了自动提示截取到的短信验证码
                if (isShowing() && splitEdit != null) {
                    splitEdit.requestFocus();
                }
            } catch (Exception e) {
                //
            }
            if (authCallBack != null) {
                authCallBack.retryRequestSign();
            }
        });
        if (data != null) {
            //noinspection unchecked
            Map<String, String> map = (Map<String, String>) data;
            updateView(map);
        }
    }

    @Override
    public void dismiss() {
        clearKeyNumber();
        super.dismiss();
    }

    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            dismiss();
        }
    }

    /**
     * 更新数据
     */
    public void updateView(Map<String, String> data) {
        if (data == null) {
            return;
        }
        String interName = UiUtil.getEmptyStr(data.containsKey("cardInterName") ? data.get("cardInterName") : null);
        if (CheckUtil.isEmpty(interName)) {
            llTips.setVisibility(View.GONE);
        } else {
            tvInterName.setText(mActivity.getString(R.string.common_sms_inter_tips, interName));
            llTips.setVisibility(View.VISIBLE);
        }
        tvTips.setText(mActivity.getString(R.string.common_sms_tips, CheckUtil.hidePhoneNumber(data.get("cardMobile"))));
        if (presenter != null) {
            presenter.setPhoneNum(data.get("cardMobile"));
        }
        String message = data.get("message");
        if (!TextUtils.isEmpty(message)){
            UiUtil.toast(message);
        }
    }

    public void onErrorCallBack(String errMsg) {
        isCheckError = true;
        try {
            splitEdit.setUnderlineErrorColor(0xFFFF5151, 0xFFFF5151);
            tvErrorMessage.setText(CheckUtil.isEmpty(errMsg) ? "验证码错误" : errMsg);
            tvErrorMessage.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            //
        }
    }

    /**
     * 清空验证码
     */
    public void clearKeyNumber() {
        if (splitEdit != null) {
            splitEdit.setText(null);
        }
    }

    /**
     * 点击按钮发送验证码
     */
    public void clickSmsCodeAndSend() {
        if (lastTime == -1) {
            needClickSmsCodeAndSend();
        }
    }

    /**
     * 需要点击按钮发送验证码，需要重发
     */
    public void needClickSmsCodeAndSend() {
        if (tvCountdown != null) {
            tvCountdown.callOnClick();
        }
    }

    /**
     * 开始倒计时
     */
    public void startTimer() {
        if (tvCountdown != null) {
            tvCountdown.startTime();
        }
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    public void setNameAuthCallBack(NameAuthCallBack callBack) {
        authCallBack = callBack;
    }

    public static long getLastTime() {
        return lastTime;
    }

    @Override
    public void onDestroy() {
        //停止短信验证码倒计时
        if (presenter != null) {
            presenter.stopTime();
        }
        lastTime = -1;
        super.onDestroy();
    }

    @Override
    public void onFinish() {
        lastTime = -1;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        lastTime = millisUntilFinished;
    }

    @Override
    public void onInputFinished(String content) {
        if (authCallBack != null) {
            authCallBack.updateSmsCode(content);
        }
    }

    @Override
    public void onInputChanged(String text) {
        if (isCheckError) {
            isCheckError = false;
            splitEdit.setText("");
            splitEdit.setUnderlineErrorColor(Color.parseColor("#E8EAEF"), Color.parseColor("#606166"));
            tvErrorMessage.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isCheckError = false;

    public interface NameAuthCallBack {
        /**
         * 重新发送验证码
         */
        void retryRequestSign();

        /**
         * 确认完成
         *
         * @param code 验证码
         */
        void updateSmsCode(String code);

    }
}
