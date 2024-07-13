package com.haiercash.gouhua.adaptor;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeEduAdapter extends PagerAdapter {
    private List<View> mViews;

    public HomeEduAdapter(List<View> viewList) {
        if (viewList == null) {
            mViews = new ArrayList<>();
        } else {
            this.mViews = viewList;
        }
    }

    public void addData(View iv) {
        mViews.add(iv);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mViews.size();
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
        container.addView(mViews.get(position));
        return mViews.get(position);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }
}
