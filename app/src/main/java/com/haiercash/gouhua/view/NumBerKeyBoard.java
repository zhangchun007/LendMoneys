package com.haiercash.gouhua.view;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.uihelper.HomeNotchScreenHelper;

import java.lang.reflect.Method;

/**
 * @Author: Sun
 * @Date :    2018/5/11
 * @FileName: NumBerKeyBoard
 * @Description:
 */

public class NumBerKeyBoard {
    private Activity mActivity;
    private static final int NUMBERKEYBOARDSTYTLE = 1;
    private static final int CUSTOMNUMBERKEYBOARDSTYTLE = 2;
    private DIYKeyboardView mKeyboardView;
    private CustomCircleKeyboardView mCustomKeyboardView;
    private Keyboard mKeyboardNumber;//数字键盘
    private Keyboard mCustomKeyboardNumber;//数字键盘
    private EditText mEditText;
    private int stytles;
    private int mHighEditCount = 1;//确定按钮高亮触发长度

    public NumBerKeyBoard(Activity activity, int stytle) {
        this.mActivity = activity;
        this.stytles = stytle;
        if (stytle == NUMBERKEYBOARDSTYTLE) {//老样式数字键盘
            mKeyboardNumber = new Keyboard(mActivity, R.xml.keyboardnumber);
            mKeyboardView = mActivity.findViewById(R.id.keyboard_view);
        } else if (stytle == CUSTOMNUMBERKEYBOARDSTYTLE) {//计算器数字键盘
            if (HomeNotchScreenHelper.hasNotchInScreen(mActivity)) {
                mCustomKeyboardNumber = new Keyboard(mActivity, R.xml.keyboardnumber_circle_notch);
            } else {
                mCustomKeyboardNumber = new Keyboard(mActivity, R.xml.keyboardnumber_circle);
            }

            mCustomKeyboardView = mActivity.findViewById(R.id.keyboard_view);
            mCustomKeyboardView.setTextBackground(R.drawable.bg_circle50_tr);
        }

    }

    public void setHighEditCount(int highEditCount) {
        this.mHighEditCount = highEditCount;
    }

    /**
     * edittext绑定自定义键盘
     *
     * @param editText 需要绑定自定义键盘的edittext
     */
    public void attachTo(EditText editText, boolean showKeyboard) {
        this.mEditText = editText;
        mEditText.addTextChangedListener(watcher);
        if (stytles == NUMBERKEYBOARDSTYTLE) {
            if (!CheckUtil.isEmpty(mEditText.getText().toString())) {
                // mKeyboardView.setTextColor(mActivity.getResources().getColor(R.color.textColor));
                mKeyboardView.setTextBackground(R.drawable.keyboard_true_pressed);
            } else {
                mKeyboardView.setTextBackground(R.drawable.keyboard_true_gray);
            }
        }
        hideSystemSofeKeyboard(mActivity.getApplicationContext(), mEditText);
        if (showKeyboard) {
            showSoftKeyboard();
        }
    }

    public void attachTo(EditText editText) {
        attachTo(editText, true);
    }

    public void setKeyboardView(DIYKeyboardView keyboardView) {
        mKeyboardView = keyboardView;
    }

