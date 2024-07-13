package com.haiercash.gouhua.fragments.mine;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.tplibrary.PagePath;

/**
 * Created by yuelb on 2017/7/20.
 */
@Route(path = PagePath.FRAGMENT_ACRACK)
public class ACrackFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView scrollView = new ScrollView(getContext());
        TextView textView = new TextView(getContext());
        scrollView.addView(textView);
        textView.setBackgroundColor(Color.WHITE);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18);
        textView.setPadding(10, 10, 10, 10);
        textView.setLineSpacing(1F, 1F);
        textView.setText(config());
        return scrollView;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initEventAndData() {

    }

    public String config() {
        StringBuilder sb = new StringBuilder();
        sb.append("version:").append(SystemUtils.getAppVersion(mActivity)).append("\n");
        sb.append("BuildConfig.DEBUG:").append(BuildConfig.DEBUG).append("\n");
        sb.append("BuildConfig.IS_RELEASE:").append(BuildConfig.IS_RELEASE).append("\n");
        sb.append("API_SERVER_URL:").append(ApiUrl.API_SERVER_URL).append("\n");
        sb.append("bairongCid:").append(CommonConfig.bairongCid).append("\n");
        sb.append("TD_APP_ID:").append(SystemUtils.metaDataValue(mActivity, "TD_APP_ID")).append("\n");
        sb.append("TD_CHANNEL_ID:").append(SystemUtils.metaDataValue(mActivity, "TD_CHANNEL_ID")).append("\n");
        sb.append("XG_V2_ACCESS_ID:").append(SystemUtils.metaDataValue(mActivity, "XG_V2_ACCESS_ID")).append("\n");
        sb.append("XG_V2_ACCESS_KEY:").append(SystemUtils.metaDataValue(mActivity, "XG_V2_ACCESS_KEY")).append("\n");
        sb.append("com.baidu.lbsapi.API_KEYY:").append(SystemUtils.metaDataValue(mActivity, "com.baidu.lbsapi.API_KEY")).append("\n");
        sb.append("userid:").append(AppApplication.userid).append("\n\n");

        Activity activity = mActivity;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        sb.append("density").append(":").append(dm.density).append("\n");
        sb.append("densityDpi").append(":").append(dm.densityDpi).append("\n");
        sb.append("widthPixels").append(":").append(dm.widthPixels).append("\n");
        sb.append("heightPixels").append(":").append(dm.heightPixels).append("\n");
        sb.append("scaledDensity").append(":").append(dm.scaledDensity).append("\n");
        sb.append("xdpi").append(":").append(dm.xdpi).append("\n");
        sb.append("ydpi").append(":").append(dm.ydpi).append("\n");

        return sb.toString();
    }
}
