package com.haiercash.gouhua.unity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.haiercash.gouhua.R;


/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/23
 * @Version: 1.0
 */
public abstract class BaseDialog extends Dialog implements DialogInterface.OnDismissListener {

    private static final float DEFAULT_DIM = 0.5f;
    public Context mContext;
    /**
     * RootView
     */
    protected View mRootView;

    public BaseDialog(Context context) {
        super(context, R.style.DialogAnim);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
    }

    protected  void initView(){
    }

    protected void init() {
        try {
            int layoutId = getLayoutRes();
            mRootView = View.inflate(mContext, layoutId, null);
            setContentView(mRootView);
            setOnDismissListener(this);
        } catch (Exception e) {
        }
    }

    protected abstract int getLayoutRes();

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        if (null != window) {
            setCancelable(getCancelable());
            setCanceledOnTouchOutside(getCancelOutside());
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = getDimAmount();
            if (getHeight() > 0) {
                params.height = getHeight();
            } else {
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            }
            if (getWidth() > 0) {
                params.width = getWidth();
            } else {
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
            }
            params.gravity = getGravity();
            if (getAnim() > 0) {
                window.setWindowAnimations(getAnim());
            }
            if (getVerticalMargin() > 0) {
                params.verticalMargin = getVerticalMargin();
            }
            window.setAttributes(params);
        }
    }

    protected float getDimAmount() {
        return DEFAULT_DIM;
    }

    protected boolean getCancelOutside() {
        return false;
    }

    protected boolean getCancelable() {
        return false;
    }

    protected int getHeight() {
        return -1;
    }

    protected int getWidth() {
        return -1;
    }

    protected int getGravity() {
        return Gravity.CENTER;
    }

    protected int getAnim() {
        return 0;
    }

    protected float getVerticalMargin() {
        return 0f;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }


}
