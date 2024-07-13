package com.haiercash.gouhua.beans.gesture;

/**
 * Created by 刘明戈 on 2016/5/10.
 * 手势密码
 */
public class GestureBean
{
    /**
     * userId : 15066666666
     * password : 66
     * gesture : 111
     */
    public String userId;
    public String password;
    public String gesture;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGesture() {
        return gesture;
    }

    public void setGesture(String gesture) {
        this.gesture = gesture;
    }

    @Override
    public String toString() {
        return "DengLuYangZheng_Bean{" + "userId='" + userId + '\''
                + "password='" + password + '\'' + "gesture='" + gesture + '\'' + '}';
    }
}
