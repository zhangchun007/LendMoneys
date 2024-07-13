package com.haiercash.gouhua.utils;


import android.content.Context;
import android.text.TextUtils;

import com.app.haiercash.base.CommonManager;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.AppUntil;
import com.haiercash.gouhua.interfaces.SpKey;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com<br/>
 * 版    本：1.0<br/>
 * 创建日期：2021/3/31<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class UMengUtil {
    /**
     * 埋点的debug控制
     */
    public static final boolean IS_BURY_DEBUG = true;
    //private static final String id = "18670957";//测试
//    private static final String id = "22069617";//生产
    private static final String id = BuildConfig.IS_RELEASE ? "22069617" : "18670957";// 生产
    public static String LOG_PAGE = "#NewUserExclusivePage#LoginPage#InputTransactionPasswordPage#HomePage#CheckstandPage#CheckstandTransactionPasswordPage#PersonLoanContractPage#";
    public static String LOG_CLICK = "#TransactionPasswordConfirmed_Click#ConfirmedRepay_Click#PasswordLogin#MessageLogin#WebCatLogin#";

    /**
     * 友盟数据采集初始化
     */
    public static void preInit(boolean isDebug) {
        UMConfigure.setCustomDomain("https://hermaplus.haiercash.com", null); // 请传⼊您⾃⼰的收数域名
        UMConfigure.setLogEnabled(isDebug && IS_BURY_DEBUG);//打开LOG:过滤条件：MobclickAgent、UMLog
        UMConfigure.preInit(AppApplication.CONTEXT, id, SystemUtils.metaDataValueForTDChannelId(AppApplication.CONTEXT));
        //MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        MobclickAgent.disableActivityPageCollection();//禁⽌SDK⾃动采集Activity⻚⾯路径数据接⼝
        //UMConfigure.setProcessEvent(true);// ⽀持在⼦进程中统计⾃定义事件
    }

    /**
     * 协议后的初始化
     */
    public static void init() {
        UMConfigure.init(AppApplication.CONTEXT, id, SystemUtils.metaDataValueForTDChannelId(AppApplication.CONTEXT), 1, "");
        //手动采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);

        //老版本使用了超级属性，新版本废除了，但是要清理掉
        MobclickAgent.clearSuperProperties(AppApplication.CONTEXT);
    }

    public static void pageStart(CharSequence pageCode) {
        if (IS_BURY_DEBUG && LOG_PAGE.contains("#" + pageCode.toString() + "#") && !BuildConfig.IS_RELEASE) {
            Logger.e("UMengUtil\n" + pageCode + "\n---pageStart---\n" + MobclickAgent.getGlobalProperties(AppApplication.CONTEXT));
        }
        MobclickAgent.onPageStart(String.valueOf(pageCode));
    }

    public static void pageEnd(CharSequence pageCode) {
        if (IS_BURY_DEBUG && LOG_PAGE.contains("#" + pageCode.toString() + "#") && !BuildConfig.IS_RELEASE) {
            Logger.e("UMengUtil\n" + pageCode + "\n---pageEnd---\n" + MobclickAgent.getGlobalProperties(AppApplication.CONTEXT));
        }
        MobclickAgent.onPageEnd(String.valueOf(pageCode));
    }

    public static void onKillProcess() {
        MobclickAgent.onKillProcess(AppApplication.CONTEXT);
    }

    /**
     * 注册⼀个全局属性
     */
    public static void registerGlobalProperty(String isLogin, String userId) {
        MobclickAgent.clearGlobalProperties(AppApplication.CONTEXT);
        if (CheckUtil.isEmpty(userId)) {
            userId = "unknown";
        }
        boolean noToken = CheckUtil.isEmpty(TokenHelper.getInstance().getCacheToken());
        Map<String, Object> map = new HashMap<>();//传值时注意都要传字符串类型
        map.put("is_login", isLogin);
        map.put("is_member", "Y".equals(SpHp.getLogin(SpKey.VIP_STATUS)) ? "true" : "false");
        map.put("user_id", userId);
        String channel_id = TokenHelper.getInstance().getSmyParameter("channelNo");//42替换成T5、T6
        if (CheckUtil.isEmpty(channel_id)) {//默认app默认渠道42
            channel_id = CommonManager.CHANNEL_ID;
        }
        map.put("channel_Id", channel_id);
        map.put("business", noToken ? NetConfig.TD_BUSINESS_SMY : TokenHelper.getInstance().getSmyParameter("business"));
        map.put("idea", noToken ? NetConfig.TD_IDEA_SMY : TokenHelper.getInstance().getSmyParameter("ideaCode"));
        map.put("regis_vector", noToken ? NetConfig.TD_REGIS_VECTOR_SMY : TokenHelper.getInstance().getSmyParameter("registerVector"));

        map.put("support64bit", SystemUtils.isSupport64Bit() + "");
        if (!CheckUtil.isEmpty(NetConfig.TD_APP_DOWN_FROM_SMY)) {
            map.put("qd_id", NetConfig.TD_APP_DOWN_FROM_SMY); //只取安装包里的，不取登录返回的
        }

        //保险措施：友盟源码中map为null或者map中key或者value为null或者空时会停止put数据到友盟全局属性JSONObject中存储
        for (Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            if (entry == null || TextUtils.isEmpty(entry.getKey())) {
                iterator.remove();
            } else if (entry.getValue() == null || (entry.getValue() instanceof String && ((String) entry.getValue()).length() == 0)) {
                //value为空时自动设置为"unknown"
                entry.setValue("unknown");
            }
        }
        MobclickAgent.registerGlobalProperties(AppApplication.CONTEXT, map);
    }

    //登录或退出登录成功
    public static void signInOrOut() {
        if (TextUtils.isEmpty(AppApplication.userid)) {
            MobclickAgent.onProfileSignOff();
        } else {
            MobclickAgent.onProfileSignIn(AppApplication.userid);
        }

    }

    /**
     * ⾃定义事件
     * pageCode页面浏览code
     * * map中key只能为数字、数字数组或列表、字符串、字符串列表或数组
     */
    public static void onEventObject(String eventID, Map<String, Object> map, String pageCode) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.put("create_time", System.currentTimeMillis());
        if (IS_BURY_DEBUG && LOG_CLICK.contains("#" + eventID + "#") && !BuildConfig.IS_RELEASE) {
            Logger.e("UMengUtil\n" + eventID + "\n" + "---onEventObject---" + "\n" + map + "\n" + MobclickAgent.getGlobalProperties(AppApplication.CONTEXT));
        }
        try {
            //保险措施：友盟源码中map为null或者map中key或者value为null时不会执行统计发送操作
            map.remove("");
            if (map.containsValue(null)) {
                for (Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, Object> entry = iterator.next();
                    if (entry == null || entry.getValue() == null) {
                        iterator.remove();
                    }
                }
            }
            map.remove(null);
        } catch (Exception e) {
            if (IS_BURY_DEBUG) {
                Logger.e("UMengUtil-onEventObject-" + eventID + "去null操作异常");
            }
        }
        if (!AppUntil.isTourist()) {
            MobclickAgent.onEventObject(AppApplication.CONTEXT, eventID, map, pageCode);
        }

    }

    /* ******************************************************************************************* */

    /**
     * APP启动埋点
     */
    public static void appStartEvent(Object start_mode) {
        Map<String, Object> map = new HashMap<>();
        map.put("start_mode", start_mode);
        onEventObject("AppStart", map, "");
    }

    /**
     * 埋点只有eventId，没有其他属性
     */

    public static void postEvent(String eventID) {
        MobclickAgent.onEvent(AppApplication.CONTEXT, eventID);
    }

    //上送配置的埋点，只需把里面的id取出来
    public static void postEvent(Map<String, Object> map) {
        if (!CheckUtil.isEmpty(map) && !CheckUtil.isEmpty(map.get("event_id"))) {
            String id = map.get("event_id") + "";
            Map<String, Object> newMap = new HashMap<>(map);
            newMap.remove("event_id");
            onEventObject(id, newMap, "");
        }
    }


    /**
     * APP禁止自动采集埋点
     */
    public static void skipEvent() {
        MobclickAgent.skipMe(AppApplication.CONTEXT, null);
    }

    /**
     * 按钮模块点击
     * <p>
     * value[0] = source_column  按钮所在栏目<p>
     * value[0] = source_tier  页面所属层级<p>
     * value[1] = source_module  按钮所在分类模块<p>
     * value[2] = button_name  按钮名称<p>
     * value[3] = button_position 按钮位置<p>
     */
    public static void eventModuleButtonClick(Context context, String pageCode, Object... value) {
        Map<String, Object> map = new HashMap<>();
        map.put("source_column", value[0]);
        map.put("source_tier", value[1]);
        map.put("source_module", value[2]);
        map.put("button_name", value[3]);
        if (value.length >= 5) {
            map.put("button_position", value[4]);
        }
        onEventObject("ModuleButtonClick", map, pageCode);
    }

    /**
     * 简单的完成时间上送
     *
     * @param eventId    事件ID
     * @param isSuccess  成功与否
     * @param failReason 失败原因
     */
    public static void eventSimpleComplete(String eventId, String pageCode, String isSuccess, String failReason) {
        Map<String, Object> map = new HashMap<>();
        map.put("is_success", isSuccess);
        if (failReason != null) {
            map.put("fail_reason", failReason);
        }
        onEventObject(eventId, map, pageCode);
    }

    /**
     * 简单的完成时间上送
     *
     * @param eventId    事件ID
     * @param isSuccess  成功与否
     * @param failReason 失败原因
     */
    public static void eventSimpleCompleteWithPageName(String eventId, String pageCode,String page_name_cn, String isSuccess, String failReason) {
        Map<String, Object> map = new HashMap<>();
        map.put("is_success", isSuccess);
        map.put("page_name_cn", page_name_cn);
        if (failReason != null) {
            map.put("fail_reason", failReason);
        }
        onEventObject(eventId, map, pageCode);
    }

    /**
     * 通用的点击事件
     * 大部分的点击事件都只包含了一个元素-- button_name
     * eventId  -- eventId
     * buttonName  -- 点击的 button名
     * pageCode页面浏览code
     */
    public static void commonClickEvent(String eventId, String buttonName, String pageCode) {
        commonClickEvent(eventId, buttonName, null, null, pageCode);
    }

    /**
     * 通用的点击事件
     * 大部分的点击事件都只包含了一个元素-- button_name
     * eventId  -- eventId
     * buttonName  -- 点击的 button名
     * pageName  -- 页面名
     * pageCode页面浏览code
     */
    public static void commonClickEvent(String eventId, String buttonName, String pageName, String pageCode) {
        commonClickEvent(eventId, buttonName, pageName, null, pageCode);
    }

    /**
     * 通用的点击事件
     * 大部分的点击事件都只包含了一个元素-- button_name
     * eventId  -- eventId
     * buttonName  -- 点击的 button名
     * pageName  -- 页面名
     * pageCode页面浏览code
     */
    public static void commonClickEvent(String eventId, String buttonName, String pageName, Map<String, Object> map, String pageCode) {
        Map<String, Object> hashMap = new HashMap<>();
        if (!TextUtils.isEmpty(buttonName)) {
            hashMap.put("button_name", buttonName);
        }
        if (!TextUtils.isEmpty(pageName)) {
            hashMap.put("page_name_cn", pageName);
        }
        if (map != null) {
            hashMap.putAll(map);
        }
        onEventObject(eventId, hashMap, pageCode);
    }

    /**
     * 通用的页面点击事件
     * 大部分的点击事件都只包含了一个元素-- button_name
     * 但是某些会包含页面信息 source_page	首页、我的商品、二维码生成、商品二维码
     * pageCode页面浏览code
     */
    public static void commonClickEvent(String eventId, String buttonName, Map<String, Object> map, String pageCode) {
        Map<String, Object> hashMap = new HashMap<>();
        if (!TextUtils.isEmpty(buttonName)) {
            hashMap.put("button_name", buttonName);
        }
        if (map != null) {
            hashMap.putAll(map);
        }
        onEventObject(eventId, hashMap, pageCode);
    }

    /**
     * 通用资源位点击事件
     */
    public static void commonClickBannerEvent(String eventId, String pageName, String bannerName, Map<String, Object> map, String pageCode) {
        Map<String, Object> hashMap = new HashMap<>();
        if (!TextUtils.isEmpty(pageName)) {
            hashMap.put("page_name_cn", pageName);
        }
        if (!TextUtils.isEmpty(bannerName)) {
            hashMap.put("ad_name", bannerName);
        }
        if (map != null) {
            hashMap.putAll(map);
        }
        onEventObject(eventId, hashMap, pageCode);
    }

    public static void commonClickBannerEvent(String eventId, String pageName, String bannerName, String cid, String groupId, Map<String, Object> map, String pageCode) {
        Map<String, Object> hashMap = new HashMap<>();
        if (!TextUtils.isEmpty(pageName)) {
            hashMap.put("page_name_cn", pageName);
        }
        if (!TextUtils.isEmpty(bannerName)) {
            hashMap.put("ad_name", bannerName);
        }
        hashMap.put("cid", cid != null ? cid : "");
        hashMap.put("group_id", groupId != null ? groupId : "");
        if (map != null) {
            hashMap.putAll(map);
        }
        onEventObject(eventId, hashMap, pageCode);
    }

    public static void commonClickBannerEvent(String eventId, String pageName, String bannerName, String cid, String groupId, String pageCode) {
        commonClickBannerEvent(eventId, pageName, bannerName, cid, groupId, null, pageCode);
    }

    public static void commonClickBannerEvent(String eventId, String pageName, String bannerName, String pageCode) {
        commonClickBannerEvent(eventId, pageName, bannerName, null, pageCode);
    }

    /**
     * 通用的页面点击完成事件
     * id button_name is_success fail_reason
     * pageCode页面浏览code
     */
    public static void commonClickCompleteEvent(String eventId, String buttonName, String isSuccess, String failReason, String pageCode) {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("button_name", buttonName);
        hashMap.put("is_success", isSuccess);
        hashMap.put("fail_reason", failReason);
        onEventObject(eventId, hashMap, pageCode);
    }

    /**
     * 通用的完成事件
     * 通过对完成事件总结，完成事件一般包含以下元素
     * is_success    fail_reason（有些没有该元素）
     * 因此封装如下方法
     * eventID  --  事件id
     * pageCode页面浏览code
     * map  --  除了公共参数以外的参数
     * 通用参数
     */
    public static void commonCompleteEvent(String eventId, Map<String, Object> map, String isSuccess, String failReason, String pageCode) {
        Map<String, Object> completeMap = new HashMap<>();
        completeMap.put("is_success", isSuccess);
        if (map != null) {
            completeMap.putAll(map);
        }
        if (failReason != null) {
            completeMap.put("fail_reason", failReason);
        }
        onEventObject(eventId, completeMap, pageCode);
    }

    /**
     * 贷超曝光事件
     * pageCode页面浏览code
     */
    public static void eventDaiChao(String eventId, boolean isExposure, String exposureReason, String pageCode) {
        //UiUtil.toastDeBug(" 友盟初始化完成: " + isInit + " 友盟埋点：id= " + id + " 贷超 " + isExposure);
        //UiUtil.toast("友盟初始化完成:" + isInit, true);
        Map<String, Object> map = new HashMap<>();
        map.put("is_exposure", String.valueOf(isExposure));
        map.put("exposure_reason", exposureReason);
        map.put("page_name_cn", "首页");
        onEventObject(eventId, map, pageCode);
    }

    /**
     * 通用曝光事件
     *
     * @param eventId  事件id
     * @param pageName 页面名称
     * @param adName   曝光的资源位名称
     * @param pageCode 页面浏览事件eventId
     */
    public static void commonExposureEvent(String eventId, String pageName, String adName, String pageCode) {
        Map<String, Object> map = new HashMap<>();
        if (!CheckUtil.isEmpty(pageName)) {
            map.put("page_name_cn", pageName);
        }
        if (!CheckUtil.isEmpty(adName)) {
            map.put("ad_name", adName);
        }
        onEventObject(eventId, map, pageCode);
    }

    public static void commonExposureEvent(String eventId, String pageName, String adName, Map<String, Object> map, String pageCode) {
        Map<String, Object> hashMap = new HashMap<>();
        if (!CheckUtil.isEmpty(pageName)) {
            hashMap.put("page_name_cn", pageName);
        }
        if (!CheckUtil.isEmpty(adName)) {
            hashMap.put("ad_name", adName);
        }
        if (map != null) {
            hashMap.putAll(map);
        }
        onEventObject(eventId, hashMap, pageCode);
    }

    public static void commonExposureEvent(String eventId, String pageName, String adName, String cid, String groupId, String pageCode) {
        Map<String, Object> map = new HashMap<>();
        if (!CheckUtil.isEmpty(pageName)) {
            map.put("page_name_cn", pageName);
        }
        if (!CheckUtil.isEmpty(adName)) {
            map.put("ad_name", adName);
        }
        map.put("cid", cid != null ? cid : "");
        map.put("group_id", groupId != null ? groupId : "");
        onEventObject(eventId, map, pageCode);
    }

    public static void commonExposureEvent(String eventId, String pageName, String adName, String cid, String groupId, Map<String, Object> map, String pageCode) {
        Map<String, Object> hashMap = new HashMap<>();
        if (!CheckUtil.isEmpty(pageName)) {
            hashMap.put("page_name_cn", pageName);
        }
        if (!CheckUtil.isEmpty(adName)) {
            hashMap.put("ad_name", adName);
        }
        hashMap.put("cid", cid != null ? cid : "");
        hashMap.put("group_id", groupId != null ? groupId : "");
        if (map != null) {
            hashMap.putAll(map);
        }
        onEventObject(eventId, hashMap, pageCode);
    }
}

