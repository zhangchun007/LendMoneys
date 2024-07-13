package com.haiercash.gouhua.beans.homepage;

/**
 * 首页推荐商品返回实体类
 * "imgUrl": "http://img11.360buyimg.com/n1/jfs/t1/96369/35/16703/170984/5e7f029cE5c150bbb/e23d34fb2899cd33.jpg",
 * "skuName": "SK-II神仙水230ml精华液护肤品套装化妆品礼盒（礼盒内赠清莹露+洗面奶）SK2 补水保湿 均匀肤色",
 * "refPrice": "0",
 * "salePrice": "1480",
 * "tag": "0息0费",
 * "url": "www.baidu.com"
 */
public class Goods {
    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    private String skuId;
    private String imgUrl;
    private String skuName;
    private String refPrice;
    private String salePrice;
    private String tag;
    private String url;

    @Override
    public String toString() {
        return "Goods{" +
                "imgUrl='" + imgUrl + '\'' +
                ", skuName='" + skuName + '\'' +
                ", refPrice='" + refPrice + '\'' +
                ", salePrice='" + salePrice + '\'' +
                ", tag='" + tag + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getRefPrice() {
        return refPrice;
    }

    public void setRefPrice(String refPrice) {
        this.refPrice = refPrice;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Goods(String imgUrl, String skuName, String refPrice, String salePrice, String tag, String url) {
        this.imgUrl = imgUrl;
        this.skuName = skuName;
        this.refPrice = refPrice;
        this.salePrice = salePrice;
        this.tag = tag;
        this.url = url;
    }

    public Goods() {
    }
}
