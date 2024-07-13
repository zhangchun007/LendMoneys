package com.haiercash.gouhua.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.adaptor.LoanAssociationAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseListFragment;
import com.haiercash.gouhua.beans.LoanBillsBean;
import com.haiercash.gouhua.bill.BillDetailsActivity;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.BankCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/6/30<br/>
 * 描    述：关联贷款<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_LOAN_ASSOCIATION)
public class LoanAssociationFragment extends BaseListFragment {
    @BindView(R.id.bcv_view)
    BankCardView bcView;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.btn_next)
    TextView btnNext;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.cb_agree)
    CheckBox cbAgree;
    private String cardNo, bankName, bankCode;
    private ArrayList<String> loanNos;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_loan_association;
    }

    @Override
    public void initEventAndData() {
        super.initEventAndData();
        mActivity.setTitle("关联贷款");
        loanNos = new ArrayList<>();
        setRefreshEnable(false);
        mRefreshHelper.setEmptyData(R.drawable.src_loan_association_empty, "暂无可关联的贷款");
        mRefreshHelper.getEmptyOrErrorView().setTextSize(12);
        mRefreshHelper.setEmptyViewLineGone(true);
        tv1.setVisibility(View.GONE);
        tv2.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);

        mAdapter.addChildClickViewIds(R.id.cb, R.id.rl_content);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            LoanBillsBean bean = (LoanBillsBean) adapter.getData().get(position);
            if (view.getId() == R.id.cb) {
                bean.setChoose(!bean.isChoose());
                adapter.notifyItemChanged(position);
                if (bean.isChoose()) {
                    loanNos.add(bean.getLoanNo());
                } else {
                    loanNos.remove(bean.getLoanNo());
                }
                onCheckChange(cbAgree.isChecked());
            } else {
                Intent intent = new Intent(mActivity, BillDetailsActivity.class);
                intent.putExtra("loanNo", bean.getLoanNo());
                startActivity(intent);
            }
        });
        if (getArguments() == null) {
            mActivity.finish();
            return;
        }
        bankCode = getArguments().getString("bankCode");
        bankName = getArguments().getString("bankName");
        cardNo = getArguments().getString("cardNo");
        String cardTypeName = getArguments().getString("cardTypeName");
        bcView.updateView(bankName, cardNo, cardTypeName);
        bcView.initStation("Y", "SIGN_SUCCESS", true);

        SpannableStringBuilder builder = SpannableStringUtils.getBuilder(mActivity, "我已阅读并同意")
                .setForegroundColor(Color.parseColor("#666666"))
                .append("《银行卡扣款授权书》").setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        String url = ApiUrl.urlCardContracts + "?custNo=" + SpHp.getUser(SpKey.USER_CUSTNO) +
                                "&cardNo=" + cardNo + "&Authorization=" + "Bearer" + SpHp.getLogin(SpKey.LOGIN_ACCESSTOKEN)
                                + "&channel=18&channel_no=42" + "&access_token=" + SpHp.getLogin(SpKey.LOGIN_ACCESSTOKEN) + "&loanNo=" + EncryptUtil.encrypt(getLoanNums())
                                + "&custName=" + EncryptUtil.encrypt(SpHp.getUser(SpKey.USER_CUSTNAME)) + "&bankName=" + EncryptUtil.encrypt(bankName) + "&bankCardNo=" + cardNo + "&idCardNo=" + SpHp.getUser(SpKey.USER_CERTNO);
                        WebSimpleFragment.WebService(mActivity, url, "银行卡扣款授权书", WebSimpleFragment.STYLE_OTHERS);
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(UiUtil.getColor(R.color.colorPrimary));
                        ds.setUnderlineText(false);
                        ds.clearShadowLayer();
                    }
                }).append("，确认以上贷款还款卡变更为" + bankName + "(" + cardNo.substring(cardNo.length() - 4) + ")")
                .setForegroundColor(Color.parseColor("#666666"))
                .create();
        cbAgree.setText(builder);
        cbAgree.setMovementMethod(LinkMovementMethod.getInstance());
        cbAgree.setHighlightColor(Color.TRANSPARENT);

        showProgress(true);
        mRefreshHelper.build(true, false);
    }

    /**
     * 获取借据号
     *
     * @return
     */
    private String getLoanNums() {
        StringBuilder builder = new StringBuilder();
        if (loanNos != null && loanNos.size() > 0) {
            for (int i = 0; i < loanNos.size(); i++) {
                if (i != loanNos.size() - 1) {
                    builder.append(loanNos.get(i));
                    builder.append("、");
                } else {
                    builder.append(loanNos.get(i));
                }
            }
            return builder.toString();
        } else {
            return "";
        }

    }

    @Override
    public void loadSourceData(int page, int pageSize) {
        onCheckChange(cbAgree.isChecked());
        mRefreshHelper.updateData(null);
        Map<String, String> map = new HashMap<>();
        map.put("custNo", SpHp.getUser(SpKey.USER_CUSTNO));
        map.put("cardNo", EncryptUtil.simpleEncrypt(cardNo));
        netHelper.postService(ApiUrl.URL_POST_LOAN_BILL_CHANGE_CARD, map, HashMap.class, true);
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new LoanAssociationAdapter();
    }

    @OnClick(R.id.btn_next)
    public void viewOnClick() {
        Map<String, Object> map = new HashMap<>();
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));
        map.put("custName", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNAME)));
        map.put("certNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CERTNO)));
        map.put("cardNo", RSAUtils.encryptByRSA(cardNo));
        map.put("bankCode", RSAUtils.encryptByRSA(bankCode));
        map.put("bankName", RSAUtils.encryptByRSA(bankName));
        map.put("loanNoList", loanNos);
        map.put("isRsa", "Y");
        netHelper.postService(ApiUrl.URL_POST_BATCH_CHANGE_CARD, map);
    }

    @OnCheckedChanged(R.id.cb_agree)
    void onCheckChange(boolean isChecked) {
        if (isChecked && !CheckUtil.isEmpty(loanNos)) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }

    @Override
    public void onSuccess(Object t, String url) {
        if (ApiUrl.URL_POST_LOAN_BILL_CHANGE_CARD.equals(url)) {
            List<LoanBillsBean> list = JsonUtils.fromJsonArray(t, "loanBills", LoanBillsBean.class);
            if (loanNos != null) {
                loanNos.clear();
            }
            for (LoanBillsBean bean : list) {
                if ("N".equals(bean.getCardChangeFlag())) {
                    loanNos.add(bean.getLoanNo());
                }
            }
            mRefreshHelper.updateData(list);
            tv1.setVisibility(CheckUtil.isEmpty(loanNos) ? View.GONE : View.VISIBLE);
            tv2.setVisibility(CheckUtil.isEmpty(loanNos) ? View.GONE : View.VISIBLE);
            llBottom.setVisibility(CheckUtil.isEmpty(loanNos) ? View.GONE : View.VISIBLE);
            showProgress(false);
        } else if (ApiUrl.URL_POST_BATCH_CHANGE_CARD.equals(url)) {
            UiUtil.toast("贷款关联成功");
            cbAgree.setChecked(false);
            showProgress(true);
            loadSourceData(1, 1);
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.URL_POST_LOAN_BILL_CHANGE_CARD.equals(url)) {
            tv1.setVisibility(View.GONE);
            tv2.setVisibility(View.GONE);
            llBottom.setVisibility(View.GONE);
        } else if (ApiUrl.URL_POST_BATCH_CHANGE_CARD.equals(url)) {
            showProgress(true);
            loadSourceData(1, 1);
        }
        super.onError(error, url);
    }
}
