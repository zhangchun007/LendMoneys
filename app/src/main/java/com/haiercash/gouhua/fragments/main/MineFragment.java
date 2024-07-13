package com.haiercash.gouhua.fragments.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

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
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.RePayAndRecordActivity;
import com.haiercash.gouhua.activity.SplashActivity;
import com.haiercash.gouhua.activity.bankcard.MyCreditCardActivity;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.activity.comm.ResourceHelper;
import com.haiercash.gouhua.activity.gesture.GesturesSettingActivity;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.activity.personalinfor.PersonalInformationActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.BannerBean2;
import com.haiercash.gouhua.beans.MyLoanBean;
import com.haiercash.gouhua.beans.MySevenPayBean;
import com.haiercash.gouhua.beans.ResourceBean;
import com.haiercash.gouhua.beans.homepage.ImageLinkBean;
import com.haiercash.gouhua.bill.AllBillsFragment;
import com.haiercash.gouhua.bill.PeriodBillsFragment;
import com.haiercash.gouhua.biometriclib.BiometricUntil;
import com.haiercash.gouhua.hybrid.H5LinkJumpHelper;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.AIServer;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.PointView;

import java.util.HashMap;
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
public class MineFragment extends BaseFragment implements BiometricUntil.BiometricUntilCallBack {
    private int savedIndex = 0;
    private int checkedIndex = 0;

