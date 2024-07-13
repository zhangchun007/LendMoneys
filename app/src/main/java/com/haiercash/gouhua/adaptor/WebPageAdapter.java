package com.haiercash.gouhua.adaptor;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.haiercash.gouhua.x5webview.CusWebView;

import java.util.ArrayList;
import java.util.List;

public class WebPageAdapter extends PagerAdapter {
    private List<CusWebView> mListViews;

    public WebPageAdapter(List<CusWebView> viewList) {
        if (viewList == null) {
            mListViews = new ArrayList<>();
        } else {
            this.mListViews = viewList;
        }
    }

    public void addData(CusWebView iv) {
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
