package com.haiercash.gouhua.beans.register;

/**
 * Created by use on 2016/5/4.
 *
 * 用户账号	userId
 手机号	mobile
 用户名称	userName
 用户昵称	nickName
 登录密码	password
 电子邮箱	email
 用户描述	userDesc

 */
public class SaveUauthUsersBeanRtn
{
    public String userId;
    public String mobile;
    public String userName;
    public String nickName;
    public String password;
    public String email;
    public String userDesc;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    @Override
    public String toString() {
        return "SaveUauthUsersBeanRtn{" +
                "userId='" + userId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", userDesc='" + userDesc + '\'' +
                '}';
    }
}
