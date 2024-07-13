package com.haiercash.gouhua.beans.br;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/1/7<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BrInitParam {
    private String token_id;
    private String gid;
    private BrInitDataBean brData;

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public BrInitDataBean getBrData() {
        return brData;
    }

    public void setBrData(BrInitDataBean brData) {
        this.brData = brData;
    }
}
