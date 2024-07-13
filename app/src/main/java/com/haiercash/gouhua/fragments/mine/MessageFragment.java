package com.haiercash.gouhua.fragments.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.CommonSpKey;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.TabPageAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.msg.UnReadMessageCount;
import com.haiercash.gouhua.fragments.FragmentController;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.NotificationsUtils;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * ================================================================
 * 作    者：yuelibiao
 * 邮    箱：yuelibiao@haiercash.com
 * 版    本：1.0
 * 创建日期：2017/7/6
 * 描    述：消息中心
 * 修订历史：1：通知 2：还款提醒 3：贷款进度 4：额度状态 5：优惠活动  6：我的交易
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_MESSAGE)
public class MessageFragment extends BaseFragment {
    @BindView(R.id.v_page)
    ViewPager vPage;
    @BindView(R.id.rb_1)
    RadioButton rb1;
    @BindView(R.id.rb_2)
    RadioButton rb2;
    @BindView(R.id.layout_open_notice)
    ConstraintLayout layout_open_notice;
    @BindView(R.id.tv_open_notice)
    TextView tvOpenNotice;

    @BindView(R.id.notice_num)
    AppCompatTextView notice_num;

    @BindView(R.id.activity_num)
    AppCompatTextView activity_num;

    //是否是一键已读
    private boolean isClearAll;

    private String currentToken = "";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initEventAndData() {
        setRightTextViewColorAndImg(false);
        mActivity.setTitle("消息中心");
        mActivity.setBarRightTextAndLeftImg("全部已读", Color.parseColor("#606166"), R.drawable.img_can_clear_message, null);
        mActivity.setRightTextSize(12f);

        ArrayList<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(FragmentController.obtainByName(MessageListFragment.class.getSimpleName(), getExtra("NOTICE")));
        fragmentList.add(FragmentController.obtainByName(MessageListFragment.class.getSimpleName(), getExtra("ACTIVITY")));

        TabPageAdapter myViewPageAdapter = new TabPageAdapter(getChildFragmentManager(), fragmentList);
        vPage.setAdapter(myViewPageAdapter);
        vPage.setOffscreenPageLimit(fragmentList.size());


        //设置默认选中的tab
        setTabTextSize(0);
        //获取未读消息数量
        getUnReadMessageNum();
    }

