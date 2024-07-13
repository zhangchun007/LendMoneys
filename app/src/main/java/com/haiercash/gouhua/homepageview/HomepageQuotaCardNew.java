package com.haiercash.gouhua.homepageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.activity.login.SmsWayLoginActivity;
import com.haiercash.gouhua.adaptor.homepage.HomeKumgangAdapter;
import com.haiercash.gouhua.adaptor.homepage.HomeLoanMarketAdapter;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.AppUntil;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.CreditLifeBorrowBean;
import com.haiercash.gouhua.beans.PopupWindowBean;
import com.haiercash.gouhua.beans.homepage.Credit;
import com.haiercash.gouhua.beans.homepage.CreditProcess;
import com.haiercash.gouhua.beans.homepage.GzxData;
import com.haiercash.gouhua.beans.homepage.HomeQuotaBean;
import com.haiercash.gouhua.beans.homepage.MainCardBean;
import com.haiercash.gouhua.beans.homepage.ResidentRibbon;
import com.haiercash.gouhua.fragments.main.MainEduBorrowUntil;
import com.haiercash.gouhua.gzx.GzxAgreementActivity;
import com.haiercash.gouhua.gzx.GzxTransitionActivity;
import com.haiercash.gouhua.hybrid.H5LinkJumpHelper;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.uihelper.CreditLifeHelp;
import com.haiercash.gouhua.uihelper.NormalNewPopWindow;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.SchemeHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.AnimLightView;
import com.yy.mobile.rollingtextview.CharOrder;
import com.yy.mobile.rollingtextview.RollingTextView;
import com.yy.mobile.rollingtextview.strategy.Direction;
import com.yy.mobile.rollingtextview.strategy.Strategy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomepageQuotaCardNew extends LinearLayout implements NormalNewPopWindow.ClickButtonCallback {
    private Credit homeInfo;
    private BaseActivity baseActivity;
    private Context mContext;

    private MainCardBean mainCardBean;
    private String pageCode;
    private ConstraintLayout quotaUnLogin; //未登录背景
    private RelativeLayout rlQuota;  //登录之后的额度卡片
    private RelativeLayout rlQuotaBack; //给额度卡片设置背景的view
    private TextView tvLogoInfo;//显示 随用随借 灵活还款+logo
    private TextView tvSceneLogo;//显示 场景现金贷文案+logo
    private View viewLine; //卡片上的虚线，需控制显示隐藏
    private LottieAnimationView ivLoading; //loading，需控制显示隐藏与loading
    private RelativeLayout rlMoneyView; //显示钱的区域，与ivLoading互斥
    private TextView tvTitle; //最高可借
    private TextView tvOverdueDay; //逾期气泡
    private RelativeLayout rlMoney; //显示额度和下划线的view
    private RollingTextView tvMoney;// 额度
    private TextView tvRateInfo;   //主卡片显示的年利率
    private TextView tvSubmit;//卡片上的按钮
    private TextView tvOverdueInfo; //底部逾期信息
    private RelativeLayout rlCreditStep; //申额阶段展示进度
    private TextView tvCreditTitle; //申额步骤title
    private ImageView ivCreditStepOne; //申额第一步图片
    private AnimLightView ivCreditLightOne; //申额第一步闪图
    private TextView tvCreditStepOne;  //申额第一步title
    private ImageView ivCreditStepTwo;//申额第二步图片
    private AnimLightView ivCreditLightTwo; //申额第二步闪图
    private TextView tvCreditStepTwo;//申额第二步title
    private ImageView ivCreditStepThree;//申额第三步图片
    private AnimLightView ivCreditLightThree; //申额第三步闪图
    private TextView tvCreditStepThree;//申额第三步title
    private RecyclerView rvKumgang;  //金刚区
    private ImageView ivVipLogo;  //是vip则显示该标签
    private ImageView ivCreditOneArrow;  //申额进度第一个箭头
    private ImageView ivCreditTwoArrow;  //申额进度第二个箭头
    private ImageView ivExclusive;  //限时专享
    private HomeQuotaBean homeQuotaBean; //首页返回的所有数据
    private RelativeLayout rlLoanMarket; //贷超布局
    private RecyclerView rvLoanMarket;// 贷超列表
    private View viewUnderline;
    private CreditLifeHelp creditLifeHelp;
    private TextView tvShowMoney;  //未登录显示的最高可借 200，000
    private TextView tvExtraBubble;
    private TextView tvExtraInfo;
    private LinearLayout llRateExtra; //利率和临时额度父view
    private ImageView ivRightArrow; //有临时额度需要展示该图片
    private ImageView ivGzx;  //够智选数据

    private TextView tvLoginBubble; //登录按钮上的气泡
    private TextView tvSubmitBubble;//卡片上的气泡
    private TextView tvZeroBubble; //额度借光气泡


    public HomepageQuotaCardNew(Context context) {
        super(context);
        this.mContext = context;
        Logger.e("HomepageQuotaCard 被创建了");
        init(context);
    }

    public HomepageQuotaCardNew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomepageQuotaCardNew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public String getPageCode() {
        return pageCode;
    }

    public HomepageQuotaCardNew setPageCode(String pageCode) {
        this.pageCode = pageCode;
        return this;
    }

    private void init(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.layout_homepage_quota_new, this);
        quotaUnLogin = view.findViewById(R.id.cl_quota_unLogin);
        tvShowMoney = view.findViewById(R.id.tv_show_money);
        rlQuota = view.findViewById(R.id.rl_quota);
        ivVipLogo = view.findViewById(R.id.iv_vip_logo);
        rlQuotaBack = view.findViewById(R.id.rl_quota_back);
        tvLogoInfo = view.findViewById(R.id.tv_logo_info);
        tvSceneLogo = view.findViewById(R.id.tv_scene_logo);
        viewLine = view.findViewById(R.id.view_line);
        ivLoading = view.findViewById(R.id.iv_loading);
        rlMoneyView = view.findViewById(R.id.rl_money_view);
        tvTitle = view.findViewById(R.id.tv_title);
        tvOverdueDay = view.findViewById(R.id.tv_overdue_day);
        rlMoney = view.findViewById(R.id.rl_money);
        tvZeroBubble = view.findViewById(R.id.tv_zero_bubble);
        tvMoney = view.findViewById(R.id.tv_money);
        viewUnderline = view.findViewById(R.id.view_underline);
        tvRateInfo = view.findViewById(R.id.tv_rate_info);
        llRateExtra = view.findViewById(R.id.ll_rate_extra);
        tvExtraBubble = view.findViewById(R.id.tv_extra_bubble);
        tvExtraInfo = view.findViewById(R.id.tv_extra_info);
        ivRightArrow = view.findViewById(R.id.iv_right_arrow);
        tvSubmit = view.findViewById(R.id.tv_submit);
        tvOverdueInfo = view.findViewById(R.id.tv_overdue_info);
        rlCreditStep = view.findViewById(R.id.rl_credit_step);
        tvCreditTitle = view.findViewById(R.id.tv_credit_title);

        ivCreditStepOne = view.findViewById(R.id.iv_credit_step_one);
        ivCreditLightOne = view.findViewById(R.id.alv_one);
        tvCreditStepOne = view.findViewById(R.id.tv_credit_step_one);
        ivCreditStepTwo = view.findViewById(R.id.iv_credit_step_two);
        ivCreditLightTwo = view.findViewById(R.id.alv_two);
        tvCreditStepTwo = view.findViewById(R.id.tv_credit_step_two);
        ivCreditStepThree = view.findViewById(R.id.iv_credit_step_three);
        ivCreditLightThree = view.findViewById(R.id.alv_three);
        tvCreditStepThree = view.findViewById(R.id.tv_credit_step_three);
        ivCreditOneArrow = view.findViewById(R.id.iv_credit_one_arrow);
        ivCreditTwoArrow = view.findViewById(R.id.iv_credit_two_arrow);
        ivExclusive = view.findViewById(R.id.iv_exclusive);


        rvKumgang = view.findViewById(R.id.rv_kumgang);
        rlLoanMarket = view.findViewById(R.id.rl_loan_market);
        rvLoanMarket = view.findViewById(R.id.rv_loan_market);

        ivGzx = view.findViewById(R.id.iv_gzx);

        tvLoginBubble = view.findViewById(R.id.tv_login_bubble);
        tvSubmitBubble = view.findViewById(R.id.tv_submit_bubble);

        tvShowMoney.setTypeface(FontCustom.getDinFont(getContext()));
        setTvMoneyAnim();
        if (!AppApplication.isLogIn()) {
            quotaUnLogin.setVisibility(VISIBLE);
            rlQuota.setVisibility(GONE);
            rlLoanMarket.setVisibility(GONE);
            ivGzx.setVisibility(GONE);
            rvKumgang.setVisibility(GONE);
            HashMap<String, Object> map = new HashMap<>();
            map.put("page_name_cn", "够花-首页");
            map.put("button_name", "未登录-立即登录");
            UMengUtil.onEventObject("QuotaElement_Exposure", map, pageCode);
        } else {
            quotaUnLogin.setVisibility(GONE);
            rlQuota.setVisibility(VISIBLE);
        }
        quotaUnLogin.setOnClickListener(v -> {
            if (context instanceof BaseActivity && AppUntil.touristIntercept(quotaUnLogin, (BaseActivity) context)) {
                return;
            }
            LoginSelectHelper.staticToGeneralLogin();
            HashMap<String, Object> map = new HashMap<>();
            map.put("page_name_cn", "够花-首页");
            map.put("button_name", "未登录-立即登录");
            UMengUtil.onEventObject("QuotaElement", map, pageCode);
        });

        tvSubmit.setOnClickListener(v -> {
            if (context instanceof BaseActivity && AppUntil.touristIntercept(tvSubmit, (BaseActivity) context)) {
                return;
            }
            if (!CheckUtil.isEmpty(homeInfo)) {
                if (!CheckUtil.isEmpty(homeInfo.getMain()) && !CheckUtil.isEmpty(homeInfo.getMain().getBtnClickCheck()) && !CheckUtil.isEmpty(homeInfo.getMain().getBtnClickCheck().getBtnType())) {
                    if ("repay".equals(homeInfo.getMain().getBtnClickCheck().getBtnType())) {
                        showPayDialog(this, homeInfo.getMain().getBtnClickCheck().getErrMsg());
                    } else {
                        baseActivity.showDialog(homeInfo.getMain().getBtnClickCheck().getErrMsg());
                    }
                    //失败原因上送
                    MainEduBorrowUntil.INSTANCE(baseActivity).postHomePageButtonForErrorEvent("QuotaElement", homeInfo.getMain().getBtnText(), true, homeInfo.getMain().getBtnClickCheck().getErrMsg(), homeInfo.getThirdTitleUMeng());
                } else {
                    MainEduBorrowUntil.INSTANCE(baseActivity).startEvent(homeInfo, v, true);
                }
            } else {
                MainEduBorrowUntil.INSTANCE(baseActivity).postHomePageButtonEvent("QuotaElement", "获取失败-立即刷新");
                startRefreshState(true);
            }
        });
        rlCreditStep.setOnClickListener(v -> {
            MainEduBorrowUntil.INSTANCE(baseActivity).startEvent(homeInfo, v);
            postStepEvent("Gh_Home_NoviceGuide_Click");
        });

        //临时额度跳转
        llRateExtra.setOnClickListener(v -> {
            if (!CheckUtil.isEmpty(homeInfo) && !CheckUtil.isEmpty(homeInfo.getMain()) && !CheckUtil.isEmpty(homeInfo.getMain().getExtSubText())) {
                if (!CheckUtil.isEmpty(homeInfo.getMain().getExtraCreditLink())) {
                    SchemeHelper.jumpFromScheme(homeInfo.getMain().getExtraCreditLink(), baseActivity);
                } else {
                    baseActivity.showDialog("获取信息失败，请刷新重试");
                }
            }
        });

        //主卡片点击warning按钮
        tvTitle.setOnClickListener(v -> {
            if (mainCardBean != null) {
                if (mainCardBean.getShowWarning() == 1) {
                    if (!CheckUtil.isEmpty(mainCardBean.getReason())) {
                        baseActivity.showDialog(mainCardBean.getReason());
                    } else {
                        baseActivity.showDialog("获取原因失败，请稍后重试");
                    }
                }
            }

        });

        ivGzx.setOnClickListener(v -> {
            postGzxEvent("Gh_Home_GzxDiversion_Click");
            if (!CheckUtil.isEmpty(homeQuotaBean) &&
                    !CheckUtil.isEmpty(homeQuotaBean.getGzxData()) &&
                    !CheckUtil.isEmpty(homeQuotaBean.getGzxData().getGzxUrl())) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("url", homeQuotaBean.getGzxData().getGzxUrl());
                if (!CheckUtil.isEmpty(homeQuotaBean.getGzxData().getAgreementList())) {
                    bundle.putSerializable("agreementList", (Serializable) homeQuotaBean.getGzxData().getAgreementList());
                    // intent.setClass(baseActivity, GzxAgreementActivity.class);
                    intent.putExtras(bundle);
                    GzxAgreementActivity.startDialogActivity(baseActivity, GzxAgreementActivity.class, SmsWayLoginActivity.ANIM_BOTTOM_IN_RIGHT_OUT, intent);

                } else {
                    intent.setClass(baseActivity, GzxTransitionActivity.class);
                    intent.putExtras(bundle);
                    baseActivity.startActivity(intent);
                }
            } else {
                baseActivity.showDialog("数据异常，请刷新重试");
            }
        });

    }

    //显示逾期弹窗，提示先还款
    private void showPayDialog(View v, String content) {
        PopupWindowBean bean = new PopupWindowBean();
        bean.setTitle("提示");
        bean.setContent(!TextUtils.isEmpty(content) ? content : "经系统评估，您的信用评分过低，当前账户被临时限制用信，请保持正常还款，防止额度被冻结。");
        bean.setButtonTv("去还款");
        bean.setButtonBack(R.drawable.bg_btn_commit);
        new NormalNewPopWindow(baseActivity, bean, this).showAtLocation(v);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                quotaUnLogin.setVisibility(GONE);
                rlQuota.setVisibility(VISIBLE);
                rlMoneyView.setVisibility(GONE);
                ivLoading.setVisibility(VISIBLE);
                startAnimation();
                tvSubmit.setText("加载中");
                tvSubmitBubble.setVisibility(GONE);
                tvSubmit.setEnabled(false);
                MainEduBorrowUntil.INSTANCE(baseActivity).postHomePageButtonEvent("QuotaElement_Exposure", "加载-加载中");
                mHandler.removeCallbacksAndMessages(null);
            }
        }
    };


    /**
     * A：无额度、无额度申请记录、未登录时的默认状态
     * B：额度审批中
     * C：额度变更中
     * D：额度正常
     * E：额度冻结
     * F：额度无效
     * G：额度审批退回
     * H：额度拒绝
     * R：[A~H]3均转换为R3，代表存在逾期
     */
    public void setData(HomeQuotaBean homeQuotaBean, BaseActivity context) {
        endRefreshState();
        refreshLayoutState();
        if (homeQuotaBean == null || homeQuotaBean.getCredit() == null || homeQuotaBean.getCredit().getMain() == null) {
            this.homeInfo = null;
            setTvTitle(false, true, "额度获取失败", "#FFFFFF", false);
            setTvMoney("", "");
            setSubtextInfo("请刷新重试～", "#FFFFFF", false);
            setSubmitButtonInfo(true, "立即刷新");
            MainEduBorrowUntil.INSTANCE(baseActivity).postHomePageButtonEvent("QuotaElement_Exposure", "获取失败-立即刷新");
            return;
        }
        this.homeQuotaBean = homeQuotaBean;
        this.homeInfo = homeQuotaBean.getCredit();
        MainEduBorrowUntil.INSTANCE(baseActivity).setCreditInfo(homeInfo);
        this.baseActivity = context;
        mainCardBean = homeInfo.getMain();
        boolean isMoreTopMargin = "large".equals(mainCardBean.getDistance());
        boolean isShowVip = "Y".equals(homeInfo.getHyOpenState());
        SpHp.saveSpLogin(SpKey.VIP_STATUS, homeInfo.getHyOpenState());
        UMengUtil.registerGlobalProperty("true", AppApplication.userid);
        if (isShowVip) {
            ivVipLogo.setVisibility(VISIBLE);
        } else {
            ivVipLogo.setVisibility(GONE);
        }

        setLogoInfo(homeInfo.getThirdTitle(), homeInfo.getThirdLogo());
        setTvTitle(mainCardBean.getShowWarning() == 1, isMoreTopMargin, mainCardBean.getText(), mainCardBean.getTextColor(), "small".equals(mainCardBean.getCardType()));
        setTvMoney(mainCardBean.getAmountOld(), mainCardBean.getAmount());
        setSubtextInfo(mainCardBean.getSubText(), mainCardBean.getSubTextColor(), "small".equals(mainCardBean.getCardType()));
        setExtraInfo(mainCardBean.getExtSubBubbleText(), mainCardBean.getExtSubBubbleTextColor()
                , mainCardBean.getExtSubBubbleStartColor(), mainCardBean.getExtSubBubbleEndColor(),
                mainCardBean.getExtSubText(), mainCardBean.getExtSubTextColor());
        setBubbleData(mainCardBean.getShowBubble() == 1, mainCardBean.getBubbleText(), mainCardBean.getBubbleStartColor(), mainCardBean.getBubbleEndColor());
        setBottomText(mainCardBean.getMainInfoText(), mainCardBean.getMainInfoTextColor());
        setSubmitButtonInfo(!CheckUtil.isEmpty(mainCardBean.getBtnText()), mainCardBean.getBtnText());
        setCreditStepInfo(homeInfo.getCreditProcessTitle(), homeInfo.getCreditProcess());
        setKumgangArea(homeQuotaBean.getResidentRibbon());
        setGzxData(homeQuotaBean.getGzxData());
        setLoanMarket(homeQuotaBean.getLoanMarket());

        String cardType = mainCardBean.getCardType();
        RelativeLayout.LayoutParams quotaParams = (RelativeLayout.LayoutParams) rlQuotaBack.getLayoutParams();
        RelativeLayout.LayoutParams loadingParams = (RelativeLayout.LayoutParams) ivLoading.getLayoutParams();
        loadingParams.topMargin = UiUtil.dip2px(context, 56);

        if (CheckUtil.isEmpty(cardType)) {
            cardType = "large";
        }

        switch (cardType) {
            case "small":
                rlQuotaBack.setBackgroundResource(R.drawable.bg_quota_small);
                loadingParams.topMargin = UiUtil.dip2px(context, 42);
                quotaParams.height = UiUtil.dip2px(context, 110);
                viewLine.setVisibility(GONE);
                tvLogoInfo.setVisibility(GONE);
                break;
            case "normal_shadow":
                rlQuotaBack.setBackgroundResource(R.drawable.bg_quota_normal_shadow);
                quotaParams.height = UiUtil.dip2px(context, 215);
                viewLine.setVisibility(VISIBLE);
                tvLogoInfo.setVisibility(tvSceneLogo.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE);
                break;
            case "large":
                rlQuotaBack.setBackgroundResource(R.drawable.bg_quota_large);
                quotaParams.height = UiUtil.dip2px(context, 237);
                viewLine.setVisibility(VISIBLE);
                tvLogoInfo.setVisibility(tvSceneLogo.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE);
                break;
            default:
                rlQuotaBack.setBackgroundResource(R.drawable.bg_quota_normal);
                quotaParams.height = UiUtil.dip2px(context, 200);
                viewLine.setVisibility(VISIBLE);
                tvLogoInfo.setVisibility(tvSceneLogo.getVisibility() == VISIBLE ? INVISIBLE : VISIBLE);
                break;
        }
        ivLoading.setLayoutParams(loadingParams);
        rlQuotaBack.setLayoutParams(quotaParams);
        invalidate();

    }

    //设置顶部logo，有拒量三方时有数据
    private void setLogoInfo(String thirdTitle, String thirdLogo) {
        if (tvSceneLogo.getVisibility() == VISIBLE) {
            return;
        }
        tvLogoInfo.setVisibility(VISIBLE);
        ivExclusive.setVisibility(GONE);
        if (!CheckUtil.isEmpty(thirdTitle)) {
            tvLogoInfo.setText(thirdTitle);
            loadThirdLogoPic(thirdLogo);
        } else {
            tvLogoInfo.setText(baseActivity.getString(R.string.home_logo_info));
            Drawable drawableStart = ContextCompat.getDrawable(getContext(), R.drawable.icon_logo_left_small);
            tvLogoInfo.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null);
            tvLogoInfo.setCompoundDrawablePadding(UiUtil.dip2px(getContext(), 5));
        }
    }

    public void setSceneLogoInfo(String thirdTitle) {
        if (!CheckUtil.isEmpty(thirdTitle)) {
            ivExclusive.setVisibility(VISIBLE);
            tvSceneLogo.setVisibility(VISIBLE);
            tvLogoInfo.setVisibility(INVISIBLE);
            tvSceneLogo.setText(thirdTitle);
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.icon_quality_customers);
            drawable.setBounds(0, 0, UiUtil.dip2px(baseActivity, 20), UiUtil.dip2px(baseActivity, 20));
            tvSceneLogo.setCompoundDrawables(drawable, null, null, null);
            tvSceneLogo.setCompoundDrawablePadding(UiUtil.dip2px(getContext(), 5));
        } else {
            tvSceneLogo.setVisibility(GONE);
            tvLogoInfo.setVisibility(VISIBLE);
            ivExclusive.setVisibility(GONE);
            tvLogoInfo.setText(baseActivity.getString(R.string.home_logo_info));
            Drawable drawableStart = ContextCompat.getDrawable(getContext(), R.drawable.icon_logo_left_small);
            tvLogoInfo.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null);
            tvLogoInfo.setCompoundDrawablePadding(UiUtil.dip2px(getContext(), 5));
        }
    }


    /* isMarginMore 两行时候距离顶部加大
     *
     *text 展示的文字
     */

    private void setTvTitle(boolean isShowWarning, boolean isMarginMore, String text, String textColor, boolean isSmallCard) {
        if (CheckUtil.isEmpty(text)) {
            tvTitle.setVisibility(GONE);
        } else {
            tvTitle.setVisibility(VISIBLE);
        }
        if (isShowWarning) {
            Drawable drawableEnd = ContextCompat.getDrawable(getContext(), R.drawable.icon_little_warning);
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableEnd, null);
            tvTitle.setCompoundDrawablePadding(UiUtil.dip2px(getContext(), 4));
        } else {
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        if (isMarginMore || isSmallCard) {
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else {
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        }
        float margin;
        if (isSmallCard) {
            margin = 26;
        } else {
            margin = isMarginMore ? 43 : 20;
        }
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvTitle.getLayoutParams();
        lp.topMargin = UiUtil.dip2px(getContext(), margin);
        tvTitle.setLayoutParams(lp);
        tvTitle.setText(text);
        try {
            tvTitle.setTextColor(Color.parseColor(textColor));
        } catch (Exception e) {
            tvTitle.setTextColor(baseActivity.getResources().getColor(R.color.white));
        }
    }


   /* @param
    text 显示金额
   */

    private void setTvMoney(String textOld, String text) {
        tvMoney.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
        if (!CheckUtil.isEmpty(textOld) && !CheckUtil.isEmpty(text)) {
            tvMoney.setVisibility(VISIBLE);
            setRollingTv(textOld, text);
            tvMoney.setTypeface(FontCustom.getDinFont(getContext()));
            viewUnderline.setVisibility(VISIBLE);
        } else if (!CheckUtil.isEmpty(text)) {
            tvMoney.setVisibility(VISIBLE);
            tvMoney.setTypeface(FontCustom.getDinFont(getContext()));
            if (CheckUtil.isZero(text)) {
                tvMoney.setText("已全部借出", false);
                tvMoney.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                tvZeroBubble.setVisibility(VISIBLE);
                viewUnderline.setVisibility(GONE);
            } else {
                tvMoney.setText(text, false);
                tvZeroBubble.setVisibility(GONE);
                viewUnderline.setVisibility(VISIBLE);
            }
        } else {
            tvMoney.setVisibility(GONE);
            tvZeroBubble.setVisibility(GONE);
            viewUnderline.setVisibility(GONE);
        }
    }

    //展示气泡
    private void setBubbleData(boolean showBubble, String bubbleText, String bubbleStartColor, String bubbleEndColor) {
        if (!showBubble || CheckUtil.isEmpty(bubbleText)) {
            tvOverdueDay.setVisibility(GONE);
            return;
        } else {
            tvOverdueDay.setVisibility(VISIBLE);
        }
        tvOverdueDay.setText(bubbleText);
        try {
            GradientDrawable drawable = (GradientDrawable) tvOverdueDay.getBackground();
            int[] colors = {Color.parseColor(bubbleStartColor), Color.parseColor(bubbleEndColor)};
            drawable.setColors(colors);
            tvOverdueDay.setBackground(drawable);
        } catch (Exception e) {
            Logger.e("转换颜色异常");
        }

    }

    //临时额度
    private void setExtraInfo(String extSubBubbleText, String extSubBubbleTextColor,
                              String startColor, String endColor,
                              String extSubText, String subTextColor) {
        if (!CheckUtil.isEmpty(extSubBubbleText)) {
            tvExtraBubble.setVisibility(VISIBLE);
            tvExtraBubble.setText(extSubBubbleText);
            try {
                tvExtraBubble.setTextColor(Color.parseColor(extSubBubbleTextColor));
            } catch (Exception e) {
                Logger.e("tvExtraBubble字体色转换颜色异常");
            }
            try {
                GradientDrawable drawable = (GradientDrawable) tvExtraBubble.getBackground();
                int[] colors = {Color.parseColor(startColor), Color.parseColor(endColor)};
                //drawable.setStroke(1, Color.parseColor(stokeColor));
                drawable.setColors(colors);
                tvExtraBubble.setBackground(drawable);
            } catch (Exception e) {
                Logger.e("tvExtraBubble背景色转换颜色异常");
            }

        } else {
            tvExtraBubble.setVisibility(GONE);
        }

        if (!CheckUtil.isEmpty(extSubText)) {
            tvExtraInfo.setVisibility(VISIBLE);
            tvExtraInfo.setText(extSubText);
            ivRightArrow.setVisibility(VISIBLE);
            try {
                tvExtraInfo.setTextColor(Color.parseColor(subTextColor));
            } catch (Exception e) {
                Logger.e("tvExtraInfo字体转换颜色异常");
            }

        } else {
            tvExtraInfo.setVisibility(GONE);
            ivRightArrow.setVisibility(GONE);
        }

    }

    //一般展示利率信息
    private void setSubtextInfo(String subText, String subTextColor, boolean isSmallCard) {
        if (!CheckUtil.isEmpty(subText)) {
            tvRateInfo.setVisibility(VISIBLE);
            tvRateInfo.setText(subText);
            if (isSmallCard) {
                tvRateInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            } else {
                tvRateInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            }
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) llRateExtra.getLayoutParams();
            lp.topMargin = UiUtil.dip2px(getContext(), isSmallCard ? 12 : 10);
            llRateExtra.setLayoutParams(lp);

            try {
                tvRateInfo.setTextColor(Color.parseColor(subTextColor));
            } catch (Exception e) {
                tvRateInfo.setTextColor(baseActivity.getResources().getColor(R.color.white));
            }
        } else {
            tvRateInfo.setVisibility(GONE);
        }
    }

    //卡片底部有数据时展示
    private void setBottomText(String mainInfoText, String mainInfoTextColor) {
        if (!CheckUtil.isEmpty(mainInfoText)) {
            tvOverdueInfo.setVisibility(VISIBLE);
            tvOverdueInfo.setText(mainInfoText);
            try {
                tvOverdueInfo.setTextColor(Color.parseColor(mainInfoTextColor));
            } catch (Exception e) {
                tvOverdueInfo.setTextColor(baseActivity.getResources().getColor(R.color.white));
            }
        } else {
            tvOverdueInfo.setVisibility(GONE);
        }
    }

      /*  *
    @param
    isShow 按钮是否显示
     *
    @param
    text 文字信息*/


    private void setSubmitButtonInfo(boolean isShow, String text) {
        boolean hasLoanOut = !CheckUtil.isEmpty(homeInfo)
                && !CheckUtil.isEmpty(homeInfo.getStatus())
                && homeInfo.getStatus().contains("D")   //额度正常
                && !CheckUtil.isEmpty(mainCardBean)
                && CheckUtil.isZero(mainCardBean.getAmount());
        if (isShow && !hasLoanOut) {
            tvSubmit.setVisibility(VISIBLE);
            tvSubmit.setText(text);
            tvSubmit.setEnabled(true);
        } else {
            tvSubmit.setVisibility(GONE);
        }
    }

    //申额进度
    private void setCreditStepInfo(String creditProcessTitle, List<CreditProcess> creditProcess) {
        if (CheckUtil.isEmpty(creditProcessTitle) || CheckUtil.isEmpty(creditProcess) || creditProcess.size() != 3) {
            rlCreditStep.setVisibility(GONE);
        } else {
            rlCreditStep.setVisibility(VISIBLE);
            postStepEvent("Gh_Home_NoviceGuide_Exposure");
            tvCreditTitle.setText(creditProcessTitle);
            for (int i = 0; i < creditProcess.size(); i++) {
                if (i == 0) {
                    GlideUtils.loadCenterCrop(getContext(), ivCreditStepOne, creditProcess.get(i).getIcon(), R.drawable.logo, R.drawable.logo);
                    tvCreditStepOne.setText(creditProcess.get(i).getText());
                    if ("Y".equals(creditProcess.get(0).getComplete())) {
                        ivCreditOneArrow.setImageResource(R.drawable.icon_credit_highlight);
                        tvCreditStepOne.setTextColor(getResources().getColor(R.color.color_303133));
                        if (!"Y".equals(creditProcess.get(1).getComplete())) {
                            ivCreditLightTwo.startAnim();
                        }
                    } else {
                        ivCreditOneArrow.setImageResource(R.drawable.icon_credit_gray);
                        if (ivCreditLightOne != null) {
                            ivCreditLightOne.startAnim();
                        }
                    }
                } else if (i == 1) {
                    GlideUtils.loadCenterCrop(getContext(), ivCreditStepTwo, creditProcess.get(i).getIcon(), R.drawable.logo, R.drawable.logo);
                    tvCreditStepTwo.setText(creditProcess.get(i).getText());
                    if ("Y".equals(creditProcess.get(1).getComplete())) {
                        ivCreditTwoArrow.setImageResource(R.drawable.icon_credit_highlight);
                        tvCreditStepTwo.setTextColor(getResources().getColor(R.color.color_303133));
                        if (!"Y".equals(creditProcess.get(2).getComplete())) {
                            ivCreditLightThree.startAnim();
                        }
                    } else {
                        ivCreditTwoArrow.setImageResource(R.drawable.icon_credit_gray);
                        tvCreditStepThree.setTextColor(getResources().getColor(R.color.gray_909199));
                    }
                } else if (i == 2) {
                    GlideUtils.loadCenterCrop(getContext(), ivCreditStepThree, creditProcess.get(i).getIcon(), R.drawable.logo, R.drawable.logo);
                    tvCreditStepThree.setText(creditProcess.get(i).getText());
                    if ("Y".equals(creditProcess.get(2).getComplete())) {
                        tvCreditStepThree.setTextColor(getResources().getColor(R.color.color_303133));
                    }
                }
            }


        }

    }

    //首页-新手引导-曝光
    private void postStepEvent(String id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "够花-首页");
        map.put("banner_name", "新手引导区");
        UMengUtil.onEventObject(id, map, pageCode);

    }

    //设置金刚区数据
    private void setKumgangArea(List<ResidentRibbon> residentRibbon) {
        if (CheckUtil.isEmpty(residentRibbon)) {
            rvKumgang.setVisibility(GONE);
        } else {
            rvKumgang.setVisibility(VISIBLE);
            postKumgangExposure(residentRibbon);
            HomeKumgangAdapter adapter = new HomeKumgangAdapter();
            adapter.setNewData(residentRibbon);
            GridLayoutManager manager = new GridLayoutManager(getContext(), 4);
            rvKumgang.setLayoutManager(manager);
            rvKumgang.setAdapter(adapter);
            adapter.addChildClickViewIds(R.id.cl_kumgang);
            adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("page_name_cn", "够花-首页");
                    map.put("banner_name", residentRibbon.get(position).getText());
                    if (!CheckUtil.isEmpty(residentRibbon.get(position).getClickErrorMsg())) {
                        UiUtil.toast(residentRibbon.get(position).getClickErrorMsg());
                        map.put("is_success", "false");
                        map.put("fail_reason", residentRibbon.get(position).getClickErrorMsg());
                    } else {
                        if (CheckUtil.isEmpty(residentRibbon.get(position).getItemAction())) {
                            UiUtil.toast("网络开小差了，请稍后重试~");
                            map.put("is_success", "false");
                            map.put("fail_reason", "server没有返回具体的itemAction");
                        } else {
                            boolean needLoginFromScheme = SchemeHelper.isNeedLoginFromScheme(residentRibbon.get(position).getItemAction());
                            AppApplication.setLoginCallbackTodo(needLoginFromScheme, new LoginCallbackC() {
                                @Override
                                public void onLoginSuccess() {
                                    map.put("is_success", "true");
                                    SchemeHelper.jumpFromScheme(residentRibbon.get(position).getItemAction(), baseActivity, homeInfo.getKumGangRepayList());
                                }
                            });
                        }

                    }
                    UMengUtil.onEventObject("Gh_Home_Vajra_Click", map, pageCode);
                }
            });

        }
    }

    //设置够智选数据
    private void setGzxData(GzxData gzxData) {
        if (!CheckUtil.isEmpty(gzxData) &&
                !CheckUtil.isEmpty(gzxData.getGzxIcon()) &&
                !CheckUtil.isEmpty(gzxData.getGzxUrl())) {
            ivGzx.setVisibility(VISIBLE);
            GlideUtils.loadFit(baseActivity, ivGzx, gzxData.getGzxIcon());
            postGzxEvent("Gh_Home_GzxDiversion_Exposure");
        } else {
            ivGzx.setVisibility(GONE);
        }
    }

    //设置贷超数据
    private void setLoanMarket(List<CreditLifeBorrowBean> market) {
        if (CheckUtil.isEmpty(market)) {
            rlLoanMarket.setVisibility(GONE);
        } else {
            postLoanMarketExposure(market);
            rlLoanMarket.setVisibility(VISIBLE);
            HomeLoanMarketAdapter adapter = new HomeLoanMarketAdapter();
            adapter.setNewData(market);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            rvLoanMarket.setLayoutManager(manager);
            rvLoanMarket.setAdapter(adapter);
            adapter.addChildClickViewIds(R.id.tv_apply);
            adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                @Override
                public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                    CreditLifeBorrowBean marketItem = market.get(position);
                    //根据当前状态进行业务处理   检查当前item状态并进行相应处理
                    creditLifeHelp = new CreditLifeHelp(baseActivity, marketItem);
                    creditLifeHelp.dispatchUniteLogin();
                    Map<String, Object> map = new HashMap<>();
                    map.put("page_name_cn", "够花-首页");
                    map.put("banner_name", "拒量导流区");
                    map.put("partner_name", marketItem.getChannelName());
                    map.put("button_name", "立即申请");
                    UMengUtil.commonClickEvent("Gh_Home_LoanMarket_Click", null, map, getPageCode());
                }
            });

        }
    }

    //Gzx事件
    private void postGzxEvent(String id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "够花-首页");
        map.put("banner_name", "GZX推荐区");
        UMengUtil.onEventObject(id, map, pageCode);
    }

    //贷超曝光事件
    private void postLoanMarketExposure(List<CreditLifeBorrowBean> market) {
        for (CreditLifeBorrowBean bean : market) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("page_name_cn", "够花-首页");
            map.put("banner_name", "拒量导流区");
            map.put("partner_name", bean.getChannelName());
            UMengUtil.onEventObject("Gh_Home_LoanMarket_Exposure", map, pageCode);
        }
    }

    //金刚区曝光事件
    private void postKumgangExposure(List<ResidentRibbon> market) {
        for (ResidentRibbon bean : market) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("page_name_cn", "够花-首页");
            map.put("banner_name", bean.getText());
            UMengUtil.onEventObject("Gh_Home_Vajra_Exposure", map, pageCode);
        }
    }


    //开始刷新
    public void startRefreshState() {
        startRefreshState(false);
    }

    //卡片是否在屏幕上
    public boolean isCardInVisible() {
        if (rlQuotaBack != null) {
            Rect rect = new Rect();
            rlQuotaBack.getGlobalVisibleRect(rect);
            return rect.bottom < UiUtil.dip2px(getContext(), 111); //111为顶部浮窗高度
        }
        return false;
    }

    //设置气泡数据
    public void setBubbleData(String bubbleText) {
        if (CheckUtil.isEmpty(bubbleText)) {
            tvLoginBubble.setVisibility(GONE);
            tvSubmitBubble.setVisibility(GONE);
        } else {
            if (!AppApplication.isLogIn()) {
                tvLoginBubble.setVisibility(VISIBLE);
                tvLoginBubble.setText(bubbleText);
                tvSubmitBubble.setVisibility(GONE);
            } else {
                tvLoginBubble.setVisibility(GONE);
                if (tvSubmit.getVisibility() == VISIBLE && tvSubmit.isEnabled()) {
                    tvSubmitBubble.setVisibility(VISIBLE);
                    tvSubmitBubble.setText(bubbleText);
                } else {
                    tvSubmitBubble.setVisibility(GONE);
                }

            }
        }
    }

    public void startRefreshState(boolean needLoadData) {
        if (!AppApplication.isLogIn()) {
            quotaUnLogin.setVisibility(VISIBLE);
            rlQuota.setVisibility(GONE);
            rlLoanMarket.setVisibility(GONE);
            rvKumgang.setVisibility(GONE);
            ivGzx.setVisibility(GONE);
        } else {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(1);
                }
            }, 800);

        }
        if (needLoadData) {
            RxBus.getInstance().post(new ActionEvent(ActionEvent.MAINFRAGMENT_REFRESH_CREDIT));
        }

    }

    //刷新布局状态
    public void refreshLayoutState() {
        if (!AppApplication.isLogIn()) {
            quotaUnLogin.setVisibility(VISIBLE);
            rlQuota.setVisibility(GONE);
            rlLoanMarket.setVisibility(GONE);
            rvKumgang.setVisibility(GONE);
        } else {
            quotaUnLogin.setVisibility(GONE);
            rlQuota.setVisibility(VISIBLE);
        }

    }

    //设置滚动字符串
    private void setRollingTv(String textOld, String text) {
        tvMoney.setText(textOld);
        tvMoney.setText(text);

        HashMap<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "够花-首页");
        UMengUtil.onEventObject("Gh_Home_Edu_Animation_Exposure", map, pageCode);
    }

    private void setTvMoneyAnim() {
        tvMoney.setAnimationDuration(2000L);
        tvMoney.setCharStrategy(Strategy.SameDirectionAnimation(Direction.SCROLL_DOWN));
        tvMoney.addCharOrder(CharOrder.Number);
        tvMoney.setAnimationInterpolator(new AccelerateDecelerateInterpolator());
    }

    //结束刷新
    public void endRefreshState() {
        rlMoneyView.setVisibility(VISIBLE);
        ivLoading.setVisibility(GONE);
        clearLoadingAnimation();
        tvSubmit.setEnabled(true);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void startAnimation() {
//        Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        animation.setDuration(2000);
//        animation.setRepeatCount(99);//动画的反复次数
//        animation.setFillAfter(true);//设置为true，动画转化结束后被应用
//        ivLoading.startAnimation(animation);//開始动画
        ivLoading.setVisibility(View.VISIBLE);
        ivLoading.setImageAssetsFolder("images/");
        ivLoading.setAnimation("home_quota_loading.json");
        ivLoading.setRepeatCount(Integer.MAX_VALUE);
        ivLoading.playAnimation();
    }

    //结束loading的动画
    private void clearLoadingAnimation() {
        ivLoading.cancelAnimation();//结束动画
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
            if (!CheckUtil.isEmpty(homeInfo.getCardRepayList())) {
                MainEduBorrowUntil.INSTANCE(baseActivity).goRepay(homeInfo.getCardRepayList());
            } else {
                H5LinkJumpHelper.INSTANCE().goH5RepayPage(baseActivity, true);
            }
    }

    private void loadThirdLogoPic(String url) {
        GlideUtils.getNetDrawable(baseActivity, url, new INetResult() {
            @Override
            public <T> void onSuccess(T t, String url) {
                try {
                    Drawable drawable = (Drawable) t;
                    drawable.setBounds(0, 0, UiUtil.dip2px(baseActivity, 20), UiUtil.dip2px(baseActivity, 20));
                    tvLogoInfo.setCompoundDrawables(drawable, null, null, null);
                    tvLogoInfo.setCompoundDrawablePadding(UiUtil.dip2px(getContext(), 5));
                } catch (Exception e) {
                    //
                }
            }

            @Override
            public void onError(BasicResponse error, String url) {

            }
        });
    }

}

