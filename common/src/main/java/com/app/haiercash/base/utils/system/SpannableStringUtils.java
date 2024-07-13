package com.app.haiercash.base.utils.system;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import com.app.haiercash.base.utils.system.CheckUtil;

import java.lang.ref.WeakReference;

import static android.graphics.BlurMaskFilter.Blur;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 16/12/13
 *     desc  : SpannableString相关工具类
 * </pre>
 */
public class SpannableStringUtils {

    private SpannableStringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 获取建造者
     *
     * @param text 样式字符串文本
     * @return {@link Builder}
     */
    public static Builder getBuilder(Context context, @NonNull CharSequence text) {
        return new Builder(context, text);
    }

    public static class Builder {
        private Context context;
        private int defaultValue = 0x12000000;
        private CharSequence text;

        private int flag;
        @ColorInt
        private int foregroundColor;
        @ColorInt
        private int backgroundColor;
        @ColorInt
        private int quoteColor;

        private boolean isLeadingMargin;
        private int first;
        private int rest;

        private boolean isBullet;
        private int gapWidth;
        private int bulletColor;

        private float proportion;
        private float xProportion;
        private boolean isStrikethrough;
        private boolean isUnderline;
        private boolean isSuperscript;
        private boolean isSubscript;
        private boolean isBold;
        private boolean isItalic;
        private boolean isBoldItalic;
        private String fontFamily;
        private Alignment align;

        private boolean imageIsBitmap;
        private Bitmap bitmap;
        private boolean imageIsDrawable;
        private Drawable drawable;
        private boolean imageIsUri;
        private Uri uri;
        private boolean imageIsResourceId;
        @DrawableRes
        private int resourceId;
        private int verticalAlignment;

        private ClickableSpan clickSpan;
        private String url;

        private boolean isBlur;
        private float radius;
        private Blur style;

        private SpannableStringBuilder mBuilder;


        private Builder(Context context, @NonNull CharSequence text) {
            this.context = context;
            this.text = text;
            flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
            foregroundColor = defaultValue;
            backgroundColor = defaultValue;
            quoteColor = defaultValue;
            proportion = -1;
            xProportion = -1;
            mBuilder = new SpannableStringBuilder();
        }

        /**
         * 设置标识
         *
         * @param flag <ul>
         *             <li>{@link Spanned#SPAN_INCLUSIVE_EXCLUSIVE}前面包括，后面不包括</li>
         *             <li>{@link Spanned#SPAN_INCLUSIVE_INCLUSIVE}前后都包括</li>
         *             <li>{@link Spanned#SPAN_EXCLUSIVE_EXCLUSIVE}前后都不包括</li>
         *             <li>{@link Spanned#SPAN_EXCLUSIVE_INCLUSIVE}前面不包括，后面包括</li>
         *             </ul>
         * @return {@link Builder}
         */
        public Builder setFlag(int flag) {
            this.flag = flag;
            return this;
        }

        /**
         * 设置前景色
         *
         * @param color 前景色
         * @return {@link Builder}
         */
        public Builder setForegroundColor(@ColorInt int color) {
            this.foregroundColor = color;
            return this;
        }

        /**
         * 设置背景色
         *
         * @param color 背景色
         * @return {@link Builder}
         */
        public Builder setBackgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        /**
         * 设置引用线的颜色
         *
         * @param color 引用线的颜色
         * @return {@link Builder}
         */
        public Builder setQuoteColor(@ColorInt int color) {
            this.quoteColor = color;
            return this;
        }

        /**
         * 设置缩进
         *
         * @param first 首行缩进
         * @param rest  剩余行缩进
         * @return {@link Builder}
         */
        public Builder setLeadingMargin(int first, int rest) {
            this.first = first;
            this.rest = rest;
            isLeadingMargin = true;
            return this;
        }

        /**
         * 设置列表标记
         *
         * @param gapWidth 列表标记和文字间距离
         * @param color    列表标记的颜色
         * @return {@link Builder}
         */
        public Builder setBullet(int gapWidth, int color) {
            this.gapWidth = gapWidth;
            bulletColor = color;
            isBullet = true;
            return this;
        }

