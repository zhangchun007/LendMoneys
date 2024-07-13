package com.haiercash.gouhua.adaptor.homepage;

import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.BuildConfig;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.AppUntil;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.adapter.BaseMultiItemAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.homepage.ConfigData;
import com.haiercash.gouhua.beans.homepage.Configs;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.widget.BannerView;
import com.haiercash.gouhua.widget.ScrollTextSwitcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页多itemType的adapter
 */
public class HomepageConfigAdapter extends BaseMultiItemAdapter<Configs, ViewHolder> {
    @SuppressWarnings("rawtypes")
    private BaseActivity context;
    private boolean isShowNotice = true; //是否显示通知,默认显示
    private String bottomUrl = ""; //将解析的url暴露出去
    private final String pageCode;

    public HomepageConfigAdapter(@SuppressWarnings("rawtypes") BaseActivity context, List<Configs> data, String pageCode) {
        super(data);
        this.context = context;
        this.pageCode = pageCode;
        addItemType(Configs.BANNER, R.layout.item_homepage_banner);
        addItemType(Configs.VIP_BANNER, R.layout.item_homepage_vip_banner);
        addItemType(Configs.GENERAL, R.layout.item_homepage_benefit);
        addItemType(Configs.BOTTOM, R.layout.item_homepage_bottom);
        addItemType(Configs.NOTICE, R.layout.item_homepage_notice);
    }

    private String getPageCode() {
        return this.pageCode;
    }

    @Override
    public void setNewData(List<Configs> data) {
        Logger.d("MainBaseFragment  : HomepageAdapter  :  setNewData");
        if (!isShowNotice) {
            for (Configs bean : data) {
                if (bean.getType().equals("notice")) {
                    data.remove(bean);
                    break;
                }
            }
        }
        super.setNewData(data);
    }

    @Override
    protected void convert(ViewHolder helper, Configs item) {
        RecyclerView.LayoutParams lp2 = (RecyclerView.LayoutParams) helper.itemView.getLayoutParams();

        if (helper.getLayoutPosition() == 1) {
            if (!AppApplication.isLogIn()) {
                lp2.setMargins(UiUtil.dip2px(getContext(), 15), UiUtil.dip2px(getContext(), -40), UiUtil.dip2px(getContext(), 15), UiUtil.dip2px(getContext(), 20));
            } else {
                lp2.setMargins(UiUtil.dip2px(getContext(), 15), UiUtil.dip2px(getContext(), 15), UiUtil.dip2px(getContext(), 15), UiUtil.dip2px(getContext(), 20));
            }
        } else {
            //极端状态下如进入首页无网，使用默认数据，有网后刷新数据，当前位置发生改变，此时会发生折叠
            if (lp2.topMargin != 0) {
                lp2.topMargin = UiUtil.dip2px(getContext(), 0);
            }
        }
        helper.itemView.setLayoutParams(lp2);
        switch (helper.getItemViewType()) {
            case Configs.BANNER: //banner
                initBannerData(helper, item);
                break;
            case Configs.VIP_BANNER: //vipBanner
                initVipBannerData(helper, item);
                break;
            case Configs.GENERAL: //专享福利
                initGeneralData(helper, item);
                break;
            case Configs.BOTTOM: //底部
                initBottomData(helper, item);
                break;
            case Configs.NOTICE: //公告
                initNoticeData(helper, item, getItemPosition(item));
                break;

            default:
                break;
        }
    }


    /**
     * 底部banner
     */
    private void initBottomData(ViewHolder helper, Configs item) {
        ConfigData imageLinkBean = item.getData().get(0);
        bottomUrl = imageLinkBean.getForwardUrl();
        helper.setText(R.id.tv_title, item.getTitle());
        GlideUtils.loadFit(context, helper.getView(R.id.iv_info), imageLinkBean.getImgUrl(), R.drawable.bg_gouhua);
        helper.itemView.setOnClickListener(v -> {
            UMengUtil.commonClickEvent("BrandKownUs_Click", "了解我们", "够花-首页", getPageCode());
            if (!CheckUtil.isEmpty(imageLinkBean.getForwardUrl())) {
                MainHelper.ImageLinkRoute(context, imageLinkBean.getForwardUrl());
            } else {
                showErrorDialog();
            }
        });
    }


