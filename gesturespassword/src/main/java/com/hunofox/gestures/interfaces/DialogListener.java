package com.hunofox.gestures.interfaces;

import android.app.AlertDialog;
import android.widget.TextView;

/**
 * Created by Limige on 2017/1/5.
 * 用于弹框提示时(有确定取消按钮)
 */
public interface DialogListener {
    void dialogCallBack(AlertDialog dialog, TextView left, TextView right);
}
