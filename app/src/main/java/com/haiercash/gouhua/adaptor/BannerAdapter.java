package com.haiercash.gouhua.adaptor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.CreditLifeBorrowBean;
import com.haiercash.gouhua.fragments.CreditLifeBorrowFragment;
import com.haiercash.gouhua.uihelper.CreditLifeHelp;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends PagerAdapter {
    private BaseActivity mContext;
    private List<String> imgs = new ArrayList<>();
    public ImageView arrowImage;
    public TextView slideText;
    private List<CreditLifeBorrowBean> applyRecordeBean;

    public BannerAdapter(BaseActivity context, List<CreditLifeBorrowBean> applyRecordeBeanList) {
        this.mContext = context;
        this.applyRecordeBean = applyRecordeBeanList;
        if (applyRecordeBeanList != null && applyRecordeBeanList.size() >= 3) {
            if (!CheckUtil.isEmpty(applyRecordeBeanList.get(0).getImageAddressBig()) && !CheckUtil.isEmpty(applyRecordeBeanList.get(1).getImageAddressBig()) && !CheckUtil.isEmpty(applyRecordeBeanList.get(2).getImageAddressBig())) {
                imgs.add(applyRecordeBeanList.get(0).getImageAddressBig());
                imgs.add(applyRecordeBeanList.get(1).getImageAddressBig());
                imgs.add(applyRecordeBeanList.get(2).getImageAddressBig());
            }
        } else if (applyRecordeBeanList != null && applyRecordeBeanList.size() == 2) {
            if (!CheckUtil.isEmpty(applyRecordeBeanList.get(0).getImageAddressBig()) && !CheckUtil.isEmpty(applyRecordeBeanList.get(1).getImageAddressBig())) {
                imgs.add(applyRecordeBeanList.get(0).getImageAddressBig());
                imgs.add(applyRecordeBeanList.get(1).getImageAddressBig());
            }
        } else if (applyRecordeBeanList != null) {
            if (!CheckUtil.isEmpty(applyRecordeBeanList.get(0).getImageAddressBig())) {
                imgs.add(applyRecordeBeanList.get(0).getImageAddressBig());
            }
        }
    }

    @Override
    public int getCount() {
        if (imgs != null && imgs.size() == 3) {
            return imgs.size() + 1;
        } else {
            CreditLifeBorrowFragment.canJump = false;
            CreditLifeBorrowFragment.canLeft = true;
            return CheckUtil.isEmpty(imgs) ? 1 : imgs.size();
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        if (position < (CheckUtil.isEmpty(imgs) ? 1 : imgs.size())) {
            ImageView imageView = new ImageView(mContext);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(lp);
            imageView.setPadding(28, 0, 32, 0);
            if (CheckUtil.isEmpty(imgs)) {
                imageView.setImageResource(R.drawable.bg_credit_default);
            } else {
                GlideUtils.loadCenterCrop(mContext, imageView, ApiUrl.urlAdPic + imgs.get(position), 0, 0);
            }
            imageView.setOnClickListener(view -> {
                if (CheckUtil.isEmpty(imgs) || CheckUtil.isEmpty(applyRecordeBean)) {
                    return;
                }
                CreditLifeBorrowBean bean = applyRecordeBean.get(position);
                CreditLifeHelp creditLifeHelp = new CreditLifeHelp(mContext, bean);
                creditLifeHelp.getUniteLoginInfo();
            });
            container.addView(imageView);
            return imageView;
        } else {
            View hintView = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.item_top_viewpager, container, false);
            arrowImage = hintView.findViewById(R.id.iv_hint);
            slideText = hintView.findViewById(R.id.tv_hint);
            container.addView(hintView);
            return hintView;
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
