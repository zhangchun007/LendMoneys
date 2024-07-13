package com.app.haiercash.base;

import android.app.Application;

import com.app.haiercash.base.db.DbUtils;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.sp.SpHelper;

/**
 * Author: Sun
 * Date :    2018/3/16
 * FileName: CommonManager
 * Description: 作为底层库的管理类
 */

public class CommonManager {
    public static final String CHANNEL_ID = "42";
    public static final String DEFAULT_DEVICEID = "000000000000000";//本机状态权限

    /**
     * 对底层依赖库进行初始化,仅初始化需要application的组件
     */
    public static void registerCommon(Application context, boolean isDebug) {
        //目前Activity生命周期采集已经采用ASM+gradle方案
        ActivityUntil.init(context);
        //SharedPreferences
        SpHelper.getInstance().registerSharedPreferences(context);
        //数据库
        DbUtils.init(context);

        //tokenHelper
        TokenHelper.getInstance().registerToken(context);

        NetConfig.registerNet(context, isDebug);
    }
}
