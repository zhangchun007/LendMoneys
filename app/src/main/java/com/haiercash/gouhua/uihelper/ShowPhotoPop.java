package com.haiercash.gouhua.uihelper;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.viewpager.widget.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.ViewPageAdapter;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BasePopupWindow;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.utils.GlideUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/5/8<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ShowPhotoPop extends BasePopupWindow {
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    List<String> images = new ArrayList<>();

    public ShowPhotoPop(BaseActivity context, Object data) {
        super(context, data);
    }

    @Override
    protected int getLayout() {
        return R.layout.pop_show_photo;
    }

    @Override
    protected void onViewCreated(Object data) {
        int n = Color.parseColor("#000000");
        ColorDrawable dw = new ColorDrawable(n);
        this.setBackgroundDrawable(dw);
        setOutTouchClickDismiss();
    }

    /**
     * @param dataList 文件url（路径）
     * @param index    设置当前显示第几张图片
     */
    public void updateData(List<String> dataList, int index) {
        if (!CheckUtil.isEmpty(dataList)) {
            images.clear();
            images.addAll(dataList);
            InitViewPager();
            viewpager.setCurrentItem(index);
        }
    }

    /**
     * 初始化四个ViewPager
     */
    private void InitViewPager() {
        List<ImageView> mFragmentList = new ArrayList<>();
        // 设置全屏
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        for (String imagePath : images) {
            //动态添加引导页图片
            ImageView imageView = new ImageView(mActivity);
            imageView.setLayoutParams(params);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            GlideUtils.loadPicFormLocal(mActivity, imagePath, imageView, 0);
            mFragmentList.add(imageView);
        }
        viewpager.setAdapter(new ViewPageAdapter(mFragmentList));
    }

    @Override
    public void showAtLocation(View view) {
        showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}
