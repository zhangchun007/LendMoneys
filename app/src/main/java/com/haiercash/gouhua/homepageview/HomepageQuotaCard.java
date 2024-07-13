package com.haiercash.gouhua.homepageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.PopupWindowBean;
import com.haiercash.gouhua.beans.homepage.Credit;
import com.haiercash.gouhua.beans.homepage.MainCardBean;
import com.haiercash.gouhua.beans.homepage.SubCardBean;
import com.haiercash.gouhua.bill.PeriodBillsFragment;
import com.haiercash.gouhua.fragments.main.MainEduBorrowUntil;
import com.haiercash.gouhua.hybrid.H5LinkJumpHelper;
import com.haiercash.gouhua.uihelper.NormalNewPopWindow;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.yy.mobile.rollingtextview.CharOrder;
import com.yy.mobile.rollingtextview.RollingTextView;
import com.yy.mobile.rollingtextview.strategy.Direction;
import com.yy.mobile.rollingtextview.strategy.Strategy;

import java.util.HashMap;
import java.util.Map;

public class HomepageQuotaCard extends LinearLayout implements NormalNewPopWindow.ClickButtonCallback {
    private TextView tvTitle;

    private RollingTextView rtMoney;//提额时候滚动的
    private TextView tvMoney;
    private TextView tvInfo;
    private TextView tvSubmit;
    private RelativeLayout rlBottom;
    private TextView tvWeeklyPay;
    private TextView tvLatestDay;
    private LinearLayout rootView;
    private Credit homeInfo;
    private BaseActivity baseActivity;
    private RelativeLayout rlQuotaView;
    private ImageView ivQuotaCard;
    private MainCardBean mainCardBean;
    private SubCardBean subCardBean;
    private String pageCode;
    private LinearLayout llInfo;
    private TextView tvRateInfo;   //主卡片显示的年化利率
    private TextView tvRate;   //副卡片显示的年化利率

    public HomepageQuotaCard(Context context) {
        super(context);
        Logger.e("HomepageQuotaCard 被创建了");
        init(context);
    }

    public HomepageQuotaCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomepageQuotaCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public String getPageCode() {
        return pageCode;
    }

    public HomepageQuotaCard setPageCode(String pageCode) {
        this.pageCode = pageCode;
        return this;
    }

