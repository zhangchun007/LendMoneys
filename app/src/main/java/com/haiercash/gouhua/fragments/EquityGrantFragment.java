package com.haiercash.gouhua.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.GlideUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/2/12<br/>
 * 描    述：额度申请被拒权益发放<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_EQUITY_GRANT)
public class EquityGrantFragment extends BaseFragment {
    @BindView(R.id.iv_eg1)
    ImageView ivEg1;
    @BindView(R.id.iv_eg2)
    ImageView ivEg2;
    @BindView(R.id.iv_eg3)
    ImageView ivEg3;
    private List<String> imgLink;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_equity_grant;
    }

    @Override
    protected void initEventAndData() {
        if (getArguments() != null) {
            List<String> adds = getArguments().getStringArrayList("imgAddress");
            imgLink = getArguments().getStringArrayList("imgLink");
            if (adds != null) {
                if (adds.size() == 1) {
                    GlideUtils.loadCenterCrop(mActivity, ivEg1, ApiUrl.urlAdPic + adds.get(0), R.drawable.bg_credit_default);
                    ivEg1.setVisibility(View.VISIBLE);
                } else if (adds.size() == 2) {
                    GlideUtils.loadCenterCrop(mActivity, ivEg1, ApiUrl.urlAdPic + adds.get(0), R.drawable.bg_credit_default);
                    GlideUtils.loadCenterCrop(mActivity, ivEg2, ApiUrl.urlAdPic + adds.get(1), R.drawable.bg_credit_default);
                    ivEg1.setVisibility(View.VISIBLE);
                    ivEg2.setVisibility(View.VISIBLE);
                } else {
                    GlideUtils.loadCenterCrop(mActivity, ivEg1, ApiUrl.urlAdPic + adds.get(0), R.drawable.bg_credit_default);
                    GlideUtils.loadCenterCrop(mActivity, ivEg2, ApiUrl.urlAdPic + adds.get(1), R.drawable.bg_credit_default);
                    GlideUtils.loadCenterCrop(mActivity, ivEg3, ApiUrl.urlAdPic + adds.get(2), R.drawable.bg_credit_default);
                    ivEg1.setVisibility(View.VISIBLE);
                    ivEg2.setVisibility(View.VISIBLE);
                    ivEg3.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    @OnClick({R.id.iv_eg1, R.id.iv_eg2, R.id.iv_eg3, R.id.btn_next})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_eg1:
                if (imgLink != null && imgLink.size() >= 1) {
                    //WebSimpleFragment.WebService(mActivity, imgLink.get(0), "够花", WebSimpleFragment.STYLE_OTHERS);
                    showWebSimple(imgLink.get(0));
                }
                break;
            case R.id.iv_eg2:
                if (imgLink != null && imgLink.size() >= 2) {
                    //WebSimpleFragment.WebService(mActivity, imgLink.get(1), "够花", WebSimpleFragment.STYLE_OTHERS);
                    showWebSimple(imgLink.get(1));
                }
                break;
            case R.id.iv_eg3:
                if (imgLink != null && imgLink.size() >= 3) {
                    //WebSimpleFragment.WebService(mActivity, imgLink.get(2), "够花", WebSimpleFragment.STYLE_OTHERS);
                    showWebSimple(imgLink.get(2));
                }
                break;
            case R.id.btn_next:
                onBackPressed();
                break;
        }
    }

    private void showWebSimple(String url) {
        Intent intent = new Intent(mActivity, JsWebBaseActivity.class);
        intent.putExtra("jumpKey", url);
        intent.putExtra("title", "够花");
        intent.putExtra("isShowWebViewTitle", true);
        startActivity(intent);
    }

    @Override
    public boolean onBackPressed() {
        ARouterUntil.getInstance(PagePath.ACTIVITY_MAIN).navigation();
        ActivityUntil.finishOthersActivityByPageKey(PagePath.ACTIVITY_MAIN);
        return true;
    }
}
