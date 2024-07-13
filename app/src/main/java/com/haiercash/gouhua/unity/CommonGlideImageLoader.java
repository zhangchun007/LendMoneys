package com.haiercash.gouhua.unity;

import android.content.Context;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;


import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.unity.ImageInfoBean;
import com.haiercash.gouhua.utils.UiUtil;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import java.util.Map;

/**
 *
 */

public class CommonGlideImageLoader extends ImageLoader {
    private Banner mBanner;
    private ImageInfoBean mImageInfoBean;
    private String mDescription;

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        /*
         注意：
         1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
         2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
         传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
         切记不要胡乱强转！
         */
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //Glide 加载图片
        if (mBanner != null && mImageInfoBean != null) {
            int imgWidth = SystemUtils.getDeviceWidth(context) - UiUtil.dip2px(context, 30);
            CardView.LayoutParams params = (CardView.LayoutParams) mBanner.getLayoutParams();
            params.width = imgWidth;
            params.height = (int) (imgWidth * mImageInfoBean.getImgRatio());
            mBanner.setLayoutParams(params);
            imageView.setLayoutParams(params);
        }

        Map<String, Object> map = FlattenJsonUtils.getMap();
        String imgUrl = ReplaceHolderUtils.replaceKeysWithValues((String) path, map);
        int defaultBg=R.drawable.img_banner_default_bg;
        if ("home_brand".equals(mDescription)){
            defaultBg=R.drawable.img_home_brand_default_bg;
        }else if ("person_brand".equals(mDescription)){
            defaultBg=R.drawable.img_person_brand_default_bg;
        }
        com.haiercash.gouhua.unity.ImageLoader.loadImage(context, imgUrl, imageView, defaultBg);

    }

    public void setBanner(Banner banner, ImageInfoBean imageInfoBean,String description) {
        this.mBanner = banner;
        this.mImageInfoBean = imageInfoBean;
        this.mDescription=description;
    }
}
