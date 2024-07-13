package com.haiercash.gouhua.beans.face;

import com.haiercash.gouhua.beans.TDYX_Bean;

import java.util.List;

/**
 * 项目名称：goHuaAND
 * 项目作者：胡玉君
 * 创建日期：2017/6/30 9:24.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */

public class FaceBean {

    public String isRetry;
    public String isOK;
    public String isResend;
    public List<TDYX_Bean> attachList;

    public String getIsRetry() {
        return isRetry;
    }

    public void setIsRetry(String isRetry) {
        this.isRetry = isRetry;
    }

    public String getIsOK() {
        return isOK;
    }

    public void setIsOK(String isOK) {
        this.isOK = isOK;
    }

    public String getIsResend() {
        return isResend;
    }

    public void setIsResend(String isResend) {
        this.isResend = isResend;
    }

    public List<TDYX_Bean> getAttachList() {
        return attachList;
    }

    public void setAttachList(List<TDYX_Bean> attachList) {
        this.attachList = attachList;
    }
}