        /**
         * 设置字体比例
         *
         * @param proportion 比例
         * @return {@link Builder}
         */
        public Builder setProportion(float proportion) {
            this.proportion = proportion;
            return this;
        }

        /**
         * 设置字体横向比例
         *
         * @param proportion 比例
         * @return {@link Builder}
         */
        public Builder setXProportion(float proportion) {
            this.xProportion = proportion;
            return this;
        }

        /**
         * 设置删除线
         *
         * @return {@link Builder}
         */
        public Builder setStrikethrough() {
            this.isStrikethrough = true;
            return this;
        }

        /**
         * 设置下划线
         *
         * @return {@link Builder}
         */
        public Builder setUnderline() {
            this.isUnderline = true;
            return this;
        }

        /**
         * 设置上标
         *
         * @return {@link Builder}
         */
        public Builder setSuperscript() {
            this.isSuperscript = true;
            return this;
        }

        /**
         * 设置下标
         *
         * @return {@link Builder}
         */
        public Builder setSubscript() {
            this.isSubscript = true;
            return this;
        }

        /**
         * 设置粗体
         *
         * @return {@link Builder}
         */
        public Builder setBold() {
            isBold = true;
            return this;
        }

        /**
         * 设置斜体
         *
         * @return {@link Builder}
         */
        public Builder setItalic() {
            isItalic = true;
            return this;
        }

        /**
         * 设置粗斜体
         *
         * @return {@link Builder}
         */
        public Builder setBoldItalic() {
            isBoldItalic = true;
            return this;
        }

        /**
         * 设置字体
         *
         * @param fontFamily 字体
         *                   <ul>
         *                   <li>monospace</li>
         *                   <li>serif</li>
         *                   <li>sans-serif</li>
         *                   </ul>
         * @return {@link Builder}
         */
        public Builder setFontFamily(@Nullable String fontFamily) {
            this.fontFamily = fontFamily;
            return this;
        }

        /**
         * 设置对齐
         *
         * @param align 对其方式
         *              <ul>
         *              <li>{@link Alignment#ALIGN_NORMAL}正常</li>
         *              <li>{@link Alignment#ALIGN_OPPOSITE}相反</li>
         *              <li>{@link Alignment#ALIGN_CENTER}居中</li>
         *              </ul>
         * @return {@link Builder}
         */
        public Builder setAlign(@Nullable Alignment align) {
            this.align = align;
            return this;
        }

        /**
         * 设置图片
         *
         * @param bitmap            图片位图
         * @param verticalAlignment 图片位置
         *                          <ul>
         *                          <li>AlignImageSpan.ALIGN_BOTTOM</li>
         *                          <li>AlignImageSpan.ALIGN_BASELINE</li>
         *                          <li>AlignImageSpan.CENTER</li>
         *                          </ul>
         * @return {@link Builder}
         */
        public Builder setBitmap(@NonNull Bitmap bitmap, int verticalAlignment) {
            this.bitmap = bitmap;
            this.verticalAlignment = verticalAlignment;
            imageIsBitmap = true;
            return this;
        }

        /**
         * 设置图片
         *
         * @param drawable          图片资源
         * @param verticalAlignment 图片位置
         *                          <ul>
         *                          <li>AlignImageSpan.ALIGN_BOTTOM</li>
         *                          <li>AlignImageSpan.ALIGN_BASELINE</li>
         *                          <li>AlignImageSpan.CENTER</li>
         *                          </ul>
         * @return {@link Builder}
         */
        public Builder setDrawable(@NonNull Drawable drawable, int verticalAlignment) {
            this.drawable = drawable;
            this.verticalAlignment = verticalAlignment;
            imageIsDrawable = true;
            return this;
        }

        /**
         * 设置图片
         *
         * @param uri               图片uri
         * @param verticalAlignment 图片位置
         *                          <ul>
         *                          <li>AlignImageSpan.ALIGN_BOTTOM</li>
         *                          <li>AlignImageSpan.ALIGN_BASELINE</li>
         *                          <li>AlignImageSpan.CENTER</li>
         *                          </ul>
         * @return {@link Builder}
         */
        public Builder setUri(@NonNull Uri uri, int verticalAlignment) {
            this.uri = uri;
            this.verticalAlignment = verticalAlignment;
            imageIsUri = true;
            return this;
        }

