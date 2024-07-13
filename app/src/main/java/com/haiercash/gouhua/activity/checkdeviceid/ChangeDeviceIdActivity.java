package com.haiercash.gouhua.activity.checkdeviceid;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.account.CheckRealFourFragment;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.login.UserInfoBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.utils.BrAgentUtils;
import com.haiercash.gouhua.utils.LoginUserHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Limige on 2017-03-07.
 * 验证手机号添加设备号
 */

public class ChangeDeviceIdActivity extends BaseActivity {
    @BindView(R.id.tv_phone)
    TextView tvMobile;
    @BindView(R.id.message_code)
    EditText etCode;
    @BindView(R.id.bt_commit)
    TextView btCommit;

    private String phone;
    private String verifyNo;
    private String userId;
    private String deviceId;
    private UserInfoBean bean;
    private boolean isOneKeyLogin;

    @Override
    protected int getLayout() {
        return R.layout.activity_change_device_id;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("验证手机号");
        bean = (UserInfoBean) getIntent().getSerializableExtra("userInfoBean");
        isOneKeyLogin = getIntent().getBooleanExtra("isOneKeyLogin", false);
        phone = SpHp.getLogin(SpKey.LOGIN_MOBILE);
        userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        deviceId = SystemUtils.getDeviceID(this);
        tvMobile.setText(UiUtil.getStr("验证码将发送至", CheckUtil.hidePhoneNumber(phone)));
        //加载发送验证码模块
        registerSmsTime(R.id.get_code).setPhoneNum(phone).setBizCode("Y".equals(bean.getIsNewDevice()) ? "TMP_002" : "TMP_003");
        btCommit.setEnabled(false);
    }

    private void requestVerifyAndBindDeviceId() {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("userId", RSAUtils.encryptByRSA(userId));
        map.put("deviceId", RSAUtils.encryptByRSA(deviceId));
        map.put("verifyNo", RSAUtils.encryptByRSA(verifyNo));
        map.put("phone", RSAUtils.encryptByRSA(phone));
        map.put("deviceType", "AND");
        map.put("isRsa", "Y");
        map.put("iccId", RSAUtils.encryptByRSA(SystemUtils.getDeviceICCID(this)));//设备iccid
        netHelper.postService(ApiUrl.urlVerifyAndBindDeviceId, map, UserInfoBean.class, true);
    }

    @OnClick({R.id.bt_commit, R.id.tv_modify_phone})
    public void viewOnClick(View view) {
        if (view.getId() == R.id.bt_commit) {
            verifyNo = etCode.getText().toString();
            if (CheckUtil.isEmpty(verifyNo)) {
                showDialog("请输入验证码");
                UMengUtil.commonClickCompleteEvent("Confirmed_Click", "确定", "false", "请输入验证码", getPageCode());
                return;
            }
            if (CheckUtil.isEmpty(userId)) {
                UMengUtil.commonClickCompleteEvent("Confirmed_Click", "确定", "false", "账号异常", getPageCode());
                showDialog("账号异常，请退出重试");
                return;
            }
            if (CheckUtil.isEmpty(phone)) {
                UMengUtil.commonClickCompleteEvent("Confirmed_Click", "确定", "false", "账号异常", getPageCode());
                showDialog("账号异常，请退出重试");
                return;
            }
            if (CheckUtil.isEmpty(deviceId)) {
                UMengUtil.commonClickCompleteEvent("Confirmed_Click", "确定", "false", "deviceId获取失败", getPageCode());
                showDialog("deviceId获取失败");
                return;
            }
            requestVerifyAndBindDeviceId();
        } else {
            if (bean != null && "Y".equals(bean.getIsRealInfo())) {
                Bundle extra = new Bundle();
                extra.putString("doType", "ValidateUser");
                ContainerActivity.toForResult(mContext, CheckRealFourFragment.ID, extra, 98);
            } else {
                showDialog("如手机号不再使用，请使用可用手机号登录", "取消", "去登录", (dialog, which) -> {
                    if (which == 2) {
                        LoginSelectHelper.staticToGeneralLogin();
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (98 == requestCode && 1 == resultCode) {
            setResult(1, data);
            finish();
        } else if (0 == requestCode && 1 == resultCode) {  //实名验证成功后
            setResult(1, data);
            finish();
        }
    }


    @OnTextChanged(value = R.id.message_code, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        btCommit.setEnabled(etCode.getText().toString().trim().length() > 0);
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.urlVerifyAndBindDeviceId.equals(url)) {
            UMengUtil.commonClickCompleteEvent("Confirmed_Click", "确定", "true", "", getPageCode());
            Logger.e("后台登录成功");
            SpHelper.getInstance().deleteAllMsgFromSp(SpKey.NEED_CHANGE_DEVICE_STATE);
            UserInfoBean bean = LoginUserHelper.getDecrypt((UserInfoBean) success);
            LoginUserHelper.saveLoginInfo(bean);
            //百融风险采集登录
            BrAgentUtils.logInBrAgent(this);
            if (isOneKeyLogin) {//从一键登录进入时 新设备登录成功后风险数据上送
                RiskNetServer.startRiskServer1(ChangeDeviceIdActivity.this, "register_login_oauth_login", "", 2);
            }
            showProgress(false);

            Intent intent = new Intent();
            intent.putExtra("userInfoBean", bean);
            setResult(1,intent);
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            setResult(10086);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected String getPageCode() {
        return "MessageValidationPage";
    }
}
