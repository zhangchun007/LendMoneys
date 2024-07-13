package com.haiercash.gouhua.tplibrary.bean;

/**
 * 开始人脸获取到的bizToken
 */
public class BizTokenBean {
    private String applSeq;
    private String bizToken;

    @Override
    public String toString() {
        return "BizTokenBean{" +
                "applSeq='" + applSeq + '\'' +
                ", bizToken='" + bizToken + '\'' +
                '}';
    }

    public String getApplSeq() {
        return applSeq;
    }

    public void setApplSeq(String applSeq) {
        this.applSeq = applSeq;
    }

    public String getBizToken() {
        return bizToken;
    }

    public void setBizToken(String bizToken) {
        this.bizToken = bizToken;
    }

    public BizTokenBean(String applSeq, String bizToken) {
        this.applSeq = applSeq;
        this.bizToken = bizToken;
    }

    public BizTokenBean() {
    }
}