        /**
         * 设置图片
         *
         * @param resourceId        图片资源id
         * @param verticalAlignment 图片位置
         *                          <ul>
         *                          <li>AlignImageSpan.ALIGN_BOTTOM</li>
         *                          <li>AlignImageSpan.ALIGN_BASELINE</li>
         *                          <li>AlignImageSpan.CENTER</li>
         *                          </ul>
         * @return {@link Builder}
         */
        public Builder setResourceId(@DrawableRes int resourceId, int verticalAlignment) {
            this.resourceId = resourceId;
            this.verticalAlignment = verticalAlignment;
            imageIsResourceId = true;
            return this;
        }

        /**
         * 设置点击事件
         * <p>需添加view.setMovementMethod(LinkMovementMethod.getInstance())</p>
         *
         * @param clickSpan 点击事件
         * @return {@link Builder}
         */
        public Builder setClickSpan(@NonNull ClickableSpan clickSpan) {
            this.clickSpan = clickSpan;
            return this;
        }

        /**
         * 设置超链接
         * <p>需添加view.setMovementMethod(LinkMovementMethod.getInstance())</p>
         *
         * @param url 超链接
         * @return {@link Builder}
         */
        public Builder setUrl(@NonNull String url) {
            this.url = url;
            return this;
        }

        /**
         * 设置模糊
         * <p>尚存bug，其他地方存在相同的字体的话，相同字体出现在之前的话那么就不会模糊，出现在之后的话那会一起模糊</p>
         * <p>推荐还是把所有字体都模糊这样使用</p>
         *
         * @param radius 模糊半径（需大于0）
         * @param style  模糊样式<ul>
         *               <li>{@link Blur#NORMAL}</li>
         *               <li>{@link Blur#SOLID}</li>
         *               <li>{@link Blur#OUTER}</li>
         *               <li>{@link Blur#INNER}</li>
         *               </ul>
         * @return {@link Builder}
         */
        public Builder setBlur(float radius, Blur style) {
            this.radius = radius;
            this.style = style;
            this.isBlur = true;
            return this;
        }

        /**
         * 追加样式字符串
         *
         * @param text 样式字符串文本
         * @return {@link Builder}
         */
        public Builder append(@NonNull CharSequence text) {
            setSpan();
            this.text = text;
            return this;
        }

        /**
         * 创建样式字符串
         *
         * @return 样式字符串
         */
        public SpannableStringBuilder create() {
            setSpan();
            return mBuilder;
        }

