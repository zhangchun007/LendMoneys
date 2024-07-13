package com.haiercash.gouhua.adaptor;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.adapter.BaseMultiItemAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.homepage.Goods;
import com.haiercash.gouhua.beans.homepage.GoodsBean;
import com.haiercash.gouhua.beans.homepage.HomeNoticeBean;
import com.haiercash.gouhua.beans.homepage.HomePageBean;
import com.haiercash.gouhua.beans.homepage.ImageLinkBean;
import com.haiercash.gouhua.beans.homepage.LoanProductBean;
import com.haiercash.gouhua.beans.homepage.ProductBean;
import com.haiercash.gouhua.beans.homepage.ThemeBean;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.homepageview.HomepageQuotaCard;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.widget.BannerView;
import com.haiercash.gouhua.widget.ScrollTextSwitcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页多itemType的adapter
 */
public class HomepageAdapter extends BaseMultiItemAdapter<HomePageBean, ViewHolder> {
    @SuppressWarnings("rawtypes")
    private BaseActivity context;
    private boolean isShowNotice = true; //是否显示通知,默认显示
    private String bottomUrl = ""; //将解析的url暴露出去
    private final String pageCode;

    public HomepageAdapter(@SuppressWarnings("rawtypes") BaseActivity context, List<HomePageBean> data, String pageCode) {
        super(data);
        this.context = context;
        this.pageCode = pageCode;
        addItemType(HomePageBean.THEME, R.layout.item_homepage_theme);
        addItemType(HomePageBean.NOTICE, R.layout.item_homepage_notice);
        addItemType(HomePageBean.BANNER, R.layout.item_homepage_banner);
        addItemType(HomePageBean.VIP_BANNER, R.layout.item_homepage_vip_banner);
        addItemType(HomePageBean.GOODS, R.layout.item_homepage_goods);
        addItemType(HomePageBean.LOAN_PRODUCT, R.layout.item_homepage_loan_product);
        addItemType(HomePageBean.BOTTOM, R.layout.item_homepage_bottom);
        addItemType(HomePageBean.DEFAULT, R.layout.item_homepage_default);
    }

    private String getPageCode() {
        return this.pageCode;
    }

    @Override
    public void setNewData(List<HomePageBean> data) {
        Logger.d("MainBaseFragment  : HomepageAdapter  :  setNewData");
        if (!isShowNotice) {
            for (HomePageBean bean : data) {
                if (bean.getType().equals("notice")) {
                    data.remove(bean);
                    break;
                }
            }
        }
        super.setNewData(data);
    }

    @Override
    protected void convert(ViewHolder helper, HomePageBean item) {
        switch (helper.getItemViewType()) {
            case HomePageBean.THEME: //主题+额度信息
                Logger.d("MainBaseFragment  : HomepageAdapter  :  convert : HomePageBean.THEME");
                initThemeData(helper, item);
                break;
            case HomePageBean.BANNER: //banner
                initBannerData(helper, item);
                break;
            case HomePageBean.VIP_BANNER: //vipBanner
                initVipBannerData(helper, item);
                break;
            case HomePageBean.NOTICE: //公告
                initNoticeData(helper, item, helper.getLayoutPosition());
                break;
            case HomePageBean.GOODS: //推荐商品
                initGoodsType(helper, item);
                break;
            case HomePageBean.LOAN_PRODUCT: //够分期
                initLoanProductData(helper, item);
                break;
            case HomePageBean.BOTTOM: //底部
                initBottomData(helper, item);
                break;
            default:
                break;
        }
    }

    /**
     * 底部banner
     */
    private void initBottomData(ViewHolder helper, HomePageBean item) {
        ImageLinkBean imageLinkBean = JsonUtils.fromJson(item.getData(), ImageLinkBean.class);
        bottomUrl = imageLinkBean.getForwardUrl();
        GlideUtils.loadFit(context, helper.getView(R.id.iv_info), imageLinkBean.getImgUrl(), R.drawable.bg_gouhua);
        helper.itemView.setOnClickListener(v -> {
            UMengUtil.commonClickEvent("BrandKownUs_Click", "了解我们", getPageCode());
            MainHelper.ImageLinkRoute(context, imageLinkBean.getForwardUrl());
        });
    }

