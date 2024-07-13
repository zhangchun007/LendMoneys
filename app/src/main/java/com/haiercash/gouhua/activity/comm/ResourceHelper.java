package com.haiercash.gouhua.activity.comm;

import android.util.DisplayMetrics;

import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.beans.ResourceBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 资源位工具类
 */
public class ResourceHelper {

    public static void requestResource(NetHelper netHelper, String page, String module, String size) {
        Map<String, String> params = new HashMap<>();
        String biz;
        //用户相关业务线
        String token = TokenHelper.getInstance().getCacheToken();
        if (!CheckUtil.isEmpty(token)) {
            biz = TokenHelper.getInstance().getSmyParameter("business");
        } else {
            biz = NetConfig.TD_BUSINESS_SMY;
        }
        /*
         * X0-平台运营
         * X1-萨摩耶
         * X2-互联网自营
         */
        params.put("biz", !CheckUtil.isEmpty(biz) ? biz : NetConfig.TD_BUSINESS_EMPTY);
        /*
         * ghAPP-够花APP
         * bzH5-H5
         * xjAPP-消金APP
         * ghXcx-够花小程序
         * xjXcx-消金小程序
         * ghGzh-够花公众号
         * xjGzh-消金公众号
         * 载体用app包默认的
         */
        params.put("carrier", NetConfig.TD_REGIS_VECTOR_SMY);
        /*
         * OPEN-开屏页
         * INDEX-首页
         * PERCENTER-我的
         * CREATERESULT-额度申请结果页
         * LOANAPPLY-借款申请页
         * LOANRESULT-借款申请结果页
         * REPAYRESULT-还款结果页
         */
        params.put("page", page);
        /*
         * 位置：入参 BANNER/TOPIC
         * 开屏页：SPREAD
         * 首页：（banner/主题）  BANNER/TOPIC
         * 我的：TOPBANNER
         * 申额结果页：头部审核中-TOPREVBANNER，头部审核成功-TOPSUCBANNER，头部审核失败-TOPFALBANNER
         * 借款申请页：INPUTPRIBANNER
         * 借款结果页：头部审核中-TOPREVBANNER，头部审核成功-TOPSUCBANNER，头部审核失败-TOPFALBANNER
         * 还款结果页：中部banner-MIDBANNER
         * 其他banner：BANNER
         */
        params.put("module", module);
        /*
         *size（开屏页有该字段）
         * “1440*2560” “1080*1920” “720*1280” “480*853”
         */
        if (!CheckUtil.isEmpty(size)) {
            params.put("size", size);
        }
        String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        if (!CheckUtil.isEmpty(userId)) {
            params.put("userId", RSAUtils.encryptByRSA(userId));
        }
        netHelper.postService(ApiUrl.POST_QUERY_RESOURCE_BY_PAGE, params, ResourceBean.class);
    }

    public static void requestResource(NetHelper netHelper, String page) {
        requestResource(netHelper, page, "BANNER", null);
    }

    public static void requestResource(NetHelper netHelper, String page, String module) {
        requestResource(netHelper, page, module, null);
    }

    /**
     * 开屏页
     */
    public static void requestOpenResource(NetHelper netHelper) {
        /*
         *size（开屏页有该字段）
         * “1440*2560” “1080*1920” “720*1280” “480*853”
         */
        int x, y;
        DisplayMetrics displayMetrics = AppApplication.CONTEXT.getResources().getDisplayMetrics();
        //高包涵低
        if (displayMetrics.densityDpi > 480) {
            x = 1440;
            y = 2560;
        } else if (displayMetrics.widthPixels > 320) {
            x = 1080;
            y = 1920;
        } else if (displayMetrics.widthPixels > 240) {
            x = 720;
            y = 1280;
        } else {
            x = 480;
            y = 853;
        }
        requestResource(netHelper, "OPEN", "SPREAD", UiUtil.getStr(x, "*", y));
    }

    /**
     * 请求页面"申额结果页"中部资源位
     * result为01是审核中，为02是成功，为03是失败
     */
    public static void requestEdResultMidResource(NetHelper netHelper, String result) {
        requestResource(netHelper, "CREATERESULT", "02".equals(result) ? "TOPSUCBANNER" : ("03".equals(result) ? "TOPFALBANNER" : "TOPREVBANNER"));
    }

    /**
     * 请求页面"申额结果页"底部资源位
     */
    public static void requestEdResultBottomResource(NetHelper netHelper) {
        requestResource(netHelper, "CREATERESULT");
    }

    /**
     * 请求页面"借款页"资源位
     */
    public static void requestBorrowResource(NetHelper netHelper) {
        requestResource(netHelper, "LOANAPPLY", "INPUTPRIBANNER");
    }

    /**
     * 请求页面"借款结果页"中部资源位
     * result为01是审核中，为02是成功，为03是失败
     */
    public static void requestBorrowResultMidResource(NetHelper netHelper, String result) {
        requestResource(netHelper, "LOANRESULT", "02".equals(result) ? "TOPSUCBANNER" : ("03".equals(result) ? "TOPFALBANNER" : "TOPREVBANNER"));
    }

    /**
     * 请求页面"借款结果页"底部资源位
     */
    public static void requestBorrowResultBottomResource(NetHelper netHelper) {
        requestResource(netHelper, "LOANRESULT");
    }

    /**
     * 请求页面"还款结果页"中部资源位
     */
    public static void requestRepaymentMidResource(NetHelper netHelper) {
        requestResource(netHelper, "REPAYRESULT", "MIDBANNER");
    }

    /**
     * 请求页面"还款结果页"底部资源位
     */
    public static void requestRepaymentBottomResource(NetHelper netHelper) {
        requestResource(netHelper, "REPAYRESULT");
    }

    /**
     * 请求页面"我的"资源位
     */
    public static void requestMineResource(NetHelper netHelper) {
        requestResource(netHelper, "PERCENTER", "TOPBANNER");
    }
}
