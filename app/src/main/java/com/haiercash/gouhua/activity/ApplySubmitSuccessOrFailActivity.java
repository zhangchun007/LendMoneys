package com.haiercash.gouhua.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;

/**
 * 申请提交成功/失败页面
 */
public class ApplySubmitSuccessOrFailActivity extends BaseActivity {
    @Override
    protected int getLayout() {
        return R.layout.activity_apply_submit_success_fail;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setLeftIvVisibility(false);
        String email = getIntent() != null ? getIntent().getStringExtra("email") : "";
        setTitle(R.string.discharge_success_title);
        TextView tvSuccess2 = findViewById(R.id.tv_success_2);
        tvSuccess2.setText(getString(R.string.discharge_success_2, email));
        TextView tvConfirmSuccess = findViewById(R.id.tv_confirm_success);
        tvConfirmSuccess.setOnClickListener(this);
        tvConfirmSuccess.setTypeface(FontCustom.getMediumFont(this));
    }

    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        if (v.getId() == R.id.tv_confirm_success) {
            finish();
        } else {
            super.onClick(v);
        }
    }
}