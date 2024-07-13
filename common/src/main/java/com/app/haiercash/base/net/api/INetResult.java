package com.app.haiercash.base.net.api;


import com.app.haiercash.base.net.bean.BasicResponse;

/**
 * @Author: Sun
 * @Date :    2018/3/14
 * @FileName: INetResult
 * @Description: 网络请求回调
 */

public interface INetResult {

    <T> void onSuccess(T t, String url);

    void onError(BasicResponse error, String url);
}
