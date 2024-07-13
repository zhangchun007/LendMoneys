package com.haiercash.gouhua.beans.login;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Limige on 2017-03-07.
 * 3.4.23.	(POST) 校验验证码并绑定设备号
 * 请求bean
 */

public class UserInfoBean implements Serializable {
    private String isBanding;                       // 是否绑定   String    Y 已绑定 N 未绑定
    private String openId;                          //微信授权用户唯一标识
    private String userId;
    private String clientSecret;
    private String registChannel;
    private String mobile;
    private String state;
    private String provider;
    private String lastLoginDays;
    private HashMap<String, Object> token;
    private String isRealInfo;
    private HashMap<String, String> realInfo;
    private String isNewDevice;
    private String retFlag;
    private HashMap<String, Object> h5LoginInfo;
    private String passwordSet;//是否设置过登录密码,Y-是，N-否
    private String isNewUser;//是否新用户,Y-是，N-否
    private String processId;
    private String hasAuth; //是否开启了设备权限请求弹框

    //申诉提交接口才返---start
    private String mashCertNo;//掩码身份号码
    private String mashMobile;//掩码手机号
    private String compensationFlag;
    //申诉提交接口才返---end


    public UserInfoBean() {
    }

    public UserInfoBean(String isBanding, String openId, String userId, String clientSecret, String registChannel, String mobile, String state, String provider, String lastLoginDays, HashMap<String, Object> token, String isRealInfo, HashMap<String, String> realInfo, String isNewDevice, String retFlag, HashMap<String, Object> h5LoginInfo, String passwordSet, String isNewUser, String processId, String hasAuth, String mashCertNo, String mashMobile, String compensationFlag, String custNo, String longTimeNotLogin, String ideaCode, String registerVector, String business, String channelNo, String regisChannel, String appDownFrom, String pwdStatus) {
        this.isBanding = isBanding;
        this.openId = openId;
        this.userId = userId;
        this.clientSecret = clientSecret;
        this.registChannel = registChannel;
        this.mobile = mobile;
        this.state = state;
        this.provider = provider;
        this.lastLoginDays = lastLoginDays;
        this.token = token;
        this.isRealInfo = isRealInfo;
        this.realInfo = realInfo;
        this.isNewDevice = isNewDevice;
        this.retFlag = retFlag;
        this.h5LoginInfo = h5LoginInfo;
        this.passwordSet = passwordSet;
        this.isNewUser = isNewUser;
        this.processId = processId;
        this.hasAuth = hasAuth;
        this.mashCertNo = mashCertNo;
        this.mashMobile = mashMobile;
        this.compensationFlag = compensationFlag;
        this.custNo = custNo;
        this.longTimeNotLogin = longTimeNotLogin;
        this.ideaCode = ideaCode;
        this.registerVector = registerVector;
        this.business = business;
        this.channelNo = channelNo;
        this.regisChannel = regisChannel;
        this.appDownFrom = appDownFrom;
        this.pwdStatus = pwdStatus;
    }

    public String getCompensationFlag() {
        return compensationFlag;
    }

    public void setCompensationFlag(String compensationFlag) {
        this.compensationFlag = compensationFlag;
    }

    public String getHasAuth() {
        return hasAuth;
    }

