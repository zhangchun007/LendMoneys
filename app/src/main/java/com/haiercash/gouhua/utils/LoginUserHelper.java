package com.haiercash.gouhua.utils;

import androidx.annotation.NonNull;

import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.beans.gesture.ValidateUserBean;
import com.haiercash.gouhua.beans.login.UserInfoBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.tplibrary.receiver.XGUntil;
import com.networkbench.agent.impl.NBSAppAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/6/10<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class LoginUserHelper {

    /**
     * 账号密码保存登陆相关信息<br/>
     * 微信授权登录成功
     */
    public static void saveLoginInfo(UserInfoBean login) {
        CommomUtils.clearSp();
        SpHp.saveSpLogin(SpKey.LOGIN_USERID, login.getUserId());
        AppApplication.userid = login.getUserId();
        NBSAppAgent.setUserIdentifier(login.getUserId());
        SpHp.saveSpState(SpKey.STATE_LOGINSTATE, "Y");
        SpHp.saveSpLogin(SpKey.LOGIN_MOBILE, login.getMobile());
        SpHp.saveSpLogin(SpKey.LOGIN_PROCESS_ID, login.getProcessId());
        SpHp.saveSpLogin(SpKey.LOGIN_REGISTCHANNEL, login.getRegistChannel());
        SpHp.saveSpLogin(SpKey.LOGIN_PASSWORD_STATUS, login.getPasswordSet());
        SpHp.saveSpLogin(SpKey.CHANNEL_NO, login.getChannelNo());

        //用于刷新Token
        TokenHelper.getInstance().saveClientSecret(login.getClientSecret());
        TokenHelper.getInstance().seveMobile(login.getMobile());

        //保存Token
        Map<String, Object> token = login.getToken();
        String access_Token = String.valueOf(token.get("access_token"));
        Logger.e(access_Token);
        TokenHelper.getInstance().saveToken(access_Token);

        String refreshToken = String.valueOf(token.get("refreshToken"));
        TokenHelper.getInstance().saveRefreshToken(refreshToken);

        TokenHelper.getInstance().saveSmyParameter(login.getIdeaCode(), login.getRegisterVector(), login.getBusiness(), login.getChannelNo(), login.getRegisChannel(), login.getAppDownFrom());
        saveRealNameMsg(login.getRealInfo(), login.getIsRealInfo());

        SpHp.saveSpOther(SpKey.LAST_LOGIN_SUCCESS_MOBILE, login.getMobile());
        SpHp.saveSpOther(SpKey.LAST_LOGIN_SUCCESS_USERID, login.getUserId());

        //保存H5信息
        if (login.getH5LoginInfo() != null) {
            try {
                TokenHelper.getInstance().saveH5LoginInfo(JsonUtils.toJson(login.getH5LoginInfo()));
                //保存Token
                Map<String, Object> h5Token = (Map<String, Object>) login.getH5LoginInfo().get("token");
                String h5TokenValue = String.valueOf(h5Token.get("value"));
                TokenHelper.getInstance().saveH5Token(h5TokenValue);
                //保存processId
                String processId = login.getH5LoginInfo().get("processId") + "";
                TokenHelper.getInstance().saveH5ProcessId(processId);
            } catch (Exception e) {
                Logger.e("保存H5信息失败： " + e.getMessage());
            }
        }
        UMengUtil.registerGlobalProperty("true", AppApplication.userid);
        UMengUtil.signInOrOut();
//        GrowingIO 下降
//        if (saveRealNameMsg(login.getRealInfo(), login.getIsRealInfo())) {
//            GrowingIOUtils.setCustNo();
//        }
//        GrowingIOUtils.setUerId();

        XGUntil.registerPushAndAccount(AppApplication.CONTEXT, AppApplication.userid);
        RxBus.getInstance().post(new ActionEvent(ActionEvent.DEAL_WITH_PUSH_INFO));
    }

    /**
     * 手势密码、指纹、微信  登录<br/>
     * 包含启动页启动、五分钟锁屏
     */
    public static void saveLoginInfo(ValidateUserBean bean) {
        SpHp.saveSpLogin(SpKey.LOGIN_USERID, bean.getUserId());
        AppApplication.userid = bean.getUserId();
        SpHp.saveSpState(SpKey.STATE_LOGINSTATE, "Y");
        SpHp.saveSpLogin(SpKey.LOGIN_MOBILE, bean.getMobile());
        SpHp.saveSpLogin(SpKey.LOGIN_PROCESS_ID, bean.getProcessId());
        SpHp.saveSpLogin(SpKey.LOGIN_REGISTCHANNEL, bean.getRegistChannel());
        SpHp.saveSpLogin(SpKey.LOGIN_PASSWORD_STATUS, bean.getPasswordSet());

        TokenHelper.getInstance().seveMobile(bean.getMobile());

        SpHp.saveSpOther(SpKey.LAST_LOGIN_SUCCESS_MOBILE, bean.getMobile());
        SpHp.saveSpOther(SpKey.LAST_LOGIN_SUCCESS_USERID, bean.getUserId());

        UMengUtil.registerGlobalProperty("true", AppApplication.userid);
        UMengUtil.signInOrOut();
    }

    /**
     * 登录成功但是DeviceId变更
     */
    public static void loginSuccessButDeviceId(UserInfoBean body) {
        Logger.d("设备ID变更--需要清除上一个用户缓存的数据");
        CommomUtils.clearSp();
        ActivityUntil.finishActivityByPageKey(PagePath.ACTIVITY_GESTURES_SECRET);
        SpHp.saveSpLogin(SpKey.LOGIN_USERID, body.getUserId());
        SpHp.saveSpLogin(SpKey.LOGIN_MOBILE, body.getMobile());
        SpHp.saveSpLogin(SpKey.LOGIN_PROCESS_ID, body.getProcessId());
        SpHp.saveSpLogin(SpKey.LOGIN_PASSWORD_STATUS, body.getPasswordSet());

        SpHp.saveSpOther(SpKey.LAST_LOGIN_SUCCESS_MOBILE, body.getMobile());
        SpHp.saveSpOther(SpKey.LAST_LOGIN_SUCCESS_USERID, body.getUserId());

        //保存H5信息
        if (body.getH5LoginInfo() != null) {
            try {
                TokenHelper.getInstance().saveH5LoginInfo(JsonUtils.toJson(body.getH5LoginInfo()));
                //保存Token
                Map<String, Object> h5Token = (Map<String, Object>) body.getH5LoginInfo().get("token");
                String h5TokenValue = String.valueOf(h5Token.get("value"));
                TokenHelper.getInstance().saveH5Token(h5TokenValue);
                //保存processId
                String processId = body.getH5LoginInfo().get("processId") + "";
                TokenHelper.getInstance().saveH5ProcessId(processId);
            } catch (Exception e) {
                Logger.e("保存H5信息失败： " + e.getMessage());
            }
        }
        saveRealNameMsg(body.getRealInfo(), body.getIsRealInfo());
    }

    /**
     * 1、老卡签约成功后需要更新本地实名信息<br/>
     * 2、添加银行卡保存成功后需要更新本地实名信息
     */
    public static void saveRealNameMsg(Object realInfo) {
        try {
            Map map = (Map) realInfo;
            saveRealNameMsg(map.get("realInfo"), "Y");
        } catch (Exception e) {
            Logger.e("保存实名信息失败。。。" + e.getMessage());
        }
    }

    /**
     * 保存实名信息
     */
    private static boolean saveRealNameMsg(Object realInfo, String isRealInfo) {
        try {
            if (null != realInfo && "Y".equals(isRealInfo)) {
                Map map = (Map) realInfo;
                SpHp.saveUser(SpKey.USER_CUSTNO, String.valueOf(map.get("custNo")));
                SpHp.saveUser(SpKey.USER_CUSTNAME, String.valueOf(map.get("custName")));
                SpHp.saveUser(SpKey.USER_CERTNO, String.valueOf(map.get("certNo")));
                SpHp.saveUser(SpKey.USER_MOBILE, String.valueOf(map.get("mobile")));
                return true;
            }
            return false;
        } catch (Exception e) {
            Logger.e("保存实名信息失败。。。" + e.getMessage());
            return false;
        }
    }

    //Body 中 userId 和 mobile,custNo,
    //字段加密
    //realInfo 中 custNo certNo custName cardNo mobile acctName acctNo cardNo均加密返回
    //userName,nickName,email 这几个字段未使用，故处于注释状态，若后期需要用上，要在此处先解密
    public static UserInfoBean getDecrypt(UserInfoBean userInfoBean) {
        userInfoBean.setUserId(EncryptUtil.simpleDefaultDecrypt(userInfoBean.getUserId()));
        userInfoBean.setMobile(EncryptUtil.simpleDefaultDecrypt(userInfoBean.getMobile()));
        userInfoBean.setCustNo(EncryptUtil.simpleDefaultDecrypt(userInfoBean.getCustNo()));
        if (userInfoBean.getRealInfo() != null && userInfoBean.getIsRealInfo() != null && userInfoBean.getIsRealInfo().equals("Y")) {
            HashMap<String, String> realInfo = userInfoBean.getRealInfo();
            realInfo.put("custNo", EncryptUtil.simpleDefaultDecrypt(realInfo.get("custNo")));
            realInfo.put("certNo", EncryptUtil.simpleDefaultDecrypt(realInfo.get("certNo")));
            realInfo.put("custName", EncryptUtil.simpleDefaultDecrypt(realInfo.get("custName")));
            realInfo.put("cardNo", EncryptUtil.simpleDefaultDecrypt(realInfo.get("cardNo")));
            realInfo.put("mobile", EncryptUtil.simpleDefaultDecrypt(realInfo.get("mobile")));
            realInfo.put("acctName", EncryptUtil.simpleDefaultDecrypt(realInfo.get("acctName")));
            realInfo.put("acctNo", EncryptUtil.simpleDefaultDecrypt(realInfo.get("acctNo")));
        }
        return userInfoBean;
    }

    /**
     * 更改登录手机号成功后更新相关本地存储
     *
     * 实名手机号与登录手机号是两个概念，不相关!!!
     */
    public static void updateLoginMobileSuccessAndSave(@NonNull String newPhone) {
        SpHp.saveSpLogin(SpKey.LOGIN_MOBILE, newPhone);
        TokenHelper.getInstance().seveMobile(newPhone);
        SpHp.saveSpOther(SpKey.LAST_LOGIN_SUCCESS_MOBILE, newPhone);
        try {
            HashMap<String, Object> h5LoginInfo = JsonUtils.fromJson(TokenHelper.getInstance().getH5LoginInfo(), HashMap.class);
            h5LoginInfo.put("mobile", EncryptUtil.simpleEncrypt(newPhone));
            TokenHelper.getInstance().saveH5LoginInfo(JsonUtils.toJson(h5LoginInfo));
        } catch (Exception e) {
            //
        }
    }
}
