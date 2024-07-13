package com.app.haiercash.base.utils.picker;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.haiercash.base.R;
import com.app.haiercash.base.bean.ArrayBean;
import com.app.haiercash.base.bui.BaseGHPopupWindow;
import com.app.haiercash.base.db.DbUtils;
import com.app.haiercash.base.utils.system.FontCustom;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;

import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/3/19<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class PopSelectPick extends BaseGHPopupWindow implements OnOptionsSelectListener, OnOptionsSelectChangeListener {
    private List<ArrayBean>[] dataList;
    private OptionsPickerView pvOptions;
    private IPickerSelectCallBack callBack;
    private TextView tvTitle;
    //标题之下-picker之上
    private FrameLayout flSub;
    private boolean isNeedSelectChangeListener = false;
    private int opt1, opt2;

    public PopSelectPick(Context context, Object data, IPickerSelectCallBack callBack) {
        this(context, data, null, callBack);
    }

    public PopSelectPick(Context context, Object data, View subV, IPickerSelectCallBack callBack) {
        super(context, data);
        this.callBack = callBack;
        addSubView(subV, true);
    }

    @Override
    protected int getLayout() {
        return R.layout.pop_select_pick;
    }

    /**
     * R.id.fragmen_fragment填充View的Layout资源id,用于子类覆写扩展而差异化UI
     * 注意该layout中view的id必须有且类型相同，只能做差异化UI处理
     */
    protected int getFragmentLayout() {
        return R.layout.pickerview_custom_select;
    }

    @Override
    protected void onViewCreated(Object data) {
        FrameLayout mFrameLayout = mView.findViewById(R.id.fragmen_fragment);
        pvOptions = new OptionsPickerBuilder(mContext, this)
                .setLayoutRes(getFragmentLayout(), new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        tvTitle = v.findViewById(R.id.tv_title);
                        tvTitle.setTypeface(FontCustom.getMediumFont(mContext));
                        View iv_close = v.findViewById(R.id.iv_close);
                        iv_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.dismiss();
                                dismiss();
                            }
                        });
                        TextView tv_true = v.findViewById(R.id.tv_true);
                        tv_true.setTypeface(FontCustom.getMediumFont(mContext));
                        tv_true.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.returnData();
                                pvOptions.dismiss();
                                dismiss();
                            }
                        });
                        //标题之下-picker之上
                        flSub = v.findViewById(R.id.flSub);
                    }
                })
                .setDividerColor(Color.parseColor("#DDDDDD"))
                .setContentTextSize(20)
                .setOptionsSelectChangeListener(this)
                .setDecorView(mFrameLayout)//非dialog模式下,设置ViewGroup, pickerView将会添加到这个ViewGroup中
                .setOutSideColor(0x00000000)
                //.setOutSideCancelable(false)
                .build();
        pvOptions.setKeyBackCancelable(true);//系统返回键监听屏蔽掉
        pvOptions.setOnDismissListener(new com.bigkoo.pickerview.listener.OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                if (PopSelectPick.this.isShowing()) {
                    PopSelectPick.this.dismiss();
                }
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (pvOptions != null && pvOptions.isShowing()) {
                    pvOptions.dismiss();
                }
            }
        });
        setPopupOutsideTouchable(true);
    }

    /**
     * 添加view到标题之下和选择器之上
     *
     * @param subV          要添加的分view
     * @param needRemoveAll 是否需要移除之前添加的
     */
    public PopSelectPick addSubView(View subV, boolean needRemoveAll) {
        if (flSub != null) {
            if (needRemoveAll) {
                flSub.removeAllViews();
            }
            if (subV != null) {
                flSub.addView(subV);
            }
        }
        return this;
    }

    @Override
    public void showAtLocation(View view) {
        pvOptions.show(view, false);
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * @param title title 显示
     */
    @SafeVarargs
    public final void showSelect(View v, String title, List<ArrayBean>... data) {
        tvTitle.setText(title);
        resetDataList(data);
        showAtLocation(v);
    }

    /**
     * 显示三级联动的省市区
     */
    @SuppressWarnings("unchecked")
    public void showProvince(final View v, final String title) {
        if (dataList != null) {
            pvOptions.setNPicker(dataList[0], dataList[1], dataList[2]);
            showAtLocation(v);
            return;
        }
        isNeedSelectChangeListener = true;
        dataList = new List[3];
        getProvinceData();
        getCity(dataList[0].get(0));
        getAreas(dataList[1].get(0));
        tvTitle.setText(title);
        pvOptions.setNPicker(dataList[0], dataList[1], dataList[2]);
        showAtLocation(v);
    }

    public void setSelectProvince(int option1, int option2, int option3) {
        pvOptions.setSelectOptions(option1, option2, option3);
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    private final void resetDataList(List<ArrayBean>... data) {
        dataList = data;
        if (data.length == 1) {
            pvOptions.setPicker(data[0]);//一级选择器*/
        } else if (data.length == 2) {
            pvOptions.setPicker(data[0], data[1]);//二级三级联动选择器
        } else if (data.length == 3) {
            pvOptions.setPicker(data[0], data[1], data[2]);//三级联动选择器
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onOptionsSelectChanged(int options1, int options2, int options3) {
        if (isNeedSelectChangeListener) {
            if (options1 != opt1) {
                opt1 = options1;
                getCity(dataList[0].get(opt1));
                getAreas(dataList[1].get(0));
                pvOptions.setNPicker(dataList[0], dataList[1], dataList[2]);
                pvOptions.setSelectOptions(options1, 0, 0);
            } else if (options2 != opt2) {
                opt2 = options2;
                getAreas(dataList[1].get(opt2));
                pvOptions.setNPicker(dataList[0], dataList[1], dataList[2]);
                pvOptions.setSelectOptions(opt1, opt2, 0);
            }
        }
    }

    @Override
    public void onOptionsSelect(int options1, int options2, int options3, View v) {
        if (dataList.length == 1) {
            callBack.timeSelect(dataList[0].get(options1));
        } else if (dataList.length == 2) {
            callBack.timeSelect(dataList[0].get(options1), dataList[1].get(options2));
        } else if (dataList.length == 3) {
            callBack.timeSelect(dataList[0].get(options1), dataList[1].get(options2), dataList[2].get(options3));
        }
    }


    /*****/
    private void getProvinceData() {
        dataList[0] = DbUtils.getAddress().addressDao().getProvince();
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void getCity(ArrayBean provinceBean) {
        dataList[1] = DbUtils.getAddress().getCityByProvince(provinceBean.code);
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void getAreas(ArrayBean cityBean) {
        dataList[2] = DbUtils.getAddress().getAreaByCity(cityBean.code);
    }
}
