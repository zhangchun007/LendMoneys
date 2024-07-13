package com.haiercash.gouhua.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/17
 * 描    述：DialogFragment
 * 修订历史：
 * ================================================================
 */
public class BaseDialog extends Dialog implements View.OnClickListener {
    protected Context mContext;// 上下文
    private RelativeLayout mLayoutTop;// 头部根布局
    private TextView mHtvTitle;// 标题
    private View v_line, v_line1;//标题下分割线
    private LinearLayout mLayoutContent;// 内容根布局
    private TextView mHtvMessage;// 内容
    private LinearLayout mLayoutBottom;// 底部根布局
    private TextView mBtnButton1;// 底部按钮1
    private View line2;
    private TextView mBtnButton2;// 底部按钮2
    private View line3;
    private TextView mBtnButton3;// 底部按钮3

    private ImageView ivClose;//关闭按钮

    private OnClickListener mOnClickListener1;// 按钮1的单击监听事件
    private OnClickListener mOnClickListener2;// 按钮2的单击监听事件
    private OnClickListener mOnClickListener3;// 按钮3的单击监听事件
    private OnClickListener mOnClickListener;// 按钮的单击监听事件
    private ScrollView svContent;

    public BaseDialog(@NonNull Context context) {
        this(context, true);
    }

    public BaseDialog(@NonNull Context context, boolean cancel) {
        super(context, R.style.Theme_Light_FullScreenDialogAct);
        initViewAndEvent(context);
        setCancelable(cancel);
        setCanceledOnTouchOutside(cancel);
    }

    private void initViewAndEvent(Context context) {
        mContext = context;
        setContentView(R.layout.dialog_common_generic);
        mLayoutTop = findViewById(R.id.ll_dialog_top);
        mHtvTitle = findViewById(R.id.tv_dialog_title);
        v_line = findViewById(R.id.v_line);
        v_line1 = findViewById(R.id.v_line1);
        mLayoutContent = findViewById(R.id.ll_dialog_content);
        svContent = findViewById(R.id.sv_content);
        mHtvMessage = findViewById(R.id.tv_dialog_message);
        mLayoutBottom = findViewById(R.id.ll_dialog_bottom);
        mBtnButton1 = findViewById(R.id.tv_dialog_btn1);
        line2 = findViewById(R.id.line2_dialog);
        mBtnButton2 = findViewById(R.id.tv_dialog_btn2);
        line3 = findViewById(R.id.line3_dialog);
        mBtnButton3 = findViewById(R.id.tv_dialog_btn3);
        ivClose = findViewById(R.id.iv_close);

        mBtnButton1.setOnClickListener(this);
        mBtnButton2.setOnClickListener(this);
        mBtnButton3.setOnClickListener(this);
        ivClose.setOnClickListener(this);
    }

    /**
     * 填充新布局到内容布局
     *
     * @param resource 资源文件
     */
    public void setDialogContentView(int resource) {
        View v = LayoutInflater.from(mContext).inflate(resource, null);
        if (mLayoutContent.getChildCount() > 0) {
            mLayoutContent.removeAllViews();
        }
        mLayoutContent.addView(v);
    }

    /**
     * 填充新布局到内容布局
     *
     * @param view 自定义View
     */
    public void setDialogContentView(View view) {
        if (mLayoutContent.getChildCount() > 0) {
            mLayoutContent.removeAllViews();
        }
        mLayoutContent.addView(view);
    }

    /**
     * 设置文本高度
     */
    public void setContentViewHeight(int height) {
        if (mLayoutContent != null) {
            ViewGroup.LayoutParams params = mLayoutContent.getLayoutParams();
            params.height = UiUtil.dip2px(mContext, height);
            mLayoutContent.setLayoutParams(params);
            if (svContent != null) {
                svContent.setFadingEdgeLength(UiUtil.dip2px(mContext, 30));
                svContent.setVerticalFadingEdgeEnabled(true);
            }
        }

    }


    /**
     * 填充新布局到内容布局
     */
    public void setDialogContentView(int resource, LinearLayout.LayoutParams params) {
        View v = LayoutInflater.from(mContext).inflate(resource, null);
        if (mLayoutContent.getChildCount() > 0) {
            mLayoutContent.removeAllViews();
        }
        mLayoutContent.addView(v, params);
    }

    /**
     * 给title赋值
     */
    @Override
    public void setTitle(CharSequence text) {
        if (text != null) {
            mLayoutTop.setVisibility(View.VISIBLE);
            v_line.setVisibility(View.GONE);
            mHtvTitle.setText(text);
            mHtvMessage.setTextColor(getContext().getResources().getColor(R.color.color_606166));
        } else {
            mLayoutTop.setVisibility(View.GONE);
            v_line.setVisibility(View.GONE);
            mHtvMessage.setTextColor(getContext().getResources().getColor(R.color.color_303133));
        }
    }

