package com.haiercash.gouhua.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.CreditLifeBorrowBean;
import com.haiercash.gouhua.uihelper.CreditLifeHelp;
import com.haiercash.gouhua.utils.UiUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: liuyaxun
 * Date :    2019/2/26
 * FileName: CreditApplyDetailFragment
 * Description: 信用生活--借钱--点击条目跳转
 */
public class CreditApplyDetailFragment extends BaseFragment {
    public static final int ID = CreditApplyDetailFragment.class.hashCode();
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_apply_detail)
    TextView tvTitleApplyDetail;
    @BindView(R.id.rl_title_mine)
    RelativeLayout rlTitleMine;
    @BindView(R.id.tv_ed_start)
    TextView tvEdStart;
    @BindView(R.id.ll_apply_money)
    LinearLayout llApplyMoney;
    @BindView(R.id.rl_mine_banner)
    RelativeLayout rlMineBanner;
    @BindView(R.id.tv_detail_rate)
    TextView tvDetailRate;
    @BindView(R.id.tv_detail_date)
    TextView tvDetailDate;
    @BindView(R.id.tv_detail_time)
    TextView tvDetailTime;
    @BindView(R.id.ll_mine_card)
    LinearLayout llMineCard;
    @BindView(R.id.tv_submit_detail_apply)
    TextView tvSubmitDetailApply;
    @BindView(R.id.tv_loanterms1)
    TextView tvLoanterms1;
    @BindView(R.id.tv_loan_data1)
    TextView tvLoanData1;
    @BindView(R.id.tv_other)
    TextView tvOther;
    @BindView(R.id.ll_loan_terms)
    LinearLayout llLoanTrems;
    @BindView(R.id.ll_child_loan_terms)
    LinearLayout llChildLoanTrems;
    @BindView(R.id.ll_loan_data)
    LinearLayout llLoanData;
    @BindView(R.id.ll_child_loan_data)
    LinearLayout llChildLoanData;
    @BindView(R.id.ll_other)
    LinearLayout llOther;
    @BindView(R.id.ll_child_other)
    LinearLayout llChildOther;
    @BindView(R.id.sv_apply)
    NestedScrollView svApply;
    @BindView(R.id.iv_back_white)
    ImageView ivBackWhite;
    @BindView(R.id.tv_title_apply_detail_white)
    TextView tvTitleApplyDetailWhite;
    @BindView(R.id.rl_title_white)
    RelativeLayout rlTitleWhite;
    @BindView(R.id.cb_loan_greement)
    CheckBox cbLoanGreement;
    @BindView(R.id.tv_old_loan_agreement)
    TextView tvOldLoanAgreement;
    private int titleWhiteHeight;

    private CreditLifeBorrowBean creditLifeBorrowBean;
    private CreditLifeHelp help;

    public static BaseFragment newInstance(Bundle extra) {
        final CreditApplyDetailFragment f = new CreditApplyDetailFragment();
        if (extra != null) {
            f.setArguments(extra);
        }
        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ctrdit_apply_detail;
    }

    @Override
    protected void initEventAndData() {
        mActivity.getTitleBarView().setVisibility(View.GONE);
        tvEdStart.setTypeface(FontCustom.getMediumFont(mActivity));
        Bundle mArguments = getArguments();
        if (mArguments != null) {
            creditLifeBorrowBean = (CreditLifeBorrowBean) mArguments.getSerializable("creditLifeBorrowBean");
            tvTitleApplyDetail.setText(creditLifeBorrowBean.getChannelName());
            tvTitleApplyDetailWhite.setText(creditLifeBorrowBean.getChannelName());
            tvEdStart.setText(creditLifeBorrowBean.getEduAmt());
            tvDetailRate.setText(creditLifeBorrowBean.getRateInterest());
            tvDetailDate.setText(creditLifeBorrowBean.getTimeLimit());
            tvDetailTime.setText(creditLifeBorrowBean.getLoanDate());
            if (!CheckUtil.isEmpty(creditLifeBorrowBean.getLoanTerms())) {
                if (creditLifeBorrowBean.getLoanTerms().contains("\n")) {
                    llChildLoanTrems.setVisibility(View.GONE);
                    String[] loanTerms = creditLifeBorrowBean.getLoanTerms().split("\\n");
                    for (String loanTerm : loanTerms) {
                        setDesView(llLoanTrems, loanTerm);
                    }
                } else {
                    llChildLoanTrems.setVisibility(View.VISIBLE);
                    tvLoanterms1.setText(creditLifeBorrowBean.getLoanTerms());
                }
            } else {
                llChildLoanTrems.setVisibility(View.GONE);
            }
            if (!CheckUtil.isEmpty(creditLifeBorrowBean.getApplyData())) {
                if (creditLifeBorrowBean.getApplyData().contains("\n")) {
                    llChildLoanData.setVisibility(View.GONE);
                    String[] applyDate = creditLifeBorrowBean.getApplyData().split("\\n");
                    for (String s : applyDate) {
                        setDesView(llLoanData, s);
                    }
                } else {
                    llChildLoanData.setVisibility(View.VISIBLE);
                    tvLoanData1.setText(creditLifeBorrowBean.getApplyData());
                }

            } else {
                llChildLoanData.setVisibility(View.GONE);
            }
            if (!CheckUtil.isEmpty(creditLifeBorrowBean.getOthers())) {
                if (creditLifeBorrowBean.getOthers().contains("\n")) {
                    llChildOther.setVisibility(View.GONE);
                    String[] others = creditLifeBorrowBean.getOthers().split("\\n");
                    for (String other : others) {
                        setDesView(llOther, other);
                    }
                } else {
                    llChildOther.setVisibility(View.VISIBLE);
                    tvOther.setText(creditLifeBorrowBean.getOthers());
                }

            } else {
                llChildOther.setVisibility(View.GONE);
            }
        } else {
            UiUtil.toast("账号异常，请退出重试");
            mActivity.finish();
            return;
        }


        svApply.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                titleWhiteHeight = rlTitleWhite.getHeight() - 160;
                svApply.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        svApply.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY <= 0) {
                //未滑动
                tvTitleApplyDetailWhite.setAlpha(0);
                ivBackWhite.setAlpha(0f);
                tvTitleApplyDetail.setAlpha(1);
                ivBack.setAlpha(1.0f);
                rlTitleWhite.setAlpha(0);
                rlTitleMine.setAlpha(1);
                rlTitleWhite.setBackgroundColor(Color.argb(0, 255, 255, 255));
            } else if (scrollY <= titleWhiteHeight) {
                //滑动中，在bannerHeight之内
                float alpha = ((float) scrollY / titleWhiteHeight);
                if (alpha > 0.45) {
                    tvTitleApplyDetail.setAlpha(0);
                    ivBack.setAlpha(0f);
                    tvTitleApplyDetailWhite.setAlpha(1);
                    ivBackWhite.setAlpha(1.0f);
                    rlTitleWhite.setAlpha(1);
                    rlTitleMine.setAlpha(0);
                    rlTitleWhite.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                } else {
                    tvTitleApplyDetail.setAlpha(alpha);
                    tvTitleApplyDetailWhite.setAlpha(alpha);
                    ivBackWhite.setAlpha(alpha);
                    ivBack.setAlpha(alpha);
                    rlTitleWhite.setAlpha(alpha);
                    rlTitleMine.setAlpha(alpha);
                    rlTitleWhite.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                }
            } else {
                //超出bannerHeight
                tvTitleApplyDetail.setAlpha(0);
                tvTitleApplyDetailWhite.setAlpha(1.0f);
                ivBackWhite.setAlpha(1.0f);
                ivBack.setAlpha(0f);
                rlTitleMine.setAlpha(0);
                rlTitleWhite.setAlpha(1);
                rlTitleWhite.setBackgroundColor(Color.argb(255, 255, 255, 255));
            }
        });

        cbLoanGreement.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                tvSubmitDetailApply.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_blue_radius));
                tvSubmitDetailApply.setEnabled(true);
            } else {
                tvSubmitDetailApply.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_gray_radius));
                tvSubmitDetailApply.setEnabled(false);
            }
        });
    }

    private void setDesView(LinearLayout llLoanDatas, String des) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout view = new LinearLayout(mActivity);
        view.setLayoutParams(lp);
        view.setOrientation(LinearLayout.HORIZONTAL);
        view.setGravity(Gravity.CENTER_VERTICAL);
        TextView textView = new TextView(mActivity);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(ContextCompat.getColor(mActivity, R.color.text_gray_dark));
        textView.setPadding(0, 14, 0, 0);
        Drawable drawable = ContextCompat.getDrawable(mActivity, R.drawable.circle_gray);
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
        textView.setCompoundDrawablePadding(30);
        textView.setText(des);
        view.addView(textView);
        llLoanDatas.addView(view);
    }

    @OnClick({R.id.tv_submit_detail_apply, R.id.iv_back, R.id.tv_old_loan_agreement, R.id.iv_back_white})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_submit_detail_apply:
                if (cbLoanGreement.isChecked()) {
                    //获取联合登陆信息
                    help = new CreditLifeHelp(mActivity, creditLifeBorrowBean);
                    help.getUniteLoginInfo();
                }
                break;
            case R.id.iv_back_white:
            case R.id.iv_back:
                mActivity.finish();
                break;
            case R.id.tv_old_loan_agreement:
                WebSimpleFragment.WebService(mActivity, "file:///android_asset/login.htm", "联合注册授权书", WebSimpleFragment.STYLE_NORMAL);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        if (help != null) {
            help.destory();
        }
        super.onDestroyView();
    }
}
