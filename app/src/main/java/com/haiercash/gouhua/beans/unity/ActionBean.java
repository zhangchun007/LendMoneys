package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @Description: 跳转动作
 * @Author: zhangchun
 * @CreateDate: 2023/11/9
 * @Version: 1.0
 */
public class ActionBean implements Serializable {
    private String actionType; //跳转类型
    private String jumpUrl; //跳转路径
    private boolean needLogin;
    private boolean needRealName;
    private String realNameUrl;
    private HashMap<String, Object> event;

    public ActionBean() {
    }

    public ActionBean(String actionType, String jumpUrl, boolean needLogin, boolean needRealName, String realNameUrl, HashMap<String, Object> event) {
        this.actionType = actionType;
        this.jumpUrl = jumpUrl;
        this.needLogin = needLogin;
        this.needRealName = needRealName;
        this.realNameUrl = realNameUrl;
        this.event = event;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public boolean isNeedRealName() {
        return needRealName;
    }

    public HashMap<String, Object> getEvent() {
        return event;
    }

    public void setEvent(HashMap<String, Object> event) {
        this.event = event;
    }

    public boolean getNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public boolean getNeedRealName() {
        return needRealName;
    }

    public void setNeedRealName(boolean needRealName) {
        this.needRealName = needRealName;
    }

    public String getRealNameUrl() {
        return realNameUrl;
    }

    public void setRealNameUrl(String realNameUrl) {
        this.realNameUrl = realNameUrl;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }
}
