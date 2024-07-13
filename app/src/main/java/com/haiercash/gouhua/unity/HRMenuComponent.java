package com.haiercash.gouhua.unity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.unity.ActionBean;
import com.haiercash.gouhua.beans.unity.ComponentBean;
import com.haiercash.gouhua.beans.unity.ComponentInfoBean;
import com.haiercash.gouhua.beans.unity.MenusBean;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 常用功能组件
 * @Author: zhangchun
 * @CreateDate: 2023/11/10
 * @Version: 1.0
 */
public class HRMenuComponent extends FrameLayout {

    private HRTitleBarComponent titleBarComponent;
    private RecyclerView rvMenu;
    private GridLayoutManager layoutManager;
    private MenuComponentAdapter menuComponentAdapter;
    private List<MenusBean> menuList = new ArrayList<>();
    private Map<String, Object> mPersonMap;

    public HRMenuComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HRMenuComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRMenuComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        if (context == null) return;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_menu_component, this);
        titleBarComponent = view.findViewById(R.id.tv_title_bar);

        //列表
        rvMenu = view.findViewById(R.id.rv_menu);
        layoutManager = new GridLayoutManager(context, 4);
        rvMenu.setLayoutManager(layoutManager);
        menuComponentAdapter = new MenuComponentAdapter(menuList);
        rvMenu.setAdapter(menuComponentAdapter);
        menuComponentAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                MenusBean menusBean = menuList.get(position);
                ActionBean action = menusBean.getAction();
                action.setJumpUrl(ReplaceHolderUtils.replaceKeysWithValues(action.getJumpUrl(),mPersonMap));
                JumpUtils.jumpAction(getContext(),action);
            }
        });
    }

    /**
     * 设置数据
     */
    public void setData(ComponentBean componentBean, Map<String, Object> personMap) {
        mPersonMap = personMap;
        if (componentBean == null
                || !ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(componentBean.getDefaultShow(), mPersonMap))
                || componentBean.getData() == null) {
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
        if (!showTitle && !showMore) {
            titleBarComponent.setVisibility(GONE);
        } else {
            titleBarComponent.setVisibility(VISIBLE);
            titleBarComponent.setData(componentInfoBean.getTitle(), showTitle, componentInfoBean.getShowMore(), showMore,mPersonMap);
        }
        //设置集合
        if (componentInfoBean.getSourceList().size() >= 0) {
            menuList.clear();
            menuList.addAll(componentInfoBean.getSourceList());
            if (menuComponentAdapter != null){
                menuComponentAdapter.setPersonCenterData(mPersonMap);
                menuComponentAdapter.notifyDataSetChanged();
            }
        }
    }


}
