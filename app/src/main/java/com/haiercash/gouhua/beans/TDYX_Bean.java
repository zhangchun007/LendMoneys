package com.haiercash.gouhua.beans;

import java.io.Serializable;

/**
 * Created by use on 2016/11/24.
 * 替代影像
 */

public class TDYX_Bean implements Serializable {
    public String docCde;
    public String docDesc;
    public String id;
    public String count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDocCde() {
        return docCde;
    }

    public void setDocCde(String docCde) {
        this.docCde = docCde;
    }

    public String getDocDesc() {
        return docDesc;
    }

    public void setDocDesc(String docDesc) {
        this.docDesc = docDesc;
    }

    @Override
    public String toString() {
        return "TDYX_Bean{" +
                "docCde='" + docCde + '\'' +
                ", docDesc='" + docDesc + '\'' +
                ", id='" + id + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}
