package com.haiercash.gouhua.view;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.haiercash.gouhua.R;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/15
 * 描    述：
 * 修订历史：
 * ================================================================
 */
public class ProgressBarView extends LinearLayout {
    ImageView iv20;
    ImageView iv40;
    ImageView iv60;
    ImageView iv80;
    ProgressBar progressBar;

    private int progress = 0;

    public ProgressBarView(Context context) {
        this(context, null);
    }

    public ProgressBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressBarViewStyle);
          //  progress = a.getInt(R.styleable.ProgressBarViewStyle_pbv_progress, 0);
            a.recycle();
        }
        View.inflate(context, R.layout.layout_progress_bar, this);
//        iv20 = findViewById(R.id.iv_20);
//        iv40 = findViewById(R.id.iv_40);
//        iv60 = findViewById(R.id.iv_60);
//        iv80 = findViewById(R.id.iv_80);
//        progressBar = findViewById(R.id.pb_scale);
        if (progress != 0) {
            setProgress(progress);
        }
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (progressBar == null) {
            return;
        }
        if (progress < 40) {
            progressBar.setProgress(18);
            iv20.setVisibility(View.VISIBLE);
        } else if (progress < 60) {
            progressBar.setProgress(39);
            iv40.setVisibility(View.VISIBLE);
        } else if (progress < 80) {
            progressBar.setProgress(61);
            iv60.setVisibility(View.VISIBLE);
        } else if (progress < 100) {
            progressBar.setProgress(82);
            iv80.setVisibility(View.VISIBLE);
        } else {
            progressBar.setProgress(100);
        }
    }
}
