package com.haiercash.gouhua.beans.msg;

import java.util.List;

/**
 * ================================================================
 * 作    者：zhangchun
 * 版    本：1.0
 * 创建日期：2023/6/7
 * 描    述：消息中心
 * 修订历史：
 * ================================================================
 */
public class MessageBeanNew {
    private String total;//站内信总数
    private String size;//数量
    private String current;//当前页
    private List<MessageInfoNew> records;//站内信消息列表

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public List<MessageInfoNew> getRecords() {
        return records;
    }

    public void setRecords(List<MessageInfoNew> records) {
        this.records = records;
    }
}
