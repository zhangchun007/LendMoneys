package com.app.haiercash.base.bui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.R;
import com.app.haiercash.base.utils.system.SystemUtils;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2017/12/22<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class TitleBarView extends Toolbar {
    public static final int NO_IMAGE = 0;
    public static final int NOT_CHANGE = 1;

    protected int leftImageId = NO_IMAGE;//左侧图片的id
    protected int rightImageId = NO_IMAGE;//右侧图片的id
    protected String rightText;//右侧文字
    protected CharSequence title;//标题栏文字

    protected View rl_title;

    protected ImageView header_iv_left;//左侧图片控件
    private ImageView head_left_close;//左侧图片关闭控件
    protected OnClickListener ivLeftListener;//左侧按钮点击侦听

    protected ImageView header_iv_right;//右侧图片控件
    protected OnClickListener ivRightListener;//右侧按钮点击侦听

    protected TextView header_tv_title;//标题
    protected OnClickListener tvTitleListener;//标题点击侦听

    protected TextView header_tv_right;//右侧文字控件
    protected OnClickListener tvRightListener;//右侧文字点击侦听

    public TitleBarView(Context context) {
        this(context, null);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        //setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        setBackgroundColor(Color.parseColor("#FFFFFF"));
        View view = LayoutInflater.from(context).inflate(R.layout.include_header, null);
        addView(view);
        setPadding(0, SystemUtils.getStatusBarHeight(context), 0, 0);
        rl_title = findViewById(R.id.rl_title);
        //左侧按钮的图片和点击侦听
        header_iv_left = findViewById(R.id.head_left);
        head_left_close = findViewById(R.id.head_left_close);
        header_iv_left.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.iv_back_arrow_blues));
        if (leftImageId != NO_IMAGE && leftImageId != NOT_CHANGE) {
            header_iv_left.setImageDrawable(ContextCompat.getDrawable(context, leftImageId));
        }
        head_left_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }
            }
        });
        if (ivLeftListener == null) {
            //左键默认为回退键
            header_iv_left.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getContext() instanceof Activity) {
                        ((Activity) getContext()).onBackPressed();
                    }
                }
            });
        } else {
            header_iv_left.setOnClickListener(ivLeftListener);
        }

        //右侧图片设置及点击侦听
        header_iv_right = findViewById(R.id.head_right);
        header_iv_right.setOnClickListener(ivRightListener);
        if (rightImageId == NO_IMAGE) {
            header_iv_right.setOnClickListener(null);
        }
        if (rightImageId != NO_IMAGE && rightImageId != NOT_CHANGE) {
            header_iv_right.setVisibility(View.VISIBLE);
            header_iv_right.setImageDrawable(ContextCompat.getDrawable(context, rightImageId));
        }

        //标题文字及点击侦听
        header_tv_title = findViewById(R.id.head_title);
        if (title != null) {
            header_tv_title.setText(title);
        }
        if (tvTitleListener != null) {
            header_tv_title.setOnClickListener(tvTitleListener);
        }

        //右侧文字设置及点击侦听
        header_tv_right = findViewById(R.id.head_rightText);
        if (!TextUtils.isEmpty(rightText)) {
            header_tv_right.setText(rightText);

            //有文字才能设置点击侦听
            if (tvRightListener != null) {
                header_tv_right.setOnClickListener(tvRightListener);
            }
        }
    }

    /**
     * 设置左侧图片、右侧图片、右侧文字、标题栏
     */
    public void setLeftImage(int resourceId, OnClickListener listener) {
        this.leftImageId = resourceId;
        this.ivLeftListener = listener;
        if (header_iv_left != null) {
            if (resourceId != NO_IMAGE && resourceId != NOT_CHANGE) {
                header_iv_left.setImageDrawable(ContextCompat.getDrawable(getContext(), resourceId));
            }
            if (listener != null) {
                header_iv_left.setOnClickListener(listener);
            }

            if (leftImageId == NO_IMAGE && ivLeftListener == null) {
                setLeftDisable(false);
            }
        }
    }

    public void setLeftDisable(boolean isShow) {
        header_iv_left.setVisibility(isShow ? VISIBLE : GONE);
    }

    /**
     * 左侧关闭按钮是否显示
     *
     * @param isShow true -显示
     */
    public void setLeftClostDisable(boolean isShow) {
        head_left_close.setVisibility(isShow ? VISIBLE : GONE);
    }

    /**
     * 右侧图片是否显示
     *
     * @param isShow true -显示
     */
    public void setRightImageDisable(boolean isShow) {
        header_iv_right.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setRightImage(int resourceId, OnClickListener listener) {
        this.rightImageId = resourceId;
        this.ivRightListener = listener;
        if (header_iv_right != null) {
            header_iv_right.setOnClickListener(listener);
            if (rightImageId == NO_IMAGE) {
                header_iv_right.setOnClickListener(null);
            }
            if (rightImageId != NO_IMAGE && rightImageId != NOT_CHANGE) {
                header_iv_right.setImageDrawable(ContextCompat.getDrawable(getContext(), resourceId));
            }
        }
    }

    public void setRightTextColor(int color) {
        if (header_tv_right != null) {
            header_tv_right.setTextColor(color);
        }
    }

    public void setRightTextSize(float size) {
        if (header_tv_right != null) {
            header_tv_right.setTextSize(size);
        }
    }

    public void setRightTextDrawLeft(int drawLeft) {
        if (header_tv_right != null && drawLeft != 0) {
            Drawable drawableLeft = getResources().getDrawable(drawLeft);
            header_tv_right.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
            header_tv_right.setCompoundDrawablePadding(5);
        }
    }

    public void setRightText(String text, OnClickListener listener) {
        this.rightText = text;
        this.tvRightListener = listener;
        if (header_tv_right != null) {
            header_tv_right.setOnClickListener(listener);
            if (!TextUtils.isEmpty(text)) {
                header_tv_right.setText(text);
            } else {
                header_tv_right.setOnClickListener(null);
            }
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        setTitle(title, 0, null);
    }

    public void setTitle(CharSequence title, String color) {
        setTitle(title, color == null ? 0 : Color.parseColor(color), null);
    }

    public void setTitle(CharSequence sequence, int color, OnClickListener listener) {
        this.tvTitleListener = listener;
        if (header_tv_title != null) {
            this.title = sequence;
            if (title != null) {
                header_tv_title.setText(title);
            }
            if (color != 0) {
                header_tv_title.setTextColor(color);
            }
            header_tv_title.setOnClickListener(listener);
        }
    }

    @Override
    public CharSequence getTitle() {
        return title;
    }

    public TextView getRightTextView() {
        return header_tv_right;
    }

    public void setTitleBackgroundColor(int color) {
        super.setBackgroundColor(color);
        rl_title.setBackgroundColor(color);
    }
}