    /**
     * 够分期
     */
    private void initLoanProductData(ViewHolder helper, HomePageBean item) {
        LoanProductBean beans = JsonUtils.fromJson(item.getData(), LoanProductBean.class);
        List<ProductBean> products = beans.getProducts();
        List<ProductBean> showProducts = new ArrayList<>();
        //返回为空则不显示
        if (products == null || products.size() <= 0) {
            return;
        }
        if (beans.getMore() == 1 && products.size() > 3) {
            for (int i = 0; i < 3; i++) {  //后端返回确定需要显示更多，并且数据大于等于三条，则取前三条
                showProducts.add(products.get(i));
            }
            //手动拼接“更多”
            showProducts.add(new ProductBean("更多", "", beans.getMoreUrl(), ""));
        } else {
            if (products.size() == 1 || products.size() == 3) {
                products.add(new ProductBean("敬请期待", "payAttention", "", ""));
            }
            //最多只取前四条
            for (int i = 0; i < products.size(); i++) {
                if (i == 4) {
                    break;
                }
                showProducts.add(products.get(i));
            }
        }
        helper.setText(R.id.tv_title, TextUtils.isEmpty(item.getTitle()) ? "够分期" : item.getTitle());
        RecyclerView loanProductView = helper.getView(R.id.rv_loan_product);
        GridLayoutManager manager = new GridLayoutManager(context, 2);
        loanProductView.setLayoutManager(manager);
        BaseQuickAdapter<ProductBean, ViewHolder> adapter = new BaseQuickAdapter<ProductBean, ViewHolder>(R.layout.item_loan_product) {
            @Override
            protected void convert(ViewHolder holder, ProductBean bean) {
                int pos = holder.getLayoutPosition();
                holder.setText(R.id.tv_title, TextUtils.isEmpty(bean.getProductName()) ? "默认名称" : bean.getProductName());
                if ((beans.getMore() == 1 && pos == 3) || "payAttention".equals(bean.getProductImg())) {
                    GlideUtils.loadCenterCrop(context, holder.getView(R.id.iv_logo), "", R.drawable.icon_loan_product_more);
                } else {
                    GlideUtils.loadCenterCrop(context, holder.getView(R.id.iv_logo), bean.getProductImg(), R.drawable.ic_gouhua_login);
                }
                if (pos >= 2) {
                    holder.itemView.setBackgroundResource(R.drawable.bg_loan_product_2);
                }
            }
        };
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            ProductBean bean = (ProductBean) adapter1.getData().get(position);
            if ("payAttention".equals(bean.getProductImg())) {
                return;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("stage_id", bean.getProductCode());
            map.put("stage_name", bean.getProductName());
            UMengUtil.commonClickEvent("StageList_Click", null, map, getPageCode());
            MainHelper.ImageLinkRoute(context, bean.getProductUrl());

        });
        adapter.addData(showProducts);
        loanProductView.setAdapter(adapter);
    }

    /**
     * 推荐商品
     */
    private void initGoodsType(ViewHolder helper, HomePageBean item) {
        GoodsBean beans = JsonUtils.fromJson(item.getData(), GoodsBean.class);
        if (beans == null) {
            return;
        }
        helper.setText(R.id.tv_title, TextUtils.isEmpty(item.getTitle()) ? "猜你喜欢" : item.getTitle());
        helper.setGone(R.id.tv_more, beans.getMore() <= 0);
        helper.getView(R.id.tv_more).setOnClickListener(v -> {
            UMengUtil.commonClickEvent("GuessYouLikeMore_Click", "更多点击", getPageCode());
            MainHelper.ImageLinkRoute(context, beans.getMoreUrl());
        });
        List<Goods> goodsList = beans.getGoods();
        RecyclerView goodsView = helper.getView(R.id.rv_goods);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        goodsView.setLayoutManager(manager);
        BaseQuickAdapter<Goods, ViewHolder> adapter = new BaseQuickAdapter<Goods, ViewHolder>(R.layout.item_goods) {
            @Override
            protected void convert(ViewHolder baseViewHolder, Goods goods) {
                GlideUtils.loadCenterCropRadius(context, baseViewHolder.getView(R.id.iv_goods), goods.getImgUrl(), R.drawable.ic_gouhua_login, GlideUtils.TOP, 8);
                baseViewHolder.setText(R.id.tv_title, TextUtils.isEmpty(goods.getSkuName()) ? "默认商品名称" : goods.getSkuName());
                baseViewHolder.setText(R.id.tv_money, TextUtils.isEmpty(goods.getSalePrice()) ? "0.00" : goods.getSalePrice());
                baseViewHolder.setText(R.id.tv_tag, TextUtils.isEmpty(goods.getTag()) ? "0息0费" : goods.getTag());
            }
        };
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("good_id", goodsList.get(position).getSkuId());
            map.put("good_name", goodsList.get(position).getSkuName());
            UMengUtil.commonClickEvent("GuessYouLikeList_Click", null, map, getPageCode());
            MainHelper.ImageLinkRoute(context, goodsList.get(position).getUrl());
        });
        adapter.addData(goodsList);
        goodsView.setAdapter(adapter);

    }

    /**
     * 公告\通知：小黄条
     */
    private void initNoticeData(ViewHolder helper, HomePageBean item, int position) {
        List<HomeNoticeBean> noticeBeans = JsonUtils.fromJsonArray(item.getData(), HomeNoticeBean.class);
        if (noticeBeans.size() == 0) {
            return;
        }
        ImageView ivClose = helper.getView(R.id.iv_close);
        ivClose.setOnClickListener(v -> {
            UMengUtil.commonClickEvent("SystemNoticeClose_Click", "关闭", getPageCode());
            isShowNotice = false;
            getData().remove(position);
            //删除动画
            notifyItemRemoved(position);
            notifyDataSetChanged();
        });
        ScrollTextSwitcher tsNotice = helper.getView(R.id.ts_notice);
        tsNotice.setScrollListener(new ScrollTextSwitcher.ScrollViewClick() {
            @Override
            public String getTextValue(int index) {
                return noticeBeans.get(index).getTitle();
            }

            @Override
            public void onViewClick(View view, int flagTag, Object obj) {
                HomeNoticeBean bean = noticeBeans.get(flagTag);
                UMengUtil.commonClickEvent("SystemNoticeOpen_Click", bean.getId(), getPageCode());
                ARouterUntil.getContainerInstance(PagePath.FRAGMENT_NOTICE)
                        .put("noticeTitle", bean.getSummary())
                        .put("noticeTime", bean.getEffectiveTime())
                        .put("noticeContent", bean.getContentText())
                        .navigation();
            }
        });
        tsNotice.startScrollShow(noticeBeans.size());
    }

    private void initVipBannerData(ViewHolder helper, HomePageBean item) {
        List<ImageLinkBean> imageLinkBeanList = JsonUtils.fromJsonArray(item.getData(), ImageLinkBean.class);
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
                UMengUtil.commonClickBannerEvent("HomeMemberPosition_Click", getPageName(), imageLinkBeanList.get(0).getBannerName(),
                        imageLinkBeanList.get(0).getCid(), imageLinkBeanList.get(0).getGroupId(), getPageCode());
                if (!CheckUtil.isEmpty(url)) {
                    MainHelper.ImageLinkRoute(context, url);
                }
            }
        });
        UMengUtil.commonExposureEvent("HomeMemberPosition_Exposure", getPageName(), imageLinkBeanList.get(0).getBannerName(),
                imageLinkBeanList.get(0).getCid(), imageLinkBeanList.get(0).getGroupId(), getPageCode());
    }

    private void initBannerData(ViewHolder helper, HomePageBean item) {
        List<ImageLinkBean> imageLinkBeanList = JsonUtils.fromJsonArray(item.getData(), ImageLinkBean.class);
        BannerView banner = helper.getView(R.id.banner);
        banner.resetViewPageData();
        banner.setLayoutDotPosition(10);
        banner.setRadius(GlideUtils.NONE, 0);
        banner.resetViewPagerData(imageLinkBeanList, "imgUrl", (BannerView.CycleViewListener<ImageLinkBean>) (info, position, imageView) -> {
            String url = info.getForwardUrl();
            UMengUtil.commonClickBannerEvent("HomeAdPosition_Click", getPageName(), info.getBannerName(), info.getCid(), info.getGroupId(), getPageCode());
            MainHelper.ImageLinkRoute(context, url);
        });
        if (!CheckUtil.isEmpty(imageLinkBeanList) && imageLinkBeanList.get(0) != null) {
            UMengUtil.commonExposureEvent("HomeAdPosition_Exposure", getPageName(), imageLinkBeanList.get(0).getBannerName(),
                    imageLinkBeanList.get(0).getCid(), imageLinkBeanList.get(0).getGroupId(), getPageCode());
        }
    }

    private String getPageName() {
        return "首页";
    }

    /**
     * 顶部Banner
     */
    private void initThemeData(ViewHolder helper, HomePageBean item) {
        ThemeBean themeBean = JsonUtils.fromJson(item.getData(), ThemeBean.class);
        //额度
        ViewPager viewPager = helper.getView(R.id.unLoadPage);
        List<View> views = new ArrayList<>();
        boolean isLarge = themeBean != null && themeBean.getCredit() != null
                && themeBean.getCredit().getMain() != null;
//                && themeBean.getCredit().getSub() != null
//                && !themeBean.getCredit().getSub().isNull();

        RelativeLayout.LayoutParams quotaParams = (RelativeLayout.LayoutParams) viewPager.getLayoutParams();
        if (isLarge) {
            quotaParams.height = UiUtil.dip2px(context, 206);
        } else {
            quotaParams.height = UiUtil.dip2px(context, 182);
        }
        viewPager.setLayoutParams(quotaParams);
        HomepageQuotaCard quotaView = new HomepageQuotaCard(getContext()).setPageCode(getPageCode());//额度
        if (themeBean != null) {
            if (themeBean.getCredit() == null || themeBean.getCredit().getMain() == null) {
                quotaView.setData(null, context);
            } else {
                quotaView.setData(themeBean.getCredit(), context);
            }
        }
        views.add(quotaView);
        viewPager.setAdapter(new HomeEduAdapter(views));
        viewPager.setCurrentItem(Math.min(mPagerPos, views.size() - 1), false);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mPagerPos = position;
            }
        });
    }

    private int mPagerPos;

    //adapter解析的数据暴露给fragment
    public String getBottomUrl() {
        return bottomUrl;
    }
}
