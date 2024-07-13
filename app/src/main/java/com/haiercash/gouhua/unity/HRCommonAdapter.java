package com.haiercash.gouhua.unity;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.adapter.BaseMultiItemAdapter;
import com.haiercash.gouhua.base.adapter.ViewHolder;
import com.haiercash.gouhua.beans.unity.ComponentBean;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/8
 * @Version: 1.0
 */
public class HRCommonAdapter extends BaseMultiItemAdapter<ComponentBean, ViewHolder> {

    private Activity mContext;
    private Map<String, Object> mPersonMap;

    public HRCommonAdapter(Activity context, List<ComponentBean> data) {
        super(data);
        this.mContext = context;
        //添加每个item类型的布局
        addItemType(ComponentBean.USERINFOCOMPONENT, R.layout.item_hr_user_info_component);
        addItemType(ComponentBean.DATABOARDCOMPONENT, R.layout.item_hr_data_board_component);
        addItemType(ComponentBean.MENUCOMPONENT, R.layout.item_hr_menu_component);
        addItemType(ComponentBean.REPAYMENTCOMPONENT, R.layout.item_hr_repayment_component);
        addItemType(ComponentBean.BANNERCOMPONENT, R.layout.item_hr_banner_component);
        addItemType(ComponentBean.FOOTVIEWCOMPONENT, R.layout.item_foot_view_component);
        addItemType(ComponentBean.BENEFITCOMPONENT, R.layout.item_benefit_view_component);
        addItemType(ComponentBean.CONSUMERCOMPONENT, R.layout.item_xiaobao_view_component);
        addItemType(ComponentBean.CMSBANNERCOMPONENT, R.layout.item_hr_cms_banner_component);
        addItemType(ComponentBean.SERVICECOMPONENT, R.layout.item_service_view_component);
        addItemType(ComponentBean.NOTICECOMPONENT, R.layout.item_notice_component);
    }

    /**
     * 重写setNewData() 如果配置的组件不显示删除
     *
     * @param data
     */
    @Override
    public void setNewData(@Nullable List<ComponentBean> data) {
        if (CheckUtil.isEmpty(data)) {
            return;
        }
        super.setNewData(data);
    }

    /**
     * 接受业务数据
     */
    public void setPersonCenterData(Map<String, Object> personMap) {
        this.mPersonMap = personMap;
    }

    /**
     * 每个item数据的处理
     *
     * @param holder
     * @param item
     */
    @Override
    protected void convert(@NonNull ViewHolder holder, ComponentBean item) {
        switch (item.getItemType()) {
            case ComponentBean.USERINFOCOMPONENT: //用户组件
                initUserInfoComponent(holder, item, mPersonMap);
                break;
            case ComponentBean.DATABOARDCOMPONENT://用户卡券组件
                initDataBoardComponent(holder, item, mPersonMap);
                break;
            case ComponentBean.MENUCOMPONENT://常用功能组件
                initMenuComponent(holder, item, mPersonMap);
                break;
            case ComponentBean.REPAYMENTCOMPONENT://待还组件
                initRepayMentComponent(holder, item, mPersonMap);
                break;
            case ComponentBean.BANNERCOMPONENT://banner组件
                initBannerComponent(holder, item, mPersonMap);
                break;

            case ComponentBean.FOOTVIEWCOMPONENT://banner组件
                initFootComponent(holder, item);
                break;
            case ComponentBean.BENEFITCOMPONENT://一拖二组件
                initCmsBenifitComponent(holder, item, mPersonMap);
                break;
            case ComponentBean.CONSUMERCOMPONENT://消宝组件
                initCmsXiaoBaoComponent(holder, item, mPersonMap);
                break;
            case ComponentBean.CMSBANNERCOMPONENT://cmsbanner组件
                initCmsBannerComponent(holder, item, mPersonMap);
                break;
            case ComponentBean.SERVICECOMPONENT://在线客服
                initServiceoComponent(holder, item, mPersonMap);
                break;
            case ComponentBean.NOTICECOMPONENT://notice组件
                initNoticeComponent(holder, item, mPersonMap);
                break;
            default:
                break;
        }
    }

