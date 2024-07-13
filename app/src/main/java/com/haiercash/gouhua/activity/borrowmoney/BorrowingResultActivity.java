package com.haiercash.gouhua.activity.borrowmoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.RePayAndRecordActivity;
import com.haiercash.gouhua.activity.comm.ResourceHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.CopyWriterBean;
import com.haiercash.gouhua.beans.ProcessBean;
import com.haiercash.gouhua.beans.ResourceBean;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.widget.MarqueeTextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Limige on 2017-07-03.
 * 借款结果页
 */
public class BorrowingResultActivity extends BaseActivity {
    @BindView(R.id.tv_result)
    TextView mTvResult;
    @BindView(R.id.tv_applyAmt0)
    TextView mTvApplyAmt0;
    @BindView(R.id.tv_applyAmt)
    TextView mTvApplyAmt;
    @BindView(R.id.iv_mid_banner)
    ImageView ivMidBanner;
    @BindView(R.id.iv_banner)
    ImageView iv_banner;
    @BindView(R.id.iv_result_bg)
    ImageView ivResultBg;
    @BindView(R.id.iv_result)
    ImageView ivResult;
    @BindView(R.id.tv_check_recode)
    TextView tvCheckRecode;
    @BindView(R.id.tv_fail_desc)
    TextView tvFailDesc;
    @BindView(R.id.tv_notice)
    MarqueeTextView tv_notice;
    @BindView(R.id.sv_borrow_result)
    NestedScrollView svBorrowResult;
    @BindView(R.id.v_borrow_result_header)
    View vBorrowResultHeader;

    private String applSeq;//流水号
    private String midBannerJumpUrl, bannerJumpUrl;
    private String midBannerName, bannerName, midCid, cid, midGroupId, groupId;
    private NetHelper mMidBannerNetHelper;
    private String mLoanResult;//成功02，失败03，审核中01

