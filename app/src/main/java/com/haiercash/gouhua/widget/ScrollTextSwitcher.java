package com.haiercash.gouhua.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.app.haiercash.base.utils.handler.CycleHandlerCallback;
import com.haiercash.gouhua.interfaces.OnPopClickListener;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/11/26<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ScrollTextSwitcher extends TextSwitcher implements ViewSwitcher.ViewFactory {
    private Handler cycleHandler;
    private Context mContext;
    private ScrollViewClick switcherListener;
    private int index = 0;//textview上下滚动下标
    private int dataSize = 0;

    public ScrollTextSwitcher(Context context) {
        this(context, null);
    }

    public ScrollTextSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    @SuppressLint("HandlerLeak")
    private void initView(Context context) {
        mContext = context;
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        setFactory(this);
        cycleHandler = new Handler(new CycleHandlerCallback((Activity) mContext) {
            @Override
            public void dispatchMessage(Message msg) {
                index++;
                setText(switcherListener.getTextValue(index % dataSize));
                if (index == dataSize) {
                    index = 0;
                }
                cycleHandler.removeCallbacksAndMessages(null);
                cycleHandler.sendEmptyMessageDelayed(0, 3 * 1000);
            }
        });
    }

    @Override
    public View makeView() {
        final TextView tv = new TextView(mContext);
        //设置文字大小
        tv.setTextSize(12);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setSingleLine();
        tv.setTextColor(Color.parseColor("#303333"));
        tv.setGravity(Gravity.START | Gravity.CENTER);
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tv.setLayoutParams(params);
        //内容的点击事件
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switcherListener != null) {
                    switcherListener.onViewClick(v, index, tv.getText());
                }
            }
        });
        return tv;
    }

    public void setScrollListener(ScrollViewClick listener) {
        this.switcherListener = listener;
    }

    public void startScrollShow(int size) {
        dataSize = size;
        setText(switcherListener.getTextValue(index));
        if (size > 1) {
            cycleHandler.sendEmptyMessageDelayed(0, 3 * 1000);
        }
    }

    public interface ScrollViewClick extends OnPopClickListener {
        String getTextValue(int index);
    }
}
