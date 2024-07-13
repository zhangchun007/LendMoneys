package com.haiercash.gouhua.uihelper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.haiercash.gouhua.R;


/**
 * 2021/10/13 15:08
 * Description:非学生承诺弹窗
 */
public class CheckStudentDialog extends Dialog {
    private final OnCheckStudentDialogListener checkStudentDialogListener;
    private final Context context;
    private String contName;
    private String contUrl;

    public CheckStudentDialog(Context context, String contName, String contUrl, OnCheckStudentDialogListener onCheckStudentDialogListener) {
        super(context);
        this.context = context;
        this.contName = contName;
        this.contUrl = contUrl;
        this.checkStudentDialogListener = onCheckStudentDialogListener;
    }

    // 定义回调事件，用于dialog的点击事件
    public interface OnCheckStudentDialogListener {
        void isStudent();

        void notStudent();

        void clickContract(String url);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_check_student);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        CheckBox cbAgree = findViewById(R.id.cb_agree);
        TextView tvContract = findViewById(R.id.tv_contract);

        tvContract.setText(contName);
        TextView tvYes = findViewById(R.id.tv_yes);
        TextView tvNo = findViewById(R.id.tv_no);
        cbAgree.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvNo.setEnabled(true);
                tvNo.setClickable(true);
            } else {
                tvNo.setEnabled(false);
                tvNo.setClickable(false);
            }
        });

        tvContract.setOnClickListener(v -> {
            checkStudentDialogListener.clickContract(contUrl);
        });
        tvYes.setOnClickListener(v -> {
            checkStudentDialogListener.isStudent();
        });

        tvNo.setOnClickListener(v -> {
            checkStudentDialogListener.notStudent();
        });
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        //Display display = getActivity().getWindowManager().getDefaultDisplay();
        params.width = (int) (display.widthPixels * 0.8);
        window.setAttributes(params);

    }

}
