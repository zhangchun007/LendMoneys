package com.haiercash.gouhua.fragments.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.ScanQrCodeActivity;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.activity.edu.EduProgressHelper;
import com.haiercash.gouhua.activity.edu.NameAuthIdCardActivity;
import com.haiercash.gouhua.adaptor.HomepageAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.homepage.Credit;
import com.haiercash.gouhua.beans.homepage.HomePageBean;
import com.haiercash.gouhua.beans.homepage.ImageLinkBean;
import com.haiercash.gouhua.beans.homepage.ThemeBean;
import com.haiercash.gouhua.databinding.FragmentMain1Binding;
import com.haiercash.gouhua.hybrid.H5ConfigHelper;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.BillBearLoginPop;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.view.PointView;
import com.haiercash.gouhua.widget.HomeRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：4.0<br/>
 * 创建日期：2019/8/5<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class MainFragment extends BaseFragment {

    private FragmentMain1Binding getBinding() {
        return (FragmentMain1Binding) _binding;
    }

    Credit creditBean;

    private PointView pointView;
    String status; //用户额度状态，对应A1/A2.....

    private BillBearLoginPop bearLoginPop;
    private List<HomePageBean> homepageBean = new ArrayList<>();
    private HomepageAdapter adapter;
    private LinearLayoutManager manager;
    private ImageLinkBean themeBean;
    private boolean hasShowThemeNotice;//一次顶部主题资源位图片加载成功算首次提示，一次app启动只提示一次

    @Override
    protected FragmentMain1Binding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentMain1Binding.inflate(inflater, container, false);
    }

    @RequiresApi(23)
    @Override
    protected void initEventAndData() {
        setStatusBarTextColor(true);
        //初始化部分控件隐藏
        setonClickByViewId(R.id.ivMessage, R.id.llEmptyView, R.id.llUpAmount, R.id.rlVipVideo, R.id.ivVipClose, R.id.ivScan);
        getBinding().llUpAmount.setVisibility(View.GONE);
        getBinding().rlVipVideo.setVisibility("Y".equals(SpHp.getOther(SpKey.OTHER_BILL_BEAR_SWITCH)) ? View.VISIBLE : View.GONE);
        pointView = new PointView(mActivity);
        pointView.setPointTopRightWithMargin20(getBinding().ivMessage, 16, 0);
        String number = SpHp.getLogin(SpKey.NOTICE_POINT_OPERATE, "0");
        isHidePoint("0".equals(number));

        if (AppApplication.isLogIn()) {
            //重置:用户当前的状态
            SpHp.saveSpLogin(SpKey.LOGIN_STATUS, String.valueOf(EduProgressHelper.NORMAL_PROGRESS));
        }
        getBinding().uudvpContent.setNextPageListener(() -> {
            updateTitle(false);
            getBinding().uudvpContent.postDelayed(() -> {
                UMengUtil.commonClickEvent("BrandSlide_Click", "底部滑动二楼", getPageCode());
                MainHelper.ImageLinkRoute(mActivity, adapter.getBottomUrl());
                getBinding().uudvpContent.postDelayed(() -> {
                    getBinding().uudvpContent.resetPage();
                    updateTitle(true);
                }, 1000);
            }, 500);
        });
        getBinding().tvRefreshDesc.setTypeface(FontCustom.getMediumFont(getContext()));
        initHomeList();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initHomeList() {
        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        getBinding().rvHomepage.setLayoutManager(manager);
        adapter = new HomepageAdapter(mActivity, homepageBean, getPageCode());
        View view = getLayoutInflater().inflate(R.layout.item_homepage_footer, null);
        view.setOnClickListener(v -> {
            UMengUtil.commonClickEvent("BrandBigPicture_Click", "底部大图", getPageCode());
            MainHelper.ImageLinkRoute(mActivity, adapter.getBottomUrl());
        });
        adapter.addFooterView(view);
        pullDownStatus = 0;
        getBinding().rvHomepage.setAdapter(adapter);
        getBinding().rvHomepage.setHomeRecyclerDispatchTouchListener(new HomeRecyclerView.OnHomeRecyclerDispatchTouchListener() {
            private static final int MODE_IDLE = 0;
            private static final int MODE_HORIZONTAL = 1;
            private static final int MODE_VERTICAL = 2;
            private float mDy = -1;
            private float xLast, yLast;
            private int scrollMode = MODE_IDLE;

            @Override
            public boolean dispatchTouchEvent(MotionEvent event) {
                if (CheckUtil.isEmpty(adapter.getData()) || pullDownStatus == 2 || pullDownStatus == -1 || pullDownStatus == -2) {
                    return false;
                }
                //getTop只是FirstVisibleItem的top，不是adapter的第一个
                boolean isSlideTop = manager.findFirstVisibleItemPosition() == 0 && getBinding().rvHomepage.getChildAt(0).getTop() == 0;
                // getBinding().vHomeTopBg.setAlpha(isSlideTop ? 1 : 0);//圆弧白底只有初始状态或者无资源位数据且下拉时才显示
                float f;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xLast = event.getRawX();
                        yLast = event.getRawY();
                        scrollMode = MODE_IDLE;
                        if (isSlideTop) {
                            mDy = event.getRawY();
                        } else {
                            mDy = -1;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //实时判断滚动方向，只处理垂直滑动
                        if (scrollMode == MODE_IDLE) {
                            float xDistance = Math.abs(xLast - event.getRawX());
                            float yDistance = Math.abs(yLast - event.getRawY());
                            if (xDistance > yDistance && xDistance > 0) {
                                scrollMode = MODE_HORIZONTAL;
                            } else if (yDistance > xDistance && yDistance > 0) {
                                scrollMode = MODE_VERTICAL;
                            }
                            xLast = event.getRawX();
                            yLast = event.getRawY();
                        }
                        if (scrollMode != MODE_VERTICAL) {
                            break;
                        }
                        if (!isSlideTop) {
                            break;
                        }
                        if (mDy < 0) {
                            mDy = event.getRawY();
                            break;
                        }
                        f = event.getRawY() - mDy;
                        //顶部且手指向下滑，到达各阈值后逻辑
                        if (f >= getPullJumpH()) {
                            updateTopUi(3, f);
                            //此种情况需要拦截recyclerView本身的滑动
                            return true;
                        } else if (f >= getSrlRefreshH()) {
                            updateTopUi(11, f);
                            //此种情况需要拦截recyclerView本身的滑动
                            return true;
                        } else if (f > 0) {
                            updateTopUi(1, f);
                            //此种情况需要拦截recyclerView本身的滑动
                            return true;
                        } else {
                            if (pullDownStatus != 0) {
                                updateTopUi(0, 0);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        f = event.getRawY() - mDy;
                        mDy = -1;
                        if (scrollMode != MODE_VERTICAL) {
                            break;
                        }
                        if (!isSlideTop) {
                            updateTopUi(0, 0);
                            break;
                        }
                        //到达个阈值范围松手逻辑
                        if (f >= getPullJumpH()) {
                            if (themeBean != null && MainHelper.ImageLinkRoute(mActivity, themeBean.getForwardUrl())) {
                                UMengUtil.commonClickBannerEvent("HomeAdPosition_Click", getPageName(), themeBean.getThemeName(), themeBean.getCid(), themeBean.getGroupId(), getPageCode());
                                //跳转回来后会自动刷新页面的，所以不需要再调代码刷新
                                updateTopUi(-2, f);
                            } else {
                                onRefresh();
                            }
                            return true;
                        } else if (f <= 0) {
                            updateTopUi(-2, f);
                        } else if (f < getSrlRefreshH()) {
                            updateTopUi(-2, f);
                            return true;
                        } else {
                            onRefresh();
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 下拉状态显示不同theme里UI，
     * -2松手动画
     * -1顶部theme资源位图片首次加载成功后展示theme资源位2秒后回归常规状态0，没有配置则不存在这种状态
     * 0默认不展示theme资源位，
     * 1下拉展示资源位未达到刷新阈值并对应文案UI“下拉进入详情”
     * 11下拉展示资源位达到刷新阈值并并对应文案UI"松手刷新"
     * 2下拉展示资源位并并对应文案UI“刷新中”
     * 3下拉一定层度展示资源位并对应文案UI“松手进入详情”
     */
    private int pullDownStatus;
    private float topSpaceH;
    private int carryNum = 20;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateTopUi(pullDownStatus, topSpaceH * 3 / 5f);//每次上升五分之二
        }
    };
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private void updateTopUi(int pullDownStatus, float topSlide) {
        //theme资源位从不可见到可见，曝光
        if (this.pullDownStatus == 0 && pullDownStatus != 0 && themeBean != null) {
            try {
                UMengUtil.commonExposureEvent("HomeAdPosition_Exposure", getPageName(), themeBean.getThemeName(), getPageCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.pullDownStatus = pullDownStatus;
        this.topSpaceH = topSlide;
        //松手动效
        if (this.pullDownStatus == -2) {
            mHandler.removeCallbacks(runnable);
            if (carryNum > 0) {
                updateTopUi();
                carryNum--;
                mHandler.postDelayed(runnable, 15);
                return;
            } else {
                //正常时重置
                this.pullDownStatus = 0;
                this.topSpaceH = 0;
                carryNum = 20;//正常时重置
            }
        } else {
            carryNum = 20;//正常时重置
        }
        updateTopUi();
    }

    private void updateTopUi() {
        //主视图顶部
        getBinding().vSpace.setVisibility(this.pullDownStatus != 0 && themeBean != null ? View.INVISIBLE : View.VISIBLE);//theme与额度卡片之间的空间
        updateTitle(this.pullDownStatus == 0 || themeBean == null);

        this.topSpaceH = Math.max(this.topSpaceH, 0);//负值自动以0处理
        RelativeLayout.LayoutParams ivThemeLayoutParams = (RelativeLayout.LayoutParams) getBinding().ivTheme.getLayoutParams();
        int d = (int) (-getPullJumpH() + this.topSpaceH);
        //初始topMargin默认-getPullJumpH(),到达下拉最大值之后topMargin为0
        ivThemeLayoutParams.topMargin = Math.min(0, d);
        getBinding().ivTheme.setLayoutParams(ivThemeLayoutParams);
        getBinding().ivRefreshDesc.clearAnimation();
        //没资源位数据跟有资源位数据时不一样
        if (themeBean == null) {
            getBinding().tvRefreshDesc.setVisibility(View.GONE);
            getBinding().vSpace.setVisibility(View.INVISIBLE);
            if (pullDownStatus == 2) {
                getBinding().ivRefreshDesc.setImageResource(R.drawable.home_refresh);
                getBinding().ivRefreshDesc.setVisibility(View.VISIBLE);
                AnimationDrawable animationDrawable = (AnimationDrawable) getBinding().ivRefreshDesc.getDrawable();
                animationDrawable.start();
            } else if (pullDownStatus == 0) {
                getBinding().ivRefreshDesc.setVisibility(View.INVISIBLE);
            } else {
                getBinding().ivRefreshDesc.setImageResource(R.drawable.home_refresh_01);
                getBinding().ivRefreshDesc.setVisibility(View.VISIBLE);
            }
        } else {
            getBinding().ivRefreshDesc.clearAnimation();
            switch (pullDownStatus) {
                case 2:
                    getBinding().vSpace.setVisibility(View.VISIBLE);
                    getBinding().ivRefreshDesc.setImageResource(R.drawable.icon_home_refresh);
                    getBinding().ivRefreshDesc.setVisibility(View.VISIBLE);
                    RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(500);
                    rotateAnimation.setRepeatCount(Animation.INFINITE);
                    rotateAnimation.setRepeatMode(Animation.RESTART);
                    getBinding().ivRefreshDesc.startAnimation(rotateAnimation);
                    getBinding().tvRefreshDesc.setText("刷新中");
                    getBinding().tvRefreshDesc.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    getBinding().vSpace.setVisibility(View.VISIBLE);
                    getBinding().ivRefreshDesc.setImageResource(R.drawable.icon_home_pull);
                    getBinding().ivRefreshDesc.setVisibility(View.VISIBLE);
                    getBinding().tvRefreshDesc.setText("松手进入详情");
                    getBinding().tvRefreshDesc.setVisibility(View.VISIBLE);
                    break;
                case -2:
                case -1:
                case 1:
                    getBinding().vSpace.setVisibility(View.VISIBLE);
                    getBinding().ivRefreshDesc.setImageResource(R.drawable.icon_home_pull);
                    getBinding().ivRefreshDesc.setVisibility(View.VISIBLE);
                    getBinding().tvRefreshDesc.setText("下拉进入详情");
                    getBinding().tvRefreshDesc.setVisibility(View.VISIBLE);
                    break;
                case 11:
                    getBinding().vSpace.setVisibility(View.VISIBLE);
                    getBinding().ivRefreshDesc.setImageResource(R.drawable.icon_home_pull);
                    getBinding().ivRefreshDesc.setVisibility(View.VISIBLE);
                    getBinding().tvRefreshDesc.setText("松手刷新");
                    getBinding().tvRefreshDesc.setVisibility(View.VISIBLE);
                    break;
                default:
                    getBinding().vSpace.setVisibility(View.INVISIBLE);
                    getBinding().ivRefreshDesc.setVisibility(View.INVISIBLE);
                    getBinding().tvRefreshDesc.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void updateTitle(boolean show) {
        // getBinding().vHomeTopBg0.setAlpha(show ? 1 : 0);//顶部背景vHomeTopBg拉太长可能存在空隙
        //getBinding().vHomeTopBg.setAlpha(show && manager.findFirstVisibleItemPosition() == 0 && getBinding().rvHomepage.getChildAt(0).getTop() == 0 ? 1 : 0);
        getBinding().rlHeadTitle.setAlpha(show ? 1 : 0);
        getBinding().ivHeadImg.setVisibility(show ? View.VISIBLE : View.GONE);
        getBinding().tvLogoInfo.setVisibility(show ? View.VISIBLE : View.GONE);
        getBinding().ivScan.setVisibility(show ? View.VISIBLE : View.GONE);
        getBinding().ivMessage.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
            Logger.d("MainBaseFragment  : onResume  :  loadHomeData");
            loadHomeData();
        }
    }

    private int getSrlRefreshH() {
        return UiUtil.dip2px(mActivity, 93);//93dp阈值
    }

    private int getPullJumpH() {
        return UiUtil.dip2px(mActivity, 193);//193dp阈值
    }

    private void onRefresh() {
        updateTopUi(2, getSrlRefreshH());
        Logger.d("MainBaseFragment  : onRefresh  :  loadHomeData");
        loadHomeData(false);
    }

    private void loadHomeData() {
        loadHomeData(true);
    }

    /**
     * 请求首页数据状态
     */
    private void loadHomeData(boolean showLoading) {
        String number = SpHp.getLogin(SpKey.NOTICE_POINT_OPERATE, "0");
        isHidePoint("0".equals(number));
        RxBus.getInstance().post(new ActionEvent(ActionEvent.MainRefreshHomePage, showLoading ? "true" : "false", "true"));
        resetData();
    }

    private void jumpToMessage() {
        SpHp.saveSpLogin(SpKey.NOTICE_POINT_OPERATE, "0");
        isHidePoint(true);
        ARouterUntil.getContainerInstance(PagePath.FRAGMENT_MESSAGE).navigation();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivMessage) {
            Map<String, Object> map = new HashMap();
            map.put("is_read", pointView != null && pointView.getVisibility() == View.VISIBLE ? "true" : "false");
            UMengUtil.commonClickEvent("MessageCenter_Click", "消息中心", map, getPageCode());
            AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    jumpToMessage();
                }
            });
        } else if (view.getId() == R.id.llEmptyView) {
            loadHomeData();
        } else if (view.getId() == R.id.llUpAmount) {
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_PROMOTE_LIMIT).navigation();
        } else if (view.getId() == R.id.rlVipVideo) {
            AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    clickRvVipVideoAction();
                }
            });
        } else if (view.getId() == R.id.ivVipClose) {
            getBinding().rlVipVideo.setVisibility(View.GONE);
        } else if (view.getId() == R.id.ivScan) {  //扫码
            UMengUtil.commonClickEvent("ScanCode_Click", "扫码", getPageCode());
            AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    jumpToScanQrCode();
                }
            });
        }
    }

    private void clickRvVipVideoAction() {
        if (bearLoginPop == null) {
            bearLoginPop = new BillBearLoginPop(mActivity, null);
        }
        bearLoginPop.checkLoginStatus(getBinding().rlVipVideo);
    }

    private void jumpToScanQrCode() {
        //跳转扫码
        startActivity(new Intent(mActivity, ScanQrCodeActivity.class));
    }

    /**
     * 初始化界面数据
     */
    private void initHomeView() {
        //更新完首页UI后调用首页弹窗整个逻辑
        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).queryNeedResignAgreements((view, flagTag, obj) -> {
                if (!mActivity.isShowingDialog()) {
                    controlDialogUtil.setUmParam(getPageName(), getPageCode());
                    controlDialogUtil.checkDialog();
                }
            });
        }
    }

    private String getPageName() {
        return "够花-首页";
    }

    @Override
    public void onSuccess(Object response, final String flag) {
        if (ApiUrl.URL_HOME_INFO.equals(flag)) {
            showErrorPage(false);
            if (response != null && homepageBean != null && response.toString().equals(JsonUtils.toJson(homepageBean))) {
                Logger.e("首页接口加载的数据未发生变化，不更新UI");
            } else {
                homepageBean = JsonUtils.fromJsonArray(response, HomePageBean.class);
                if (adapter != null) {
                    adapter.setNewData(homepageBean);
                }
            }
            ImageLinkBean imageLinkBean = null;
            for (HomePageBean bean : homepageBean) {
                if ("theme".equals(bean.getType())) {
                    ThemeBean themeBean = JsonUtils.fromJson(bean.getData(), ThemeBean.class);
                    creditBean = themeBean.getCredit();
                    if (!CheckUtil.isEmpty(themeBean.getTheme())) {
                        imageLinkBean = themeBean.getTheme().get(0);
                    }
                    break;
                }
            }
            ImageLinkBean finalImageLinkBean = imageLinkBean;
            GlideUtils.getNetBitmap(mActivity, finalImageLinkBean != null ? finalImageLinkBean.getImgUrl() : null, new INetResult() {
                @Override
                public <T> void onSuccess(T t, String url) {
                    themeBean = finalImageLinkBean;
                    getBinding().ivTheme.setImageBitmap((Bitmap) t);
                    if (!hasShowThemeNotice) {
                        hasShowThemeNotice = true;
                        updateTopUi(-1, UiUtil.dip2px(mActivity, 63));
                        new Handler(Looper.getMainLooper()).postDelayed(() -> updateTopUi(-2, UiUtil.dip2px(mActivity, 63)), 3000);
                    }
                }

                @Override
                public void onError(BasicResponse error, String url) {
                    themeBean = null;
                    getBinding().ivTheme.setImageBitmap(null);
                }
            });
            finishRefresh();
            if (creditBean == null) {
                return;
            }
            if (AppApplication.isLogIn()) {
                getH5ConfigData(creditBean.getStatus(), creditBean.getApplSeq());
            }
            SpHp.saveUser(SpKey.USER_EDU_SPE_SEQ, creditBean.getSpeSeq());
            MainHelper.saveMoneyState(creditBean.getAvailLimit(), creditBean.getTotalLimit());
            //贷款品种
            MainEduBorrowUntil.INSTANCE(mActivity).setMinAmtAndTypeLevelTwo(creditBean.getTypCde(), creditBean.getMinAmt(), creditBean.getTypLvlCde());
            MainEduBorrowUntil.status = creditBean.getStatus();
           // MainEduBorrowUntil.INSTANCE(mActivity).postHomePageButtonEvent();
            status = creditBean.getUserStatus();
            initHomeView();
            loadCreditPromotion();
        } else if (ApiUrl.URL_CREDIT_PROMOTION.equals(flag)) {
            showCreditLimitPromotion(true);
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        showProgress(false);
        if (ApiUrl.URL_HOME_INFO.equals(url)) {
            showCreditLimitPromotion(false);
            finishRefresh();
            showErrorPage(true);
        } else if (ApiUrl.URL_CREDIT_PROMOTION.equals(url)) {
            showCreditLimitPromotion(false);
        } else {
            super.onError(error, url);
        }
    }

    private void getH5ConfigData(String status, String applSeq) {
        H5ConfigHelper helper = new H5ConfigHelper(status, applSeq, null);
        helper.getH5LinkData();
    }

    //首页接口请求失败，显示错误页
    private void showErrorPage(boolean isError) {
        if (isError) {
            getBinding().llEmptyView.setVisibility(View.VISIBLE);
            getBinding().rlHeadTitle.setVisibility(View.GONE);
            getBinding().vHomeTopBg.setVisibility(View.GONE);
            getBinding().uudvpContent.setVisibility(View.GONE);
        } else {
            getBinding().llEmptyView.setVisibility(View.GONE);
            //getBinding().rlHeadTitle.setVisibility(View.VISIBLE);
            //getBinding().vHomeTopBg.setVisibility(View.VISIBLE);
            getBinding().uudvpContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示提额入口
     */
    void showCreditLimitPromotion(boolean isShow) {
        getBinding().llUpAmount.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 111) {
            Logger.d("MainBaseFragment  : onActivityResult  :  loadHomeData");
            loadHomeData();
        } else if (requestCode == WebSimpleFragment.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            startActivity(new Intent(mActivity, NameAuthIdCardActivity.class));
        }
    }

    /**
     * 消息小红点控制
     */
    public void isHidePoint(boolean flag) {
        pointView.setVisibility(flag ? View.GONE : View.VISIBLE);
    }

    /**
     * 是否可以提额查询
     */
    private void loadCreditPromotion() {
        //实名并且mEduBean中userState=02（有额且没逾期）的情况
        if (CommomUtils.isRealName() && ("D1".equals(status) || "D2".equals(status))) {
            String custName = SpHp.getUser(SpKey.USER_CUSTNAME);
            String certNo = SpHp.getUser(SpKey.USER_CERTNO);
            String custNo = SpHp.getUser(SpKey.USER_CUSTNO);
            Map<String, String> map = new HashMap<>();
            map.put("custName", RSAUtils.encryptByRSA(custName));
            map.put("certNo", RSAUtils.encryptByRSA(certNo));
            map.put("custNo", RSAUtils.encryptByRSA(custNo));
            map.put("isRsa", "Y");
            netHelper.postService(ApiUrl.URL_CREDIT_PROMOTION, map);
        } else {
            showCreditLimitPromotion(false);
        }
    }


    @Override
    public void onDestroyView() {
        if (bearLoginPop != null) {
            bearLoginPop.onDestroy();
        }
        super.onDestroyView();
    }

    @Override
    public void resetData() {
        super.resetData();
        showCreditLimitPromotion(false);
    }

    private void finishRefresh() {
        updateTopUi(this.pullDownStatus == 2 ? -2 : 0, this.pullDownStatus == 2 ? getSrlRefreshH() : 0);
    }

    @Override
    protected String getPageCode() {
        return "HomePage";
    }
}
