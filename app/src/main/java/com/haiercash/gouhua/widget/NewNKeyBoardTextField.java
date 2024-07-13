package com.haiercash.gouhua.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.log.Logger;
import com.appsafekb.safekeyboard.NKeyBoardTextField;
import com.appsafekb.safekeyboard.encrypt.EncryptTypeFactory;
import com.appsafekb.safekeyboard.interfaces.KeyBoardListener;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * 方便NKeyBoardTextField设置焦点监听
 */
public class NewNKeyBoardTextField extends NKeyBoardTextField {
    private OnFocusChangeListener mOnFocusChangeLis;
    private float mHintTextSize, mPwdChatSize = 13f;//hint字体大小，密文字符大小

    public NewNKeyBoardTextField(Context context) {
        super(context);
        newNKeyInit();
    }

    public NewNKeyBoardTextField(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        newNKeyInit();
    }

    public NewNKeyBoardTextField(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        newNKeyInit();
    }

    private void newNKeyInit() {
        SHOW_PWD_CHAR = '●';
        //整体加密设置--start
        final String keyX = getResources().getString(R.string.ijm_key_x);
        final String keyY = getResources().getString(R.string.ijm_key_y);
        final String randomStr = EncryptUtil.random(32);
        final String publicKey = keyX + keyY;
        //开启整体加密模式
        setNKeyboardKeyEncryption(true);
        //设置整体加密模式的加密方式
        setNKeyboardEncryptTypeData(EncryptTypeFactory.generatePubKeySm2EncryptionType(publicKey, true, randomStr, 1));
        //整体加密设置--end
        //检测发现有被改字体，重置成默认字体
        setTypeface(Typeface.DEFAULT);
    }

    /**
     * 设置明文hint和密文的大小
     */
    public void setNewNKeyTextSize(float hintTextSize, float pwdChatSize) {
        mHintTextSize = hintTextSize;
        mPwdChatSize = pwdChatSize;
    }

    @Override
    protected void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        super.onTextChanged(charSequence, i, i1, i2);
        if (mHintTextSize > 0 && mPwdChatSize > 0) {
            boolean empty = getNKeyboardTextLength() <= 0;
            setTextSize(empty ? mHintTextSize : mPwdChatSize);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setLetterSpacing(empty ? 0f : 3f / 13f);
            }
        }
    }

    @Override
    public void setCompoundDrawablePadding(int pad) {
        super.setCompoundDrawablePadding(pad);
        //看源码得知可以在这里设置
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.iv_delete);
        if (drawable != null) {
            drawable.setBounds(0, 0, UiUtil.dip2px(getContext(), 15), UiUtil.dip2px(getContext(), 15));
        }
        setCompoundDrawables(null, null, drawable, null);
    }

    public void addOnFocusChangeLis(OnFocusChangeListener mOnFocusChangeLis) {
        this.mOnFocusChangeLis = mOnFocusChangeLis;
    }

    @Override
    protected void doOnFocusChange(View view, boolean b) {
        super.doOnFocusChange(view, b);
        try {
            setEditClearIcon(b);
        } catch (Exception e) {
            Logger.e("-----");
        }
        if (mOnFocusChangeLis != null) {
            mOnFocusChangeLis.onFocusChange(view, b);
        }
    }

    public static class MyNKeyBoardListener implements KeyBoardListener {

        private final NKeyBoardTextField nKeyBoardTextField;

        public MyNKeyBoardListener(NKeyBoardTextField nKeyBoardTextField) {
            this.nKeyBoardTextField = nKeyBoardTextField;
        }

        @Override
        public void onKey(int i) {

        }

        @Override
        public void onDismiss(DialogInterface dialogInterface) {
            try {
                nKeyBoardTextField.clearFocus();//失去焦点，去除光标
            } catch (Exception e) {
                //
            }
        }

        @Override
        public void onShow(DialogInterface dialogInterface) {

        }

        @Override
        public void onConfigLoadSucc() {

        }

        @Override
        public boolean onCompleteKeyboardKeepShow() {
            return false;
        }
    }
}
