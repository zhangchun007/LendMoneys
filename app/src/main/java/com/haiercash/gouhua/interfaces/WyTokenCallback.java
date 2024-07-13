package com.haiercash.gouhua.interfaces;


/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2/2/23
 * @Version: 1.0
 */
public interface WyTokenCallback {
    /**
     *
     * @param token 正常情况下返回token值
     * @param code  异常情况下返回的错误码
     * @param msg 异常情况下返回的错误信息
     */
    void onResult(String token,int code,String msg);
}
