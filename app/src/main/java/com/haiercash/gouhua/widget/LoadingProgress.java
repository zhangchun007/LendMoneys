package com.haiercash.gouhua.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haiercash.gouhua.R;


/**
 * 项目名称：转圈的菊花
 * 项目作者：胡玉君
 * 创建日期：2016/12/9 16:06.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class LoadingProgress {
    private static final String TAG = "LoadingDialog";
    /**
     * 加载进度
     */
    private AlertDialog progressDialog;
    private boolean isInitContentView = false;
    private TextView tv_msg;

    private Context mContext;

    public LoadingProgress(Context context) {
        mContext = context;
    }

    /**
     * 取消loading
     */
    public void cancelLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                Log.e(TAG, "progressDialog销毁失败");
            }
        }
        //tv_msg = null;
        //progressDialog = null;
        isInitContentView = false;
    }

    /**
     * 默认载入loading
     */
    public void showLoadingDialog() {
        showLoadingDialog(null);
    }

    public void showLoadingDialog(String message) {
        showLoadingDialog(message, false);
    }

    private void showLoadingDialog(String message, boolean cancelable) {
        if (progressDialog == null) {
            progressDialog = new AlertDialog(mContext, R.style.ProgressHUD) {
                @Override
                public void onWindowFocusChanged(boolean hasFocus) {
                    super.onWindowFocusChanged(hasFocus);
                    ImageView imageView = findViewById(R.id.spinnerImageView);
                    AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
                    spinner.start();
                }
            };
        }
        progressDialog.setCancelable(cancelable);
        progressDialog.setCanceledOnTouchOutside(cancelable);
        if (cancelable) {
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    if (mContext instanceof Activity) {
                        ((Activity) mContext).onBackPressed();
                    }
                }
            });
        } else {
            progressDialog.setOnCancelListener(null);
        }
        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "progressDialog启动失败");
        }
        if (!isInitContentView) {
            progressDialog.setContentView(R.layout.progress_hud);
            tv_msg = progressDialog.findViewById(R.id.message);
        }
        isInitContentView = true;
        if (tv_msg == null) {
            return;
        }
        if (TextUtils.isEmpty(message)) {
            tv_msg.setVisibility(View.GONE);
        } else {
            tv_msg.setVisibility(View.VISIBLE);
            tv_msg.setText(message);
            tv_msg.invalidate();
        }
    }
}
