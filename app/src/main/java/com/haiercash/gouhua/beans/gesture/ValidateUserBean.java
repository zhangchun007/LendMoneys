package com.haiercash.gouhua.beans.gesture;


/**
 * Created by StarFall on 2016/5/10.
 * 用户信息
 */
public class ValidateUserBean {

    /**
     * userId : 15066666666
     * mobile : 15066666666
     * userName : aa
     * nickName : null
     * userDesc : null
     * email : null
     * avatarUrl : null
     * state : N
     * registDt : 2016-04-16 15:14:34
     */

    public String userId;//用户账号
    public String mobile;//手机号
    public String userName;//用户名称
    public String nickName;//用户昵称
    public String userDesc;//用户描述
    public String email;//电子邮箱
    public String avatarUrl;//头像
    public String state;//状态 N：正常F：冻结
    public String registDt;//注册时间
    public String flag;//
    public String provider;//
    public String failedCount;//失败次数
    public String lastLoginDt;//上次登陆时间
    public String lastLoginDays;//距离上次登陆的天数
    public String registChannel;
    private String longTimeNotLogin;//长时间未登录（当前为三个月，90天） Y-是,N-否
    private String passwordSet;//是否设置过登录密码,Y-是，N-否
    private String isNewUser;//是否新用户,Y-是，N-否
    private String processId;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getPasswordSet() {
        return passwordSet;
    }

    public void setPasswordSet(String passwordSet) {
        this.passwordSet = passwordSet;
    }

    public String getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(String isNewUser) {
        this.isNewUser = isNewUser;
    }

    public String getLongTimeNotLogin() {
        return longTimeNotLogin;
    }

    public void setLongTimeNotLogin(String longTimeNotLogin) {
        this.longTimeNotLogin = longTimeNotLogin;
    }

    public String getRegistChannel() {
        return registChannel;
    }

    public void setRegistChannel(String registChannel) {
        this.registChannel = registChannel;
    }

    public String getLastLoginDt() {
        return lastLoginDt;
    }

    public void setLastLoginDt(String lastLoginDt) {
        this.lastLoginDt = lastLoginDt;
    }

    public String getLastLoginDays() {
        return lastLoginDays;
    }

    public void setLastLoginDays(String lastLoginDays) {
        this.lastLoginDays = lastLoginDays;
    }

    public String getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(String failedCount) {
        this.failedCount = failedCount;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRegistDt() {
        return registDt;
    }

    public void setRegistDt(String registDt) {
        this.registDt = registDt;
    }
}
