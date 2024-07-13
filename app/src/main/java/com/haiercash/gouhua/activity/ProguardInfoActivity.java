package com.haiercash.gouhua.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;

import com.airbnb.lottie.LottieAnimationView;
import com.app.haiercash.base.bui.TitleBarView;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.PermissionPageUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.setting.MsgSettingActivity;
import com.haiercash.gouhua.activity.setting.PermissionsActivity;
import com.haiercash.gouhua.activity.setting.PrivateDescActivity;
import com.haiercash.gouhua.activity.setting.PushManagerActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.PersonalRecommendBean;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.uihelper.HomeNotchScreenHelper;
import com.haiercash.gouhua.utils.AIServer;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 信息保护页面
 *
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 3/22/23
 * @Version: 1.0
 */
public class ProguardInfoActivity extends BaseActivity {

    @BindView(R.id.loading_anim)
    LottieAnimationView loading_anim;

    @BindView(R.id.bar_header)
    TitleBarView bar_header;

    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    @BindView(R.id.tv_switch_notification)
    TextView tvSwitchNotification;
    @BindView(R.id.layout_push)
    View layoutPush;
    @BindView(R.id.tv_switch_push)
    TextView tvSwitchPush;

    @BindView(R.id.img_top_bg)
    ImageView img_top_bg;

    private int mmRvScrollY;//srcollview滑动的距离
    private int mHeight;
    //个性化开关结果
    private String switchConfig;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        boolean isRecommendOpen = isRecommendOpen();
        layoutPush.setVisibility(isRecommendOpen ? View.VISIBLE : View.GONE);
        loading_anim.setImageAssetsFolder("images/");
        loading_anim.setAnimation("info_protect.json");
        loading_anim.loop(true);
        loading_anim.playAnimation();
        setTitle("", "#00000000");
        bar_header.setLeftImage(R.drawable.img_guard_arrrow_white, null);
        Logger.e("ProguardInfoActivity--"+HomeNotchScreenHelper.hasNotchInScreen(this));
        if (HomeNotchScreenHelper.hasNotchInScreen(this)) {
            img_top_bg.setImageResource(R.drawable.img_info_guard_screen_bg);
        }else {
            img_top_bg.setImageResource(R.drawable.img_info_guard_bg);
        }

        //头部设置滑动
        bar_header.setTitleBackgroundColor(Color.parseColor("#00000000"));
        nestedScrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            Logger.e("onScrollChange", "scrollY==" + scrollY);
            mmRvScrollY = scrollY;
            mHeight = bar_header.getHeight();
            if (mmRvScrollY <= 0) {//未滑动
                setTitle("", "#00000000");
                bar_header.setLeftImage(R.drawable.img_guard_arrrow_white, null);
                bar_header.setBackgroundColor(Color.argb((int) 0, 255, 255, 255));
            } else if (mmRvScrollY > 0 && mmRvScrollY <= mHeight) { //滑动过程中 并且在mHeight之内
                float scale = (float) mmRvScrollY / mHeight;
                float alpha = (255 * scale);
                setTitle("信息保护", "#303133");
                bar_header.setLeftImage(R.drawable.img_guard_arrow_gray, null);
                bar_header.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
            } else {//超过mHeight
                setTitle("信息保护", "#303133");
                bar_header.setLeftImage(R.drawable.img_guard_arrow_gray, null);
                bar_header.setBackgroundColor(Color.argb((int) 255, 255, 255, 255));
            }
        });

    }

    //个性化开关
    public static boolean isRecommendOpen() {
        return TextUtils.equals("Y", SpHp.getOther(SpKey.CONFIGURE_SWITCH_PERSONAL_RECOMMEND, "N"));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_info_proguard;
    }

    @OnClick({R.id.layout_permission, R.id.layout_notification, R.id.layout_push,
            R.id.layout_privacy, R.id.layout_consult})
    public void onClick(View v) {
        if (v.getId() == R.id.layout_permission) {
            //权限管理
            startActivity(new Intent(this, PermissionsActivity.class));
        } else if (v.getId() == R.id.layout_notification) {
            //消息通知
            startActivity(new Intent(this, MsgSettingActivity.class));
        } else if (v.getId() == R.id.layout_push) {
            //定向推送
            AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    Intent intent = new Intent(ProguardInfoActivity.this, PushManagerActivity.class);
                    if (switchConfig != null) {
                        intent.putExtra("switchConfig", switchConfig);
                    }
                    startActivity(intent);
                }
            });
        } else if (v.getId() == R.id.layout_privacy) {
            //隐私说明
            startActivity(new Intent(this, PrivateDescActivity.class));
        } else if (v.getId() == R.id.layout_consult) {
            //消费者咨询
            AppApplication.setLoginCallbackTodo(new LoginCallbackC() {
                @Override
                public void onLoginSuccess() {
                    AIServer.showAiWebServer(ProguardInfoActivity.this, "帮助中心");
                }
            });
        } else {
            super.onClick(v);
        }
    }

    /**
     * 获取定向推送开关状态
     */
    private void queryRecommendState() {
        HashMap<String, String> map = new HashMap<>();
        String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        map.put("userId", userId);
        netHelper.postService(ApiUrl.POST_QUERY_RECOMMEND_STATE, map, PersonalRecommendBean.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PermissionPageUtils.isNotificationEnable(this)) {
            tvSwitchNotification.setText(R.string.setting_status_yes);
        } else {
            tvSwitchNotification.setText(R.string.setting_status_no);
        }
        //开关开的状态下
        if (AppApplication.isLogIn() && isRecommendOpen()) {
            queryRecommendState();
        }
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.POST_QUERY_RECOMMEND_STATE.equals(url)) {
            PersonalRecommendBean personalRecommendBean = (PersonalRecommendBean) success;
            if (personalRecommendBean != null && !CheckUtil.isEmpty(personalRecommendBean.getSwitchConfig())) {
                switchConfig = personalRecommendBean.getSwitchConfig();
                setPushStatus();
            }
        }
        showProgress(false);
    }

    private void setPushStatus() {
        //只有接口请求成功且是N才显示关闭状态
        try {
            tvSwitchPush.setText(getString("N".equals(switchConfig) ? R.string.setting_status_no : R.string.setting_status_yes));
        } catch (Exception e) {
            //
        }
    }
}
