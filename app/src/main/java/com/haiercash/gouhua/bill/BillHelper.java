package com.haiercash.gouhua.bill;

import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.repayment.AllRePay;
import com.haiercash.gouhua.beans.repayment.LoanBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.uihelper.CallPhoneNumberHelper;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillHelper {
    public static void showUnableRepayDialog(BaseActivity activity) {
        CallPhoneNumberHelper.callServiceNumber(activity,
                "您的账单正处于短暂锁定状态，请保持还款银行卡余额充足，为避免重复还款，请稍后再试，详情请咨询400-018-7777。",
                "联系客服", "放弃").setTitle("您暂时不能还款");
    }

    /**
     * 接口请求：获取某个订单的全部可用还款券
     *
     * @param activity   哪一个发的loading框
     * @param fragment   哪一个发的loading框
     * @param netHelper  网络请求工具对象
     * @param loanNo     借据号
     * @param applSeq    借据流水号
     * @param loanMoney  贷款金额
     * @param loanTnr    贷款期限
     * @param loanType   贷款品种
     * @param currentTnr 当前期数 ,当前期数和利息不能同时为空，用于计算优惠金额
     * @param interestMap   利息map
     * interest           利息
     * @param repayAmt   当前应还金额
     */
    public static void getAllRepayCouponsForOrder(BaseActivity activity, BaseFragment fragment,
                                                  NetHelper netHelper, String loanNo, String applSeq,
                                                  String loanMoney, String loanTnr, String loanType,
                                                  String currentTnr, Map<String, Object> interestMap, String repayAmt,
                                                  String setlMode, String preSettle, String interest) {
        if (netHelper != null) {
            if (fragment != null) {
                fragment.showProgress(true);
            } else if (activity != null) {
                activity.showProgress(true);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));//客户号
            map.put("loanNo", RSAUtils.encryptByRSA(loanNo));//借据号
            map.put("applSeq", RSAUtils.encryptByRSA(applSeq));
            map.put("loanMoney", RSAUtils.encryptByRSA(loanMoney));
            map.put("loanTnr", loanTnr);
            map.put("loanType", loanType);
            map.put("loanRepayType", "repay");//还款券
            map.put("currentTnr", currentTnr);
            map.put("interests", interestMap);
            map.put("interest", interest);
            map.put("repayAmt", RSAUtils.encryptByRSA(repayAmt));
            map.put("setlMode", setlMode);//还款模式 FS（全部还款）NF (指定期数还款,适用于正常数据)NF还多期不支持
            map.put("preSettle", preSettle);//是否提前结清
            netHelper.postService(ApiUrl.URL_ALL_REPAY_COUPONS_FOR_ORDER, map);
        }
    }

    public static void getAllCouponsForOverdueOrder(NetHelper netHelper, List<LoanBean> selectList) {
        Map<String, Object> map = new HashMap<>();
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));//客户号
        map.put("loanList", RSAUtils.encryptByRSA(JsonUtils.toJson(selectList)));//逾期列表
        netHelper.postService(ApiUrl.URL_COUPONS_FOR_OVERDUE_ORDER, map);

    }

    public static void getAllRepayCouponsForOrder(BaseActivity activity, BaseFragment fragment, NetHelper netHelper, LoanBean loanBean, String setlMode, String preSettle) {
        if (loanBean == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put(loanBean.getPsPerdNo(), loanBean.getPsNormInt());

        getAllRepayCouponsForOrder(activity, fragment, netHelper, loanBean.getLoanNo(), loanBean.getApplSeq(), loanBean.getApprvAmt(), loanBean.getApprvTnr(),
                loanBean.getLoanTypeCode(), loanBean.getPsPerdNo(), map, loanBean.getAmount(), setlMode, preSettle, "");
    }

    public static void getAllRepayCouponsForOrder(BaseActivity activity, BaseFragment fragment, NetHelper netHelper, AllRePay allRePay, String setlMode, String preSettle) {
        if (allRePay == null) {
            return;
        }
        getAllRepayCouponsForOrder(activity, fragment, netHelper, allRePay.getLoanNo(), allRePay.getApplSeq(), allRePay.getApprvAmt(), allRePay.getApprvTnr(),
                allRePay.getLoanTypeCode(), allRePay.getCurrentTnr(), allRePay.getInterests(), allRePay.getStayAmount(), setlMode, preSettle, allRePay.getInterest());
    }
}
