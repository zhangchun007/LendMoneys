package com.haiercash.gouhua.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.utils.UiUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 数字键盘自定义按钮样式
 */
public class DIYKeyboardView extends KeyboardView {
    private Context mContext;
    private int drawable;

    public DIYKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public DIYKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Keyboard keyboard = getKeyboard();
        List<Keyboard.Key> keys = null;
        if (keyboard != null) {
            keys = keyboard.getKeys();
        }
        if (keys != null) {
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == -4) {
                    Drawable dr = ContextCompat.getDrawable(getContext(), getDrawable());
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                    drawText(canvas, key);
                }
            }
        }
    }

    private void drawText(Canvas canvas, Keyboard.Key key) {
        try {//确定键的自定义绘制
            Rect bounds = new Rect();
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setAntiAlias(true);
            paint.setColor(Color.parseColor("#ffffff"));
            String label="确定";
            if (key.label != null) {
                Field field;
                if (label.length() > 1 && key.codes.length < 2) {
                    int labelTextSize = 0;
                    try {
                        field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                        field.setAccessible(true);
                        labelTextSize = (int) field.get(this);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    paint.setTextSize(UiUtil.sp2px(mContext, 21));
                    //paint.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    int keyTextSize = 0;
                    try {
                        field = KeyboardView.class.getDeclaredField("mLabelTextSize");
                        field.setAccessible(true);
                        keyTextSize = (int) field.get(this);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    paint.setTextSize(keyTextSize);
                    paint.setTypeface(Typeface.DEFAULT);
                }

                paint.getTextBounds(label, 0, label.length(), bounds);
                canvas.drawText(label, key.x + (key.width / 2),
                        (key.y + key.height / 2) + bounds.height() / 2, paint);
            } else if (key.icon != null) {
                key.icon.setBounds(key.x + (key.width - key.icon.getIntrinsicWidth()) / 2, key.y + (key.height - key.icon.getIntrinsicHeight()) / 2,
                        key.x + (key.width - key.icon.getIntrinsicWidth()) / 2 + key.icon.getIntrinsicWidth(), key.y + (key.height - key.icon.getIntrinsicHeight()) / 2 + key.icon.getIntrinsicHeight());
                key.icon.draw(canvas);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* private int getColor() {
         return color==0? Color.parseColor("#ffffff"):color;
     }
     public void setTextColor(int textColor){
         color = textColor;
         requestLayout();
         invalidate();
     }*/
    private int getDrawable() {
        return drawable == 0 ? R.drawable.keyboard_true_pressed : drawable;
    }

    public void setTextBackground(int res) {
        drawable = res;
        requestLayout();
        invalidate();
    }
}
