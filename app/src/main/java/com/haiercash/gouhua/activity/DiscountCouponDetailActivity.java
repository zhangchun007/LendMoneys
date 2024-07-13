package com.haiercash.gouhua.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.UiUtil;

import java.net.URL;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 优惠券详情
 */
public class DiscountCouponDetailActivity extends BaseActivity {
    @BindView(R.id.iv_dicount_detail)
    ImageView ivDicountDetail;
    @BindView(R.id.iv_dicount_detail_name)
    TextView ivDicountDetailName;
    @BindView(R.id.iv_dicount_detail_title)
    TextView ivDicountDetailTitle;
    @BindView(R.id.iv_dicount_detail_sub)
    TextView ivDicountDetailSub;
    @BindView(R.id.tv_coupon_code)
    TextView tvCouponCode;
    @BindView(R.id.rl_coupon_code)
    RelativeLayout rlCouponCode;
    @BindView(R.id.tv_coupon_pwd)
    TextView tvCouponPwd;
    @BindView(R.id.rl_coupon_pwd)
    RelativeLayout rlCouponPwd;
    @BindView(R.id.tv_start_end_data)
    TextView tvStartEndData;
    @BindView(R.id.tv_dicount_des_content)
    TextView tvDicountDesContent;

    @BindView(R.id.tv_copy_code)
    TextView tv_copy_code;

    @BindView(R.id.layout_dicount_detail_sub)
    ConstraintLayout layoutDiscountDetailSub;

    private String passType;
    private String htmlUrl;

    @Override
    protected int getLayout() {
        return R.layout.activity_discount_coupon_detail;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        SystemUtils.setWindowSecure(this);
        setTitle("优惠券");
        if (getIntent() == null) {
            return;
        }
        String latinosDesc = getIntent().getStringExtra("latinosDesc");
        String latinosSub = getIntent().getStringExtra("latinosSub");
        String latinosStartDt = getIntent().getStringExtra("latinosStartDt");
        String latinosEndDt = getIntent().getStringExtra("latinosEndDt");
        String latinosIconPic = getIntent().getStringExtra("latinosIconPic");
        String couponCode = getIntent().getStringExtra("couponCode");
        String couponPasswd = getIntent().getStringExtra("couponPasswd");
        String latinosName = getIntent().getStringExtra("latinosName");
        String latinosAmt = getIntent().getStringExtra("latinosAmt");
        //卡密类型,CODE_PAS：券码+卡密；PAS：卡密；URL：链接
        passType = getIntent().getStringExtra("passType");
        htmlUrl = couponPasswd;

        if ("CODE_PAS".equals(passType)) {//券码+卡密
            rlCouponCode.setVisibility(CheckUtil.isEmpty(couponCode) ? View.GONE : View.VISIBLE);
            rlCouponPwd.setVisibility(CheckUtil.isEmpty(couponPasswd) ? View.GONE : View.VISIBLE);
        } else if ("PAS".equals(passType)) {//卡密
            rlCouponCode.setVisibility(CheckUtil.isEmpty(couponPasswd) ? View.GONE : View.VISIBLE);
            rlCouponPwd.setVisibility(View.GONE);
        } else if ("URL".equals(passType)) {//URL
            rlCouponCode.setVisibility(CheckUtil.isEmpty(htmlUrl) ? (View.GONE) : View.VISIBLE);
            rlCouponPwd.setVisibility(View.GONE);
            tv_copy_code.setText("去使用");
        }

        if (!CheckUtil.isEmpty(latinosIconPic)) {
            GlideUtils.loadFit(this, ivDicountDetail, latinosIconPic);
        } else {
            ivDicountDetail.setImageResource(R.drawable.img_discount_defaule);
        }
        ivDicountDetailName.setText(latinosName);
        ivDicountDetailTitle.setText(latinosSub);

        if (!CheckUtil.isEmpty(latinosAmt)){
            layoutDiscountDetailSub.setVisibility(View.VISIBLE);
            ivDicountDetailSub.setText(latinosAmt);
        }else {
            layoutDiscountDetailSub.setVisibility(View.GONE);
        }

        if ("URL".equals(passType) && !CheckUtil.isEmpty(htmlUrl)) {
            tvCouponCode.setText(UiUtil.getStr("链接：", htmlUrl));
        }else if ("CODE_PAS".equals(passType)){
            tvCouponCode.setText(UiUtil.getStr("券码：", couponCode));
        }else if ("PAS".equals(passType)){
            tvCouponCode.setText(UiUtil.getStr("券码：", couponPasswd));
        }
        tvCouponPwd.setText(UiUtil.getStr("卡密：", couponPasswd));
        String endDate = "";
        if (!CheckUtil.isEmpty(latinosStartDt)) {
            endDate += latinosStartDt.replace("-", ".");
        }
        endDate += "-";
        if (!CheckUtil.isEmpty(latinosEndDt)) {
            endDate += latinosEndDt.replace("-", ".");
        }
        tvStartEndData.setText(endDate);
        tvDicountDesContent.setText(CheckUtil.isEmpty(latinosDesc) ? "" : latinosDesc.replace("；", "；\n"));
    }

    @OnClick({R.id.tv_copy_code, R.id.tv_copy_pwd})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_copy_code:
                if ("URL".equals(passType) && !CheckUtil.isEmpty(htmlUrl)) { //URL：链接
                    Intent intent = new Intent(this, JsWebBaseActivity.class);
                    intent.putExtra("jumpKey", htmlUrl);
                    startActivity(intent);
                } else {
                    UiUtil.copyValueToShearPlate(this, tvCouponCode.getText().toString().replace("券码：", ""));
                }
                break;
            case R.id.tv_copy_pwd:
                UiUtil.copyValueToShearPlate(this, tvCouponPwd.getText().toString().replace("卡密：", ""));
                break;
        }
    }
}
