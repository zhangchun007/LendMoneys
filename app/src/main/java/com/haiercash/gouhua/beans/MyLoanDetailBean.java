package com.haiercash.gouhua.beans;

public class MyLoanDetailBean {
    private String label;
    private String url;

    public MyLoanDetailBean(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public MyLoanDetailBean() {
    }

    @Override
    public String toString() {
        return "MyLoanDetailBean{" +
                "label='" + label + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
