package com.app.haiercash.base.utils.rxbus;

import android.content.Intent;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/1/8<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ActionEvent {
    /**
     * 1:首页显示对应的Icon<p>
     */
    public static final int MainShowIcon = 1;
    public static final int WxShareSuccess = 2;
    public static final int WxPayResult = 3;

    /**
     * 重新刷新首页接口
     */
    public static final int MainRefreshHomePage = 5;
    /**
     * 重置首页相应的fragment数据
     */
    public static final int MainFragmentReset = 6;

    /**
     * 登录页面清空手机号
     */
    public static final int RegisterFragmentClearPhoneNum = 7;

    /**
     * 登录后刷新个人中心数据
     */
    public static final int REFRESHUSERINFO = 8;

    /**
     * 跳转到短信登录
     */
    public static final int GO_SMS_LOGIN_WAY = 10;

    /**
     * 跳转到短信登录
     */
    public static final int GO_PASSWORD_LOGIN_WAY = 11;

    /**
     * 跳转到登录页并toast消息
     */
    public static final int HOMEPAGE_TOAST_MESSAGE = 12;

    /**
     * 红包微信授权
     */
    public static final int RED_BAG_WX_AUTH = 13;

    /**
     * 红包微信授权
     */
    public static final int MAINFRAGMENT_REFRESH_CREDIT = 14;
    /**
     * 消息列表一键已读
     */
    public static final int ONE_KEY_CLEAR_MESSAGE = 15;

    /**
     * 需要读取push信息
     */
    public static final int DEAL_WITH_PUSH_INFO = 16;

    /**
     * 返回首页切换到第一个tab
     */
    public static final int SELECT_MAIN_TAB = 17;


    private int actionType;
    private String actionMsg;
    private String actionRem;

    public Intent getParams() {
        return params;
    }

    public void setParams(Intent params) {
        this.params = params;
    }

    private Intent params;

    public ActionEvent(int actionType, Intent params) {
        this.actionType = actionType;
        this.params = params;
    }

    public ActionEvent(int actionType) {
        this.actionType = actionType;
    }

    public ActionEvent(int actionType, String actionMsg) {
        this.actionType = actionType;
        this.actionMsg = actionMsg;
    }

    public ActionEvent(int actionType, String actionMsg, String actionRem) {
        this.actionType = actionType;
        this.actionMsg = actionMsg;
        this.actionRem = actionRem;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public String getActionMsg() {
        return actionMsg;
    }

    public void setActionMsg(String actionMsg) {
        this.actionMsg = actionMsg;
    }

    public String getActionRem() {
        return actionRem;
    }

    public void setActionRem(String actionRem) {
        this.actionRem = actionRem;
    }
}
