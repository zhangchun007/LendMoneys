package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;

/**
 * @Description: 气泡
 * @Author: zhangchun
 * @CreateDate: 2023/11/9
 * @Version: 1.0
 */
public class PopBean implements Serializable {
    private String defaultShow; //展示条件
    private String popText; //气泡文本
    private ShowConditionBean showCondition; //是否展示气泡Y/N
    private String popStartColor;
    private String popEndColor;
    private String popStrokeColor;
    private String popStrokeWidth;

    public String getDefaultShow() {
        return defaultShow;
    }

    public void setDefaultShow(String defaultShow) {
        this.defaultShow = defaultShow;
    }

    public String getPopStartColor() {
        return popStartColor;
    }

    public void setPopStartColor(String popStartColor) {
        this.popStartColor = popStartColor;
    }

    public String getPopEndColor() {
        return popEndColor;
    }

    public void setPopEndColor(String popEndColor) {
        this.popEndColor = popEndColor;
    }

    public String getPopStrokeColor() {
        return popStrokeColor;
    }

    public void setPopStrokeColor(String popStrokeColor) {
        this.popStrokeColor = popStrokeColor;
    }

    public String getPopStrokeWidth() {
        return popStrokeWidth;
    }

    public void setPopStrokeWidth(String popStrokeWidth) {
        this.popStrokeWidth = popStrokeWidth;
    }

    public String getPopText() {
        return popText;
    }

    public void setPopText(String popText) {
        this.popText = popText;
    }

    public ShowConditionBean getShowCondition() {
        return showCondition;
    }

    public void setShowCondition(ShowConditionBean showCondition) {
        this.showCondition = showCondition;
    }
}
