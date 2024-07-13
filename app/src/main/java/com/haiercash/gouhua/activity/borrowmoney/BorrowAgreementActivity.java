package com.haiercash.gouhua.activity.borrowmoney;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager.widget.ViewPager;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.google.android.material.tabs.TabLayout;
import com.haiercash.gouhua.activity.comm.AgreementUiHelper;
import com.haiercash.gouhua.adaptor.WebPageAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.SaveOrderBean;
import com.haiercash.gouhua.beans.agreement.SmyAgreementBean;
import com.haiercash.gouhua.beans.bankcard.QueryCardBean;
import com.haiercash.gouhua.beans.repayment.SignBankCardNeed;
import com.haiercash.gouhua.databinding.ActivityBorrowAgreementBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.repayment.SignBankCardActivity;
import com.haiercash.gouhua.repayment.SignBankCardHelper;
import com.haiercash.gouhua.uihelper.AgreementsPop;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.x5webview.CusWebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 借款协议
 */
public class BorrowAgreementActivity extends BaseActivity {
    private SignBankCardHelper signBankCardHelper;
    private AgreementUiHelper agreementUiHelper;
    /**
     * tag标识<br/>
     * XGJYMM:修改交易密码<br/>
     * EDJH:额度激活<br/>
     * XJD ：现金贷<br/>
     */
    private String getTag;
    private String applSeq;//流水号
    private String mBankCardNo;//银行卡号
    private final List<SmyAgreementBean.AgreementBean> loanLinksBeans = new ArrayList<>();
    private ActivityBorrowAgreementBinding agreementBinding;
    private CusWebView creditCwv, contractCwv;
    private AgreementsPop agreementsPop;
    private String token;
    private String cusNo;
    private String certNo;
    List<CusWebView> webViewList = new ArrayList<>();//webview列表
    private boolean showAgreementDialog, requestSuccess;

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        agreementBinding = ActivityBorrowAgreementBinding.inflate(inflater, null, false);
        return agreementBinding;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("相关协议");
        signBankCardHelper = new SignBankCardHelper(this);
        getTag = getIntent().getStringExtra("tag");
        mBankCardNo = getIntent().getStringExtra("bankCardNo");
        applSeq = getIntent().getStringExtra("applSeq");
        initTabAndPager();
        agreementUiHelper = new AgreementUiHelper(agreementBinding.vBottom.vBottom, agreementBinding.vBottom.btContract, agreementBinding.flContent, creditCwv, contractCwv);
        initAgreement();
        setonClickByView(agreementBinding.vBottom.btContract);
        requestAgreements();
    }

    private void requestAgreements() {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("scene", "loan");
        netHelper.postService(ApiUrl.POST_AGREEMENTS_INFO_URL, map, SmyAgreementBean.class);
    }

    private String getCreditAgreementTitle() {
        return "征信查询授权协议";
    }

    private String getBorrowAgreementTitle() {
        return "个人借款合同";
    }

    private void updateSelectSize(TabLayout.Tab tab, boolean select) {
        if (tab == null) {
            return;
        }
        CharSequence text = tab.getText();
        if (CheckUtil.isEmpty(text)) {
            return;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        spannableStringBuilder.setSpan(new AbsoluteSizeSpan((int) UiUtil.sp2px(this, select ? 16 : 15)), 0, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tab.setText(spannableStringBuilder);
    }

    private void initTabAndPager() {
        TabLayout.Tab tab0 = agreementBinding.tlAgreement.newTab().setText(getCreditAgreementTitle());
        tab0.select();
        updateSelectSize(tab0, true);
        agreementBinding.tlAgreement.addTab(tab0);
        agreementBinding.tlAgreement.addTab(agreementBinding.tlAgreement.newTab().setText(getBorrowAgreementTitle()));
        agreementBinding.tlAgreement.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(agreementBinding.vpAgreement) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                updateSelectSize(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                updateSelectSize(tab, false);
            }
        });
        creditCwv = new CusWebView(this);
//        creditCwv.getSettings().setTextZoom(75);//因为征信授权协议H5里面字体太大了，所以缩放到75%
        contractCwv = new CusWebView(this);
        webViewList.add(creditCwv);
        webViewList.add(contractCwv);
        agreementBinding.vpAgreement.setAdapter(new WebPageAdapter(webViewList));
        agreementBinding.vpAgreement.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //防止重复套娃
                if (agreementBinding.tlAgreement.getSelectedTabPosition() != position) {
                    TabLayout.Tab tabAt = agreementBinding.tlAgreement.getTabAt(position);
                    if (tabAt != null) {
                        tabAt.select();
                    }
                }
            }
        });
    }

    private void initAgreement() {
        certNo = SpHp.getUser(SpKey.USER_CERTNO);
        creditCwv.loadUrl(ApiUrl.API_SERVER_URL + ApiUrl.url_edCredit
                + "?custName=" + EncryptUtil.encrypt(SpHp.getUser(SpKey.USER_CUSTNAME)) + "&certNo=" + certNo);
        SaveOrderBean.UniteLoanInfoBean uniteLoanInfo = (SaveOrderBean.UniteLoanInfoBean) getIntent().getSerializableExtra("uniteLoanInfoDate");
        SaveOrderBean.LinksBean linksBean = (SaveOrderBean.LinksBean) getIntent().getSerializableExtra("forcePreviewInfo");
        if (linksBean != null && linksBean.getLink() != null && linksBean.getName() != null) {
            CusWebView webView = new CusWebView(this);
            agreementBinding.tlAgreement.addTab(agreementBinding.tlAgreement.newTab().setText(linksBean.getName()));
            webView.loadUrl(ApiUrl.API_SERVER_URL + linksBean.getLink() + "&custName=" + SpHp.getUser(SpKey.USER_CUSTNAME) + "&certNo=" + certNo);
            webViewList.add(webView);
            agreementBinding.tlAgreement.setTabMode(TabLayout.MODE_SCROLLABLE);
            agreementBinding.vpAgreement.setAdapter(new WebPageAdapter(webViewList));
        } else {
            agreementBinding.tlAgreement.setTabMode(TabLayout.MODE_FIXED);
        }
        token = TokenHelper.getInstance().getCacheToken();
        cusNo = SpHp.getUser(SpKey.USER_CUSTNO);
        if (CheckUtil.isEmpty(uniteLoanInfo) || !"1".equals(uniteLoanInfo.getIsUniteLoan())
                || !addLoanLinksBeans(uniteLoanInfo.getLinks())) {
            //条件addLoanLinksBeans里处理联合放款，下面处理非联合放款
            contractCwv.loadUrl(ApiUrl.urlContractUri + "?applseq=" + applSeq + "&custNo=" + cusNo + "&access_token=" + token);
        }
        loanLinksBeans.add(new SmyAgreementBean.AgreementBean("电子认证服务协议", ApiUrl.URL_AGREEMENT_URL_4));
        SpannableStringUtils.Builder builder = SpannableStringUtils.getBuilder(this, "请仔细阅读");
        builder.append(SpannableStringUtils.getBuilder(this, getCreditAgreementTitle())
                .setClickSpan(new AgreementUiHelper.AgreementClickSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {//点击切换tab(CreditAgreement)
                        agreementBinding.vpAgreement.setCurrentItem(0, true);
                    }
                }).create());
        builder.append("、");
        builder.append(SpannableStringUtils.getBuilder(this, getBorrowAgreementTitle())
                .setClickSpan(new AgreementUiHelper.AgreementClickSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {//点击切换tab(BorrowAgreement)
                        agreementBinding.vpAgreement.setCurrentItem(1, true);
                    }
                }).create());
        if (linksBean != null && linksBean.getLink() != null && linksBean.getName() != null) {
            builder.append("、");
            builder.append(SpannableStringUtils.getBuilder(this, linksBean.getName())
                    .setClickSpan(new AgreementUiHelper.AgreementClickSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {//点击切换tab(BorrowAgreement)
                            agreementBinding.vpAgreement.setCurrentItem(2, true);
                        }
                    }).create());
        }
        builder.append("，您也可点击查看");
        builder.append(SpannableStringUtils.getBuilder(this, "其他相关协议")
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
        builder.append("；点击阅读并同意，则表示同意以上所有协议");
        agreementBinding.vBottom.tvAgreement.setText(builder.create());
        agreementBinding.vBottom.tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        agreementBinding.vBottom.tvAgreement.setHighlightColor(Color.TRANSPARENT);
    }

    private void initAgreement1(ArrayList<SmyAgreementBean.AgreementBean> smyAgreementInfo) {
        //萨摩耶协议
        if (!CheckUtil.isEmpty(smyAgreementInfo)) {
            String token = TokenHelper.getInstance().getCacheToken();
            Map<String, String> map = new HashMap<>();
            map.put("orderNo", applSeq);
            map.put("applseq", applSeq);
            map.put("token", token);
            map.put("accessToken", token);
            map.put("channelNo", TokenHelper.getInstance().getSmyParameter("channelNo"));
            for (SmyAgreementBean.AgreementBean agreementBean : smyAgreementInfo) {
                agreementBean.setLink(WebHelper.addUrlParam(WebHelper.formatMyUrlPath(agreementBean.getLink()), map));
            }
            loanLinksBeans.addAll(smyAgreementInfo);
        }
    }

    private void showAgreementPop() {
        if (agreementsPop == null) {
            agreementsPop = new AgreementsPop(this, loanLinksBeans);
        }
        agreementsPop.showAtLocation(agreementBinding.vBottom.vBottom);
    }

    private boolean addLoanLinksBeans(List<SaveOrderBean.LinksBean> links) {
        if (links == null) {
            return false;
        }
        boolean hasContract = false;
        SaveOrderBean.LinksBean bean;
        if (links.size() >= 1) {
            for (int i = 0; i < links.size(); i++) {
                bean = links.get(i);
                if ("contract".equals(bean.getType())) {
                    contractCwv.loadUrl(ApiUrl.API_SERVER_URL + bean.getLink());
                    hasContract = true;
                } else {
                    loanLinksBeans.add(new SmyAgreementBean.AgreementBean(bean.getName(), bean.getLink() + "&custName=" + SpHp.getUser(SpKey.USER_CUSTNAME) + "&certNo=" + certNo));
                }
            }
        }
        /*if (links.size() >= 2) {
            bean = links.get(1);
            if ("contract".equals(bean.getType())) {
                contractCwv.loadUrl(ApiUrl.API_SERVER_URL + bean.getLink());
                hasContract = true;
            } else {
                loanLinksBeans.add(new SmyAgreementBean.AgreementBean(bean.getName(), bean.getLink()));
            }
        }*/
        return hasContract;
    }

    @Override
    protected String getPageCode() {
        return "XJD".equals(getTag) ? "PersonLoanContractPage" : super.getPageCode();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == agreementBinding.vBottom.btContract) {
            UMengUtil.commonClickEvent("ReadAgree_Click_gouhua", agreementBinding.vBottom.btContract.getText().toString(), getPageCode());
            showProgress(true);
            signBankCardHelper.requestNeedSign(mBankCardNo, 2);
        }
    }

    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        showProgress(false);
        if (url.equals(ApiUrl.URL_QUERY_NEED_SIGN)) {
            SignBankCardNeed bean = (SignBankCardNeed) success;
            if (bean.needSign()) {
                SignBankCardActivity.goSignBankCard(this, (QueryCardBean) getIntent().getSerializableExtra("bankcard"), bean);
            } else {
                showBorrowPayPwdPage();
            }
        } else if (ApiUrl.POST_AGREEMENTS_INFO_URL.equals(url)) {
            requestSuccess = true;
            SmyAgreementBean mSmyAgreementBean = (SmyAgreementBean) success;
            initAgreement1(mSmyAgreementBean != null ? mSmyAgreementBean.getAgreementLinkList() : null);
            if (showAgreementDialog) {
                showAgreementPop();
            } else if (agreementUiHelper != null) {
                agreementUiHelper.startCounter();
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        if (ApiUrl.POST_AGREEMENTS_INFO_URL.equals(url)) {
            if (!showAgreementDialog && agreementUiHelper != null) {
                initAgreement1(null);
                agreementUiHelper.startCounter();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignBankCardActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                showBorrowPayPwdPage();
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getBooleanExtra("toUpdateBankCard", false)) {
                    //更换银行卡按钮返回借款页（结束当前页就行）
                    finish();
                } else if (data != null && data.getBooleanExtra("toPwdCheck", false)) {
                    //交易密码验证页面
                    showBorrowPayPwdPage();
                }
            }
        }
    }

    /**
     * 展示交易密码验证真正页面
     */
    private void showBorrowPayPwdPage() {
        Intent intent = new Intent(this, BorrowPayPwdActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (agreementUiHelper != null) {
            agreementUiHelper.destroy();
            agreementUiHelper = null;
        }
        if (creditCwv != null) {
            creditCwv.destroy();
            creditCwv = null;
        }
        if (contractCwv != null) {
            contractCwv.destroy();
            contractCwv = null;
        }
        if (agreementsPop != null) {
            agreementsPop.onDestroy();
            agreementsPop = null;
        }
        super.onDestroy();
    }
}