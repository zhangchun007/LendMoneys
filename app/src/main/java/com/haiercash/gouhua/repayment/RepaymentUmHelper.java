package com.haiercash.gouhua.repayment;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.beans.InterestFreeBean;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

public class RepaymentUmHelper {

    //免息券使用成功与否还款友盟埋点
    public static void postUmClickEvent(InterestFreeBean.RepayCouponsBean bean, String repayMoney, String loanNo, String isSuccess, String failReason, String pageCode) {
        if (bean == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "还款页面");
        map.put("free_card_No", UiUtil.getEmptyStr(bean.getBatchNo()));
        map.put("free_card_id", UiUtil.getEmptyStr(bean.getCouponNo()));
        map.put("free_card_type", UiUtil.getEmptyStr(bean.getKindName()));
        String date = "";
        if (!CheckUtil.isEmpty(bean.getBindStartDt())) {
            date += TimeUtil.getNeedDate(bean.getBindStartDt());
        }
        date += "-";
        if (!CheckUtil.isEmpty(bean.getBindEndDt())) {
            date += TimeUtil.getNeedDate(bean.getBindEndDt());
        }
        map.put("free_card_timelimit", date);
        map.put("free_card_name", bean.getCouponTypeName());
        map.put("discount_memory", UiUtil.getEmptyMoneyStr(bean.getDiscValue()));
        map.put("repayment_money", UiUtil.getEmptyMoneyStr(repayMoney));
        map.put("loan_id", UiUtil.getEmptyStr(loanNo));
        map.put("is_contact_loan", bean.hasBind() ? "是" : "否");
        UMengUtil.commonCompleteEvent("FreeCardRepayment", map, isSuccess, failReason, pageCode);
    }
}
