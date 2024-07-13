package com.haiercash.gouhua.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.haiercash.gouhua.utils.UiUtil;

/**
 * *@Author:    Sun
 * *@Date  :    2020/9/21
 * *@FileName: OvalImageView
 * *@Description: 用来显示圆角图片，可定义位置及弧度
 */
public class OvalImageView extends AppCompatImageView {
    private Path mPath;
    private int mW, mH;
    /**
     * Array of 8 values, 4 pairs of [X,Y] radii
     * {
     * LeftTopCorner, LeftTopCorner,
     * RightTopCorner, RightTopCorner,
     * RightBottomCorner, RightBottomCorner,
     * LeftBottomCorner, LeftBottomCorner
     * }
     */
    private float[] rids;

    public OvalImageView(Context context) {
        super(context);
        initV();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
        mH = h;
    }

    /**
     * 设置圆角部分
     *
     * @param leftTop     左上角
     * @param rightTop    右上角
     * @param leftBottom  左下角
     * @param rightBottom 右下角
     */
    public void setRoundingRadius(float leftTop, float rightTop, float leftBottom, float rightBottom) {
        //4 pairs of [X,Y] radii
        rids = new float[]{UiUtil.dip2px(getContext(), leftTop), UiUtil.dip2px(getContext(), leftTop), UiUtil.dip2px(getContext(), rightTop), UiUtil.dip2px(getContext(), rightTop), UiUtil.dip2px(getContext(), leftBottom), UiUtil.dip2px(getContext(), leftBottom), UiUtil.dip2px(getContext(), rightBottom), UiUtil.dip2px(getContext(), rightBottom)};
        invalidate();
    }


    public OvalImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initV();
    }

    public OvalImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initV();
    }

    private void initV() {
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
//        mPath.addRoundRect(new RectF(0, 0, w, h), rids, Path.Direction.CW);
//        canvas.clipPath(mPath);

        mPath.reset();
        addCirclePath();
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        mPath.addRoundRect(new RectF(0, 0, mW, mH), rids, Path.Direction.CCW);
        super.onDraw(canvas);
    }

    private void addCirclePath() {
        Path addPath = new Path();
        addPath.addRect(new RectF(0, 0, mW, mH), Path.Direction.CCW);
        addPath.addCircle(mW / 2, mH / 2, Math.min(mW, mH) / 2, Path.Direction.CW);
        setPath(addPath);
    }

    private void setPath(Path path) {
        mPath.reset();

        mPath.setFillType(Path.FillType.WINDING);
        mPath.addPath(path);

        invalidate();
    }
}
