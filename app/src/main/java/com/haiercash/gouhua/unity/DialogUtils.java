package com.haiercash.gouhua.unity;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * @Description: dialog弹窗 分为三种样式
 * STYLE1：两个按钮都没有背景
 * STYLE2：两个按钮都有背景
 * STYLE3：一上一下两个按钮
 * @Author: zhangchun
 * @CreateDate: 2023/11/23
 * @Version: 1.0
 */
public class DialogUtils extends BaseDialog implements View.OnClickListener {

    private DialogButtonClickListener mDialogButtonClickListener;
    //是否展示title
    private boolean mShowTitle = true;
    private String mTitleContent;
    //内容
    private String mContent, mLeftButtoText, mRightButtoText;
    private boolean mLeftButtonShow=true,mRightButtonShow=true;

    private TextView tvContent, tvTitle;
    //是否展示关闭按钮
    private boolean mShowCloseIcon;
    private FrameLayout llClose;

    //dialog样式，1，2，3 目前取值
    private DialogStyle mStyle = DialogStyle.STYLE1;

    /**********style1 控件**********/
    private TextView tvStyle1Left, tvStyle1Right;
    private View viewline, styleLine1;
    private ConstraintLayout llStyle1;

    /**********style2 控件**********/
    private View styleLine2;
    private ConstraintLayout llStyle2;
    private TextView tvStyle2Left, tvStyle2Right;

    /**********style3 控件**********/
    private ConstraintLayout llStyle3;
    private TextView tvStyle3Left, tvStyle3Right;

    public DialogUtils(Context context) {
        super(context);
    }

    /**
     * 创建当前dialog
     *
     * @return 返回当前dialog
     */
    public static DialogUtils create(Context context) {
        DialogUtils dialog = new DialogUtils(context);
        return dialog;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_common;
    }

    @Override
    protected int getWidth() {
        return UiUtil.dip2px(mContext, 285);
    }

