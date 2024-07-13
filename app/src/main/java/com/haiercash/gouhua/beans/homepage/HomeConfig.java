package com.haiercash.gouhua.beans.homepage;
/**
 * 首页配置信息返回的数据
 */

import java.io.Serializable;
import java.util.List;

public class HomeConfig implements Serializable {

    private List<Configs> configs;
    private HomeBubbleBean bubbleInfoMap;

    public HomeBubbleBean getBubbleInfoMap() {
        return bubbleInfoMap;
    }

    public void setBubbleInfoMap(HomeBubbleBean bubbleInfoMap) {
        this.bubbleInfoMap = bubbleInfoMap;
    }

    public List<Configs> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Configs> configs) {
        this.configs = configs;
    }

    public HomeConfig(List<Configs> configs) {
        this.configs = configs;
    }

    public HomeConfig() {
    }

}
