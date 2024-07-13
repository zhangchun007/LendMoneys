package com.app.haiercash.base.bui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.viewbinding.ViewBinding;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/3/19<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public abstract class BaseGHPopupWindow extends PopupWindow {
    protected Context mContext;
    protected ViewBinding _binding;
    private Unbinder mUnBinder;
    protected View mView;

    public BaseGHPopupWindow(Context context, Object data) {
        super(context);
        mContext = context;
        init(context, data);
    }

    protected void init(Context context, Object data) {
        _binding = initBinding(LayoutInflater.from(context));
        if (_binding != null) {
            mView = _binding.getRoot();
        } else {
            mView = LayoutInflater.from(context).inflate(getLayout(), null);
        }
//        this.setClippingEnabled(false);
        // 设置SelectPicPopupWindow的View
        this.setContentView(mView);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        // 刷新状态
        this.update();

        // 实例化一个ColorDrawable颜色为半透明
        // ColorDrawable dw = new ColorDrawable(0000000000);
        int n = Color.parseColor("#B3000000");
        ColorDrawable dw = new ColorDrawable(n);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        //软键盘不会挡着popupwindow
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mUnBinder = ButterKnife.bind(this, mView);
        onViewCreated(data);
    }


    protected void setPopupOutsideTouchable(boolean touchable) {
        this.setFocusable(touchable);
        this.setOutsideTouchable(touchable);
    }

    protected void setOutTouchClickDismiss() {
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void showProgress(boolean flag, String msg) {
        if (mContext instanceof BaseGHActivity) {
            ((BaseGHActivity) mContext).showProgress(flag, msg);
        }
    }

    public void showDialog(String msg) {
        if (mContext instanceof BaseGHActivity) {
            ((BaseGHActivity) mContext).showDialog(msg);
        }
    }

    public boolean onBackPressed() {
        if (isShowing()) {
            dismiss();
            return true;
        }
        return false;
    }

    /**
     * 绑定的LayoutId
     */
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return null;
    }

    protected int getLayout() {
        return 0;
    }

    protected abstract void onViewCreated(Object data);

    public abstract void showAtLocation(View view);

    private void unBinder() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        if (_binding != null) {
            _binding = null;
        }
    }

    public void onDestroy() {
        if (isShowing()) {
            dismiss();
        }
        unBinder();
    }
}
