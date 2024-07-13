package com.haiercash.gouhua.beans.risk;

import java.io.Serializable;

public class AppPerson implements Serializable {
    private String err_message;      //toast弹得错误提示
    private String contact_phone1;  //联系人1

    @Override
    public String toString() {
        return "AppPerson{" +
                "err_message='" + err_message + '\'' +
                ", contact_phone1='" + contact_phone1 + '\'' +
                ", contact_phone2='" + contact_phone2 + '\'' +
                '}';
    }

    public String getErr_message() {
        return err_message;
    }

    public void setErr_message(String err_message) {
        this.err_message = err_message;
    }

    public String getContact_phone1() {
        return contact_phone1;
    }

    public void setContact_phone1(String contact_phone1) {
        this.contact_phone1 = contact_phone1;
    }

    public String getContact_phone2() {
        return contact_phone2;
    }

    public void setContact_phone2(String contact_phone2) {
        this.contact_phone2 = contact_phone2;
    }

    public AppPerson(String err_message, String contact_phone1, String contact_phone2) {
        this.err_message = err_message;
        this.contact_phone1 = contact_phone1;
        this.contact_phone2 = contact_phone2;
    }

    public AppPerson() {
    }

    private String contact_phone2;   //联系人2
}
