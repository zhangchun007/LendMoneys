package com.haiercash.gouhua.jsweb;

import android.os.Bundle;
import android.view.animation.TranslateAnimation;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.haiercash.gouhua.R;

/**
 * Web是全透明弹窗,APP是全透明主题，父控件控制半透明蒙层效果，TopSpace控制蒙层高度，不需要顶部导航栏，属于弹窗式跳转动画(从小往上出现，从上往下消失)
 */
public class JsWebPopActivity extends JsWebBaseActivity {

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);
    }

    @Override
    protected void setStyle() {
        super.setStyle();
        showHeader(false, true);
        setWebVisible(false);
        showProgress(true);
        setBackgroundTransparent();
////        设置遮罩层的高度
//        setTopSpaceHeight(SystemUtils.getDeviceHeight(this) / 3);
        //先移动到底部消失，正式出现时从下往上的出场动画能流畅
        TranslateAnimation translateAnimation = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 1);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(0);
        getWebView().startAnimation(translateAnimation);
    }

    @Override
    protected boolean showProgressBar() {
        return false;
    }

    @Override
    protected void onPageError() {
        super.onPageError();
        showProgress(false);
        //半透明蒙层
        setRootBackgroundColor(ContextCompat.getColor(this, R.color.translucency));
        //展示
        setWebVisible(true);
    }

    @Override
    public void onH5LoadFinished() {
        super.onH5LoadFinished();
        showProgress(false);
        //半透明蒙层
        setRootBackgroundColor(ContextCompat.getColor(this, R.color.translucency));
        //展示
        setWebVisible(true);
        //从下往上动画出现
        TranslateAnimation translateAnimation_show = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_SELF, 0, TranslateAnimation.RELATIVE_TO_SELF, 0,
                TranslateAnimation.RELATIVE_TO_SELF, 1, TranslateAnimation.RELATIVE_TO_SELF, 0);
        translateAnimation_show.setFillAfter(true);
        translateAnimation_show.setDuration(500);
        getWebView().startAnimation(translateAnimation_show);
    }

    @Override
    public void finish() {
        setRootBackgroundColor(0);//取消蒙层
        setResult(RESULT_OK);
        super.finish();
        //注意：overridePendingTransition一定要在startActivity 或者finish 之后调用，否则没有效果！而且可能会有各种其他问题！
        overridePendingTransition(0, R.anim.activity_down_out);
    }
}
