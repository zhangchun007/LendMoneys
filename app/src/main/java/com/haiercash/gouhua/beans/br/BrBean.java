package com.haiercash.gouhua.beans.br;

import java.util.HashMap;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/1/3<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BrBean {
    private int code = -1;
    private String msg;
    private HashMap<String, String> data;
    private HashMap<String, String> param;
    private String api_code;
    private String sign;
    private String swift_number;

    //String start_time;
    //int cost_time;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public void setData(HashMap<String, String> data) {
        this.data = data;
    }

    public HashMap<String, String> getParam() {
        return param;
    }

    public void setParam(HashMap<String, String> param) {
        this.param = param;
    }

    public String getApi_code() {
        return api_code;
    }

    public void setApi_code(String api_code) {
        this.api_code = api_code;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSwift_number() {
        return swift_number;
    }

    public void setSwift_number(String swift_number) {
        this.swift_number = swift_number;
    }
}
