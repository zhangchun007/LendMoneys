package com.haiercash.gouhua.beans;

public class PopupWindowBean {
    private String title;  //弹窗标题
    private String content;  //弹窗内容
    private String buttonTv;   //按钮名称
    private int buttonBack;   //按钮背景

    @Override
    public String toString() {
        return "PopupwindowBean{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", buttonTv='" + buttonTv + '\'' +
                ", buttonColor=" + buttonBack +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getButtonTv() {
        return buttonTv;
    }

    public void setButtonTv(String buttonTv) {
        this.buttonTv = buttonTv;
    }

    public int getButtonBack() {
        return buttonBack;
    }

    public void setButtonBack(int buttonBack) {
        this.buttonBack = buttonBack;
    }

    public PopupWindowBean(String title, String content, String buttonTv, int buttonBack) {
        this.title = title;
        this.content = content;
        this.buttonTv = buttonTv;
        this.buttonBack = buttonBack;
    }

    public PopupWindowBean() {
    }
}
