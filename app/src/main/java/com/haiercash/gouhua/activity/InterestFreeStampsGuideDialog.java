package com.haiercash.gouhua.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.utils.UiUtil;

/**
 * 免息券引导弹窗
 */
public class InterestFreeStampsGuideDialog extends BaseActivity implements View.OnClickListener {

    private int status;//status=1第一个页面，2第二个页面，以此类推
    private View vContent;
    private ImageView ivGuide;
    private int y;

    @Override
    protected int getLayout() {
        return R.layout.dialog_interest_free_stamps_guide;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.activity_alpha_out);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        y=getIntent().getIntExtra("topMargin",0);
        vContent = findViewById(R.id.vContent);
        ivGuide = findViewById(R.id.ivGuide);
        ((TextView) findViewById(R.id.tv_rule)).setTextColor(0xFF909199);
        ((TextView) findViewById(R.id.tv_rule)).setTextSize(11f);
        findViewById(R.id.vBg).setBackgroundResource(R.drawable.bg_item_coupon);
        findViewById(R.id.vDialog).setOnClickListener(this);
        setData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.vDialog) {
            status++;
            if (status >= 2) {
                finish();
                return;
            }
            setData();
        }
    }

    private void setData() {
        ConstraintLayout.LayoutParams rlLm = (ConstraintLayout.LayoutParams) vContent.getLayoutParams();
        ConstraintLayout.LayoutParams ivGuildLm = (ConstraintLayout.LayoutParams) ivGuide.getLayoutParams();
        if (status == 0) {
            rlLm.topMargin = y;
            ivGuildLm.topMargin = UiUtil.dip2px(this, 2);
            ivGuildLm.width = UiUtil.dip2px(this, 278);
            ivGuildLm.height = UiUtil.dip2px(this, 170);
            ivGuide.setImageResource(R.drawable.bg_dialog_interest_free_stamps_guide_1);
            findViewById(R.id.ivLeftTopFlag).setVisibility(View.GONE);
            findViewById(R.id.tvMoneyType).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.tvMoney)).setText("5折");
            ((TextView) findViewById(R.id.tvCouponType)).setText("息费折扣券");
            ((TextView) findViewById(R.id.tvLatinosName)).setText("够花老用户息费五折");
            ((TextView) findViewById(R.id.tvLatinosDes)).setText("2021.07.22-2021.09.30");
            ((TextView) findViewById(R.id.tvUser)).setText("去使用");
            findViewById(R.id.tvUser).setVisibility(View.VISIBLE);
        } else if (status == 1) {
            rlLm.topMargin = y;
            ivGuildLm.topMargin = UiUtil.dip2px(this, 1);
            ivGuildLm.width = UiUtil.dip2px(this, 284);
            ivGuildLm.height = UiUtil.dip2px(this, 171);
            ivGuide.setImageResource(R.drawable.bg_dialog_interest_free_stamps_guide_2);
            ((ImageView) findViewById(R.id.ivLeftTopFlag)).setImageResource(R.drawable.img_item_coupon_bind);
            findViewById(R.id.ivLeftTopFlag).setVisibility(View.VISIBLE);
            findViewById(R.id.tvMoneyType).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tvMoney)).setText("100");
            ((TextView) findViewById(R.id.tvCouponType)).setText("息费抵扣券");
            ((TextView) findViewById(R.id.tvLatinosName)).setText("贷款满1000元可用");
            ((TextView) findViewById(R.id.tvLatinosDes)).setText("2021.08.08-2021.10.07");
            ((TextView) findViewById(R.id.tvUser)).setText("去还款");
            findViewById(R.id.tvUser).setVisibility(View.VISIBLE);
        }
    }
}
