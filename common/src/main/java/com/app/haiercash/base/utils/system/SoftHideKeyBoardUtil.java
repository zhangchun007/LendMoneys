package com.app.haiercash.base.utils.system;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

/**
 * 监听软键盘弹出和消失，并重绘布局
 * 不适用半弹窗
 */
public class SoftHideKeyBoardUtil {

    private final WeakReference<Activity> activity;

    public static SoftHideKeyBoardUtil assistActivity(Activity activity) {
        return new SoftHideKeyBoardUtil(activity);
    }

    private SoftKeyBordListener mSoftKeyBordListener;
    private final WeakReference<View> mChildOfContent;
    private int usableHeightPrevious;
    private final FrameLayout.LayoutParams frameLayoutParams;
    //为适应华为小米等手机键盘上方出现黑条或不适配
    private int contentHeight;//获取setContentView本来view的高度
    private boolean isfirst = true;//只用获取一次
    private int statusBarHeight;//状态栏高度

    private SoftHideKeyBoardUtil(Activity activity) {
        this.activity = new WeakReference<>(activity);
        //1､找到Activity的最外层布局控件，它其实是一个DecorView,它所用的控件就是FrameLayout
        FrameLayout content = activity.findViewById(android.R.id.content);
        //2､获取到setContentView放进去的View
        View childOdContent = content.getChildAt(0);
        mChildOfContent = new WeakReference<>(childOdContent);
        //3､给Activity的xml布局设置View树监听，当布局有变化，如键盘弹出或收起时，都会回调此监听
        childOdContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            //4､软键盘弹起会使GlobalLayout发生变化
            @Override
            public void onGlobalLayout() {
                try {
                    if (isfirst) {
                        contentHeight = mChildOfContent.get().getHeight();//兼容华为等机型
                        isfirst = false;
                    }
                    //5､当前布局发生变化时，对Activity的xml布局进行重绘
                    possiblyResizeChildOfContent();
                } catch (Exception e) {
                    //view空时捕获
                }
            }
        });
        //6､获取到Activity的xml布局的放置参数
        frameLayoutParams = (FrameLayout.LayoutParams) childOdContent.getLayoutParams();
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.get().getWindowVisibleDisplayFrame(r);
        // 全屏模式下：直接返回r.bottom，r.top其实是状态栏的高度
        return (r.bottom - r.top);
    }

    // 获取界面可用高度，如果软键盘弹起后，Activity的xml布局可用高度需要减去键盘高度
    private void possiblyResizeChildOfContent() {
        //1､获取当前界面可用高度，键盘弹起后，当前界面可用布局会减少键盘的高度
        int usableHeightNow = computeUsableHeight();
        //2､如果当前可用高度和原始值不一样
        if (usableHeightNow != usableHeightPrevious) {
            //3､获取Activity中xml中布局在当前界面显示的高度
            int usableHeightSansKeyboard = mChildOfContent.get().getRootView().getHeight();

            //4､Activity中xml布局的高度-当前可用高度
            //这个判断是为了解决19之前的版本不支持沉浸式状态栏导致布局显示不完全的问题
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                Rect frame = new Rect();
                activity.get().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                int statusBarHeight = frame.top;
                usableHeightSansKeyboard -= statusBarHeight;
            }

            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            //5､高度差大于屏幕1/4时，说明键盘弹出
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // 6､键盘弹出了，Activity的xml布局高度应当减去键盘高度
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    frameLayoutParams.height = usableHeightSansKeyboard - heightDifference + statusBarHeight;
                } else {
                    frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                }
                if (mSoftKeyBordListener != null) {
                    mSoftKeyBordListener.softShowing();
                }
            } else {
                frameLayoutParams.height = contentHeight;
                if (mSoftKeyBordListener != null) {
                    mSoftKeyBordListener.softHide();
                }
            }
            //7､ 重绘Activity的xml布局
            mChildOfContent.get().requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }


    public void setSoftKeyBordListener(SoftKeyBordListener softKeyBordListener) {
        mSoftKeyBordListener = softKeyBordListener;
    }

    public interface SoftKeyBordListener {
        /**
         * 弹起键盘
         */
        void softShowing();

        /**
         * 隐藏键盘
         */
        void softHide();
    }

    public static class MySoftKeyboardListener implements SoftKeyBordListener {
        private final WeakReference<EditText> editText;

        public MySoftKeyboardListener(EditText editText) {
            this.editText = new WeakReference<>(editText);
        }

        @Override
        public void softShowing() {

        }

        @Override
        public void softHide() {
            try {
                editText.get().clearFocus();//失去焦点去除光标
            } catch (Exception e) {
                //
            }
        }
    }
}
