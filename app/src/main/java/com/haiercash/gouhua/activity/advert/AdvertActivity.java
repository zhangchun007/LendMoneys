package com.haiercash.gouhua.activity.advert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.utils.UMengUtil;

import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: Sun<br/>
 * Date :    2018/1/27<br/>
 * FileName: AdvertActivity<br/>
 * Description:<br/>
 */
public class AdvertActivity extends BaseActivity {

    //@BindView(R.id.tv_last_time)
    //TextView tvLastTime;
    @BindView(R.id.ll_advert)
    LinearLayout llAdvert;

    //3秒倒计时,500为系统偏差处理
    private long lastTime = 3 * 1000 + 500L;
    //间隔时间
    private long spaceTime = 1000L;

    private static final String TAG_CLASS = "class";
    private static final String TAG_FILE_PATH = "filepath";
    private static final String TAG_AD_NAME = "adName";
    private static final String TAG_AD_CID = "adCid";
    private static final String TAG_AD_GROUP_ID = "adGroupId";
    private AtomicBoolean mRefreshing = new AtomicBoolean(false);

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer = null;
        }
        super.onDestroy();
    }

    @Override
    protected String getPageCode() {
        return "StartPage";
    }

    private String getPageName() {
        return "闪屏页";
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMengUtil.commonExposureEvent("StartAdPosition_Exposure", getPageName(),
                getIntent().getStringExtra(TAG_AD_NAME),
                getIntent().getStringExtra(TAG_AD_CID),
                getIntent().getStringExtra(TAG_AD_GROUP_ID),
                getPageCode());
    }

    private CountDownTimer countDownTimer = new CountDownTimer(lastTime, spaceTime) {
        @Override
        public void onTick(long millisUntilFinished) {
            //if (tvLastTime != null) {
            //    tvLastTime.setText(millisUntilFinished / 1000 + " 跳过");
            //}
        }

        @Override
        public void onFinish() {
            startTargetActivity();
        }
    };


    public static void getAdvertActivity(Context context, String filePath, Class className, String adName, String cid, String groupId, String jumpKey) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG_CLASS, className);
        Intent intent = new Intent(context, AdvertActivity.class);
        intent.putExtra(TAG_FILE_PATH, filePath);
        intent.putExtra(TAG_AD_NAME, adName);
        intent.putExtra(TAG_AD_CID, cid);
        intent.putExtra(TAG_AD_GROUP_ID, groupId);
        if (!CheckUtil.isEmpty(jumpKey)) {
            intent.putExtra("jumpLink", jumpKey);
        }
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).finish();
        ((Activity) context).overridePendingTransition(0, 0);
    }

    private void startTargetActivity() {
        Class className = (Class) getIntent().getSerializableExtra(TAG_CLASS);
        startActivity(new Intent(mContext, className));
        finish();
    }

    @OnClick({R.id.tv_last_time, R.id.ll_advert})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_last_time:
                countDownTimer.cancel();
                startTargetActivity();
                break;
            case R.id.ll_advert:
                //为了防止重复点击
                if (!mRefreshing.compareAndSet(false, true)) {
                    return;
                }
                UMengUtil.commonClickBannerEvent("StartAdPosition_Click", getPageName(),
                        getIntent().getStringExtra(TAG_AD_NAME),
                        getIntent().getStringExtra(TAG_AD_CID),
                        getIntent().getStringExtra(TAG_AD_GROUP_ID),
                        getPageCode());
                String jumpUrl = getIntent().getStringExtra("jumpLink");
                if (CheckUtil.isEmpty(jumpUrl)) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(TAG_CLASS, getIntent().getSerializableExtra(TAG_CLASS));
                bundle.putBoolean("isAdvert", true);
                //能跳转才跳转
                if (WebHelper.startActivityForUrl(this, jumpUrl, bundle)) {
                    countDownTimer.cancel();
                    finish();
                }
                mRefreshing.set(false);
                break;
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_advert;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        //tvLastTime.setText("3 跳过");
        //tvLastTime.setAlpha((float) 0.3);
        //tvLastTime.setTextColor(ContextCompat.getColor(this, R.color.white));
        countDownTimer.start();
        llAdvert.setBackground(Drawable.createFromPath(getIntent().getStringExtra(TAG_FILE_PATH)));
    }
}
