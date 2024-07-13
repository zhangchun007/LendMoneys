package com.haiercash.gouhua.unity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.unity.ComponentInfoBean;
import com.haiercash.gouhua.beans.unity.ImageInfoBean;


/**
 * @Description: 底部slogan布局
 * @Author: zhangchun
 * @CreateDate: 2023/12/1
 * @Version: 1.0
 */
public class HRFootViewComponent extends FrameLayout {
    private ImageView ivFoot;

    public HRFootViewComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HRFootViewComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRFootViewComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.layout_foot_view_component, this);
        ivFoot = view.findViewById(R.id.iv_foot);
    }


    /**
     * 设置数据
     *
     * @param componentInfoBean
     */
    public void setData(ComponentInfoBean componentInfoBean) {
        if (getContext() == null ||
                componentInfoBean == null
                || componentInfoBean.getSourceList() == null
                || componentInfoBean.getSourceList().size() == 0) {
            setVisibility(GONE);
            return;
        }
        ImageInfoBean imageInfoBean = componentInfoBean.getSourceList().get(0).getImage();
        ImageLoader.loadImage(getContext(), imageInfoBean.getImageUrl(), ivFoot, R.drawable.img_mine_slogan);
    }
}
