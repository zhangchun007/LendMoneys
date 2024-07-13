package com.haiercash.gouhua.utils;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.activity.borrowmoney.GoBorrowMoneyActivity;
import com.haiercash.gouhua.activity.edu.EduProgressHelper;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.ChaXunKeHuBianHao_get;
import com.haiercash.gouhua.beans.InterestFreeBean;
import com.haiercash.gouhua.beans.OrderBean;
import com.haiercash.gouhua.beans.borrowmoney.LoanRatAndProduct;
import com.haiercash.gouhua.beans.homepage.Credit;
import com.haiercash.gouhua.fragments.main.MainEduBorrowUntil;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.hybrid.H5ConfigHelper;
import com.haiercash.gouhua.hybrid.H5LinkJumpHelper;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.network.NetHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 点击“免息券去使用”工具类
 * 包括各状态跳转逻辑
 */
public class ClickCouponToUseUtil implements INetResult {
    private BaseActivity mActivity;
    private final NetHelper netHelper;
    private Credit homePageInfo;
    private String typeLevelTwo;
    private String minAmt;
    private String typCde;
    private InterestFreeBean.RepayCouponsBean mClickCoupon;//某些页面传递过来的券，跳转七日待还列表可能需要
    private boolean fromCoupon;  //来自优惠券则走优惠券逻辑，否则走正常流程

    public ClickCouponToUseUtil(@NonNull BaseActivity mActivity) {
        this.mActivity = mActivity;
        netHelper = new NetHelper(this);
    }

    public void clickCouponToUse(String clickCouponNo) {
        if (CheckUtil.isEmpty(clickCouponNo)) {
            return;
        }
        mActivity.showProgress(true);
        Map<String, String> params = new HashMap<>();
        params.put("couponNo", RSAUtils.encryptByRSA(clickCouponNo));
        params.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));//用户编号
        params.put("userId", RSAUtils.encryptByRSA(SpHp.getLogin(SpKey.LOGIN_USERID)));//用户id
        netHelper.postService(ApiUrl.POST_QUERY_COUPON_DETAIL, params);
    }

    public void clickCouponToUse(InterestFreeBean.RepayCouponsBean clickCoupon) {
        if (clickCoupon == null) {
            mActivity.showProgress(false);
            return;
        }
        mClickCoupon = clickCoupon;
        /*
         *根据后台返回券状态跳转，4和4A才需要调额度状态接口
         * "2"-未到绑定有效期/使用有效期（新）或使用有效期（老）
         * "4"-老券/新券（绑定有效期为空）在使用有效期内，但是未使用->先做额度校验,有额度就跳转七日待还，不传券,无额度就走申额流程
         * "4A”-新券绑定有效期内未绑定->先做额度校验，有额度就跳转借款，其他走正常流程
         * "3A“-新券已绑定未使用且在使用有效期内->跳转七日待还，并传券，跳转后做待还使用匹配
         */
        if ("2".equals(clickCoupon.getSubState())) {
            mActivity.showProgress(false);
            UiUtil.toast("此券还未到可使用期哦~");
        } else if ("3A".equals(clickCoupon.getSubState())) {
            mActivity.showProgress(false);
                H5LinkJumpHelper.INSTANCE().goH5RepayPage(mActivity);
        } else if ("4".equals(clickCoupon.getSubState()) ||
                "4A".equals(clickCoupon.getSubState())) {
            requestUserLoanStatus();
        }
    }

    private void requestUserLoanStatus() {
        requestUserLoanStatus(true);
    }

    public void requestUserLoanStatus(boolean isFromCoupon) {
        fromCoupon = isFromCoupon;
        mActivity.showProgress(true);
        Map<String, String> params = new HashMap<>();
        netHelper.postService(ApiUrl.POST_CREDIT_INFO, params, Credit.class);
    }

//    private LoanLimitDialog mLoanLimitDialog;

