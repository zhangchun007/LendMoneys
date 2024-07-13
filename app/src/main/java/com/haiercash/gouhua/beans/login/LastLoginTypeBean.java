package com.haiercash.gouhua.beans.login;

/**
 * 上一次登录方式
 */
public class LastLoginTypeBean {
    /**
     * gesture：手势密码登录
     * password：密码登录
     * captcha：验证码登录
     * face：人脸登录
     * fingerprint：指纹登录
     * other：其他
     * 微信登录： weixin
     * 一键登录： oauth
     * 如未查询到则返回 unknown
     */
    private String loginMethod;

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }
}
