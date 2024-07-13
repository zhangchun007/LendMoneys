package com.haiercash.gouhua.unity;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.haiercash.adapter.HomeGuardHeadAdapter;
import com.haiercash.adapter.HomeGuardNewsAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.homepage.ConfigData;
import com.haiercash.gouhua.beans.unity.ActionBean;
import com.haiercash.gouhua.beans.unity.ComponentBean;
import com.haiercash.gouhua.beans.unity.ComponentInfoBean;
import com.haiercash.gouhua.beans.unity.ShowMoreBean;
import com.haiercash.gouhua.beans.unity.XiaoBaoBean;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 消宝
 * @Author: zhangchun
 * @CreateDate: 2023/12/15
 * @Version: 1.0
 */
public class HRXBComponent extends FrameLayout {

    private HRTitleBarComponent titleBarComponent;
    private RecyclerView rvHorizontalNotice, rvNews;
    private List<ConfigData> guardHeadData = new ArrayList<>();
    private List<ConfigData> guardNewsData = new ArrayList<>();
    private Map<String, Object> mPersonMap;
    private HomeGuardHeadAdapter homeGuardHeadAdapter;
    private HomeGuardNewsAdapter homeGuardNewsAdapter;

    public HRXBComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HRXBComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRXBComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.layout_xiaobao_component, this);
        titleBarComponent = view.findViewById(R.id.tv_title_bar);
        rvHorizontalNotice = view.findViewById(R.id.rvHorizontalNotice);
        rvNews = view.findViewById(R.id.rvNews);
        //图片
        homeGuardHeadAdapter = new HomeGuardHeadAdapter(guardHeadData);
        rvHorizontalNotice.setAdapter(homeGuardHeadAdapter);
        rvHorizontalNotice.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        //新闻
        homeGuardNewsAdapter = new HomeGuardNewsAdapter(guardNewsData);
        rvNews.setAdapter(homeGuardNewsAdapter);
        rvNews.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

    }

    /**
     * 设置数据
     *
     * @param componentBean
     */
    public void setData(ComponentBean componentBean, Map<String, Object> personMap) {
        this.mPersonMap = personMap;
        if (componentBean == null||mPersonMap==null) {
            setVisibility(GONE);
            return;
        }

        String isShow = componentBean.getDefaultShow();
        if (TextUtils.isEmpty(isShow)||"0".equals(isShow)){
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        String keyJson = ReplaceHolderUtils.replaceKeysWithValues(componentBean.getData().getCmsData(), mPersonMap);
        XiaoBaoBean xiaoBaoBean = JsonUtils.json2Class(keyJson, XiaoBaoBean.class);
        if ( xiaoBaoBean == null
                || xiaoBaoBean.getData() == null
                || xiaoBaoBean.getRecode() == null) {
            setVisibility(GONE);
            return;
        }
        ComponentInfoBean componentInfoBean = componentBean.getData();
        //设置标题
        boolean showTitle =false;
        if (componentInfoBean.getTitle()!=null){
            showTitle=  ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(componentInfoBean.getTitle().getDefaultShow(), mPersonMap));
        }
        boolean showMore = false;
        if (componentInfoBean.getShowMore()!=null){
            showMore=ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(componentInfoBean.getShowMore().getDefaultShow(), mPersonMap));
        }

        //设置标题
        if (!showTitle && !showMore) {
            titleBarComponent.setVisibility(GONE);
        } else {
            titleBarComponent.setVisibility(VISIBLE);
            ShowMoreBean showMoreBean = componentInfoBean.getShowMore();
            ActionBean actionBean = new ActionBean();
            actionBean.setJumpUrl(xiaoBaoBean.getForwardUrl());
            actionBean.setActionType("jumpH5");
            showMoreBean.setAction(actionBean);
            titleBarComponent.setData(componentInfoBean.getTitle(), showTitle, showMoreBean, showMore,mPersonMap);
        }
        guardHeadData.clear();
        guardNewsData.clear();

        guardHeadData.addAll(xiaoBaoBean.getData());
        homeGuardHeadAdapter.notifyDataSetChanged();
        homeGuardHeadAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                ConfigData configData = guardHeadData.get(position);
                if (!TextUtils.isEmpty(configData.getForwardUrl())) {
                    JumpUtils.jumpJsWebActivity(getContext(),configData.getForwardUrl());
                }
            }
        });
        guardNewsData.addAll(xiaoBaoBean.getRecode());
        homeGuardNewsAdapter.notifyDataSetChanged();
        homeGuardNewsAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                ConfigData configData = guardNewsData.get(position);
                if (!TextUtils.isEmpty(configData.getForwardUrl())) {
                    JumpUtils.jumpJsWebActivity(getContext(),configData.getForwardUrl());
                }
            }
        });


    }

}