    public void showSoftKeyboard() {
        if (stytles == NUMBERKEYBOARDSTYTLE) {//老样式数字键盘
            if (mKeyboardNumber == null) {
                mKeyboardNumber = new Keyboard(mActivity, R.xml.keyboardnumber);
            }
        } else if (stytles == CUSTOMNUMBERKEYBOARDSTYTLE) {//计算器数字键盘
            if (mCustomKeyboardNumber == null) {
                mCustomKeyboardNumber = new Keyboard(mActivity, R.xml.keyboardnumber_circle);
            }
        }
        if (stytles == NUMBERKEYBOARDSTYTLE) {//老样式数字键盘
            if (mKeyboardView == null) {
                mKeyboardView = mActivity.findViewById(R.id.keyboard_view);
            }
        } else if (stytles == CUSTOMNUMBERKEYBOARDSTYTLE) {//计算器数字键盘
            if (mCustomKeyboardView == null) {
                mCustomKeyboardView = mActivity.findViewById(R.id.keyboard_view);
            }
        }

        if (stytles == NUMBERKEYBOARDSTYTLE) {//老样式数字键盘
            mKeyboardView.setKeyboard(mKeyboardNumber);
            mKeyboardView.setEnabled(true);
            mKeyboardView.setPreviewEnabled(false);
            mKeyboardView.setVisibility(View.VISIBLE);
            mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        } else if (stytles == CUSTOMNUMBERKEYBOARDSTYTLE) {//计算器数字键盘
            mCustomKeyboardView.setKeyboard(mCustomKeyboardNumber);
            mCustomKeyboardView.setEnabled(true);
            mCustomKeyboardView.setPreviewEnabled(false);
            mCustomKeyboardView.setVisibility(View.VISIBLE);
            mCustomKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        }

    }

    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = mEditText == null ? null : mEditText.getText();
            int start = mEditText == null ? 0 : mEditText.getSelectionStart();
            if (stytles == NUMBERKEYBOARDSTYTLE) {//老样式数字键盘
                if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                } else if (primaryCode == Keyboard.KEYCODE_DONE) {// 确定
                    if (mOnOkClick != null) {
                        mOnOkClick.onOkClick();
                    }
                } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {//收起键盘
                    // 隐藏键盘
                    if (mEditText != null) {
                        mEditText.clearFocus();
                    }
                    hideKeyboard();
                } else if (editable != null) {
                    editable.insert(start, Character.toString((char) primaryCode));
                }
            } else if (stytles == CUSTOMNUMBERKEYBOARDSTYTLE) {//计算器数字键盘
                if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 回退
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                } else if (editable != null) {
                    editable.insert(start, Character.toString((char) primaryCode));
                }
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };


    /**
     * 隐藏系统键盘
     */
    public static void hideSystemSofeKeyboard(Context context, EditText editText) {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= 11) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            editText.setInputType(InputType.TYPE_NULL);
        }
        // 如果软键盘已经显示，则隐藏
        InputMethodManager imm = (InputMethodManager) context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public interface OnOkClick {
        void onOkClick();
    }


    public OnOkClick mOnOkClick = null;


    public void setOnOkClick(OnOkClick onOkClick) {
        mOnOkClick = onOkClick;
    }

    public void showKeyboard() {
        if (stytles == NUMBERKEYBOARDSTYTLE) {
            int visibility = mKeyboardView.getVisibility();
            if (visibility != View.VISIBLE) {
                mKeyboardView.setVisibility(View.VISIBLE);
            }
        } else if (stytles == CUSTOMNUMBERKEYBOARDSTYTLE) {
            int visibility = mCustomKeyboardView.getVisibility();
            if (visibility != View.VISIBLE) {
                mCustomKeyboardView.setVisibility(View.VISIBLE);
            }
        }

    }

    public void hideKeyboard() {
        if (stytles == NUMBERKEYBOARDSTYTLE) {
            int visibility = mKeyboardView.getVisibility();
            if (visibility == View.VISIBLE) {
                mKeyboardView.setVisibility(View.INVISIBLE);
            }
        } else if (stytles == CUSTOMNUMBERKEYBOARDSTYTLE) {
            int visibility = mCustomKeyboardView.getVisibility();
            if (visibility == View.VISIBLE) {
                mCustomKeyboardView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (stytles == NUMBERKEYBOARDSTYTLE) {
                if (mEditText.getText().toString().length() < mHighEditCount) {
                    //mKeyboardView.setTextColor(mActivity.getResources().getColor(R.color.text_Color));
                    mKeyboardView.setTextBackground(R.drawable.keyboard_true_gray);
                } else {
                    //mKeyboardView.setTextColor(mActivity.getResources().getColor(R.color.textColor));
                    mKeyboardView.setTextBackground(R.drawable.keyboard_true_pressed);
                }
            }
        }
    };

}