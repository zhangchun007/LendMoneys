package com.haiercash.gouhua.beans;

import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/12/3<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BannerList {
    private String title;
    private List<BannerBean2> boothType;
    private BannerBean2 boothType2;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<BannerBean2> getBoothType() {
        return boothType;
    }

    public void setBoothType(List<BannerBean2> boothType) {
        this.boothType = boothType;
    }

    public BannerBean2 getBoothType2() {
        return boothType2;
    }

    public void setBoothType2(BannerBean2 boothType2) {
        this.boothType2 = boothType2;
    }
}
