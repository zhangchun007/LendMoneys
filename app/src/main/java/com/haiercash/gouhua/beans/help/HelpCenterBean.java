package com.haiercash.gouhua.beans.help;

/**
 * Created by StarFall on 2016/6/8.
 * 帮助中心
 */
public class HelpCenterBean {

    /**
     * createdDate : 2019-05-13 10:17:25
     * id : 1
     * isCommon : 1
     * ordBy : 1
     * problemContent : 1111
     * problemTitle : 111
     * problemType : 1111
     * problemTypeId : 5
     * updatedDate : 2019-05-13 10:17:28
     */

    private String createdDate;
    private int id;
    private int isCommon;
    private int ordBy;
    private String problemContent;
    private String problemTitle;
    private String problemType;
    private int problemTypeId;
    private String updatedDate;

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsCommon() {
        return isCommon;
    }

    public void setIsCommon(int isCommon) {
        this.isCommon = isCommon;
    }

    public int getOrdBy() {
        return ordBy;
    }

    public void setOrdBy(int ordBy) {
        this.ordBy = ordBy;
    }

    public String getProblemContent() {
        return problemContent;
    }

    public void setProblemContent(String problemContent) {
        this.problemContent = problemContent;
    }

    public String getProblemTitle() {
        return problemTitle;
    }

    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    public String getProblemType() {
        return problemType;
    }

    public void setProblemType(String problemType) {
        this.problemType = problemType;
    }

    public int getProblemTypeId() {
        return problemTypeId;
    }

    public void setProblemTypeId(int problemTypeId) {
        this.problemTypeId = problemTypeId;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }
}
