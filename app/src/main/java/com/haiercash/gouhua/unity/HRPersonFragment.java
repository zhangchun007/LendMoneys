package com.haiercash.gouhua.unity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.alibaba.fastjson.JSONObject;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.CommonSpKey;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.gesture.GesturesSettingActivity;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.activity.personalinfor.PersonalInformationActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.base.CommonConfig;
import com.haiercash.gouhua.beans.msg.UnReadMessageCount;
import com.haiercash.gouhua.beans.unity.ComponentBean;
import com.haiercash.gouhua.beans.unity.MultComponentBean;
import com.haiercash.gouhua.beans.unity.UserComponentInfoBean;
import com.haiercash.gouhua.biometriclib.BiometricUntil;
import com.haiercash.gouhua.databinding.FragmentCenterBinding;
import com.haiercash.gouhua.hybrid.H5LinkJumpHelper;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.UploadEventHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/8
 * @Version: 1.0
 */
public class HRPersonFragment extends BaseFragment implements BiometricUntil.BiometricUntilCallBack {

    private LinearLayoutManager manager;
    private HRCommonAdapter hrCommonAdapter;
    private List<ComponentBean> mList = new ArrayList<>();
    private Map<String, Object> mPersonMap;

    private String currentScerete;
    private MultComponentBean multComponentBean;

    //头部滑动监听
    private int dyTop;
    private int mHeight;

    private boolean isFinger; //是否可以开启指纹
    //设置指纹
    private BiometricUntil biometricUntil;

    private FragmentCenterBinding getBinding() {
        return (FragmentCenterBinding) _binding;
    }

    @Override
    protected ViewBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentCenterBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initEventAndData() {
        initHeaderView();
        initListener();
        manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        getBinding().rvCenter.setLayoutManager(manager);
        hrCommonAdapter = new HRCommonAdapter(getActivity(), mList);
        getBinding().rvCenter.setAdapter(hrCommonAdapter);
        //获取用户数据
        getUserInfo(true);
        //下啦刷新
        getBinding().layoutRefresh.setOnRefreshListener(refreshLayout -> {
                    getPersonModelInfo();
                    getUserInfo(true);
                    getBinding().layoutRefresh.finishRefresh();
                }
        );
        //判断模版数据有没有，没有加载网络
        String modelJson = FlattenJsonUtils.getPersonModelDataJson();
        if (!TextUtils.isEmpty(modelJson)) {
            multComponentBean = JsonUtils.fromJson(modelJson, MultComponentBean.class);
            hrCommonAdapter.setNewData(multComponentBean.getComponentList());
        } else {
            getPersonModelInfo();
        }


    }


