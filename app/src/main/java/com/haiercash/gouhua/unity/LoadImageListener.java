package com.haiercash.gouhua.unity;

import android.view.ViewGroup;

/**
 * @Description:
 * @Author: zhangchun
 * @CreateDate: 2023/11/13
 * @Version: 1.0
 */
public interface LoadImageListener {
    void onLoadSuccess(ViewGroup.LayoutParams layoutParams);

    void onLoadError(String url);
}
