package com.haiercash.gouhua.beans.register;

/**
 * Created by StarFall on 2016/5/12.
 */
public class IsPasswordExistBean {

    /**
     * payPasswdFlag : 0
     * gestureFlag : 0
     */
    public String payPasswdFlag;
    private String gestureFlag;

    public String getPayPasswdFlag() {
        return payPasswdFlag;
    }

    public void setPayPasswdFlag(String payPasswdFlag) {
        this.payPasswdFlag = payPasswdFlag;
    }

    public String getGestureFlag() {
        return gestureFlag;
    }

    public void setGestureFlag(String gestureFlag) {
        this.gestureFlag = gestureFlag;
    }
}
