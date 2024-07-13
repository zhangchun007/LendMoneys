package com.haiercash.gouhua.activity.accountsettings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.AntiHijackingUtil;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.login.ChangeLoginPwd;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.widget.NewNKeyBoardTextField;

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
 * 创建日期：2018/8/31<br/>
 * 描    述：设置新登录密码<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ChangeNewLoginPasswordActivity extends BaseActivity implements View.OnFocusChangeListener {
    /**
     * WJDLMM:忘记登录密码<br/>
     * XGDLMM:修改登录密码
     * SZDLMM:设置登录密码
     */
    public static final String PWD_TAG = "ChangePwdOrForgetPwd";
    @BindView(R.id.tvSetFinish)
    TextView mSet;
    @BindView(R.id.edt_new_pwd)
    NewNKeyBoardTextField edtNewPwd;
    @BindView(R.id.edt_new_pwd_again)
    NewNKeyBoardTextField edtNewPwdAgain;
    private View linePwd, linePwdAgain;

    private String oldLoginPwd;

    @Override
    protected int getLayout() {
        return R.layout.activity_change_new_login_pwd;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        SystemUtils.setWindowSecure(this);
        oldLoginPwd = getIntent().getStringExtra("oldLoginPwd");
        TextView subtitle = findViewById(R.id.tv_change_new_login_pwd_subtitle);
        if ("SZDLMM".equals(getIntent().getStringExtra(PWD_TAG))) {
            setTitle("设置密码");
            subtitle.setText("设置登录密码");
        } else if ("XGDLMM".equals(getIntent().getStringExtra(PWD_TAG))) {
            setTitle("修改密码");
            subtitle.setText("设置新密码");
        } else if ("WJDLMM".equals(getIntent().getStringExtra(PWD_TAG))) {
            setTitle("忘记密码");
            subtitle.setText("设置新密码");
        } else {
            setTitle("设置登录密码");
            subtitle.setText("设置新密码");
        }
        subtitle.setTypeface(FontCustom.getMediumFont(this));
        linePwd = findViewById(R.id.line_new_pwd);
        linePwdAgain = findViewById(R.id.line_new_pwd_again);
        mSet.setTypeface(FontCustom.getMediumFont(this));
        mSet.setEnabled(false);
        edtNewPwd.setNewNKeyTextSize(16f, 13f);
        edtNewPwd.setEditClearIcon(true);
        edtNewPwdAgain.setNewNKeyTextSize(16f, 13f);
        edtNewPwdAgain.setEditClearIcon(true);
        setBarLeftImage(0, v -> showDialog("确定退出修改登录密码？", "确定", "取消", (dialog, which) -> {
            if (which == 1) {
                onBackPressed();
            }
        }));
        edtNewPwd.setKeyBoardListener(new NewNKeyBoardTextField.MyNKeyBoardListener(edtNewPwd));
        edtNewPwd.addOnFocusChangeLis(this);
        edtNewPwdAgain.setKeyBoardListener(new NewNKeyBoardTextField.MyNKeyBoardListener(edtNewPwdAgain));
        edtNewPwdAgain.addOnFocusChangeLis(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backOutTransition();
    }

    @Override
    protected void onPause() {
        AntiHijackingUtil.activityPageOnPause(this);
        super.onPause();
    }

    @OnCheckedChanged(R.id.passwordCheckBox)
    public void onCheckedChanged(boolean isChecked) {
//        if (isChecked) {
//            edtNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//            edtNewPwdAgain.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//        } else {
//            edtNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            edtNewPwdAgain.setTransformationMethod(PasswordTransformationMethod.getInstance());
//        }
        edtNewPwdAgain.setSelection(edtNewPwdAgain.getNKeyboardText().length());
    }

    private void postInterface() {
        Log.e("-------->", "开始传递设置交易密码数据");
        String uId = SpHp.getLogin(SpKey.LOGIN_USERID);
        String pwdNew = CheckUtil.clearBlank(edtNewPwd.getNKeyboardText());
        String pwdAgain = CheckUtil.clearBlank(edtNewPwdAgain.getNKeyboardText());
        if (CheckUtil.isEmpty(uId)) {
            showDialog("账号异常，请退出重试");
            return;
        }
        ChangeLoginPwd map = new ChangeLoginPwd();
        map.setUserId(EncryptUtil.simpleEncrypt(uId));
        map.setPassword(oldLoginPwd);
        map.setNewPassword(pwdNew);
        map.setSecondPassword(pwdAgain);
        map.setType("set");
        map.setDeviceType("AND");
        netHelper.putService(ApiUrl.url_changeloginpwd_put, map);
        showProgress(true);
    }

    @Override
    @OnClick(R.id.tvSetFinish)
    public void onClick(View v) {
        if (v.getId() == R.id.tvSetFinish) {
            if (CheckUtil.isEmpty(edtNewPwd.getNKeyboardText().trim()) || CheckUtil.isEmpty(edtNewPwdAgain.getNKeyboardText().trim())) {
                UiUtil.toast("请输入8-20位字母和数字的组合");
                return;
            } else if ((edtNewPwd.getNKeyboardTextLength() != edtNewPwdAgain.getNKeyboardTextLength())) {
                UiUtil.toast("两次设置密码不一致，请重新输入");
                return;
            }
            String smsToken = getIntent().getStringExtra("smsToken");
            String fourKeysToken = getIntent().getStringExtra("fourKeysToken");
            String mobile = getIntent().getStringExtra("mobile");
            Map<String, String> map = new HashMap<>();
            if (!CheckUtil.isEmpty(smsToken)) {
                map.put("mobile", EncryptUtil.simpleEncrypt(mobile));//登录手机号
                map.put("smsToken", smsToken);//登录手机号短信码验证成功的凭证信息
                map.put("newPassword", CheckUtil.clearBlank(edtNewPwd.getNKeyboardText()));//新登录密码
                map.put("secondPassword", CheckUtil.clearBlank(edtNewPwdAgain.getNKeyboardText()));//确认新登录密码
                map.put("deviceType", "AND");//设备类型
                map.put("deviceId", EncryptUtil.simpleEncrypt(SystemUtils.getDeviceID(this)));//设备号
                netHelper.postService(ApiUrl.POST_UPDATE_LOGIN_PWD, map);
            } else if (!CheckUtil.isEmpty(fourKeysToken)) {
                map.put("mobile", EncryptUtil.simpleEncrypt(mobile));//登录手机号
                map.put("fourKeysToken", fourKeysToken);//实名四要素验证成功的凭证信息
                map.put("newPassword", CheckUtil.clearBlank(edtNewPwd.getNKeyboardText()));//新登录密码
                map.put("secondPassword", CheckUtil.clearBlank(edtNewPwdAgain.getNKeyboardText()));//确认新登录密码
                map.put("deviceType", "AND");//设备类型
                map.put("deviceId", EncryptUtil.simpleEncrypt(SystemUtils.getDeviceID(this)));//设备号
                netHelper.postService(ApiUrl.POST_UPDATE_LOGIN_PWD_BY_REAL_INFO, map);
            } else {
                postInterface();
            }
        }
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (success == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        postData();
        if ("SZDLMM".equals(getIntent().getStringExtra(PWD_TAG))) {
            UiUtil.toast("登录密码设置成功");
        } else if ("XGDLMM".equals(getIntent().getStringExtra(PWD_TAG))) {
            UiUtil.toast("密码重置成功");
        } else {
            CommomUtils.clearSp();
            UiUtil.toast("密码重置成功");
        }
        showProgress(false);
        @SuppressWarnings("unchecked") Map<String, String> map = (Map<String, String>) success;
        if (!CheckUtil.isEmpty(map.get("userId"))) {
            AppApplication.userid = map.get("userId");
        }
        RiskInfoUtils.updateRiskBro09Or06(true, getIntent().getStringExtra(PWD_TAG));
        if ("SZDLMM".equals(getIntent().getStringExtra(PWD_TAG)) || "XGDLMM".equals(getIntent().getStringExtra(PWD_TAG))) {
            SpHp.saveSpLogin(SpKey.LOGIN_PASSWORD_STATUS, "Y");
            onBackPressed();
        } else {
            LoginSelectHelper.closeExceptMainAndToLogin(this);
        }
    }

    private void postData() {
        if ("WJDLMM".equals(getIntent().getStringExtra(PWD_TAG))) {
            RiskNetServer.startRiskServer1(this, "forget_login password_modify_complete", "", 0);
        } else if ("XGDLMM".equals(getIntent().getStringExtra(PWD_TAG))) {
            RiskNetServer.startRiskServer1(this, "modify_login_password_complete", "", 0);
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
//        RiskInfoUtils.updateRiskBro09Or06(false, getIntent().getStringExtra(PWD_TAG));
        showProgress(false);
        if (ApiUrl.url_changeloginpwd_put.equals(url)) {
            UiUtil.toast(error.getHead().getRetMsg());
        } else if (ApiUrl.POST_UPDATE_LOGIN_PWD.equals(url)) {
            //A182560 请求参数无效
            //A182561 登录手机与验证码token不匹配
            //A182562 smsToken超时（目前限制15分钟有效），客户端需要重新走流程
            super.onError(error, url);
        } else if (ApiUrl.POST_UPDATE_LOGIN_PWD_BY_REAL_INFO.equals(url)) {
            //A182550 请求参数无效
            //A182551 登录手机号与实名信息不匹配
            //A182552 fourKeysToken超时（目前限制15分钟有效）
            super.onError(error, url);
        } else {
            super.onError(error, url);
        }
    }

    @OnTextChanged(value = {R.id.edt_new_pwd, R.id.edt_new_pwd_again}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        mSet.setEnabled(edtNewPwd.getNKeyboardText().length() > 0 && (edtNewPwdAgain.getVisibility() != View.VISIBLE || edtNewPwdAgain.getNKeyboardText().length() > 0));
    }

    @Override
    protected void onDestroy() {
        if (edtNewPwd != null) {
            edtNewPwd.destoryNKeyboard();
        }
        if (edtNewPwdAgain != null) {
            edtNewPwdAgain.destoryNKeyboard();
        }
        super.onDestroy();
    }

    @Override
    protected void backOutTransition() {
        super.backOutTransition();
        if ("SZDLMM".equals(getIntent().getStringExtra(PWD_TAG)) || "XGDLMM".equals(getIntent().getStringExtra(PWD_TAG))) {
            super.backOutTransition();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int color = hasFocus ? 0xFF606166 : 0xFFE8EAEF;
        if (v == edtNewPwd) {
            linePwd.setBackgroundColor(color);
        } else if (v == edtNewPwdAgain) {
            linePwdAgain.setBackgroundColor(color);
        }
    }
}
