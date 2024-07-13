package com.haiercash.gouhua.unity;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.login.LoginSelectHelper;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.beans.unity.ActionBean;
import com.haiercash.gouhua.beans.unity.AvatarBean;
import com.haiercash.gouhua.beans.unity.ComponentInfoBean;
import com.haiercash.gouhua.beans.unity.ImageInfoBean;
import com.haiercash.gouhua.beans.unity.MenusBean;
import com.haiercash.gouhua.beans.unity.ShowConditionBean;
import com.haiercash.gouhua.beans.unity.UserComponentInfoBean;
import com.haiercash.gouhua.beans.unity.UserTagBean;
import com.haiercash.gouhua.beans.unity.VipImageBean;
import com.haiercash.gouhua.beans.unity.VipImageListBean;
import com.haiercash.gouhua.beans.unity.VipTipsBean;
import com.haiercash.gouhua.utils.CommomUtils;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人中心--用户模块组件
 *
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/8
 * @Version: 1.0
 */
public class HRUserInfoComponent extends FrameLayout implements View.OnClickListener {
    //金刚区
    private RecyclerView rvMenu;
    private LinearLayoutManager layoutManager;
    private JinGComponentAdapter jinGComponentAdapter;
    private List<MenusBean> menuList = new ArrayList<>();

    //user用户数据
    private ImageView ivVip, imgUserAvatar, imgTopBg;
    private UserComponentInfoBean userComponentInfoBean;
    private Map<String, Object> mPersonMap;
    private TextView tvUserName, tvUserNumBottom, tvUserNumLeft, tvVipTips;
    private LinearLayoutManager vipRightManager, vipBottomManager;
    private RecyclerView rvVipRight, rvVipBottom;
    private UserVipTagAdapter vipTagAdapter;
    private List<UserTagBean> vipTagList = new ArrayList<>();
    private VipImageListBean defaultVipImagBean = null;

