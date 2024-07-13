package com.haiercash.gouhua.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.widget.NewNKeyBoardTextField;

/**
 * Created by Sun on 2017/8/16.
 * 自定义交易密码框 兼容验证码输入框
 */

public class CustomCodeView extends LinearLayout {

    private Context context;
    private NewNKeyBoardTextField passWord;
    private int maxLength = 6;
    private TextView[] tvPassWord;
    private OnInputFinishedListener onInputFinishedListener;
    private boolean canClearn = false;
    private static final int PassWord = 1;//密码框样式
    private static final int SmsCode = 2;//短信验证码样式

    private int mShowStytle = PassWord; //当前样式

    public CustomCodeView(Context context) {
        super(context);
    }

    public CustomCodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomCodeView);
            mShowStytle = attributes.getInt(R.styleable.CustomCodeView_style, PassWord);
            attributes.recycle();
        }
        initPayEditText();
        initEvent();
    }

    public CustomCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initEvent() {
        //设置6位长度
        passWord.setNKeyboardMaxInputLength(6);
        passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = s.toString();
                if (start >= maxLength) {
                    return;
                }
                //增加一个密码
                if (count > 0) {
                    setTextValue(start, value.charAt(start) + "");
                } else {
                    //删除一个密码
                    setTextValue(start, "");
                }
                if (onInputFinishedListener != null) {
                    if (value.length() == maxLength || (value.length() == 0 && !canClearn)) {
                        onInputFinishedListener.onInputFinished(value);
                        if (canClearn) {
                            canClearn = false;
                        }
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 初始化PayEditText
     */
    private void initPayEditText() {
        View view = View.inflate(context, R.layout.passwordview, null);
        Drawable bottom = null;
        if (mShowStytle == PassWord) {
            view.findViewById(R.id.ll_root).setBackgroundResource(R.drawable.iv_numpassword);
        } else {
            bottom = ContextCompat.getDrawable(getContext(), R.drawable.ic_line);
        }
        tvPassWord = new TextView[maxLength];
        for (int i = 0; i < maxLength; i++) {
            TextView viewText = view.findViewById(R.id.tv_pay1 + i);
            if (mShowStytle == SmsCode) {
                viewText.setInputType(InputType.TYPE_CLASS_NUMBER);
                viewText.setTextSize(24);
                viewText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, bottom);
            }
            tvPassWord[i] = viewText;
        }
        passWord = view.findViewById(R.id.et_password);
        passWord.setNkeyboardType(3);
        passWord.showNKeyboard();
        addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        passWord.setEditClearIcon(false);
    }

    public EditText getPassWord() {
        return passWord;
    }

    public String getCurrentWord() {
        return passWord.getNKeyboardText();
    }


    //设置字符的显示
    private void setTextValue(int position, String value) {
        tvPassWord[position].setText(value);
        /*输入的数字，按下键盘后，显示一秒数字，之后变回圆点
        tvPassWord[position].setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        tvPassWord[position].postDelayed(new Runnable() {
            @Override
            public void run() {
                //变回不可见数字
                tvPassWord[position].setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }, 1000);*/
    }

    /**
     * 清除数据
     */
    public void clearnData() {
        canClearn = true;
        passWord.setText("");
        for (int i = maxLength - 1; i >= 0; i--) {
            tvPassWord[i].setText("");
        }
    }

    /**
     * 输入框获取焦点
     */
    public void setEditRequestFocus() {
        passWord.setFocusable(true);
        passWord.setFocusableInTouchMode(true);
        passWord.requestFocus();
        passWord.requestFocusFromTouch();
    }

    /**
     * 当密码输入完成时的回调接口
     */
    public interface OnInputFinishedListener {
        void onInputFinished(String password);
    }

    /**
     * 对外开放的方法
     */
    public void setOnInputFinishedListener(OnInputFinishedListener onInputFinishedListener) {
        this.onInputFinishedListener = onInputFinishedListener;
    }
}


