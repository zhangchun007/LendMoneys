package com.haiercash.gouhua.beans.homepage;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 配置数据
 */
public class Configs implements Serializable, MultiItemEntity {
    public static final int UNKNOWN = 0;
    public static final int BANNER = 1;
    public static final int VIP_BANNER = 2;
    public static final int GENERAL = 3;
    public static final int BOTTOM = 4;
    public static final int NOTICE = 5;

    private String type;//类型
    private String title;//标题
    private List<ConfigData> data;//待展示数据

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ConfigData> getData() {
        return data;
    }

    public void setData(List<ConfigData> data) {
        this.data = data;
    }

    public Configs(String type, String title, List<ConfigData> data) {
        this.type = type;
        this.title = title;
        this.data = data;
    }

    public Configs() {
    }

    @Override
    public int getItemType() {
        switch (type) {
            case "banner":
                return BANNER;
            case "midRes":
                return VIP_BANNER;
            case "general":
                return GENERAL;
            case "bottom":
                return BOTTOM;
            case "notice":
                return NOTICE;
        }
        return UNKNOWN;
    }

}
