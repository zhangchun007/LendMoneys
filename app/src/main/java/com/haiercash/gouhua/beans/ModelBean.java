package com.haiercash.gouhua.beans;

/**
 * Created by use on 2017/2/10.
 */
public class ModelBean {
    public String name;
    public String name2;
    public String code;
    public String code2;//第二联系人code

    public ModelBean() {
    }

    public ModelBean(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public boolean isEmpty() {
        return code == null || name == null;
    }

    private String dictCode;
    private String dictName;

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }
}
