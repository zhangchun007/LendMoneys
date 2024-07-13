package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/28
 * @Version: 1.0
 */
public class CouponInfoBean implements Serializable {
    private String youhui;
    private String hongbao;
    private String mianxi;
    private String nearExpiry;

    public String getYouhui() {
        return youhui;
    }

    public void setYouhui(String youhui) {
        this.youhui = youhui;
    }

    public String getHongbao() {
        return hongbao;
    }

    public void setHongbao(String hongbao) {
        this.hongbao = hongbao;
    }

    public String getMianxi() {
        return mianxi;
    }

    public void setMianxi(String mianxi) {
        this.mianxi = mianxi;
    }

    public String getNearExpiry() {
        return nearExpiry;
    }

    public void setNearExpiry(String nearExpiry) {
        this.nearExpiry = nearExpiry;
    }
}
