package com.haiercash.gouhua.unity;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.unity.ActionBean;
import com.haiercash.gouhua.beans.unity.ComponentBean;
import com.haiercash.gouhua.beans.unity.MenusBean;

import java.util.Map;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/12/18
 * @Version: 1.0
 */
public class HRServiceComponent extends FrameLayout {
    private Map<String, Object> mPersonMap;

    private TextView tv_online_service;

    public HRServiceComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HRServiceComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRServiceComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.layout_service_component, this);
        tv_online_service = view.findViewById(R.id.tv_online_service);
    }

    /**
     * 设置数据
     *
     * @param componentBean
     */
    public void setData(ComponentBean componentBean, Map<String, Object> personMap) {
        this.mPersonMap = personMap;
        if (getContext() == null || componentBean == null ||
                componentBean.getData() == null
                || componentBean.getData().getSourceList() == null
                || componentBean.getData().getSourceList().size() == 0) {
            setVisibility(GONE);
            return;
        }
        MenusBean menusBean = componentBean.getData().getSourceList().get(0);
        if (menusBean != null) {
            ActionBean action = menusBean.getAction();
            tv_online_service.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (action != null && !TextUtils.isEmpty(action.getJumpUrl()) && "jumpH5".equals(action.getActionType())) {
                        JumpUtils.jumpJsWebActivity(getContext(), action.getJumpUrl());
                    } else {
                        JumpUtils.jumpNative(getContext(), action.getActionType());
                    }
                }
            });
        }

    }
}
