package com.haiercash.gouhua.beans.agreement;

/**
 * 需要重新签章的协议信息
 */
public class NeedResignAgreement {
    private String signType;//签章类型
    private String idNo;//证件号
    private String signName;//签章名称
    private String previewUrl;//协议预览地址

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
}
