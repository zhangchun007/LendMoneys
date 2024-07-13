package com.hunofox.gestures.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.hunofox.gesturesPassword.R;

/**
 * 项目名称：手势密码图案提示
 * 项目作者：胡玉君
 * 创建日期：2016/4/5 10:12.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 *
 * onDraw方法中paint.setColor代码未发现用途(67行附近)
 * ----------------------------------------------------------------------------------------------------
 */
public class LockIndicator extends View {
    private int numRow = 3;	// 行
    private int numColum = 3; // 列
    private int patternWidth = 50;
    private int patternHeight = 50;
    private int f = 5;
    private int g = 5;
    private int strokeWidth = 3;
    private Paint paint = null;
    private Drawable patternNoraml = null;
    private Drawable patternPressed = null;
    private String lockPassStr; // 手势密码

    public LockIndicator(Context paramContext) {
        super(paramContext);
    }

    public LockIndicator(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet, 0);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        patternNoraml = ContextCompat.getDrawable(paramContext,R.drawable.iv_small_gray_point);//R.drawable.iv_circle_empty_small
        patternPressed = ContextCompat.getDrawable(paramContext,R.drawable.iv_small_blue_point);//R.drawable.iv_circle_full_small
        if (patternPressed != null) {
            patternWidth = patternPressed.getIntrinsicWidth();
            patternHeight = patternPressed.getIntrinsicHeight();
            this.f = (patternWidth / 4);
            this.g = (patternHeight / 4);
            patternPressed.setBounds(0, 0, patternWidth, patternHeight);
            patternNoraml.setBounds(0, 0, patternWidth, patternHeight);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if ((patternPressed == null) || (patternNoraml == null)) {
            return;
        }
        // 绘制3*3的图标
        for (int i = 0; i < numRow; i++) {
            for (int j = 0; j < numColum; j++) {
                paint.setColor(Color.BLACK);/* 暂未发现此代码用途 */
                int i1 = j * patternHeight + j * this.g;
                int i2 = i * patternWidth + i * this.f;
                canvas.save();
                canvas.translate(i1, i2);
                String curNum = String.valueOf(numColum * i + (j + 1));
                if (!TextUtils.isEmpty(lockPassStr)) {
                    if (!lockPassStr.contains(curNum)) {
                        // 未选中
                        patternNoraml.draw(canvas);
                    } else {
                        // 被选中
                        patternPressed.draw(canvas);
                    }
                } else {
                    // 重置状态
                    patternNoraml.draw(canvas);
                }
                canvas.restore();
            }
        }
    }

    @Override
    protected void onMeasure(int paramInt1, int paramInt2) {
        if (patternPressed != null) {
            setMeasuredDimension(numColum * patternHeight + this.g
                    * (-1 + numColum), numRow * patternWidth + this.f
                    * (-1 + numRow));
        }
    }

    /**
     * 请求重新绘制
     * @param paramString 手势密码字符序列
     */
    public void setPath(String paramString) {
        lockPassStr = paramString;
        invalidate();
    }
}
