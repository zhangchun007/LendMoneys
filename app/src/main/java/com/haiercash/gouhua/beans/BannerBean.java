package com.haiercash.gouhua.beans;

/**
 * 项目名称：goHuaAND
 * 项目作者：胡玉君
 * 创建日期：2017/6/30 16:06.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class BannerBean {
    private String jumpKey;
    private String adImg;
    /**
     * 活动类型  h5link （活动链接）<br/>
     * customize（自定义内容）
     */
    private String activeType;//活动类型
    private String detail;// 自定义内容）

    public String getJumpKey() {
        return jumpKey;
    }

    public void setJumpKey(String jumpKey) {
        this.jumpKey = jumpKey;
    }

    public String getAdImg() {
        return adImg;
    }

    public void setAdImg(String adImg) {
        this.adImg = adImg;
    }

    public String getActiveType() {
        return activeType;
    }

    public void setActiveType(String activeType) {
        this.activeType = activeType;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
