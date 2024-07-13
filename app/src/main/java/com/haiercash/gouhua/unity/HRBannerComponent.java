package com.haiercash.gouhua.unity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.unity.ActionBean;
import com.haiercash.gouhua.beans.unity.ComponentBean;
import com.haiercash.gouhua.beans.unity.ComponentInfoBean;
import com.haiercash.gouhua.beans.unity.ImageInfoBean;
import com.haiercash.gouhua.beans.unity.MenusBean;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: banner组件
 * @Author: zhangchun
 * @CreateDate: 2023/11/17
 * @Version: 1.0
 */
public class HRBannerComponent extends FrameLayout {
    private HRTitleBarComponent titleBarComponent;
    private Banner banner;
    private List<MenusBean> sourceList;
    private Map<String, Object> mPersonMap;
    //描述
    private String mDescription;

    public HRBannerComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HRBannerComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRBannerComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        if (context == null) return;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_banner_component, this);
        titleBarComponent = view.findViewById(R.id.tv_title_bar);
        banner = view.findViewById(R.id.banner);
    }

    /**
     * 设置数据
     *
     * @param componentBean
     */
    public void setData(ComponentBean componentBean, Map<String, Object> personMap) {
        mPersonMap = personMap;
        if (componentBean == null
                || !ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(componentBean.getDefaultShow(), mPersonMap))
                || componentBean.getData() == null) {
            setVisibility(GONE);
            return;
        }
        mDescription=componentBean.getDescription();
        ComponentInfoBean componentInfoBean = componentBean.getData();
        if (componentInfoBean != null && componentInfoBean.getSourceList() != null) {
            //设置标题
            boolean showTitle =false;
            if (componentInfoBean.getTitle()!=null){
                showTitle=  ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(componentInfoBean.getTitle().getDefaultShow(), mPersonMap));
            }
            boolean showMore = false;
            if (componentInfoBean.getShowMore()!=null){
                showMore=ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(componentInfoBean.getShowMore().getDefaultShow(), mPersonMap));
            }
            if (!showTitle && !showMore) {
                titleBarComponent.setVisibility(GONE);
            } else {
                titleBarComponent.setVisibility(VISIBLE);
                titleBarComponent.setData(componentInfoBean.getTitle(), showTitle, componentInfoBean.getShowMore(), showMore,mPersonMap);
            }
            //banner
            sourceList = componentInfoBean.getSourceList();
            List<String> images = new ArrayList<>();
            images.clear();
            for (int i = 0; i < sourceList.size(); i++) {
                images.add(sourceList.get(i).getImage().getImageUrl());
            }
            setBannerData(images, sourceList.get(0).getImage());
        }

    }

    private void setBannerData(List<String> images, ImageInfoBean imageInfoBean) {
        CommonGlideImageLoader commonGlideImageLoader = new CommonGlideImageLoader();
        commonGlideImageLoader.setBanner(banner, imageInfoBean,mDescription);
        //设置图片加载器
        banner.setImageLoader(commonGlideImageLoader);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.Default);
        //设置图片集合
        banner.setImages(images);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(Object object, int position) {
                ActionBean action = sourceList.get(position).getAction();
                action.setJumpUrl(ReplaceHolderUtils.replaceKeysWithValues(action.getJumpUrl(),mPersonMap));
                JumpUtils.jumpAction(getContext(), action);
            }
        });
        if (CheckUtil.isEmpty(images) || images.size() == 1) {
            //不轮播
            banner.isAutoPlay(false);
            //不可手动滑动
            banner.setViewPagerIsScroll(false);
            //设置banner样式，无指示器
            banner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        } else {
            //设置自动轮播，默认为true
            banner.isAutoPlay(true);
            //设置手动滑动
            banner.setViewPagerIsScroll(true);
            //设置轮播时间
            banner.setDelayTime(3000);
            //设置banner样式，有指示器
            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
            //设置指示器位置（当banner模式中有指示器时）
            banner.setIndicatorGravity(BannerConfig.CENTER);
        }
        banner.setIndicatorPadding(15);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }
}
