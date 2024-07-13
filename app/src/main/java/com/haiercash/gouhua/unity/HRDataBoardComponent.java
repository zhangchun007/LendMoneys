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
 * @Description: 用户卡券组件
 * @Author: zhangchun
 * @CreateDate: 2023/11/15
 * @Version: 1.0
 */
public class HRDataBoardComponent extends FrameLayout {
    private HRTitleBarComponent titleBarComponent;
    private RecyclerView rvDataBoard;
    private GridLayoutManager layoutManager;
    private HRDataBoardAdapter dataBoardAdapter;
    private List<MenusBean> dataBoardList = new ArrayList<>();
    private Map<String, Object> mPersonMap;

    public HRDataBoardComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HRDataBoardComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRDataBoardComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.layout_databoard_component, this);
        titleBarComponent = view.findViewById(R.id.tv_title_bar);

        //列表
        rvDataBoard = view.findViewById(R.id.rv_data_board);
        layoutManager = new GridLayoutManager(context, 3);
        rvDataBoard.setLayoutManager(layoutManager);
        dataBoardAdapter = new HRDataBoardAdapter(dataBoardList);
        rvDataBoard.setAdapter(dataBoardAdapter);

        dataBoardAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                MenusBean menusBean = dataBoardList.get(position);
                ActionBean action = menusBean.getAction();
                action.setJumpUrl(ReplaceHolderUtils.replaceKeysWithValues(action.getJumpUrl(),mPersonMap));
                JumpUtils.jumpAction(getContext(), action);
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
        List<MenusBean> sourceList = componentInfoBean.getSourceList();
        if (sourceList.size() >= 0) {
            dataBoardList.clear();
            dataBoardList.addAll(sourceList);
            dataBoardAdapter.setPersonCenterData(mPersonMap);
            if (dataBoardAdapter != null)
                dataBoardAdapter.notifyDataSetChanged();
        }

    }

}