    /**
     * 公告\通知：小黄条
     */
    private void initNoticeData(ViewHolder helper, Configs item, int position) {
        List<ConfigData> noticeBeans = item.getData();
        if (noticeBeans.size() == 0) {
            return;
        }
        ScrollTextSwitcher tsNotice = helper.getView(R.id.ts_notice);
        tsNotice.setScrollListener(new ScrollTextSwitcher.ScrollViewClick() {
            @Override
            public String getTextValue(int index) {
                View ivCircle = helper.getView(R.id.iv_circle);
                if ("N".equals(noticeBeans.get(index).getReadStatus())) {
                    ivCircle.setVisibility(View.VISIBLE);
                } else {
                    ivCircle.setVisibility(View.GONE);
                }
                return noticeBeans.get(index).getPushTitle();
            }

            @Override
            public void onViewClick(View view, int flagTag, Object obj) {
                ConfigData bean = noticeBeans.get(flagTag);
                UMengUtil.commonClickEvent("SystemNoticeOpen_Click", bean.getId(), "够花-首页", getPageCode());
                if (!"Y".equals(bean.getReadStatus())) {
                    bean.setReadStatus("Y");
                    notifyDataSetChanged();
                }
                ((MainActivity) context).updateReadStatus(noticeBeans.get(flagTag).getId());
            }
        });
        tsNotice.startScrollShow(noticeBeans.size());
    }

    //专享福利
    private void initGeneralData(ViewHolder helper, Configs item) {
        helper.setText(R.id.tv_title, item.getTitle());
        ConstraintLayout cl_root = helper.getView(R.id.cl_root);
        List<ConfigData> configData = item.getData();
        ImageView ivBig = helper.getView(R.id.iv_big);
        ImageView ivRightTop = helper.getView(R.id.iv_right_top);
        ImageView ivRightBottom = helper.getView(R.id.iv_right_bottom);
        //后台数据不等于3直接隐藏
        if (configData == null || configData.size() != 3) {
            cl_root.setVisibility(View.GONE);
            return;
        }

        String urlBig = BuildConfig.IS_RELEASE ? "https://static.haiercash.com/h5ActivityCenter/gouhuaService" : "https://testpm.haiercash.com:9002/h5ActivityCenter/gouhuaService";
        String urlTop = BuildConfig.IS_RELEASE ? "https://standardpay.haiercash.com/officialAccount" : "https://standardpay-stg.haiercash.com/officialAccount";
        String urlBottom = BuildConfig.IS_RELEASE ? "https://standardpay.haiercash.com/quotaGuide" : "https://standardpay-stg.haiercash.com/quotaGuide";
        GlideUtils.loadFit(context, ivBig, configData.get(0).getImgUrl(), R.drawable.icon_benifit_big);
        postGeneralEvent("Gh_Home_UsualBanner_Exposure", configData.get(0), "左");
        postGeneralEvent("Gh_Home_UsualBanner_Exposure", configData.get(1), "右上");
        postGeneralEvent("Gh_Home_UsualBanner_Exposure", configData.get(2), "右下");

        GlideUtils.loadFit(context, ivRightTop, configData.get(1).getImgUrl(), R.drawable.icon_benifit_top);
        GlideUtils.loadFit(context, ivRightBottom, configData.get(2).getImgUrl(), R.drawable.icon_benifit_bottom);
        ivBig.setOnClickListener(v -> {
            if (AppUntil.touristIntercept(ivBig, context)) {
                return;
            }
            if (!CheckUtil.isEmpty(configData.get(0).getForwardUrl())) {
                jumpWeb(configData.get(0).getForwardUrl());
                postGeneralEvent("Gh_Home_UsualBanner_Click", configData.get(0), "左");
            } else {
                jumpWeb(urlBig);
            }
        });
        ivRightTop.setOnClickListener(v -> {
            if (AppUntil.touristIntercept(ivRightTop, context)) {
                return;
            }
            if (!CheckUtil.isEmpty(configData.get(1).getForwardUrl())) {
                jumpWeb(configData.get(1).getForwardUrl());
                postGeneralEvent("Gh_Home_UsualBanner_Click", configData.get(1), "右上");
            } else {
                jumpWeb(urlTop);
            }
        });

        ivRightBottom.setOnClickListener(v -> {
            if (AppUntil.touristIntercept(ivRightBottom, context)) {
                return;
            }
            if (!CheckUtil.isEmpty(configData.get(2).getForwardUrl())) {
                jumpWeb(configData.get(2).getForwardUrl());
                postGeneralEvent("Gh_Home_UsualBanner_Click", configData.get(2), "右下");
            } else {
                jumpWeb(urlBottom);
            }
        });


    }

    private void postGeneralEvent(String id, ConfigData info, String position) {
        Map<String, Object> map = new HashMap<>();
        map.put("view_seat", position);
        UMengUtil.commonClickBannerEvent(id, getPageName(), "三分位", info.getCid(), info.getGroupId(), map, getPageCode());
    }

