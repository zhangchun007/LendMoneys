package com.haiercash.gouhua.biometriclib;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/3/27<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BiometricPromptDialog extends DialogFragment {
    public static final int STATE_NORMAL = 1;
    public static final int STATE_FAILED = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_SUCCEED = 4;
    private LinearLayout llMsg;
    private TextView tvTitle;
    private TextView tvContent;

    private LinearLayout llFingerprint;
    private TextView mStateTv;
    private TextView mUsePasswordBtn;
    private TextView mCancelBtn;
    private Activity mActivity;
    private int authenticateNumber = 0;
    private OnBiometricPromptDialogActionCallback mDialogActionCallback;

    public interface OnBiometricPromptDialogActionCallback {
        void onDialogDismiss();

        void onUsePassword();

        void onCancel();
    }

    public static BiometricPromptDialog newInstance() {
        return new BiometricPromptDialog();
    }

    public void setOnBiometricPromptDialogActionCallback(OnBiometricPromptDialogActionCallback callback) {
        mDialogActionCallback = callback;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupWindow(getDialog().getWindow());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_biometric_prompt_dialog, container);
        RelativeLayout rootView = view.findViewById(R.id.root_view);
        rootView.setClickable(false);
        View viewContent = view.findViewById(R.id.ll_prompt);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
//        drawable.setGradientType(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(UiUtil.dip2px(mActivity, 13));
        drawable.setColor(ContextCompat.getColor(mActivity, R.color.white));
        viewContent.setBackground(drawable);

        llMsg = view.findViewById(R.id.ll_msg);
        tvTitle = view.findViewById(R.id.tv_title);
        tvContent = view.findViewById(R.id.tv_content);

        llFingerprint = view.findViewById(R.id.ll_fingerprint);
        mStateTv = view.findViewById(R.id.state_tv);
        mUsePasswordBtn = view.findViewById(R.id.use_password_btn);
        mCancelBtn = view.findViewById(R.id.cancel_btn);
        mUsePasswordBtn.setVisibility(View.GONE);
        mUsePasswordBtn.setOnClickListener(view12 -> {
            if (mDialogActionCallback != null) {
                mDialogActionCallback.onUsePassword();
            }
            dismiss();
        });
        mCancelBtn.setOnClickListener(view1 -> {
            if (mDialogActionCallback != null) {
                mDialogActionCallback.onCancel();
            }
            dismiss();
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent99);
        }
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        authenticateNumber = 0;
        if (mDialogActionCallback != null) {
            mDialogActionCallback.onDialogDismiss();
        }
    }

    private void setupWindow(Window window) {
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.dimAmount = 0;
            lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(lp);
            window.setBackgroundDrawableResource(R.color.transparent99);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    public void setState(int state) {
        setState(state, 0, null);
    }

    public void setState(int state, int errorCode, String reason) {
        llMsg.setVisibility(View.GONE);
        llFingerprint.setVisibility(View.VISIBLE);
        switch (state) {
            case STATE_NORMAL:
                //mStateTv.setTextColor(ContextCompat.getColor(mActivity, R.color.text_quaternary));
                //mStateTv.setText(mActivity.getString(R.string.biometric_dialog_state_normal));
                //mStateTv.setTextColor(Color.parseColor("#CDCED0"));
                mStateTv.setText(CheckUtil.isEmpty(reason) ? "请验证指纹" : reason);
                mCancelBtn.setVisibility(View.VISIBLE);
                mUsePasswordBtn.setVisibility(View.GONE);
                break;
            case STATE_FAILED:
                authenticateNumber++;
                //mStateTv.setTextColor(ContextCompat.getColor(mActivity, R.color.text_red));
                //mStateTv.setText(mActivity.getString(R.string.biometric_dialog_state_failed));
                //mStateTv.setTextColor(Color.parseColor("#FF5555"));
                //mStateTv.setText(CheckUtil.isEmpty(reason) ? "指纹不匹配" : reason);
                if (authenticateNumber == 1) {
                    mStateTv.setText("再试一次");
                } else if (authenticateNumber == 2) {
                    mStateTv.setText("请再试一次");
                } else {
                    mStateTv.setText(CheckUtil.isEmpty(reason) ? "指纹不匹配" : reason);
                }
                mCancelBtn.setVisibility(View.VISIBLE);
                mUsePasswordBtn.setVisibility(View.GONE);
//                if (authenticateNumber >= 5) {
//                    llMsg.setVisibility(View.VISIBLE);
//                    llFingerprint.setVisibility(View.GONE);
//                    tvTitle.setText("指纹验证失败");
//                    tvContent.setText("验证失败次数过多，请稍后重试～");
//                }
                break;
            case STATE_ERROR:
                //5-取消，7-五次及以上失败，由于弹窗文案不一样，所以这里不处理，调用地各自处理
                //9-失败过多导致传感器停用
                dismissAllowingStateLoss();
//                dismiss();
                //mStateTv.setTextColor(ContextCompat.getColor(mActivity, R.color.text_red));
                //mStateTv.setText(mActivity.getString(R.string.biometric_dialog_state_error));
                //mStateTv.setTextColor(Color.parseColor("#FF5555"));
//                    mStateTv.setText(CheckUtil.isEmpty(reason) ? "验证错误" : reason);
                //mUsePasswordBtn.setVisibility(View.VISIBLE);
                //mCancelBtn.setVisibility(View.VISIBLE);

//                    llMsg.setVisibility(View.VISIBLE);
//                    llFingerprint.setVisibility(View.GONE);
//                    tvTitle.setText("指纹验证失败");
//                    tvContent.setText(CheckUtil.isEmpty(reason) ? "指纹验证失败次数过多，请稍后重试" : reason);
//                    tvContent.setText("验证失败次数过多，请稍后重试～");
                break;
            case STATE_SUCCEED:
                //mStateTv.setTextColor(ContextCompat.getColor(mActivity, R.color.text_green));
                //mStateTv.setText(mActivity.getString(R.string.biometric_dialog_state_succeeded));
                mStateTv.setTextColor(Color.parseColor("#82C785"));
                mStateTv.setText(CheckUtil.isEmpty(reason) ? "验证成功" : reason);
                mCancelBtn.setVisibility(View.VISIBLE);
                mUsePasswordBtn.setVisibility(View.GONE);
                dismiss();
                break;
            default:
                break;
        }
    }
}