    @Override
    protected void initView() {
        super.initView();
        if (mRootView != null) {
            //标题
            tvTitle = mRootView.findViewById(R.id.tv_title);
            if (!TextUtils.isEmpty(mTitleContent)) {
                tvTitle.setText(mTitleContent);
            }
            tvTitle.setVisibility(mShowTitle ? View.VISIBLE : View.GONE);
            //右上角关闭按钮
            llClose = mRootView.findViewById(R.id.ll_close);
            llClose.setOnClickListener(this);
            llClose.setVisibility(mShowCloseIcon ? View.VISIBLE : View.GONE);

            //内容
            tvContent = mRootView.findViewById(R.id.tv_content);
            if (!TextUtils.isEmpty(mContent)) {
                tvContent.setText(mContent);
            }

            /**********style1 控件**********/
            llStyle1 = mRootView.findViewById(R.id.ll_style1);
            viewline = mRootView.findViewById(R.id.viewline);
            styleLine1 = mRootView.findViewById(R.id.style_line1);
            tvStyle1Left = mRootView.findViewById(R.id.tv_style1_left);
            tvStyle1Right = mRootView.findViewById(R.id.tv_style1_right);
            tvStyle1Left.setOnClickListener(this);
            tvStyle1Right.setOnClickListener(this);

            /**********style2 控件**********/
            llStyle2 = mRootView.findViewById(R.id.ll_style2);
            styleLine2 = mRootView.findViewById(R.id.style_line2);
            tvStyle2Left = mRootView.findViewById(R.id.tv_style2_left);
            tvStyle2Right = mRootView.findViewById(R.id.tv_style2_right);
            tvStyle2Left.setOnClickListener(this);
            tvStyle2Right.setOnClickListener(this);

            /**********style3 控件**********/
            llStyle3 = mRootView.findViewById(R.id.ll_style3);
            tvStyle3Left = mRootView.findViewById(R.id.tv_style3_left);
            tvStyle3Right = mRootView.findViewById(R.id.tv_style3_right);
            tvStyle3Left.setOnClickListener(this);
            tvStyle3Right.setOnClickListener(this);

            //设置style
            setLayoutStyle(mStyle);

            //设置按钮文本
            if (!TextUtils.isEmpty(mLeftButtoText)) {
                if (mStyle == DialogStyle.STYLE1) {
                    tvStyle1Left.setText(mLeftButtoText);
                } else if (mStyle == DialogStyle.STYLE2) {
                    tvStyle2Left.setText(mLeftButtoText);
                } else if (mStyle == DialogStyle.STYLE3) {
                    tvStyle3Left.setText(mLeftButtoText);
                }
            }
            if (!TextUtils.isEmpty(mRightButtoText)) {
                if (mStyle == DialogStyle.STYLE1) {
                    tvStyle1Right.setText(mRightButtoText);
                } else if (mStyle == DialogStyle.STYLE2) {
                    tvStyle2Right.setText(mRightButtoText);
                } else if (mStyle == DialogStyle.STYLE3) {
                    tvStyle3Right.setText(mRightButtoText);
                }
            }
            //不显示左边按钮
            if (!mLeftButtonShow){
                if (mStyle == DialogStyle.STYLE1) {
                    tvStyle1Left.setVisibility(View.GONE);
                    styleLine1.setVisibility(View.GONE);
                } else if (mStyle == DialogStyle.STYLE2) {
                    tvStyle2Left.setVisibility(View.GONE);
                    styleLine2.setVisibility(View.GONE);
                } else if (mStyle == DialogStyle.STYLE3) {
                    tvStyle3Left.setVisibility(View.GONE);
                }
            }
            //不显示右边按钮
            if (!mRightButtonShow){
                if (mStyle == DialogStyle.STYLE1) {
                    tvStyle1Right.setVisibility(View.GONE);
                    styleLine1.setVisibility(View.GONE);
                } else if (mStyle == DialogStyle.STYLE2) {
                    tvStyle2Right.setVisibility(View.GONE);
                    styleLine2.setVisibility(View.GONE);
                } else if (mStyle == DialogStyle.STYLE3) {
                    tvStyle3Right.setVisibility(View.GONE);
                }
            }

        }
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public DialogUtils setTitle(String title) {
        this.mTitleContent = title;
        return this;
    }

    /**
     * 标题是否展示
     *
     * @param showTitle
     */
    public DialogUtils setShowTitle(boolean showTitle) {
        this.mShowTitle = showTitle;
        return this;
    }

    /**
     * 左边按钮是否展示
     * @param showLeftButton
     * @return
     */
    public DialogUtils setShowLeftButton(boolean showLeftButton){
        this.mLeftButtonShow=showLeftButton;
        return this;
    }
    /**
     * 右边按钮是否展示
     * @param showRightButton
     * @return
     */
    public DialogUtils setShowRightButton(boolean showRightButton){
        this.mRightButtonShow=showRightButton;
        return this;
    }

    /**
     * 设置文本
     *
     * @param content
     */
    public DialogUtils setContent(String content) {
        this.mContent = content;
        return this;
    }

    /**
     * 是否展示关闭按钮
     *
     * @param showCloseIcom
     */
    public DialogUtils setShowCloseIcom(boolean showCloseIcom) {
        this.mShowCloseIcon = showCloseIcom;
        return this;
    }

    /**
     * 设置dialog样式
     *
     * @param style
     * @return
     */
    public DialogUtils setDialogStyle(DialogStyle style) {
        this.mStyle = style;
        return this;
    }

    /**
     * 设置左边按钮文字
     *
     * @param leftButtoText
     * @return
     */
    public DialogUtils setLeftButtoText(String leftButtoText) {
        mLeftButtoText = leftButtoText;
        return this;
    }

    /**
     * 设置右边按钮文字
     *
     * @param rightButtoText
     * @return
     */
    public DialogUtils setRightButtoText(String rightButtoText) {
        mRightButtoText = rightButtoText;
        return this;
    }

    /**
     * 根据style值动态显示布局
     *
     * @param style
     */
    private void setLayoutStyle(DialogStyle style) {
        switch (style) {
            case STYLE1: //
                llStyle1.setVisibility(View.VISIBLE);
                viewline.setVisibility(View.VISIBLE);
                llStyle2.setVisibility(View.GONE);
                llStyle3.setVisibility(View.GONE);
                break;
            case STYLE2:
                llStyle1.setVisibility(View.GONE);
                viewline.setVisibility(View.GONE);
                llStyle2.setVisibility(View.VISIBLE);
                llStyle3.setVisibility(View.GONE);
                break;
            case STYLE3:
                llStyle1.setVisibility(View.GONE);
                viewline.setVisibility(View.GONE);
                llStyle2.setVisibility(View.GONE);
                llStyle3.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 设置点击事件
     *
     * @param listener
     */
    public DialogUtils setOnButtonClickListener(DialogButtonClickListener listener) {
        this.mDialogButtonClickListener = listener;
        return this;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_close:
                dismiss();
                break;
            case R.id.tv_style1_left:
            case R.id.tv_style2_left:
            case R.id.tv_style3_left:
                if (mDialogButtonClickListener != null) {
                    mDialogButtonClickListener.onLeftButtonClick();
                }
                dismiss();
                break;
            case R.id.tv_style1_right:
            case R.id.tv_style2_right:
            case R.id.tv_style3_right:
                if (mDialogButtonClickListener != null) {
                    mDialogButtonClickListener.onRightButtonClick();
                }
                dismiss();
                break;
        }
    }
}
