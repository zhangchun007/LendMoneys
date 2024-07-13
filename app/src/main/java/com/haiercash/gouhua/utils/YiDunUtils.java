package com.haiercash.gouhua.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.interfaces.SpKey;
import com.netease.nis.captcha.Captcha;
import com.netease.nis.captcha.CaptchaConfiguration;
import com.netease.nis.captcha.CaptchaListener;

/**
 * 网易易盾工具类
 * 接入文档链接：https://support.dun.163.com/documents/15588062143475712?docId=150442945983729664
 * 特制失败不处理，需自行处理
 */
public class YiDunUtils {
    /**
     * 获取初始化网易易盾验证码实例对象
     */
    public static Captcha init(Activity activity, MyYiDunCaptchaListener listener) {
        CaptchaConfiguration captchaConfiguration = new CaptchaConfiguration.Builder()
                .captchaId("e5f6b5bfae0d48aba73741c9fc7165b9")
                .debug(!BuildConfig.IS_RELEASE)
                .listener(listener)
                .isDimAmountZero(false)//解决弹窗页面触发滑块弹出时闪烁问题
                .hideCloseButton(false)
                .isShowLoading(false)
                .loadingText(null)
                .touchOutsideDisappear(false)
                .ipv6(true)
                .build(activity);
        return Captcha.getInstance().init(captchaConfiguration);
    }

    public static void initAndValidate(Activity activity, MyYiDunCaptchaListener listener) {
        //需要每次都初始化，不然如果连续两个页面都有初始化滑块sdk，再回到第一个页面不重新初始化而执行validate就会阻塞
        init(activity, listener);
        Captcha.getInstance().validate();
    }

    //网易滑块开关
    public static boolean isSliderOpen() {
        return TextUtils.equals("Y", SpHp.getOther(SpKey.CONFIGURE_SWITCH_WY_SLIDER, "Y"));
    }

    public interface MyYiDunCaptchaListener extends CaptchaListener {
        @Override
        void onCaptchaVisible();//滑块弹窗肉眼可见时回调
    }
}