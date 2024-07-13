package com.haiercash.gouhua.fragments.main;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.RepayListDialogActivity;
import com.haiercash.gouhua.activity.borrowmoney.GoBorrowMoneyActivity;
import com.haiercash.gouhua.activity.edu.EduProgressActivity;
import com.haiercash.gouhua.activity.edu.EduProgressHelper;
import com.haiercash.gouhua.activity.edu.NameAuthIdCardPatchActivity;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.CheckForApplyCreditBean;
import com.haiercash.gouhua.beans.OrderBean;
import com.haiercash.gouhua.beans.Retreated;
import com.haiercash.gouhua.beans.borrowmoney.LoanRatAndProduct;
import com.haiercash.gouhua.beans.homepage.Credit;
import com.haiercash.gouhua.beans.homepage.HomeRepayBean;
import com.haiercash.gouhua.hybrid.H5ConfigHelper;
import com.haiercash.gouhua.hybrid.H5LinkJumpHelper;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.tplibrary.livedetect.FaceRecognitionActivity;
import com.haiercash.gouhua.utils.GhLocation;
import com.haiercash.gouhua.utils.RiskInfoUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/4/16<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class MainEduBorrowUntil implements INetResult {
    private NetHelper netHelper;
    private static BaseActivity mActivity;
    private Credit homePageInfo;
    private String typeLevelTwo;
    private String minAmt;
    private String typCde;
    public static String status = ""; //记录额度状态

    public static String oldStatus = ""; //记录老的额度状态
    private static MainEduBorrowUntil instance;

    public static MainEduBorrowUntil INSTANCE(BaseActivity mActivity) {
        if (instance == null) {
            instance = new MainEduBorrowUntil();
        }
        MainEduBorrowUntil.mActivity = mActivity;
        return instance;
    }

    private MainEduBorrowUntil() {
        netHelper = new NetHelper(this);
    }

    public void startEvent(Credit credit, View v) {
        startEvent(credit, v, false);
    }

    //isHomeCard为TRUE时候，为在首页卡片点击，若是还款需要跳转到我的逾期待还，其他地方跳转到我的待还
    public void startEvent(Credit credit, View v, boolean isHomeCard) {
        if (!AppApplication.isLogIn()) {
            LoginSelectHelper.staticToGeneralLogin();
            return;
        }
        this.homePageInfo = credit;
        if (homePageInfo == null || CheckUtil.isEmpty(homePageInfo.getStatus()) || homePageInfo.getMain() == null) {
            status = "A";
            oldStatus = "A1";
        } else {
            status = credit.getStatus();
            oldStatus = credit.getOldStatus();
        }
        postHomePageButtonEvent("QuotaElement", homePageInfo == null || CheckUtil.isEmpty(homePageInfo.getMain()) ? "" : credit.getMain().getBtnText(), true);
        if ("R".equals(status)) {
            goRepay(homePageInfo.getCardRepayList(), true);
            return;
        }
        /*
         * 枚举：、、、额度获取失败-立即刷新、、有还款逾期-去还款、额度被冻结有还款逾期-去还款）
         */
        switch (status) {
            case "A":
            case "F":
                if (AppApplication.enableCredit) {
                    goRealPage(oldStatus, "");
                } else {
                    checkEDJHProgress();
                }
                break;
            case "D":
                RiskInfoUtils.postGioData(mActivity, "START_CASH");
                if (CheckUtil.isEmpty(minAmt) && CheckUtil.isEmpty(homePageInfo.getThirdTitleUMeng())) {
                    mActivity.showProgress(true);
                    Map<String, String> map = new HashMap<>();
                    map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));
                    //map.put("typGrp", "02");
                    map.put("speSeq", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_EDU_SPE_SEQ)));
                    netHelper.postService(ApiUrl.URL_GET_STANDARD_PRODUCT_INFO, map);
                    return;
                }
                if (!CheckUtil.isEmpty(homePageInfo) && !CheckUtil.isEmpty(homePageInfo.getThirdTitleUMeng())) {
                    mActivity.showProgress(false);
                    postCompleteEvent("true", "无");
                    if (AppApplication.enableLoan) {
                        goH5LoanPage();
                    } else {
                        mActivity.startActivity(new Intent(mActivity, GoBorrowMoneyActivity.class));
                    }
                } else if (Double.parseDouble(homePageInfo.getAvailLimit() == null ? "0" : homePageInfo.getAvailLimit()) >= Double.parseDouble(minAmt)) {
                    startBorrow();
                } else {
                    mActivity.showDialog("对不起，您的剩余额度低于最低借款金额" + minAmt + "元，建议您在额度恢复后再借款");
                    postCompleteEvent("false", "对不起，您的剩余额度低于最低借款金额" + minAmt + "元，建议您在额度恢复后再借款");
                }
                break;
            case "E":
                mActivity.showDialog("经系统评估，您的信用评分过低，当前账户被临时限制用信，请保持正常还款，防止额度被冻结。");
                break;

            case "G":
                if (AppApplication.enableCredit) {
                    goRealPage(oldStatus, "");
                } else {
                    onRetreated(mActivity, homePageInfo);
                }
                break;
            //”重新申请”
            case "H":
                if (AppApplication.enableCredit) {
                    goRealPage(oldStatus, credit.getApplSeq());
                } else {
                    checkHStateProgress(credit.getApplSeq());
                }
                break;

        }
    }

    //还款统一入口,isHomeCard区分兜底是否是额度卡片的点击
    public void goRepay(List<HomeRepayBean> repayList) {
        goRepay(repayList, false);
    }

    public void goRepay(List<HomeRepayBean> repayList, boolean isHomeCard) {
        if (!CheckUtil.isEmpty(repayList)) {
            if (repayList.size() == 1) {
                Intent intent = new Intent(mActivity, JsWebBaseActivity.class);
                intent.putExtra("jumpKey", repayList.get(0).getJumpH5Url());
                intent.putExtra("isHideCloseIcon", true);
                mActivity.startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.putExtra(RepayListDialogActivity.REPAY_DATA, (Serializable) repayList);
                RepayListDialogActivity.startDialogActivity(mActivity, RepayListDialogActivity.class, RepayListDialogActivity.ANIM_BOTTOM_IN_RIGHT_OUT, intent);
            }
        } else {
            H5LinkJumpHelper.INSTANCE().goH5RepayPage(mActivity, isHomeCard);
        }

    }

    public void startBorrow() {
        if (!CheckUtil.isEmpty(homePageInfo) && !CheckUtil.isEmpty(homePageInfo.getThirdTitleUMeng())) {
            mActivity.showProgress(false);
            goH5LoanPage();
            return;
        }
        mActivity.showProgress(true);
        new GhLocation(mActivity, true, (isSuccess, reason) -> {
            if (isSuccess) {
                startBorrowForOrder();
            } else {
                mActivity.showProgress(false);
                if (!mActivity.isShowingDialog()) {
                    mActivity.showDialog(CheckUtil.isEmpty(reason) ? "定位失败,请稍后重试" : reason);
                }
                postCompleteEvent("false", CheckUtil.isEmpty(reason) ? "定位失败,请稍后重试" : reason);
            }
        }).requestLocation();
    }

    //首页按钮曝光
     /*
      * A：无额度、无额度申请记录、未登录时的默认状态
     * B：额度审批中
     * C：额度变更中
     * D：额度正常
     * E：额度冻结
     * F：额度无效
     * G：额度审批退回
     * H：额度拒绝
     * R：[A~H]3均转换为R3，代表存在逾期
         * "额度状态-当前按钮名称
    （枚举：未登录-立即登录、
    * 激活-立即申请、
    * 激活-继续申请、
    * 加载-加载中、
    * 获取失败-立即刷新、
    * 正常-去借款、
    * 审批中-无按钮、
    * 被退回-修改提交、
    * 被拒-无按钮、
    * 失效-重新申请、
    * 变更中-无按钮，
    * 逾期-去还款）"
         */
    public void postHomePageButtonEvent(String id, String buttonName) {
        postHomePageButtonEvent(id, buttonName, false);
    }

    public void postHomePageButtonEvent(String id, String buttonName, boolean isNeedStatus) {
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "够花-首页");
        if (!CheckUtil.isEmpty(homePageInfo) && !TextUtils.isEmpty(homePageInfo.getThirdTitleUMeng())) {
            map.put("fund_providers_name", homePageInfo.getThirdTitleUMeng());
            map.put("is_assistance", "true");
        } else {
            map.put("is_assistance", "false");
        }
        String buttonValue = "";
        if ("R".equals(status)) {
            map.put("overdue_flag", "true");
            buttonValue = "逾期-去还款";

        } else {
            map.put("overdue_flag", "false");
            if (isNeedStatus) {
                switch (status) {
                    case "A":
                        buttonValue = "激活-" + buttonName;
                        break;
                    case "B":
                        buttonValue = "审批中-无按钮";
                        break;
                    case "C":
                        buttonValue = "变更中-无按钮";
                        break;
                    case "D":
                        buttonValue = "正常-" + buttonName;
                        break;
                    case "E":
                        buttonValue = "额度被冻结-" + buttonName;
                        break;
                    case "F":
                        buttonValue = "失效-" + buttonName;
                        break;
                    case "G":
                        buttonValue = "被退回-" + buttonName;
                        break;
                    //”重新申请”
                    case "H":
                        buttonValue = "被拒-无按钮";
                        break;
                    default:
                        break;

                }
            }
        }
        map.put("button_name", CheckUtil.isEmpty(buttonValue) ? buttonName : buttonValue);
        map.put("page_name_cn", "够花-首页");
        UMengUtil.onEventObject(id, map, "HomePage");
    }

    public void postHomePageButtonForErrorEvent(String id, String buttonName, boolean isNeedStatus, String failReason) {
        postHomePageButtonForErrorEvent(id, buttonName, isNeedStatus, failReason, "");
    }

    public void postHomePageButtonForErrorEvent(String id, String buttonName, boolean isNeedStatus, String failReason, String thirdName) {
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "够花-首页");
        if (!TextUtils.isEmpty(failReason)) {
            map.put("failReason", failReason);
        }
        if (!TextUtils.isEmpty(thirdName)) {
            map.put("fund_providers_name", thirdName);
            map.put("is_assistance", "true");
        } else {
            map.put("is_assistance", "false");
        }
        String buttonValue = "";
        if ("R".equals(status)) {
            map.put("overdue_flag", "true");
            buttonValue = "逾期-去还款";

        } else {
            map.put("overdue_flag", "false");
            if (isNeedStatus) {
                switch (status) {
                    case "A":
                        buttonValue = "激活-" + buttonName;
                        break;
                    case "B":
                        buttonValue = "审批中-无按钮";
                        break;
                    case "C":
                        buttonValue = "变更中-无按钮";
                        break;
                    case "D":
                        buttonValue = "正常-" + buttonName;
                        break;
                    case "E":
                        buttonValue = "额度被冻结-" + buttonName;
                        break;
                    case "F":
                        buttonValue = "失效-" + buttonName;
                        break;
                    case "G":
                        buttonValue = "被退回-" + buttonName;
                        break;
                    //”重新申请”
                    case "H":
                        buttonValue = "被拒-无按钮";
                        break;
                    default:
                        break;

                }
            }
        }
        map.put("button_name", CheckUtil.isEmpty(buttonValue) ? buttonName : buttonValue);
        map.put("page_name_cn", "够花-首页");
        UMengUtil.onEventObject(id, map, "HomePage");
    }


    //状态为A1、A2、F1、F2   H1、H2   G1、G2
    private void goRealPage(String state, String applSeq) {
        if (!CheckUtil.isEmpty(H5ConfigHelper.h5SceneCreditUrl)) {
            goH5Page(H5ConfigHelper.h5SceneCreditUrl);
        } else if (!CheckUtil.isEmpty(H5ConfigHelper.h5CreditUrl)) {
            goH5Page(H5ConfigHelper.h5CreditUrl);
        } else {
            mActivity.showProgress(true);
            H5ConfigHelper helper = new H5ConfigHelper(oldStatus, applSeq, new H5ConfigHelper.IConfigCallback() {
                @Override
                public void configSuccess() {
                    mActivity.showProgress(false);
                    if (!CheckUtil.isEmpty(H5ConfigHelper.h5SceneCreditUrl)) {
                        goH5Page(H5ConfigHelper.h5SceneCreditUrl);
                    } else if (!CheckUtil.isEmpty(H5ConfigHelper.h5CreditUrl)) {
                        goH5Page(H5ConfigHelper.h5CreditUrl);
                    } else {
                        configFail();
                    }
                }

                //再次调用失败的话走原生
                @Override
                public void configFail() {
                    mActivity.showProgress(false);
                    switch (status) {
                        case "A":
                        case "F":
                            checkEDJHProgress();
                            break;
                        case "G":
                            onRetreated(mActivity, homePageInfo);
                            break;
                        case "H":
                            checkHStateProgress(applSeq);
                            break;
                    }
                }
            });
            helper.getH5LinkData();
        }
    }

    //检查额度被拒之后具体流程
    private void checkHStateProgress(String applSeq) {
        Map<String, String> map = new HashMap<>();
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));//客户号
        map.put("applSeq", RSAUtils.encryptByRSA(applSeq));
        netHelper.postService(ApiUrl.URL_GET_CREDIT_CHECK_INFO, map);
    }

    /**
     * 检查额度激活流程
     */
    private void checkEDJHProgress() {
        EduProgressHelper.getInstance().checkProgress(mActivity, false);
    }

    /**
     * 申请被退回
     */
    public static void onRetreated(BaseActivity activity, Credit mBean) {
        if (mBean == null) {
            activity.showDialog("获取数据异常，请稍后重试");
            return;
        }
        List<Retreated> list = JsonUtils.fromJsonArray(mBean, Retreated.class);
        //多个原因或没有原因则按正常的退回流程处理
        if (list == null || list.isEmpty() || list.size() > 1) {
            EduProgressHelper.getInstance().checkProgress(activity, false);
        } else {
            Retreated retreated = list.get(0);
            switch (retreated.getErrorFlag()) {
                // showRetreatedDialog(activity.getResources().getString(R.string.retreat_unconnected), "再次提交", null);
                case "002":
                    Intent intent = new Intent(activity, FaceRecognitionActivity.class);
                    intent.putExtra("tag", "EDJH");
                    activity.startActivity(intent);
                    //showRetreatedDialog(activity.getResources().getString(R.string.retreat_face), "继续申请", FaceRecognitionActivity.class);
                    break;
                case "003":
                    Intent intent1 = new Intent(activity, NameAuthIdCardPatchActivity.class);
                    intent1.putExtra("tag", "EDJH");
                    activity.startActivity(intent1);
                    //showRetreatedDialog(activity.getResources().getString(R.string.retreat_idcard), "继续申请", NameAuthIdCardPatchActivity.class);
                    break;
                default:
                    EduProgressHelper.getInstance().checkProgress(activity, false);
                    break;
            }
        }
    }

    /**
     * 退回流程
     */
    private void showRetreatedDialog(String msg, String button, final Class className) {
        mActivity.showDialog(null, msg, button, null, (dialog, which) -> {
            SpHp.saveSpLogin(SpKey.LOGIN_STATUS, String.valueOf(EduProgressHelper.NORMAL_PROGRESS));
            if (className == null) {
                checkEDJHProgress();
            } else {
                Intent intent = new Intent(mActivity, className);
                intent.putExtra("tag", "EDJH");
                mActivity.startActivity(intent);
            }
        }).setButtonTextColor(1, R.color.colorPrimary).setCancelFlag(true);
    }

    //跳转到h5页面
    private void goH5Page(String url) {
        mActivity.showProgress(false);
        Intent intent = new Intent(mActivity, JsWebBaseActivity.class);
        intent.putExtra("jumpKey", url);
        intent.putExtra("isHideCloseIcon", true);
        mActivity.startActivity(intent);
    }

    /******************************************接口响应*************************************************/
    @Override
    public void onSuccess(Object t, String url) {
        mActivity.showProgress(false);
        if (ApiUrl.urlOrder.equals(url)) {
            if (mActivity instanceof JsWebBaseActivity) {
                mActivity.finish();
            }
            postCompleteEvent("true", "无");
            if (AppApplication.enableLoan) {
                goH5LoanPage();
            } else {
                mActivity.startActivity(new Intent(mActivity, GoBorrowMoneyActivity.class));
            }
        } else if (ApiUrl.URL_GET_STANDARD_PRODUCT_INFO.equals(url)) {
            List<LoanRatAndProduct> loanCodeBeanList = JsonUtils.fromJsonArray(t, "tnrOptList", LoanRatAndProduct.class);
            if (CheckUtil.isEmpty(loanCodeBeanList)) {
                mActivity.showDialog("没有匹配到可选择的贷款产品");
                postCompleteEvent("false", "没有匹配到可选择的贷款产品");
                return;
            }
            String[] codeMin = MainHelper.getCodeAndMin(loanCodeBeanList);
            setMinAmtAndTypeLevelTwo(codeMin[0], codeMin[1], codeMin[2]);
            startEvent(homePageInfo, null);
        } else if (ApiUrl.URL_GET_CREDIT_CHECK_INFO.equals(url)) {
            CheckForApplyCreditBean checkBean = JsonUtils.fromJson(t, CheckForApplyCreditBean.class);
            if (checkBean == null) return;
            if ("Y".equals(checkBean.allowApply)) { //如果allowApply =Y，则进入额度申请流程。走A1、A2、F1、F2目前走的流程。
                checkEDJHProgress();
            } else {//被拒
                Intent intent = new Intent(mActivity, EduProgressActivity.class);
                intent.putExtra("Result", "因综合评分不足，您未能通过额度审批");
                intent.putExtra("allowDays", checkBean.getAllowDays());
                intent.putExtra("allowDate", checkBean.getAllowDate());
                mActivity.startActivity(intent);
            }
        }
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
            String state = homePageInfo.getStatus();
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

    @Override
    public void onError(BasicResponse error, String url) {
        mActivity.showProgress(false);
        if (ApiUrl.urlOrder.equals(url) || ApiUrl.URL_GET_STANDARD_PRODUCT_INFO.equals(url)) {
            postCompleteEvent("false", error.getHead().getRetMsg());
            mActivity.showDialog(error.getHead().getRetMsg());
        } else if (ApiUrl.URL_GET_CREDIT_CHECK_INFO.equals(url)) {
            mActivity.showDialog(error.getHead().getRetMsg());
        } else {
            mActivity.onError(error, url);
        }
    }

    /******************************************定位响应*************************************************/
    private void startBorrowForOrder() {
        mActivity.showProgress(false);
        // 定位成功 则进行 录单校验
        String cityCode = SpHelper.getInstance().readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_CITYCODE);//市代码
        String provinceCode = SpHelper.getInstance().readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_PROVINCECODE);//省代码
        if (CheckUtil.isEmpty(typeLevelTwo)) {
            mActivity.showProgress(false);
            UiUtil.toastDeBug("贷款品种小类 [typeLevelTwo] 为空");
            postCompleteEvent("false", "贷款品种小类 [typeLevelTwo] 为空");
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
     * 手动断开连接
     */
    protected void onDestory() {
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
        mActivity = null;
        EduProgressHelper.getInstance().onDestroy();
    }

    public void destroy() {
        instance = null;
    }

    private void postCompleteEvent(String success, String failReason) {
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "够花-首页");
        UMengUtil.commonCompleteEvent("HomePageQuotaButton_Click", map, success, failReason, "HomePage");
    }

    public void setCreditInfo(Credit credit) {
        this.homePageInfo = credit;
    }
}
