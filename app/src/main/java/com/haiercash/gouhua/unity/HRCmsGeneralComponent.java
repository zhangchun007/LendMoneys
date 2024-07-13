package com.haiercash.gouhua.unity;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.unity.CmsBean;
import com.haiercash.gouhua.beans.unity.ComponentBean;
import com.haiercash.gouhua.beans.unity.ComponentInfoBean;

import java.util.Map;

/**
 * @Description: 一拖二 资源位
 * @Author: zhangchun
 * @CreateDate: 2023/12/14
 * @Version: 1.0
 */
public class HRCmsGeneralComponent extends FrameLayout {

    private HRTitleBarComponent titleBarComponent;
    private ImageView ivBig, ivRightTop, ivRightBottom;
    private Map<String, Object> mPersonMap;
    private Context mContext;

    public HRCmsGeneralComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HRCmsGeneralComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRCmsGeneralComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_general_component, this);
        titleBarComponent = view.findViewById(R.id.tv_title_bar);
        ivBig = view.findViewById(R.id.iv_big);
        ivRightTop = view.findViewById(R.id.iv_right_top);
        ivRightBottom = view.findViewById(R.id.iv_right_bottom);
    }

    /**
     * 设置数据
     *
     * @param componentBean
     */
    public void setData(ComponentBean componentBean, Map<String, Object> map) {
        this.mPersonMap = map;
        if (componentBean == null || CheckUtil.isEmpty(mPersonMap)) {
            setVisibility(GONE);
            return;
        }
        String isShow = componentBean.getDefaultShow();
        if (TextUtils.isEmpty(isShow)||"0".equals(isShow)){
            setVisibility(GONE);
            return;
        }
        String keyJson = ReplaceHolderUtils.replaceKeysWithValues(componentBean.getData().getCmsData(), mPersonMap);
        CmsBean cmsBean = JsonUtils.json2Class(keyJson, CmsBean.class);
        if (cmsBean == null
                || "0".equals(cmsBean.getIsShow())
                || cmsBean.getData() == null
                || cmsBean.getData().size() != 3) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);
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
            titleBarComponent.setData(componentInfoBean.getTitle(), showTitle, componentInfoBean.getShowMore(), showMore,mPersonMap);
        }

        String bigUrl = cmsBean.getData().get(0).getImgUrl();
        ImageLoader.loadImage(mContext, bigUrl, ivBig, R.drawable.img_big_left_default_bg);
        ivBig.setOnClickListener(v -> JumpUtils.jumpJsWebActivity(mContext, cmsBean.getData().get(0).getForwardUrl()));

        String rightTopUrl = cmsBean.getData().get(1).getImgUrl();
        ImageLoader.loadImage(mContext, rightTopUrl, ivRightTop, R.drawable.img_genral_top_default_bg);
        ivRightTop.setOnClickListener(v -> JumpUtils.jumpJsWebActivity(mContext, cmsBean.getData().get(1).getForwardUrl()));

        String rightBottomUrl = cmsBean.getData().get(2).getImgUrl();
        ImageLoader.loadImage(mContext, rightBottomUrl, ivRightBottom, R.drawable.img_general_bottom_default_bg);
        ivRightBottom.setOnClickListener(v -> JumpUtils.jumpJsWebActivity(mContext, cmsBean.getData().get(2).getForwardUrl()));
    }


}