        /**
         * 设置样式
         */
        private void setSpan() {
            int start = mBuilder.length();
            mBuilder.append(this.text);
            int end = mBuilder.length();
            if (foregroundColor != defaultValue) {
                mBuilder.setSpan(new ForegroundColorSpan(foregroundColor), start, end, flag);
                foregroundColor = defaultValue;
            }
            if (backgroundColor != defaultValue) {
                mBuilder.setSpan(new BackgroundColorSpan(backgroundColor), start, end, flag);
                backgroundColor = defaultValue;
            }
            if (isLeadingMargin) {
                mBuilder.setSpan(new LeadingMarginSpan.Standard(first, rest), start, end, flag);
                isLeadingMargin = false;
            }
            if (quoteColor != defaultValue) {
                mBuilder.setSpan(new QuoteSpan(quoteColor), start, end, 0);
                quoteColor = defaultValue;
            }
            if (isBullet) {
                mBuilder.setSpan(new BulletSpan(gapWidth, bulletColor), start, end, 0);
                isBullet = false;
            }
            if (!CheckUtil.isEqual(proportion, -1)) {
                mBuilder.setSpan(new RelativeSizeSpan(proportion), start, end, flag);
                proportion = -1;
            }
            if (!CheckUtil.isEqual(xProportion, 1)) {
                mBuilder.setSpan(new ScaleXSpan(xProportion), start, end, flag);
                xProportion = -1;
            }
            if (isStrikethrough) {
                mBuilder.setSpan(new StrikethroughSpan(), start, end, flag);
                isStrikethrough = false;
            }
            if (isUnderline) {
                mBuilder.setSpan(new UnderlineSpan(), start, end, flag);
                isUnderline = false;
            }
            if (isSuperscript) {
                mBuilder.setSpan(new SuperscriptSpan(), start, end, flag);
                isSuperscript = false;
            }
            if (isSubscript) {
                mBuilder.setSpan(new SubscriptSpan(), start, end, flag);
                isSubscript = false;
            }
            if (isBold) {
                mBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, flag);
                isBold = false;
            }
            if (isItalic) {
                mBuilder.setSpan(new StyleSpan(Typeface.ITALIC), start, end, flag);
                isItalic = false;
            }
            if (isBoldItalic) {
                mBuilder.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), start, end, flag);
                isBoldItalic = false;
            }
            if (fontFamily != null) {
                mBuilder.setSpan(new TypefaceSpan(fontFamily), start, end, flag);
                fontFamily = null;
            }
            if (align != null) {
                mBuilder.setSpan(new AlignmentSpan.Standard(align), start, end, flag);
                align = null;
            }
            if (imageIsBitmap || imageIsDrawable || imageIsUri || imageIsResourceId) {
                if (imageIsBitmap) {
                    mBuilder.setSpan(new AlignImageSpan(context, bitmap, verticalAlignment), start, end, flag);
                    bitmap = null;
                    imageIsBitmap = false;
                } else if (imageIsDrawable) {
                    mBuilder.setSpan(new AlignImageSpan(drawable, verticalAlignment), start, end, flag);
                    drawable = null;
                    imageIsDrawable = false;
                } else if (imageIsUri) {
                    mBuilder.setSpan(new AlignImageSpan(context, uri, verticalAlignment), start, end, flag);
                    uri = null;
                    imageIsUri = false;
                } else {
                    mBuilder.setSpan(new AlignImageSpan(context, resourceId, verticalAlignment), start, end, flag);
                    resourceId = 0;
                    imageIsResourceId = false;
                }
            }
            if (clickSpan != null) {
                mBuilder.setSpan(clickSpan, start, end, flag);
                clickSpan = null;
            }
            if (url != null) {
                mBuilder.setSpan(new URLSpan(url), start, end, flag);
                url = null;
            }
            if (isBlur) {
                mBuilder.setSpan(new MaskFilterSpan(new BlurMaskFilter(radius, style)), start, end, flag);
                isBlur = false;
            }
            flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
        }
    }

    public static class AlignImageSpan extends ImageSpan {
        public static final int CENTRE = 2;

        AlignImageSpan(@NonNull Context context, @NonNull Bitmap bitmap, int verticalAlignment) {
            super(context, bitmap, verticalAlignment);
        }

        AlignImageSpan(@NonNull Drawable drawable, int verticalAlignment) {
            super(drawable, verticalAlignment);
        }

        AlignImageSpan(@NonNull Context context, @NonNull Uri uri, int verticalAlignment) {
            super(context, uri, verticalAlignment);
        }

        AlignImageSpan(@NonNull Context context, @DrawableRes int resourceId, int verticalAlignment) {
            super(context, resourceId, verticalAlignment);
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            if (mVerticalAlignment == ALIGN_BASELINE) {
                super.draw(canvas, text, start, end, x, top, y, bottom, paint);
            } else if (mVerticalAlignment == ALIGN_BOTTOM) {
                super.draw(canvas, text, start, end, x, top, y, bottom, paint);
            } else {
                Drawable b = getCachedDrawable();
                canvas.save();

                Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
                // y 是基准线  Ascent是基准线 之上字符最高处的距离   descent 是基准线之下字符最低处的距离
                int transY = (y + fontMetricsInt.descent + y + fontMetricsInt.ascent) / 2 - b.getBounds().bottom / 2;
                canvas.translate(x, transY);
                b.draw(canvas);
                canvas.restore();
            }
        }

        private Drawable getCachedDrawable() {
            WeakReference<Drawable> wr = mDrawableRef;
            Drawable d = null;

            if (wr != null) {
                d = wr.get();
            }

            if (d == null) {
                d = getDrawable();
                mDrawableRef = new WeakReference<>(d);
            }

            return d;
        }

        private WeakReference<Drawable> mDrawableRef;
    }
}
