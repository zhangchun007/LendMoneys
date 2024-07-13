package com.haiercash.gouhua.utils;

import android.text.TextUtils;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.base.AppApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 上传点击事件
 * @Author: zhangchun
 * @CreateDate: 3/28/23
 * @Version: 1.0
 */
public class UploadEventHelper {


    /*指纹设置成功/失败埋点*/
    public static void postUmEventWithFingerprintResult(boolean isSuccess, String failReason) {
        Map<String, Object> map = new HashMap<>();
        map.put("is_success", isSuccess ? "true" : "false");
        map.put("fail_reason", !CheckUtil.isEmpty(failReason) ? failReason : "无");
        map.put("page_name_cn", "设置指纹页");
        UMengUtil.onEventObject("SfrpSet_Result", map, "MePage");
    }

    /**
     * 个人中心关于我们图片点击
     */
    public static void postAboutImgClickEvent() {
        Map<String, Object> map = new HashMap<>();
        map.put("button_name", "品宣图片");
        map.put("page_name_cn", "我的页");
        UMengUtil.onEventObject("Gh_Me_BottonBanner_Click", map, "MePage");
    }


    /**
     * 我的页面-常用功能-点击
     */
    public static void postCommonFuncClickEvent(String button_name) {
        Map<String, Object> map = new HashMap<>();
        map.put("button_name", button_name);
        map.put("page_name_cn", "我的页");
        UMengUtil.onEventObject("Gh_Me_CommonWork_Click", map, "MePage");
    }

    /**
     * 我的页-导航栏-设置-点击
     */
    public static void postCenterSettingClickEvent() {
        Map<String, Object> map = new HashMap<>();
        map.put("button_name", "设置");
        map.put("page_name_cn", "我的页");
        UMengUtil.onEventObject("Gh_Me_Set_Click", map, "MePage");
    }

    /**
     * 我的页-导航栏-消息中心-点击
     */
    public static void postCenterMessageClickEvent(boolean is_read) {
        Map<String, Object> map = new HashMap<>();
        map.put("button_name", "消息中心");
        map.put("page_name_cn", "我的页");
        map.put("is_read", !is_read ? "是" : "否");
        UMengUtil.onEventObject("Gh_Me_Message_Click", map, "MePage");
    }


    /**
     * 我的页-还款卡片-曝光
     */
    public static void postSevenCardShowEvent(String loanIsOd, String days, String recently_repay_day) {
        boolean isOver = "Y".equals(loanIsOd);
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "我的页");
        map.put("button_name", "去还款");
        map.put("overdue_flag", isOver ? "是" : "否");
        if (isOver && !TextUtils.isEmpty(days)) {
            map.put("overdue_day", days);
        }
        if (!isOver && !TextUtils.isEmpty(recently_repay_day)) {
            map.put("recently_repay_day", recently_repay_day);
        }
        UMengUtil.onEventObject("Gh_Me_EduCard_Exposure", map, "MePage");
    }

    /**
     * 我的页-还款卡片点击
     */
    public static void postSevenCardClickEvent(String loanIsOd, String days, String recently_repay_day) {
        boolean isOver = "Y".equals(loanIsOd);
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "我的页");
        map.put("button_name", "去还款");
        map.put("overdue_flag", isOver ? "是" : "否");
        if (isOver && !TextUtils.isEmpty(days)) {
            map.put("overdue_day", days);
        }
        if (!isOver && !TextUtils.isEmpty(recently_repay_day)) {
            map.put("recently_repay_day", recently_repay_day);
        }
        UMengUtil.onEventObject("Gh_Me_EduCard_Click", map, "MePage");
    }


    /**
     * 我的页-会员入口-曝光
     *
     * @is_member 是否是vip
     */
    public static void postVipImgShowEvent(boolean is_member) {
        String user_state = "";
        if (AppApplication.isLogIn()) {
            if (CommomUtils.isRealName()) {
                user_state = "已实名";
            } else {
                user_state = "未实名";
            }
        } else {
            user_state = "未登录";
        }
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "我的页");
        map.put("ad_name", "会员入口");
        map.put("user_state", user_state);
        map.put("is_member", is_member ? "会员" : "非会员");
        UMengUtil.onEventObject("Gh_Me_Member_Exposure", map, "MePage");
    }

    /**
     * 我的页-金刚区功能-点击
     */
    public static void postGhMeVajraClickEvent(String button_name) {
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "我的页");
        map.put("button_name", button_name);
        UMengUtil.onEventObject("Gh_Me_Vajra_Click", map, "MePage");
    }

    /**
     * 我的页-会员入口-点击
     */
    public static void postVipImgClickEvent(boolean is_member) {
        String user_state = "";
        if (AppApplication.isLogIn()) {
            if (CommomUtils.isRealName()) {
                user_state = "已实名";
            } else {
                user_state = "未实名";
            }
        } else {
            user_state = "未登录";
        }
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "我的页");
        map.put("ad_name", "会员入口");
        map.put("user_state", user_state);
        map.put("is_member", is_member ? "会员" : "非会员");
        UMengUtil.onEventObject("MePageAdPosition_Click", map, "MePage");
    }

    /**
     * 我的页-个人信息-点击
     */
    public static void postPersonClickEvent(boolean is_member) {
        String user_state = "";
        if (AppApplication.isLogIn()) {
            if (CommomUtils.isRealName()) {
                user_state = "已实名";
            } else {
                user_state = "未实名";
            }
        } else {
            user_state = "未登录";
        }
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "我的页");
        map.put("button_name", "个人信息");
        map.put("user_state", user_state);
        map.put("is_member", is_member ? "会员" : "非会员");
        UMengUtil.onEventObject("Gh_Me_PersonMsg_Click", map, "MePage");
    }

    /**
     * 点击七日或者全部
     *
     * @param id         事件ID
     * @param buttonName 7日待还&全部待还
     * @param isOverdue  是否逾期
     */
    public static void postPayEvent(String id, String buttonName, String isOverdue) {
        Map<String, Object> map = new HashMap<>();
        String overdue_flag = "Y".equals(isOverdue) ? "true" : "false";
        map.put("overdue_flag", overdue_flag);
        UMengUtil.commonClickEvent(id, buttonName, map, "MePage");
    }


}
