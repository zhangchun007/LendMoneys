package com.haiercash.gouhua.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.accountsettings.ChangeNewLoginPasswordActivity;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/10/11<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class CheckRealThreeFragment extends BaseFragment {
    @BindView(R.id.name)
    EditText mEditTextName;
    @BindView(R.id.idcard)
    EditText mEditTextIdCard;
    @BindView(R.id.tvSetOK)
    TextView mTextViewSetOK;

    private String mobile;
    private String smsToken;

    public static BaseFragment newInstance(Bundle extra) {
        CheckRealThreeFragment fragment = new CheckRealThreeFragment();
        if (extra != null) {
            fragment.setArguments(extra);
        }
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_check_certno;
    }

    @Override
    protected void initEventAndData() {
        SystemUtils.setWindowSecure(mActivity);
        mActivity.setTitle("验证身份");
        if (getArguments() == null) {
            UiUtil.toast("账号异常，请退出重试");
            mActivity.finish();
            return;
        }
        mobile = getArguments().getString("mobile");
        smsToken = getArguments().getString("smsToken");
    }

    @OnClick({R.id.tvSetOK})
    public void onViewClicked(View view) {
        String name = mEditTextName.getText().toString();
        String idCard = mEditTextIdCard.getText().toString();
        if (CheckUtil.isEmpty(name)) {
            UiUtil.toast("姓名不能为空");
            return;
        }
        if (CheckUtil.isEmpty(idCard)) {
            UiUtil.toast("身份证号不能为空");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("mobile", EncryptUtil.simpleEncrypt(mobile));//登录手机号
        map.put("custName", EncryptUtil.simpleEncrypt(name));//姓名
        map.put("certNo", EncryptUtil.simpleEncrypt(idCard));//身份证号
        map.put("needToken", "1");//返回实名验证成功token
        netHelper.postService(ApiUrl.POST_CHECK_THREE_INFO, map);
    }

    @OnTextChanged(value = {R.id.name, R.id.idcard}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        String name = mEditTextName.getText().toString();
        String idCard = mEditTextIdCard.getText().toString();
//        if (CheckUtil.isEmpty(name) || CheckUtil.isEmpty(idCard)) {//CheckUtil.isEmpty(smscode) ||CheckUtil.isEmpty(name) ||CheckUtil.isEmpty(idcard) ||CheckUtil.isEmpty(password) ||CheckUtil.isEmpty(password2)
//            mTextViewSetOK.setEnabled(false);
//        } else {
//            mTextViewSetOK.setEnabled(true);
//        }
        mTextViewSetOK.setEnabled(!(CheckUtil.isEmpty(name) || CheckUtil.isEmpty(idCard)));
    }

    @Override
    public void onSuccess(Object t, String url) {
        showProgress(false);
        Intent intent = new Intent(mActivity, ChangeNewLoginPasswordActivity.class);
        intent.putExtra(ChangeNewLoginPasswordActivity.PWD_TAG, "WJDLMM");
        intent.putExtra("mobile", mobile);
        intent.putExtra("smsToken", smsToken);
        startActivity(intent);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        //super.onError(error, url);
        showProgress(false);
        showDialog("请仔细检查填写本人信息真实正确，确认信息无误的情况下仍无法通过，可以通过申诉流程进行密码找回。",
                "账号申诉", "确定", (dialog, which) -> {
                    if (which == 1) {
                        //UiUtil.toast("进入申诉流程。。。。");
                        Bundle extra = new Bundle();
                        extra.putString("doType", "Appeal");
                        extra.putString("mobile", mobile);
                        extra.putString("smsToken", smsToken);
                        ContainerActivity.to(mActivity, CheckRealFourFragment.class.getSimpleName(), extra);
                    }
                });
    }
}