    //跳转
    private void jumpWeb(String url) {
        MainHelper.ImageLinkRoute(context, url);
    }


    private void initVipBannerData(ViewHolder helper, Configs item) {
        List<ConfigData> imageLinkBeanList = item.getData();
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) helper.getView(R.id.rl_root).getLayoutParams();
        if (CheckUtil.isEmpty(imageLinkBeanList) || imageLinkBeanList.get(0) == null || CheckUtil.isEmpty(imageLinkBeanList.get(0).getImgUrl())) {
            layoutParams.bottomMargin = 0;
            return;
        } else {
            layoutParams.bottomMargin = 0;
        }
        ImageView banner = helper.getView(R.id.iv_banner);
        GlideUtils.loadForWidth(context, imageLinkBeanList.get(0).getImgUrl(), banner,
                SystemUtils.getDeviceWidth(context) - 2 * UiUtil.dip2px(context, 15),
                UiUtil.dip2px(context, 8), new INetResult() {
                    @Override
                    public <T> void onSuccess(T t, String url) {
                        try {//因为高度自适应，所以要调整成功或者失败时的下边距，不然会导致item缝隙不一样
                            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) helper.getView(R.id.rl_root).getLayoutParams();
                            layoutParams.bottomMargin = UiUtil.dip2px(getContext(), 15);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(BasicResponse error, String url) {
                        try {
                            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) helper.getView(R.id.rl_root).getLayoutParams();
                            layoutParams.bottomMargin = 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = imageLinkBeanList.get(0).getForwardUrl();
                UMengUtil.commonClickBannerEvent("HomeMemberPosition_Click", getPageName(), imageLinkBeanList.get(0).getName(),
                        imageLinkBeanList.get(0).getCid(), imageLinkBeanList.get(0).getGroupId(), getPageCode());
                if (!CheckUtil.isEmpty(url)) {
                    MainHelper.ImageLinkRoute(context, url);
                }
            }
        });
        UMengUtil.commonExposureEvent("HomeMemberPosition_Exposure", getPageName(), imageLinkBeanList.get(0).getName(),
                imageLinkBeanList.get(0).getCid(), imageLinkBeanList.get(0).getGroupId(), getPageCode());
    }

    private void initBannerData(ViewHolder helper, Configs item) {
        List<ConfigData> imageLinkBeanList = item.getData();
        BannerView banner = helper.getView(R.id.banner);
        banner.resetViewPageData();
        banner.setLayoutDotPosition(10);
        banner.setRadius(GlideUtils.NONE, 0);
        banner.resetViewPagerData(imageLinkBeanList, "imgUrl", (BannerView.CycleViewListener<ConfigData>) (info, position, imageView) -> {
            if (AppUntil.touristIntercept(banner, context)) {
                return;
            }
            String url = info.getForwardUrl();
            Map<String, Object> map = new HashMap<>();
            map.put("view_seat", banner.getCurrentItem() + 1);
            UMengUtil.commonClickBannerEvent("HomeAdPosition_Click", getPageName(), info.getName(), info.getCid(), info.getGroupId(), map, getPageCode());
            MainHelper.ImageLinkRoute(context, url);
        });
        if (imageLinkBeanList.size() == 1) {
            Map<String, Object> map = new HashMap<>();
            map.put("view_seat", 1);
            UMengUtil.commonClickBannerEvent("HomeAdPosition_Exposure", getPageName(),
                    imageLinkBeanList.get(0).getName(),
                    imageLinkBeanList.get(0).getCid(),
                    imageLinkBeanList.get(0).getGroupId(),
                    map, getPageCode());
        }
        banner.addPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (!CheckUtil.isEmpty(imageLinkBeanList) && imageLinkBeanList.get(position) != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("view_seat", position + 1);
                    UMengUtil.commonClickBannerEvent("HomeAdPosition_Exposure", getPageName(),
                            imageLinkBeanList.get(position).getName(),
                            imageLinkBeanList.get(position).getCid(),
                            imageLinkBeanList.get(position).getGroupId(),
                            map, getPageCode());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private String getPageName() {
        return "首页";
    }

    private int mPagerPos;

    //adapter解析的数据暴露给fragment
    public String getBottomUrl() {
        return bottomUrl;
    }

    private void showErrorDialog() {
        context.showDialog(context.getString(R.string.network_error), "我知道了", "刷新重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == 2) {
                    RxBus.getInstance().post(new ActionEvent(ActionEvent.MainRefreshHomePage, "true", "true"));
                }
            }
        }).setStandardStyle(2);
    }
}
