package com.haiercash.gouhua.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TabWidget;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import android.util.AttributeSet;

import com.haiercash.gouhua.utils.UiUtil;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/6/4<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class PointView extends AppCompatTextView {
    private boolean mHideOnNull = true;

    public PointView(Context context) {
        this(context, null);
    }

    public PointView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public PointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!(getLayoutParams() instanceof LayoutParams)) {
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.TOP);
//            LayoutParams layoutParams = new LayoutParams(40, 40, Gravity.RIGHT | Gravity.TOP);
            setLayoutParams(layoutParams);
        }
        // set default font
        setTextColor(Color.WHITE);
        setTypeface(Typeface.DEFAULT_BOLD);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 6);
        setPadding(UiUtil.dip2px(getContext(), 5), UiUtil.dip2px(getContext(), 1), UiUtil.dip2px(getContext(), 5), UiUtil.dip2px(getContext(), 1));
        // set default background
        setBackground(10, Color.parseColor("#ff3202"));
//        setBackgroundColor(Color.parseColor("#d3321b"));
        setGravity(Gravity.CENTER);
        // default values
        setHideOnNull(true);
        setBadgeCount(0);
    }

    public void setBackground(int dipRadius, int badgeColor) {
        int radius = UiUtil.dip2px(getContext(), dipRadius);
        float[] radiusArray = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
        RoundRectShape roundRect = new RoundRectShape(radiusArray, null, null);
        ShapeDrawable bgDrawable = new ShapeDrawable(roundRect);
        bgDrawable.getPaint().setColor(badgeColor);
        //setBackground(bgDrawable);
        setBackgroundDrawable(bgDrawable);
    }

    /**
     * @return Returns true if view is hidden on badge value 0 or null;
     */
    public boolean isHideOnNull() {
        return mHideOnNull;
    }


    /**
     * @param hideOnNull the hideOnNull to set
     */
    public void setHideOnNull(boolean hideOnNull) {
        mHideOnNull = hideOnNull;
        setText(getText());
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.TextView#setText(java.lang.CharSequence, android.widget.TextView.BufferType)
     */
    @Override
    public void setText(CharSequence text, BufferType type) {
        if (isHideOnNull() && (text == null || "0".equalsIgnoreCase(text.toString()))) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
        }
        super.setText(text, type);
    }

    public void setBadgeCount(int count) {
        setText(String.valueOf(count));
    }


    public Integer getBadgeCount() {
        if (getText() == null) {
            return null;
        }
        String text = getText().toString();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void setBadgeGravity(int gravity) {
        FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        params.gravity = gravity;
        setLayoutParams(params);
    }

    public int getBadgeGravity() {
        FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        return params.gravity;
    }

    public void setBadgeMargin(int dipMargin) {
        setBadgeMargin(dipMargin, dipMargin, dipMargin, dipMargin);
    }

    public void setBadgeMargin(int leftDipMargin, int topDipMargin, int rightDipMargin, int bottomDipMargin) {
        FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        params.leftMargin = UiUtil.dip2px(getContext(), leftDipMargin);
        params.topMargin = UiUtil.dip2px(getContext(), topDipMargin);
        params.rightMargin = UiUtil.dip2px(getContext(), rightDipMargin);
        params.bottomMargin = UiUtil.dip2px(getContext(), bottomDipMargin);
        setLayoutParams(params);
    }

    public int[] getBadgeMargin() {
        FrameLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        return new int[]{params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin};
    }

    public void incrementBadgeCount(int increment) {
        Integer count = getBadgeCount();
        if (count == null) {
            setBadgeCount(increment);
        } else {
            setBadgeCount(increment + count);
        }
    }

    public void decrementBadgeCount(int decrement) {
        incrementBadgeCount(-decrement);
    }

    /*
     * Attach the BadgeView to the TabWidget
     *
     * @param target the TabWidget to attach the BadgeView
     *
     * @param tabIndex index of the tab
     */
    public void setTargetView(TabWidget target, int tabIndex) {
        View tabView = target.getChildTabViewAt(tabIndex);
        setTargetView(tabView);
    }

    /*
     * Attach the BadgeView to the target view
     *
     * @param target the view to attach the BadgeView
     */
    public void setTargetView(View target) {
        if (getParent() != null) {
            ((ViewGroup) getParent()).removeView(this);
        }
        if (target == null) {
            return;
        }
        if (target.getParent() instanceof FrameLayout) {
            ((FrameLayout) target.getParent()).addView(this);
        } else if (target.getParent() instanceof ViewGroup) {
            // use a new Framelayout container for adding badge
            ViewGroup parentContainer = (ViewGroup) target.getParent();
            int groupIndex = parentContainer.indexOfChild(target);
            parentContainer.removeView(target);
            FrameLayout badgeContainer = new FrameLayout(getContext());
            ViewGroup.LayoutParams parentLayoutParams = target.getLayoutParams();
            badgeContainer.setLayoutParams(parentLayoutParams);
            target.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            parentContainer.addView(badgeContainer, groupIndex, parentLayoutParams);
            badgeContainer.addView(target);
            badgeContainer.addView(this);
        } else if (target.getParent() == null) {
            Log.e(getClass().getSimpleName(), "ParentView is needed");
        }
    }

    public void setPointTopRight15(View targetView, int rightMargin, int topMargin) {
        setPoint(targetView, Gravity.TOP | Gravity.END | Gravity.CENTER, 5, topMargin, rightMargin, 5, 15);
    }

    public void setPointTopRight20(View targetView, int rightMargin, int topMargin) {
        setPoint(targetView, Gravity.TOP | Gravity.END | Gravity.CENTER, 5, topMargin, rightMargin, 5, 20);
    }

    public void setPointTopRightWithMargin20(View targetView, int leftMargin, int topMargin) {
        setPoint(targetView, Gravity.TOP | Gravity.END | Gravity.CENTER, leftMargin, topMargin, 0, 5, 20);
    }

    public void setPointCenterRight20(View targetView, int rightMargin) {
        setPoint(targetView, Gravity.END | Gravity.CENTER, 0, 0, rightMargin, 0, 20);
    }

    private void setPoint(View targetView, int gravity, int leftDipMargin, int topDipMargin, int rightDipMargin, int bottomDipMargin, int size) {
        setTargetView(targetView);
        setBadgeGravity(gravity);
        setText(" ");
        setVisibility(View.VISIBLE);
        setBadgeMargin(leftDipMargin, topDipMargin, rightDipMargin, bottomDipMargin);
        getLayoutParams().width = size;
        getLayoutParams().height = size;
    }
}
