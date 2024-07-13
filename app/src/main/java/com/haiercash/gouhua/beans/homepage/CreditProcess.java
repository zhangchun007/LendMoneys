package com.haiercash.gouhua.beans.homepage;

import java.io.Serializable;

/**
 * 申额进度条
 */
public class CreditProcess implements Serializable {
    private String text;//文本String
    private String icon;//图标String
    private String complete;//是否完成StringY是N否

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getComplete() {
        return complete;
    }

    public void setComplete(String complete) {
        this.complete = complete;
    }

    public CreditProcess(String text, String icon, String complete) {
        this.text = text;
        this.icon = icon;
        this.complete = complete;
    }

    public CreditProcess() {
    }
}
