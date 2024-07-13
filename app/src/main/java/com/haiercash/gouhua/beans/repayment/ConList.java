package com.haiercash.gouhua.beans.repayment;

//借款详情的合同
public class ConList {
    private String docType;//合同类型
    private String docDesc;//合同名称

    @Override
    public String toString() {
        return "ConList{" +
                "docType='" + docType + '\'' +
                ", docDesc='" + docDesc + '\'' +
                '}';
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocDesc() {
        return docDesc;
    }

    public void setDocDesc(String docDesc) {
        this.docDesc = docDesc;
    }

    public ConList(String docType, String docDesc) {
        this.docType = docType;
        this.docDesc = docDesc;
    }

    public ConList() {
    }
}
