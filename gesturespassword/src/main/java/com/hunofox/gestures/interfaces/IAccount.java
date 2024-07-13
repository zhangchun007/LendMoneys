package com.hunofox.gestures.interfaces;

/**
 * 项目名称：接口调用监听
 * 项目作者：胡玉君
 * 创建日期：2016/4/5 17:40.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：注意无网络连接时如何处理结果
 * ----------------------------------------------------------------------------------------------------
 */
public interface IAccount {

    /**
     * 从网络获取该账户是否被冻结
     *
     * 此处参数为从本地获取的是否被冻结了，默认是不冻结
     */
    boolean isFreeze(boolean isFreeze);

    /**
     * 从网络获取输入错误次数
     *
     * 此处count是从本地获取的count
     */
    int getCount(int count);

    /**
     * 将输入错误次数上传至服务器
     *
     * 此处count为正确的count，需要上传到服务器
     */
    void setCount(int count);

    /**
     * 是否冻结用户账户(上传至网络)
     * @param isFreeze true为冻结，false为解冻，此参数为最终正确的参数，需要上传到服务器
     */
    void freezeCount(boolean isFreeze);

    /**
     * 手势密码输入成功后的回调
     */
    void success(String inputCode);
}
