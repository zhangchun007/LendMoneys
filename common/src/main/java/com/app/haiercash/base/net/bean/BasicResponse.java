package com.app.haiercash.base.net.bean;


import com.app.haiercash.base.net.config.NetConfig;

/**
 * @Author: Sun
 * @Date :    2017/12/19
 * @FileName: BasicResponse
 * @Description: 网络请求结构类型
 */

public class BasicResponse<T> {
    //请求结果头
    public ResultHead head;
    //请求结果内容
    public T body;
    //增加access_token 是因为请求token接口数据格式差异
    private String access_token;

    //统一加解密时只返回responseData
    private String responseData;


    public boolean isSuccess() {
        return (head != null) && NetConfig.FLAG_NET_SUCCESS.equals(head.retFlag);
    }

    public static boolean isTokenInvalid(String error) {
        return error.contains(NetConfig.FLAG_TOKEN_INVALID) || error.toLowerCase().contains("unauthorized");
    }

    public BasicResponse(ResultHead head, T body) {
        this.setHead(head);
        this.setBody(body);
    }

    public BasicResponse(String code, String message) {
        this(new ResultHead(code, message), null);
    }


    public ResultHead getHead() {
        return head;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setHead(ResultHead head) {
        this.head = head;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
}