    @Override
    protected int getLayout() {
        return R.layout.activity_borrowing_result;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("");
        setLeftIvVisibility(false);
        mTvResult.setTypeface(FontCustom.getMediumFont(this));
        mTvApplyAmt0.setTypeface(FontCustom.getDinAlternateBoldFont(this));
        mTvApplyAmt.setTypeface(FontCustom.getDinFont(this));
        initMidBannerNetHelper();
        applSeq = getIntent().getStringExtra("applSeq");
        GlideUtils.loadDrawableSourceGif(this, R.drawable.ic_borrow_result_waiting, ivResult);
        ResourceHelper.requestBorrowResultBottomResource(netHelper);

        Map<String, String> map = new HashMap<>();
        map.put("copyWriterPosition", "2");
        netHelper.getService(ApiUrl.URL_CONFIG_NOTICE, map, CopyWriterBean.class);
        //4.0新增，测试压力大暂时去除，后期需要添加只需要去掉注释就行
        /*if (!isShowingDialog()) {
            controlDialogUtil.checkDialog("loan_appl");
        }*/
        svBorrowResult.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                vBorrowResultHeader.setAlpha(svBorrowResult.getScrollY() == 0 ? 0 : 1);
            }
        });
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
                        if (ivMidBanner != null) {
                            ivMidBanner.setVisibility(View.GONE);
                        }
                    } else {
                        midBannerName = resourceBean.getResourceName();
                        midCid = resourceBean.getCid();
                        midGroupId = resourceBean.getGroupId();
                        ResourceBean.ContentBean resourceContentBean = resourceBean.getContents().get(0);
                        midBannerJumpUrl = resourceContentBean.getH5Url();
                        GlideUtils.loadForWidth(BorrowingResultActivity.this, resourceContentBean.getPicUrl(), ivMidBanner,
                                SystemUtils.getDeviceWidth(BorrowingResultActivity.this) - 2 * UiUtil.dip2px(BorrowingResultActivity.this, 15),
                                UiUtil.dip2px(BorrowingResultActivity.this, 8),
                                new INetResult() {
                                    @Override
                                    public <T> void onSuccess(T t, String url) {
                                        if (ivMidBanner != null) {
                                            ivMidBanner.setVisibility(View.VISIBLE);
                                            postMidBannerExposure();
                                        }
                                    }

                                    @Override
                                    public void onError(BasicResponse error, String url) {
                                        if (ivMidBanner != null) {
                                            ivMidBanner.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                }
            }

            @Override
            public void onError(BasicResponse error, String url) {

            }
        });
    }

    @OnClick({R.id.iv_mid_banner, R.id.iv_banner, R.id.tv_check_recode, R.id.result_head_rightText})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.result_head_rightText:
                //4.2.8版本接产品需求，将按钮文案"完成"改为"去首页看看"
                UMengUtil.commonClickEvent("GoHomePageSee_Click", "去首页看看", getPageCode());
                startActivity(new Intent(BorrowingResultActivity.this, MainActivity.class));
                break;
            case R.id.iv_mid_banner:
                postUmEvent("BorrowResultMemberAdPosition_Click", true);
                WebHelper.startActivityForUrl(this, midBannerJumpUrl);
                break;
            case R.id.iv_banner:
                postUmEvent("BorrowResultAdPosition_Click", false);
                if (WebHelper.startActivityForUrl(this, bannerJumpUrl)) {
                    finish();
                }
                break;
            case R.id.tv_check_recode:
                UMengUtil.commonClickEvent("SeeBorrowRecord_Click", "查看借款记录", getPageCode());
                MainActivity activity = ActivityUntil.findActivity(MainActivity.class);
                if (activity != null) {
                    activity.setFragment(R.id.rbMine);
                }
                startActivity(new Intent(this, MainActivity.class));
                Intent intent = new Intent(this, RePayAndRecordActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    /*12.5.	(Get)额度或贷款审批进度查询-够花*/
    private void approvalProcessBySeq() {
        if (CheckUtil.isEmpty(applSeq)) {
            showDialog("流水号获取失败，请退出重试");
            return;
        }
        showProgress(true);
        Map<String, String> params = new HashMap<>();
        params.put("applSeq", applSeq);
        // HttpDAO dao = new HttpDAO(this, ProcessBean.class);
        netHelper.getService(ApiUrl.URL_PROCESS, params, ProcessBean.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(Object response, String url) {
        if (response == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        if (ApiUrl.URL_PROCESS.equals(url)) {
            ProcessBean pBean = (ProcessBean) response;
            mTvApplyAmt.setText(CheckUtil.toThousands(CheckUtil.formattedAmount(pBean.getApplyAmt())));
            updateResultUi(pBean.getAppStatus());
            showProgress(false);
        } else if (ApiUrl.POST_QUERY_RESOURCE_BY_PAGE.equals(url)) {
            ResourceBean resourceBean = (ResourceBean) response;
            bannerName = resourceBean.getResourceName();
            cid = resourceBean.getCid();
            groupId = resourceBean.getGroupId();
            List<ResourceBean.ContentBean> list = resourceBean.getContents();
            if (!CheckUtil.isEmpty(list)) {
                bannerJumpUrl = list.get(0).getH5Url();
                String imageUrl = list.get(0).getPicUrl();
                GlideUtils.loadForWidth(BorrowingResultActivity.this, imageUrl, iv_banner,
                        SystemUtils.getDeviceWidth(BorrowingResultActivity.this), UiUtil.dip2px(BorrowingResultActivity.this, 8),
                        new INetResult() {
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
                                    iv_banner.setImageResource(R.drawable.wx_banner);
                                    iv_banner.setVisibility(View.VISIBLE);
                                    postBottomBannerExposure();
                                }
                            }
                        });
            }
        } else if (ApiUrl.URL_CONFIG_NOTICE.equals(url)) {
            CopyWriterBean bean = (CopyWriterBean) response;
            if (CheckUtil.isEmpty(bean.getContent())) {
                tv_notice.setVisibility(View.GONE);
            } else {
                tv_notice.setText(bean.getContent());
                tv_notice.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateResultUi(String appStatus) {
        mLoanResult = appStatus;
        switch (mLoanResult) {
            case "02"://成功
                ivResultBg.setVisibility(View.GONE);
                ivResult.setImageResource(R.drawable.ic_borrow_result_success);
                mTvResult.setText("借款成功");
                tvCheckRecode.setVisibility(View.VISIBLE);
                tvFailDesc.setVisibility(View.GONE);
                break;
            case "03"://失败
                ivResultBg.setVisibility(View.GONE);
                ivResult.setImageResource(R.drawable.ic_borrow_result_fail);
                mTvResult.setText("借款失败");
                tvCheckRecode.setVisibility(View.GONE);
                tvFailDesc.setVisibility(View.VISIBLE);
                break;
            default://审批中
                ivResultBg.setVisibility(View.VISIBLE);
                GlideUtils.loadDrawableSourceGif(this, R.drawable.ic_borrow_result_waiting, ivResult);
                mTvResult.setText("借款审批中");
                tvCheckRecode.setVisibility(View.VISIBLE);
                tvFailDesc.setVisibility(View.GONE);
                break;
        }
        ResourceHelper.requestBorrowResultMidResource(mMidBannerNetHelper, mLoanResult);
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.URL_CONFIG_NOTICE.equals(url)) {
            return;
        }
        if (ApiUrl.URL_PROCESS.equals(url)) {
            ResourceHelper.requestBorrowResultMidResource(mMidBannerNetHelper, mLoanResult);
        }
        super.onError(error, url);
    }

    @Override
    protected String getPageCode() {
        return "BorrowResultPage";
    }

    private String getPageName() {
        return "借款结果页";
    }

    @Override
    protected void onResume() {
        super.onResume();
        postBottomBannerExposure();
        //审核中才需要刷结果，结果已出的只用刷资源位
        if (CheckUtil.isEmpty(mLoanResult) || "01".equals(mLoanResult)) {
            approvalProcessBySeq();
        } else {
            ResourceHelper.requestBorrowResultMidResource(mMidBannerNetHelper, mLoanResult);
        }
    }

    @Override
    protected void onDestroy() {
        if (mMidBannerNetHelper != null) {
            mMidBannerNetHelper.recoveryNetHelper();
            mMidBannerNetHelper = null;
        }
        super.onDestroy();
    }

    private void postMidBannerExposure() {
        if (ivMidBanner.getVisibility() == View.VISIBLE) {
            postUmEvent("BorrowResultMemberAdPosition_Exposure", true);
        }
    }

    private void postBottomBannerExposure() {
        if (iv_banner.getVisibility() == View.VISIBLE) {
            postUmEvent("BorrowResultAdPosition_Exposure", false);
        }
    }

    //中部资源位曝光事件埋点和点击事件埋点通用
    private void postUmEvent(String eventId, boolean isMidRes) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("loan_result", "02".equals(mLoanResult) ? "成功" : ("03".equals(mLoanResult) ? "失败" : "审核中"));
        UMengUtil.commonClickBannerEvent(eventId, getPageName(), isMidRes ? midBannerName : bannerName,
                isMidRes ? midCid : cid,
                isMidRes ? midGroupId : groupId,
                map, getPageCode());
    }
}
