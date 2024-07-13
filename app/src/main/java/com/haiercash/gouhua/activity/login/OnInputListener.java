package com.haiercash.gouhua.activity.login;

/**
 * 输入的监听抽象类
 * 没定义接口的原因是可以在抽象类里面定义空实现的方法,可以让用户根据需求选择性的复写某些方法
 */
public interface OnInputListener {

    /**
     * 输入完成的抽象方法
     *
     * @param content 输入内容
     */
    void onInputFinished(String content);

    /**
     * 输入的内容
     */
    void onInputChanged(String text);
}
