package com.haiercash.gouhua.unity;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.unity.ActionBean;
import com.haiercash.gouhua.beans.unity.ImageInfoBean;
import com.haiercash.gouhua.beans.unity.ShowConditionBean;
import com.haiercash.gouhua.beans.unity.UserTagBean;
import com.haiercash.gouhua.beans.unity.VipImageListBean;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/12/1
 * @Version: 1.0
 */
public class UserVipTagAdapter extends BaseQuickAdapter<UserTagBean, ViewHolder> {
    private Map<String, Object> mPersonMap;

    public UserVipTagAdapter(@Nullable List<UserTagBean> data) {
        super(R.layout.item_vip_tag, data);
    }

    @Override
    protected void convert(@NonNull ViewHolder holder, UserTagBean userTagBean) {
        ImageView imageView = holder.getView(R.id.iv_vip_tag);
        if (isShowImg(userTagBean)) {
            imageView.setVisibility(View.VISIBLE);
            ImageInfoBean imageTag = getImageTag(userTagBean);
            if (imageTag != null && !TextUtils.isEmpty(imageTag.getImageUrl())) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                int height = UiUtil.dip2px(getContext(), 21);
                params.height = height;
                params.width = (int) (height / imageTag.getImgRatio());
                String url=ReplaceHolderUtils.replaceKeysWithValues(imageTag.getImageUrl(),mPersonMap);
                ImageLoader.loadImage(getContext(), url, imageView, R.drawable.src_default_banner);
                imageView.setOnClickListener(v -> {
                    ActionBean action = getImageTagActionBean(userTagBean);
                    action.setJumpUrl(ReplaceHolderUtils.replaceKeysWithValues(action.getJumpUrl(),mPersonMap));
                    JumpUtils.jumpAction(getContext(), action);
                });
            } else {
                imageView.setVisibility(View.GONE);
            }
        } else {
            imageView.setVisibility(View.GONE);
        }

    }

    /**
     * 是否展示图片
     *
     * @param userTagBean
     * @return
     */
    private boolean isShowImg(UserTagBean userTagBean) {
        if (userTagBean != null) {
            return ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(userTagBean.getDefaultShow(), mPersonMap));
        }
        return false;
    }

    private ImageInfoBean getImageTag(UserTagBean userTagBean) {
        ImageInfoBean defaultImageTag = null;
        if (userTagBean != null && userTagBean.getImageList() != null && userTagBean.getImageList().size() > 0) {
            for (int i = 0; i < userTagBean.getImageList().size(); i++) {
                VipImageListBean vipImageListBean = userTagBean.getImageList().get(i);
                ImageInfoBean image = vipImageListBean.getImage();
                if (image != null && image.getShowCondition() != null) {
                    ShowConditionBean showCondition = image.getShowCondition();
                    String key = showCondition.getKey();
                    String standard = showCondition.getStandard();
                    if (standard.equals(ReplaceHolderUtils.replaceKeysWithValues(key, mPersonMap))) {
                        defaultImageTag = image;
                        break;
                    }
                }
            }
        }
        return defaultImageTag;
    }

    private ActionBean getImageTagActionBean(UserTagBean userTagBean) {
        ActionBean actionBean = null;
        if (userTagBean != null && userTagBean.getImageList() != null && userTagBean.getImageList().size() > 0) {
            for (int i = 0; i < userTagBean.getImageList().size(); i++) {
                VipImageListBean vipImageListBean = userTagBean.getImageList().get(i);
                ImageInfoBean image = vipImageListBean.getImage();
                if (image != null && image.getShowCondition() != null) {
                    ShowConditionBean showCondition = image.getShowCondition();
                    String key = showCondition.getKey();
                    String standard = showCondition.getStandard();
                    if (standard.equals(ReplaceHolderUtils.replaceKeysWithValues(key, mPersonMap))) {
                        actionBean = vipImageListBean.getAction();
                        break;
                    }
                }
            }
        }
        return actionBean;
    }


    /**
     * 接受业务数据
     */
    public void setPersonCenterData(Map<String, Object> personMap) {
        this.mPersonMap = personMap;
    }


}
