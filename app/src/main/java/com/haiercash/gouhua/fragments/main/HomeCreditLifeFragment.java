package com.haiercash.gouhua.fragments.main;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.app.haiercash.base.bui.TitleBarView;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.log.Logger;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.fragments.CreditLifeBorrowFragment;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnCheckedChanged;

/**
 * Author: Sun<br/>
 * Date :    2019/3/1<br/>
 * FileName: HomeCreditLifeFragment<br/>
 * Description:首页--信用生活<br/>
 */
public class HomeCreditLifeFragment extends BaseFragment {
    @BindView(R.id.bar_header)
    TitleBarView mBarView;
    @BindView(R.id.ll_group)
    LinearLayout ll_group;
    @BindView(R.id.rb_type1)
    RadioButton rb_type1;
    @BindView(R.id.rb_type2)
    RadioButton rb_type2;
    @BindView(R.id.ll_root)
    LinearLayout llRoot;
    /**
     * 贷超白名单开关
     */
    private boolean isWhite = false;
    /**
     * 借钱开关
     */
    private boolean isLoan = false;
    /**
     * 信用卡开关
     */
    private boolean isCreditCard = false;

    private final int STATUS_ONLY_LOAN = 0x01;

    private final int STATUS_ONLY_CREDIT = 0x02;

    private final int STATUS_ALL = 0x03;

    private int mCurrentStatus;


    private List<BaseFragment> fragmentMap = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_list;
    }

    @Override
    protected void initEventAndData() {
        mBarView.setLeftDisable(false);
        fragmentMap.add(CreditLifeBorrowFragment.newInstance(null));
        fragmentMap.add(HomeCreditCardFragment.newInstance(null));
        initData();
        initView();
    }

    /**
     * 初始化页面相关数据
     */
    private void initData() {
        isWhite = "true".equals(SpHp.getLogin(SpKey.IS_IN_WHITE_LIST));
        isCreditCard = "Y".equals(SpHp.getOther(SpKey.CREDIT_CARD_SWITCH));
        isLoan = "Y".equals(SpHp.getLogin(SpKey.LOAN_MARKET_EDU_STATUS));
    }

    @Override
    public void onResume() {
        super.onResume();
        UMengUtil.pageStart("CreditLifePage");
        //4.0新增，测试压力大暂时去除，后期需要添加只需要去掉注释就行
       /* if (!mActivity.isShowingDialog()) {
            controlDialogUtil.checkDialog("cre_life");
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        UMengUtil.pageEnd("CreditLifePage");
    }

    private void initView() {
        llRoot.setPadding(0, 0, 0, 0);
        if (isCreditCard && (isWhite || isLoan)) {
            mBarView.setVisibility(View.GONE);
            llRoot.setPadding(0, UiUtil.dip2px(mActivity, 24), 0, 0);
            rb_type1.setText("借钱");
            rb_type2.setText("信用卡");
            ll_group.setVisibility(View.VISIBLE);
            mCurrentStatus = STATUS_ALL;
            initFragment(0);
        } else if (isLoan || isWhite) {
            mBarView.setVisibility(View.VISIBLE);
            mBarView.setTitle("借钱");
            ll_group.setVisibility(View.GONE);
            mCurrentStatus = STATUS_ONLY_LOAN;
            initFragment(0);
        } else {
            mBarView.setVisibility(View.VISIBLE);
            mBarView.setTitle("信用卡");
            ll_group.setVisibility(View.GONE);
            mCurrentStatus = STATUS_ONLY_CREDIT;
            initFragment(1);
        }
    }


    /**
     * 获取当前页面状态是否发生改变
     */
    private boolean currentStatusChanged() {
        isWhite = "true".equals(SpHp.getLogin(SpKey.IS_IN_WHITE_LIST));
        isCreditCard = "Y".equals(SpHp.getOther(SpKey.CREDIT_CARD_SWITCH));
        isLoan = "Y".equals(SpHp.getLogin(SpKey.LOAN_MARKET_EDU_STATUS));
        if (isCreditCard && (isWhite || isLoan)) {
            return mCurrentStatus == STATUS_ALL;
        } else if (isWhite || isLoan) {
            return mCurrentStatus == STATUS_ONLY_LOAN;
        } else {
            return mCurrentStatus == STATUS_ONLY_CREDIT;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (currentSupportFragment != null) {
            //让当子fragment跟父fragment同步状态
            currentSupportFragment.onHiddenChanged(hidden);
        }
        //如果页面显示且页面状态发生改变则重新加载页面
        if (!hidden && !currentStatusChanged()) {
            initView();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentSupportFragment != null) {
            //让当子fragment跟父fragment同步状态
            currentSupportFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @OnCheckedChanged({R.id.rb_type1, R.id.rb_type2})
    void checkChangListener(CompoundButton compoundButton, boolean b) {
        if (b) {
            if (compoundButton.getId() == R.id.rb_type1) {
                initFragment(0);//借钱
            } else if (compoundButton.getId() == R.id.rb_type2) {
                initFragment(1);//信用卡
            }
        }
    }

    /**
     * 加载fragment
     */
    private void initFragment(int position) {
        rb_type1.getPaint().setFakeBoldText(position == 0);
        rb_type2.getPaint().setFakeBoldText(position == 1);
        changeFragment(R.id.fl_content, fragmentMap.get(position));
    }

    @Override
    public void onSuccess(Object t, String url) {
        if (ApiUrl.URL_IS_IN_WHITE_LIST.equals(url)) {
            Logger.d("load success :" + ApiUrl.URL_IS_IN_WHITE_LIST);
            initData();
            initView();
        } else if (ApiUrl.URL_HOME_INFO.equals(url)) {
            //加载用户状态信息
            Logger.d("load success :" + ApiUrl.URL_HOME_INFO);
        } else if (ApiUrl.URL_CHANNEL_SET.equals(url)) {
            initData();
            initView();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        if (ApiUrl.URL_IS_IN_WHITE_LIST.equals(url)) {
            Logger.d("load success :" + ApiUrl.URL_IS_IN_WHITE_LIST);
            initData();
            initView();
        } else if (ApiUrl.URL_HOME_INFO.equals(url)) {
            Logger.d("load error :" + ApiUrl.URL_HOME_INFO);
        } else if (ApiUrl.URL_CHANNEL_SET.equals(url)) {
            initData();
            initView();
        }
    }

    @Override
    public void resetData() {
        for (BaseFragment fragment : fragmentMap) {
            fragment.resetData();
        }
    }
}