    private final String[] environmentList = {"https://shop-p2.haiercash.com/", "https://shop-stg.haiercash.com/", "https://shop.haiercash.com/"};
    @BindView(R.id.ivHeadPortrait)
    ImageView ivHeadPortrait;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tvPhoneNo)
    TextView tvPhoneNo;
    @BindView(R.id.cl_banner)
    ConstraintLayout clBanner;
    @BindView(R.id.iv_banner)
    ImageView ivBanner;
    @BindView(R.id.iv_bottom)
    ImageView ivBottom;
    @BindView(R.id.mine_sv_view)
    NestedScrollView rootView;  //根布局
    @BindView(R.id.view_head_back)
    View headBackView;  //头布局背景
    @BindView(R.id.tv_title)
    TextView tvTitle;  //头布局背景
    @BindView(R.id.rl_seven_pay)
    RelativeLayout rlSevenPay;  //七日待还布局
    @BindView(R.id.tv_seven_title)
    TextView tvSevenTitle;//七日待还布局title
    @BindView(R.id.tv_seven_amount)
    TextView tvSevenAmount;//七日待还金额
    @BindView(R.id.tv_seven_go_pay)
    TextView tvSevenGoPay;//七日待还布局按钮
    @BindView(R.id.ll_my_loan)
    LinearLayout llMyLoan;  //我的借款布局
    @BindView(R.id.tv_check_all)
    TextView tvCheckAll;//我的借款查看全部按钮
    @BindView(R.id.tv_mine_to_commit)
    TextView tvCommit;//待提交
    @BindView(R.id.tv_mine_under_approval)
    TextView tvUnderApproval;//审批中
    @BindView(R.id.tv_mine_to_repay)
    TextView tvToRepay;//待还款
    @BindView(R.id.tv_order)
    TextView tvOrder;//订单
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.rl_quick_login)
    RelativeLayout rlQuickLogin;//快捷登录提示弹窗
    @BindView(R.id.tv_go_setting)
    TextView tvGoSetting;//去设置
    @BindView(R.id.ll_close_quick)
    LinearLayout llClose;//关闭快捷登录提示弹窗
    @BindView(R.id.tv_quick_info)
    TextView tvQuickInfo;

    @BindView(R.id.tv_red_bag)
    TextView tvRedBag; //红包


    private PointView pointView;
    private boolean isFinger; //是否可以开启指纹
    /**
     * -1：变量初始化状态，界面初始化时不存在该值<br/>
     * 0：UI默认状态，未登录<br/>
     * 1：UI已登录但是未实名状态<br/>
     * 2：UI已实名状态<br/>
     * 3：UI账户切换<br/>
     */
    private String tvNameTag = "-1";
    private AlertDialog alertDialog;
    private ImageLinkBean bottomImg;  //接口返回的底部图片
    private MyLoanBean myLoanBean;//我的借款返回的数据
    private MySevenPayBean mySevenPayBean;//七日待还数据
    private String mBannerJumpUrl;//资源位跳转链接
    private String mBannerName, mBannerCid, mBannerGroupId;
    private String currentToken = "";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initEventAndData() {
        setStatusBarTextColor(false);
        pointView = new PointView(mActivity);
        resetUIData();
        GlideUtils.loadCenterCropRadius(getContext(), ivBottom, "", R.drawable.bg_mine_gohua_info, GlideUtils.ALL, 8);
        rootView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY <= UiUtil.dip2px(mActivity, 65)) {
                    //float alpha = (float) scrollY / UiUtil.dip2px(mActivity, 65);
                    headBackView.setAlpha(0);
                    tvTitle.setAlpha(0);
                    //System.out.println("onScrollChange-->A: " + alpha);
                } else {
                    headBackView.setAlpha(1);
                    tvTitle.setAlpha(1);
                    // headBack.setAlpha(1);

                }
            }
        });
        mView.findViewById(R.id.tv_tmp).setVisibility(BuildConfig.IS_RELEASE ? View.GONE : View.VISIBLE);
        mView.findViewById(R.id.view_lastLine).setVisibility(BuildConfig.IS_RELEASE ? View.GONE : View.VISIBLE);

        RxBus.getInstance().addSubscription(this, RxBus.getInstance().register(ActionEvent.class, (actionEvent -> {
            if (actionEvent.getActionType() == ActionEvent.REFRESHUSERINFO) {
                resetUIData();
            }
        })));
    }

    //检查是否需要展示快捷登录弹窗
    private void checkQuickLogin() {
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

    @Override
    public void onResume() {
        super.onResume();
        mActivity.setStatusBarTextColor(true);
        resetUIData();
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

           /* if ("22".equals(SpHp.getUser(SpKey.USER_STATE_TYPE))) {
                pointView.setPointTopRight20(tvPayMoney, 5, 0);
            }*/
        } else {
            mActivity.setStatusBarTextColor(false);
        }
    }

    private void resetUIData() {
        if (AppApplication.isLogIn()) {
            if (CommomUtils.isRealName()) {
                tvName.setTag("2");
            } else {
                tvName.setTag("1");
            }
            //账号切换的状态
            if (!CheckUtil.hidePhoneNumber(SpHp.getLogin(SpKey.LOGIN_MOBILE)).equals(tvPhoneNo.getText().toString())) {
                tvNameTag = "-1";
                tvName.setTag("3");
            }
            getMyData();
            checkQuickLogin();

        } else {
            tvName.setTag("0");
            rlSevenPay.setVisibility(View.GONE);
            rlQuickLogin.setVisibility(View.GONE);
        }
        if (!tvNameTag.equals(tvName.getTag())) {
            initView();
        }
        tvNameTag = String.valueOf(tvName.getTag());

        /*if ("22".equals(SpHp.getUser(SpKey.USER_STATE_TYPE))) {
            pointView.setPointTopRight15(tvPayMoney, 0, 0);
        }*/

        if (AppApplication.isLogIn() && !CheckUtil.isEmpty(AppApplication.userid)) {
            String url = ApiUrl.urlCustImage + "?userId=" + EncryptUtil.simpleEncrypt(AppApplication.userid);
            GlideUtils.loadHeadPortrait(getContext(), url, ivHeadPortrait, true);
        } else {
            ivHeadPortrait.setImageResource(R.drawable.iv_head_img);
        }
        ResourceHelper.requestMineResource(netHelper);
        if (mActivity instanceof MainActivity && !mActivity.isShowingDialog()) {
            controlDialogUtil.checkDialog("mine");
        }

        String redBagSwitch = SpHp.getOther(SpKey.RED_BAG_SWTICH);
        String redBagUrl = SpHp.getOther(SpKey.RED_BAG_URL);
        if ("Y".equals(redBagSwitch) && !TextUtils.isEmpty(redBagUrl)) {
            tvRedBag.setVisibility(View.VISIBLE);
        } else {
            tvRedBag.setVisibility(View.GONE);
        }

    }

    //获取七日待还和我的借款数据
    private void getMyData() {
        netHelper.getService(ApiUrl.GET_SEVEN_PAY, null, MySevenPayBean.class);
        netHelper.getService(ApiUrl.GET_MY_LOAN_URL, null, MyLoanBean.class);

    }

    /**
     * 界面设置
     */
    private void initView() {
        if (AppApplication.isLogIn()) {
            if (CommomUtils.isRealName()) {
                tvPhoneNo.setVisibility(View.VISIBLE);
                tvName.setVisibility(View.VISIBLE);
                //if ("vivo".equals(Build.MANUFACTURER)) {
                tvPhoneNo.setTextSize(UiUtil.sp2px(mActivity, 4.5f));
                //} else {
                //     tvPhoneNo.setTextSize(UiUtil.sp2px(mActivity, 6.0f));
                //}
                tvPhoneNo.setText(CheckUtil.hidePhoneNumber(SpHp.getLogin(SpKey.LOGIN_MOBILE)));
                tvName.setText(CheckUtil.getName(SpHp.getUser(SpKey.USER_CUSTNAME)));
            } else {
                tvName.setVisibility(View.GONE);
                // if ("vivo".equals(Build.MANUFACTURER)) {
                tvPhoneNo.setTextSize(UiUtil.sp2px(mActivity, 4.5f));
                //} else {
                //   tvPhoneNo.setTextSize(UiUtil.sp2px(mActivity, 6.5f));
                //}
                tvPhoneNo.setVisibility(View.VISIBLE);
                tvPhoneNo.setText(CheckUtil.hidePhoneNumber(SpHp.getLogin(SpKey.LOGIN_MOBILE)));
            }

        } else {
            tvPhoneNo.setVisibility(View.GONE);
            tvName.setText("请登录");
            tvName.setVisibility(View.VISIBLE);
        }
        isHidePoint();
        if (AppApplication.isLogIn() && !CheckUtil.isEmpty(AppApplication.userid)) {
            String url = ApiUrl.urlCustImage + "?userId=" + EncryptUtil.simpleEncrypt(AppApplication.userid);
            GlideUtils.loadHeadPortrait(getContext(), url, ivHeadPortrait, true);
        } else {
            ivHeadPortrait.setImageResource(R.drawable.iv_head_img);
        }
        showProgress(true);
        netHelper.getService(ApiUrl.MINE_BACKGROUND_IMG, null, BannerBean2.class);
        netHelper.getService(ApiUrl.BOTTOM_BANNER, null, ImageLinkBean.class);
    }

    public void isHidePoint() {
        String number = SpHp.getLogin(SpKey.NOTICE_POINT_OPERATE, "0");
        pointView.setVisibility("0".equals(number) ? View.GONE : View.VISIBLE);
    }

    @OnClick({R.id.ivHeadPortrait, R.id.llName, R.id.iv_setting, R.id.iv_mine_message, R.id.tv_person_center, R.id.tv_customer_service, R.id.tv_tmp, R.id.iv_bottom, R.id.tv_seven_go_pay, R.id.tv_check_all, R.id.tv_mine_to_commit, R.id.tv_mine_under_approval, R.id.tv_mine_to_repay, R.id.tv_order, R.id.tv_ticket, R.id.tv_loan_record, R.id.tv_tool, R.id.iv_banner, R.id.tv_go_setting, R.id.ll_close_quick, R.id.tv_red_bag})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivHeadPortrait:
            case R.id.llName:
                Logger.e("头像点击事件");
                if (!AppApplication.isLogIn()) {
                    LoginSelectHelper.staticToGeneralLogin();
                } else {
                    startActivityForResult(new Intent(mActivity, PersonalInformationActivity.class), 1);
                }
                break;
            case R.id.iv_banner://资源位
                UMengUtil.commonClickBannerEvent("MePageAdPosition_Click", getPageName(), mBannerName, mBannerCid, mBannerGroupId, getPageCode());
                WebHelper.startActivityForUrl(mActivity, mBannerJumpUrl);
                break;
            case R.id.iv_setting://设置
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_SETTING).navigation(mActivity, 1);
                break;
            case R.id.iv_mine_message://消息中心
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        SpHp.saveSpLogin(SpKey.NOTICE_POINT_OPERATE, "0");
                        isHidePoint();
                        ARouterUntil.getContainerInstance(PagePath.FRAGMENT_MESSAGE).navigation();
                    }
                });
                break;
            case R.id.tv_person_center://个人中心
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        startActivityForResult(new Intent(mActivity, PersonalInformationActivity.class), 1);
                    }
                });
                break;
            case R.id.tv_ticket://券包
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        ARouterUntil.getInstance(PagePath.ACTIVITY_COUPON_BAG).navigation();
                    }
                });
                break;

            case R.id.tv_red_bag://红包
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        String url = SpHp.getOther(SpKey.RED_BAG_URL);
                        if (!TextUtils.isEmpty(url)) {
                            Intent intent = new Intent(getActivity(), JsWebBaseActivity.class);
                            intent.putExtra("jumpKey", url);
                            intent.putExtra("isHideCloseIcon", true);
                            startActivity(intent);
                        } else {
                            showDialog("网络异常，请稍后重试");
                        }
                    }
                });
                break;
            case R.id.tv_loan_record://交易记录
                postUmEvent("BorrowRepayRecord_Click", "借还记录");
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        startActivity(new Intent(mActivity, RePayAndRecordActivity.class));
                    }
                });
                break;
            case R.id.tv_contract: //签约渠道
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        startActivity(new Intent(mActivity, MyCreditCardActivity.class));
                    }
                });
                break;
            case R.id.tv_customer_service: //客服
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        AIServer.showAiWebServer(mActivity, "我的");
                    }
                });
                break;
            case R.id.tv_tmp:
//                startActivity(new Intent(mActivity, NameAuthStartActivity.class));
                //ContainerActivity.to(mActivity, RegisterFragment.ID, null);
//                requestPermission(aBoolean -> {
//                    UiUtil.toast("a:" + aBoolean);
//                    RiskNetServer.startRiskServer(mActivity, "apply_submit_success", "123456789", 4);
//                }, 0, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS);
                //CallPhoneNumberHelper.callCustomerService(mActivity, view);
//                UiUtil.toast("测试toastffffffff", true);
//                ActivityUntil.openFilePath(mActivity, Logger.getFilePath());
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_TMP).navigation();
                //UiUtil.toastLongTime("文件目录：" + Logger.getFilePath());
//                startActivity(new Intent(mActivity, GoBorrowMoneyActivity.class));
                //startActivity(new Intent(mActivity, ScanQrCodeActivity.class));
                //Uri的格式如下：scheme://host:port/path
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("gouhua://com.haiercash")));//短链接的链接
                //new RegisterAgreementPopupWindow(mActivity, null).showAtLocation(view);
                //startActivity(new Intent(mActivity, PerfectInfoActivity.class));
                //Intent intent = new Intent(mActivity, EduProgressActivity.class);
                //intent.putExtra("applSeq", "13310975");
                //startActivity(intent);
                //ARouterUntil.getContainerInstance(PagePath.FRAGMENT_DIVERSION).put("isShowTitle", false).navigation();
                //ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HOME_OTHER_WEB)
                //        .put("isShowTitle", false).put("url", "https://developer.android.google.cn/").navigation();
                //TokenHelper.getInstance().refreshTokenWhenTokenInvalid();
                //ARouterUntil.getContainerInstance(PagePath.FRAGMENT_DIVERSION).put("isShowTitle", false).navigation();