    public void setHasAuth(String hasAuth) {
        this.hasAuth = hasAuth;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getMashCertNo() {
        return mashCertNo;
    }

    public void setMashCertNo(String mashCertNo) {
        this.mashCertNo = mashCertNo;
    }

    public String getMashMobile() {
        return mashMobile;
    }

    public void setMashMobile(String mashMobile) {
        this.mashMobile = mashMobile;
    }

    public String getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(String isNewUser) {
        this.isNewUser = isNewUser;
    }

    public String getPasswordSet() {
        return passwordSet;
    }

    public void setPasswordSet(String passwordSet) {
        this.passwordSet = passwordSet;
    }

    public HashMap<String, Object> getH5LoginInfo() {
        return h5LoginInfo;
    }

    public void setH5LoginInfo(HashMap<String, Object> h5LoginInfo) {
        this.h5LoginInfo = h5LoginInfo;
    }

    //public String avatarUrl;
    //public String userName;  //3.5.6登录接口返回的该字段加密，若后期使用需要在保存信息时候先解密该属性
    //public String nickName;  //同上
    //public String email;     //同上
    //public String userDesc;
    //public String registDt;
    //public List<String> deviceIdList;
    private String custNo;  //U01999 代表降期用户 会返回body body中有custNo
    private String longTimeNotLogin;//长时间未登录（当前为三个月，90天） Y-是,N-否


    private String ideaCode;            //创意code		否
    private String registerVector;      //注册载体		否
    private String business;            //运营业务线		否
    private String channelNo;           //渠道号		是	如果运营业务线字段不为空，则按照运营业务线做转换，否则该字段同前端请求头中的渠道号
    private String regisChannel;        //注册渠道		Y
    private String appDownFrom;         //投放渠道		Y
    private String pwdStatus;  //区分是否有设置过登录密码 N : 没设置过   Y : 设置过

    public String getPwdStatus() {
        return pwdStatus;
    }

    public void setPwdStatus(String pwdStatus) {
        this.pwdStatus = pwdStatus;
    }

    public String getRetFlag() {
        String newFlag = retFlag;
        if (newFlag.contains("-")) {
            int startIndex = newFlag.indexOf("-") + 1;
            int enDIndex = newFlag.lastIndexOf("-");
            if (newFlag.length() >= enDIndex) {
                newFlag = newFlag.substring(startIndex, enDIndex);
            }
        }
        return newFlag;
    }

    public void setRetFlag(String retFlag) {
        this.retFlag = retFlag;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }


    @Override
    public String toString() {
        return "DengLuYangZheng_Bean{" + "clientSecret='" + clientSecret + '\'' + "userId='" + userId + '\'' + '}';
    }

    public String getIsBanding() {
        return isBanding;
    }

    public void setIsBanding(String isBanding) {
        this.isBanding = isBanding;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRegistChannel() {
        return registChannel;
    }

    public void setRegistChannel(String registChannel) {
        this.registChannel = registChannel;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLastLoginDays() {
        return lastLoginDays;
    }

    public void setLastLoginDays(String lastLoginDays) {
        this.lastLoginDays = lastLoginDays;
    }

    public HashMap<String, Object> getToken() {
        return token;
    }

    public void setToken(HashMap<String, Object> token) {
        this.token = token;
    }

    public String getIsRealInfo() {
        return isRealInfo;
    }

    public void setIsRealInfo(String isRealInfo) {
        this.isRealInfo = isRealInfo;
    }

    public HashMap<String, String> getRealInfo() {
        return realInfo;
    }

    public void setRealInfo(HashMap<String, String> realInfo) {
        this.realInfo = realInfo;
    }

    public String getIsNewDevice() {
        String isNew = isNewDevice;
        if (isNew.contains("-")) {
            int index = isNew.indexOf("-") + 1;
            if (isNew.length() >= index + 1) {
                isNew = isNew.substring(index, index + 1);
            }
        }
        return isNew;
    }

    public void setIsNewDevice(String isNewDevice) {
        this.isNewDevice = isNewDevice;
    }

    public String getIdeaCode() {
        return ideaCode;
    }

    public void setIdeaCode(String ideaCode) {
        this.ideaCode = ideaCode;
    }

    public String getRegisterVector() {
        return registerVector;
    }

    public void setRegisterVector(String registerVector) {
        this.registerVector = registerVector;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getRegisChannel() {
        return regisChannel;
    }

    public void setRegisChannel(String regisChannel) {
        this.regisChannel = regisChannel;
    }

    public String getAppDownFrom() {
        return appDownFrom;
    }

    public void setAppDownFrom(String appDownFrom) {
        this.appDownFrom = appDownFrom;
    }

    public String getLongTimeNotLogin() {
        return longTimeNotLogin;
    }

    public void setLongTimeNotLogin(String longTimeNotLogin) {
        this.longTimeNotLogin = longTimeNotLogin;
    }

}