    private void initNoticeComponent(ViewHolder holder, ComponentBean item, Map<String, Object> mPersonMap) {
        if (holder == null) return;
        HRNoticeComponent hrNoticeComponent = holder.getView(R.id.hr_notice_component);
        hrNoticeComponent.setData(item, mPersonMap);
    }

    /**
     * 消保
     *
     * @param holder
     * @param item
     */
    private void initCmsBannerComponent(ViewHolder holder, ComponentBean item, Map<String, Object> map) {
        if (holder == null) return;
        HRCMSBannerComponent hrCMSBanner = holder.getView(R.id.hr_cms_banner_component);
        hrCMSBanner.setData(item, map);
    }

    /**
     * 客服
     *
     * @param holder
     * @param item
     * @param mPersonMap
     */
    private void initServiceoComponent(ViewHolder holder, ComponentBean item, Map<String, Object> mPersonMap) {
        if (holder == null) return;
        HRServiceComponent hrServiceComponent = holder.getView(R.id.hr_item_service_component);
        hrServiceComponent.setData(item, mPersonMap);
    }

    /**
     * 消保
     *
     * @param holder
     * @param item
     */
    private void initCmsXiaoBaoComponent(ViewHolder holder, ComponentBean item, Map<String, Object> map) {
        if (holder == null) return;
        HRXBComponent hrxbComponent = holder.getView(R.id.hr_item_cms_xiobao_component);
        hrxbComponent.setData(item, map);
    }

    /**
     * 一拖二组件 权益组件
     *
     * @param holder
     * @param item
     */
    private void initCmsBenifitComponent(ViewHolder holder, ComponentBean item, Map<String, Object> map) {
        if (holder == null) return;
        HRCmsGeneralComponent hrCmsGeneralComponent = holder.getView(R.id.hr_item_cms_general_component);
        hrCmsGeneralComponent.setData(item, map);
    }

    /**
     * 底部布局
     *
     * @param holder
     * @param item
     */
    private void initFootComponent(ViewHolder holder, ComponentBean item) {
        if (holder == null || item == null) return;
        HRFootViewComponent hrFootViewComponent = holder.getView(R.id.hr_foot_view_component);
        hrFootViewComponent.setData(item.getData());
    }

    /**
     * banner组件
     *
     * @param holder
     * @param item
     */
    private void initBannerComponent(ViewHolder holder, ComponentBean item, Map<String, Object> personMap) {
        if (holder == null || item == null) return;
        HRBannerComponent hrBannerComponent = holder.getView(R.id.hr_banner_component);
        hrBannerComponent.setData(item,personMap);
    }

    /**
     * 待还组件
     *
     * @param holder
     * @param item
     */
    private void initRepayMentComponent(ViewHolder holder, ComponentBean item, Map<String, Object> personMap) {
        if (holder == null || item == null) return;
        HRRepaymentComponent hrRepaymentComponent = holder.getView(R.id.hr_repay_component);
        hrRepaymentComponent.setData(item, personMap);
    }


    /**
     * 用户组件
     *
     * @param holder
     * @param item
     */
    private void initUserInfoComponent(ViewHolder holder, ComponentBean item, Map<String, Object> personMap) {
        if (holder == null || item == null) return;
        HRUserInfoComponent hrUserInfoComponentnfo = holder.getView(R.id.hr_user_info_component);
        hrUserInfoComponentnfo.setData(item.getData(), personMap);
    }

    /**
     * 用户卡券组件
     *
     * @param holder
     * @param item
     */
    private void initDataBoardComponent(ViewHolder holder, ComponentBean item, Map<String, Object> personMap) {
        if (holder == null || item == null) return;
        HRDataBoardComponent hrDataBoardComponent = holder.getView(R.id.hr_data_board_component);
        hrDataBoardComponent.setData(item, personMap);
    }


    /**
     * 常用功能组件
     *
     * @param holder
     * @param item
     */
    private void initMenuComponent(ViewHolder holder, ComponentBean item, Map<String, Object> personMap) {
        if (holder == null || item == null) return;
        HRMenuComponent hrMenuComponent = holder.getView(R.id.hr_menu_component);
        hrMenuComponent.setData(item,personMap);
    }


}
