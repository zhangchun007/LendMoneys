package com.haiercash.gouhua.utils;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.unity.LoadImageListener;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by zhangjie on 2018/1/10.
 * 图片加载器，为首页的轮播图设置图片加载方法
 */

public class HomeGlideImageLoader extends ImageLoader {
    private Banner mBanner;

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
        int imgWidth = SystemUtils.getDeviceWidth(context) - UiUtil.dip2px(context, 30);
        com.haiercash.gouhua.unity.ImageLoader.loadForWidth(context, (String) path, imageView, imgWidth, 0, R.drawable.src_default_banner, new LoadImageListener() {
            @Override
            public void onLoadSuccess(ViewGroup.LayoutParams layoutParams) {
                if (mBanner != null) {
                    CardView.LayoutParams params = (CardView.LayoutParams) mBanner.getLayoutParams();
                    params.width = layoutParams.width;
                    params.height = layoutParams.height;
                    mBanner.setLayoutParams(params);
                }
            }

            @Override
            public void onLoadError(String url) {
                com.haiercash.gouhua.unity.ImageLoader.loadImage(context, url, imageView);

            }
        });


    }

    public void setBanner(Banner banner) {
        this.mBanner = banner;
    }
}
