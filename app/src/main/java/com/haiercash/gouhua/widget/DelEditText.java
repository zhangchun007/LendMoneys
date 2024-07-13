package com.haiercash.gouhua.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.utils.UiUtil;


public class DelEditText extends TextInputEditText {

    private View.OnFocusChangeListener focusChangeListener;
    private final int isChinese = 2;
    private final int idNumber = 1;
    private final int limitNumber = 3;

    private String digitsIdNumber = "0123456789X"; //身份证号限制
    private int mShowStytle;
    private int mDeleteIconW;//清除按钮宽度（高度一样）
    private int mDeleteIconDrawableId;//清除按钮图标

    private int lineColor_active, lineColor_negative;// 点击时 & 未点击颜色
    private int linePosition; // 分割线位置
    private Paint mPaint;

    private boolean enableBottomLine; //是否允许底部横线，true的话使用本自定义view的，false的话自己定义


    public DelEditText(Context context) {
        super(context);
        init(context, null);
    }

    public DelEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DelEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DelEditTextStyle);
            mShowStytle = a.getInt(R.styleable.DelEditTextStyle_input_style, -1);
            mDeleteIconW = a.getDimensionPixelSize(R.styleable.DelEditTextStyle_delete_icon_w, UiUtil.dip2px(context, 15));
            enableBottomLine = a.getBoolean(R.styleable.DelEditTextStyle_enable_custom_line, false);
            if (enableBottomLine) {

                /**
                 * 初始化分割线（颜色、粗细、位置）
                 */
                // 1. 设置画笔
                mPaint = new Paint();
                mPaint.setStrokeWidth(0.8f); // 分割线粗细

                // 2. 设置分割线颜色（使用十六进制代码，如#333、#8e8e8e）
                int line_color_active = context.getResources().getColor(R.color.gray_606166); // 默认 = 606166
                int line_color_negative = context.getResources().getColor(R.color.color_e8eaef); // 默认 = e8eaef
                lineColor_active = a.getColor(R.styleable.DelEditTextStyle_line_color_active, line_color_active);
                lineColor_negative = a.getColor(R.styleable.DelEditTextStyle_line_color_negative, line_color_negative);

                mPaint.setColor(lineColor_negative); // 分割线默认颜色 = 灰色
                //是否设置抗锯齿效果
                mPaint.setAntiAlias(true);
                // 3. 分割线位置
                linePosition = a.getInteger(R.styleable.DelEditTextStyle_et_line_position, 2);
                // 消除自带下划线
                setBackground(null);
            }
            a.recycle();
        } else {
            mShowStytle = -1;
            mDeleteIconW = UiUtil.dip2px(context, 15);
        }
        mDeleteIconDrawableId = R.drawable.iv_delete;
        if (mShowStytle == idNumber) {
            setKeyListener(DigitsKeyListener.getInstance(digitsIdNumber));
        }
        addTextChangedListener(new IconTextWatcher());
        setOnFocusChangeListener(new IconFocusChange());
    }

    public void setDeleteIconDrawableId(int mDeleteIconDrawableId) {
        this.mDeleteIconDrawableId = mDeleteIconDrawableId;
    }

    public String getInputText() {
        if (CheckUtil.isEmpty(getText())) {
            return "";
        } else {
            return getText().toString().trim();
        }
    }

    public String getInputTextReplace() {
        return getInputText().replaceAll(" ", "");
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Drawable drawable = getCompoundDrawables()[2];
            if (drawable != null && event.getX() > getWidth()
                    - getPaddingRight()
                    - drawable.getIntrinsicWidth()) {
                if (listener == null) {
                    setText("");
                } else {
                    listener.onClick(drawable);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void addIcon() {
        if (getText() == null || getText().length() <= 0) {
            return;
        }
        Drawable rightDrawable = getCompoundDrawables()[2];
        if (rightDrawable == null) {
            rightDrawable = ContextCompat.getDrawable(getContext(), mDeleteIconDrawableId);
        }
        if (mDeleteIconW > 0 && rightDrawable != null) {
            rightDrawable.setBounds(0, 0, mDeleteIconW, mDeleteIconW);
        }
        setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[0],
                getCompoundDrawables()[1],
                rightDrawable,
                getCompoundDrawables()[3]);
    }

    public void removeIcon() {
        setCompoundDrawablesWithIntrinsicBounds(getCompoundDrawables()[0],
                getCompoundDrawables()[1],
                null,
                getCompoundDrawables()[3]);
    }

    class IconTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setLineColor(hasFocus());
            if (mShowStytle == isChinese) {
                String source = getInputText();
                String str = CheckUtil.isMandarin(source);
                if (!source.equals(str)) {
                    setText(str);
                    setSelection(str.length());
                }
            } else if (mShowStytle == limitNumber) {
                String source = getInputText();
                String str = CheckUtil.limitNubber(source);
                if (!source.equals(str)) {
                    setText(str);
                    setSelection(str.length());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().length() > 0) {
                if (isFocused()) {
                    addIcon();
                }
            } else {
                removeIcon();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (enableBottomLine) {
            // 绘制分割线
            // 需要考虑：当输入长度超过输入框时，所画的线需要跟随着延伸
            // 解决方案：线的长度 = 控件长度 + 延伸后的长度
            int x = this.getScrollX(); // 获取延伸后的长度
            int w = this.getMeasuredWidth(); // 获取控件长度

            // 传入参数时，线的长度 = 控件长度 + 延伸后的长度
            canvas.drawLine(0, this.getMeasuredHeight() - linePosition, w + x,
                    this.getMeasuredHeight() - linePosition, mPaint);
        }
    }

    class IconFocusChange implements OnFocusChangeListener {
        @SuppressLint("RestrictedApi")
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            try {//fragment销毁时会出现异常（也会走这里，并且linePhone为null）
                setLineColor(hasFocus);
                if (!hasFocus) {
                    removeIcon();
                } else {
                    addIcon();
                }

                if (focusChangeListener != null) {
                    focusChangeListener.onFocusChange(v, hasFocus);
                }
            } catch (Exception e) {
                Logger.e("fragment销毁而使设置了onFocusChange的View失去焦点");
            }
        }
    }

    /**
     * 关注1
     * 作用：设置分割线颜色
     */
    private void setLineColor(boolean active) {
        if (enableBottomLine) {
            mPaint.setColor(active ? lineColor_active : lineColor_negative);
            invalidate();
        }
    }

    public void addFocusChangeListener(OnFocusChangeListener l) {
        this.focusChangeListener = l;
    }

    private OnClickDrawableListener listener;

    public void setOnDrawableClickedListener(OnClickDrawableListener listener) {
        this.listener = listener;
    }

    public interface OnClickDrawableListener {
        void onClick(Drawable drawable);
    }
}
