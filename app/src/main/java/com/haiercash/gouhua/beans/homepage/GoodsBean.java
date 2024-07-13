package com.haiercash.gouhua.beans.homepage;

import java.util.List;

public class GoodsBean {
    private int more;
    private List<Goods> goods;
    private String moreUrl;

    public void setMore(int more) {
        this.more = more;
    }

    public int getMore() {
        return more;
    }

    public void setGoods(List<Goods> goods) {
        this.goods = goods;
    }

    public List<Goods> getGoods() {
        return goods;
    }

    public void setMoreUrl(String moreUrl) {
        this.moreUrl = moreUrl;
    }

    public String getMoreUrl() {
        return moreUrl;
    }
}
