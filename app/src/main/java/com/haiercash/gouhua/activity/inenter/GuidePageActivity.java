package com.haiercash.gouhua.activity.inenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.ViewPageAdapter;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.view.ViewPagerCircleLineIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Limige on 2017-07-05.
 * 引导页
 */

public class GuidePageActivity extends BaseActivity {

    @BindView(R.id.vp_guide)
    ViewPager mVpGuide;
    @BindView(R.id.indicator_line2)
    ViewPagerCircleLineIndicator indicator;
    @BindView(R.id.btn_next)
    Button btnNext;
    private int currentPos;

    // 引导页图片资源，如果引导页增加或减少只需修改图片资源即可
    private final int[] pics = {R.drawable.iv_guide_page_one, R.drawable.iv_guide_page_two,
            R.drawable.iv_guide_page_three};

    @Override
    protected int getLayout() {
        return R.layout.activity_guide_page;
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        InitViewPager();
    }

    // 初始化四个VierPager
    @SuppressLint("ClickableViewAccessibility")
    private void InitViewPager() {
        List<ImageView> mFragmentList = new ArrayList<>();
        // 设置全屏
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        for (int pic : pics) {
            //动态添加引导页图片
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(pic);
            mFragmentList.add(imageView);
        }
        mVpGuide.setAdapter(new ViewPageAdapter(mFragmentList));
        mVpGuide.setCurrentItem(0);
        indicator.setViewPager(mVpGuide, pics.length);
        mVpGuide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPos = position;
                if (position == pics.length - 1) {
                    btnNext.setVisibility(View.VISIBLE);
                } else {
                    btnNext.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mVpGuide.setOnTouchListener(new View.OnTouchListener() {
            float startX;
            float endX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        endX = event.getX();
                        if (currentPos == (pics.length - 1) && startX - endX >= 1) {
                            // 在引导页的最后一页进行点击进入主页
                            SpHp.saveSpOther(SpKey.OTHER_GUIDE_PAGE, "N");
                            Intent intent = new Intent();
                            intent.setClass(GuidePageActivity.this, MainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_right_in, R.anim.mg_slide_out_left);
                            finish();
                        }
                        break;
                }
                return false;
            }
        });
    }

    @OnClick(R.id.btn_next)
    public void viewOnClick() {
        // 在引导页的最后一页进行点击进入主页
        SpHp.saveSpOther(SpKey.OTHER_GUIDE_PAGE, "N");
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.mg_slide_out_left);
        finish();
    }
}
