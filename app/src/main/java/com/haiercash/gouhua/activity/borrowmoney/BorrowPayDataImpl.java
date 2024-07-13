package com.haiercash.gouhua.activity.borrowmoney;

import android.text.TextUtils;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.HmacSHA256Utils;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.SaveOrderBean;
import com.haiercash.gouhua.beans.getpayss.LoanCoupon;
import com.haiercash.gouhua.beans.postputbean.ZhiFu_Password;
import com.haiercash.gouhua.beans.risk.RiskBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.service.RiskNetServer;
import com.haiercash.gouhua.utils.BrAgentUtils;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.RiskKfaUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.WyDeviceIdUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/6/4<br/>
 * 描    述：借款支用Data类<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BorrowPayDataImpl implements INetResult {
    private final BaseActivity mActivity;
    private final NetHelper mNetHelper;
    private final String orderNo;
    private final LoanCoupon coupon;
    private final String applySeq;
    private final String borrowMoney;
    private final String borrowTnr;
    private final String typCde;
    private RiskBean riskBean;
    private final SaveOrderBean.UniteLoanInfoBean uniteLoanInfo;
    private final IBorrowPayView iView;
    private final String pageCode;

    public BorrowPayDataImpl(BaseActivity mActivity, String orderNo, String borrowMoney, String borrowTnr,
                             String typCde, LoanCoupon coupon, String applySeq,
                             SaveOrderBean.UniteLoanInfoBean uniteLoanInfo, IBorrowPayView iView, String pageCode) {
        this.mActivity = mActivity;
        this.applySeq = applySeq;
        this.orderNo = orderNo;
        this.borrowMoney = borrowMoney;
        this.borrowTnr = borrowTnr;
        this.typCde = typCde;
        this.coupon = coupon;
        this.uniteLoanInfo = uniteLoanInfo;
        this.iView = iView;
        this.pageCode = pageCode;
        mNetHelper = new NetHelper(this);
    }

    /**
     * 验证交易密码是否正确
     *
     * @param pass 交易密码
     */
    public void payPwd(String pass) {
        String userId = SpHp.getLogin(SpKey.LOGIN_USERID);//用户名
        if (CheckUtil.isEmpty(userId)) {
            UiUtil.toast("账号异常，请退出重试");
            return;
        }
        String deviceId = SystemUtils.getDeviceID(mActivity);
        if (CheckUtil.isEmpty(deviceId)) {
            UiUtil.toast("deviceId获取失败");
            return;
        }
        mActivity.showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("userId", EncryptUtil.simpleEncrypt(userId));//用户账号
        map.put("payPasswd", pass);//交易密码
        map.put("deviceId", RSAUtils.encryptByRSA(deviceId));
        //必须放在map最后一行，是对整个map参数进行签名对
        map.put("sign", HmacSHA256Utils.buildNeedSignValue(map));
        mNetHelper.getService(ApiUrl.URL_CHECK_PAY_SECRET, map, ZhiFu_Password.class, true);
    }

    /**
     * 结清证明订单提交
     */
    public void postCertificate(String applseq) {
        //调用结清证明提交订单接口，成功跳success页面，失败关闭密码页面，然后弹错提示弹窗
        Map<String, Object> map = new HashMap<>();
        map.put("applSeq", applseq);
        mNetHelper.postService(ApiUrl.DISCHARGE_ORDER_SUBMIT, map);
    }

    /**
     * 合同签订
     */
    public void signingContract() {
        if (CheckUtil.isEmpty(orderNo)) {
            mActivity.showProgress(false);
            UiUtil.toast("账号异常，请退出重试");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("orderNo", orderNo);
        mNetHelper.postService(ApiUrl.url_hetong, map);
    }

    /**
     * 提交订单+风险采集
     */
    private void commitOrderAndRiskInfo() {
        try {
            RiskKfaUtils.getRiskBean(mActivity, 0, 4, "apply_submit_success", obj -> {
                riskBean = (RiskBean) obj;
                if (riskBean != null) {
                    riskBean.setProd_code(typCde);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            UiUtil.toastDeBug("Exception  commitOrderAndRiskInfo  " + e.getMessage());
        }

        String pcaStatue = SpHelper.getInstance().readMsgFromSp(SpKey.PERSONAL_CREDIT_AUTHORIZATION, SpKey.PERSONAL_CREDIT_AUTHORIZATION_STATUE);
        Map<String, Object> map = new HashMap<>();
        map.put("orderNo", orderNo);
        //map.put("msgCode", verifyNo);
        map.put("opType", "1");
        //绑定免息券
        if (coupon != null && !CheckUtil.isEmpty(coupon.getCouponNo())) {
            map.put("couponNo", coupon.getCouponNo());
        }
        //是否联合放款
        map.put("isUniteLoan", CheckUtil.isEmpty(uniteLoanInfo) ? "0" : uniteLoanInfo.getIsUniteLoan());
        //是否需要重签征信授权
        map.put("needResignCredit", CheckUtil.isEmpty(pcaStatue) ? "N" : pcaStatue);
        String agencyId = "", agencyIdName = "", isCredit = "1";
        if (!CheckUtil.isEmpty(uniteLoanInfo)) {
            if ("1".equals(uniteLoanInfo.getIsUniteLoan())) {//是联合放款
                agencyIdName = uniteLoanInfo.getAgencyIdName();
                //合作机构主键id
                agencyId = uniteLoanInfo.getAgencyId();
                //是否需要签联合放款征信授权
                isCredit = uniteLoanInfo.getIsCredit();
            }
        }
        map.put("agencyId", agencyId);
        map.put("agencyIdName", agencyIdName);
        map.put("uniteLoanNeedSignCredit", isCredit);
        map.put("deviceId", RSAUtils.encryptByRSA(SystemUtils.getDeviceID(mActivity)));
        map.put("listRiskMap", RiskInfoUtils.getAllRiskInfo(mActivity, applySeq));
        //设置网易设备指纹数据
        WyDeviceIdUtils.getInstance().getWyDeviceIDTokenFromNative(AppApplication.CONTEXT, (token, code, msg) -> {
            if (!TextUtils.isEmpty(token)) {
                map.put("ydunToken", token);
            }
            mNetHelper.postService(ApiUrl.URL_COMMIT_ORDER_RISK_INFO, map);
        });
    }

    /**
     * 百融风险信息采集
     */
    private void useBrAgent(final String applSeq) {
        //百融风险采集
        BrAgentUtils.cashInfoBrAgent(mActivity, (afSwiftNumber, brObject) -> {
            RiskInfoUtils.postBrOrBigData(mActivity, "cash", applSeq, brObject);
            RiskInfoUtils.requestRiskInfoBrAgentInfo(afSwiftNumber, "antifraud_cash", applSeq);
        });
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        if (ApiUrl.url_hetong.equals(url)) {
            Logger.e("合同签订成功");
            commitOrderAndRiskInfo();
        } else if (ApiUrl.URL_COMMIT_ORDER_RISK_INFO.equals(url)) {
            UMengUtil.commonCompleteEvent("BorrowResult", null, "true", "", this.pageCode);
            postUmClickEvent(this.coupon, "true", "");
            mActivity.showProgress(false);
            //RiskNetServer.startRiskServer(mActivity, "apply_submit_success", applySeq, 4);
            RiskNetServer.startService(mActivity, riskBean, applySeq);
            RiskInfoUtils.send(mActivity, "额度支用", applySeq);
            RiskInfoUtils.updateRiskInfoByNode("BR014", "YES", applySeq);
            //进行百融采集
            useBrAgent(applySeq);
            if (iView != null) {
                iView.commitApplyRisk();
            }
        } else if (ApiUrl.URL_CHECK_PAY_SECRET.equals(url)) {
            iView.payPwdCallBack(true, 0);
        } else if (url.equals(ApiUrl.DISCHARGE_ORDER_SUBMIT)) {
            iView.certificateCallBack(true, JsonUtils.getRequest(t).get("email"), null);
        }
    }

    //@SuppressWarnings({"unchecked", "BoxingBoxedValue", "ConstantConditions"})
    @Override
    public void onError(BasicResponse error, String url) {
        String errorMsg = error == null || error.getHead() == null ? NetConfig.DATA_PARSER_ERROR : error.getHead().getRetMsg();
        mActivity.showProgress(false);
        if (ApiUrl.URL_COMMIT_ORDER_RISK_INFO.equals(url)) {
            UMengUtil.commonCompleteEvent("BorrowResult", null, "false", errorMsg, this.pageCode);
            postUmClickEvent(this.coupon, "false", errorMsg);
            //RiskNetServer.startRiskServer(mActivity, "apply_submit_success", applySeq, 4);
            RiskNetServer.startService(mActivity, riskBean, applySeq);
            RiskInfoUtils.updateRiskInfoByNode("BR014", "NO", applySeq);
        } else if (ApiUrl.URL_CHECK_PAY_SECRET.equals(url)) {
            Map<String, Double> map = (Map<String, Double>) error.getBody();
            if (map != null && map.containsKey("payErrorNumber") && map.containsKey("maxPayErrorNumber")) {//交易密码验证错误次数
                int num = Double.valueOf(map.get("maxPayErrorNumber")).intValue() - Double.valueOf(map.get("payErrorNumber")).intValue();
                if (Double.valueOf(map.get("payErrorNumber")).intValue() <= 5) {
                    //1.用户输入前5次密码错误
                    iView.payPwdCallBack(false, 1);
                } else if (Double.valueOf(map.get("payErrorNumber")).intValue() == 6 || Double.valueOf(map.get("payErrorNumber")).intValue() == 7) {
                    //2、用户输入第6/7次密码错误时，提示用户重新输入
                    iView.payPwdCallBack(false, num);
                }
            } else {
                //3、当用户第8次输入错误时，提示用户交易密码锁定，24小时后尝试，仅提供找回密码入口
                iView.payPwdCallBack(false, 3);
            }
            return;
        } else if (ApiUrl.DISCHARGE_ORDER_SUBMIT.equals(url)) {
            iView.certificateCallBack(false, null, errorMsg);
            return;
        }
        UiUtil.toast(errorMsg);
    }

    public void onDestroy() {
        if (mNetHelper != null) {
            mNetHelper.recoveryNetHelper();
        }
    }

    private void postUmClickEvent(LoanCoupon bean, String isSuccess, String failReason) {
        if (bean == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "借款环节");
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
        map.put("discount_money", UiUtil.getEmptyMoneyStr(bean.getDiscValue()));
        map.put("borrow_money", UiUtil.getEmptyMoneyStr(borrowMoney));
        map.put("borrow_cycle", !CheckUtil.isEmpty(borrowTnr) ? borrowTnr : "");
        UMengUtil.commonCompleteEvent("FreeCardBorrow", map, isSuccess, failReason, this.pageCode);
    }

    public interface IBorrowPayView {
        /**
         * URL_CHECK_PAY_SECRET = "app/appserver/uauth/validatePayPasswd" 接口请求
         *
         * @param payResult true 成功，false 失败
         * @param failSum   失败次数:<br/>
         *                  1.用户输入前5次密码错误<br/>
         *                  2、用户输入第6/7次密码错误时，提示用户重新输入<br/>
         *                  3、当用户第8次输入错误时，提示用户交易密码锁定，24小时后尝试，仅提供找回密码入口
         */
        void payPwdCallBack(boolean payResult, int failSum);

        /**
         * 结清证明申请接口
         *
         * @param isSuccess true 表示成功，false 表示失败
         */
        void certificateCallBack(boolean isSuccess, String email, String error);

        /**
         * 支用申请及风控信息更新整合接口(整合以下两个接口) 提交订单<br/>
         * URL_COMMIT_ORDER_RISK_INFO = "app/appserver/integration/fkb/commitApplAndUpdateRiskInfo"<br/>
         */
        void commitApplyRisk();
    }
}
