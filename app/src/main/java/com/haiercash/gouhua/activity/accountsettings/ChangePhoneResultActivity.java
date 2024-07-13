package com.haiercash.gouhua.activity.accountsettings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.databinding.ActivityUpdatePhoneResultBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;

/**
 * 修改手机号结果页面
 */
public class ChangePhoneResultActivity extends BaseActivity {
    private ActivityUpdatePhoneResultBinding updatePhoneResultBinding;

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return updatePhoneResultBinding = ActivityUpdatePhoneResultBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        updatePhoneResultBinding.tvResult.setTypeface(FontCustom.getMediumFont(this));
        updatePhoneResultBinding.tvInfo.setText(getString(R.string.update_phone_success_desc, CheckUtil.hidePhoneNumber(SpHp.getLogin(SpKey.LOGIN_MOBILE))));
        updatePhoneResultBinding.tvComplete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == updatePhoneResultBinding.tvComplete) {
            onBackPressed();
        } else {
            super.onClick(v);
        }
    }
}
