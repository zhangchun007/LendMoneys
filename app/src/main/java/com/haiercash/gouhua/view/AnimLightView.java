package com.haiercash.gouhua.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.haiercash.gouhua.R;

/**
 * 覆盖在某一区域的从左到右的白光动效
 * 从左到右，然后悬停1.5秒再来，以此往复，默认从左到右总共跑两次
 */
public class AnimLightView extends View implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    private int mW;//此自定义view的宽高
    private int mLightW;//动效图片的宽
    private Drawable mLightDrawable;//光的图片(矢量图、png、webp都行)
    private int mRepeatCount = 2;//动画次数
    private int mHasShowCount;//动效已经启动次数，用来判断是否悬停1.5秒后再来，还是完成
    private Rect mDestRect;//光的图片显现区域
    private ValueAnimator mValueAnimator;
    private int mAlpha = 255;//0-255
    private boolean needCancel = true;
    private int duration;
    private boolean needAlpha = true;
    private int startDelayDuration = 1500;//默认重复动画间隔

    public AnimLightView(Context context) {
        super(context);
        init(context, null);
    }

    public AnimLightView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AnimLightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AnimLightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AnimLightView);
            mLightW = ta.getDimensionPixelSize(R.styleable.AnimLightView_lightWidth, mLightW);
            mRepeatCount = ta.getInteger(R.styleable.AnimLightView_repeatCount, mRepeatCount);
            needCancel = ta.getBoolean(R.styleable.AnimLightView_needCancel, true);
            duration = ta.getInteger(R.styleable.AnimLightView_duration, 0);
            needAlpha = ta.getBoolean(R.styleable.AnimLightView_needAlpha, true);
            startDelayDuration = ta.getInt(R.styleable.AnimLightView_startDelayDuration, 1500);
            mLightDrawable = ResourcesCompat.getDrawable(getResources(), ta.getResourceId(R.styleable.AnimLightView_lightSrc, 0), null);
            if (mLightDrawable != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                mLightDrawable = (DrawableCompat.wrap(mLightDrawable)).mutate();
            }
            ta.recycle();
        }
        mDestRect = new Rect(0, 0, 0, 0);
        initAnim(0, 0);
    }

    private void initAnim(int start, int end) {
        if (mValueAnimator == null) {
            mValueAnimator = ValueAnimator.ofInt();
            mValueAnimator.addListener(this);
            mValueAnimator.addUpdateListener(this);
        }
        mValueAnimator.cancel();
        mValueAnimator.setIntValues(start, end);
        if (duration != 0) {
            mValueAnimator.setDuration(duration);
        } else {
            mValueAnimator.setDuration(mW * 500L / 390);//390像素宽度0.5秒走完一次的速度
        }
        mValueAnimator.setStartDelay(0);
    }

    private void setLocation(int left, int right) {
        mDestRect.left = left;
        mDestRect.right = right;
        invalidate();
    }

    /**
     * 启动动画
     *
     * @param startX 起点，相对本View的左边沿
     */
    public void startAnim(int startX) {
        post(() -> {
            initAnim(startX, mW);
            mValueAnimator.start();
        });
    }

    public void startAnim() {
        startAnim(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mW = MeasureSpec.getSize(widthMeasureSpec);
        this.mDestRect.bottom = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (mLightDrawable != null) {
                mLightDrawable.setBounds(mDestRect);
                if (needAlpha) {
                    mLightDrawable.setAlpha(mAlpha);
                }
                mLightDrawable.draw(canvas);
            }
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        //剩余三分之一内渐渐变淡直到消失
        mAlpha = (int) (255 * (1 - Math.max(3 * animation.getAnimatedFraction() - 2, 0)));
        int animatedValue = (int) animation.getAnimatedValue();
        setLocation(animatedValue, animatedValue + mLightW);
    }

    @Override
    public void onAnimationStart(Animator animation) {
        mHasShowCount++;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        setLocation(0, 0);
        //每次动画间隔1.5秒
        if (mHasShowCount > 0 && mHasShowCount < mRepeatCount) {
            mValueAnimator.setStartDelay(startDelayDuration);
            mValueAnimator.start();
        } else {
            mValueAnimator.setStartDelay(0);
            mHasShowCount = 0;
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        //每次cancel后都会走onAnimationEnd
        mHasShowCount = 0;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    protected void onDetachedFromWindow() {
        if (mValueAnimator != null && needCancel) {
            mValueAnimator.cancel();
        }
        super.onDetachedFromWindow();
    }
}
