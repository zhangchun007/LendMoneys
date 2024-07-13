package com.app.haiercash.base.analysis;

import com.app.haiercash.base.bean.CollectInfo;
import com.app.haiercash.base.db.DbUtils;
import com.app.haiercash.base.net.config.CommonSpKey;
import com.app.haiercash.base.utils.sp.SpHelper;

import java.util.List;

/**
 * *@Author:    Sun
 * *@Date  :    2020/7/22
 * *@FileName: AddressInfo
 * *@Description:
 */
public class AnalysisManager {


    /**
     * 该方法不主动调用，由ASM插件调用
     *
     * @param info
     */
    public static void insertEvent(String info) {
        deleteAfterAll();
        try {
            CollectInfo collectInfo = new CollectInfo();
            collectInfo.time = System.currentTimeMillis();
            collectInfo.name = SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_MOBILE_VALUE);
            collectInfo.event = info;
            //DbUtils.getAppDatabase().collectInfoDao().insertOneInfo(collectInfo);
            //Logger.w("插入一条记录" + JsonUtils.toJson(collectInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断上一条记录时间和当前时间是否同一天
     *
     * @param time 当前时间
     * @return
     */
    private static boolean isToday(long time) {
        List<CollectInfo> list = DbUtils.getAppDatabase().collectInfoDao().getAllCollect();
        if (list.isEmpty()) {
            return true;
        }
        CollectInfo last = list.get(0);
        return time - last.getTime() < 1000 * 60 * 60 * 12;
    }


    /**
     * 删除今天以前的所有数据
     */
    private static void deleteAfterAll() {
        if (!isToday(System.currentTimeMillis())) {
            DbUtils.getAppDatabase().collectInfoDao().deleteAll();
        }
    }

}