    /**
     * 消息已读
     *
     * @inmailId 站内信ID
     * @readAll 设置为全部已读 Y： 是  N：否  默认：N
     */
    public void makeMessageRead(String inmailId, String readAll) {
        if ("Y".equals(readAll)) {
            isClearAll = true;
            mActivity.showProgress(true);
        }
        Map<String, String> map = new HashMap<>();
        if (!CheckUtil.isEmpty(inmailId)) {
            map.put("inmailId", inmailId);
        }
        map.put("readAll", readAll);
        netHelper.postService(ApiUrl.MESSAGE_READ, map);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentToken = SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_TOKEN_CLIENT_SECRET);
        if (isShowMessageNotificationLayout() && !NotificationsUtils.isNotificationsEnabled(mActivity)) {
            layout_open_notice.setVisibility(View.VISIBLE);
        } else {
            layout_open_notice.setVisibility(View.GONE);
        }
    }

    /**
     * 是否展示消息通知弹框
     *
     * @return
     */
    private boolean isShowMessageNotificationLayout() {
        currentToken = SpHelper.getInstance().readMsgFromSp(CommonSpKey.TAG_COMMON, CommonSpKey.TAG_TOKEN_CLIENT_SECRET);
        String latestToken = SpHelper.getInstance().readMsgFromSp(SpKey.MESSAGE_NOTIFICATION_STATE, SpKey.MESSAGE_NOTIFICATION_TOKEN);
        if (!currentToken.equals(latestToken)) {
            return true;
        }
        return false;
    }


    /**
     * 未读消息数量
     */
    public void getUnReadMessageNum() {
        Map<String, String> map = new HashMap<>();
        netHelper.postService(ApiUrl.GET_UNREAD_MESSAGE_COUNT, map, UnReadMessageCount.class);
    }


    @Override
    public void onSuccess(Object response, String url) {
        if (ApiUrl.MESSAGE_READ.equals(url)) {//消息一键读取
            if (isClearAll) {
                mActivity.showProgress(false);
                UiUtil.toast("已全部标记为已读");
                if (vPage == null) return;
                RxBus.getInstance().post(new ActionEvent(ActionEvent.ONE_KEY_CLEAR_MESSAGE));
            }
            getUnReadMessageNum();
        } else if (ApiUrl.GET_UNREAD_MESSAGE_COUNT.equals(url)) {//未读消息数量
            UnReadMessageCount data = (UnReadMessageCount) response;
            if (data != null) {
                if (data.getTotal() > 0) {
                    setRightTextViewColorAndImg(true);
                } else {
                    setRightTextViewColorAndImg(false);
                }
                //通知消息数量
                if (data.getNoticeCnt() <= 0) {
                    notice_num.setVisibility(View.GONE);
                } else if (data.getNoticeCnt() < 10) {
                    notice_num.setVisibility(View.VISIBLE);
                    notice_num.setText(data.getNoticeCnt() + "");
                    notice_num.setBackgroundResource(R.drawable.shape_circle12_ff5151);
                } else if (data.getNoticeCnt() >= 10) {
                    notice_num.setVisibility(View.VISIBLE);
                    if (data.getTotal() < 100) {
                        notice_num.setText(data.getNoticeCnt() + "");
                    } else {
                        notice_num.setText("99+");
                    }
                    notice_num.setBackgroundResource(R.drawable.shape_messge_notice_num_bg);
                }
                //活动消息数量
                if (data.getActivityCnt() <= 0) {
                    activity_num.setVisibility(View.GONE);
                } else if (data.getActivityCnt() < 10) {
                    activity_num.setVisibility(View.VISIBLE);
                    activity_num.setText(data.getActivityCnt() + "");
                    activity_num.setBackgroundResource(R.drawable.shape_circle12_ff5151);
                } else if (data.getActivityCnt() >= 10) {
                    activity_num.setVisibility(View.VISIBLE);
                    if (data.getTotal() < 100) {
                        activity_num.setText(data.getActivityCnt() + "");
                    } else {
                        activity_num.setText("99+");
                    }
                    activity_num.setBackgroundResource(R.drawable.shape_messge_notice_num_bg);
                }
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        super.onError(error, url);
        if (ApiUrl.GET_UNREAD_MESSAGE_COUNT.equals(url)) {
            setRightTextViewColorAndImg(false);
        }
    }

    /**
     * 设置标题右边图标跟文字颜色
     *
     * @param canClear
     */
    public void setRightTextViewColorAndImg(boolean canClear) {
        mActivity.setBarRightTextAndLeftImg("全部已读", canClear ? Color.parseColor("#606166") : Color.parseColor("#AEAFB6"),
                canClear ? R.drawable.img_can_clear_message : R.drawable.img_clear_message, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (canClear) {
                            makeMessageRead("", "Y");
                        }
                    }
                });
    }

    private Bundle getExtra(String msgType) {
        Bundle extra = new Bundle();
        extra.putString("msgType", msgType);
        return extra;
    }

    @OnPageChange(value = R.id.v_page, callback = OnPageChange.Callback.PAGE_SELECTED)
    void onPageSelected(int position) {
        setCheckFalse();
        switch (position) {
            case 0:
                rb1.setChecked(true);
                setTabTextSize(0);
                break;
            case 1:
                rb2.setChecked(true);
                setTabTextSize(1);
                break;
        }
    }

    /**
     * 设置选中Tab字体大小跟加粗状态
     *
     * @param pos
     */
    private void setTabTextSize(int pos) {
        if (pos == 0) {
            rb1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            rb2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        } else if (pos == 1) {
            rb1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            rb2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }

    }

    @OnCheckedChanged({R.id.rb_1, R.id.rb_2})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = -1;
        switch (buttonView.getId()) {
            case R.id.rb_1:
                position = 0;
                break;
            case R.id.rb_2:
                position = 1;
                break;
        }
        if (position == -1 || position == vPage.getCurrentItem()) {
            return;
        }
        setCheckFalse();
        vPage.setCurrentItem(position);
        setTabTextSize(position);
    }

    @OnClick({R.id.tv_open_notice, R.id.img_message_notice_close})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_open_notice:
                //通知权限
                NotificationsUtils.toSetting(mActivity);
                break;
            case R.id.img_message_notice_close:

                SpHelper.getInstance().saveMsgToSp(SpKey.MESSAGE_NOTIFICATION_STATE, SpKey.MESSAGE_NOTIFICATION_TOKEN, currentToken);
                layout_open_notice.setVisibility(View.GONE);
                break;
        }
    }

    private void setCheckFalse() {
        rb1.setChecked(false);
        rb2.setChecked(false);
    }
}