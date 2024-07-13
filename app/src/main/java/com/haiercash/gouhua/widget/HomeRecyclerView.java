package com.haiercash.gouhua.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 首页特制RecyclerView，方便进行事件分发处理
 */
public class HomeRecyclerView extends JudgeRecyclerView {
    private OnHomeRecyclerDispatchTouchListener onHomeRecyclerDispatchTouchListener;

    public HomeRecyclerView(@NonNull Context context) {
        super(context);
        initHomeRecyclerView();
    }

    public HomeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initHomeRecyclerView();
    }

    private void initHomeRecyclerView() {

    }

    public void setHomeRecyclerDispatchTouchListener(OnHomeRecyclerDispatchTouchListener onHomeRecyclerDispatchTouchListener) {
        this.onHomeRecyclerDispatchTouchListener = onHomeRecyclerDispatchTouchListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean returnValue = false;
        if (ev != null && onHomeRecyclerDispatchTouchListener != null) {
            returnValue = onHomeRecyclerDispatchTouchListener.dispatchTouchEvent(ev);
        }
        return returnValue || super.dispatchTouchEvent(ev);
    }

    public interface OnHomeRecyclerDispatchTouchListener {
        boolean dispatchTouchEvent(MotionEvent ev);
    }
}
