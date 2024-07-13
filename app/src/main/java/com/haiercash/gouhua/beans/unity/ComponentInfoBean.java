package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/9
 * @Version: 1.0
 */
public class ComponentInfoBean implements Serializable {
    private TitleBean title;//左边标题
    private ShowMoreBean showMore;//右边标题
    private SourceDataBean sourceData;//内容
    private List<MenusBean> sourceList;//金刚区
    private String cmsData; //资源位json数据

    private HashMap<String, Object> event;
    private HashMap<String, Object> exposure;

    public ComponentInfoBean() {
    }

    public ComponentInfoBean(TitleBean title, ShowMoreBean showMore, SourceDataBean sourceData, List<MenusBean> sourceList, String cmsData, HashMap<String, Object> event) {
        this.title = title;
        this.showMore = showMore;
        this.sourceData = sourceData;
        this.sourceList = sourceList;
        this.cmsData = cmsData;
        this.event = event;
    }

    public ComponentInfoBean(TitleBean title, ShowMoreBean showMore, SourceDataBean sourceData, List<MenusBean> sourceList, String cmsData, HashMap<String, Object> event, HashMap<String, Object> exposure) {
        this.title = title;
        this.showMore = showMore;
        this.sourceData = sourceData;
        this.sourceList = sourceList;
        this.cmsData = cmsData;
        this.event = event;
        this.exposure = exposure;
    }

    public HashMap<String, Object> getExposure() {
        return exposure;
    }

    public void setExposure(HashMap<String, Object> exposure) {
        this.exposure = exposure;
    }

    public HashMap<String, Object> getEvent() {
        return event;
    }

    public void setEvent(HashMap<String, Object> event) {
        this.event = event;
    }

    public String getCmsData() {
        return cmsData;
    }

    public void setCmsData(String cmsData) {
        this.cmsData = cmsData;
    }

    public TitleBean getTitle() {
        return title;
    }

    public void setTitle(TitleBean title) {
        this.title = title;
    }

    public ShowMoreBean getShowMore() {
        return showMore;
    }

    public void setShowMore(ShowMoreBean showMore) {
        this.showMore = showMore;
    }

    public SourceDataBean getSourceData() {
        return sourceData;
    }

    public void setSourceData(SourceDataBean sourceData) {
        this.sourceData = sourceData;
    }

    public List<MenusBean> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<MenusBean> sourceList) {
        this.sourceList = sourceList;
    }
}
