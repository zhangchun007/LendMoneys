package com.haiercash.gouhua.jsweb;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;

/**
 * Web是全透明弹窗,APP是全透明主题，父控件控制半透明蒙层效果，TopSpace控制蒙层高度，不需要顶部导航栏，属于弹窗式跳转动画(从小往上出现，从上往下消失)
 */
public class WebPopLimitHeightActivity extends JsWebBaseActivity {

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
        setTopSpaceHeight(SystemUtils.getDeviceHeight(this) / 3);
        setTitleBarRightImage(true, R.drawable.icon_close_grey, v -> finish());
        vTopSpace.setOnClickListener(v -> finish());

    }

    @Override
    protected void setStyle() {
        super.setStyle();
        //showHeader(false, true);
        //半透明蒙层
        setLeftImageVisible(false);
        setStatusBarVisible(false);
        setLeftImageCloseVisibility(false);
        setTitleVisible(false);
        setTitleBarRightImageParam(20, 5, 15);
        setTitleBarBackground(true, R.drawable.bg_top_radius, "");

    }

    @Override
    public void finish() {
        super.finish();
        //注意：overridePendingTransition一定要在startActivity 或者finish 之后调用，否则没有效果！而且可能会有各种其他问题！
        overridePendingTransition(0, R.anim.activity_down_out);
    }
}
