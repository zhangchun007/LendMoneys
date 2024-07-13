package com.app.haiercash.base.utils.picker;

import android.content.Context;
import android.view.View;

import com.app.haiercash.base.R;

/**
 * 顶部左标题的picker
 */
public class PopLeftTitleSelectPick extends PopSelectPick {
    public PopLeftTitleSelectPick(Context context, Object data, IPickerSelectCallBack callBack) {
        this(context, data, null, callBack);
    }

    public PopLeftTitleSelectPick(Context context, Object data, View subV, IPickerSelectCallBack callBack) {
        super(context, data, subV, callBack);
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.pickerview_custom_select_left_title;
    }
}
