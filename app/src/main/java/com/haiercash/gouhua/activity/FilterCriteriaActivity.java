package com.haiercash.gouhua.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.picker.IPickerSelectCallBack;
import com.app.haiercash.base.utils.picker.PickerSelectUtil;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseDialog;
import com.haiercash.gouhua.databinding.ActivityFilterCriteriaBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @Description: 还款记录的筛选条件
 * @Author: zhangchun
 * @CreateDate: 9/19/22
 * @Version: 1.0
 */
public class FilterCriteriaActivity extends BaseActivity implements IPickerSelectCallBack {
    private ActivityFilterCriteriaBinding binding;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.rb_three_month)
    TextView rb1;
    @BindView(R.id.rb_six_month)
    TextView rb2;
    @BindView(R.id.rb_one_year)
    TextView rb3;
    @BindView(R.id.tv_certain)
    TextView tv_certain;


    @BindView(R.id.fl_container)
    FrameLayout flContainer;

    private boolean isStartTimeSelect = true;

    private PickerSelectUtil pickerSelectUtil;

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {

        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;//设置对话框置顶显示
        win.setAttributes(lp);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        tv_certain.setTypeface(FontCustom.getMediumFont(this));
        tvEndTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        pickerSelectUtil = new PickerSelectUtil(FilterCriteriaActivity.this, this);
        String[] startAndEnd = TimeUtil.parse(mContext, "近三月", true);
        String startTimeChecked = SpHelper.getInstance().readMsgFromSp(SpKey.FILTER_CRITERIA_KEY,SpKey.FILTER_CRITERIA_START_TIME,startAndEnd[0]);
        String endTimeChecked = SpHelper.getInstance().readMsgFromSp(SpKey.FILTER_CRITERIA_KEY,SpKey.FILTER_CRITERIA_END_TIME,TimeUtil.calendarToString2());
        tvStartTime.setText(startTimeChecked);

        String currentDate = TimeUtil.getNowDate(TimeUtil.YYYY_MM_DD);
        String startDate = TimeUtil.getBeforeYear(currentDate, 2);
        pickerSelectUtil.showTimeSelect(null, startTimeChecked, startDate, null, false, flContainer);

        tvEndTime.setText(endTimeChecked);
        if(TextUtils.equals(startTimeChecked,startAndEnd[0])&&TextUtils.equals(endTimeChecked,TimeUtil.calendarToString2())){
            rb1.setSelected(true);
        }else {
            tvStartTime.setSelected(true);
        }
    }

    @Override
    protected ActivityFilterCriteriaBinding initBinding(LayoutInflater inflater) {
        return binding = ActivityFilterCriteriaBinding.inflate(inflater);
    }

    @OnClick({R.id.tv_certain, R.id.layout_close, R.id.tv_start_time, R.id.tv_end_time, R.id.rb_three_month, R.id.rb_six_month, R.id.rb_one_year, R.id.layout_dialog_tips, R.id.ll_tips})
    public void onClick(View v) {
        if (v.getId() == R.id.tv_start_time) {
            setDealTimeNotSelect();
            isStartTimeSelect = true;
            tvStartTime.setSelected(true);
            tvEndTime.setSelected(false);

        } else if (v.getId() == R.id.tv_end_time) {
            setDealTimeNotSelect();
            isStartTimeSelect = false;
            tvStartTime.setSelected(false);
            tvEndTime.setSelected(true);

        } else if (v.getId() == R.id.layout_close) {
            saveCheckedTime();
            finish();
        } else if (v.getId() == R.id.tv_certain) {
            saveCheckedTime();
            submitFilter();
        } else if (v.getId() == R.id.rb_three_month) {
            String[] startAndEnd = TimeUtil.parse(mContext, "近三月", true);
            tvStartTime.setText(startAndEnd[0]);

            pickerSelectUtil.setCurrentData(startAndEnd[0]);
            rb1.setSelected(true);
            rb2.setSelected(false);
            rb3.setSelected(false);
            setCustomTimeNotSelect();
        } else if (v.getId() == R.id.rb_six_month) {
            String[] startAndEnd = TimeUtil.parse(mContext, "近半年", true);
            tvStartTime.setText(startAndEnd[0]);
            pickerSelectUtil.setCurrentData(startAndEnd[0]);
            rb1.setSelected(false);
            rb2.setSelected(true);
            rb3.setSelected(false);
            setCustomTimeNotSelect();
        } else if (v.getId() == R.id.rb_one_year) {
            String[] startAndEnd = TimeUtil.parse(mContext, "近一年", true);
            tvStartTime.setText(startAndEnd[0]);
            pickerSelectUtil.setCurrentData(startAndEnd[0]);
            rb1.setSelected(false);
            rb2.setSelected(false);
            rb3.setSelected(true);
            setCustomTimeNotSelect();
        } else if (v.getId() == R.id.layout_dialog_tips || v.getId() == R.id.ll_tips) {
            showErrorTimeDialog();
        }
    }

    private void saveCheckedTime(){
        String startTimeChecked = tvStartTime.getText().toString();
        String endTimeChecked = tvEndTime.getText().toString();
        SpHelper.getInstance().saveMsgToSp(SpKey.FILTER_CRITERIA_KEY,SpKey.FILTER_CRITERIA_START_TIME, startTimeChecked);
        SpHelper.getInstance().saveMsgToSp(SpKey.FILTER_CRITERIA_KEY,SpKey.FILTER_CRITERIA_END_TIME, endTimeChecked);
    }
    /**
     * 设置交易时间不选中
     */
    private void setDealTimeNotSelect() {
        rb1.setSelected(false);
        rb2.setSelected(false);
        rb3.setSelected(false);
    }

    public void setCustomTimeNotSelect() {
        tvStartTime.setSelected(false);
        tvEndTime.setSelected(false);
        tvEndTime.setText(TimeUtil.calendarToString2());
    }


    /**
     * 判断两个时间
     */
    private void submitFilter() {
        String startTime = tvStartTime.getText().toString() + " 00:00:00";
        String endTime = tvEndTime.getText().toString() + " 23:59:59";
        String dealSelectTips = "";
        if (rb1.isSelected()) {
            dealSelectTips = "近三月";
        }
        if (rb2.isSelected()) {
            dealSelectTips = "近半年";
        }
        if (rb3.isSelected()) {
            dealSelectTips = "近一年";
        }

        Date sDate = TimeUtil.getDateFromString(startTime);
        Date eDate = TimeUtil.getDateFromString(endTime);

        long diff = -1;
        if (sDate != null && eDate != null) {
            diff = eDate.getTime() - sDate.getTime();
        }
        //判断如果开始时间<结束时间
        if (diff < 0) {
            startTime = tvEndTime.getText().toString() + " 23:59:59";
            endTime = tvStartTime.getText().toString() + " 00:00:00";
        }
        if (CheckUtil.isEmpty(startTime) || CheckUtil.isEmpty(endTime)) {
            showErrorTimeDialog();
        } else {
            if (TimeUtil.compareDate(startTime, endTime, 365 - 1)) {
                Intent intent = new Intent();
                intent.putExtra("startTime", startTime);
                intent.putExtra("endTime", endTime);
                intent.putExtra("dealSelectTips", dealSelectTips);
                setResult(5, intent);
                finish();
            } else {
                showDialog("选择的时间跨度超过了一年，请调整范围后重试。");
            }
        }
    }

    @Override
    public void timeSelect(Object... time) {
        setDealTimeNotSelect();
        if (!tvStartTime.isSelected() && !tvEndTime.isSelected()) {
            tvStartTime.setSelected(true);
        }
        if (isStartTimeSelect) {
            tvStartTime.setText(String.valueOf(time[0]));
        } else {
            tvEndTime.setText(String.valueOf(time[0]));
        }
    }

    private void showErrorTimeDialog() {
        BaseDialog baseDialog = null;
        String phone = getString(R.string.about_us_phone_number);
        String allTips = getString(R.string.history_date_tips) + phone;
        SpannableString sp = new SpannableString(allTips);
        BaseDialog finalBaseDialog = baseDialog;
        sp.setSpan(new MyClickableSpan(UiUtil.getColor(R.color.colorPrimary), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + mContext.getString(R.string.about_us_phone_number)));
                        startActivity(intent);
                        if (finalBaseDialog != null) {
                            finalBaseDialog.dismiss();
                        }
                        finish();

                    }
                }),
                allTips.indexOf(phone), allTips.indexOf(phone) + phone.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        baseDialog = showDialog(sp).setMessageViewMovementMethod().setTitleLineVisible(false);
    }
}


