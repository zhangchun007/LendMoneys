package com.haiercash.gouhua.beans.login;

import java.io.Serializable;

public class QueryAgreementListBean implements Serializable {
    private String contName;
    private String contType;
    private String contCode;
    private String contUrl;

    public QueryAgreementListBean() {
    }

    public QueryAgreementListBean(String contName, String contType, String contCode, String contUrl) {
        this.contName = contName;
        this.contType = contType;
        this.contCode = contCode;
        this.contUrl = contUrl;
    }

    public String getContName() {
        return contName;
    }

    public void setContName(String contName) {
        this.contName = contName;
    }

    public String getContType() {
        return contType;
    }

    public void setContType(String contType) {
        this.contType = contType;
    }

    public String getContCode() {
        return contCode;
    }

    public void setContCode(String contCode) {
        this.contCode = contCode;
    }

    public String getContUrl() {
        return contUrl;
    }

    public void setContUrl(String contUrl) {
        this.contUrl = contUrl;
    }
}