//    /**
//     * 弹出没有额度的弹窗
//     */
//    private void showLoanLimitDialog() {
//        if (mLoanLimitDialog == null || mLoanLimitDialog.getOwnerActivity() == null || mLoanLimitDialog.getOwnerActivity().isFinishing()) {
//            mLoanLimitDialog = new LoanLimitDialog(mActivity);
//        }
//        mLoanLimitDialog.setOnClickListener((dialog, which) -> checkEDJHProgress());
//        if (!mLoanLimitDialog.isShowing()) {
//            mLoanLimitDialog.show();
//        }
//    }

    private void startEvent(Credit creditBean) {
        if (!AppApplication.isLogIn()) {
            mActivity.showProgress(false);
            LoginSelectHelper.staticToGeneralLogin();
            return;
        }
        if (!fromCoupon) {
            mActivity.showProgress(false);
            MainEduBorrowUntil.INSTANCE(mActivity).startEvent(creditBean, null);
            return;
        }
        this.homePageInfo = creditBean;
        String status;
        if (creditBean != null && !CheckUtil.isEmpty(creditBean.getStatus())) {
            status = creditBean.getStatus();
            MainEduBorrowUntil.status = status;
            MainEduBorrowUntil.oldStatus = creditBean.getOldStatus();
        } else {
            status = "A";
        }
        String clickCouponSubState = mClickCoupon != null && mClickCoupon.getSubState() != null ? mClickCoupon.getSubState() : "";
        /*免息券状态-4
         总体设计遵循的优先级：7日还款＞借款＞申额
         额度卡片码值	跳转
         A2-H2/A3-H3	7日待还
         D1	借款
         A1/F1/G1（无额度、被退回、失效）	申额
         B1/C1/E1/H1(审批中、冻结、被拒)	异常状态
         异常状态提示dialog文案：您的额度状态异常，无法借款
         异常状态提示dialog按钮：知道了
         *
         免息券状态：4A
         设计遵循的优先级：借款>申额>还款
         额度卡片码值	跳转
         D1/D2	借款
         A1/A2/F1/F2/G1/G2	申额
         A3-H3	7日待还
         B1/B2/C1/C2/E1/E2/H1/H2（审批中、额度变更中、额度冻结、被拒）	异常状态
         异常状态提示dialog文案：您的额度状态异常，无法借款
         异常状态提示dialog按钮：知道了
         */
        if (("4".equals(clickCouponSubState) && ("R".equals(status)))
                || ("4A".equals(clickCouponSubState) && "R".equals(status))) {
            mActivity.showProgress(false);
                H5LinkJumpHelper.INSTANCE().goH5RepayPage(mActivity);
        } else if (("4".equals(clickCouponSubState) && status.equals("D"))
                || ("4A".equals(clickCouponSubState) && (status.equals("D")))) {
            toBorrow();
        } else if (("4".equals(clickCouponSubState) || "4A".equals(clickCouponSubState)) && (status.equals("A") || status.equals("F") || status.equals("G") || status.equals("H"))) {
//            showLoanLimitDialog();
            if (status.equals("G") || status.equals("H")) {
                MainEduBorrowUntil.INSTANCE(mActivity).startEvent(this.homePageInfo, null);
            } else {
                checkEDJHProgress();
            }
        } else if (("4".equals(clickCouponSubState) || "4A".equals(clickCouponSubState)) && (status.equals("B") || status.equals("C") || status.equals("E"))) {
            mActivity.showProgress(false);
            mActivity.showDialog("您的额度状态异常，无法借款");
        }
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        mActivity.showProgress(false);
        if (ApiUrl.POST_CREDIT_INFO.equals(url)) {
            Credit bean = (Credit) t;
            if (bean != null) {
                SpHp.saveUser(SpKey.USER_EDU_SPE_SEQ, bean.getSpeSeq());
                MainHelper.saveMoneyState(bean.getAvailLimit(), bean.getTotalLimit());
            }
            startEvent(bean);

        } else if (ApiUrl.urlOrder.equals(url)) {
            mActivity.showProgress(false);
            if (AppApplication.enableLoan) {
                goH5LoanPage();
            } else {
                mActivity.startActivity(new Intent(mActivity, GoBorrowMoneyActivity.class));
            }
        } else if (ApiUrl.URL_GET_STANDARD_PRODUCT_INFO.equals(url)) {
            List<LoanRatAndProduct> loanCodeBeanList = JsonUtils.fromJsonArray(t, "tnrOptList", LoanRatAndProduct.class);
            if (CheckUtil.isEmpty(loanCodeBeanList)) {
                mActivity.showProgress(false);
                mActivity.showDialog("没有匹配到可选择的贷款产品");
                return;
            }
            String[] codeMin = MainHelper.getCodeAndMin(loanCodeBeanList);
            setMinAmtAndTypeLevelTwo(codeMin[0], codeMin[1], codeMin[2]);
            startEvent(homePageInfo);
        } else if (ApiUrl.POST_QUERY_COUPON_DETAIL.equals(url)) {
            if (t != null) {
                clickCouponToUse(JsonUtils.fromJson(t, InterestFreeBean.RepayCouponsBean.class));
            } else {
                mActivity.showProgress(false);
            }
        } else if (ApiUrl.GET_HOME_CUST_INFO.equals(url)) {
            ChaXunKeHuBianHao_get bean = (ChaXunKeHuBianHao_get) t;
            if (!CheckUtil.isEmpty(bean) &&
                    !CheckUtil.isEmpty(bean.getCustNo()) &&
                    !CheckUtil.isEmpty(bean.getSpeSeq())) {
                SpHp.saveUser(SpKey.USER_CUSTNAME, bean.getCustName());//客户姓名
                SpHp.saveUser(SpKey.USER_CUSTNO, bean.getCustNo());//客户编号
                SpHp.saveUser(SpKey.USER_CERTNO, bean.getCertNo());//证件号
                SpHp.saveUser(SpKey.USER_MOBILE, bean.getMobile());//实名认证手机号
                SpHp.saveUser(SpKey.USER_EDU_SPE_SEQ, bean.getSpeSeq());
                getStandardProductInfo();
            } else mActivity.showDialog("数据有误，请稍后重试");
        }

    }

    @Override
    public void onError(BasicResponse error, String url) {
        mActivity.showProgress(false);
        if (ApiUrl.urlOrder.equals(url) || ApiUrl.URL_GET_STANDARD_PRODUCT_INFO.equals(url)) {
            mActivity.showDialog(error.getHead().getRetMsg());
        } else {
            mActivity.onError(error, url);
        }
    }

    private void toBorrow() {
        RiskInfoUtils.postGioData(mActivity, "START_CASH");
        if (CheckUtil.isEmpty(minAmt)) {
            mActivity.showProgress(true);
            if (CheckUtil.isEmpty(SpHp.getUser(SpKey.USER_CUSTNO)) ||
                    CheckUtil.isEmpty(SpHp.getUser(SpKey.USER_EDU_SPE_SEQ))) {
                netHelper.getService(ApiUrl.GET_HOME_CUST_INFO, null, ChaXunKeHuBianHao_get.class);
            } else {
                getStandardProductInfo();
            }
            return;
        }
        if (Double.parseDouble(homePageInfo.getAvailLimit() == null ? "0" : homePageInfo.getAvailLimit()) >= Double.parseDouble(minAmt)) {
            mActivity.showProgress(true);
            new GhLocation(mActivity, true, (isSuccess, reason) -> {
                if (isSuccess) {
                    startBorrowForOrder();
                } else {
                    mActivity.showProgress(false);
                    mActivity.showDialog(CheckUtil.isEmpty(reason) ? "定位失败,请稍后重试" : reason);
                }
            }).requestLocation();
        } else {
            mActivity.showProgress(false);
            mActivity.showDialog("对不起，您的剩余额度低于最低借款金额" + minAmt + "元，建议您在额度恢复后再借款");
        }
    }

    //查询已认证客户的贷款品种及利率费率
    private void getStandardProductInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));
        //map.put("typGrp", "02");
        map.put("speSeq", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_EDU_SPE_SEQ)));
        netHelper.postService(ApiUrl.URL_GET_STANDARD_PRODUCT_INFO, map);
    }

    /**
     * 检查额度激活流程
     */
    private void checkEDJHProgress() {
        if (AppApplication.enableCredit) {
            if (!CheckUtil.isEmpty(H5ConfigHelper.h5SceneCreditUrl)) {
                goH5Page(H5ConfigHelper.h5SceneCreditUrl);
            } else if (!CheckUtil.isEmpty(H5ConfigHelper.h5CreditUrl)) {
                goH5Page(H5ConfigHelper.h5CreditUrl);
            } else {
                mActivity.showProgress(true);
                H5ConfigHelper helper = new H5ConfigHelper(MainEduBorrowUntil.oldStatus, "", new H5ConfigHelper.IConfigCallback() {
                    @Override
                    public void configSuccess() {
                        mActivity.showProgress(false);
                        if (!CheckUtil.isEmpty(H5ConfigHelper.h5SceneCreditUrl)) {
                            goH5Page(H5ConfigHelper.h5SceneCreditUrl);
                        }else if (!CheckUtil.isEmpty(H5ConfigHelper.h5CreditUrl)) {
                            goH5Page(H5ConfigHelper.h5CreditUrl);
                        } else {
                            configFail();
                        }
                    }

                    //再次调用失败的话走原生
                    @Override
                    public void configFail() {
                        mActivity.showProgress(false);
                        EduProgressHelper.getInstance().checkProgress(mActivity, false);
                    }
                });
                helper.getH5LinkData();
            }
        } else {
            EduProgressHelper.getInstance().checkProgress(mActivity, false);
        }
    }

    //跳转到h5页面
    private void goH5Page(String url) {
        mActivity.showProgress(false);
        Intent intent = new Intent(mActivity, JsWebBaseActivity.class);
        intent.putExtra("jumpKey", url);
        intent.putExtra("isHideCloseIcon", true);
        mActivity.startActivity(intent);
    }

    /******************************************定位响应*************************************************/
    private void startBorrowForOrder() {
        // 定位成功 则进行 录单校验
        String cityCode = SpHelper.getInstance().readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_CITYCODE);//市代码
        String provinceCode = SpHelper.getInstance().readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_PROVINCECODE);//省代码
        if (CheckUtil.isEmpty(typeLevelTwo)) {
            mActivity.showProgress(false);
            UiUtil.toastDeBug("贷款品种小类 [typeLevelTwo] 为空");
            return;
        }
        mActivity.showProgress(true);
        //录单校验
        Map<String, String> map = new HashMap<>();
        map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
        map.put("provinceCode", provinceCode);
        map.put("cityCode", cityCode);
        map.put("typLevelTwo", typeLevelTwo);//贷款品种小类
        map.put("typCde", typCde);
        netHelper.getService(ApiUrl.urlOrder, map, OrderBean.class, true);
    }

    void setMinAmtAndTypeLevelTwo(String typCde, String minAmt, String typeLevelTwo) {
        this.typCde = typCde;
        this.minAmt = minAmt;
        this.typeLevelTwo = typeLevelTwo;
    }

    /**
     * 手动断开连接，释放资源
     */
    public void onDestroy() {
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
        mActivity = null;
        EduProgressHelper.getInstance().onDestroy();
    }

    private void goH5LoanPage() {
        if (!CheckUtil.isEmpty(H5ConfigHelper.h5SceneLoanUrl)) {
            goH5Page(H5ConfigHelper.h5SceneLoanUrl);
        } else if (!CheckUtil.isEmpty(H5ConfigHelper.h5LoanUrl)) {
            goH5Page(H5ConfigHelper.h5LoanUrl);
        } else {
            if (homePageInfo == null) {
                mActivity.showDialog("数据有误，请稍后重试");
                return;
            }
            mActivity.showProgress(true);
            String oldStatus = homePageInfo.getOldStatus();
            H5ConfigHelper helper = new H5ConfigHelper(oldStatus, "", new H5ConfigHelper.IConfigCallback() {
                @Override
                public void configSuccess() {
                    mActivity.showProgress(false);
                    if (!CheckUtil.isEmpty(H5ConfigHelper.h5SceneLoanUrl)) {
                        goH5Page(H5ConfigHelper.h5SceneLoanUrl);
                    } else if (!CheckUtil.isEmpty(H5ConfigHelper.h5LoanUrl)) {
                        goH5Page(H5ConfigHelper.h5LoanUrl);
                    } else {
                        configFail();
                    }
                }

                //再次调用失败的话走原生
                @Override
                public void configFail() {
                    mActivity.showProgress(false);
                    mActivity.startActivity(new Intent(mActivity, GoBorrowMoneyActivity.class));
                }
            });
            helper.getH5LinkData();
        }
    }

}
