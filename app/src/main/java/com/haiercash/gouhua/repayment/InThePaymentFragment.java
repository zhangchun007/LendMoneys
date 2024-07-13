package com.haiercash.gouhua.repayment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.RePayAndRecordActivity;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.activity.comm.ResourceHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.ResourceBean;
import com.haiercash.gouhua.beans.repayment.PaymentResult;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by use on 2017/6/29.
 * hqx
 * 还款结果页
 */
public class InThePaymentFragment extends BaseFragment {

    public static final int ID = InThePaymentFragment.class.hashCode();
    private static final String REPAY_RESULT_MONEY = "resultMoney";
    private static final String REPAY_RESULT_LIST = "resultList";

    @BindView(R.id.tvMoney)
    TextView tvMoney;

    @BindView(R.id.tv_paying)
    TextView tvPaying;
    @BindView(R.id.v_line2)
    View vLine2;
    @BindView(R.id.iv3)
    ImageView iv3;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.tvResultMoney)
    TextView tvResultMoney;
    @BindView(R.id.iv_mid_banner)
    ImageView ivMidBanner;
    @BindView(R.id.iv_banner)
    ImageView ivBanner;
    private String isSuccess;
    private String reason = "";
    private String midBannerJumpUrl, bannerJumpUrl;
    private String midBannerName, bannerName, midCid, cid, midGroupId, groupId;
    private NetHelper mMidBannerNetHelper;

    public static boolean TAG_REPAYMENT_RESULT = false;

    public static InThePaymentFragment newInstance(Bundle bd) {
        final InThePaymentFragment f = new InThePaymentFragment();
        if (bd != null) {
            f.setArguments(bd);
        }
        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.in_thepaymentfragment;
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle("还款结果");
        tvMoney.setTypeface(FontCustom.getMediumFont(mActivity));
        tvResultMoney.setTypeface(FontCustom.getMediumFont(mActivity));
        initMidBannerNetHelper();
        ArrayList<PaymentResult> list = null;
        if (getArguments() != null) {
            tvMoney.setText(UiUtil.getStr(getArguments().getString(REPAY_RESULT_MONEY), "元"));
            tvResultMoney.setText(UiUtil.getStr(getArguments().getString(REPAY_RESULT_MONEY), "元"));
            TAG_REPAYMENT_RESULT = true;
            list = (ArrayList<PaymentResult>) getArguments().getSerializable(REPAY_RESULT_LIST);
        }
        //01：还款处理中
        //02：还款成功
        //03：还款失败
        if (list != null && list.size() > 0) {
            for (PaymentResult result : list) {
                if ("03".equals(result.getSetlSts()) || "03".equals(result.getRepaySts())) {
                    isSuccess = "false";
                    reason = TextUtils.isEmpty(result.getFailReason()) ? "" : result.getFailReason();
                    initViewResult(2);
                    break;
                }
                if ("01".equals(result.getSetlSts()) || "01".equals(result.getRepaySts())) {
                    isSuccess = null;
                    reason = "还款处理中";
                    initViewResult(0);
                    break;
                }
                if ("02".equals(result.getSetlSts()) || "02".equals(result.getRepaySts())) {
                    isSuccess = "true";
                    reason = "";
                    initViewResult(1);
                    break;
                }
            }
        } else {
            isSuccess = "false";
            reason = "";
            initViewResult(0);
        }
        ResourceHelper.requestRepaymentBottomResource(netHelper);
        //4.0新增，测试压力大暂时去除，后期需要添加只需要去掉注释就行
       /* if (!mActivity.isShowingDialog()) {
            controlDialogUtil.checkDialog("pay_rest");
        }*/
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
                        GlideUtils.loadForWidth(mActivity, resourceContentBean.getPicUrl(), ivMidBanner,
                                SystemUtils.getDeviceWidth(mActivity) - 2 * UiUtil.dip2px(mActivity, 15),
                                UiUtil.dip2px(mActivity, 8),
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

    private void initViewResult(int result) {
        if (result == 1) {
            tvMoney.setVisibility(View.GONE);
            tvResultMoney.setVisibility(View.VISIBLE);
            tvPaying.setTextColor(ContextCompat.getColor(mActivity, R.color.text_gray_dark));
            iv3.setImageResource(R.drawable.pay_result_sucess);
            tvResult.setText("还款成功");
            tvResult.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        } else if (result == 2) {
            tvMoney.setVisibility(View.GONE);
            tvResultMoney.setVisibility(View.VISIBLE);
            tvPaying.setTextColor(ContextCompat.getColor(mActivity, R.color.text_gray_dark));
            iv3.setImageResource(R.drawable.pay_result_fail);
            tvResult.setText("还款失败");
            tvResult.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        } else {
            tvMoney.setVisibility(View.VISIBLE);
            tvResultMoney.setVisibility(View.GONE);
            tvPaying.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            iv3.setImageResource(R.drawable.submit_result);
            tvResult.setText("还款结果");
            tvResult.setTextColor(ContextCompat.getColor(mActivity, R.color.text_gray_light));
        }
    }

    @Override
    public void onSuccess(Object response, String flag) {
        if (response == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        if (ApiUrl.POST_QUERY_RESOURCE_BY_PAGE.equals(flag)) {
            ResourceBean resourceBean = (ResourceBean) response;
            bannerName = resourceBean.getResourceName();
            cid = resourceBean.getCid();
            groupId = resourceBean.getGroupId();
            List<ResourceBean.ContentBean> list = resourceBean.getContents();
            if (!CheckUtil.isEmpty(list)) {
                bannerJumpUrl = list.get(0).getH5Url();
                String url = list.get(0).getPicUrl();
                GlideUtils.loadForWidth(mActivity, url, ivBanner,
                        SystemUtils.getDeviceWidth(mActivity),
                        UiUtil.dip2px(mActivity, 8),
                        new INetResult() {
                            @Override
                            public <T> void onSuccess(T t, String url) {
                                if (ivBanner != null) {
                                    ivBanner.setVisibility(View.VISIBLE);
                                    postBottomBannerExposure();
                                }
                            }

                            @Override
                            public void onError(BasicResponse error, String url) {
                                if (ivBanner != null) {
                                    ivBanner.setImageResource(R.drawable.wx_banner);
                                    ivBanner.setVisibility(View.VISIBLE);
                                    postBottomBannerExposure();
                                }
                            }
                        });
            }
        }
    }

    @OnClick({R.id.btn_loan, R.id.next, R.id.iv_mid_banner, R.id.iv_banner})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_loan:
                UMengUtil.commonClickEvent("SeeRepayList_Click", "查看还款记录", getPageCode());
                Intent intent = new Intent(mActivity, RePayAndRecordActivity.class);
                intent.putExtra("tabRePayListFragment", 2);
                startActivity(intent);
                mActivity.finish();
                break;
            case R.id.next:
                UMengUtil.commonClickEvent("RepayResultGoHomePageSee_Click", "去首页看看", getPageCode());
                //返回首页
                ActivityUntil.finishOthersActivity(MainActivity.class);
                MainActivity activity = ActivityUntil.findActivity(MainActivity.class);
                if (activity != null) {
                    activity.setFragment();
                }
                //mActivity.finish();
                break;
            case R.id.iv_mid_banner:
                postUmEvent("RepayResultMemberAdPosition_Click", true);
                WebHelper.startActivityForUrl(mActivity, midBannerJumpUrl);
                break;
            case R.id.iv_banner:
                postUmEvent("RepayResultAdPosition_Click", false);
                if (WebHelper.startActivityForUrl(mActivity, bannerJumpUrl)) {
                    mActivity.finish();
                }
                break;
        }
    }

    static void toRePayMentResult(Context context, String odAmount, ArrayList<PaymentResult> list) {
        Bundle bundle = new Bundle();
        bundle.putString(REPAY_RESULT_MONEY, odAmount);
        bundle.putSerializable(REPAY_RESULT_LIST, list);
        ContainerActivity.to(context, ID, bundle);
    }

    @Override
    protected String getPageCode() {
        return "RepayResultPage";
    }

    @Override
    public void onResume() {
        super.onResume();
        postBottomBannerExposure();
        UMengUtil.commonCompleteEvent("RepayResult", null, isSuccess, reason, getPageCode());
        ResourceHelper.requestRepaymentMidResource(mMidBannerNetHelper);
    }

    @Override
    public void onDestroyView() {
        if (mMidBannerNetHelper != null) {
            mMidBannerNetHelper.recoveryNetHelper();
            mMidBannerNetHelper = null;
        }
        super.onDestroyView();
    }

    private String getPageName() {
        return "还款结果页";
    }

    private void postMidBannerExposure() {
        if (ivMidBanner.getVisibility() == View.VISIBLE) {
            postUmEvent("RepayResultMemberAdPosition_Exposure", true);
        }
    }

    private void postBottomBannerExposure() {
        if (ivBanner.getVisibility() == View.VISIBLE) {
            postUmEvent("RepayResultAdPosition_Exposure", false);
        }
    }

    //中部资源位曝光事件埋点和点击事件埋点通用
    private void postUmEvent(String eventId, boolean isMidRes) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("repay_result", "true".equals(isSuccess) ? "成功" : "false".equals(isSuccess) ? "失败" : "审核中");
        UMengUtil.commonClickBannerEvent(eventId, getPageName(), isMidRes ? midBannerName : bannerName,
                isMidRes ? midCid : cid,
                isMidRes ? midGroupId : groupId,
                map, getPageCode());
    }
}
