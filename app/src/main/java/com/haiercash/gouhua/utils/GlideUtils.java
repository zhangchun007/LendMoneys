package com.haiercash.gouhua.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.utils.log.Logger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;

import java.io.File;

/**
 * Author: Sun<br/>
 * Date :    2019/3/13<br/>
 * FileName: GlideUtils<br/>
 * Description:<br/>
 */
public class GlideUtils {

    private static final int placeholderResId = -1;
    private static final int errorResId = -1;
    public static final int TOP = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT_TOP = 3;
    public static final int RIGHT_TOP = 4;
    public static final int LEFT_BOTTOM = 5;
    public static final int RIGHT_BOTTOM = 6;
    public static final int ALL = 7;
    public static final int NONE = 8;

    public static void loadFit(Context context, ImageView iv, String url, int placeholderResId, int errorResId) {
        if (iv == null) {
            return;
        }
        RequestBuilder<Drawable> creator = Glide.with(context).load(url);
        if (placeholderResId > 0) {
            creator.placeholder(placeholderResId);
        }
        if (errorResId > 0) {
            creator.error(errorResId);
        }
        creator.fitCenter().into(iv);
    }

    public static void loadFitGif(Context context, ImageView iv, String url) {
        if (iv == null) {
            return;
        }
        if (url != null && (url.endsWith(".gif") || url.endsWith(".GIF"))) {
            Glide.with(context).asGif().load(url).fitCenter().into(iv);
        } else {
            Glide.with(context).load(url).fitCenter().into(iv);
        }
    }

    public static void loadFitGif(Context context, ImageView iv, String url, int defaultImageId, int errorImageId) {
        if (iv == null) {
            return;
        }
        if (url != null && (url.endsWith(".gif") || url.endsWith(".GIF"))) {
            Glide.with(context).asGif().load(url).placeholder(defaultImageId).error(errorImageId).fitCenter().into(iv);
        } else {
            Glide.with(context).load(url).placeholder(defaultImageId).error(errorImageId).fitCenter().into(iv);
        }
    }

    private static RoundedCornersTransform getRoundedCornersTransform(Context context, int which, int radius) {
        RoundedCornersTransform transform = new RoundedCornersTransform(context, UiUtil.dip2px(context, radius));
        switch (which) {
            case TOP:
                transform.setNeedCorner(true, true, false, false);
                break;
            case BOTTOM:
                transform.setNeedCorner(false, false, true, true);
                break;
            case LEFT_TOP:
                transform.setNeedCorner(true, false, false, false);
                break;

            case RIGHT_TOP:
                transform.setNeedCorner(false, true, false, false);
                break;

            case LEFT_BOTTOM:
                transform.setNeedCorner(false, false, true, false);
                break;

            case RIGHT_BOTTOM:
                transform.setNeedCorner(false, false, false, true);
                break;
            case ALL:
                transform.setNeedCorner(true, true, true, true);
                break;
            case NONE:
                transform.setNeedCorner(false, false, false, false);
                break;
        }
        return transform;
    }

    //加载带圆角图片
    public static void loadCenterCropRadius(Context context, ImageView iv, String url, int defaultId, int which, int radius) {
        RequestOptions options = new RequestOptions().placeholder(defaultId).transform(getRoundedCornersTransform(context, which, radius));
        Glide.with(context).asBitmap().load(url).apply(options).into(iv);
       /* if (placeholderResId > 0) {
            builder.placeholder(placeholderResId);
        }
        if (errorResId > 0) {
            builder.error(errorResId);
        }
        builder.fitCenter().into(iv);*/
    }

    //加载带圆角图片
    public static void loadCenterCropRadiusGif(Context context, ImageView iv, String url, int defaultId, int which, int radius) {
        RequestOptions options = new RequestOptions().placeholder(defaultId).transform(getRoundedCornersTransform(context, which, radius));
        if (url != null && (url.endsWith(".gif") || url.endsWith(".GIF"))) {
            Glide.with(context).asGif().load(url).apply(options).into(iv);
        } else {
            Glide.with(context).load(url).apply(options).into(iv);
        }
    }

    //加载带圆角图片
    public static void loadFitRadius(Context context, ImageView iv, String url, int defaultId, int errorId, int which, int radius) {
        RequestOptions options = new RequestOptions().fitCenter().placeholder(defaultId).error(errorId).transform(getRoundedCornersTransform(context, which, radius));
        Glide.with(context).asBitmap().load(url).apply(options).into(iv);
    }

