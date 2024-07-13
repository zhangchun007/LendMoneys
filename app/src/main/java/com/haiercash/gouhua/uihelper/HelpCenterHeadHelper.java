package com.haiercash.gouhua.uihelper;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.haiercash.base.utils.router.ARouterUntil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.help.HelpCenterProblemBean;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.widget.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class HelpCenterHeadHelper {
    public static int item_grid_num = 8;//每一页中GridView中item的数量
    public static int number_columns = 4;//gridview一行展示的数目
    private List<GridView> gridList = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private BaseActivity mContext;

    public HelpCenterHeadHelper(LayoutInflater layoutInflater, BaseActivity mActivity) {
        this.layoutInflater = layoutInflater;
        this.mContext = mActivity;
    }

    public void initHeadView(final List<HelpCenterProblemBean> dataList, ViewPager rvHelpHead, CirclePageIndicator indicator) {
        HelpViewPagerAdapter mAdapter = new HelpViewPagerAdapter();
        rvHelpHead.setAdapter(mAdapter);
        //计算viewpager一共显示几页
        int pageSize = dataList.size() % item_grid_num == 0
                ? dataList.size() / item_grid_num
                : dataList.size() / item_grid_num + 1;
        //圆点指示器
        if (pageSize >= 2) {
            indicator.setVisibility(View.VISIBLE);
            indicator.setViewPager(rvHelpHead);
        }
        if (gridList.size() > 0) {
            gridList.clear();
        }

        for (int i = 0; i < pageSize; i++) {
            GridView gridView = new GridView(mContext);
            gridView.setGravity(Gravity.CENTER);
            GridViewAdapter adapter = new GridViewAdapter(dataList, i);
            gridView.setNumColumns(number_columns);
            gridView.setAdapter(adapter);
            gridList.add(gridView);
        }
        mAdapter.add(gridList);
    }

    private class HelpViewPagerAdapter extends PagerAdapter {
        private List<GridView> gridList;

        HelpViewPagerAdapter() {
            gridList = new ArrayList<>();
        }

        void add(List<GridView> datas) {
            if (gridList.size() > 0) {
                gridList.clear();
            }
            gridList.addAll(datas);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return gridList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(gridList.get(position));
            return gridList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private class GridViewAdapter extends BaseAdapter {
        private List<HelpCenterProblemBean> dataList;

        GridViewAdapter(List<HelpCenterProblemBean> datas, int page) {
            dataList = new ArrayList<>();
            //start end分别代表要显示的数组在总数据List中的开始和结束位置
            int start = page * item_grid_num;
            int end = start + item_grid_num;
            while ((start < datas.size()) && (start < end)) {
                dataList.add(datas.get(start));
                start++;
            }
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int i) {
            return dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View itemView, ViewGroup viewGroup) {
            ViewHolder mHolder;
            if (itemView == null) {
                mHolder = new ViewHolder();
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_head, viewGroup, false);
                mHolder.ivTabIcon = (ImageView) itemView.findViewById(R.id.iv_tab_icon);
                mHolder.tvTabName = (TextView) itemView.findViewById(R.id.tv_tab_name);
                mHolder.llHelpItem = (LinearLayout) itemView.findViewById(R.id.ll_help_item);

                itemView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) itemView.getTag();
            }
            final HelpCenterProblemBean bean = dataList.get(i);
            if (bean != null) {
                mHolder.tvTabName.setText(bean.getProblemType());
                GlideUtils.loadCenterCrop(mContext, mHolder.ivTabIcon, ApiUrl.urlAdPic + bean.getItemIcon(), R.drawable.icon_help_default, R.drawable.icon_help_default);
                mHolder.llHelpItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!CheckUtil.isEmpty(bean.getId())) {
                            Bundle bd = new Bundle();
                            bd.putString("problemTypeId", bean.getId() + "");
                            bd.putString("problemTypeTitle", bean.getProblemType());
                            // ContainerActivity.to(mContext, HelpCenterQuestionList.ID, bd);
                            ARouterUntil.getContainerInstance(PagePath.FRAGMENT_HELPER_CENTER_QUESTIONLIST).put(bd).navigation();
                        }
                    }
                });
            }

            return itemView;
        }

        private class ViewHolder {
            TextView tvTabName;
            ImageView ivTabIcon;
            LinearLayout llHelpItem;
        }
    }
}