    public HRUserInfoComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HRUserInfoComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRUserInfoComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (context == null) return;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_user_info_component, this);
        rvMenu = view.findViewById(R.id.rv_menu);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        rvMenu.setLayoutManager(layoutManager);
        jinGComponentAdapter = new JinGComponentAdapter(menuList);
        rvMenu.setAdapter(jinGComponentAdapter);
        tvVipTips = view.findViewById(R.id.tv_vip_tips);
        ivVip = view.findViewById(R.id.iv_vip);
        ivVip.setOnClickListener(this);

        tvUserNumLeft = view.findViewById(R.id.tv_user_num_left);
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserName.setOnClickListener(this);
        tvUserNumBottom = view.findViewById(R.id.tv_user_num_bottom);
        imgTopBg = view.findViewById(R.id.img_top_bg);
        imgUserAvatar = view.findViewById(R.id.img_user_avatar);
        imgUserAvatar.setOnClickListener(this);

        jinGComponentAdapter.setOnItemClickListener((adapter, view1, position) -> {
            MenusBean menusBean = menuList.get(position);
            ActionBean action = menusBean.getAction();
            action.setJumpUrl(ReplaceHolderUtils.replaceKeysWithValues(action.getJumpUrl(), mPersonMap));
            JumpUtils.jumpAction(getContext(), action);
        });

        //vip tag
        vipRightManager = new LinearLayoutManager(context);
        vipRightManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        vipTagAdapter = new UserVipTagAdapter(vipTagList);
        rvVipRight = view.findViewById(R.id.rv_vip_right);
        rvVipRight.setLayoutManager(vipRightManager);
        rvVipRight.setAdapter(vipTagAdapter);

        vipBottomManager = new LinearLayoutManager(context);
        vipBottomManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvVipBottom = view.findViewById(R.id.rv_vip_bottom);
        rvVipBottom.setLayoutManager(vipBottomManager);
        rvVipBottom.setAdapter(vipTagAdapter);
    }


    /**
     * 设置数据
     *
     * @param componentInfoBean
     */
    public void setData(ComponentInfoBean componentInfoBean, Map<String, Object> personMap) {
        if (componentInfoBean == null) return;
        if (componentInfoBean.getSourceData() == null) return;
        //用户信息
        userComponentInfoBean = componentInfoBean.getSourceData().getUserInfo();
        if (userComponentInfoBean == null) return;
        this.mPersonMap = personMap;
        //头像
        if (UserStateUtils.isLogIn()) {
//            String imageUrl = BuildConfig.API_HOST + BaseUrl.URL_GETUSERPIC + "?userId=" + EncryptUtil.simpleEncrypt(CommonUtils.getUserId());
            String imageUrl = ReplaceHolderUtils.replaceKeysWithValues(userComponentInfoBean.getAvatar().getImage().getImageUrl(), mPersonMap);
            ImageLoader.loadHeadImage(getContext(), imageUrl, imgUserAvatar, R.drawable.img_user_avatar);
        } else {
            imgUserAvatar.setImageResource(R.drawable.img_user_avatar);
        }
        //设置会员背景图片的大小跟图片数据
        float defaultVipBgImgRatio = 0.616f;
        if (userComponentInfoBean.getVipState() != null && userComponentInfoBean.getVipState().getVipBgImage() != null) {
            List<ImageInfoBean> listVipBg = userComponentInfoBean.getVipState().getVipBgImage();
            ImageInfoBean defaultVipBgBean = null;
            //获取需要展示那一张背景图片
            if (listVipBg != null && listVipBg.size() > 0) {
                for (int i = 0; i < listVipBg.size(); i++) {
                    ImageInfoBean imageInfoBean = listVipBg.get(i);
                    if (imageInfoBean != null && imageInfoBean.getShowCondition() != null) {
                        String key = imageInfoBean.getShowCondition().getKey();
                        String standard = imageInfoBean.getShowCondition().getStandard();
                        if (standard.equals(ReplaceHolderUtils.replaceKeysWithValues(key, mPersonMap))) {
                            defaultVipBgBean = imageInfoBean;
                            break;
                        }
                    }
                }
            }
            if (defaultVipBgBean != null) {
                defaultVipBgImgRatio = defaultVipBgBean.getImgRatio();
                //设置背景图片
                String imgBgUrl = ReplaceHolderUtils.replaceKeysWithValues(defaultVipBgBean.getImageUrl(), mPersonMap);
                ImageLoader.loadImage(getContext(), imgBgUrl, imgTopBg, R.drawable.img_mine_top_bg);
            }

        }
        //设置背景图片大小
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) imgTopBg.getLayoutParams();
        int vipbgWidth = UiUtil.getDeviceWidth();
        params.width = vipbgWidth;
        int vipbgHeight = (int) (vipbgWidth * defaultVipBgImgRatio);
        params.height = vipbgHeight;
        imgTopBg.setLayoutParams(params);

        //会员图片信息
        if (userComponentInfoBean.getVipState() != null && userComponentInfoBean.getVipState().getVipImage() != null && isShowVipImage(userComponentInfoBean.getVipState().getVipImage())) {
            VipImageBean vipImage = userComponentInfoBean.getVipState().getVipImage();
            List<VipImageListBean> vipImageList = vipImage.getVipImageList();
            if (vipImageList != null && vipImageList.size() > 0) {
                defaultVipImagBean = getVipImageBean(vipImageList);
                if (defaultVipImagBean != null) {
                    ivVip.setVisibility(VISIBLE);
                    postVipEvent(defaultVipImagBean.getExposure());
                    //设置会员
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ivVip.getLayoutParams();
                    int width = UiUtil.getDeviceWidth() - UiUtil.dip2px(getContext(), 30);
                    layoutParams.width = width;
                    int height = (int) (width * defaultVipImagBean.getImage().getImgRatio());
                    layoutParams.height = height;
                    ivVip.setLayoutParams(layoutParams);
                    ImageLoader.loadImage(getContext(), defaultVipImagBean.getImage().getImageUrl(), ivVip, R.drawable.img_vip_default);

                } else {
                    ivVip.setVisibility(GONE);
                }
            } else {
                ivVip.setVisibility(GONE);
            }
        } else {
            ivVip.setVisibility(GONE);
        }

        //会员提示语
        if (userComponentInfoBean.getVipState() != null && isShowVipTips(userComponentInfoBean.getVipState().getVipTips()) && !TextUtils.isEmpty(getVipTips(userComponentInfoBean.getVipState().getVipTips()))) {
            tvVipTips.setVisibility(VISIBLE);
            tvVipTips.setText(getVipTips(userComponentInfoBean.getVipState().getVipTips()));
        } else {
            tvVipTips.setVisibility(GONE);
        }

        if (!UserStateUtils.isLogIn()) { //非登录
            rvVipRight.setVisibility(GONE);
            rvVipBottom.setVisibility(GONE);
            tvUserNumLeft.setVisibility(GONE);
            tvUserName.setText("登录/注册");
            tvUserNumBottom.setText("登录可享更多精彩");
            tvUserNumBottom.setVisibility(VISIBLE);
        } else {
            //姓名
            String userName = ReplaceHolderUtils.replaceKeysWithValues(userComponentInfoBean.getName(), mPersonMap);
            if (TextUtils.isEmpty(userName)) {
                tvUserName.setText("Hi,你好");
            } else {//实名
                tvUserName.setText(userName);
            }
            //手机号
            String userMobile = ReplaceHolderUtils.replaceKeysWithValues(userComponentInfoBean.getMobile(), mPersonMap);
            if (!TextUtils.isEmpty(userMobile)) {
                tvUserNumBottom.setText(userMobile);
                tvUserNumLeft.setText(userMobile);
            } else {
                tvUserNumLeft.setText("");
                tvUserNumBottom.setText("");
                tvUserNumLeft.setVisibility(GONE);
                tvUserNumLeft.setVisibility(GONE);
            }
            //创客
            if (isShowCK()) {
                tvUserNumLeft.setVisibility(VISIBLE);
                tvUserNumBottom.setVisibility(GONE);
                rvVipRight.setVisibility(GONE);
                rvVipBottom.setVisibility(VISIBLE);
            } else {
                rvVipBottom.setVisibility(GONE);
                tvUserNumLeft.setVisibility(GONE);
                tvUserNumBottom.setVisibility(VISIBLE);
            }
            //viptag数据
            List<UserTagBean> userTagList = userComponentInfoBean.getUserTag();
            if (userTagList != null && userTagList.size() > 0) {
                if (vipTagList != null) {
                    vipTagList.clear();
                    vipTagList.addAll(userTagList);
                }
                if (vipTagAdapter != null) {
                    vipTagAdapter.setPersonCenterData(mPersonMap);
                    vipTagAdapter.notifyDataSetChanged();
                }
            } else {
                tvUserNumLeft.setVisibility(GONE);
                tvUserNumBottom.setVisibility(VISIBLE);
                rvVipRight.setVisibility(GONE);
                rvVipBottom.setVisibility(GONE);
            }


        }
        //金刚位数据
        if (componentInfoBean.getSourceData() != null && componentInfoBean.getSourceData().getMenus().size() > 0) {
            if (menuList != null) {
                menuList.clear();
                menuList.addAll(componentInfoBean.getSourceData().getMenus());
            }
            if (jinGComponentAdapter != null) {
                jinGComponentAdapter.setPersonCenterData(mPersonMap);
                jinGComponentAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onClick(View v) {
        if (getContext() == null) return;
        switch (v.getId()) {
            case R.id.iv_vip: // vip跳转
                if (defaultVipImagBean != null) {
                    ActionBean action = defaultVipImagBean.getAction();
                    action.setJumpUrl(ReplaceHolderUtils.replaceKeysWithValues(action.getJumpUrl(), mPersonMap));
                    postVipEvent(action.getEvent());
                    JumpUtils.jumpAction(getContext(), action);
                }
                break;
            case R.id.img_user_avatar: //头像点击
            case R.id.tv_user_name: //头像点击
                if (userComponentInfoBean != null && userComponentInfoBean.getAvatar() != null) {
                    AvatarBean avatarBean = userComponentInfoBean.getAvatar();
                    ActionBean action = avatarBean.getAction();
                    action.setJumpUrl(ReplaceHolderUtils.replaceKeysWithValues(action.getJumpUrl(), mPersonMap));
                    postHeadClickEvent(action.getEvent());
                    if (action.getNeedLogin() && !UserStateUtils.isLogIn()) {
                        LoginSelectHelper.staticToGeneralLogin();
                    } else {
                        JumpUtils.jumpAction(getContext(), action);
                    }
                }
                break;

        }
    }

    //点击会员事件
    private void postVipEvent(HashMap<String, Object> event) {
        if (!CheckUtil.isEmpty(event)) {
            event.put("user_status", getUserState());
            event.put("is_member", getMember());
            UMengUtil.postEvent(event);
        }
    }

    //点击头像事件
    private void postHeadClickEvent(HashMap<String, Object> event) {
        if (!CheckUtil.isEmpty(event)) {
            event.put("user_status", getUserState());
            event.put("is_member", getMember());
            UMengUtil.postEvent(event);

        }
    }

    private String getUserState() {
        String status;
        if (!AppApplication.isLogIn()) {
            status = "未登录";
        } else if (CommomUtils.isRealName()) {
            status = "已实名";
        } else {
            status = "未实名";
        }
        return status;
    }

    //是否是会员
    private String getMember() {
        if (mPersonMap != null && mPersonMap.get("memberInfo.hyOpenState") != null) {
            return "1".equals(mPersonMap.get("memberInfo.hyOpenState")) ? "是会员" : "不是会员";
        }
        return "不是会员";
    }


    /**
     * 是否是创客
     *
     * @return
     */
    private boolean isShowCK() {
        String realValue = "";
        if (mPersonMap != null && mPersonMap.get("chuangkeCert.isShow") != null) {
            realValue = mPersonMap.get("chuangkeCert.isShow").toString();
        }
        return "1".equals(realValue);
    }


    /**
     * 是否展示会员图片
     *
     * @return
     */
    private boolean isShowVipImage(VipImageBean vipImage) {
        if (vipImage != null) {
            return ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(vipImage.getDefaultShow(), mPersonMap));
        }
        return false;
    }

    /**
     * 获取会员图片
     *
     * @param vipImageList
     * @return
     */
    private VipImageListBean getVipImageBean(List<VipImageListBean> vipImageList) {
        VipImageListBean vipImagBean = null;
        if (vipImageList != null && vipImageList.size() > 0) {
            //确定展示哪一张会员图片
            for (int i = 0; i < vipImageList.size(); i++) {
                ShowConditionBean showCondition = vipImageList.get(i).getImage().getShowCondition();
                if (showCondition != null) {
                    String standard = showCondition.getStandard();
                    String realValue = ReplaceHolderUtils.replaceKeysWithValues(showCondition.getKey(), mPersonMap);
                    if (standard.equals(realValue)) {
                        vipImagBean = vipImageList.get(i);
                        break;
                    }
                }
            }
        }
        return vipImagBean;
    }


    /**
     * 是否是会员提示语
     *
     * @return
     */
    private boolean isShowVipTips(VipTipsBean vipTipsBean) {
        if (vipTipsBean == null) {
            return false;
        }
        return ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(vipTipsBean.getDefaultShow(), mPersonMap));
    }

    /**
     * 是否是会员提示语
     *
     * @return
     */
    private String getVipTips(VipTipsBean vipTipsBean) {
        if (vipTipsBean == null || vipTipsBean.getTips() == null) return "";
        return ReplaceHolderUtils.replaceKeysWithValues(vipTipsBean.getTips(), mPersonMap);
    }


}
