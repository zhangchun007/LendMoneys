package com.haiercash.gouhua.beans.homepage;

import java.util.List;

public class LoanProductBean {
    private int more;
    private String moreUrl;

    public String getMoreUrl() {
        return moreUrl;
    }

    public void setMoreUrl(String moreUrl) {
        this.moreUrl = moreUrl;
    }

    private List<ProductBean> products;

    @Override
    public String toString() {
        return "LoanProductBean{" +
                "more='" + more + '\'' +
                ", products=" + products +
                '}';
    }

    public int getMore() {
        return more;
    }

    public void setMore(int more) {
        this.more = more;
    }

    public List<ProductBean> getProducts() {
        return products;
    }

    public void setProducts(List<ProductBean> products) {
        this.products = products;
    }

    public LoanProductBean(int more, List<ProductBean> products) {
        this.more = more;
        this.products = products;
    }

    public LoanProductBean() {
    }
}
