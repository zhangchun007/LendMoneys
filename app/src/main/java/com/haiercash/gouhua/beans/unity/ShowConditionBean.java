package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/12/1
 * @Version: 1.0
 */
public class ShowConditionBean implements Serializable {
    private String key; //"repayInfo.showBubble"
    private String value; //"1"
    private String standard; //"1"

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