    private void init(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.layout_homepage_quota, this);
        tvTitle = view.findViewById(R.id.tv_title);
        rtMoney = view.findViewById(R.id.rt_money);
        tvMoney = view.findViewById(R.id.tv_money);
        tvInfo = view.findViewById(R.id.tv_info);
        llInfo = view.findViewById(R.id.ll_info);
        tvRateInfo = view.findViewById(R.id.tv_rate_info);
        tvSubmit = view.findViewById(R.id.tv_submit);
        rlBottom = view.findViewById(R.id.rl_bottom);
        tvWeeklyPay = view.findViewById(R.id.tv_weekly_pay);
        tvLatestDay = view.findViewById(R.id.tv_latest_day);
        tvRate = view.findViewById(R.id.tv_rate);
        rootView = view.findViewById(R.id.ll_root_view);
        rlQuotaView = view.findViewById(R.id.rl_quota_back);
        ivQuotaCard = view.findViewById(R.id.iv_quota_card);
        tvSubmit.setOnClickListener(v -> MainEduBorrowUntil.INSTANCE(baseActivity).startEvent(homeInfo, v));
        tvLatestDay.setOnClickListener(v -> {
            if (homeInfo != null && !CheckUtil.isEmpty(homeInfo.getStatus())) {
                if (homeInfo.getStatus().endsWith("2")) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("button_name", "最近还款日");
                    map.put("overdue_flag", "false");
                    UMengUtil.commonClickEvent("LatestRepaymentDate_Click", null, map, getPageCode());
                    if (AppApplication.enableRepay) {
                        H5LinkJumpHelper.INSTANCE().goH5RepayPage(baseActivity);
                    } else {
                        ContainerActivity.to(baseActivity, PeriodBillsFragment.ID, null);
                    }
                }
                if (homeInfo.getStatus().endsWith("3")) {
                    showPayDialog(v);
                }
            }

        });

        //主卡片点击warning按钮
        tvInfo.setOnClickListener(v -> {
            if (mainCardBean != null) {
                if (mainCardBean.getShowWarning() == 1) {
                    if (!CheckUtil.isEmpty(mainCardBean.getReason())) {
                        baseActivity.showDialog(mainCardBean.getReason());
                    } else {
                        baseActivity.showDialog("未知原因");
                    }
                }
            }

        });

        //主卡片title点击warning按钮
        tvTitle.setOnClickListener(v -> {
            if (mainCardBean != null) {
                if (mainCardBean.getShowTitleWarning() == 1) {
                    if (!CheckUtil.isEmpty(mainCardBean.getReason())) {
                        baseActivity.showDialog(mainCardBean.getReason());
                    } else {
                        baseActivity.showDialog("未知原因");
                    }
                }
            }

        });


        //副卡片点击warning按钮
        tvWeeklyPay.setOnClickListener(v -> {
            if ("H3".equals(homeInfo.getStatus())) {
                showPayDialog(v);
            } else {
                if (subCardBean != null && !subCardBean.isNull()) {
                    if (subCardBean.getShowWarning() == 1) {
                        if (!CheckUtil.isEmpty(subCardBean.getReason())) {
                            baseActivity.showDialog(subCardBean.getReason());
                        } else {
                            baseActivity.showDialog("未知原因");
                        }
                    }
                }
            }
        });

    }

    //显示逾期弹窗，提示先还款
    private void showPayDialog(View v) {
        PopupWindowBean bean = new PopupWindowBean();
        bean.setTitle("提示");
        bean.setContent("经系统评估，您的信用评分过低，当前账户被临时限制用信，请保持正常还款，防止额度被冻结。");
        bean.setButtonTv("去还款");
        bean.setButtonBack(R.drawable.bg_btn_commit);
        new NormalNewPopWindow(baseActivity, bean, this).showAtLocation(v);
    }

    /**
     * A1	额度不存在且额度申请记录不存在，无7日待还记录或者7日待还为0
     * 主卡：logo、最高可借（元）、默认最高可借金额、查看额度按钮，slogan
     * <p>
     * 副卡：none
     * <p>
     * A2	额度不存在且额度申请记录不存在，有7日待还记录，且7日待还记录没有逾期。
     * 主卡：logo、最高可借（元）、默认最高可借金额、查看额度按钮，slogan
     * <p>
     * 副卡：近7日待还（元）：、待还金额、最近还款日xx-xx、arrow
     * <p>
     * A3	额度不存在且额度申请记录不存在，有7日待还记录，且7日待还中有至少一笔逾期记录。
     * 主卡：近7日待还（元）、待还金额、去还款按钮、您有x笔借款已逾期（红色）
     * <p>
     * 副卡：logo、最高可借、默认最高可借金额、查看额度文本按钮、arrow
     * <p>
     * B1	无额度信息且最近一次额度申请状态为01（审批中）或者额度申请状态为27（审批通过）且金额不存在或为0，无7日待还记录或者7日待还为0。
     * 主卡：logo、额度审批中（颜色）、敬请期待...
     * <p>
     * 副卡：none
     * <p>
     * B2	无额度信息且最近一次额度申请状态为01（审批中）或者额度申请状态为27（审批通过）且金额不存在或为0，有7日待还记录，且7日待还记录没有逾期。
     * 主卡：logo、额度审批中（颜色）、敬请期待...
     * <p>
     * 副卡：近7日待还（元）：、待还金额、最近还款日xx-xx、arrow
     * <p>
     * B3	无额度信息且最近一次额度申请状态为01（审批中）或者额度申请状态为27（审批通过）且金额不存在或为0，且7日待还中有至少一笔逾期记录。
     * 主卡：近7日待还（元）、待还金额、去还款按钮、您有x笔借款已逾期（红色）
     * <p>
     * 副卡：logo、额度审批中（颜色）、敬请期待...
     * <p>
     * C1	有额度信息且额度状态为30（变更中），无7日待还记录或者7日待还为0。	主卡：logo、额度变更审批中（颜色）、暂不能借款哦~
     * C2	有额度信息且额度状态为30（变更中），有7日待还记录，且7日待还记录没有逾期。
     * 主卡：logo、额度变更审批中（颜色）、暂不能借款哦~
     * <p>
     * 副卡：近7日待还（元）：、待还金额、最近还款日xx--xx、arrow
     * <p>
     * C3	有额度信息且额度状态为30（变更中），，有7日待还记录，且7日待还中有至少一笔逾期记录。
     * 主卡：近7日待还（元）、待还金额、去还款按钮、您有x笔借款已逾期（红色）
     * <p>
     * 副卡：logo、额度变更审批中（颜色）、暂不能借款哦~
     * <p>
     * D1	有额度信息且额度状态为10（正常），无7日待还记录或者7日待还为0。
     * 主卡：logo、可借额度（元），可借金额、去借款按钮、总额度xxxx
     * <p>
     * D2	有额度信息且额度状态为10（正常），有7日待还记录，且7日待还记录没有逾期。
     * 主卡：logo、可借额度（元），可借金额、去借款按钮、总额度xxxx
     * <p>
     * 副卡：近7日待还（元）：、待还金额、最近还款日xx-xx、arrow
     * <p>
     * D3	有额度信息且额度状态为10（正常），有7日待还记录，且7日待还中有至少一笔逾期记录。
     * 主卡：近7日待还（元）、待还金额、去还款按钮、您有x笔借款已逾期（红色）
     * <p>
     * 副卡：logo、最高可借xxx、查看额度、arrow
     * <p>
     * E1	有额度信息且额度状态为20（冻结），无7日待还记录或者7日待还为0。
     * 主卡：logo、额度被冻结、暂不能进行额度执行、联系我们按钮
     * <p>
     * 主卡：logo、可借（元）xxx、去借款、arrow
     * <p>
     * E2	有额度信息且额度状态为20（冻结），有7日待还记录，且7日待还记录没有逾期。
     * 主卡：logo、额度被冻结、暂不能进行额度执行、联系我们按钮
     * <p>
     * 副卡：近7日待还（元）：、待还金额、最近还款日xx-xx、arrow
     * <p>
     * 主卡：logo、可借（元）xxx、去借款、arrow
     * <p>
     * 副卡：近7日待还（元）：、待还金额、最近还款日xx-xx、arrow
     * <p>
     * E3	有额度信息且额度状态为20（冻结），有7日待还记录，且7日待还中有至少一笔逾期记录。
     * 主卡：近7日待还（元）、待还金额、去还款按钮、您有x笔借款已逾期（红色）
     * <p>
     * 副卡：logo、可借（元）xxx、去借款、arrow
     * <p>
     * F1	有额度信息且额度状态为40（失效），无7日待还记录或者7日待还为0。	主卡：logo、额度失效、重新申请按钮、请重新申请额度
     * F2	有额度信息且额度状态为40（失效），有7日待还记录，且7日待还记录没有逾期。
     * 主卡：logo、额度失效、重新申请按钮、请重新申请额度
     * <p>
     * 副卡：近7日待还（元）：、待还金额、最近还款日xx-xx、arrow
     * <p>
     * F3	有额度信息且额度状态为40（失效），有7日待还记录，且7日待还中有至少一笔逾期记录。
     * 主卡：近7日待还（元）、待还金额、去还款按钮、您有x笔借款已逾期（红色）
     * <p>
     * 副卡：logo、额度已失效、重新申请、arrow
     * <p>
     * G1	无额度信息且最近一次额度申请状态为22（审批退回），无7日待还记录或者7日待还为0。	主卡：logo、额度审批被退回、修改提交按钮、查看原因
     * G2	无额度信息且最近一次额度申请状态为22（审批退回），有7日待还记录，且7日待还记录没有逾期。
     * 主卡：logo、额度审批被退回、修改提交按钮、查看原因
     * <p>
     * 副卡：近7日待还（元）：、待还金额、最近还款日xx-xx、arrow
     * <p>
     * G3	无额度信息且最近一次额度申请状态为22（审批退回），有7日待还记录，且7日待还中有至少一笔逾期记录。
     * 主卡：近7日待还（元）、待还金额、去还款按钮、您有x笔借款已逾期（红色）
     * <p>
     * 副卡：logo、额度已失效、重新申请、arrow
     * <p>
     * H1	无额度信息且最近一次额度申请状态为25（额度申请拒绝），无7日待还记录或者7日待还为0。	主卡：logo、额度审批未通过、查看原因
     * H2	无额度信息且最近一次额度申请状态为25（额度申请拒绝），有7日待还记录，且7日待还记录没有逾期。
     * 主卡：logo、额度审批未通过、查看原因
     * <p>
     * 副卡：近7日待还（元）：、待还金额、最近还款日xx-xx、arrow
     * <p>
     * H3	无额度信息且最近一次额度申请状态为25（额度申请拒绝），有7日待还记录，且7日待还中有至少一笔逾期记录。
     * 主卡：近7日待还（元）、待还金额、去还款按钮、您有x笔借款已逾期（红色）
     * <p>
     * 副卡：额度审批未通过、查看原因
     */
    public void setData(Credit homePageInfo, BaseActivity context) {
        if (homePageInfo == null || homePageInfo.getMain() == null) {
            return;
        }
        this.homeInfo = homePageInfo;
        this.baseActivity = context;
        mainCardBean = homePageInfo.getMain();
        subCardBean = null;//homePageInfo.getSub();
        boolean hasBottomInfo = subCardBean != null && !subCardBean.isNull();
        boolean isMoreTopMargin = "large".equals(mainCardBean.getDistance());
        boolean isShowVip = "Y".equals(homeInfo.getHyOpenState());
        setTvTitle(mainCardBean.getShowLogo() == 1, mainCardBean.getShowTitleWarning() == 1, isMoreTopMargin, hasBottomInfo,
                mainCardBean.getText(), mainCardBean.getTextColor());
        setTvMoney("small".equals(mainCardBean.getDistance()), mainCardBean.getAmountOld(), mainCardBean.getAmount());
        setTvInfo(isMoreTopMargin, mainCardBean.getShowWarning() == 1, mainCardBean);
        setSubmitButtonInfo(!CheckUtil.isEmpty(mainCardBean.getBtnText()), mainCardBean.getBtnText());
        setBottomInfo(subCardBean);
        if (hasBottomInfo) {
            rootView.setBackgroundResource(isShowVip ? R.drawable.main_credit_card_background_large2_vip : R.drawable.main_credit_card_background_large2);
            rlQuotaView.setBackgroundResource(isShowVip ? 0 : R.drawable.main_credit_card_background_small);
        } else {
            rootView.setBackgroundResource(isShowVip ? R.drawable.main_credit_card_background_vip : R.drawable.main_credit_card_background);
            rlQuotaView.setBackgroundResource(0);
        }
    }

    /**
     * 设置title
     * isShowLogo  是否展示够花logo
     * isShowWarning  是否展示警告logo
     * isMarginMore  两行时候距离顶部加大
     * hasBottomInfo  有底部信息，tvTitle距离顶部更小
     * text  展示的文字
     */
    private void setTvTitle(boolean isShowLogo, boolean isShowWarning, boolean isMarginMore, boolean hasBottomInfo, String text, String textColor) {
        setLogo(isShowLogo, isShowWarning);

        LayoutParams lp = (LayoutParams) tvTitle.getLayoutParams();
        if (hasBottomInfo) {
            lp.topMargin = UiUtil.dip2px(getContext(), isMarginMore ? 45 : 25);
        } else {
            lp.topMargin = UiUtil.dip2px(getContext(), isMarginMore ? 50 : 30);
        }
        tvTitle.setLayoutParams(lp);
        tvTitle.setText(text);
        try {
            tvTitle.setTextColor(Color.parseColor(textColor));
        } catch (Exception e) {
            tvTitle.setTextColor(baseActivity.getResources().getColor(R.color.white));
        }
    }

    /**
     * @param isShow  是否显示
     * @param textOld 老的金额，有值则需要滚动显示
     * @param text    显示金额
     */
    private void setTvMoney(boolean isShow, String textOld, String text) {
        if (isShow) {
            if (!CheckUtil.isEmpty(textOld) && !CheckUtil.isEmpty(text)) {
                tvMoney.setVisibility(GONE);
                rtMoney.setVisibility(VISIBLE);
                setRollingTv(textOld, text);
                rtMoney.setTypeface(FontCustom.getDinFont(getContext()));
            } else {
                tvMoney.setVisibility(VISIBLE);
                rtMoney.setVisibility(GONE);
                tvMoney.setText(text);
                tvMoney.setTypeface(FontCustom.getDinFont(getContext()));
            }
        } else {
            tvMoney.setVisibility(GONE);
            rtMoney.setVisibility(GONE);
        }
    }

    //设置滚动字符串
    private void setRollingTv(String textOld, String text) {
        rtMoney.setText(textOld);
        rtMoney.setAnimationDuration(2000L);
        rtMoney.setCharStrategy(Strategy.SameDirectionAnimation(Direction.SCROLL_DOWN));
        rtMoney.addCharOrder(CharOrder.Number);
        rtMoney.setAnimationInterpolator(new AccelerateDecelerateInterpolator());
        rtMoney.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        rtMoney.setText(text);


    }

    /**
     * isMoreTopMargin  是否距离顶部更多
     * isShowWarning  是否展示警告图片
     *
     * @param bean 主卡片数据
     */
    private void setTvInfo(boolean isMoreTopMargin, boolean isShowIcon, MainCardBean bean) {
        LayoutParams lp = (LayoutParams) llInfo.getLayoutParams();
        if (isMoreTopMargin) {
            lp.topMargin = UiUtil.dip2px(getContext(), 20);
        } else {
            lp.topMargin = UiUtil.dip2px(getContext(), 8);
        }
        llInfo.setLayoutParams(lp);
        if (isShowIcon) {
            ivQuotaCard.setImageResource(R.drawable.icon_page2_bottom_no_pass);
            Drawable drawableLeft = ContextCompat.getDrawable(getContext(), R.drawable.icon_little_warning);
            tvInfo.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
            tvInfo.setCompoundDrawablePadding(UiUtil.dip2px(getContext(), 4));
        } else {
            ivQuotaCard.setImageResource(R.drawable.icon_page2_bottom);
            tvInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        //逾期
        boolean isOver = this.homeInfo != null && this.homeInfo.getStatus() != null && this.homeInfo.getStatus().endsWith("3");
        tvInfo.setText(bean.getSubText());
        if (!CheckUtil.isEmpty(bean.getMainInfoText())) {
            tvRateInfo.setVisibility(VISIBLE);
            tvRateInfo.setText(bean.getMainInfoText());
            try {
                tvRateInfo.setTextColor(Color.parseColor(bean.getMainInfoTextColor()));
            } catch (Exception e) {
                tvRateInfo.setTextColor(baseActivity.getResources().getColor(R.color.color_B9E2FF));
            }
        } else {
            tvRateInfo.setVisibility(GONE);
        }
        try {
            tvInfo.setTextColor(Color.parseColor(bean.getSubTextColor()));
        } catch (Exception e) {
            tvInfo.setTextColor(isOver ? 0xFFFFB900 : baseActivity.getResources().getColor(R.color.white));
        }
    }

    /**
     * @param isShow 按钮是否显示
     * @param text   文字信息
     */
    private void setSubmitButtonInfo(boolean isShow, String text) {
        if (isShow) {
            tvSubmit.setVisibility(VISIBLE);
            tvSubmit.setText(text);
        } else {
            tvSubmit.setVisibility(GONE);
        }
    }

    /**
     * 额度模块下半部分
     */
    private void setBottomInfo(SubCardBean subCardBean) {
        if (subCardBean != null && !subCardBean.isNull()) {
            rlBottom.setVisibility(VISIBLE);
            if (!CheckUtil.isEmpty(subCardBean.getSubInfoText())) {
                tvRate.setVisibility(VISIBLE);
                tvRate.setText(subCardBean.getSubInfoText());
                try {
                    tvRate.setTextColor(Color.parseColor(subCardBean.getSubInfoTextColor()));
                } catch (Exception e) {
                    tvRate.setTextColor(baseActivity.getResources().getColor(R.color.color_B9E2FF));
                }
            } else {
                tvRate.setVisibility(GONE);
            }
            if (!CheckUtil.isEmpty(subCardBean.getText())) {
                Drawable drawableLeft = null;
                Drawable drawableRight = null;
                if (subCardBean.getShowLogo() == 1) {
                    drawableLeft = ContextCompat.getDrawable(getContext(), R.drawable.icon_left_logo_small);
                }
                if (subCardBean.getShowWarning() == 1) {
                    drawableRight = ContextCompat.getDrawable(getContext(), R.drawable.icon_little_warning);
                }
                tvWeeklyPay.setText(subCardBean.getText());
                tvWeeklyPay.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null);
                try {
                    tvWeeklyPay.setTextColor(Color.parseColor(subCardBean.getTextColor()));
                } catch (Exception e) {
                    tvWeeklyPay.setTextColor(baseActivity.getResources().getColor(R.color.white));
                }
            }

            if (!CheckUtil.isEmpty(subCardBean.getSubText())) {
                tvLatestDay.setText(subCardBean.getSubText());
                Drawable drawableMore = null;
                if (subCardBean.getShowArrow() == 1) {
                    drawableMore = ContextCompat.getDrawable(getContext(), R.drawable.icon_right_more_small);
                }
                tvLatestDay.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableMore, null);
                try {
                    tvLatestDay.setTextColor(Color.parseColor(subCardBean.getSubTextColor()));
                } catch (Exception e) {
                    tvLatestDay.setTextColor(baseActivity.getResources().getColor(R.color.white));
                }
            }
        } else {
            rlBottom.setVisibility(GONE);
        }

    }

    private void setLogo(boolean isShow, boolean isShowWaring) {
        Drawable drawableLeft = ContextCompat.getDrawable(getContext(), R.drawable.icon_left_logo);
        Drawable drawableRight = ContextCompat.getDrawable(getContext(), R.drawable.icon_little_warning);

        if (isShow && isShowWaring) {
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRight, null);
            tvTitle.setCompoundDrawablePadding(UiUtil.dip2px(getContext(), 2));
        } else if (isShow) {
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        } else if (isShowWaring) {
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight, null);
            tvTitle.setCompoundDrawablePadding(UiUtil.dip2px(getContext(), 4));
        } else {
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    //点击弹窗的按钮回调
    @Override
    public void clickButton() {
        if (AppApplication.enableRepay) {
            H5LinkJumpHelper.INSTANCE().goH5RepayPage(baseActivity);
        } else {
            ContainerActivity.to(baseActivity, PeriodBillsFragment.ID, null);
        }
    }
}

