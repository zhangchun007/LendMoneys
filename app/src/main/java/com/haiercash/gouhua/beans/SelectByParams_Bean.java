package com.haiercash.gouhua.beans;

/**
 * Created by use on 2016/8/18.
 * 系统标识	sysTyp
 * 参数代码	paramCode
 * 参数名称	paramName
 * 参数值	paramValue
 */
public class SelectByParams_Bean {
    public String sysTyp;
    public String paramCode;
    public String paramName;
    public String paramValue;

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getSysTyp() {
        return sysTyp;
    }

    public void setSysTyp(String sysTyp) {
        this.sysTyp = sysTyp;
    }
}
