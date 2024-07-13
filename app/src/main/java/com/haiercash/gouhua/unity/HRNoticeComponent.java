package com.haiercash.gouhua.unity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.homepage.ConfigData;
import com.haiercash.gouhua.beans.homepage.Configs;
import com.haiercash.gouhua.beans.unity.ComponentBean;
import com.haiercash.gouhua.beans.unity.ComponentInfoBean;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.widget.ScrollTextSwitcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: cmsbanner组件
 * @Author: zhangchun
 * @CreateDate: 2023/11/17
 * @Version: 1.0
 */
public class HRNoticeComponent extends FrameLayout {
    private View ivCircle;
    ScrollTextSwitcher tsNotice;
    private List<ConfigData> sourceList;
    private Map<String, Object> mPersonMap;

    public HRNoticeComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HRNoticeComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRNoticeComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.layout_notice_component, this);
        ivCircle = view.findViewById(R.id.iv_circle);
        tsNotice = view.findViewById(R.id.ts_notice);
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
        Configs configs = JsonUtils.json2Class(keyJson, Configs.class);
        if (configs == null
                || configs.getData() == null
                || configs.getData().size() == 0) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
        ComponentInfoBean componentInfoBean = componentBean.getData();
        if (componentInfoBean != null && componentInfoBean.getCmsData() != null) {
            //banner
            sourceList = configs.getData();
            HashMap<String, Object> eventMap = componentInfoBean.getEvent();
            if (CheckUtil.isEmpty(sourceList)) {
                setVisibility(GONE);
                return;
            }
            tsNotice.setScrollListener(new ScrollTextSwitcher.ScrollViewClick() {
                @Override
                public String getTextValue(int index) {
                    if ("N".equals(sourceList.get(index).getReadStatus())) {
                        ivCircle.setVisibility(View.VISIBLE);
                    } else {
                        ivCircle.setVisibility(View.GONE);
                    }
                    return sourceList.get(index).getPushTitle();
                }

                @Override
                public void onViewClick(View view, int flagTag, Object obj) {
                    ConfigData bean = sourceList.get(flagTag);
                    //UmengUtil.commonClickEvent("SystemNoticeOpen_Click", bean.getId(), "消金-首页");
                    if (!"Y".equals(bean.getReadStatus())) {
                        bean.setReadStatus("Y");
                        //notifyDataSetChanged();
                    }
                    if (!CheckUtil.isEmpty(eventMap)) {
                        eventMap.put("button_name", bean.getId());
                        UMengUtil.postEvent(eventMap);
                    }
                    //通知后端消息已读
//                    if (mMessageReadListener != null) {
//                        mMessageReadListener.onMessageRead(bean.getId(), "N");
//                    }
                    //消息目前跟iOS统一只支持跳转url
                    if (!TextUtils.isEmpty(bean.getJumpUrl())) {
                        Intent intent = new Intent(getContext(), JsWebBaseActivity.class);
                        intent.putExtra("jumpKey", bean.getJumpUrl());
                        getContext().startActivity(intent);
                    }
                }
            });
            tsNotice.startScrollShow(sourceList.size());
        }
    }

}
