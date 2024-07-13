package com.app.haiercash.base.utils.system;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import java.lang.ref.WeakReference;

/**
 * 只是监听软键盘弹出和消失
 */
public class SoftKeyBoardListenerUtil {
    private int rootViewVisibleHeight;//纪录根视图的显示高度
    private OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener;
    private static boolean isShowKeyBoard = false;//软件盘是否正在弹出

    public SoftKeyBoardListenerUtil(Activity activity) {
        //获取activity的根视图
        final View rootView = activity.getWindow().getDecorView();
        //获取初始时软键盘未弹出时rootView显示高度
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        if (rootViewVisibleHeight == 0) {
            rootViewVisibleHeight = r.height();
        }
        //监听视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    //获取当前根视图在屏幕上显示的大小
                    Rect r = new Rect();
                    rootView.getWindowVisibleDisplayFrame(r);
                    int visibleHeight = r.height();
                    //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
                    if (rootViewVisibleHeight == visibleHeight) {
                        return;
                    }

                    //根视图显示高度变小超过200，可以看作软键盘显示了
                    if (rootViewVisibleHeight - visibleHeight > 200) {
                        if (onSoftKeyBoardChangeListener != null) {
                            onSoftKeyBoardChangeListener.keyBoardShow(rootViewVisibleHeight - visibleHeight);
                        }
                        rootViewVisibleHeight = visibleHeight;
                        isShowKeyBoard = true;
                        return;
                    }

                    //根视图显示高度变大超过200，可以看作软键盘隐藏了
                    if (visibleHeight - rootViewVisibleHeight > 200) {
                        if (onSoftKeyBoardChangeListener != null) {
                            onSoftKeyBoardChangeListener.keyBoardHide(visibleHeight - rootViewVisibleHeight);
                        }
                        rootViewVisibleHeight = visibleHeight;
                        isShowKeyBoard = false;
                    }
                } catch (Exception e) {
                    //
                }
            }
        });
    }

    private void setOnSoftKeyBoardChangeListener(OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener;
    }

    public interface OnSoftKeyBoardChangeListener {
        void keyBoardShow(int height);

        void keyBoardHide(int height);
    }

    public static class MySoftKeyboardListener implements OnSoftKeyBoardChangeListener {
        private final WeakReference<EditText> editText;

        public MySoftKeyboardListener(EditText editText) {
            this.editText = new WeakReference<>(editText);
        }

        @Override
        public void keyBoardShow(int height) {

        }

        @Override
        public void keyBoardHide(int height) {
            try {
                editText.get().clearFocus();//失去焦点去除光标
            } catch (Exception e) {
                //
            }
        }
    }

    /**
     * 当前页面是否弹出软键盘
     *
     * @return true 弹出 false隐藏
     */
    public static boolean isShowKeyBoard() {
        return isShowKeyBoard;
    }

    public static void setListener(Activity activity, OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        SoftKeyBoardListenerUtil softKeyBoardListener = new SoftKeyBoardListenerUtil(activity);
        softKeyBoardListener.setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener);
    }
}