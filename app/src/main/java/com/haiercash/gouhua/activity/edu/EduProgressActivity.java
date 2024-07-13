package com.haiercash.gouhua.activity.edu;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.handler.CycleHandlerCallback;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.ResourceHelper;
import com.haiercash.gouhua.adaptor.ApplyRecordeAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.BannerBean2;
import com.haiercash.gouhua.beans.CopyWriterBean;
import com.haiercash.gouhua.beans.CreditLifeBorrowBean;
import com.haiercash.gouhua.beans.ProcessBean;
import com.haiercash.gouhua.beans.ResourceBean;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.CreditLifeHelp;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2017/12/11<br/>
 * 描    述：额度申请结果页<br/>
 * 中部资源位先根据配置情况返回而加载(支持加载动图)，如果没有从后端取到，则根据申额结果来分别加载本地的资源位图片，如果加载本地，则不跳转
 * ================================================================
 */
public class EduProgressActivity extends BaseActivity {
    @BindView(R.id.iv_mid_banner)
    ImageView ivMidBanner;
    @BindView(R.id.iv_banner)
    ImageView iv_banner;
    @BindView(R.id.tv_notice)
    TextView tv_notice;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_status_info)
    TextView tvStatusInfo;
    @BindView(R.id.tv_countdown)
    TextView tvCountDown;
    @BindView(R.id.tv_countdown_desc)
    TextView tvCountDownDesc;
    @BindView(R.id.tv_back_msg)
    TextView tvBackMsg;
    @BindView(R.id.ll_loan_market)
    View llLoanMarket;
    @BindView(R.id.recycler_loan)
    RecyclerView recyclerLoan;
    @BindView(R.id.tv_tip2)
    TextView tvTip2;

    private ApplyRecordeAdapter mAdapter;
    private CreditLifeHelp mHelp;
    private int maxSecond = 20;
    private long currentTime = 0;
    private boolean isResultFinish = false;
    private boolean isProcessing = false;
    private String applSeq;

    private ArrayList<String> imgAddress, imgLink;
    private String midBannerJumpUrl, bannerJumpUrl;
    private String midBannerName, bannerName, midCid, cid, midGroupId, groupId;
    private NetHelper mMidBannerNetHelper;
    private String mEdResult = "01";
    /**
     * 正常：normal<br/>
     * 导流：Diversion<br/>
     * 权益：Equity<br/>
     */
    private String isDiversionOrEquity = "normal";

    private final int STATUS_SUCC = 0x00;
    private final int STATUS_FAIL = 0x01;
    private String otherResult;

    @Override
    protected int getLayout() {
        return R.layout.activity_edu_progress;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("");
        setBarLeftImage(0, null);
        tvStatus.setTypeface(FontCustom.getMediumFont(this));
        tvCountDown.setTypeface(FontCustom.getMediumFont(this));
        tvCountDownDesc.setTypeface(FontCustom.getMediumFont(this));
        applSeq = getIntent().getStringExtra("applSeq");
        applSeq = CheckUtil.deletePointZero(applSeq);
        tvCountDown.setText(UiUtil.getStr("(20S)"));
        //贷超
        mAdapter = new ApplyRecordeAdapter(true);
        recyclerLoan.setLayoutManager(new LinearLayoutManager(EduProgressActivity.this));
        recyclerLoan.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter1, view, position) -> {
            CreditLifeBorrowBean borrowBean = mAdapter.getData().get(position);
            mHelp = new CreditLifeHelp(EduProgressActivity.this, borrowBean);
            mHelp.dispatchUniteLogin();
        });
        initMidBannerNetHelper();
        loadDefaultMidBanner();
        Map<String, String> map = new HashMap<>();
        map.put("copyWriterPosition", "1");
        netHelper.getService(ApiUrl.URL_CONFIG_NOTICE, map, CopyWriterBean.class);
        RxBus.getInstance().post(new ActionEvent(ActionEvent.MainFragmentReset));
        String failReason = getIntent().getStringExtra("Result");
        if (CheckUtil.isEmpty(failReason)) {
            showProgress(true);
            currentTime = System.currentTimeMillis();
            mHandler.sendEmptyMessage(0);
            mHandler.sendEmptyMessage(2);
            tvTip2.setVisibility(View.GONE);
        } else {
            mEdResult = "03";
            isResultFinish = true;
            setEduStatus(STATUS_FAIL);
            //用户点击重新申请按钮后，调用信贷拒绝码接口查询拒绝有效期剩余天数X
            String allowDays = getIntent().getStringExtra("allowDays");
            String allowDate = getIntent().getStringExtra("allowDate");
            String content = "请于" + allowDays + "天后 (" + allowDate + ") 尝试申请";
            String args = allowDays + "天";
            SpannableString msg = formatMessage(content, content.indexOf(args), args.length());//处理字符串
            if (TextUtils.isEmpty(allowDays) || TextUtils.isEmpty(allowDate)) {
                tvTip2.setVisibility(View.GONE);
            } else {
                tvTip2.setVisibility(View.VISIBLE);
                tvTip2.setText(msg);
            }
            ResourceHelper.requestEdResultMidResource(mMidBannerNetHelper, mEdResult);
            requestLoanMarket();
        }
        ResourceHelper.requestEdResultBottomResource(netHelper);
        setBarRightText("完成", 0xFF303133, v -> {
            UMengUtil.commonClickEvent("QuotaApplicationBackHomePage_Click", "完成", getPageCode());
            if (isResultFinish) {
                finish();
                MainHelper.backToMainHome();
            } else {
                showDialog("正等待返回结果", "即将返回审批结果，请您耐心等待 ", "去首页", "继续等待", (dialog, which) -> {
                    if (which == 1) {
                        finish();
                        MainHelper.backToMainHome();
                    }
                }).setButtonTextColor(1, R.color.colorPrimary).setButtonTextColor(2, R.color.colorPrimary);
            }
        });
        //4.0新增，测试压力大暂时去除，后期需要添加只需要去掉注释就行
       /* if (!isShowingDialog()) {
            controlDialogUtil.checkDialog("cre_appl");
        }*/
    }

    private void requestLoanMarket() {
        Map<String, String> hot = new HashMap<>();
        netHelper.getService(ApiUrl.GET_LIST_HOT, hot);
    }

    private void initMidBannerNetHelper() {
        mMidBannerNetHelper = new NetHelper(this, new INetResult() {
            @Override
            public <T> void onSuccess(T t, String url) {
                if (ApiUrl.POST_QUERY_RESOURCE_BY_PAGE.equals(url)) {
                    ResourceBean resourceBean = (ResourceBean) t;
                    if (resourceBean == null || CheckUtil.isEmpty(resourceBean.getContents())
                            || resourceBean.getContents().get(0) == null
                            || CheckUtil.isEmpty(resourceBean.getContents().get(0).getPicUrl())) {
                        resetMidBannerData();
                        updateMidBannerUi(null);
                    } else {
                        midBannerName = resourceBean.getResourceName();
                        midCid = resourceBean.getCid();
                        midGroupId = resourceBean.getGroupId();
                        ResourceBean.ContentBean resourceContentBean = resourceBean.getContents().get(0);
                        midBannerJumpUrl = resourceContentBean.getH5Url();
                        updateMidBannerUi(resourceContentBean.getPicUrl());
                    }
                }
            }

            @Override
            public void onError(BasicResponse error, String url) {
                if (ApiUrl.POST_QUERY_RESOURCE_BY_PAGE.equals(url)) {
                    resetMidBannerData();
                    updateMidBannerUi(null);
                }
            }
        });
    }

    private void resetMidBannerData() {
        midBannerName = null;
        midCid = null;
        midGroupId = null;
        midBannerJumpUrl = null;
    }

    private Handler mHandler = new Handler(new CycleHandlerCallback(this) {
        int random = new SecureRandom().nextInt(14) + 7;

        @Override
        public void dispatchMessage(Message msg) {
            if (mHandler != null && msg.what == 0) {
                if (isResultFinish) {
                    return;
                }
                if ((maxSecond == 20 || maxSecond % 3 == 0) && maxSecond > 0) {
                    System.out.println("P-EduProgressActivity--------->请求：" + maxSecond);
                    reloadEduResult();
                }
                mHandler.sendEmptyMessageDelayed(0, 1000);//发起接口请求
            } else if (mHandler != null && msg.what == 1) {
                if (maxSecond <= (20 - random)) {
                    stopTimeAndShowResultAndEquityGrant();
                    return;
                }
                mHandler.sendEmptyMessageDelayed(1, 1000);
            } else if (mHandler != null && msg.what == 2) {//单纯的倒计时以及倒计时到0的时候需要做的事情
                if (maxSecond > 0) {
                    System.out.println("P-EduProgressActivity--------->倒计时：" + maxSecond);
                    tvCountDown.setText(UiUtil.getStr("(", maxSecond, "S)"));
                    maxSecond--;
                    mHandler.sendEmptyMessageDelayed(2, 1000);//发起请求倒计时
                } else {
                    System.out.println("P-EduProgressActivity--------->倒计时结束：" + maxSecond);
                    isResultFinish = true;
                    tvCountDown.setText(null);
                    if (!mHasLoadMidImageSuccess && "01".equals(mEdResult)) {
                        ivMidBanner.setImageResource(R.drawable.img_ed_mid_banner_waiting_0);//倒计时结束且审核中且中部资源位没有加载成功情况下动图变静图
                    }
                    tvCountDownDesc.setText("暂未获得自动审批结果，请至首页刷新尝试!");
                    tvBackMsg.setText("请保持手机畅通，我们将会进行电话核实\n外呼电话为0532青岛座机，请注意接听");
                    destroyHandler();
                }
            }
        }
    });

    private void destroyHandler() {
        if (mHandler != null) {
            mHandler.removeMessages(0);
            mHandler.removeMessages(1);
            mHandler.removeMessages(2);
            mHandler = null;
        }
    }

    /**
     * 停止倒计时；并且判断是否可以跳转权益页和展示失败页面
     */
    private synchronized void stopTimeAndShowResultAndEquityGrant() {
        destroyHandler();
        //UiUtil.toast("倒计时结束，是否可以跳转权益页：" + (imgAddress.size() > 0));
        if ("normal".equals(isDiversionOrEquity)) {
            System.out.println("默认拒绝页");
        } else if ("Diversion".equals(isDiversionOrEquity)) {
            System.out.println("贷超");
            requestLoanMarket();
        } else if ("Equity".equals(isDiversionOrEquity)) {
            if (!CheckUtil.isEmpty(imgAddress) && !CheckUtil.isEmpty(imgLink)) {
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_EQUITY_GRANT)
                        .put("isShowTitle", false).put("imgAddress", imgAddress)
                        .put("imgLink", imgLink).navigation();
                System.out.println("权益页");
                finish();
                return;
            }
        }
        setEduStatus(STATUS_FAIL);
    }


    @Override
    @OnClick({R.id.iv_mid_banner, R.id.iv_banner})
    public void onClick(View v) {
        if (v == null) {
            return;
        }
        if (v == iv_banner) {
            postUmEvent("BottomAdPosition_Click", false);
            WebHelper.startActivityForUrl(this, bannerJumpUrl);
        } else if (v == ivMidBanner) {
            postUmEvent("QuotaApplicationAdPosition_Click", true);
            WebHelper.startActivityForUrl(this, midBannerJumpUrl);
        }
    }

    /**
     * 加载申请结果
     */
    private void reloadEduResult() {
        if (CheckUtil.isEmpty(applSeq)) {
            showProgress(false);
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("applSeq", applSeq);
        params.put("currentTime", String.valueOf(maxSecond));
        netHelper.getService(ApiUrl.URL_PROCESS, params, ProcessBean.class);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        showProgress(false);
        if (ApiUrl.URL_PROCESS.equals(url)) {
            System.out.println("--EduProgressActivity--------->结果onError：" + maxSecond + "------message:" + JsonUtils.toJson(error));
            postResultEvent("false", error.getHead().retMsg);
            ResourceHelper.requestEdResultMidResource(mMidBannerNetHelper, mEdResult);
            return;
        } else if (ApiUrl.GET_LIST_HOT.equals(url)) {
            return;
        } else if (ApiUrl.URL_MEMBER_SHIP_SELECT.equals(url)) {
            isDiversionOrEquity = "normal";
            handlerFailDelayed();
            return;
        }
        showDialog(error.getHead().retMsg);
    }

    private void loadDefaultMidBanner() {
        if (ivMidBanner == null) {
            return;
        }
        resetMidBannerData();
        int drawableId;
        switch (mEdResult) {
            case "02"://成功
                drawableId = R.drawable.img_ed_mid_banner_success;
                break;
            case "03"://失败
                drawableId = R.drawable.img_ed_mid_banner_fail;
                break;
            default://审批中
                drawableId = R.drawable.img_ed_mid_banner_waiting;
                break;
        }
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);
        if (drawable != null) {
            ViewGroup.LayoutParams layoutParams = ivMidBanner.getLayoutParams();
            layoutParams.width = SystemUtils.getDeviceWidth(this) - 2 * UiUtil.dip2px(this, 15);
            layoutParams.height = layoutParams.width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
            ivMidBanner.setLayoutParams(layoutParams);
        }
        switch (mEdResult) {
            case "02"://成功
            case "03"://失败
                ivMidBanner.setImageResource(drawableId);
                break;
            default://审批中,gif动画,倒计时结束且没有加载成功配置的资源位则显示本地静图
                if (maxSecond > 0) {
                    GlideUtils.loadDrawableSourceGif(EduProgressActivity.this, drawableId, ivMidBanner);
                } else {
                    ivMidBanner.setImageResource(R.drawable.img_ed_mid_banner_waiting_0);
                }
                break;
        }
    }

    private boolean mHasLoadMidImageSuccess = false;//中部资源位图片加载成功

    private void updateMidBannerUi(String imgUrl) {
        GlideUtils.loadForWidth(EduProgressActivity.this, imgUrl, ivMidBanner,
                SystemUtils.getDeviceWidth(this) - 2 * UiUtil.dip2px(this, 15),
                UiUtil.dip2px(EduProgressActivity.this, 8),
                new INetResult() {
                    @Override
                    public <T> void onSuccess(T t, String url) {
                        mHasLoadMidImageSuccess = true;
                        postMidBannerExposure();
                        updateUiIfLoadCorrectResUrl(true);
                    }

                    @Override
                    public void onError(BasicResponse error, String url) {
                        mHasLoadMidImageSuccess = false;
                        loadDefaultMidBanner();
                        if (!CheckUtil.isEmpty(imgUrl)) {
                            postMidBannerExposure();
                        }
                        updateUiIfLoadCorrectResUrl(false);
                    }
                });
    }

    private void updateUiIfLoadCorrectResUrl(boolean correct) {
        boolean show = !isResultFinish && !correct;
        tvCountDownDesc.setVisibility(show ? View.VISIBLE : View.GONE);
        tvBackMsg.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSuccess(Object response, String url) {
        if (ApiUrl.URL_PROCESS.equals(url)) {
            if (isFinishing()) {
                return;
            }
            showProgress(false);
            ProcessBean data = (ProcessBean) response;
            System.out.println("--EduProgressActivity--------->结果：" + maxSecond);
            if (isResultFinish && ("02".equals(mEdResult) || "03".equals(mEdResult))) {
                System.out.println("--EduProgressActivity--------->结果已处理：" + maxSecond);
                return;
            }
            if (isProcessing) {
                System.out.println("--EduProgressActivity--------->结果处理中：" + maxSecond);
                return;
            }
            isProcessing = true;
            mEdResult = data != null ? data.appStatus : "01";
            if (data != null && "02".equals(data.appStatus)) {
                destroyHandler();
                isResultFinish = true;
                currentTime = System.currentTimeMillis() - currentTime;
                setEduStatus(STATUS_SUCC);
            } else if (data != null && "03".equals(data.appStatus)) {
                isResultFinish = true;
                currentTime = System.currentTimeMillis() - currentTime;
                if ("20".equals(data.appConclusion)) {
                    if ("Y".equals(SpHp.getLogin(SpKey.ENABLE_LOAN_MARKET)) &&
                            ("eduStatusAll".equals(SpHp.getLogin(SpKey.EDU_STATUS)) || SpHp.getLogin(SpKey.EDU_STATUS).contains("eduStatusApplyDenied"))) {
                        //导流（热门推荐）
                        isDiversionOrEquity = "Diversion";
                        SpHp.saveSpLogin(SpKey.LOAN_MARKET_EDU_STATUS, "Y");
                        handlerFailDelayed();
                    } else {
                        //请求权益数据
                        isDiversionOrEquity = "Equity";
                        netHelper.getService(ApiUrl.URL_MEMBER_SHIP_SELECT, null);
                    }
                } else {
                    setEduStatus(STATUS_FAIL);
                }
            }
            System.out.println("--EduProgressActivity--------->当前结果处理完毕：" + (data != null ? data.appStatus : "") + "-" + maxSecond);
            isProcessing = false;
            ResourceHelper.requestEdResultMidResource(mMidBannerNetHelper, mEdResult);
        } else if (ApiUrl.GET_LIST_HOT.equals(url)) {
            List<CreditLifeBorrowBean> beans = JsonUtils.fromJsonArray(response, CreditLifeBorrowBean.class);
            if (llLoanMarket != null) {
                llLoanMarket.setVisibility(!CheckUtil.isEmpty(beans) ? View.VISIBLE : View.GONE);
            }
            if (beans.size() > 2) {
                beans = beans.subList(0, 2);
            }
            mAdapter.setNewData(beans);
        } else if (ApiUrl.URL_MEMBER_SHIP_SELECT.equals(url)) {
            List<BannerBean2> bean2s = JsonUtils.fromJsonArray(response, BannerBean2.class);
            if (bean2s.size() > 0) {
                imgAddress = new ArrayList<>();
                imgLink = new ArrayList<>();
                for (BannerBean2 bean2 : bean2s) {
                    imgAddress.add(bean2.getImageAddress());
                    imgLink.add(bean2.getImageLink());
                }
            }
            handlerFailDelayed();
        } else if (ApiUrl.POST_QUERY_RESOURCE_BY_PAGE.equals(url)) {
            if (response == null) {
                return;
            }
            ResourceBean resourceBean = (ResourceBean) response;
            bannerName = resourceBean.getResourceName();
            cid = resourceBean.getCid();
            groupId = resourceBean.getGroupId();
            List<ResourceBean.ContentBean> list = resourceBean.getContents();
            if (!CheckUtil.isEmpty(list)) {
                bannerJumpUrl = list.get(0).getH5Url();
                String image = list.get(0).getPicUrl();
                GlideUtils.loadForWidth(EduProgressActivity.this, image, iv_banner,
                        SystemUtils.getDeviceWidth(this),
                        UiUtil.dip2px(EduProgressActivity.this, 8), new INetResult() {
                            @Override
                            public <T> void onSuccess(T t, String url) {
                                if (iv_banner != null) {
                                    iv_banner.setVisibility(View.VISIBLE);
                                    postBottomBannerExposure();
                                }
                            }

                            @Override
                            public void onError(BasicResponse error, String url) {
                                if (iv_banner != null) {
                                    iv_banner.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        } else if (ApiUrl.URL_CONFIG_NOTICE.equals(url)) {
            CopyWriterBean bean = (CopyWriterBean) response;
            if (bean == null || CheckUtil.isEmpty(bean.getContent())) {
                tv_notice.setVisibility(View.GONE);
            } else {
                tv_notice.setText(bean.getContent());
                tv_notice.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 在7、8秒内出现的失败的结果，需要人为让用户等待7、8秒
     */
    private void handlerFailDelayed() {
        if (maxSecond > 13) {//在7、8秒内出现的失败的结果，需要人为让用户等待7、8秒
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(1, 1000);
            }
        } else {
            stopTimeAndShowResultAndEquityGrant();
        }
    }

    /**
     * 根据状态设置页面
     */
    private void setEduStatus(int status) {
        tvCountDown.setVisibility(View.GONE);
        switch (status) {
            case STATUS_SUCC:
                tvStatus.setText("恭喜您，额度申请成功");
                tvStatusInfo.setText("您可在首页借款");
                if (!isShowingDialog() && controlDialogUtil != null) {
                    controlDialogUtil.checkDialog("cre_appl");
                }
                postResultEvent("true", "额度申请成功");
                break;
            case STATUS_FAIL:
                tvStatus.setText("额度申请未通过");
                tvStatusInfo.setText("因综合评分不足，您未能通过额度审批");
                postResultEvent("false", "暂时不能提供借款服务");
                break;
            default:
                break;
        }
    }

    //额度结果埋点
    private void postResultEvent(String success, String result) {
        Map<String, Object> map = new HashMap<>();
        map.put("is_success", success);
        map.put("application_result", result);
        UMengUtil.onEventObject("QuotaApplication", map, getPageCode());

    }

    @Override
    protected String getPageCode() {
        return "QuotaApplicationPage";
    }

    private boolean firstResume = true;

    @Override
    public void onResume() {
        if (!CheckUtil.isEmpty(otherResult)) {
            postResultEvent("false", "暂不符合申请条件");
            otherResult = "";
        }
        super.onResume();
        postBottomBannerExposure();
        //非第一次返回到前台
        if (!firstResume) {
            //审核中才需要刷结果，结果已出的只用刷资源位
            if (CheckUtil.isEmpty(mEdResult) || "01".equals(mEdResult)) {
                showProgress(true);
                reloadEduResult();
            } else {
                ResourceHelper.requestEdResultMidResource(mMidBannerNetHelper, mEdResult);
            }
        }
        firstResume = false;
    }

    @Override
    protected void onDestroy() {
        destroyHandler();
        if (mHelp != null) {
            mHelp.destory();
        }
        if (mMidBannerNetHelper != null) {
            mMidBannerNetHelper.recoveryNetHelper();
            mMidBannerNetHelper = null;
        }
        super.onDestroy();
    }

    /**
     * 文字设置span
     *
     * @param msg
     * @param start
     * @param length
     * @return
     */
    public SpannableString formatMessage(String msg, int start, int length) {
        if (TextUtils.isEmpty(msg) || start >= msg.length()) {
            return new SpannableString(msg);
        }
        SpannableString spannableString = new SpannableString(msg);
        int end = start + length;
        if (end > msg.length()) {
            end = msg.length() - 1;
        }
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.color_ffff5151));
                ds.clearShadowLayer();
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    private String getPageName() {
        return "额度申请页";
    }

    private void postMidBannerExposure() {
        if (ivMidBanner.getVisibility() == View.VISIBLE) {
            postUmEvent("QuotaApplicationAdPosition_Exposure", true);
        }
    }

    private void postBottomBannerExposure() {
        if (iv_banner.getVisibility() == View.VISIBLE) {
            postUmEvent("BottomAdPosition_Exposure", false);
        }
    }

    private void postUmEvent(String eventId, boolean isMidRes) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("page_name_cn", getPageName());
        map.put("ad_name", isMidRes ? midBannerName : bannerName);
        map.put("cid", isMidRes ? midCid : cid);
        map.put("group_id", isMidRes ? midGroupId : groupId);
        map.put("is_member", "Y".equals(SpHp.getLogin(SpKey.VIP_STATUS)) ? "true" : "false");//是否为会员
        map.put("quota_status", "02".equals(mEdResult) ? "审批成功" : ("03".equals(mEdResult) ? "审批失败" : "审核中"));//具体结果
        UMengUtil.onEventObject(eventId, map, getPageCode());
    }
}
