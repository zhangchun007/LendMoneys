package com.haiercash.gouhua.activity.accountsettings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.databinding.ActivityAppealResultBinding;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * 申诉结果
 */
public class AppealResultActivity extends BaseActivity {
    private ActivityAppealResultBinding appealResultBinding;
    public static final String KEY_MASH_PHONE = "mash_phone";
    public static final String KEY_MASH_CERT_NO = "mash_cert_no";
    private String mFrom;

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return appealResultBinding = ActivityAppealResultBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        appealResultBinding.tvResult.setTypeface(FontCustom.getMediumFont(this));
        appealResultBinding.tvToLogin.setTypeface(FontCustom.getMediumFont(this));
        String phone = null;
        String certNo = null;
        Intent intent = getIntent();
        if (intent != null) {
            phone = intent.hasExtra(KEY_MASH_PHONE) ? intent.getStringExtra(KEY_MASH_PHONE) : null;
            certNo = intent.hasExtra(KEY_MASH_CERT_NO) ? intent.getStringExtra(KEY_MASH_CERT_NO) : null;
            mFrom = intent.hasExtra(AppealActivity.FROM) ? intent.getStringExtra(AppealActivity.FROM) : null;
        }
        appealResultBinding.tvInfo.setText(getString(R.string.appeal_result_info, UiUtil.getEmptyStr(phone, ""), UiUtil.getEmptyStr(certNo, "")));
        appealResultBinding.tvToLogin.setVisibility(AppealActivity.NEW_DEVICE_ERROR.equals(mFrom) ? View.GONE : View.VISIBLE);
        appealResultBinding.tvComplete.setOnClickListener(this);
        appealResultBinding.tvToLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == appealResultBinding.tvToLogin) {
            LoginSelectHelper.closeExceptMainAndToLogin(this);
        } else if (v == appealResultBinding.tvComplete) {
            onBackPressed();
        } else {
            super.onClick(v);
        }
    }

    @Override
    public void onBackPressed() {
        if (AppealActivity.NEW_DEVICE_ERROR.equals(mFrom)) {
            MainHelper.backToMainHome();
        } else {
            LoginSelectHelper.closeExceptMainAndToLogin(this);
        }
    }
}
