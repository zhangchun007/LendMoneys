package com.haiercash.gouhua.beans.register;

/**
 * Created by use on 2016/5/5.
 */
public class IsRegisterBean {

    public String isRegister;//    N：未注册    Y：已注册    C: 手机号已被占用
    public String provider;
    public String alterPwd;//是否可以修改密码
    public String alterPwdIn;//海尔会员重置密码入口地址
    public String alterPwdOut;//海尔会员重置密码出口地址

    public String getAlterPwdIn() {
        return alterPwdIn;
    }

    public void setAlterPwdIn(String alterPwdIn) {
        this.alterPwdIn = alterPwdIn;
    }

    public String getAlterPwdOut() {
        return alterPwdOut;
    }

    public void setAlterPwdOut(String alterPwdOut) {
        this.alterPwdOut = alterPwdOut;
    }

    public String getAlterPwd() {
        return alterPwd;
    }

    public void setAlterPwd(String alterPwd) {
        this.alterPwd = alterPwd;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getIsRegister() {
        return isRegister;
    }

    public void setIsRegister(String isRegister) {
        this.isRegister = isRegister;
    }

    @Override
    public String toString() {
        return "IsRegisterBean{" +
                "isRegister='" + isRegister + '\'' +
                '}';
    }
}