    //加载带圆角图片
    public static void loadFitRadius(Context context, ImageView iv, String url, int defaultId, int which, int radius) {
        loadFitRadius(context, iv, url, defaultId, defaultId, which, radius);
    }

    //加载带圆角图片
    public static void loadFitRadius(Context context, ImageView iv, String url, int which, int radius) {
        loadFitRadius(context, iv, url, placeholderResId, errorResId, which, radius);
    }

    public static void loadCenterCrop(Context context, ImageView iv, String url, int placeholderResId, int errorResId) {
        RequestBuilder<Drawable> creator = Glide.with(context).load(url);
        if (placeholderResId > 0) {
            creator.placeholder(placeholderResId);
        }
        if (errorResId > 0) {
            creator.error(errorResId);
        }
        creator.into(iv);
    }

    //加载圆形图片
    public static void loadCircle(Context context, ImageView iv, String url) {
        Glide.with(context)
                .load(url)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv);

    }

    public static void loadFit(Context context, ImageView iv, String url) {
        loadFit(context, iv, url, placeholderResId, errorResId);
    }

    public static void loadFit(Context context, ImageView iv, String url, int defaultId) {
        loadFit(context, iv, url, defaultId, defaultId);
    }

    public static void loadCenterCrop(Context context, ImageView iv, String url, int defaultId) {
        loadCenterCrop(context, iv, url, defaultId, defaultId);
    }

    public static void loadCenterCrop(Context context, ImageView iv, String url) {
        loadCenterCrop(context, iv, url, placeholderResId, errorResId);
    }

    public static void loadCenterCrop(Context context, String url, int defaultId, GlideImageTarget listener) {
        RequestBuilder builder = Glide.with(context).load(url);
        if (defaultId > 0) {
            builder.placeholder(defaultId).error(defaultId);
        }
        builder.into(listener);
    }

    /**
     * 显示本地图片
     */
    public static void loadPicFormLocal(Context context, String path, ImageView imageView, int errorResId) {
        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
            return;
        }
        // 显示照片
        Glide.with(context)
                .load(new File(path))
                .skipMemoryCache(true)
                .error(errorResId)
                .into(imageView);
    }

    public static void loadHeadPortrait(Context context, String url, Drawable placeholderResId, GlideImageTarget listener) {
        loadHeadPhoto(context, url, true, placeholderResId).into(listener);
    }

    private static RequestBuilder loadHeadPhoto(Context context, String url, boolean isNeedError, Drawable placeholderResId) {
        if (isNeedError) {
            RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                    .placeholder(placeholderResId)
                    .skipMemoryCache(true);//不做内存缓存
            return Glide.with(context)
                    .load(url.contains(ApiUrl.urlCustImage) ? CommomUtils.reGlideHead(url) : url)
                    .error(R.drawable.iv_head_img)
                    .apply(mRequestOptions);
        } else {
            RequestOptions mRequestOptions = RequestOptions.circleCropTransform()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//不做磁盘缓存
                    .placeholder(placeholderResId)
                    .skipMemoryCache(true);//不做内存缓存
            return Glide.with(context)
                    .load(url.contains(ApiUrl.urlCustImage) ? CommomUtils.reGlideHead(url) : url)
                    .centerInside()
                    .apply(mRequestOptions);
        }
    }

    /**
     * 加载用户头像
     */
    public static void loadHeadPortrait(Context context, String url, ImageView imageView, boolean isNeedError) {
        loadHeadPhoto(context, url, isNeedError, null).into(imageView);
    }

    public static void loadHeadPortrait(Context context, String url, ImageView imageView, Drawable placeholderResId, boolean isNeedError) {
        loadHeadPhoto(context, url, isNeedError, placeholderResId).into(imageView);
    }

    public static void loadDrawableSourceGif(Context context, int resId, ImageView imageView) {
        Glide.with(context).asGif().load(resId).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(imageView);
    }

    public static void getNetBitmap(Context context, String url, final INetResult netResult) {
        Glide.with(context).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                netResult.onSuccess(resource, url);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // this is called when imageView is cleared on lifecycle call or for
                // some other reason.
                // if you are referencing the bitmap somewhere else too other than this imageView
                // clear it here as you can no longer have the bitmap
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                netResult.onError(null, url);
            }
        });
    }

    public static void getNetDrawable(Context context, String url, final INetResult netResult) {
        Glide.with(context).asDrawable().load(url).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                netResult.onSuccess(resource, url);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // this is called when imageView is cleared on lifecycle call or for
                // some other reason.
                // if you are referencing the bitmap somewhere else too other than this imageView
                // clear it here as you can no longer have the bitmap
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                netResult.onError(null, url);
            }
        });
    }

    /**
     * 定宽等比例高度加载图片
     */
    public static void loadForWidth(Context context, String url, final ImageView imageView, int imageW, int radius, final INetResult netResult) {
        Glide.with(context).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                try {
                    ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                    layoutParams.width = imageW;
                    layoutParams.height = ((imageW - imageView.getPaddingEnd() - imageView.getPaddingStart()) * resource.getHeight() / resource.getWidth()) + imageView.getPaddingTop() + imageView.getPaddingBottom();
                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageBitmap(getRoundBitmap(resource, layoutParams.width - imageView.getPaddingEnd() - imageView.getPaddingStart(),
                            layoutParams.height - imageView.getPaddingTop() - imageView.getPaddingBottom(), radius));
                    if (netResult != null) {
                        netResult.onSuccess(resource, null);
                    }
                } catch (Exception e) {
                    Logger.e("GlideUtils加载异常");
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                // this is called when imageView is cleared on lifecycle call or for
                // some other reason.
                // if you are referencing the bitmap somewhere else too other than this imageView
                // clear it here as you can no longer have the bitmap
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                try {
                    if (netResult != null) {
                        netResult.onError(null, null);
                    }
                } catch (Exception e) {
                    Logger.e("GlideUtils加载异常");
                }
            }
        });
    }

    /**
     * 定宽等比例高度加载图片
     */
    public static void loadForWidth(Context context, String url, final ImageView imageView, int imageW, int radius, int defaultImageId, final INetResult netResult) {
        if (url != null && (url.endsWith(".gif") || url.endsWith(".GIF"))) {
            Glide.with(context).asGif().load(url).placeholder(defaultImageId).error(defaultImageId).fitCenter().into(imageView);
        } else {
            Glide.with(context).asBitmap().load(url).placeholder(defaultImageId).error(defaultImageId).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    try {
                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                        layoutParams.width = imageW;
                        layoutParams.height = ((imageW - imageView.getPaddingEnd() - imageView.getPaddingStart()) * resource.getHeight() / resource.getWidth()) + imageView.getPaddingTop() + imageView.getPaddingBottom();
                        imageView.setLayoutParams(layoutParams);
                        imageView.setImageBitmap(getRoundBitmap(resource, layoutParams.width - imageView.getPaddingEnd() - imageView.getPaddingStart(),
                                layoutParams.height - imageView.getPaddingTop() - imageView.getPaddingBottom(), radius));
                        if (netResult != null) {
                            netResult.onSuccess(resource, null);
                        }
                    } catch (Exception e) {
                        Logger.e("GlideUtils加载异常");
                    }
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                    // this is called when imageView is cleared on lifecycle call or for
                    // some other reason.
                    // if you are referencing the bitmap somewhere else too other than this imageView
                    // clear it here as you can no longer have the bitmap
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                    try {
                        if (netResult != null) {
                            netResult.onError(null, null);
                        }
                    } catch (Exception e) {
                        Logger.e("GlideUtils加载异常");
                    }
                }
            });
        }

    }


    /**
     * 获取圆角bitmap
     */
    public static Bitmap getRoundBitmap(Bitmap bitmap, int w, int h, int cornerRadius) {
        if (bitmap == null) {
            return null;
        }
        if (cornerRadius <= 0) {
            return bitmap;
        }
        Bitmap output;
        try {
            output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError error) {
            try {
                output = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
            } catch (OutOfMemoryError e) {
                return bitmap;
            }
        }
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        // bitmap是上面的图片，不想要圆角的部分再画上即可
//        //下面对应取消左下角圆角
//        canvas.drawRect(new Rect(0, h - cornerRadius,
//                cornerRadius, h), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), rect, paint);
        return output;
    }
}
