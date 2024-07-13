package com.haiercash.gouhua.fragments.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.CommonSpKey;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.gesture.GesturesSettingActivity;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.activity.personalinfor.PersonalInformationActivity;
import com.haiercash.gouhua.adaptor.AlwaysFuncsAdapter;
import com.haiercash.gouhua.adaptor.PersonJinGangAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.FuncBeans;
import com.haiercash.gouhua.beans.PersonCenterInfo;
import com.haiercash.gouhua.beans.homepage.HomeRepayBean;
import com.haiercash.gouhua.beans.msg.UnReadMessageCount;
import com.haiercash.gouhua.biometriclib.BiometricUntil;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.ClickCouponToUseUtil;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.SchemeHelper;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.UploadEventHelper;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;


/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/6/5<br/>
 * 描    述：个人中心<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class MineFragmentNew extends BaseFragment implements BiometricUntil.BiometricUntilCallBack, OnItemClickListener {
    private int mmRvScrollY;//srcollview滑动的距离
    private int mHeight;
    //会员资源位数据
    private String mBannerName, mBannerCid, mBannerGroupId, mBannerUrl;

    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    @BindView(R.id.rv_always_func)
    RecyclerView rvAlwaysFunc;

    @BindView(R.id.rv_resident_ribbon)
    RecyclerView rvResidentRibbon;

//    @BindView(R.id.message_dot)
//    View pointView;

    @BindView(R.id.img_top_avatar)
    ImageView imgTopAvatar;

    @BindView(R.id.tv_top_name)
    TextView tv_top_name;

    @BindView(R.id.tv_user_num)
    TextView tv_user_num;

    @BindView(R.id.tv_top_num)
    TextView tv_top_num;

    @BindView(R.id.group_top_user_info)
    Group group_top_user_info;

    @BindView(R.id.status_bar)
    View status_bar;

    @BindView(R.id.layout_top_head)
    ConstraintLayout layout_top_head;

    @BindView(R.id.rl_quick_login)
    RelativeLayout rlQuickLogin;

    @BindView(R.id.rl_quick_apply)
    RelativeLayout rlQuickApply;

    @BindView(R.id.tv_quick_info)
    TextView tvQuickInfo;

    @BindView(R.id.tv_quick_apply_info)
    TextView tv_quick_apply_info;

    @BindView(R.id.tv_user_name)
    TextView tv_user_name;

    @BindView(R.id.img_user_avatar)
    ImageView img_user_avatar;

    @BindView(R.id.layout_seven_pay)
    ConstraintLayout layout_seven_pay;

    @BindView(R.id.tv_seven_pay)
    TextView tv_seven_pay;

    @BindView(R.id.tv_yuan)
    TextView tv_yuan;

    @BindView(R.id.tv_yuan_num)
    TextView tv_yuan_num;

    @BindView(R.id.tv_no_payment)
    TextView tv_no_payment;

    @BindView(R.id.tv_repayment_now)
    TextView tv_repayment_now;

    @BindView(R.id.tv_warning_bubble)
    TextView tv_warning_bubble;

    @BindView(R.id.tv_seven_tips)
    TextView tv_seven_tips;

    @BindView(R.id.tv_seven_bubble)
    TextView tv_seven_bubble;

    @BindView(R.id.img_top_bg)
    ImageView img_top_bg;

    @BindView(R.id.iv_quick_apply)
    ImageView iv_quick_apply;

    @BindView(R.id.iv_banner)
    ImageView iv_banner;

    @BindView(R.id.iv_about_us)
    ImageView iv_about_us;

    @BindView(R.id.card_vip)
    CardView card_vip;

    @BindView(R.id.notice_num)
    AppCompatTextView noticeNum;

    @BindView(R.id.layout_num)
    FrameLayout layoutNum;

    @BindView(R.id.srl_refresh)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.img_vip_default_logo)
    ImageView img_vip_default_logo;

    @BindView(R.id.tv_repayment_arrow)
    ImageView tv_repayment_arrow;
    //关于我们
    private String link_about_us;
    //是否开启了会员
    private boolean isVip;
    //是否有七日待还
    private boolean hasPayCount;
    //是否展示底部申额悬浮矿
    private boolean isShowFloatingWindow;
    private String currentToken = "";
    private boolean isFinger; //是否可以开启指纹
    private String loanIsOd; //借据是否逾期 Y 是  N 否
    private String remainDay; //借据是否逾期天数
    private String recently_repay_day; //距离最近还款日天数

    private GridLayoutManager layoutManager, layoutManager2;
    private AlwaysFuncsAdapter funcsAdapter;
    private PersonJinGangAdapter residentRibbonAdapter;

    private List<FuncBeans> alwaysFuncList = new ArrayList<>();
    private List<FuncBeans> residentRibbonList = new ArrayList<>();
    private BiometricUntil biometricUntil;
    private List<HomeRepayBean> homeRepayBeanList; //server返回的还款列表

    protected int getLayoutId() {
        return R.layout.fragment_mine_new;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initEventAndData() {
        setStatusBarTextColor(false);
        //初始化控件
        initView();
        //登录成功后刷新个人中心数据
        RxBus.getInstance().addSubscription(this, RxBus.getInstance().register(ActionEvent.class, (actionEvent -> {
            if (actionEvent.getActionType() == ActionEvent.REFRESHUSERINFO) {
                getPersonCenterInfo();
            }
        })));
        //获取个人中心数据
        getPersonCenterInfo();

        UMengUtil.pageStart("MePage");
    }

    private void initView() {
        //设置常用功能数据
        initData();

        //设置状态栏高度
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) status_bar.getLayoutParams();
        layoutParams.height = SystemUtils.getStatusBarHeight(getActivity());
        status_bar.setLayoutParams(layoutParams);

        //设置字体
        tv_yuan_num.setTypeface(FontCustom.getDinAlternateBoldFont(getActivity()));

        //常用功能
        layoutManager = new GridLayoutManager(getActivity(), 4);
        rvAlwaysFunc.setLayoutManager(layoutManager);
        funcsAdapter = new AlwaysFuncsAdapter(alwaysFuncList);
        funcsAdapter.setOnItemClickListener(this);
        rvAlwaysFunc.setAdapter(funcsAdapter);
        rvAlwaysFunc.setNestedScrollingEnabled(false);

        //金刚位
        layoutManager2 = new GridLayoutManager(getActivity(), 4);
        residentRibbonAdapter = new PersonJinGangAdapter(residentRibbonList);
        residentRibbonAdapter.setOnItemClickListener(this);
        rvResidentRibbon.setLayoutManager(layoutManager2);
        rvResidentRibbon.setAdapter(residentRibbonAdapter);
        rvResidentRibbon.setNestedScrollingEnabled(false);

        //头部设置滑动
        group_top_user_info.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                Logger.e("onScrollChange", "scrollY==" + scrollY);
                mmRvScrollY = scrollY;
                mHeight = layout_top_head.getHeight();
                if (mmRvScrollY <= 0) {//未滑动
                    layout_top_head.setBackgroundColor(Color.argb((int) 0, 255, 255, 255));
                    group_top_user_info.setVisibility(View.GONE);
                } else if (mmRvScrollY > 0 && mmRvScrollY <= mHeight) { //滑动过程中 并且在mHeight之内
                    if (mmRvScrollY <= mHeight / 2) {
                        group_top_user_info.setVisibility(View.GONE);
                    } else {
                        group_top_user_info.setVisibility(View.VISIBLE);
                    }
                    float scale = (float) mmRvScrollY / mHeight;
                    float alpha = (255 * scale);
                    layout_top_head.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                } else {//超过mHeight
                    layout_top_head.setBackgroundColor(Color.argb((int) 255, 255, 255, 255));
                    layout_top_head.setAlpha(1);
                    group_top_user_info.setVisibility(View.VISIBLE);
                }
            });
        }

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getPersonCenterInfo();
            }
        });

    }


    /**
     * 小圆点是否显示
     */
    public void isHidePoint() {
        String number = SpHp.getLogin(SpKey.NOTICE_POINT_OPERATE, "0");
//        pointView.setVisibility("0".equals(number) ? View.GONE : View.VISIBLE);
    }

    @OnClick({R.id.iv_message, R.id.iv_setting, R.id.img_top_avatar, R.id.tv_top_name, R.id.tv_top_num, R.id.img_user_avatar, R.id.tv_user_name, R.id.tv_user_num, R.id.iv_banner,
            R.id.ll_close_quick, R.id.tv_go_setting, R.id.layout_seven_pay, R.id.ll_close_apply, R.id.tv_go_apply, R.id.iv_about_us})
    @Override
    public void onClick(View view) {
        Logger.e("onClick-----");
        switch (view.getId()) {
            case R.id.iv_message://消息中心
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        isHidePoint();
                        ARouterUntil.getContainerInstance(PagePath.FRAGMENT_MESSAGE).navigation();
                        String number = SpHp.getLogin(SpKey.NOTICE_POINT_OPERATE, "0");//0表示没有为读消息
                        UploadEventHelper.postCenterMessageClickEvent("0".equals(number));
                        SpHp.saveSpLogin(SpKey.NOTICE_POINT_OPERATE, "0");
                    }
                });
                break;
            case R.id.iv_setting://设置
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_SETTING).navigation(mActivity, 1);
                UploadEventHelper.postCenterSettingClickEvent();
                break;

            case R.id.img_top_avatar:
            case R.id.tv_top_name:
            case R.id.tv_top_num:
            case R.id.img_user_avatar:
            case R.id.tv_user_name:
            case R.id.tv_user_num:
                Logger.e("头像点击事件");
                if (!AppApplication.isLogIn()) {
                    LoginSelectHelper.staticToGeneralLogin();
                } else {
                    startActivityForResult(new Intent(mActivity, PersonalInformationActivity.class), 1);
                }

                UploadEventHelper.postPersonClickEvent(isVip);
                break;

            case R.id.iv_banner://资源位
                UploadEventHelper.postVipImgClickEvent(isVip);
                if (!TextUtils.isEmpty(mBannerUrl)) {
                    WebHelper.startActivityForUrl(mActivity, mBannerUrl);
                }

                break;
            case R.id.ll_close_quick:
                SpHelper.getInstance().saveMsgToSp(SpKey.QUICK_LOGIN_STATE, SpKey.LATEST_LONG_TOKEN, currentToken);
                rlQuickLogin.setVisibility(View.GONE);
                resetUIData();
                break;
            case R.id.ll_close_apply:
                SpHelper.getInstance().saveMsgToSp(SpKey.QUICK_APPLY_STATE, SpKey.QUICK_APPLY_TOKEN, currentToken);
                rlQuickApply.setVisibility(View.GONE);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetUIData();
                    }
                }, 500);

                break;
            case R.id.tv_go_setting:
                if (isFinger) {
                    toSetBiometric();
                } else {
                    Intent intent = new Intent(mActivity, GesturesSettingActivity.class);
                    intent.putExtra("pageType", "changeGestures");
                    mActivity.startActivity(intent);
                }
                break;

            case R.id.layout_seven_pay: //七日待还&全部待还
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        if (hasPayCount) {
                            SpHp.saveUser(SpKey.USER_STATE_TYPE, "");//保存当前的借款状态
                            UploadEventHelper.postSevenCardClickEvent(loanIsOd, remainDay, "Y".equals(loanIsOd) ? "" : recently_repay_day);
                            MainEduBorrowUntil.INSTANCE(mActivity).goRepay(homeRepayBeanList);
                        } else {
                            UploadEventHelper.postPayEvent("AllNotRepay_Click", "全部待还", loanIsOd);
                            MainEduBorrowUntil.INSTANCE(mActivity).goRepay(homeRepayBeanList);
                        }
                    }
                });
                break;
            case R.id.tv_go_apply://申请额度
                ClickCouponToUseUtil couponToUseUtil = new ClickCouponToUseUtil(mActivity);
                couponToUseUtil.requestUserLoanStatus(false);
                break;

            case R.id.iv_about_us://关于我们
                if (!TextUtils.isEmpty(link_about_us)) {
                    MainHelper.ImageLinkRoute(mActivity, link_about_us);
                } else {
                    String errorMessage = "连接似乎有问题，请检查您的网络设置或刷新";
                    showDialog(errorMessage, "我知道了", "刷新重试", (dialog, which) -> {
                        if (which == 2) {
                            getPersonCenterInfo();
                        }
                    }).setStandardStyle(2);
                }
                UploadEventHelper.postAboutImgClickEvent();
                break;
            default:
                break;
        }
    }


    @Override
    public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        if (adapter instanceof AlwaysFuncsAdapter) {
            Logger.e("onItemClick---AlwaysFuncsAdapter");
            if (alwaysFuncList == null || alwaysFuncList.size() == 0) return;
            FuncBeans item = (FuncBeans) alwaysFuncList.get(position);
            if (!TextUtils.isEmpty(item.getItemAction())) {//服务器下发的数据
                boolean needLoginFromScheme = SchemeHelper.isNeedLoginFromScheme(item.getItemAction());
                AppApplication.setLoginCallbackTodo(needLoginFromScheme, new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        SchemeHelper.jumpFromScheme(item.getItemAction(), getMActivity());
                    }
                });

                UploadEventHelper.postCommonFuncClickEvent(item.getText());
            } else { //走默认值
                String errorMessage = "连接似乎有问题，请检查您的网络设置或刷新";
                if (!TextUtils.isEmpty(item.getClickErrorMsg())) {
                    errorMessage = item.getClickErrorMsg();
                    UploadEventHelper.postCommonFuncClickEvent(item.getText());
                }
                showDialog(errorMessage, "我知道了", "刷新重试", (dialog, which) -> {
                    if (which == 2) {
                        getPersonCenterInfo();
                    }
                }).setStandardStyle(2);
            }
        } else if (adapter instanceof PersonJinGangAdapter) {
            Logger.e("onItemClick---PersonJinGangAdapter");
            if (residentRibbonList == null || residentRibbonList.size() == 0) return;
            FuncBeans item = (FuncBeans) residentRibbonList.get(position);
            if (!TextUtils.isEmpty(item.getItemAction())) {//服务器下发的数据
                boolean needLoginFromScheme = SchemeHelper.isNeedLoginFromScheme(item.getItemAction());
                AppApplication.setLoginCallbackTodo(needLoginFromScheme, new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        SchemeHelper.jumpFromScheme(item.getItemAction(), getMActivity());
                    }
                });
                UploadEventHelper.postGhMeVajraClickEvent(item.getText());
            } else { //走默认值
                String errorMessage = "连接似乎有问题，请检查您的网络设置或刷新";
                if (!TextUtils.isEmpty(item.getClickErrorMsg())) {
                    errorMessage = item.getClickErrorMsg();
                    UploadEventHelper.postGhMeVajraClickEvent(item.getText());
                }
                showDialog(errorMessage, "我知道了", "刷新重试", (dialog, which) -> {
                    if (which == 2) {
                        getPersonCenterInfo();
                    }
                }).setStandardStyle(2);
            }
        }
    }

    /**
     * 获取个人中心数据
     */
    public void getPersonCenterInfo() {
        resetValue();
        if (netHelper != null) {
            Map<String, String> params = new HashMap<>();
//            showProgress(true);
            netHelper.postService(ApiUrl.GET_PERSON_CENTER_INFO, params, PersonCenterInfo.class);
            //弹框
            if (mActivity instanceof MainActivity && !mActivity.isShowingDialog()) {
                controlDialogUtil.checkDialog("mine");
            }
        }
    }

    @Override
    public void onSuccess(Object response, String flag) {
//        showProgress(false);
        if (refreshLayout != null)
            refreshLayout.finishRefresh();
        if (ApiUrl.GET_PERSON_CENTER_INFO.equals(flag)) {
            PersonCenterInfo personCenterInfo = (PersonCenterInfo) response;
            if (personCenterInfo != null) {
                isVip = "Y".equals(personCenterInfo.getHyOpenState()) ? true : false;
                //申额步骤
                isShowFloatingWindow = "1".equals(personCenterInfo.getShowFloatingWindow()) ? true : false;
                if (isShowFloatingWindow && personCenterInfo.getFloatingWindow() != null) {
                    tv_quick_apply_info.setText(personCenterInfo.getFloatingWindow().getText());
                    GlideUtils.loadFit(getActivity(), iv_quick_apply, personCenterInfo.getFloatingWindow().getIcon(), R.drawable.img_quick_apply_money);
                }
                //关于我们
                if (personCenterInfo.getBottom() != null) {
                    GlideUtils.loadFit(getActivity(), iv_about_us, personCenterInfo.getBottom().getImgUrl(), R.drawable.img_about_us);
                    link_about_us = personCenterInfo.getBottom().getForwardUrl();
                }
                //会员
                if (personCenterInfo.getMidRes() != null && personCenterInfo.getMidRes().getData() != null && personCenterInfo.getMidRes().getData().size() > 0
                        && !TextUtils.isEmpty(personCenterInfo.getMidRes().getData().get(0).getImgUrl())) {
                    card_vip.setVisibility(View.VISIBLE);
                    int imgWidth = SystemUtils.getDeviceWidth(getActivity()) - UiUtil.dip2px(getActivity(), 30);
                    GlideUtils.loadForWidth(getActivity(), personCenterInfo.getMidRes().getData().get(0).getImgUrl(), iv_banner, imgWidth
                            ,
                            0, R.drawable.img_vip_default, new INetResult() {
                                @Override
                                public <T> void onSuccess(T t, String url) {
                                    if (iv_banner != null) {
                                        iv_banner.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onError(BasicResponse error, String url) {
                                    if (iv_banner != null) {
                                        iv_banner.setImageResource(R.drawable.img_vip_default);
                                    }
                                }
                            });
//                    GlideUtils.loadFit(getActivity(), iv_banner, personCenterInfo.getMidRes().getData().get(0).getImgUrl(), R.drawable.img_vip_default);
                    mBannerCid = personCenterInfo.getMidRes().getData().get(0).getCid();
                    mBannerGroupId = personCenterInfo.getMidRes().getData().get(0).getGroupId();
                    mBannerName = personCenterInfo.getMidRes().getData().get(0).getName();
                    //跳转路径
                    mBannerUrl = personCenterInfo.getMidRes().getData().get(0).getForwardUrl();

                    UploadEventHelper.postVipImgShowEvent(isVip);

                } else {
                    card_vip.setVisibility(View.GONE);
                }
                //常用功能
                if (personCenterInfo.getCommonRibbon() != null && personCenterInfo.getCommonRibbon().size() > 0) {
                    alwaysFuncList.clear();
                    alwaysFuncList.addAll(personCenterInfo.getCommonRibbon());
                    funcsAdapter.notifyDataSetChanged();
                }
                //金刚位
                if (personCenterInfo.getResidentRibbon() != null && personCenterInfo.getResidentRibbon().size() > 0) {
                    residentRibbonList.clear();
                    residentRibbonList.addAll(personCenterInfo.getResidentRibbon());
                    residentRibbonAdapter.notifyDataSetChanged();
                }
                //七日待还数据
                if (personCenterInfo.getRepayCard() == null
                        || (TextUtils.isEmpty(personCenterInfo.getRepayCard().getText())
                        && TextUtils.isEmpty(personCenterInfo.getRepayCard().getRepayDesc()))) {
                    layout_seven_pay.setVisibility(View.GONE);
                    homeRepayBeanList = null;
                } else {
                    layout_seven_pay.setVisibility(View.VISIBLE);
                    //是否逾期
                    loanIsOd = personCenterInfo.getRepayCard().getLoanIsOd();
                    remainDay = personCenterInfo.getRepayCard().getRemainDay();
                    homeRepayBeanList = personCenterInfo.getRepayCard().getRepayList();
                    recently_repay_day = personCenterInfo.getRepayCard().getSubText();
                    UploadEventHelper.postSevenCardShowEvent(loanIsOd, personCenterInfo.getRepayCard().getRemainDay(), "Y".equals(loanIsOd) ? "" : recently_repay_day);
                    //主标题
                    if (TextUtils.isEmpty(personCenterInfo.getRepayCard().getText())) {
                        tv_seven_pay.setVisibility(View.GONE);
                    } else {
                        tv_seven_pay.setText(personCenterInfo.getRepayCard().getText());
                        if (!TextUtils.isEmpty(personCenterInfo.getRepayCard().getTextColor()))
                            tv_seven_pay.setTextColor(Color.parseColor(personCenterInfo.getRepayCard().getTextColor()));
                    }

                    //金额
                    if (TextUtils.isEmpty(personCenterInfo.getRepayCard().getAmount())) {
                        tv_yuan.setVisibility(View.GONE);
                        tv_yuan_num.setVisibility(View.GONE);
                        tv_no_payment.setVisibility(View.VISIBLE);
                        if (!CheckUtil.isEmpty(personCenterInfo.getRepayCard().getRepayDesc())) {
                            tv_no_payment.setText(personCenterInfo.getRepayCard().getRepayDesc());
                            tv_repayment_arrow.setVisibility(View.VISIBLE);
                            tv_repayment_now.setVisibility(View.GONE);
                        } else {
                            tv_repayment_arrow.setVisibility(View.GONE);
                            tv_repayment_now.setVisibility(View.VISIBLE);
                            String btnText = TextUtils.isEmpty(personCenterInfo.getRepayCard().getBtnText()) ? "查看全部待还" : personCenterInfo.getRepayCard().getBtnText();
                            tv_repayment_now.setText(btnText);
                            tv_repayment_now.setTextColor(Color.parseColor("#606166"));
                            tv_repayment_now.setBackgroundResource(R.drawable.bg_bfc2cc_radius19);
                        }
                        hasPayCount = false;
                    } else {
                        tv_yuan.setVisibility(View.VISIBLE);
                        tv_yuan_num.setVisibility(View.VISIBLE);
                        if (!CheckUtil.isEmpty(personCenterInfo.getRepayCard().getRepayDesc())) {
                            tv_no_payment.setVisibility(View.VISIBLE);
                            tv_no_payment.setText(personCenterInfo.getRepayCard().getRepayDesc());
                            tv_repayment_arrow.setVisibility(View.VISIBLE);
                            tv_repayment_now.setVisibility(View.GONE);
                        } else {
                            tv_no_payment.setVisibility(View.GONE);
                            tv_repayment_arrow.setVisibility(View.GONE);
                            tv_repayment_now.setVisibility(View.VISIBLE);
                        }
                        tv_yuan_num.setText(personCenterInfo.getRepayCard().getAmount());
                        String btnText = TextUtils.isEmpty(personCenterInfo.getRepayCard().getBtnText()) ? "立即还款" : personCenterInfo.getRepayCard().getBtnText();
                        tv_repayment_now.setText(btnText);
                        tv_repayment_now.setTextColor(UiUtil.getColor(R.color.colorPrimary));
                        tv_repayment_now.setBackgroundResource(R.drawable.bg_primary_color_radius19);
                        hasPayCount = true;
                    }
                    //主气泡
                    if (0 == personCenterInfo.getRepayCard().getShowBubble() || TextUtils.isEmpty(personCenterInfo.getRepayCard().getBubbleText())) {
                        tv_warning_bubble.setVisibility(View.GONE);
                    } else {
                        tv_warning_bubble.setVisibility(View.VISIBLE);
                        tv_warning_bubble.setText(personCenterInfo.getRepayCard().getBubbleText());
                        String startColor = TextUtils.isEmpty(personCenterInfo.getRepayCard().getBubbleStartColor()) ? "#FF754A" : personCenterInfo.getRepayCard().getBubbleStartColor();
                        String endColor = TextUtils.isEmpty(personCenterInfo.getRepayCard().getBubbleEndColor()) ? "#FF2727" : personCenterInfo.getRepayCard().getBubbleEndColor();
                        CommomUtils.setBG(getActivity(), tv_warning_bubble, startColor, endColor, "", 9F, 0.5F);
                    }

                    //副标题
                    if (TextUtils.isEmpty(personCenterInfo.getRepayCard().getAmount()) || TextUtils.isEmpty(personCenterInfo.getRepayCard().getSubText())) {
                        tv_seven_tips.setVisibility(View.GONE);
                    } else {
                        tv_seven_tips.setVisibility(View.VISIBLE);
                        tv_seven_tips.setText(personCenterInfo.getRepayCard().getSubText());
                        if (!TextUtils.isEmpty(personCenterInfo.getRepayCard().getSubTextColor())) {
                            tv_seven_tips.setTextColor(Color.parseColor(personCenterInfo.getRepayCard().getSubTextColor()));
                        } else {
                            tv_seven_tips.setTextColor(Color.parseColor("#909199"));
                        }
                    }
                    //副标题气泡
                    if (0 == personCenterInfo.getRepayCard().getShowSubBubble() || TextUtils.isEmpty(personCenterInfo.getRepayCard().getSubBubbleText())) {
                        tv_seven_bubble.setVisibility(View.GONE);
                    } else {
                        tv_seven_bubble.setVisibility(View.VISIBLE);
                        tv_seven_bubble.setText(personCenterInfo.getRepayCard().getSubBubbleText());
                        String startColor = TextUtils.isEmpty(personCenterInfo.getRepayCard().getSubBubbleStartColor()) ? "#E5EDFF" : personCenterInfo.getRepayCard().getSubBubbleStartColor();
                        String endColor = TextUtils.isEmpty(personCenterInfo.getRepayCard().getSubBubbleEndColor()) ? "#E5EDFF" : personCenterInfo.getRepayCard().getSubBubbleEndColor();
                        String strokeColor = TextUtils.isEmpty(personCenterInfo.getRepayCard().getSubBubbleStrokeColor()) ? "#1555FF" : personCenterInfo.getRepayCard().getSubBubbleStrokeColor();
                        CommomUtils.setBG(getActivity(), tv_seven_bubble, startColor, endColor, strokeColor, 9F, 0.5F);
                    }
                }

            }

            // 刷新下布局
            resetUIData();
        } else if (ApiUrl.GET_UNREAD_MESSAGE_COUNT.equals(flag)) {
            UnReadMessageCount data = (UnReadMessageCount) response;
            if (data != null) {
                if (data.getTotal() <= 0) {
                    layoutNum.setVisibility(View.GONE);
                } else if (data.getTotal() < 10) {
                    layoutNum.setVisibility(View.VISIBLE);
                    noticeNum.setText(data.getTotal() + "");
                    noticeNum.setBackgroundResource(R.drawable.shape_circle12_ff5151);
                } else if (data.getTotal() >= 10) {
                    layoutNum.setVisibility(View.VISIBLE);
                    if (data.getTotal() < 100) {
                        noticeNum.setText(data.getTotal() + "");
                    } else {
                        noticeNum.setText("99+");
                    }
                    noticeNum.setBackgroundResource(R.drawable.shape_messge_notice_num_bg);
                }
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        showProgress(false);
        if (refreshLayout != null)
            refreshLayout.finishRefresh();
        if (ApiUrl.GET_PERSON_CENTER_INFO.equals(url)) {
            // 刷新下布局
            resetUIData();
        }
    }

    @OnLongClick({R.id.iv_setting})
    public void longClickEvent(View view) {
        if (view.getId() == R.id.iv_setting) {
            if (!BuildConfig.IS_RELEASE) {
                CommomUtils.changeEnvironment(getMActivity());
            }
        }
    }

    /**
     * 获取默认数据
     */
    private void initData() {
        //常用功能
        if (alwaysFuncList != null) {
            alwaysFuncList.clear();
            alwaysFuncList.addAll(CommomUtils.getDefaultAlwaysFunsList());
        }
        //金刚位
        if (residentRibbonList != null) {
            residentRibbonList.clear();
            residentRibbonList.addAll(CommomUtils.getDefaultResidentTibbonList());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.setStatusBarTextColor(true);
        //刷新个人中心数据
        getPersonCenterInfo();
        //获取未读消息数量
        getUnReadMessageNum();
    }

    /**
     * 未读消息数量
     */
    public void getUnReadMessageNum() {
        if (AppApplication.isLogIn()) {
            Map<String, String> map = new HashMap<>();
            netHelper.postService(ApiUrl.GET_UNREAD_MESSAGE_COUNT, map, UnReadMessageCount.class);
        } else {
            layoutNum.setVisibility(View.GONE);
        }
    }

    private String getPageName() {
        return "我的页";
    }

    @Override
    public void onDestroyView() {
        RxBus.getInstance().unSubscribe(this);
        UMengUtil.pageEnd("MePage");
        super.onDestroyView();
    }

    @Override
    protected String getPageCode() {
        return "MePage";
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mActivity.setStatusBarTextColor(true);
        } else {
            mActivity.setStatusBarTextColor(false);
        }
    }

    /**
     * 设置控件状态
     */
    private void resetUIData() {
        if (AppApplication.isLogIn()) {
            if (isShowQuickApplyLayout()) {
                rlQuickApply.setVisibility(View.VISIBLE);
                rlQuickLogin.setVisibility(View.GONE);
            } else {
                //指纹判断
                checkQuickLogin();
                rlQuickApply.setVisibility(View.GONE);
            }

            if (CommomUtils.isRealName()) { //实名
                tv_user_name.setText(CheckUtil.getNameOnlyLastWords(SpHp.getUser(SpKey.USER_CUSTNAME)));
                tv_top_name.setText(CheckUtil.getNameOnlyLastWords(SpHp.getUser(SpKey.USER_CUSTNAME)));
            } else {
                tv_user_name.setText("Hi,你好");
                tv_top_name.setText("Hi,你好");
            }
            tv_user_num.setText(CheckUtil.hidePhoneNumber(SpHp.getLogin(SpKey.LOGIN_MOBILE)));
            tv_top_num.setText(CheckUtil.hidePhoneNumber(SpHp.getLogin(SpKey.LOGIN_MOBILE)));

        } else {//未登录
            layout_seven_pay.setVisibility(View.GONE);
            rlQuickLogin.setVisibility(View.GONE);
            rlQuickApply.setVisibility(View.GONE);
            tv_top_num.setText("");
            tv_top_num.setVisibility(View.GONE);
            tv_user_num.setText("登录可享更多精彩");
            tv_user_name.setText("登录/注册");
            tv_top_name.setText("登录可享更多精彩");
        }

        //设置头像
        if (AppApplication.isLogIn() && !CheckUtil.isEmpty(AppApplication.userid)) {
            String url = ApiUrl.urlCustImage + "?userId=" + EncryptUtil.simpleEncrypt(AppApplication.userid);
            GlideUtils.loadHeadPortrait(getContext(), url, img_user_avatar, img_user_avatar.getDrawable(), true);
            GlideUtils.loadHeadPortrait(getContext(), url, imgTopAvatar, imgTopAvatar.getDrawable(), true);
        } else {
            img_user_avatar.setImageResource(R.drawable.img_user_avatar);
            imgTopAvatar.setImageResource(R.drawable.img_user_avatar);
        }
        if (isVip) {
            img_top_bg.setImageResource(R.drawable.img_top_vip_bg);
            img_vip_default_logo.setVisibility(View.VISIBLE);
            img_user_avatar.setBackgroundResource(R.drawable.bg_circle_yellow);
        } else {
            img_vip_default_logo.setVisibility(View.GONE);
            img_top_bg.setImageResource(R.drawable.img_mine_top_bg);
            img_user_avatar.setBackgroundResource(R.drawable.bg_circle_white);
        }
        //消息小圆点
        isHidePoint();

    }

    /**
     * 重置数据
     */
    public void resetValue() {
        isVip = false;
        isShowFloatingWindow = false;
        link_about_us = "";
        mBannerName = "";
        mBannerCid = "";
        mBannerGroupId = "";
        mBannerUrl = "";
        loanIsOd = "";
        remainDay = "";
        recently_repay_day = "";
    }


    //检查是否需要展示快捷登录弹窗
    private void checkQuickLogin() {
        //是否支持指纹
        boolean isSupport = new BiometricUntil(mActivity, null).isBiometricPromptEnable();
        boolean hasSetBiometric = LoginSelectHelper.hasSetBiometric();
        currentToken = SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_TOKEN_CLIENT_SECRET);
        String latestToken = SpHelper.getInstance().readMsgFromSp(SpKey.QUICK_LOGIN_STATE, SpKey.LATEST_LONG_TOKEN);


        if (!LoginSelectHelper.hasSetGesture() && !hasSetBiometric && !currentToken.equals(latestToken)) {
            rlQuickLogin.setVisibility(View.VISIBLE);
            if (isSupport) {
                isFinger = true;
                tvQuickInfo.setText(getString(R.string.quick_login_biometric_hint));
            } else {
                isFinger = false;
                tvQuickInfo.setText(getString(R.string.quick_login_gesture_hint));
            }
        } else {
            rlQuickLogin.setVisibility(View.GONE);
        }
    }

    /**
     * 是否展示申额提示框
     *
     * @return
     */
    private boolean isShowQuickApplyLayout() {
        currentToken = SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_TOKEN_CLIENT_SECRET);
        String latestToken = SpHelper.getInstance().readMsgFromSp(SpKey.QUICK_APPLY_STATE, SpKey.QUICK_APPLY_TOKEN);
        if (!currentToken.equals(latestToken) && isShowFloatingWindow) {
            return true;
        }
        return false;
    }


    /**
     * 引导去设置指纹（前置条件是系统硬件是否支持指纹识别BiometricUntil.isHardwareDetected）
     */
    private void toSetBiometric() {
        //每次都要初始化，不然再次调起是无效的
        if (biometricUntil != null) {
            biometricUntil.cancelAuthenticate();
            biometricUntil = null;
        }
        biometricUntil = new BiometricUntil(mActivity, this);
        biometricUntil.showBiometricLibPop();
        UMengUtil.pageStart("SetFingerprintPage");
    }

    @Override
    public void biometricSwitch(boolean isOpening) {

    }

    @Override
    public void onSuccess(boolean isOpenFingerprint) {
        rlQuickLogin.setVisibility(View.GONE);
        UploadEventHelper.postUmEventWithFingerprintResult(true, "");
        UMengUtil.pageEnd("SetFingerprintPage");
    }

    @Override
    public void onFailed(Integer errorCode, String errorReason) {
        UploadEventHelper.postUmEventWithFingerprintResult(false, errorReason);
    }

    @Override
    public void onErrorForMoreFailed() {
        //5次及以上指纹验证失败
        UMengUtil.pageEnd("SetFingerprintPage");
        mActivity.showDialog("指纹验证失败", "验证失败次数过多，请稍后重试～", null);
    }

    @Override
    public void onCancel() {
        UMengUtil.pageEnd("SetFingerprintPage");
    }


}
