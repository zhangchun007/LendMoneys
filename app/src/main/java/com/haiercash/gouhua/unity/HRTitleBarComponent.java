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
import com.haiercash.gouhua.beans.unity.ShowMoreBean;
import com.haiercash.gouhua.beans.unity.TitleBean;
import com.haiercash.gouhua.unity.JumpUtils;

import java.util.Map;


/**
 * @Description: 标题栏
 * @Author: zhangchun
 * @CreateDate: 2023/11/10
 * @Version: 1.0
 */
public class HRTitleBarComponent extends FrameLayout implements View.OnClickListener {
    private TextView tv_left;
    private TextView tv_right;
    private ShowMoreBean mShowMoreBean;
    private Context mContext;
    private Map<String, Object> mPersonMap;
    public HRTitleBarComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HRTitleBarComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRTitleBarComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (context == null) return;
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_title_bar_component, this);
        tv_left = view.findViewById(R.id.tv_left);
        tv_right = view.findViewById(R.id.tv_right);
        tv_left.setOnClickListener(this);
        tv_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
                break;
            case R.id.tv_right:
                if (mShowMoreBean != null && mShowMoreBean.getAction() != null && !TextUtils.isEmpty(mShowMoreBean.getAction().getJumpUrl())) {
                    ActionBean action = mShowMoreBean.getAction();
                    action.setJumpUrl(ReplaceHolderUtils.replaceKeysWithValues(action.getJumpUrl(),mPersonMap));
                    JumpUtils.jumpAction(mContext, action);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 设置数据
     *
     * @param
     */
    public void setData(TitleBean titleBean, boolean showTitle, ShowMoreBean showMoreBean, boolean showMore,Map<String, Object> map) {
        this.mShowMoreBean = showMoreBean;
        this.mPersonMap=map;
        if (titleBean != null && showTitle) {
            tv_left.setVisibility(VISIBLE);
            tv_left.setText(titleBean.getContent());
        } else {
            tv_left.setVisibility(GONE);
        }
        if (showMoreBean != null && showMore) {
            tv_right.setVisibility(VISIBLE);
        } else {
            tv_right.setVisibility(GONE);
        }

    }
}
