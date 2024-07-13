package com.haiercash.gouhua.unity;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.utils.UiUtil;


/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/26
 * @Version: 1.0
 */
public class MaxHeightScrollView extends ScrollView {
    private int maxHeight;

    public MaxHeightScrollView(Context context) {
        this(context, null);
    }

    public MaxHeightScrollView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public MaxHeightScrollView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attr, R.styleable.MaxHeightScrollView);
        maxHeight = typedArray.getDimensionPixelSize(R.styleable.MaxHeightScrollView_mhsv_max_height,
                (int) UiUtil.dip2px(context,285));
        typedArray.recycle();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        if (maxHeight > 0 && height > maxHeight) {
            setMeasuredDimension(width, maxHeight);
        }
    }
}
