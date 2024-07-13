package com.megvii.livenesslib.baiduface.utils;

/**
 * ================================================================
 * 作    者：L14-14<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2021/11/29-10:36<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public interface IBaiduFaceCallBack {
    void faceCallBack(boolean isSuccess, int errorCode, Object data,String methodName);
}
