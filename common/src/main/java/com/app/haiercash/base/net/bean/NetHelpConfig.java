package com.app.haiercash.base.net.bean;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 8/16/22
 * @Version: 1.0
 */
public class NetHelpConfig {
    //网络请求的随机key
    private String netConfigKey;
    //网络请求的随机偏移量
    private String netConfigIV;

    public String getNetConfigKey() {
        return netConfigKey;
    }

    public void setNetConfigKey(String netConfigKey) {
        this.netConfigKey = netConfigKey;
    }

    public String getNetConfigIV() {
        return netConfigIV;
    }

    public void setNetConfigIV(String netConfigIV) {
        this.netConfigIV = netConfigIV;
    }
}
