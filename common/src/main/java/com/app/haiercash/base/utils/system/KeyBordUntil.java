package com.app.haiercash.base.utils.system;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/20
 * 描    述：键盘工具类
 * 修订历史：
 * ================================================================
 */
public class KeyBordUntil {
    /**
     * 隐藏键盘
     */
    public static void hideKeyBord(Activity activity) {
        if (activity != null && activity.getCurrentFocus() != null) {
            InputMethodManager methodManager = (InputMethodManager) activity.getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (methodManager != null) {
                methodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 隐藏键盘
     */
    public static void hideKeyBord2(Activity activity) {
        if (activity != null && activity.getCurrentFocus() != null) {
            InputMethodManager methodManager = (InputMethodManager) activity.getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (methodManager != null) {
                methodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
//        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        if (mInputMethodManager != null) {
//            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
//        }
    }

    public static void openKeyBord(Activity activity) {
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
        }
    }

    public static void showKeyBord(final Activity activity, final EditText editText) {
        try {
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        editText.requestFocus();
                        imm.showSoftInput(editText, 0);
                    } catch (Exception e) {
                        //
                    }
                }
            }, 50);
        } catch (Exception e) {
            //
        }
    }


    /**
     * 取消焦点
     *
     * @param view  控件view
     * @param event 焦点位置
     * @return 是否隐藏
     */
    public static void closeFocus(MotionEvent event, View view) {
        try {
            if (view != null && view instanceof EditText) {
//                    || view instanceof VoiceEditText
//                        || view instanceof VoiceDescriptionEditText
                int[] location = {0, 0};
                view.getLocationInWindow(location);
                int left = location[0], top = location[1], right = left
                        + view.getWidth(), bootom = top + view.getHeight();
                // 判断焦点位置坐标是否在空间内，如果位置在控件外，则隐藏键盘
                if (event.getRawX() < left || event.getRawX() > right || event.getY() < top || event.getRawY() > bootom) {
                    // 隐藏键盘
                    //IBinder token = view.getWindowToken();
                    //InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    //inputMethodManager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
                    ((View) view.getParent()).setFocusable(true);
                    ((View) view.getParent()).setFocusableInTouchMode(true);
                    ((View) view.getParent()).requestFocus();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
