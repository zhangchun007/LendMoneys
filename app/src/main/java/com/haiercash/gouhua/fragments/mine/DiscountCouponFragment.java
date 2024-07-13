package com.haiercash.gouhua.fragments.mine;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.DiscountCouponDetailActivity;
import com.haiercash.gouhua.adaptor.DiscountCouponAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseListFragment;
import com.haiercash.gouhua.beans.DiscountCouponsBean;
import com.haiercash.gouhua.beans.PopupWindowBean;
import com.haiercash.gouhua.databinding.FrgmDiscountCouponBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.NormalNewPopWindow;
import com.haiercash.gouhua.utils.SpHp;
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
 * 描    述：优惠券功能<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_DISCOUNT_COUPON)
public class DiscountCouponFragment extends BaseListFragment {
    private FrgmDiscountCouponBinding getBinding() {
        return (FrgmDiscountCouponBinding) _binding;
    }

    /**
     * 0失效，1生效
     */
    private String status = "1";

    @Override
    protected ViewBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FrgmDiscountCouponBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initEventAndData() {
        if (getArguments() != null) {
            status = getArguments().getString("status", "1");
        }
        getBinding().headInterest.setVisibility(View.GONE);
        super.initEventAndData();
        getBinding().tvInvalid.setVisibility("1".equals(status) ? View.VISIBLE : View.GONE);
        if ("0".equals(status)) {
            mActivity.setTitle("失效优惠券");
        }
        setonClickByViewId(R.id.tvInvalid);
        int padding = UiUtil.dip2px(mActivity, 13);
        mScrollRefreshLayout.setPadding(0, padding, 0, 0);
        startRefresh(true, false, true, R.drawable.img_empty_discount_coupon, "1".equals(status) ? "暂无可用优惠券" : "暂无失效优惠券");
        mRefreshHelper.setEmptyViewBgResource(R.color.mineBackground);
        mRefreshHelper.getEmptyOrErrorView().setTextSize(14);
        mAdapter.addChildClickViewIds(R.id.llBottom);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.llBottom) {
                PopupWindowBean bean = new PopupWindowBean();
                DiscountCouponsBean.CouponsBean item = (DiscountCouponsBean.CouponsBean) mAdapter.getData().get(position);
                bean.setTitle("使用规则");
                bean.setContent(item.latinosDesc);
                bean.setButtonTv("确认");
                bean.setButtonBack(R.drawable.bg_btn_commit);
                new NormalNewPopWindow(mActivity, bean).showAtLocation(view);
            }
        });
        loadSourceData(1, 0);
    }

    @Override
    public void loadSourceData(int page, int pageSize) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", RSAUtils.encryptByRSA(SpHp.getLogin(SpKey.LOGIN_USERID)));//用户账号，
        map.put("status", status);
        netHelper.postService(ApiUrl.URL_ALL_MEMBER_COUPONS, map, DiscountCouponsBean.class, true);
    }

    @Override
    public DiscountCouponAdapter getAdapter() {
        return new DiscountCouponAdapter();
    }

    @Override
    public void onItemClick(Object item) {
        super.onItemClick(item);
        DiscountCouponsBean.CouponsBean bean = (DiscountCouponsBean.CouponsBean) item;
        if ("valid".equals(bean.state)) {
            if ("COUPON".equals(bean.latinosType)) {
                Intent bundle = new Intent(mActivity, DiscountCouponDetailActivity.class);
                bundle.putExtra("latinosDesc", bean.latinosDesc);
                bundle.putExtra("latinosSub", bean.latinosSub);
                bundle.putExtra("latinosStartDt", bean.latinosStartDt);
                bundle.putExtra("latinosEndDt", bean.latinosEndDt);
                bundle.putExtra("latinosIconPic", bean.latinosIconPic);
                bundle.putExtra("couponCode", bean.couponCode);
                bundle.putExtra("couponPasswd", bean.couponPasswd);
                bundle.putExtra("latinosName", bean.latinosName);
                bundle.putExtra("latinosAmt", bean.latinosAmt);
                bundle.putExtra("passType", bean.passType);
                bundle.putExtra("htmlUrl", bean.link);
                startActivity(bundle);
            } else {
                Intent intent = new Intent(mActivity, JsWebBaseActivity.class);
                //intent.putExtra("isShowWebViewTitle", true);
                intent.putExtra("jumpKey", bean.link);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tvInvalid) {
            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_DISCOUNT_COUPON)
                    .put("isShowTitle", true).put("status", "0").navigation();
        }
    }

    @Override
    public void onSuccess(Object success, String url) {
        showProgress(false);
        if (ApiUrl.URL_ALL_MEMBER_COUPONS.equals(url)) {
            DiscountCouponsBean discountCouponsBean = (DiscountCouponsBean) success;
            if (!CheckUtil.isEmpty(discountCouponsBean)) {
                if ("0".equals(status)) {
                    getBinding().getRoot().setAlpha(0.7f);
                }
                mRefreshHelper.updateData(discountCouponsBean.getCouponsList());
            } else {
                mRefreshHelper.updateData(null);
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        if (!BuildConfig.IS_RELEASE && BuildConfig.DEBUG) {
            List<DiscountCouponsBean.CouponsBean> couponsList = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                DiscountCouponsBean.CouponsBean bean = new DiscountCouponsBean.CouponsBean();
                bean.latinosName = "latinosName名称";
                bean.latinosStartDt = "2021-01-12";
                bean.latinosEndDt = "2021-11-12";
                bean.latinosType = i % 2 == 0 ? "MJQ" : "COUPON";
                bean.latinosAmt = "" + (100 * i + 10);
                bean.usedCondition = "满x00可用";
                if (i % 2 == 0) {
                    bean.state = "invalid";
                } else if (i % 3 == 0) {
                    bean.state = "used";
                } else {
                    bean.state = "valid";
                }
                couponsList.add(bean);
            }
            mRefreshHelper.updateData(couponsList);
        } else {
            mRefreshHelper.updateData(null);
            mRefreshHelper.errorData();
        }
    }
}
