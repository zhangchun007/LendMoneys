package com.haiercash.gouhua.fragments;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.utils.picker.PickerSelectUtil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.UiUtil;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/2/28<br/>
 * 描    述：还款记录的筛选条件<br/>
 * 修订历史：<br/>
 * ================================================================
 */
@Route(path = PagePath.FRAGMENT_FILTER_CRITERIA)
public class FilterCriteriaFragment extends BaseFragment {
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.rb_three_month)
    RadioButton rb1;
    @BindView(R.id.rb_six_month)
    RadioButton rb2;
    @BindView(R.id.rb_one_year)
    RadioButton rb3;

    //private String startTime, endTime;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_filter_criteria;
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle("查询时间");
        tvStartTime.setText(TimeUtil.getDataBefore(0, 0, -89));
        tvEndTime.setText(TimeUtil.calendarToString2());
    }

    @OnCheckedChanged({R.id.rb_three_month, R.id.rb_six_month, R.id.rb_one_year})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView.getId() == R.id.rb_three_month) {
                tvStartTime.setText(TimeUtil.getDataBefore(0, 0, -89));//-90
            } else if (buttonView.getId() == R.id.rb_six_month) {
                tvStartTime.setText(TimeUtil.getDataBefore(0, 0, -179));//-180
            } else if (buttonView.getId() == R.id.rb_one_year) {
                tvStartTime.setText(TimeUtil.getDataBefore(0, 0, -364));//-365
            }
            tvEndTime.setText(TimeUtil.calendarToString2());
        }
    }

    @OnClick({R.id.btn_submit, R.id.tv_start_time, R.id.tv_end_time})
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            submitFilter();
        } else if (v.getId() == R.id.tv_start_time) {
            rb1.setChecked(false);
            rb2.setChecked(false);
            rb3.setChecked(false);
            new PickerSelectUtil(mActivity, time -> tvStartTime.setText(String.valueOf(time[0]))).showTimeSelect(v, null, "2017-01-01", null,true,null);
        } else if (v.getId() == R.id.tv_end_time) {
            rb1.setChecked(false);
            rb2.setChecked(false);
            rb3.setChecked(false);
            new PickerSelectUtil(mActivity, time -> tvEndTime.setText(String.valueOf(time[0]))).showTimeSelect(v, null, "2017-01-01", null,true,null);
        }
    }

    private void submitFilter() {
        String startTime = tvStartTime.getText().toString() + " 00:00:00";
        String endTime = tvEndTime.getText().toString() + " 23:59:59";
        if (CheckUtil.isEmpty(startTime) || CheckUtil.isEmpty(endTime)) {
            UiUtil.toast("请选择一个合适的时间范围");
        } else {
            if (TimeUtil.compareDate(startTime, endTime, 365 - 1)) {
                Intent intent = new Intent();
                intent.putExtra("startTime", startTime);
                intent.putExtra("endTime", endTime);
                mActivity.setResult(5, intent);
                mActivity.finish();
            } else {
                UiUtil.toast("请选择一个合适的时间范围");
            }
        }
    }
}
