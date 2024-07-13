package com.haiercash.gouhua.beans;

public class MySevenPayBean {
    private int display;
    private String text;
    private String amount;
    private int btnStatus;

    @Override
    public String toString() {
        return "MySevenPayBean{" +
                "display=" + display +
                ", text='" + text + '\'' +
                ", amount='" + amount + '\'' +
                ", btnStatus=" + btnStatus +
                '}';
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getBtnStatus() {
        return btnStatus;
    }

    public void setBtnStatus(int btnStatus) {
        this.btnStatus = btnStatus;
    }

    public MySevenPayBean() {
    }
}
