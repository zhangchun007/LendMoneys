package com.haiercash.gouhua.unity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.log.Logger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.utils.CommomUtils;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com<br/>
 * 版    本：1.0<br/>
 * 创建日期：2021/3/29<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ImageLoader {

    public static void loadImage(Context context, String url, ImageView iv) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.src_default_banner)
                .error(R.drawable.src_default_banner)
                .into(iv);
    }

    /**
     * R.drawable.src_default_banner
     *
     * @param context
     * @param url
     * @param iv
     * @param defaultDrawable
     */
    public static void loadImage(Context context, String url, ImageView iv, int defaultDrawable) {
        RequestOptions optionsOrder = new RequestOptions()
                .placeholder(defaultDrawable)
                .error(defaultDrawable);

        Glide.with(context)
                .load(url.contains(ApiUrl.urlCustImage) ? CommomUtils.reGlideHead(url) : url)
                .apply(optionsOrder)
                .into(iv);
    }

    /**
     * R.drawable.src_default_banner
     *
     * @param context
     * @param url
     * @param iv
     * @param defaultDrawable
     */
    public static void loadHeadImage(Context context, String url, ImageView iv, int defaultDrawable) {
        RequestOptions optionsOrder = new RequestOptions().circleCropTransform()
                .placeholder(defaultDrawable)
                .error(defaultDrawable);

        Glide.with(context)
                .load(url.contains(ApiUrl.urlCustImage) ? CommomUtils.reGlideHead(url) : url)
                .apply(optionsOrder)
                .into(iv);
    }

    /**
     * 定宽等比例高度加载图片
     */
    public static void loadForWidth(Context context, String url, final ImageView imageView, int imageW, int radius, int defaultImageId, final LoadImageListener netResult) {
        if (url != null && (url.endsWith(".gif") || url.endsWith(".GIF"))) {
            Glide.with(context).asGif().load(url).placeholder(defaultImageId).error(defaultImageId).fitCenter().into(imageView);
        } else {
            Glide.with(context).asBitmap().load(url).placeholder(defaultImageId).error(defaultImageId).into(new CustomTarget<Bitmap>() {
                @Override
                public void onStart() {
                    super.onStart();
                    imageView.setImageResource(defaultImageId);
                }

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
                            netResult.onLoadSuccess(layoutParams);
                        }
                        Logger.e("GlideUtils加载成功了--" + url + "==imageW==" + imageW + "--resource.getHeight()==" + resource.getHeight() + "--resource.getWidth()==" + resource.getWidth());
                    } catch (Exception e) {
                        Logger.e("GlideUtils加载异常了--" + url);
                        if (netResult != null) {
                            netResult.onLoadError(url);
                        }
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
                            netResult.onLoadError(url);
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
