package com.haiercash.gouhua.utils;

import android.content.Intent;
import android.net.Uri;

import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.homepage.HomeRepayBean;
import com.haiercash.gouhua.fragments.main.MainEduBorrowUntil;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.tplibrary.PagePath;

import java.util.List;

/**
 * 用于处理server返回的scheme
 */
public class SchemeHelper {
    /**
     * 根据scheme 判断是否要跳转
     *
     * @param url
     * @return
     */
    public static boolean isNeedLoginFromScheme(String url) {
        Uri data = Uri.parse(url);
        String login = data.getQueryParameter("login");
        return "1".equals(login);
    }

    public static void jumpFromScheme(String url, BaseActivity mActivity) {
        jumpFromScheme(url, mActivity, null);
    }

    public static void jumpFromScheme(String url, BaseActivity mActivity, List<HomeRepayBean> repayList) {
        Uri data = Uri.parse(url);
        Logger.e("jumpFromScheme---url" + url + "---host==" + data.getHost());
        switch (data.getHost()) {
            case "onLineCustmer"://在线客服
                AIServer.showAiWebServer(mActivity, "在线客服");
                break;
            case "loanCalculator"://贷款计算器
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_CALCULATION).put("isShowTitle", false).navigation();
                break;
            case "help_center"://帮助中心
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER).navigation();
                break;
            case "settlement"://结清证明
                if (CommomUtils.isRealName()) {
                    DischargeUtil util = new DischargeUtil(mActivity);
                    util.getDischarge();
                } else {
                    mActivity.showBtn2Dialog("您在过去24个月内暂无结清账单，如需开具24个月之前账单的结清证明,请联系客服人员。", "我知道了", ((dialog, which) -> dialog.dismiss()));
                }
                break;
            case "repayRecrd"://还款记录
                ARouterUntil.getContainerInstance(PagePath.ORDER_REPAY_LIST).put("titles", "还款记录").navigation();
                break;
            case "webView"://h5页面
                if (url.contains("url=")) {
                    String linkUrl = url.substring(url.indexOf("url=") + 4);
                    Intent intent = new Intent();
                    intent.setClass(mActivity, JsWebBaseActivity.class);
                    intent.putExtra("jumpKey", linkUrl);
                    mActivity.startActivity(intent);
                } else {
                    mActivity.showDialog("网络异常，请稍后重试");
                }
                break;
            case "repay":
                MainEduBorrowUntil.INSTANCE(mActivity).goRepay(repayList);
                break;
            case "loan":
                MainEduBorrowUntil.INSTANCE(mActivity).startBorrow();
                break;
            default: //其他原生activity
                ActivityUntil.startOtherApp(mActivity, url);
                break;
        }
    }

}