//                LoginSelectHelper.staticToGeneralLogin(getMActivity());
                break;
            case R.id.iv_bottom:
                if (bottomImg != null && !TextUtils.isEmpty(bottomImg.getForwardUrl())) {
                    MainHelper.ImageLinkRoute(mActivity, bottomImg.getForwardUrl());
                }
                break;
            case R.id.tv_seven_go_pay:
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        if (mySevenPayBean != null) {
                            if (mySevenPayBean.getBtnStatus() == 0) {
                                SpHp.saveUser(SpKey.USER_STATE_TYPE, "");//保存当前的借款状态

                                postPayEvent("SevenNotRepay_Click", "7日待还");
                                if (AppApplication.enableRepay) {
                                    H5LinkJumpHelper.INSTANCE().goH5RepayPage(mActivity);
                                } else {
                                    ContainerActivity.to(mActivity, PeriodBillsFragment.ID, null);
                                }
                            } else {
                                postPayEvent("AllNotRepay_Click", "全部待还");
                                if (AppApplication.enableRepay) {
                                    H5LinkJumpHelper.INSTANCE().goH5RepayPage(mActivity);
                                } else {
                                    ContainerActivity.to(mActivity, AllBillsFragment.ID);
                                }
                            }
                        } else {
                            showDialog("网络异常，请稍后重试");
                        }
                    }
                });
                break;
            case R.id.tv_check_all:
                postUmEvent("SeeAll_Click", "查看全部");
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        if (myLoanBean != null && myLoanBean.getLoanAll() != null) {
                            goMyLoan(myLoanBean.getLoanAll().getUrl());
                        } else {
                            showDialog("网络异常，请稍后重试");
                        }
                    }
                });
                break;
            case R.id.tv_mine_to_commit:
                postUmEvent("ToBeSubmit_Click", "待提交");
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        if (myLoanBean != null && myLoanBean.getLoanSubmit() != null) {
                            goMyLoan(myLoanBean.getLoanSubmit().getUrl());
                        } else {
                            showDialog("网络异常，请稍后重试");
                        }
                    }
                });
                break;
            case R.id.tv_mine_under_approval:
                postUmEvent("Approving_Click", "审批中");
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        if (myLoanBean != null && myLoanBean.getLoanApproval() != null) {
                            goMyLoan(myLoanBean.getLoanApproval().getUrl());
                        } else {
                            showDialog("网络异常，请稍后重试");
                        }
                    }
                });
                break;
            case R.id.tv_mine_to_repay:
                postUmEvent("ToBeRepayment_Click", "待还款");
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        if (myLoanBean != null && myLoanBean.getLoanRepay() != null) {
                            goMyLoan(myLoanBean.getLoanRepay().getUrl());
                        } else {
                            showDialog("网络异常，请稍后重试");
                        }
                    }
                });
                break;
            case R.id.tv_order:
                postUmEvent("Order_Click", "订单");
                AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        if (myLoanBean != null && myLoanBean.getOrder() != null) {
                            goMyLoan(myLoanBean.getOrder().getUrl());
                        } else {
                            showDialog("网络异常，请稍后重试");
                        }
                    }
                });
                break;
            case R.id.tv_tool:
                postUmEvent("ToolService_Click", "工具服务");
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_TOOLS).navigation();
                break;
            case R.id.ll_close_quick:
                SpHelper.getInstance().saveMsgToSp(SpKey.QUICK_LOGIN_STATE, SpKey.LATEST_LONG_TOKEN, currentToken);
                rlQuickLogin.setVisibility(View.GONE);
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
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        RxBus.getInstance().unSubscribe(this);
        super.onDestroyView();
    }

    @OnLongClick({R.id.iv_setting})
    public void longClickEvent(View view) {
        if (view.getId() == R.id.iv_setting) {
            if (!BuildConfig.IS_RELEASE) {
                changeEnvironment();
            }
        }
    }

    @Override
    public void onSuccess(Object response, String flag) {
        showProgress(false);
        if (ApiUrl.URL_IS_FEEDBACK_ALLOW.equals(flag)) {
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_FEEDBACK).navigation();
        } else if (ApiUrl.MINE_BACKGROUND_IMG.equals(flag)) {
            BannerBean2 imgBean = (BannerBean2) response;
            if (imgBean != null && !CheckUtil.isEmpty(imgBean.getImageAddress())) {
                GlideUtils.loadFit(getContext(), ivHead, ApiUrl.urlAdPic + imgBean.getImageAddress(), R.drawable.bg_mine_head);
            } else {
                ivHead.setImageResource(R.drawable.bg_mine_head);
            }
        } else if (ApiUrl.POST_QUERY_RESOURCE_BY_PAGE.equals(flag)) {
            ResourceBean resourceBean = (ResourceBean) response;
            if (resourceBean == null || CheckUtil.isEmpty(resourceBean.getContents()) || resourceBean.getContents().get(0) == null || CheckUtil.isEmpty(resourceBean.getContents().get(0).getPicUrl())) {
                clBanner.setVisibility(View.GONE);
            } else {
                mBannerName = resourceBean.getResourceName();
                mBannerCid = resourceBean.getCid();
                mBannerGroupId = resourceBean.getGroupId();
                ResourceBean.ContentBean resourceContentBean = resourceBean.getContents().get(0);
                mBannerJumpUrl = resourceContentBean.getH5Url();
                GlideUtils.loadFitGif(mActivity, ivBanner, resourceContentBean.getPicUrl());
                clBanner.setVisibility(View.VISIBLE);
                UMengUtil.commonExposureEvent("MePageAdPosition_Exposure", getPageName(), mBannerName, mBannerCid, mBannerGroupId, getPageCode());
            }
        } else if (ApiUrl.BOTTOM_BANNER.equals(flag)) {
            bottomImg = (ImageLinkBean) response;
            if (bottomImg != null) {
                GlideUtils.loadCenterCrop(getContext(), ivBottom, bottomImg.getImgUrl(), R.drawable.bg_mine_gohua_info);
                GlideUtils.loadCenterCropRadius(getContext(), ivBottom, bottomImg.getImgUrl(), R.drawable.bg_mine_gohua_info, GlideUtils.ALL, 8);
            }
        } else if (ApiUrl.GET_SEVEN_PAY.equals(flag)) {
            mySevenPayBean = (MySevenPayBean) response;
            if (mySevenPayBean != null) {
                if (mySevenPayBean.getDisplay() == 1) {
                    rlSevenPay.setVisibility(View.VISIBLE);
                    setNoNullText(tvSevenTitle, mySevenPayBean.getText(), "7日待还");
                    setNoNullText(tvSevenAmount, mySevenPayBean.getAmount(), "0.00");
                    tvSevenAmount.setTypeface(FontCustom.getDinFont(getContext()));
                    tvSevenGoPay.setText(mySevenPayBean.getBtnStatus() == 0 ? "去还款" : "查看全部待还");

                } else {
                    rlSevenPay.setVisibility(View.GONE);
                }
            }
        } else if (ApiUrl.GET_MY_LOAN_URL.equals(flag)) {
            myLoanBean = (MyLoanBean) response;
            if (myLoanBean != null) {
                String allTitle = CheckUtil.isEmpty(myLoanBean.getLoanAll()) || CheckUtil.isEmpty(myLoanBean.getLoanAll().getLabel()) ? "查看全部" : myLoanBean.getLoanAll().getLabel();
                tvCheckAll.setText(allTitle);
                String commitTitle = CheckUtil.isEmpty(myLoanBean.getLoanSubmit()) || CheckUtil.isEmpty(myLoanBean.getLoanSubmit().getLabel()) ? "待提交" : myLoanBean.getLoanSubmit().getLabel();
                tvCommit.setText(commitTitle);
                String approvalTitle = CheckUtil.isEmpty(myLoanBean.getLoanApproval()) || CheckUtil.isEmpty(myLoanBean.getLoanApproval().getLabel()) ? "待审批" : myLoanBean.getLoanApproval().getLabel();
                tvUnderApproval.setText(approvalTitle);
                String repayTitle = CheckUtil.isEmpty(myLoanBean.getLoanRepay()) || CheckUtil.isEmpty(myLoanBean.getLoanRepay().getLabel()) ? "待还款" : myLoanBean.getLoanRepay().getLabel();
                tvToRepay.setText(repayTitle);
                if (!CheckUtil.isEmpty(myLoanBean.getOrder()) && !CheckUtil.isEmpty(myLoanBean.getOrder().getUrl())) {
                    if (!CheckUtil.isEmpty(myLoanBean.getOrder().getLabel())) {
                        tvOrder.setText(myLoanBean.getOrder().getLabel());
                    }
                }
            }

        }
    }

    //为元素赋值
    private void setNoNullText(TextView tv, String expect, String defaultValue) {
        tv.setText(CheckUtil.isEmpty(expect) ? defaultValue : expect);
    }

    //点击我的借款或者订单
    private void goMyLoan(String url) {
        AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
            @Override
            public void onLoginSuccess() {
                if (CheckUtil.isEmpty(url)) {
                    showDialog("网络异常，请稍后重试");
                } else {
                    Intent intent = new Intent(getActivity(), JsWebBaseActivity.class);
                    intent.putExtra("jumpKey", url);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onError(BasicResponse error, String url) {
        showProgress(false);
        if (ApiUrl.MINE_BACKGROUND_IMG.equals(url)) {
            ivHead.setImageResource(R.drawable.bg_mine_head);
        } else if (ApiUrl.GET_SEVEN_PAY.equals(url)) {
            rlSevenPay.setVisibility(View.GONE);
            mySevenPayBean = null;
        } else if (ApiUrl.GET_MY_LOAN_URL.equals(url)) {
            myLoanBean = null;
        } else {
            super.onError(error, url);
        }
    }

    private void changeEnvironment() {
        final String[] items = {"测试B", "封测", "生产"};
        String saveUrl = SpHp.getOther(SpKey.OTHER_CURRENT_URL, "");
        if (!TextUtils.isEmpty(saveUrl)) {
            for (int i = 0; i < environmentList.length; i++) {
                if (environmentList[i].equals(saveUrl)) {
                    savedIndex = i;
                }
            }
        } else {
            String url = BuildConfig.API_SERVER_URL;
            if (!TextUtils.isEmpty(url)) {
                for (int i = 0; i < environmentList.length; i++) {
                    if (environmentList[i].equals(url)) {
                        savedIndex = i;
                    }
                }
            }
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this.mActivity);
        alertBuilder.setTitle("环境切换");
        alertBuilder.setSingleChoiceItems(items, savedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkedIndex = i;
            }
        });

        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
                if (savedIndex != checkedIndex) {
                    SpHp.saveSpOther(SpKey.OTHER_CURRENT_URL, environmentList[checkedIndex]);
                    Toast.makeText(getActivity(), "环境切换，正在重启", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AppApplication.CONTEXT, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AppApplication.CONTEXT.startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());

                }
            }
        });

        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    //点击七日或者全部
    private void postPayEvent(String id, String buttonName) {
        Map<String, Object> map = new HashMap<>();
        String isOverdue = "false";
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            isOverdue = activity.getIsOverdue();
        }
        map.put("overdue_flag", isOverdue);
        UMengUtil.commonClickEvent(id, buttonName, map, getPageCode());
    }

    private void postUmEvent(String id, String buttonName) {
        UMengUtil.commonClickEvent(id, buttonName, getPageName(), getPageCode());
    }

    private String getPageName() {
        return "我的页";
    }

    private BiometricUntil biometricUntil;

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
        postUmEventWithFingerprintResult(true, "");
        UMengUtil.pageEnd("SetFingerprintPage");
    }

    @Override
    public void onFailed(Integer errorCode, String errorReason) {
        postUmEventWithFingerprintResult(false, errorReason);
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

    /*指纹设置成功/失败埋点*/
    private void postUmEventWithFingerprintResult(boolean isSuccess, String failReason) {
        Map<String, Object> map = new HashMap<>();
        map.put("is_success", isSuccess ? "true" : "false");
        map.put("fail_reason", !CheckUtil.isEmpty(failReason) ? failReason : "无");
        map.put("page_name_cn", "设置指纹页");
        UMengUtil.onEventObject("SfrpSet_Result", map, "SetFingerprintPage");
    }
}