    //获取模版数据
    private void getPersonModelInfo() {
        if (netHelper != null) {
            Map<String, String> map = new HashMap<>();
            map.put("modelNo", CommonConfig.PERSON_MODEL_UNITY);
            netHelper.postService(ApiUrl.POST_MODEL_DATA, map, MultComponentBean.class);
        }
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        super.onSuccess(t, url);
        showProgress(false);
        if (ApiUrl.POST_MODEL_DATA.equals(url)) { //获取模版数据
            MultComponentBean personCenterInfo = (MultComponentBean) t;
            String modelJson = JsonUtils.toJson(personCenterInfo);
            FlattenJsonUtils.setPersonModelDataJson(modelJson);
            multComponentBean = JsonUtils.fromJson(modelJson, MultComponentBean.class);
            hrCommonAdapter.setNewData(multComponentBean.getComponentList());

        } else if (ApiUrl.POST_URL_PSERSON_CENTER_NEW2.equals(url)) {//获取用户数据
            String response = JsonUtils.toJson(t);
            try {
                Map<String, Object> map = JsonUtils.getRequestObj(response);
                JSONObject object = new JSONObject(map);
                mPersonMap = FlattenJsonUtils.flattenJson(object, "");
                hrCommonAdapter.setPersonCenterData(mPersonMap);
                hrCommonAdapter.notifyDataSetChanged();
                //设置底部悬浮窗展示
                showBottomFloatingView();
                //设置头部标题栏
                getItemUserInfo();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (ApiUrl.GET_UNREAD_MESSAGE_COUNT.equals(url)) {
            UnReadMessageCount data = (UnReadMessageCount) t;
            if (data != null) {
                if (data.getTotal() <= 0) {
                    getBinding().layoutNum.setVisibility(View.GONE);
                } else if (data.getTotal() < 10) {
                    getBinding().layoutNum.setVisibility(View.VISIBLE);
                    getBinding().noticeNum.setText(data.getTotal() + "");
                    getBinding().noticeNum.setBackgroundResource(R.drawable.shape_circle12_ff5151);
                } else if (data.getTotal() >= 10) {
                    getBinding().layoutNum.setVisibility(View.VISIBLE);
                    if (data.getTotal() < 100) {
                        getBinding().noticeNum.setText(data.getTotal() + "");
                    } else {
                        getBinding().noticeNum.setText("99+");
                    }
                    getBinding().noticeNum.setBackgroundResource(R.drawable.shape_messge_notice_num_bg);
                }
            }
        }

    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        showProgress(false);
        if (ApiUrl.POST_MODEL_DATA.equals(url)) {
            loadLocalModelData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //保存用户数据
        FlattenJsonUtils.saveUserInfo();
        //设置底部悬浮窗展示
        showBottomFloatingView();
        //获取未读消息数量
        getUnReadMessageNum();
        //设置头部标题栏
        getItemUserInfo();
        //获取用户数据
        getUserInfo(false);
    }

    /**
     * 未读消息数量
     */
    public void getUnReadMessageNum() {
        if (AppApplication.isLogIn()) {
            Map<String, String> map = new HashMap<>();
            netHelper.postService(ApiUrl.GET_UNREAD_MESSAGE_COUNT, map, UnReadMessageCount.class);
        } else {
            getBinding().layoutNum.setVisibility(View.GONE);
        }
    }

    /**
     * 设置底部悬浮窗展示
     */
    private void showBottomFloatingView() {
        //判断悬浮窗
        if (isShowBootomFloatView()) {
            getBinding().rlApply.rlQuickApply.setVisibility(View.VISIBLE);
            getBinding().rlApply.tvQuickApplyInfo.setText(getShowFloatingWindowContent());
            ImageLoader.loadImage(getContext(), getShowFloatingWindowImageUrl(), getBinding().rlApply.ivQuickApply, R.drawable.img_quick_apply_money);
            getBinding().rlApply.tvGoApply.setOnClickListener(v -> {
                H5LinkJumpHelper.INSTANCE().goLoanPage(getMActivity());
            });
            getBinding().rlApply.llCloseApply.setOnClickListener(v -> {
                UserStateUtils.saveApplyState(currentScerete);
                getBinding().rlApply.rlQuickApply.setVisibility(View.GONE);
            });
        } else {
            getBinding().rlApply.rlQuickApply.setVisibility(View.GONE);
            //指纹判断
            checkQuickLogin();
        }
    }

    //检查是否需要展示快捷登录弹窗
    private void checkQuickLogin() {
        //是否支持指纹
        boolean isSupport = new BiometricUntil(mActivity, null).isBiometricPromptEnable();
        boolean hasSetBiometric = LoginSelectHelper.hasSetBiometric();
        currentScerete = SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_TOKEN_CLIENT_SECRET);
        String latestToken = SpHelper.getInstance().readMsgFromSp(SpKey.QUICK_LOGIN_STATE, SpKey.LATEST_LONG_TOKEN);
        if (!LoginSelectHelper.hasSetGesture() && !hasSetBiometric && !currentScerete.equals(latestToken)) {
            getBinding().rlQuickLogin.rlQuickLogin.setVisibility(View.VISIBLE);
            if (isSupport) {
                isFinger = true;
                getBinding().rlQuickLogin.tvQuickInfo.setText(getString(R.string.quick_login_biometric_hint));
            } else {
                isFinger = false;
                getBinding().rlQuickLogin.tvQuickInfo.setText(getString(R.string.quick_login_gesture_hint));
            }
            getBinding().rlQuickLogin.tvGoSetting.setOnClickListener(v -> {
                if (isFinger) {
                    toSetBiometric();
                } else {
                    Intent intent = new Intent(mActivity, GesturesSettingActivity.class);
                    intent.putExtra("pageType", "changeGestures");
                    mActivity.startActivity(intent);
                }
            });
            getBinding().rlQuickLogin.llCloseQuick.setOnClickListener(v -> {
                SpHelper.getInstance().saveMsgToSp(SpKey.QUICK_LOGIN_STATE, SpKey.LATEST_LONG_TOKEN, currentScerete);
                getBinding().rlQuickLogin.rlQuickLogin.setVisibility(View.GONE);
            });
        } else {
            getBinding().rlQuickLogin.rlQuickLogin.setVisibility(View.GONE);
        }
    }

    /**
     * 引导去设置指纹（前置条件是系统硬件是否支持指纹识别BiometricUntil.isHardwareDetected）
     */
    private void toSetBiometric() {
        //每次都要初始化，不然再次调起是无效的
        if (biometricUntil != null) {
            biometricUntil.cancelAuthenticate();
            biometricUntil = null;
        }
        biometricUntil = new BiometricUntil(mActivity, this);
        biometricUntil.showBiometricLibPop();
        UMengUtil.pageStart("SetFingerprintPage");
    }

    /**
     * 是否展示底部浮窗
     *
     * @return
     */
    private boolean isShowBootomFloatView() {
        currentScerete = UserStateUtils.getClientSecret();
        String latestToken = UserStateUtils.getApplyState();
        if (UserStateUtils.isLogIn() && !currentScerete.equals(latestToken) && isShowFloatingWindow()) {
            return true;
        }
        return false;
    }

    /**
     * 是否展示悬浮窗
     *
     * @return
     */
    private boolean isShowFloatingWindow() {
        if (mPersonMap != null
                && mPersonMap.get("popupWindow.content") != null
                && TextUtils.isEmpty(mPersonMap.get("popupWindow.content").toString())) {
            return true;
        }
        return false;
    }

    /**
     * 悬浮窗文本
     *
     * @return
     */
    private String getShowFloatingWindowContent() {
        if (mPersonMap != null
                && mPersonMap.get("popupWindow.text") != null
                && !TextUtils.isEmpty(mPersonMap.get("popupWindow.text").toString())) {
            return mPersonMap.get("popupWindow.text").toString();
        }
        return "";
    }

    /**
     * 悬浮窗图片链接
     *
     * @return
     */
    private String getShowFloatingWindowImageUrl() {
        if (mPersonMap != null
                && mPersonMap.get("popupWindow.iconUrl") != null
                && TextUtils.isEmpty(mPersonMap.get("popupWindow.iconUrl").toString())) {
            return mPersonMap.get("popupWindow.iconUrl").toString();
        }
        return "";
    }

    /**
     * 获取用户数据
     */
    private void getUserInfo(boolean isShowProgress) {
        if (netHelper != null) {
            showProgress(isShowProgress);
            Map<String, String> map = new HashMap<>();
            map.put("modelNo", CommonConfig.PERSON_MODEL_UNITY);
            netHelper.postService(ApiUrl.POST_URL_PSERSON_CENTER_NEW2, map);
        }
    }


    private void initListener() {
        getBinding().ivMessage.setOnClickListener(v -> {
//                    postClickEvent("Xj_Me_Set_Click", "设置");
                    JumpUtils.jumpNative(getContext(), "message");
                }

        );
        getBinding().ivSetting.setOnClickListener(v -> {
            Map<String, Object> map = new HashMap<>();
            map.put("is_read", "false");
//            postClickEvent("Xj_Me_Message_Click", "消息中心", map);
            JumpUtils.jumpNative(getContext(), "setting");
        });
    }

    //获取头像数据
    private void getItemUserInfo() {
        getBinding().tvTopName.setOnClickListener(v -> {
            goProfileActivity();
        });
        getBinding().imgTopAvatar.setOnClickListener(v -> {
            goProfileActivity();
        });
        if (!UserStateUtils.isLogIn()) {
            getBinding().tvTopName.setText("登录可享更多精彩");
            getBinding().tvTopNum.setText("");
            return;
        }
        if (CheckUtil.isEmpty(multComponentBean)
                || CheckUtil.isEmpty(multComponentBean.getComponentList())
                || CheckUtil.isEmpty(mPersonMap)) {
            return;
        }
        List<ComponentBean> componentList = multComponentBean.getComponentList();
        for (ComponentBean componentBean : componentList) {
            if (componentBean == null) return;
            if ("UserInfoComponent".equals(componentBean.getType())) {
                if (componentBean.getData() == null || componentBean.getData().getSourceData() == null)
                    return;
                //用户信息
                UserComponentInfoBean userComponentInfoBean = componentBean.getData().getSourceData().getUserInfo();
                if (userComponentInfoBean == null) return;
                String imageUrl = ReplaceHolderUtils.replaceKeysWithValues(userComponentInfoBean.getAvatar().getImage().getImageUrl(), mPersonMap);
                ImageLoader.loadHeadImage(getContext(), imageUrl, getBinding().imgTopAvatar, R.drawable.img_user_avatar);

                String userName = ReplaceHolderUtils.replaceKeysWithValues(userComponentInfoBean.getName(), mPersonMap);
                if (TextUtils.isEmpty(userName)) {
                    getBinding().tvTopName.setText("Hi,你好");
                } else {//实名
                    getBinding().tvTopName.setText(userName);
                }
                String phone = ReplaceHolderUtils.replaceKeysWithValues(userComponentInfoBean.getMobile(), mPersonMap);
                if (TextUtils.isEmpty(phone)) {
                    getBinding().tvTopNum.setText("");
                } else {//实名
                    getBinding().tvTopNum.setText(phone);
                }
            }
        }

    }
    private void goProfileActivity() {
        if (!UserStateUtils.isLogIn()) {
            LoginSelectHelper.staticToGeneralLogin();
        } else {
            getActivity().startActivity(new Intent(getActivity(), PersonalInformationActivity.class));
        }
    }

    //头部滚动
    private void initHeaderView() {
        getBinding().imgTopAvatar.setImageResource(R.drawable.img_user_avatar);
        getBinding().tvTopName.setText("Hi,你好");
        getBinding().tvTopNum.setText("");
        //头部设置滑动
        getBinding().groupTopUserInfo.setVisibility(View.GONE);
        getBinding().rvCenter.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                dyTop += dy;
                mHeight = getBinding().groupTopUserInfo.getHeight();
                int firstCompletelyVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                if (dyTop <= 0 || firstCompletelyVisibleItemPosition == 0) {//未滑动
                    dyTop = 0;
                    getBinding().layoutTopHead.setBackgroundColor(Color.argb((int) 0, 255, 255, 255));
                    getBinding().groupTopUserInfo.setVisibility(View.GONE);

                } else if (dyTop <= UiUtil.dip2px(getActivity(), mHeight)) {
                    if (dyTop <= mHeight / 2) {
                        getBinding().groupTopUserInfo.setVisibility(View.GONE);
                    } else {
                        getBinding().groupTopUserInfo.setVisibility(View.VISIBLE);

                    }
                    float scale = (float) dyTop / mHeight;
                    float alpha = (255 * scale);
                    getBinding().layoutTopHead.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                } else {
                    getBinding().layoutTopHead.setBackgroundColor(Color.argb((int) 255, 255, 255, 255));
                    getBinding().layoutTopHead.setAlpha(1);
                    getBinding().groupTopUserInfo.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void biometricSwitch(boolean isOpening) {

    }

    @Override
    public void onSuccess(boolean isOpenFingerprint) {
        getBinding().rlQuickLogin.rlQuickLogin.setVisibility(View.GONE);
        UploadEventHelper.postUmEventWithFingerprintResult(true, "");
        UMengUtil.pageEnd("SetFingerprintPage");
    }

    @Override
    public void onFailed(Integer errorCode, String errorReason) {
        UploadEventHelper.postUmEventWithFingerprintResult(false, errorReason);
    }

    @Override
    public void onErrorForMoreFailed() {
        //5次及以上指纹验证失败
        UMengUtil.pageEnd("SetFingerprintPage");
        mActivity.showDialog("指纹验证失败", "验证失败次数过多，请稍后重试～", null);
    }

    @Override
    public void onCancel() {
        UMengUtil.pageEnd("SetFingerprintPage");
    }

    /**
     * 加载本地数据
     */
    private void loadLocalModelData(){
        //模拟接口请求
        try {
            AssetManager assetManager = getActivity().getAssets();
            InputStream inputStream = assetManager.open("person.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            inputStream.close();
            String jsonContent = builder.toString();
            multComponentBean = JsonUtils.json2Class(jsonContent, MultComponentBean.class);
            hrCommonAdapter.setNewData(multComponentBean.getComponentList());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
