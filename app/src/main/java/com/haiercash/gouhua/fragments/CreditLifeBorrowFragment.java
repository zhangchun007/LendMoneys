package com.haiercash.gouhua.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.adaptor.BannerAdapter;
import com.haiercash.gouhua.adaptor.CardTransformer;
import com.haiercash.gouhua.adaptor.CreditLifeBorreowAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.base.BaseListFragment;
import com.haiercash.gouhua.beans.CreditLifeBorrowBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.uihelper.CreditLifeHelp;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: liuyaxun
 * Date :    2019/2/25
 * FileName: CreditLifeBorrowFragment
 * Description: 信用生活--借钱
 */
public class CreditLifeBorrowFragment extends BaseListFragment {
    @BindView(R.id.ll_yellow_expire)
    RelativeLayout llYellowExpire;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.ll_show_title)
    LinearLayout llShowTitle;
    @BindView(R.id.ll_vp_contain)
    LinearLayout llVpContain;
    public static boolean canJump = false;
    public static boolean canLeft = true;
    @BindView(R.id.iv_close_yellow)
    ImageView iv_close_yellow;
    @BindView(R.id.v_divder)
    View v_divder;
    @BindView(R.id.v_credit_line)
    View v_credit_line;
    private int currentPosition = 0;// 当前滑动到了哪一页
    private boolean isObjAnmatitor = true;
    private boolean isObjAnmatitor2 = false;
    public static final int ID = CreditLifeBorrowFragment.class.hashCode();
    private BannerAdapter mBannerAdapter;

    private boolean isNeedReload = false;
    private CreditLifeHelp creditLifeHelp;

    public static BaseFragment newInstance(Bundle extra) {
        final CreditLifeBorrowFragment f = new CreditLifeBorrowFragment();
        if (extra != null) {
            f.setArguments(extra);
        }
        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_credit_life_borrow;
    }

    @Override
    protected void initEventAndData() {
        setStatusBarTextColor(true);
        super.initEventAndData();
        setRecyclerViewNestedScroll();
        showProgress(true);
        mRefreshHelper.build(true, true);
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        return new CreditLifeBorreowAdapter();
    }

    @Override
    public void onItemClick(Object item) {
        CreditLifeBorrowBean borrowBean = (CreditLifeBorrowBean) item;
        //根据当前状态进行业务处理   检查当前item状态并进行相应处理
        creditLifeHelp = new CreditLifeHelp(mActivity, borrowBean);
        creditLifeHelp.dispatchUniteLogin();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && isNeedReload) {
            queryApplyRecord();
        }
    }

    private void queryApplyRecord() {
        String userId = SpHp.getLogin(SpKey.LOGIN_USERID);
        if (CheckUtil.isEmpty(userId)) {
            return;
        }
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("userId", EncryptUtil.simpleEncrypt(userId));
        map.put("channelType", "Y");
        netHelper.getService(ApiUrl.QUERY_APPL_RECOCED, map);
    }

    @Override
    public void loadSourceData(int page, int pageSize) {
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("channelCode", SpHp.getLogin(SpKey.LOGIN_REGISTCHANNEL));
        netHelper.postService(ApiUrl.QUERY_GRAY_ALL, map);
        queryApplyRecord();
    }

    @Override
    public void onSuccess(Object success, String url) {
        super.onSuccess(success, url);
        showProgress(false);
        if (url.equals(ApiUrl.QUERY_GRAY_ALL)) {
            List<CreditLifeBorrowBean> creditLifeBorrowBeanList = JsonUtils.fromJsonArray(success, CreditLifeBorrowBean.class);
            mRefreshHelper.updateData(creditLifeBorrowBeanList);
        } else if (url.equals(ApiUrl.QUERY_APPL_RECOCED)) {
            List<CreditLifeBorrowBean> applyRecordeBeanList = JsonUtils.fromJsonArray(success, "records", CreditLifeBorrowBean.class);
            //查询申请记录
            if (applyRecordeBeanList.size() > 0) {//已申请
                llShowTitle.setVisibility(View.VISIBLE);
                llVpContain.setVisibility(View.VISIBLE);
                llYellowExpire.setVisibility(View.GONE);
                v_credit_line.setVisibility(View.VISIBLE);
                v_divder.setVisibility(View.VISIBLE);
                //[2]已有申请记录，申请一条banner展示一个没有查看更多，申请超过三条展示三个banner并有查看更多，滑到查看更多时跳转到申请记录列表
                initBannerViewPager(applyRecordeBeanList);
            } else {//未申请
                //[1]新用户及未申请任何贷款产品的用户显示小黄条，已有申请记录隐藏小黄条显示banner并显示贷款大全字样
                llShowTitle.setVisibility(View.GONE);
                llVpContain.setVisibility(View.GONE);
                v_divder.setVisibility(View.GONE);
                v_credit_line.setVisibility(View.GONE);
                llYellowExpire.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.QUERY_GRAY_ALL.equals(url)) {
            mRefreshHelper.errorData();
        }
        super.onError(error, url);
    }

    private void initBannerViewPager(List<CreditLifeBorrowBean> applyRecordeBeanList) {
        mBannerAdapter = new BannerAdapter(mActivity, applyRecordeBeanList);
        mViewpager.setAdapter(mBannerAdapter);
        mViewpager.setPageMargin(35);//设置viewpage之间的间距
        mViewpager.setClipChildren(false);
        mViewpager.setOffscreenPageLimit(2);
        mViewpager.setPageTransformer(true, new CardTransformer());
        mViewpager.setCurrentItem(0);
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == (mBannerAdapter.getCount() - 2)) {
                    if (positionOffset > 0.10) {
                        canJump = true;
                        if (mBannerAdapter.arrowImage != null && mBannerAdapter.slideText != null) {
                            if (isObjAnmatitor) {
                                isObjAnmatitor = false;
                                ObjectAnimator animator = ObjectAnimator.ofFloat(mBannerAdapter.arrowImage, "rotation", 0f, 180f);
                                animator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mBannerAdapter.slideText.setText("释放查看");
                                        isObjAnmatitor2 = true;
                                    }
                                });
                                animator.setDuration(500).start();
                            }
                        }
                    } else if (positionOffset <= 0.10 && positionOffset > 0) {
                        canJump = false;
                        if (mBannerAdapter.arrowImage != null && mBannerAdapter.slideText != null) {
                            if (isObjAnmatitor2) {
                                isObjAnmatitor2 = false;
                                ObjectAnimator animator = ObjectAnimator.ofFloat(mBannerAdapter.arrowImage, "rotation", 180f, 360f);
                                animator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mBannerAdapter.slideText.setText("左滑查看");
                                        isObjAnmatitor = true;
                                    }
                                });
                                animator.setDuration(500).start();
                            }
                        }
                    }
                    canLeft = false;
                } else {
                    canLeft = true;
                }
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // 判断是否是划动状态且是最后一页
                if (currentPosition == (mBannerAdapter.getCount() - 2) && !canLeft) {
                    if (state == ViewPager.SCROLL_STATE_SETTLING) {
                        if (canJump) {
                            canJump = false;
                            ContainerActivity.to(getActivity(), ApplyRecordeFragment.ID);
                        }

                        new Handler().post(() -> {
                            // 在handler里调用setCurrentItem才有效
                            mViewpager.setCurrentItem(0);//mBannerAdapter.getCount()-2
                        });

                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        queryApplyRecord();
    }

    @OnClick({R.id.iv_close_yellow})
    public void onViewClick(View view) {
        if (view.getId() == R.id.iv_close_yellow) {
            llYellowExpire.setVisibility(View.GONE);
        }
    }

    @Override
    public void resetData() {
        isNeedReload = true;
    }

    @Override
    public void onDestroyView() {
        if (creditLifeHelp != null) {
            creditLifeHelp.destory();
        }
        super.onDestroyView();
    }
}
