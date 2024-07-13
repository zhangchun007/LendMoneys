package com.haiercash.gouhua.beans.homepage;

import com.haiercash.gouhua.beans.login.QueryAgreementListBean;

import java.io.Serializable;
import java.util.List;

/**
 * 够智选数据
 */
public class GzxData implements Serializable {

    private List<QueryAgreementListBean> agreementList;  //授权协议
    private String gzxUrl;  //够智选跳转链接
    private String gzxIcon; //够智选图片icon

    public List<QueryAgreementListBean> getAgreementList() {
        return agreementList;
    }

    public void setAgreementList(List<QueryAgreementListBean> agreementList) {
        this.agreementList = agreementList;
    }

    public String getGzxUrl() {
        return gzxUrl;
    }

    public void setGzxUrl(String gzxUrl) {
        this.gzxUrl = gzxUrl;
    }

    public String getGzxIcon() {
        return gzxIcon;
    }

    public void setGzxIcon(String gzxIcon) {
        this.gzxIcon = gzxIcon;
    }

    public GzxData(List<QueryAgreementListBean> agreementList, String gzxUrl, String gzxIcon) {
        this.agreementList = agreementList;
        this.gzxUrl = gzxUrl;
        this.gzxIcon = gzxIcon;
    }

    public GzxData() {
    }

}
