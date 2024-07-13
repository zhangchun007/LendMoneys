package com.haiercash.gouhua.beans.br;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/1/7<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BrInitDataBean {
    private String api_code;
    private String swift_number;
    private String sign;
    private Object param;//InitParamBean

    public String getApi_code() {
        return api_code;
    }

    public void setApi_code(String api_code) {
        this.api_code = api_code;
    }

    public String getSwift_number() {
        return swift_number;
    }

    public void setSwift_number(String swift_number) {
        this.swift_number = swift_number;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }
}
