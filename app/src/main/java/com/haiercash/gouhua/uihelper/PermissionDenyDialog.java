package com.haiercash.gouhua.uihelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.haiercash.gouhua.R;

/**
 * 权限拒绝后不再提示后去设置的弹窗
 */
public class PermissionDenyDialog extends Dialog implements View.OnClickListener {

    private OnClickListener mOnClickListener;

    public PermissionDenyDialog(@NonNull Context context) {
        this(context, true);
    }

    public PermissionDenyDialog(@NonNull Context context, boolean cancel) {
        super(context, R.style.Theme_Light_FullScreenDialogAct);
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
        initViewAndEvent();
        setCancelable(cancel);
        setCanceledOnTouchOutside(cancel);
    }

    private void initViewAndEvent() {
        setContentView(R.layout.dialog_permission_deny);
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.tv_dialog_btn1).setOnClickListener(this);
        findViewById(R.id.tv_dialog_btn2).setOnClickListener(this);
    }

    public PermissionDenyDialog setPermissionTitle(CharSequence charSequence) {
        TextView title = findViewById(R.id.tv_dialog_title);
        if (title != null) {
            title.setText(charSequence);
        }
        return this;
    }

    public PermissionDenyDialog setPermissionContent(CharSequence charSequence) {
        TextView content = findViewById(R.id.tv_dialog_message);
        if (content != null) {
            content.setText(charSequence);
        }
        return this;
    }

    public PermissionDenyDialog setOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        return this;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (mOnClickListener != null) {
            if (v.getId() == R.id.tv_dialog_btn1) {
                mOnClickListener.onClick(this, 1);
            } else if (v.getId() == R.id.tv_dialog_btn2) {
                mOnClickListener.onClick(this, 2);
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
