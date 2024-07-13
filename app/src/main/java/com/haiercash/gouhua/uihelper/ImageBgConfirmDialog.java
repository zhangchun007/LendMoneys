package com.haiercash.gouhua.uihelper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.haiercash.gouhua.R;

/**
 * 图片背景的确认弹窗，目前用处
 */
public class ImageBgConfirmDialog extends Dialog implements View.OnClickListener {
    private int id;  //图片资源Id
    private String btName;
    private OnClickListener clickListener;

    public ImageBgConfirmDialog(@NonNull Context context, int id, String btName, OnClickListener clickListener) {
        super(context);
        this.id = id;
        this.btName = btName;
        this.clickListener = clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image_bg_confirm);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((ImageView) findViewById(R.id.iv_bg)).setImageResource(id);
        ((TextView) findViewById(R.id.bt_go)).setText(btName);
        findViewById(R.id.bt_cancel).setOnClickListener(this);
        findViewById(R.id.bt_go).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_cancel) {
            dismiss();
        } else if (v.getId() == R.id.bt_go) {
            dismiss();
            if (clickListener != null) {
                clickListener.onClick(ImageBgConfirmDialog.this, 2);
            }
        }
    }
}
