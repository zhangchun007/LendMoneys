package com.haiercash.gouhua.activity.accountsettings;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.HmacSHA256Utils;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.activity.login.LoginNetHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.login.UserInfoBean;
import com.haiercash.gouhua.beans.postputbean.ZhiFu_Password;
import com.haiercash.gouhua.fragments.mine.CheckCertNoFragment;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.livedetect.FaceCheckActivity;
import com.haiercash.gouhua.utils.LoginUserHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.widget.NewNKeyBoardTextField;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/8/30<br/>
 * 描    述：验证登录密码或者验证交易密码<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class VerifyPwdActivity extends BaseActivity {
    public static final String PAGE_KEY = "VerifyPwdPageKey";

    @BindView(R.id.tvSetOK)
    TextView mSet;
    @BindView(R.id.edt_old_pwd)
    NewNKeyBoardTextField edtOldPwd;
    @BindView(R.id.tv_forget_pwd)
    TextView tv_forget_pwd;

    private String userId;
    private String oldpassword;
    private String title;
    private Serializable pageClass;

    @Override
    protected int getLayout() {
        return isFromUpdateLoginPwd() ? R.layout.activity_vertify_pwd_from_update_login_pwd : R.layout.activity_vertify_pwd;
    }

    private boolean isFromUpdateLoginPwd() {
        return "XGDLMM".equals(getIntent().getStringExtra(ChangeNewLoginPasswordActivity.PWD_TAG));
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        SystemUtils.setWindowSecure(this);
        //edtOldPwd.setInputType(InputType.TYPE_CLASS_NUMBER);
        pageClass = getIntent().getSerializableExtra(PAGE_KEY);
        title = getIntent().getStringExtra("title");
        setTitle(CheckUtil.isEmpty(title) ? "验证密码" : title);
        TextView tv_subtitle = findViewById(R.id.tv_subtitle);
        if (tv_subtitle != null) {
            tv_subtitle.setTypeface(FontCustom.getMediumFont(this));
        }
        TextView tv_mobile_detail = findViewById(R.id.tv_mobile_detail);
        if (tv_mobile_detail != null) {
            tv_mobile_detail.setText(getResources().getString(R.string.update_login_pwd_verify_pwd_detail, CheckUtil.getPhone(SpHp.getLogin(SpKey.LOGIN_MOBILE))));
        }
        edtOldPwd.setEditClearIcon(true);
        if ("验证交易密码".equals(title)) {
            edtOldPwd.setHint("请输入原交易密码");
            tv_forget_pwd.setText("忘记交易密码？");
        }

        mSet.setTypeface(FontCustom.getMediumFont(this));
        mSet.setEnabled(false);
        userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        edtOldPwd.setNewNKeyTextSize(16f, 13f);
        edtOldPwd.setKeyBoardListener(new NewNKeyBoardTextField.MyNKeyBoardListener(edtOldPwd));
        edtOldPwd.post(() -> {
            edtOldPwd.requestFocus();
            edtOldPwd.showNKeyboard();
        });
    }

    @OnCheckedChanged(R.id.passwordCheckBox)
    public void onCheckedChanged(boolean isChecked) {
        if (isChecked) {
            edtOldPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            edtOldPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        edtOldPwd.setSelection(edtOldPwd.getNKeyboardText().length());
    }

    @OnClick({R.id.tvSetOK, R.id.tv_forget_pwd})
    public void viewOnClick(View v) {
        switch (v.getId()) {
            case R.id.tvSetOK:
                if ("验证交易密码".equals(title)) {
                    if (CheckUtil.isEmpty(userId)) {
                        showDialog("账号异常，请退出重试");
                        return;
                    }
                    String deviceId = SystemUtils.getDeviceID(this);
                    if (CheckUtil.isEmpty(deviceId)) {
                        showDialog("deviceId获取失败");
                        return;
                    }
                    showProgress(true);
                    Map<String, String> map = new HashMap<>();
                    map.put("userId", EncryptUtil.simpleEncrypt(userId));//用户账号
                    map.put("payPasswd", edtOldPwd.getNKeyboardText().trim());//交易密码
                    map.put("deviceId", RSAUtils.encryptByRSA(deviceId));
                    //必须放在map最后一行，是对整个map参数进行签名对
                    map.put("sign", HmacSHA256Utils.buildNeedSignValue(map));
                    netHelper.getService(ApiUrl.URL_CHECK_PAY_SECRET, map, ZhiFu_Password.class, true);
                } else {
                    customerLogin();
                }
                break;
            case R.id.tv_forget_pwd:
                if ("验证交易密码".equals(title)) {//修改交易密码流程
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(CheckCertNoFragment.PAGE_KEY, FaceCheckActivity.class);
                    bundle.putSerializable(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
                    bundle.putString(SetTransactionPwdActivity.TAG, "WJJYMM");
                    bundle.putString(FaceCheckActivity.FROM, "WJJYMM");
                    ContainerActivity.to(this, CheckCertNoFragment.class.getSimpleName(), bundle);
                } else {//修改登录密码流程
                    Bundle bundle = new Bundle();
                    bundle.putString(ChangeNewLoginPasswordActivity.PWD_TAG, "WJDLMM");
                    bundle.putString("mobileNo", SpHp.getLogin(SpKey.LOGIN_MOBILE));
                    String certNo = SpHp.getUser(SpKey.USER_CERTNO);
                    String custNo = SpHp.getUser(SpKey.USER_CUSTNO);
                    if (CheckUtil.isEmpty(certNo)) {//没有实名认证
                        Intent intent = new Intent(this, SetLoginPwdVerifyMobileActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        // ContainerActivity.to(this, ResetPasswordForSms.class.getSimpleName(), bundle);
                    } else {//已实名
                        bundle.putString("custNo", custNo);
                        bundle.putString("mobileNo", SpHp.getLogin(SpKey.LOGIN_MOBILE));
                        bundle.putSerializable(CheckCertNoFragment.PAGE_KEY, SetLoginPwdVerifyMobileActivity.class);
                        ContainerActivity.to(this, CheckCertNoFragment.class.getSimpleName(), bundle);
                    }
                }
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 验证登录密码接口
     */
    private void customerLogin() {
        edtOldPwd.hideNKeyboard();
        oldpassword = edtOldPwd.getNKeyboardText().trim();
        if (CheckUtil.isEmpty(userId)) {
            showDialog("账号异常，请退出重试");
            return;
        }
        if (CheckUtil.isEmpty(oldpassword)) {
            showDialog("请输入原登录密码");
            return;
        }
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("deviceType", "AND");
        map.put("type", "set");
        map.put("userId", RSAUtils.encryptByRSA(userId));
        map.put("password", oldpassword);
        map.put("timeStamp", System.currentTimeMillis() + "");
        //必须放在map最后一行，是对整个map参数进行签名对
        map.put("sign", HmacSHA256Utils.buildNeedSignValue(map));
        netHelper.putService(ApiUrl.loginUrl, map, UserInfoBean.class, true);
    }

    @Override
    public void onSuccess(Object success, String url) {
        showProgress(false);
        if (ApiUrl.loginUrl.equals(url)) {
            UserInfoBean bean = LoginUserHelper.getDecrypt((UserInfoBean) success);
            if (!"00000".equals(bean.getRetFlag())) {
                showDialog("网络异常");
                return;
            }
            Intent intent = (getIntent() == null ? new Intent() : getIntent());
            intent.putExtra("oldLoginPwd", oldpassword);//ChangeNewLoginPasswordActivity界面所需的参数
            intent.setClass(this, (Class<?>) pageClass);
            startActivity(intent);
//            if (isFromUpdateLoginPwd()) {
//                startInTransition();
//            }
        } else if (ApiUrl.URL_CHECK_PAY_SECRET.equals(url)) {
            Intent setPwd = new Intent(this, (Class<?>) pageClass);
            setPwd.putExtra(SetTransactionPwdActivity.TAG, "XGJYMM");
            startActivity(setPwd);
        }
        finish();
    }

    @Override
    public void onError(BasicResponse error, String url) {
        showProgress(false);
        if (ApiUrl.loginUrl.equals(url)) {
            if (LoginNetHelper.isDownStageUser(this, error)) {
                return;
            }
            UiUtil.toast(error.getHead().getRetMsg());
        } else if (ApiUrl.URL_CHECK_PAY_SECRET.equals(url)) {
            Map<String, Double> map = (Map<String, Double>) error.getBody();
            if (map != null && map.containsKey("payErrorNumber") && map.containsKey("maxPayErrorNumber")) {//交易密码验证错误次数
                int num = Double.valueOf(map.get("maxPayErrorNumber")).intValue() - Double.valueOf(map.get("payErrorNumber")).intValue();
                if (Double.valueOf(map.get("payErrorNumber")).intValue() <= 5) {
                    //1.用户输入前5次密码错误
                    showBtn2Dialog("交易密码输入错误", "重新输入", (dialog, which) -> {
                        //清空并重新输入
                        edtOldPwd.setText("");
                    }).setButtonTextColor(2, R.color.colorPrimary);
                } else if (Double.valueOf(map.get("payErrorNumber")).intValue() == 6 || Double.valueOf(map.get("payErrorNumber")).intValue() == 7) {

                    //2、用户输入第6/7次密码错误时，提示用户重新输入
                    showDialog("交易密码输入错误,\n   还可以输入" + num + "次", "忘记密码", "重新输入", (dialog, which) -> {
                        if (which == 1) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(CheckCertNoFragment.PAGE_KEY, FaceCheckActivity.class);
                            bundle.putSerializable(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
                            bundle.putString(SetTransactionPwdActivity.TAG, "WJJYMM");
                            ContainerActivity.to(VerifyPwdActivity.this, CheckCertNoFragment.class.getSimpleName(), bundle);
                            edtOldPwd.setText("");
                        } else {
                            //清空并重新输入
                            edtOldPwd.setText("");
                        }
                    }).setStandardStyle(4);
                }

            } else {
                //3、当用户第8次输入错误时，提示用户交易密码锁定，24小时后尝试，仅提供找回密码入口
                showDialog("交易密码输入错误次数过多,您\n的账号已被锁定,请点击忘记密\n码进行找回或明日重试", "忘记密码", "我知道了", (dialog, which) -> {
                    if (which == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(CheckCertNoFragment.PAGE_KEY, FaceCheckActivity.class);
                        bundle.putSerializable(FaceCheckActivity.ID, SetTransactionPwdActivity.class);
                        bundle.putString(SetTransactionPwdActivity.TAG, "WJJYMM");
                        ContainerActivity.to(VerifyPwdActivity.this, CheckCertNoFragment.class.getSimpleName(), bundle);
                        edtOldPwd.setText("");
                    } else {
                        edtOldPwd.setText("");
                        dialog.dismiss();
                    }
                }).setStandardStyle(4);
            }
        }
    }


    @OnTextChanged(value = R.id.edt_old_pwd, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        mSet.setEnabled(edtOldPwd.getNKeyboardText().length() > 0);
    }

    @Override
    protected void onDestroy() {
        if (edtOldPwd != null) {
            edtOldPwd.destoryNKeyboard();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backOutTransition();
    }

    @Override
    protected void backOutTransition() {
        if (isFromUpdateLoginPwd()) {
            super.backOutTransition();
        }
    }
}