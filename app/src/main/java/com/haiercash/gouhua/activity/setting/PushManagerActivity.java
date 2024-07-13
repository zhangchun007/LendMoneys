package com.haiercash.gouhua.activity.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.PersonalRecommendBean;
import com.haiercash.gouhua.databinding.ActivityPushManagerBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;

/**
 * 设置-定向推送管理
 */
public class PushManagerActivity extends BaseActivity {
    private ActivityPushManagerBinding mPushManagerBinding;
    //个性化开关结果
    private String switchConfig;

    @Override
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return mPushManagerBinding = ActivityPushManagerBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(R.string.push_manager_title);
        //设置个性化开关状态
        if (getIntent() != null) {
            switchConfig = getIntent().getStringExtra("switchConfig");
        }
        setPersonRecommendImgState(switchConfig);
        mPushManagerBinding.llRecommend.setOnClickListener(this);
        //上一页面接口请求失败了才再次发请求
        if (CheckUtil.isEmpty(switchConfig)) {
            queryRecommendState();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mPushManagerBinding.llRecommend) {
            if (!"N".equals(switchConfig)) {
                showDialog(getString(R.string.care_notice), getString(R.string.push_manager_dialog_tip),
                        getString(R.string.push_manager_close),
                        getString(R.string.push_manager_no_close),
                        (dialog, which) -> {
                            if (which == 1) {
                                changeRecommendState();
                            }
                        }).setStandardStyle(3);
            } else {
                changeRecommendState();
            }
        } else {
            super.onClick(v);
        }
    }

    /**
     * 获取定向推送开关状态
     */
    private void queryRecommendState() {
        showProgress(true);
        HashMap<String, String> map = new HashMap<>();
        String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        map.put("userId", userId);
        netHelper.postService(ApiUrl.POST_QUERY_RECOMMEND_STATE, map, PersonalRecommendBean.class);
    }

    /**
     * 切换定向推送开关状态
     */
    private void changeRecommendState() {
        showProgress(true);
        HashMap<String, String> map = new HashMap<>();
        String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        map.put("userId", userId);
        map.put("switchConfig", "N".equals(switchConfig) ? "Y" : "N");
        netHelper.postService(ApiUrl.POST_CHANGE_RECOMMEND_STATE, map);
    }

    @Override
    public void onSuccess(Object response, String flag) {
        if (response == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        if (ApiUrl.POST_CHANGE_RECOMMEND_STATE.equals(flag)) {
            if ("N".equals(switchConfig)) {
                switchConfig = "Y";
            } else {
                switchConfig = "N";
            }
            setPersonRecommendImgState(switchConfig);
        } else if (ApiUrl.POST_QUERY_RECOMMEND_STATE.equals(flag)) {
            PersonalRecommendBean personalRecommendBean = (PersonalRecommendBean) response;
            if (!CheckUtil.isEmpty(personalRecommendBean.getSwitchConfig())) {
                switchConfig = personalRecommendBean.getSwitchConfig();
                setPersonRecommendImgState(switchConfig);
            }
        }
        showProgress(false);
    }

    /**
     * 设置按钮状态
     */
    private void setPersonRecommendImgState(String switchConfig) {
        //只有接口请求成功且是N才显示关闭状态
        try {
            if ("N".equals(switchConfig)) {
                mPushManagerBinding.ivRecommend.setImageResource(R.drawable.togglebutton_off);
            } else {
                mPushManagerBinding.ivRecommend.setImageResource(R.drawable.togglebutton_on);
            }
        } catch (Exception e) {
            //
        }
    }
}
