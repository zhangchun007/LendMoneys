package com.haiercash.gouhua.beans.borrowmoney;

/**
 * Created by use on 2016/5/23.
 * 订单号	orderNo
 */
public class ContractBean
{
    public String orderNo;
    public String msgCode;
    public String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
