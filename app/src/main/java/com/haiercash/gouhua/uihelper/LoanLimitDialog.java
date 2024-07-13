package com.haiercash.gouhua.uihelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;

import com.haiercash.gouhua.R;

/**
 * 没有额度的弹窗
 */
public class LoanLimitDialog extends Dialog implements View.OnClickListener {

    private OnClickListener mOnClickListener;

    public LoanLimitDialog(@NonNull Context context) {
        this(context, true);
    }

    public LoanLimitDialog(@NonNull Context context, boolean cancel) {
        super(context, R.style.Theme_Light_FullScreenDialogAct);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
        initViewAndEvent();
        setCancelable(cancel);
        setCanceledOnTouchOutside(cancel);
    }

    private void initViewAndEvent() {
        setContentView(R.layout.dialog_loan_limit);
        findViewById(R.id.tvBtn).setOnClickListener(this);
        findViewById(R.id.ivClose).setOnClickListener(this);
    }

    public void setOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (mOnClickListener != null) {
            if (v.getId() == R.id.tvBtn) {
                mOnClickListener.onClick(this, 1);
            }
        }
    }

    @Override
    public void show() {
        //bug_ _ android.view.WindowManager$BadTokenException: Unable to add window -- token  修复
        //来源https://www.cnblogs.com/awkflf11/p/5293267.html和http://www.jianshu.com/p/e46b843b95f4
        if (getContext() instanceof Activity) {
            final Activity activity = (Activity) getContext();
            if (activity.isFinishing()) {
                return;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
                    return;
                }
            }
        }
        super.show();
    }
}
