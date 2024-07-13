package com.hunofox.gestures.widget;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.hunofox.gestures.utils.AppUtil;
import com.hunofox.gesturesPassword.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：手势密码容器
 * 项目作者：胡玉君
 * 创建日期：2016/4/5 10:08.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class GestureContentView extends ViewGroup {

    private int baseNum = 6;

    private int[] screenDispaly;

    /**
     * 每个点区域的宽度
     */
    private int blockWidth;
    /**
     * 声明一个集合用来封装坐标集合
     */
    private List<GesturePoint> list;
    private Context context;
    private boolean isVerify;
    private GestureDrawLine gestureDrawLine;

    private boolean showGestureWay;

    /**
     * 包含9个ImageView的容器，初始化
     *
     * @param context
     * @param isVerify 是否为校验手势密码
     * @param passWord 用户传入密码
     * @param callBack 手势绘制完毕的回调
     */
    public GestureContentView(Activity context, boolean isVerify, String passWord, boolean showGestureWay, GestureDrawLine.GestureCallBack callBack) {
        super(context);
        screenDispaly = AppUtil.getScreenDispaly(context);
        blockWidth = (8 * screenDispaly[0]) / 30;
        this.list = new ArrayList<>();
        this.context = context;
        this.isVerify = isVerify;
        // 添加9个图标
        addChild();
        // 初始化一个可以画线的view
        gestureDrawLine = new GestureDrawLine(context, list, isVerify, passWord, showGestureWay, callBack);
    }

    /**
     * 禁用或启用绘制
     */
    public void setDrawEnable(boolean flag) {
        gestureDrawLine.setDrawEnable(flag);
    }

    private void addChild() {
        for (int i = 0; i < 9; i++) {
            ImageView image = new ImageView(context);
            image.setImageResource(R.drawable.iv_big_gray_point);//iv_circle_empty_big
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            image.setLayoutParams(new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            this.addView(image);
            invalidate();
            // 第几行
            int row = i / 3;
            // 第几列
            int col = i % 3;
            // 定义点的每个属性
            int leftX = col * blockWidth + blockWidth / baseNum;
            int topY = row * blockWidth + blockWidth / baseNum;
            int rightX = col * blockWidth + blockWidth - blockWidth / baseNum;
            int bottomY = row * blockWidth + blockWidth - blockWidth / baseNum;

            GesturePoint p = new GesturePoint(leftX, rightX, topY, bottomY, image, i + 1);
            list.add(p);
        }
    }

    public void setParentView(ViewGroup parent) {
        // 得到屏幕的宽度
        int width = screenDispaly[0];
        LayoutParams layoutParams = new LayoutParams(width, width - 30);
        this.setLayoutParams(layoutParams);
        gestureDrawLine.setLayoutParams(layoutParams);
        parent.addView(gestureDrawLine);
        parent.addView(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            //第几行
            int row = i / 3;
            //第几列
            int col = i % 3;
            View v = getChildAt(i);
            v.layout(
                    col * blockWidth + blockWidth / baseNum,
                    row * blockWidth + blockWidth / baseNum,
                    col * blockWidth + blockWidth - blockWidth / baseNum,
                    row * blockWidth + blockWidth - blockWidth / baseNum);

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 遍历设置每个子view的大小
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 保留路径delayTime时间长
     *
     * @param delayTime
     */
    public void clearDrawlineState(long delayTime) {
        gestureDrawLine.clearDrawlineState(delayTime);
    }

    /**
     * dip 转换成 px
     *
     * @param dip
     * @return
     */
    public float dip2px(float dip) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
    }
}
