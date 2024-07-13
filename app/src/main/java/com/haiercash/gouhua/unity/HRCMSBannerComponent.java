package com.haiercash.gouhua.unity;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.unity.CmsBean;
import com.haiercash.gouhua.beans.unity.CmsInfo;
import com.haiercash.gouhua.beans.unity.ComponentBean;
import com.haiercash.gouhua.beans.unity.ComponentInfoBean;
import com.haiercash.gouhua.utils.UMengUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: cmsbanner组件
 * @Author: zhangchun
 * @CreateDate: 2023/11/17
 * @Version: 1.0
 */
public class HRCMSBannerComponent extends FrameLayout {
    private HRTitleBarComponent titleBarComponent;
    private Banner banner;
    private List<CmsInfo> sourceList;
    private Map<String, Object> mPersonMap;
    private HashMap<String, Object> exposureEvent; //曝光
    private HashMap<String, Object> clickEvent;//点击

    public HRCMSBannerComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HRCMSBannerComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRCMSBannerComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cms_banner_component, this);
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
        if (componentBean == null || CheckUtil.isEmpty(mPersonMap)) {
            setVisibility(GONE);
            return;
        }
        String isShow = componentBean.getDefaultShow();
        if (TextUtils.isEmpty(isShow)||"0".equals(isShow)) {
            setVisibility(GONE);
            return;
        }
        String keyJson = ReplaceHolderUtils.replaceKeysWithValues(componentBean.getData().getCmsData(), mPersonMap);
        CmsBean cmsBean = JsonUtils.json2Class(keyJson, CmsBean.class);
        if (cmsBean == null
                || "0".equals(cmsBean.getIsShow())
                || cmsBean.getData() == null
                || cmsBean.getData().size() == 0) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        ComponentInfoBean componentInfoBean = componentBean.getData();
        if (componentInfoBean != null && componentInfoBean.getCmsData() != null) {
            //设置标题
            boolean showTitle = false;
            if (componentInfoBean.getTitle() != null) {
                showTitle = ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(componentInfoBean.getTitle().getDefaultShow(), mPersonMap));
            }
            boolean showMore = false;
            if (componentInfoBean.getShowMore() != null) {
                showMore = ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(componentInfoBean.getShowMore().getDefaultShow(), mPersonMap));
            }
            if (!showTitle && !showMore) {
                titleBarComponent.setVisibility(GONE);
            } else {
                titleBarComponent.setVisibility(VISIBLE);
                titleBarComponent.setData(componentInfoBean.getTitle(), showTitle, componentInfoBean.getShowMore(), showMore,mPersonMap);
            }
            //banner
            sourceList = cmsBean.getData();
            if (CheckUtil.isEmpty(sourceList)) {
                setVisibility(GONE);
                return;
            }
            exposureEvent = componentInfoBean.getExposure();
            clickEvent = componentInfoBean.getEvent();
            List<String> images = new ArrayList<>();
            for (int i = 0; i < sourceList.size(); i++) {
                images.add(sourceList.get(i).getImgUrl());
            }
            setBannerData(images);
        }

    }

    private void setBannerData(List<String> images) {
        banner.setImageLoader(new CommonGlideImageLoader());
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.Default);
        //设置图片集合
        banner.setImages(images);
        postExposureEvent();
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(Object object, int position) {
                JumpUtils.jumpJsWebActivity(getContext(), sourceList.get(position).getForwardUrl());
                postClickEvent(position + 1, sourceList.get(position));
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

    //点击事件
    private void postClickEvent(int pos, CmsInfo cmsInfo) {
        if (!CheckUtil.isEmpty(clickEvent)) {
            clickEvent.put("view_seat", pos);
            clickEvent.put("cid", cmsInfo.getCid());
            clickEvent.put("group_id", cmsInfo.getGroupId());
            UMengUtil.postEvent(clickEvent);
        }
    }

    private void postExposureEvent() {
        if (CheckUtil.isEmpty(exposureEvent)) {
            return;
        }
        if (sourceList.size() == 1) {
            exposureEvent.put("view_seat", 1);
            exposureEvent.put("cid", sourceList.get(0).getCid());
            exposureEvent.put("group_id", sourceList.get(0).getGroupId());
            UMengUtil.postEvent(exposureEvent);
        }
        banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!CheckUtil.isEmpty(sourceList) && sourceList.get(position) != null) {
                    exposureEvent.put("view_seat", position + 1);
                    exposureEvent.put("cid", sourceList.get(position).getCid());
                    exposureEvent.put("group_id", sourceList.get(position).getGroupId());
                    UMengUtil.postEvent(exposureEvent);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && banner != null) {
            banner.isAutoPlay(true);
            banner.start();
        } else {
            if (banner != null) {
                banner.isAutoPlay(false);
            }
        }
    }

}