    /**
     * 给content赋值
     */
    public void setMessage(CharSequence text) {
        if (text != null) {
            mLayoutContent.setVisibility(View.VISIBLE);
            mHtvMessage.setText(text);
            mHtvMessage.setMovementMethod(LinkMovementMethod.getInstance());
            mHtvMessage.setHighlightColor(Color.TRANSPARENT);
        } else {
            mLayoutContent.setVisibility(View.GONE);
        }
    }

    public BaseDialog setMessageViewMovementMethod() {
        mHtvMessage.setMovementMethod(LinkMovementMethod.getInstance());
        mHtvMessage.setHighlightColor(Color.TRANSPARENT);
        return this;
    }

    public TextView getmHtvMessage() {
        return mHtvMessage;
    }

//    /**
//     * 设置内容所处的位置
//     */
//    public void setContentGravity(int gravity) {
//        if (mHtvMessage != null) {
//            mHtvMessage.setGravity(gravity);
//        }
//    }

    /**
     * 给第一个按钮赋值
     */
    public void setButton1(CharSequence text, OnClickListener listener) {
        if (text != null) {// && listener != null
            mLayoutBottom.setVisibility(View.VISIBLE);
            mBtnButton1.setVisibility(View.VISIBLE);
            mBtnButton1.setText(text);
            mOnClickListener1 = listener;
        } else {
            mBtnButton1.setVisibility(View.GONE);
        }
    }

    /**
     * 给第二个按钮赋值
     */
    public void setButton2(CharSequence text, OnClickListener listener) {
        if (text != null) {//&& listener != null
            mLayoutBottom.setVisibility(View.VISIBLE);
            mBtnButton2.setVisibility(View.VISIBLE);
            if (mBtnButton1 != null && mBtnButton1.getVisibility() == View.VISIBLE) {
                line2.setVisibility(View.VISIBLE);
            }
            mBtnButton2.setText(text);
            mOnClickListener2 = listener;
        } else {
            mBtnButton2.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        }
    }

    /**
     * 给第三个按钮赋值
     */
    private void setButton3(CharSequence text, OnClickListener listener) {
        //if (text != null && listener != null) {
        if (text != null) {
            mLayoutBottom.setVisibility(View.VISIBLE);
            mBtnButton3.setVisibility(View.VISIBLE);
            line3.setVisibility(View.VISIBLE);
            mBtnButton3.setText(text);
            mOnClickListener3 = listener;
        } else {
            mBtnButton3.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
        }
    }
    /* *************************************************************************************************/

