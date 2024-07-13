package com.haiercash.gouhua.beans.unity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/16
 * @Version: 1.0
 */
public class MultComponentBean implements Serializable {
      private String appid; //应用载体gouhua
      private String modelNo; //页面来源 ghPersonalCenter
      private String pageName; //页面描述 个人中心
      private String version; //版本
      private List<ComponentBean> componentList; //组件集合

      public String getAppid() {
            return appid;
      }

      public void setAppid(String appid) {
            this.appid = appid;
      }

      public String getModelNo() {
            return modelNo;
      }

      public void setModelNo(String modelNo) {
            this.modelNo = modelNo;
      }

      public String getPageName() {
            return pageName;
      }

      public void setPageName(String pageName) {
            this.pageName = pageName;
      }

      public String getVersion() {
            return version;
      }

      public void setVersion(String version) {
            this.version = version;
      }

      public List<ComponentBean> getComponentList() {
            return componentList;
      }

      public void setComponentList(List<ComponentBean> componentList) {
            this.componentList = componentList;
      }
}
