package com.haiercash.gouhua.activity.comm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.x5webview.CusWebView;
import com.haiercash.gouhua.x5webview.WebInterfaceHelper;

/**
 * 协议底部动画帮助类
 */
public class AgreementUiHelper {
    private final View vBottom;//底部协议和按钮部分
    private final TextView btContract;//底部按钮
    private final View loadContentV;//加载协议的中间内容视图，用于改变高度
    private CusWebView mLastScrollingCw;//记录的最后滚动中的WebView

    private volatile boolean mScrolling;
    private volatile boolean mAnimShowing;
    private int loadContentVH, vBottomH;

    /**
     * @param cusWebViews  展示的合同webView
     * @param loadContentV 加载协议的中间内容视图，用于改变高度
     * @param vBottom      底部协议和按钮部分
     * @param btContract   按钮点击TextView
     */
    public AgreementUiHelper(View vBottom, TextView btContract, View loadContentV, CusWebView... cusWebViews) {
        this.vBottom = vBottom;
        this.btContract = btContract;
        this.loadContentV = loadContentV;
        btContract.setEnabled(false);
        if (loadContentV != null) {
            loadContentV.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        loadContentVH = loadContentV.getHeight();
                        vBottomH = vBottom.getHeight();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if (cusWebViews != null) {
            for (CusWebView cuWebView : cusWebViews) {
                if (cuWebView == null) {
                    continue;
                }
                cuWebView.getWebIHelper().setScrollChangeListener(new WebInterfaceHelper.OnScrollChangeListener() {
                    @Override
                    public void onScrollChanged(int l, int t, int oldl, int oldt) {
                        if (mAnimShowing || vBottom == null) {
                            return;
                        }
                        vBottom.removeCallbacks(mAnimRunnableS);
                        if (!mScrolling) {//控制滚动时消失
                            mScrolling = true;
                            mLastScrollingCw = cuWebView;
                            playDismissAnim();
                        }
                        vBottom.postDelayed(mAnimRunnableS, 500);//0.5s后如果没有触发滚动就展示
                    }
                });
            }
        }
    }

    public void startCounter() {
        if (btContract != null) {
            btContract.post(timeRunnable);//5秒计时
        }
    }

    private int mTimeCount = 6;
    private final Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            mTimeCount--;
            try {//try一下防止倒计时过程中被视图销毁而报错
                btContract.setText(UiUtil.getStr("阅读并同意",
                        mTimeCount > 0 ? "（" : "",
                        mTimeCount > 0 ? mTimeCount : "",
                        mTimeCount > 0 ? "S）" : ""));
                if (mTimeCount > 0) {
                    btContract.setEnabled(false);
                    btContract.postDelayed(this, 1000);
                } else {
                    btContract.setEnabled(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final Runnable mAnimRunnableS = new Runnable() {
        @Override
        public void run() {
            mAnimShowing = true;

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(350);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) loadContentV.getLayoutParams();
                        int changeH = (int) (animation.getAnimatedFraction() * vBottomH);
                        layoutParams.height = (int) (loadContentVH + vBottomH - changeH);
                        layoutParams.weight = animation.getAnimatedFraction() == 1 ? 1 : 0;
                        loadContentV.setLayoutParams(layoutParams);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    updateAnimShowStatus();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    updateAnimShowStatus();
                }

                @Override
                public void onAnimationPause(Animator animation) {
                    super.onAnimationPause(animation);
                    updateAnimShowStatus();
                }
            });
            valueAnimator.start();
            mScrolling = false;
        }
    };

    /**
     * 开始底部协议组消失动画
     */
    private void playDismissAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(350);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                try {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) loadContentV.getLayoutParams();
                    layoutParams.height = (int) (loadContentVH + animation.getAnimatedFraction() * vBottomH);
                    layoutParams.weight = animation.getAnimatedFraction() == 0 ? 1 : 0;
                    loadContentV.setLayoutParams(layoutParams);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        valueAnimator.start();
    }

    private void updateAnimShowStatus() {
        try {
            vBottom.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                    /*如果检测到滚动最大值跟当前滚动值的差值的绝对值小于等于需要动画的底部整体View的高度，
                        那么在中间加载内容的View的盖度变化之后要立马让最近滚动的WebView滚动到最底部*/
                        boolean needScroll = mLastScrollingCw != null && Math.abs(mLastScrollingCw.getMaxScrollY() - mLastScrollingCw.getScrollY()) <= vBottomH;
                        //如果需要，则滚动到最底部，防止之前滚动到最底部后动画出现WebView滚不到最底部
                        if (needScroll && mLastScrollingCw != null) {
                            mLastScrollingCw.scrollToBottom();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mAnimShowing = false;
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (vBottom != null) {
            vBottom.removeCallbacks(mAnimRunnableS);
        }
    }

    public static abstract class AgreementClickSpan extends ClickableSpan {
        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            //颜色和下划线
            ds.setColor(UiUtil.getColor(R.color.colorPrimary));
            ds.setUnderlineText(false);
        }
    }
}