    /**
     * 设置通用的点击事件
     */
    public BaseDialog setOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        return this;
    }

    public BaseDialog setTitleLineVisible(boolean visible) {
        v_line.setVisibility(View.GONE);
        return this;
    }

    /**
     * 修改按钮的颜色值
     */
    public BaseDialog setButtonTextColor(int index, int resId) {
        switch (index) {
            case 1:
                mBtnButton1.setTextColor(ContextCompat.getColor(mContext, resId));
                break;
            case 2:
                mBtnButton2.setTextColor(ContextCompat.getColor(mContext, resId));
                break;
            case 3:
                mBtnButton3.setTextColor(ContextCompat.getColor(mContext, resId));
                break;
            default:
                break;
        }
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public BaseDialog setCancelFlag(boolean flag) {
        super.setCancelable(flag);
        setCanceledOnTouchOutside(flag);
        return this;
    }


    /* *************************************************************************************************/

    /**
     * 二个按钮+一个事件
     */
    public static BaseDialog getDialog(Context context, CharSequence title, CharSequence message, CharSequence button1,
                                       CharSequence button2, OnClickListener listener) {
        return getDialog(context, title, message, button1, null, button2, null, null, null).setOnClickListener(listener);
    }

    /**
     * 自定义
     */
    public static BaseDialog getDialog(Context context, CharSequence button1,
                                       OnClickListener listener1, CharSequence button2,
                                       OnClickListener listener2) {
        return getDialog(context, null, "", button1, listener1, button2, listener2, null, null);
    }

    /**
     * 三个按钮+一个事件
     */
    public static BaseDialog getDialog(Context context, CharSequence title, CharSequence message, CharSequence button1,
                                       CharSequence button2, CharSequence button3, OnClickListener listener) {
        return getDialog(context, title, message, button1, null, button2, null, button3, null).setOnClickListener(listener);
    }

    /**
     * 一个按钮的dialog
     */
    public static BaseDialog getDialog(Context context, CharSequence title, CharSequence message,
                                       CharSequence button1, OnClickListener listener1) {
        return getDialog(context, title, message, button1, listener1, null, null, null, null);
    }

    /**
     * 两个按钮的dialog
     */
    public static BaseDialog getDialog(Context context, CharSequence title,
                                       CharSequence message, CharSequence button1,
                                       OnClickListener listener1, CharSequence button2,
                                       OnClickListener listener2) {
        return getDialog(context, title, message, button1, listener1, button2, listener2, null, null);
    }

    /**
     * 三个按钮的Dialog
     */
    public static BaseDialog getDialog(Context context, CharSequence title,
                                       CharSequence message, CharSequence button1,
                                       OnClickListener listener1, CharSequence button2,
                                       OnClickListener listener2, CharSequence button3,
                                       OnClickListener listener3) {
        BaseDialog mBaseDialog = new BaseDialog(context, false);
        mBaseDialog.setTitle(title);
        mBaseDialog.setMessage(message);
        mBaseDialog.setButton1(button1, listener1);
        mBaseDialog.setButton2(button2, listener2);
        mBaseDialog.setButton3(button3, listener3);
        return mBaseDialog;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (mOnClickListener != null) {
            if (v.getId() == R.id.tv_dialog_btn1) {
                mOnClickListener.onClick(this, 1);
            } else if (v.getId() == R.id.tv_dialog_btn2) {
                mOnClickListener.onClick(this, 2);
            } else if (v.getId() == R.id.tv_dialog_btn3) {
                mOnClickListener.onClick(this, 3);
            }
        } else {
            if (v.getId() == R.id.tv_dialog_btn1) {
                if (mOnClickListener1 != null) {
                    mOnClickListener1.onClick(this, 1);
                }
            } else if (v.getId() == R.id.tv_dialog_btn2) {
                if (mOnClickListener2 != null) {
                    mOnClickListener2.onClick(this, 2);
                }
            } else if (v.getId() == R.id.tv_dialog_btn3) {
                if (mOnClickListener3 != null) {
                    mOnClickListener3.onClick(this, 3);
                }
            }
        }
    }


    @Override
    public void show() {
        //bug_ _ android.view.WindowManager$BadTokenException: Unable to add window -- token  修复
        //来源https://www.cnblogs.com/awkflf11/p/5293267.html和http://www.jianshu.com/p/e46b843b95f4
        if (mContext instanceof Activity) {
            final Activity activity = (Activity) mContext;
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

    /* *********************************************Dialog新样式********************************************** **/

    /**
     * 标准化样式弹窗（按照UI要求后期统一修改）
     *
     * @param type 设置按钮样式
     */
    public BaseDialog setStandardStyle(int type) {
        if (type == 3 || type == 4) {
            v_line.setVisibility(View.GONE);
            v_line1.setVisibility(View.VISIBLE);
            mLayoutBottom.setPadding(0, 0, 0, 0);
            line2.setVisibility(View.VISIBLE);
            line3.setVisibility(View.GONE);
            mBtnButton1.setBackgroundResource(R.color.transparent);
            mBtnButton1.setTextColor(UiUtil.getColor(type == 3 ? R.color.color_303133 : R.color.colorPrimary));
            mBtnButton2.setBackgroundResource(R.color.transparent);
            mBtnButton2.setTextColor(UiUtil.getColor(R.color.colorPrimary));
            return this;
        }
        v_line.setVisibility(View.GONE);
        v_line1.setVisibility(View.GONE);
        mLayoutBottom.setPadding(0, 0, 0, UiUtil.dip2px(mContext, 30));
        line2.setVisibility(View.GONE);
        line3.setVisibility(View.GONE);
        if (type == 1) {
            ivClose.setVisibility(View.VISIBLE);
            mBtnButton1.setBackgroundResource(R.drawable.diaolg_bg_shap2);
            mBtnButton1.setTextColor(UiUtil.getColor(R.color.white));
        } else if (type >= 2) {
            mBtnButton1.setBackgroundResource(R.drawable.diaolg_bg_shap1);
            mBtnButton1.setTextColor(UiUtil.getColor(R.color.colorPrimary));
            mBtnButton2.setBackgroundResource(R.drawable.diaolg_bg_shap2);
            mBtnButton2.setTextColor(UiUtil.getColor(R.color.white));
        }
        return this;
    }

    private void setButtonMargin(TextView tv) {
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) tv.getLayoutParams();
        params1.leftMargin = UiUtil.dip2px(mContext, 10);
        params1.rightMargin = UiUtil.dip2px(mContext, 10);
    }
}
