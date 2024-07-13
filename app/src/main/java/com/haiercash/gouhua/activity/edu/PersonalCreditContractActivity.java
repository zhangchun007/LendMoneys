package com.haiercash.gouhua.activity.edu;

import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.AgreementUiHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.agreement.SmyAgreementBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.AgreementsPop;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.x5webview.CusWebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class PersonalCreditContractActivity extends BaseActivity {
    @BindView(R.id.cu_web)
    CusWebView cuWebView;
    @BindView(R.id.v_bottom)
    View vBottom;
    @BindView(R.id.flContent)
    View flContent;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R.id.bt_contract)
    TextView btContract;
    private AgreementsPop agreementsPop;
    private AgreementUiHelper agreementUiHelper;
    private boolean showAgreementDialog, requestSuccess;
    private ArrayList<SmyAgreementBean.AgreementBean> agreementList;

    @Override
    protected int getLayout() {
        return R.layout.activity_personal_credit_contract;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("额度申请");
        setRightImage(R.drawable.iv_blue_details, v -> ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER).navigation());
        agreementUiHelper = new AgreementUiHelper(vBottom, btContract, flContent, cuWebView);
//        cuWebView.getSettings().setTextZoom(75);//因为征信授权协议H5里面字体太大了，所以缩放到75%
        initWebView();
        requestAgreements();
    }

    private void requestAgreements() {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("scene", "credit");
        netHelper.postService(ApiUrl.POST_AGREEMENTS_INFO_URL, map, SmyAgreementBean.class);
    }

    private void initWebView() {
        String custName = SpHp.getUser(SpKey.USER_CUSTNAME);
        String certNo = SpHp.getUser(SpKey.USER_CERTNO);
        String url = ApiUrl.API_SERVER_URL + ApiUrl.url_edCredit
                + "?custName=" + EncryptUtil.encrypt(custName) + "&certNo=" + certNo;
        cuWebView.loadUrl(url);
    }

    private void initAgreement(ArrayList<SmyAgreementBean.AgreementBean> smyAgreementInfo) {
        if (agreementList == null) {
            agreementList = new ArrayList<>();
        } else {
            agreementList.clear();
        }
        if (smyAgreementInfo != null) {
            agreementList.addAll(smyAgreementInfo);
        }
        Map<String, String> map = new HashMap<>();
        map.put("custName", SpHp.getUser(SpKey.USER_CUSTNAME));
        map.put("certNo", SpHp.getUser(SpKey.USER_CERTNO));
        map.put("identifyNo", SpHp.getUser(SpKey.USER_CERTNO));
        for (SmyAgreementBean.AgreementBean agreementBean : agreementList) {
            agreementBean.setLink(WebHelper.addUrlParam(WebHelper.formatMyUrlPath(agreementBean.getLink()), map));
        }
        agreementList.add(0, new SmyAgreementBean.AgreementBean("电子认证服务协议", ApiUrl.URL_AGREEMENT_URL_4));
        SpannableStringUtils.Builder builder = SpannableStringUtils.getBuilder(this, "请仔细阅读并确保已充分理解");
        builder.append(SpannableStringUtils.getBuilder(this, "征信查询授权协议")
                .setClickSpan(new AgreementUiHelper.AgreementClickSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        //点击使页面0.5秒内左右抖动两个来回
                        TranslateAnimation translateAnimation = new TranslateAnimation(
                                Animation.ABSOLUTE, -10, Animation.ABSOLUTE, 10,
                                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                        translateAnimation.setRepeatCount(1);
                        translateAnimation.setDuration(250);
                        cuWebView.startAnimation(translateAnimation);
                    }
                }).create())
                .append("内容，点击阅读并同意，将签订以上协议");
        builder.append("和").append(SpannableStringUtils.getBuilder(this, "其他相关协议")
                .setClickSpan(new AgreementUiHelper.AgreementClickSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        showAgreementDialog = true;
                        if (!requestSuccess) {
                            requestAgreements();
                        } else {
                            showAgreementPop();
                        }
                    }
                }).create());
        builder.append("。");
        tvAgreement.setText(builder.create());
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        tvAgreement.setHighlightColor(Color.TRANSPARENT);
    }

    private void showAgreementPop() {
        if (agreementsPop == null) {
            agreementsPop = new AgreementsPop(this, agreementList);
        }
        agreementsPop.showAtLocation(vBottom);
    }

    @Override
    @OnClick(R.id.bt_contract)
    public void onClick(View v) {
        if (v == btContract) {
            UMengUtil.commonClickEvent("CreditInvestigationConsent_Click", btContract.getText().toString(), getPageCode());
            checkEDJHProgress();
        }
    }

    /*检查额度激活流程，只有在正常额度激活时才走这里，因此肯定没有流水号*/
    private void checkEDJHProgress() {
        EduProgressHelper.getInstance().checkProgress(PersonalCreditContractActivity.this, true);
    }

    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        showProgress(false);
        if (ApiUrl.POST_AGREEMENTS_INFO_URL.equals(url)) {
            requestSuccess = true;
            SmyAgreementBean mSmyAgreementBean = (SmyAgreementBean) success;
            initAgreement(mSmyAgreementBean != null ? mSmyAgreementBean.getAgreementLinkList() : null);
            if (showAgreementDialog) {
                showAgreementPop();
            } else if (agreementUiHelper != null) {
                agreementUiHelper.startCounter();
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.POST_AGREEMENTS_INFO_URL.equals(url)) {
            showProgress(false);
            UiUtil.toast("服务器开小差了，请稍后再试");
            if (!showAgreementDialog && agreementUiHelper != null) {
                initAgreement(null);
                agreementUiHelper.startCounter();
            }
        } else {
            super.onError(error, url);
        }
    }

    @Override
    protected void onDestroy() {
        if (agreementUiHelper != null) {
            agreementUiHelper.destroy();
            agreementUiHelper = null;
        }
        if (cuWebView != null) {
            cuWebView.destroy();
            cuWebView = null;
        }
        if (agreementsPop != null) {
            agreementsPop.onDestroy();
            agreementsPop = null;
        }
        EduProgressHelper.getInstance().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        EduCommon.onBackPressed(this, "要授权一下", getPageCode(), "征信授权页面");
    }

    @Override
    protected String getPageCode() {
        return "CreditInvestigationPage";
    }
}
