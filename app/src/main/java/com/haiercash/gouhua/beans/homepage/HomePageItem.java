package com.haiercash.gouhua.beans.homepage;

public class HomePageItem {
    private String type;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public HomePageItem(String type, String title, Object data) {
        this.type = type;
        this.title = title;
        this.data = data;
    }

    private Object data;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
