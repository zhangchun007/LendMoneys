package com.haiercash.gouhua.activity.edu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.ocr.BaseOCRActivity;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *
 */
public class NameAuthIdCardPatchActivity extends BaseOCRActivity {

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    private String custName;
    private String fromProcedure;

    @Override
    protected int getLayout() {
        return R.layout.activity_nameauth_idcard_patch;
    }

    public static void startPatchIdCard(BaseActivity baseActivity, Intent intent) {
        intent.setClass(baseActivity, NameAuthIdCardPatchActivity.class);
        baseActivity.startActivityForResult(intent, 1000);
    }

    private void setTitle() {
        fromProcedure = getIntent().getStringExtra("tag");
        if (!TextUtils.isEmpty(fromProcedure) && "EDJH".equals(fromProcedure)) {
            setTitle("额度申请");
        } else {
            tvTips.setVisibility(View.INVISIBLE);
        }
        boolean borrowStep = getIntent().getBooleanExtra("borrowStep", false);
        if (borrowStep) {
            setTitle("完善信息");
        }
    }


    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        SystemUtils.setWindowSecure(this);
        setTitle();
        custName = SpHp.getUser(SpKey.USER_CUSTNAME);
        tvName.setText(UiUtil.getStr("姓名：", custName));
    }

    @Override
    public void onBackPressed() {
        if (isEDJH()) {
            EduCommon.onBackPressed(this, "要实名认证", getPageCode(), "身份证实名页面");
        } else {
            finish();
        }
    }

    private boolean isEDJH() {
        return "EDJH".equals(fromProcedure);
    }

    @Override
    protected String getPageCode() {
        return isEDJH() ? "CertificationOCRPage_gouhua" : super.getPageCode();
    }

    @OnClick({R.id.iv_nameauth, R.id.tv_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_nameauth:
            case R.id.tv_start:
                scanIdCard(CardFront);
                break;
        }
    }


    @Override
    public void ocrSuccess(int size) {
        if (CardFront == size) {
            scanIdCard(CardBack);
        } else {
            idInfo.setCustName(custName);
            Intent intent = getIntent();
            intent.putExtra("info", idInfo);
            intent.setClass(this, NameAuthIdCardActivity.class);
            intent.putExtra("style", NameAuthIdCardActivity.Supplement);
            startActivity(intent);
            finish();
        }
    }

}
