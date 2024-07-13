package com.app.haiercash.base.bean;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.app.haiercash.base.utils.time.TimeUtil;



/**
 * @Author: Sun
 * @Date :    2019/4/16
 * @FileName: CollectInfo
 * @Description:
 */
@Entity
public class CollectInfo {

    @NonNull
    @PrimaryKey
    public long time;

    public String name;

    public String event;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "time:" + TimeUtil.longToString(time) + "  " + "name:" + name + "   " + "event:" + event + "\n";
    }
}
