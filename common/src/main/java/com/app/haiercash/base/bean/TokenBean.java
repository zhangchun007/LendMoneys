package com.app.haiercash.base.bean;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 12/13/22
 * @Version: 1.0
 */
public class TokenBean {
    private String access_token;
    private String refresh_token;

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
