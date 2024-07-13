package com.app.haiercash.base.utils.picker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.app.haiercash.base.R;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.BasePickerView;
import com.bigkoo.pickerview.view.TimePickerView;

import java.util.Calendar;
import java.util.Date;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/2/26<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class PickerSelectUtil {
    private Context context;
    private IPickerSelectCallBack callBack;
    private TimePickerView pvCustomTime;

    public PickerSelectUtil(Context context, IPickerSelectCallBack callBack) {
        this.context = context;
        this.callBack = callBack;
    }

    /**
     * @param selectedDate 格式：yyyy-MM-dd
     * @param startDate    格式：yyyy-MM-dd
     * @param endDate      格式：yyyy-MM-dd
     * @param isDialogType 弹框是否是dailog模式
     * @param container    容器
     */
    public void showTimeSelect(View v, String selectedDate, String startDate, String endDate, boolean isDialogType, ViewGroup container) {
        if (pvCustomTime == null) {
            initCustomTimePicker(selectedDate, startDate, endDate, isDialogType, container);
        }
        //pvCustomTime.getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //pvCustomTime.getDialog().getWindow().setGravity(Gravity.BOTTOM);
        pvCustomTime.show(v, isDialogType);
    }

    /*
     * @description
     *
     * 注意事项：
     * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
     * 具体可参考demo 里面的两个自定义layout布局。
     * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
     * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
     */
    private void initCustomTimePicker(String sltDate, String sDate, String eDate, final boolean isDialogType, ViewGroup container) {
        Calendar selectedDate, startDate, endDate;
        if (CheckUtil.isEmpty(sltDate)) {
            selectedDate = Calendar.getInstance();//系统当前时间
        } else {
            selectedDate = TimeUtil.stringToCalendar(sltDate, "yyyy-MM-dd");
        }
        if (CheckUtil.isEmpty(sDate)) {
            startDate = Calendar.getInstance();
            startDate.set(2000, 1, 1);
        } else {
            startDate = TimeUtil.stringToCalendar(sDate, "yyyy-MM-dd");
        }
        if (CheckUtil.isEmpty(eDate)) {
            endDate = Calendar.getInstance();
        } else {
            endDate = TimeUtil.stringToCalendar(eDate, "yyyy-MM-dd");
        }
        //时间选择器 ，自定义布局
        TimePickerBuilder timePickerBuilder = new TimePickerBuilder(context, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                //btn_CustomTime.setText(TimeUtil.dateToString(date));
                callBack.timeSelect(TimeUtil.dateToString(date, "yyyy-MM-dd"));
            }
        });
        timePickerBuilder.setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
            @Override
            public void onTimeSelectChanged(Date date) {
                Log.i("pvTime", "onTimeSelectChanged"+TimeUtil.dateToString(date, "yyyy-MM-dd"));
                if (!isDialogType && callBack != null) {
                    callBack.timeSelect(TimeUtil.dateToString(date, "yyyy-MM-dd"));
                }
            }
        })
                /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentTextSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
                .setDividerColor(Color.WHITE)//设置分割线的颜色
                .setTextColorCenter(Color.LTGRAY)//设置选中项的颜色
                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setTitleBgColor(Color.DKGRAY)//标题背景颜色 Night mode
                .setBgColor(Color.BLACK)//滚轮背景颜色 Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)*/
                /*.animGravity(Gravity.RIGHT)// default is center*/
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setContentTextSize(18)
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
//                .setLabel("", "", "", "", "", "")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 40, 0, -40)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .isAlphaGradient(true); //默认设置false ，内部实现将DecorView 作为它的父控件。

        //如果是弹框模式
        if (isDialogType) {
            timePickerBuilder.setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                @Override
                public void customLayout(View v) {
                    final TextView tvSubmit = v.findViewById(R.id.tv_finish);
                    View tvCancel = v.findViewById(R.id.tv_cancel);
                    tvSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pvCustomTime.returnData();
                            pvCustomTime.dismiss();
                        }
                    });
                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pvCustomTime.dismiss();
                        }
                    });
                }
            }).isDialog(true);

        } else {
            timePickerBuilder.setLayoutRes(R.layout.pickerview_custom_time2, new CustomListener() {
                @Override
                public void customLayout(View v) {

                }
            })
                    .setDecorView(container)
                    .setOutSideCancelable(false)
                    .isDialog(false)
                    .setLabel("", "", "", "", "", "")
                    .setLineSpacingMultiplier(2.7f)
                    .setDividerColor(Color.TRANSPARENT);
        }

        pvCustomTime = timePickerBuilder.build();
        if (!isDialogType) {
            pvCustomTime.setKeyBackCancelable(false);//系统返回键监听屏蔽掉
        }
        resetPositionDialog(pvCustomTime, 0);


    }

    /**
     * 设置时间
     *
     * @param sltDate
     */
    public void setCurrentData(String sltDate) {
        Calendar selectedDate;
        if (CheckUtil.isEmpty(sltDate)) {
            selectedDate = Calendar.getInstance();//系统当前时间
        } else {
            selectedDate = TimeUtil.stringToCalendar(sltDate, "yyyy-MM-dd");
        }

        if (pvCustomTime != null)
            pvCustomTime.setDate(selectedDate);

    }

    private void resetPositionDialog(BasePickerView pickerView, int leftRightMargin) {
        Dialog mDialog = pickerView.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = leftRightMargin;
            params.rightMargin = leftRightMargin;
            pickerView.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.3f);
            }
        }
    }
}
