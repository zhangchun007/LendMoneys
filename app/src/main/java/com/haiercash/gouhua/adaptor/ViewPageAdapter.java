package com.haiercash.gouhua.adaptor;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Limige on 2017-07-05.
 */

public class ViewPageAdapter extends PagerAdapter {
    private List<ImageView> mListViews;

    public ViewPageAdapter(List<ImageView> viewList) {
        if (viewList == null) {
            mListViews = new ArrayList<>();
        } else {
            this.mListViews = viewList;
        }
    }

    public void addData(ImageView iv) {
        mListViews.add(iv);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mListViews.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 初始化position位置的界面
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mListViews.get(position));
        return mListViews.get(position);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mListViews.get(position));
    }
}
