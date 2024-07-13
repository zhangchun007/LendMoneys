package com.haiercash.gouhua.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.uihelper.HomeNotchScreenHelper;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.view.WrapContentHeightViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/10/22<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BannerView extends LinearLayout {
    private static final int delayMillis = 3500;
    private Context context;
    private WrapContentHeightViewPager viewPager;
    private LinearLayout linearLayoutDots;
    private int pointDotIndex = 0;
    private int radiusType = GlideUtils.NONE;  //圆角类型
    private int radius = 0;   //圆角幅度
    private boolean supportGif;//是否需要支持加载动图

    private boolean isShowPoint;
    private ImageView.ScaleType mScaleType = ImageView.ScaleType.FIT_XY;
    private int defaultImageResId;
    private List<ImageView> mList = new ArrayList<>();
    private List mData = new ArrayList();
    private ViewPagerAdapter pagerAdapter;

    private String urlName;
    private CycleViewListener cycleViewListener;
    private ViewPager.OnPageChangeListener onPageChangeListener;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        try {
            boolean isNeedAdaptationScreen = array.getBoolean(R.styleable.BannerView_vpb_isNeedAdaptationScreen, false);
            isShowPoint = array.getBoolean(R.styleable.BannerView_vpb_isNeedShowPoint, true);
            final int index = array.getInt(R.styleable.BannerView_vpb_scaleType, -1);
            if (index >= 0) {
                mScaleType = sScaleTypeArray[index];
            }
            int resId = array.getResourceId(R.styleable.BannerView_vpb_defaultResId, -1);
            int resIdX = array.getResourceId(R.styleable.BannerView_vpb_defaultResId_x, -1);
            if (isNeedAdaptationScreen) {
                if ((context instanceof Activity) && HomeNotchScreenHelper.hasNotchInScreen((Activity) context)) {
                    defaultImageResId = resIdX;
                } else {
                    defaultImageResId = resId;
                }
            } else {
                defaultImageResId = resId;
            }
        } finally {
            array.recycle();
        }
        initView(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(Context context) {
        this.context = context;
        View.inflate(context, R.layout.view_cycle_viewpager_contet, this);
        viewPager = findViewById(R.id.index_home_viewpager);
        linearLayoutDots = findViewById(R.id.ll_dots);
        linearLayoutDots.setVisibility(isShowPoint ? VISIBLE : GONE);
        mList.add(getPageView(context, 0));
        pagerAdapter = new ViewPagerAdapter(mList, viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
                if (mList.size() > 1) {
                    if (position == 0 && CheckUtil.isEqual(positionOffset, 0)) {
                        viewPager.setCurrentItem(mList.size() - 2, false);
                    } else if (position == mList.size() - 1 && CheckUtil.isEqual(positionOffset, 0)) {
                        viewPager.setCurrentItem(1, false);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

                try {
                    if (mList.size() > 1) {
                        linearLayoutDots.getChildAt(pointDotIndex).setEnabled(false);
                        if (position == 0) {
                            pointDotIndex = linearLayoutDots.getChildCount() - 1;
                            linearLayoutDots.getChildAt(linearLayoutDots.getChildCount() - 1).setEnabled(true);
                        } else if (position == mList.size() - 1) {
                            pointDotIndex = 0;
                        } else {
                            pointDotIndex = position - 1;
                        }
                        linearLayoutDots.getChildAt(pointDotIndex).setEnabled(true);
                        if (onPageChangeListener != null) {
                            onPageChangeListener.onPageSelected(pointDotIndex);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }

            }
        });
        viewPager.setOnTouchListener((v, event) -> {
            if (mList.size() > 1) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mHandler.removeMessages(1);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mHandler.removeMessages(1);
                        break;
                    case MotionEvent.ACTION_UP:
                        mHandler.sendEmptyMessageDelayed(1, delayMillis);
                        break;
                    default:
                        break;
                }
            }
            return false;
        });
    }

    public void setSupportGif(boolean supportGif) {
        this.supportGif = supportGif;
    }

    public void addPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.onPageChangeListener = listener;
    }

    public void resetViewPagerData(List mData, String urlName, CycleViewListener cycleViewListener) {
        this.mData = mData;
        this.urlName = urlName;
        this.cycleViewListener = cycleViewListener;
        if (mData.size() == 1) {
            setImageUrl(mList.get(0), 0);
            return;
        }
        linearLayoutDots.removeAllViews();
        mList.clear();
        //pagerAdapter.notifyDataSetChanged();
        mList.add(getPageView(context, mData.size() - 1));
        if (mData.size() > 1) {
            for (int i = 0; i < mData.size(); i++) {
                mList.add(getPageView(context, i));
                initPoint(context);
            }
            mList.add(getPageView(context, 0));
        }
        pagerAdapter = new ViewPagerAdapter(mList, viewPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(mList.size());
        if (mData.size() > 1) {
            mHandler.sendEmptyMessageDelayed(1, delayMillis);
        }
    }

    /**
     * 重置Banner的默认状态
     */
    public void resetViewPageData() {
        if (mData != null) {
            mData.clear();
        }
        if (urlName != null) {
            urlName = null;
        }
        if (cycleViewListener != null) {
            cycleViewListener = null;
        }
        linearLayoutDots.removeAllViews();
        //mList.clear();
        //viewPager.removeAllViews();
        //mList.add(getPageView(context, -1));
        //viewPager.setAdapter(pagerAdapter);
        while (true) {
            if (mList.size() > 1) {
                mList.remove(1);
            } else if (mList.size() == 1) {
                break;
            } else {
                mList.add(getPageView(context, -1));
                break;
            }
        }
        mHandler.removeMessages(1);
        mList.get(0).setImageResource(defaultImageResId);
        pagerAdapter = new ViewPagerAdapter(mList, viewPager);
        viewPager.setAdapter(pagerAdapter);
    }

    /**
     * 视图单例
     */
    private ImageView getPageView(Context context, final int position) {
        // 设置全屏
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //动态添加引导页图片
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(params);
        imageView.setScaleType(mScaleType);
        imageView.setOnClickListener(v -> {
            if (cycleViewListener != null) {
                //noinspection unchecked
                int relPos = position < 0 ? 0 : position;
                if (mData.size() == 1) {
                    relPos = 0;
                } else if (mData.size() == 0) {
                    return;
                }
                cycleViewListener.onImageClick(mData.get(relPos), relPos, v);

            }
        });
        setImageUrl(imageView, position);
        return imageView;
    }

    /**
     * 设置imageView的网络URL
     */
    private void setImageUrl(ImageView imageView, int position) {
        try {
            String url = null;
            if (mData.size() > 0) {
                Object t = mData.get(position);
                Field field = t.getClass().getDeclaredField(urlName);
                field.setAccessible(true);
                url = String.valueOf(field.get(t));
            }
            if (CheckUtil.isEmpty(url)) {
                if (supportGif) {
                    GlideUtils.loadCenterCropRadiusGif(context, imageView, url, defaultImageResId, radiusType, radius);
                } else {
                    GlideUtils.loadCenterCropRadius(context, imageView, url, defaultImageResId, radiusType, radius);
                }
            } else {
                url = url.startsWith("http") || url.startsWith("HTTP") ? url : ApiUrl.urlAdPic + url;
                if (radiusType == GlideUtils.NONE) {
                    if (supportGif) {
                        GlideUtils.loadFitGif(context, imageView, url, defaultImageResId, defaultImageResId);
                    } else {
                        GlideUtils.loadFit(context, imageView, url, defaultImageResId);
                    }
                } else {
                    if (supportGif) {
                        GlideUtils.loadCenterCropRadiusGif(context, imageView, url, defaultImageResId, radiusType, radius);
                    } else {
                        GlideUtils.loadCenterCropRadius(context, imageView, url, defaultImageResId, radiusType, radius);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化小点点
     */
    private void initPoint(Context context) {
        View dot = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 6);
        params.leftMargin = 15;
        params.bottomMargin = 10;
        dot.setBackgroundResource(R.drawable.bg_selector_enabled_banner);
        dot.setLayoutParams(params);
        dot.setEnabled(false);
        linearLayoutDots.addView(dot);
    }

    /**
     * 设置默认图片显示的资源ID
     */
    public void setDefaultImageResId(int defaultImageResId) {
        this.defaultImageResId = defaultImageResId;
    }

    public void setRadius(int radiusType, int radius) {
        this.radiusType = radiusType;
        this.radius = radius;
    }

    /**
     * 小点点的位置
     *
     * @param bottomMargin 小点点距离下边沿的距离
     */
    public void setLayoutDotPosition(int bottomMargin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) linearLayoutDots.getLayoutParams();
        params.bottomMargin = bottomMargin;
    }

    public int getCurrentItem() {
        return viewPager != null ? viewPager.getCurrentItem() : -1;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int currentItem = viewPager.getCurrentItem();
                //设置ViewPager当前显示的界面,得到的ITem+1
                viewPager.setCurrentItem(currentItem + 1);
                //通过静态方法sendEmptyMessageDelayed,延时重复执行命令.注意不是sendEmptyMessageAtTime 333
                sendEmptyMessageDelayed(1, delayMillis);
            }
        }
    };


    private static class ViewPagerAdapter extends PagerAdapter {
        private List<ImageView> images;
        private ViewPager viewPager;

        /**
         * 构造方法，传入图片列表和ViewPager实例
         */
        ViewPagerAdapter(List<ImageView> images, ViewPager viewPager) {
            this.images = images;
            this.viewPager = viewPager;
        }

        @Override
        public int getCount() {
            return images.size();//返回一个无限大的值，可以 无限循环
        }

        /**
         * 判断是否使用缓存, 如果返回的是true, 使用缓存. 不去调用instantiateItem方法创建一个新的对象
         */
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        /**
         * 初始化一个条目
         *
         * @param position 当前需要加载条目的索引
         */
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            // 把position对应位置的ImageView添加到ViewPager中
            ImageView iv = images.get(position);
            viewPager.addView(iv);
            // 把当前添加ImageView返回回去.
            return iv;
        }

        /**
         * 销毁一个条目
         * position 就是当前需要被销毁的条目的索引
         */
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            // 把ImageView从ViewPager中移除掉
            if (images != null && images.size() >= position + 1) {
                viewPager.removeView(images.get(position));
            }
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }

    /**
     * 轮播控件的监听事件
     *
     * @author minking
     */
    public interface CycleViewListener<T> {
        /**
         * 单击图片事件
         */
        void onImageClick(T info, int position, View imageView);
    }


    private static final ImageView.ScaleType[] sScaleTypeArray = {
            ImageView.ScaleType.MATRIX,
            ImageView.ScaleType.FIT_XY,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END,
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE
    };
}
