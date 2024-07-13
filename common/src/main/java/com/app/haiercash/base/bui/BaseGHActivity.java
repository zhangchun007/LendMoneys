package com.app.haiercash.base.bui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewbinding.ViewBinding;

import com.app.haiercash.base.utils.log.Logger;
import com.trello.rxlifecycle3.components.support.RxFragmentActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/9
 * 描    述：
 * 修订历史：
 * ================================================================
 */
public abstract class BaseGHActivity extends RxFragmentActivity {
    protected FragmentActivity mContext;
    private Unbinder mUnBinder;
    protected Fragment currentSupportFragment;
    private List<Fragment> listFragments;
    private TitleBarView mBarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.e("BaseActivity-进入Activity", getClass().getSimpleName() + "\n" + getIntent());
        if (getLayout() != -1) {
            setContentView(getLayout());
        } else {
            ViewBinding binding = initBinding(LayoutInflater.from(this));
            if (binding != null) {
                setContentView(binding.getRoot());
            }
        }
        mUnBinder = ButterKnife.bind(this);
        mContext = this;
    }

    protected void initBarView(@IdRes int resId) {
        mBarView = findViewById(resId);
    }

    public abstract void showProgress(boolean flag, String msg);

    public abstract void showProgress(boolean flag);

    public abstract Dialog showDialog(String msg);

    /**
     * 获取布局文件Id
     */
    protected int getLayout() {
        return -1;
    }

    /**
     * 绑定的LayoutId
     */
    protected ViewBinding initBinding(LayoutInflater inflater) {
        return null;
    }

    /**
     * 执行onCreated
     */
    protected abstract void onViewCreated(@Nullable Bundle savedInstanceState);

    /**************************HEAD START****************************/

    public void setTitle(String title) {
        setTitle(title, null);
    }

    public void setTitle(@StringRes int title) {
        setTitle(title, null);
    }

    /**
     * 设置title的文字和颜色值
     */
    public void setTitle(CharSequence title, String color) {
        if (mBarView != null) {
            mBarView.setTitle(title, color);
        } else {
            super.setTitle(title);
        }
    }

    public void setTitle(@StringRes int title, String color) {
        setTitle(getString(title), color);
    }

    public TitleBarView getTitleBarView() {
        return mBarView;
    }

    public String getTitleName() {
        if (mBarView != null) {
            return String.valueOf(mBarView.getTitle());
        }
        return null;
    }

    public void setTitleVisibility(boolean visibility) {
        if (mBarView != null) {
            mBarView.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    public void setTitleBarBackgroundResource(@DrawableRes int backgroundResource) {
        if (mBarView != null) {
            mBarView.setBackgroundResource(backgroundResource);
        }
    }

    public void setLeftImageCloseVisibility(boolean visibility) {
        if (mBarView != null) {
            mBarView.setLeftClostDisable(visibility);
        }
    }

    public void setLeftIvVisibility(boolean visibility) {
        if (mBarView != null) {
            mBarView.setLeftDisable(visibility);
        }
    }

    public void setRightImageCloseVisibility(boolean visibility) {
        if (mBarView != null) {
            mBarView.setRightImageDisable(visibility);
        }
    }

    public void setBarLeftImage(int resourceId, View.OnClickListener listener) {
        if (mBarView != null) {
            mBarView.setLeftImage(resourceId, listener);
        }
    }

    public void setBarRightText(CharSequence sequence, int color, View.OnClickListener listener) {
        if (mBarView != null) {
            mBarView.setRightText(String.valueOf(sequence), listener);
            if (color != 0) {
                mBarView.setRightTextColor(color);
            }
        }
    }

    public void setBarRightTextAndLeftImg(CharSequence sequence, int color, int drawLeftImg, View.OnClickListener listener) {
        if (mBarView != null) {
            mBarView.setRightText(String.valueOf(sequence), listener);
            if (color != 0) {
                mBarView.setRightTextColor(color);
            }
            if (drawLeftImg != 0) {
                mBarView.setRightTextDrawLeft(drawLeftImg);
            }
        }
    }

    public void setRightImage(int resourceId, View.OnClickListener listener) {
        if (mBarView != null) {
            mBarView.setRightImage(resourceId, listener);
        }
    }

    public void setRightTextSize(float size) {
        if (mBarView != null) {
            mBarView.setRightTextSize(size);
        }
    }

    /**
     * 设置BarView的背景色
     */
    public void setBackColorBarView(int color) {
        mBarView.setTitleBackgroundColor(color);
    }

    /**
     * 用Fragment替换视图
     *
     * @param resView        将要被替换掉的视图
     * @param targetFragment 用来替换的Fragment
     */
    protected void changeFragment(int resView, Fragment targetFragment) {
        if (targetFragment.equals(currentSupportFragment)) {
            return;
        }
        if (listFragments == null) {
            listFragments = new ArrayList<>();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction.add(resView, targetFragment, targetFragment.getClass().getName());
        }
        try {
            for (Fragment fragment : listFragments) {
                transaction.hide(fragment);
            }
            if (!listFragments.contains(targetFragment)) {
                listFragments.add(targetFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (targetFragment.isHidden()) {
            transaction.show(targetFragment);
        }
        if (currentSupportFragment != null && currentSupportFragment.isVisible()) {
            transaction.hide(currentSupportFragment);
            transaction.setMaxLifecycle(currentSupportFragment, Lifecycle.State.STARTED);
//            transaction.setMaxLifecycle(currentSupportFragment, Lifecycle.State.STARTED);
        }
        currentSupportFragment = targetFragment;
        transaction.setMaxLifecycle(currentSupportFragment, Lifecycle.State.RESUMED);
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
        super.onDestroy();
    }
}
