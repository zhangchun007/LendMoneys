package com.haiercash.gouhua.view;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.haiercash.gouhua.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author: Sun
 * @Date :    2018/9/26
 * @FileName: EduProgressBottomBarView
 * @Description:
 */
public class EduProgressBottomBarView extends FrameLayout {


    @BindView(R.id.iv_progress)
    ImageView ivProgress;
    private int progress = 0;

    public EduProgressBottomBarView(@NonNull Context context) {
        super(context);
    }

    public EduProgressBottomBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EduProgressBottomBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
        initProgress();
    }

    private void initProgress() {
        if (progress <= 1) {
            ivProgress.setImageResource(R.drawable.ic_progress_1);
        } else if (progress <= 2) {
            ivProgress.setImageResource(R.drawable.ic_progress_2);
        } else {
            ivProgress.setImageResource(R.drawable.ic_progress_3);
        }
    }

    private void initView(Context context, AttributeSet attrs) {
        //获取页面定义的进度
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressBarViewStyle);
            progress = a.getInt(R.styleable.ProgressBarViewStyle_edu_progress, 0);
            a.recycle();
        }
        View view = View.inflate(context, R.layout.layout_progress_bar, this);
        ButterKnife.bind(view);
    }


}
