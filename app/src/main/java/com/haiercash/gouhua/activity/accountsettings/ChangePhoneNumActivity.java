package com.haiercash.gouhua.activity.accountsettings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.fragments.mine.CheckCertNoFragment;
import com.haiercash.gouhua.tplibrary.livedetect.FaceCheckActivity;

import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/8/29<br/>
 * 描    述：修改登录手机号<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ChangePhoneNumActivity extends BaseActivity {

    @Override
    protected int getLayout() {
        return R.layout.activity_change_phone;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("修改登录手机号");
        //// 如果没有实名认证，测用户无法通过银行卡信息去修改手机号，故需要影藏功能 17.8.11
        //if (!TextUtils.isEmpty(SpHelper.getInstance().readMsgFromSp(SpKey.USER, SpKey.USER_CUSTNO))) {
        //    findViewById(R.id.tv_new_mobile).setVisibility(View.VISIBLE);
        //} else {
        //    findViewById(R.id.tv_new_mobile).setVisibility(View.GONE);
        //}
    }

    @OnClick({R.id.tv_mobile, R.id.tv_new_mobile})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_mobile://原手机号仍在使用
                startActivity(new Intent(this, ChangeOld1PhoneActivity.class));
                break;
            case R.id.tv_new_mobile://原手机号不再使用
                Bundle extra = new Bundle();
                extra.putSerializable(CheckCertNoFragment.PAGE_KEY, FaceCheckActivity.class);
                extra.putSerializable(FaceCheckActivity.ID, ChangeOld2PhoneActivity.class);
                extra.putString(FaceCheckActivity.FROM, "XGSJH");
                ContainerActivity.to(this, CheckCertNoFragment.class.getSimpleName(), extra);
                break;
            default:
                break;
        }
        finish();
    }
}
