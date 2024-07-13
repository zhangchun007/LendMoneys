package com.haiercash.gouhua.fragments.mine;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.InterestAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseListFragment;
import com.haiercash.gouhua.beans.InterestFreeBean;
import com.haiercash.gouhua.beans.PopupWindowBean;
import com.haiercash.gouhua.databinding.FrgmDiscountCouponBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.activity.InterestFreeStampsGuideDialog;
import com.haiercash.gouhua.uihelper.NormalNewPopWindow;
import com.haiercash.gouhua.utils.ClickCouponToUseUtil;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================================
 * 作    者：L14-14<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2021/12/14-17:24<br/>
 * 描    述：免息券<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_INTEREST_FREE_STAMPS)
public class InterestFreeStampsFragment extends BaseListFragment {
    public static final String TAG = "interest";
    /**
     * 1生效，0失效
     */
    private String status = "1";
    private InterestFreeBean.RepayCouponsBean clickCoupon;//当前点击的免息券
    private ClickCouponToUseUtil couponToUseUtil;

    private FrgmDiscountCouponBinding getBinding() {
        return (FrgmDiscountCouponBinding) _binding;
    }

    @Override
    protected ViewBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FrgmDiscountCouponBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initEventAndData() {
        if (getArguments() != null) {
            status = getArguments().getString("status", "1");
        }
        //getBinding().headInterest.tvHead.setVisibility(View.GONE);
        super.initEventAndData();
        getBinding().tvInvalid.setText("查看失效免息券");
        getBinding().tvInvalid.setVisibility("1".equals(status) ? View.VISIBLE : View.GONE);
        if ("0".equals(status)) {
            mActivity.setTitle("失效免息券");
        }
        couponToUseUtil = new ClickCouponToUseUtil(mActivity);
        setonClickByViewId(R.id.tvInvalid, R.id.headInterest);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {
                    //getBinding().headInterest.tvHead.setVisibility(View.GONE);
                    getBinding().headInterest.setAlpha(0);
                } else {
                    //getBinding().headInterest.tvHead.setVisibility(View.VISIBLE);
                    getBinding().headInterest.setAlpha(1);
                }
            }
        });
        showProgress(true);
        startRefresh(true, false, true, R.drawable.icon_empty_interest_free, "1".equals(status) ? "暂无可用免息券" : "暂无失效免息券");
        mRefreshHelper.setEmptyViewBgResource(R.color.mineBackground);
        mRefreshHelper.getEmptyOrErrorView().setTextSize(14);
        View headView = new View(mActivity);
        headView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiUtil.dip2px(mActivity, 36)));//item预留坐上角标，预留4dp，所以是40-4=36dp，引导图如是
        mAdapter.addHeaderView(headView);
        mAdapter.addChildClickViewIds(R.id.tv_rule, R.id.tvUser);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.tv_rule) {
                postUmClickEvent("FreeCardDirection_Click", clickCoupon, view);
                PopupWindowBean bean = new PopupWindowBean();
                InterestFreeBean.RepayCouponsBean item = (InterestFreeBean.RepayCouponsBean) mAdapter.getData().get(position);
                bean.setTitle("使用规则");
                bean.setContent(item.getBatchDetailDesc());
                bean.setButtonTv("确认");
                bean.setButtonBack(R.drawable.bg_btn_commit);
                new NormalNewPopWindow(mActivity, bean).showAtLocation(view);
            } else if (view.getId() == R.id.tvUser) {
                //不放在外面以防多布局多类型时其他item不是此类型
                clickCoupon = (InterestFreeBean.RepayCouponsBean) mAdapter.getData().get(position);
                postUmClickEvent("FreeCard_Click", clickCoupon, view);
                couponToUseUtil.clickCouponToUse(clickCoupon);
            }
        });
        showInterestFreeStampsGuideDialog();
    }


    private void showInterestFreeStampsGuideDialog() {
        getBinding().topLocationDot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!"Y".equals(SpHp.getOther(SpKey.INTEREST_FREE_STAMPS_GUIDE, "N"))) {
                    int[] xy = new int[2];
                    getBinding().topLocationDot.getLocationInWindow(xy);
                    //计算InterestFreeStampsGuideDialog实际内容与顶距离
                    int dialogTop = xy[1];
                    Intent intent = new Intent(mActivity, InterestFreeStampsGuideDialog.class);
                    intent.putExtra("topMargin", dialogTop);
                    startActivity(intent);
                    SpHp.saveSpOther(SpKey.INTEREST_FREE_STAMPS_GUIDE, "Y");
                }
            }
        });

    }

    @Override
    public InterestAdapter getAdapter() {
        return new InterestAdapter();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tvInvalid) {
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_INTEREST_FREE_STAMPS).put("isShowTitle", true).put("status", "0").navigation();
        } else if (v.getId() == R.id.headInterest) {
            PopupWindowBean bean = new PopupWindowBean();
            bean.setTitle("免息券通用说明");
            bean.setContent(this.getResources().getText(R.string.interest_free_des).toString());
            bean.setButtonTv("我知道了");
            bean.setButtonBack(R.drawable.bg_btn_commit);
            new NormalNewPopWindow(mActivity, bean).showAtLocation(mRecyclerView);
        }
    }

    @Override
    public void loadSourceData(int page, int pageSize) {
        Map<String, String> map = new HashMap<>();
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));
        map.put("userId", RSAUtils.encryptByRSA(SpHp.getLogin(SpKey.LOGIN_USERID)));
        map.put("page", "1");
        map.put("size", "100");
        if ("1".equals(status)) {
            map.put("state", "2");
        } else {
            map.put("state", "3,4,6");
        }
        //map.put("")
        netHelper.postService(ApiUrl.URL_ALL_REPAY_COUPONS, map);
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        super.onSuccess(t, url);
        if (ApiUrl.URL_ALL_REPAY_COUPONS.equals(url)) {
            showProgress(false);
            List<InterestFreeBean.RepayCouponsBean> list = JsonUtils.fromJsonArray(t, InterestFreeBean.RepayCouponsBean.class);
            if (!CheckUtil.isEmpty(list)) {
                if ("0".equals(status)) {
                    getBinding().getRoot().setAlpha(0.7f);
                }
                mRefreshHelper.updateData(list);
            } else {
                mRefreshHelper.updateData(new ArrayList<>());
            }
            mRefreshHelper.setLoadMoreEnd(true);
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        showProgress(false);
        mRefreshHelper.errorData();
    }


    @Override
    public void onDestroyView() {
        mRecyclerView.clearOnScrollListeners();
        if (couponToUseUtil != null) {
            couponToUseUtil.onDestroy();
        }
        super.onDestroyView();
    }

    private void postUmClickEvent(String clickEventId, InterestFreeBean.RepayCouponsBean bean, View view) {
        if (bean == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("page_name_cn", "我的-免息券");
        map.put("free_card_No", UiUtil.getEmptyStr(bean.getBatchNo()));
        map.put("free_card_id", UiUtil.getEmptyStr(bean.getCouponNo()));
        map.put("free_card_type", UiUtil.getEmptyStr(bean.getKindName()));
        //具体可在哪一期使用
        map.put("free_card_certain_period", UiUtil.getEmptyStr(bean.getBindPerdNo()));
        //可使用在最低总期数为多少的产品上使用（期数类型3期、6期等）
//        map.put("free_card_loan_periods", bean.getBindPerdNo());
        String date = "";
        if (!CheckUtil.isEmpty(bean.getBindStartDt())) {
            date += TimeUtil.getNeedDate(bean.getBindStartDt());
        }
        date += "-";
        if (!CheckUtil.isEmpty(bean.getBindEndDt())) {
            date += TimeUtil.getNeedDate(bean.getBindEndDt());
        }
        map.put("free_card_timelimit", date);
        map.put("free_card_name", "免息券");
        UMengUtil.commonClickEvent(clickEventId, view instanceof TextView ? ((TextView) view).getText().toString() : "", map, getPageCode());
    }

    @Override
    protected String getPageCode() {
        return "FreeCard";
    }
}