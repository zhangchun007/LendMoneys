package com.haiercash.gouhua.base;

import android.content.Context;
import android.content.DialogInterface;

public class DialogHelper {
    private Context mContext;
    private BaseDialog mGhDialog;

    public DialogHelper(Context context) {
        this.mContext = context;
    }

    /**
     * 新版样式Dialog
     */
    public BaseDialog showDialog(CharSequence title, CharSequence msg, CharSequence btn1, CharSequence btn2, DialogInterface.OnClickListener listener) {
        initDialog(title, msg, btn1, btn2, listener);
        mGhDialog.setMessageViewMovementMethod().setStandardStyle(2);
        mGhDialog.show();
        return mGhDialog;
    }

    /**
     * 初始化按钮
     */
    private void initDialog(CharSequence title, CharSequence msg, CharSequence btn1, CharSequence btn2, DialogInterface.OnClickListener listener) {
        if (mGhDialog == null) {
            mGhDialog = BaseDialog.getDialog(mContext, title, msg, btn1, btn2, listener);
        } else {
            mGhDialog.setTitle(title);
            mGhDialog.setMessage(msg);
            mGhDialog.setButton1(btn1, null);
            mGhDialog.setButton2(btn2, null);
            mGhDialog.setOnClickListener(listener);
        }
    }

    public void dismiss() {
        if (mGhDialog != null) {
            mGhDialog.dismiss();
            mGhDialog = null;
        }
    }
}
