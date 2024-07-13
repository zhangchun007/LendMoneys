package com.haiercash.gouhua.activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;

/**
 * 结清证明说明
 */
public class OpenDischargeActivity extends BaseActivity {
    @Override
    protected int getLayout() {
        return R.layout.activity_open_diacharge;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("结清证明开具说明");
    }
}
