package com.haiercash.gouhua.beans.homepage;

/**
 * 首页够分期返回实体
 * "productName": "医美分期2",
 * "productImg": "http://10.166.101.42/1cfba132681a4af595178b922007ae19.jpeg",
 * "productUrl": "http://www.sohu.com"
 */
public class ProductBean {
    private String productName;
    private String productImg;
    private String productUrl;
    private String productCode;

    public ProductBean(String productName, String productImg, String productUrl, String productCode) {
        this.productName = productName;
        this.productImg = productImg;
        this.productUrl = productUrl;
        this.productCode = productCode;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductCode() {
        return productCode;
    }
}
