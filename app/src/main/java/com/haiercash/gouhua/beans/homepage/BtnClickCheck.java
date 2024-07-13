package com.haiercash.gouhua.beans.homepage;

import java.io.Serializable;

public class BtnClickCheck implements Serializable {
    private String btnType;//按钮类型Stringrepay:去还款 common：我知道了
    private String errMsg;//提示文案String

    public String getBtnType() {
        return btnType;
    }

    public void setBtnType(String btnType) {
        this.btnType = btnType;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public BtnClickCheck(String btnType, String errMsg) {
        this.btnType = btnType;
        this.errMsg = errMsg;
    }

    public BtnClickCheck() {
    }
}
