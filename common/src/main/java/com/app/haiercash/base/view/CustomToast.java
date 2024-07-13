package com.app.haiercash.base.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.haiercash.base.R;
import com.app.haiercash.base.databinding.CommonToastBinding;

import java.lang.reflect.Field;

/**
 * author: Sun
 * date: 2017/9/19.
 * fileName: CustomToast
 * 描述 :  自定义Toast 样式
 */

public class CustomToast {

    public static void makeLongText(Context context, CharSequence text) {
        makeText(context, text, Toast.LENGTH_LONG);
    }

    public static void makeText(Context context, CharSequence text) {
        makeText(context, text, Toast.LENGTH_SHORT);

    }

    private static void makeText(Context context, CharSequence text, int duration) {
        CommonToastBinding binding = CommonToastBinding.inflate(LayoutInflater.from(context));
        //为控件设置属性
        binding.message.setText(text);
        //WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //int height = wm.getDefaultDisplay().getHeight();

        Toast toast = Toast.makeText(context, text, duration);
        //设置Toast的透明度为70%
//        binding.getRoot().setAlpha((float) 0.7);
        toast.setView(binding.getRoot());
//        toast.setMargin(0, 0);
        toast.setGravity(Gravity.CENTER, 0, 0);
//        setAnimations(toast);
        //从上到下动画出现
//        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.getRoot(), "translationY", -200, 0);
//        animator.setDuration(200);
//        animator.start();

        // 居中显示toast
        //toast.setGravity(Gravity.TOP, 0, height / 2);
        toast.show();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static void setAnimations(Toast toast) {
        try {//没效果
            Object mTN;
            mTN = getField(toast, "mTN");
            if (mTN != null) {
                Object mParams = getField(mTN, "mParams");
                if (mParams instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
                    //清除动画
                    //显示与隐藏动画
                    params.windowAnimations = R.style.ToastAnimations;

                    //Toast可点击
                    params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    params.format = PixelFormat.TRANSLUCENT;
                    //设置viewgroup宽高
                    params.width = WindowManager.LayoutParams.MATCH_PARENT; //设置Toast宽度为屏幕宽度
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT; //设置高度
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ObjectAnimator animator=ObjectAnimator.ofFloat(toast, PropertyValuesHolder.ofFloat(Property.of()));
    }

    /**
     * 反射字段
     *
     * @param object    要反射的对象
     * @param fieldName 要反射的字段名称
     */
    private static Object getField(Object object, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (field != null) {
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }
}
