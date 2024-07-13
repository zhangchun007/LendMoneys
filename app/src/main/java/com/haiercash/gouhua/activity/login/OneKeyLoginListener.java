package com.haiercash.gouhua.activity.login;

import android.app.Activity;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 11/25/22
 * @Version: 1.0
 */
public interface OneKeyLoginListener {
    void onAuthActivityCreate(Activity activity);

    /**
     * 登录按钮点击后调登录接口
     *
     * @param isChecked  协议是否勾选
     * @param process_id
     * @param token
     * @param authCode
     */
    void onLoginButtonClick(boolean isChecked, String process_id, String token, String authCode);

    /**
     * 隐私条款点击回掉
     *
     * @param name
     * @param url
     */
    void onPrivacyClick(String name, String url);


    /**
     * 一键登录失败
     */
    void oneKeyLoginError();

    void thirdLogin(String type);
}
