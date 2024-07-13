package com.haiercash.gouhua.fragments.main;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;

/**
 * Author: Sun<br/>
 * Date :    2018/11/27<br/>
 * FileName: HomeCreditCardFragment<br/>
 * Description: 首页信用卡<br/>
 */
public class HomeCreditCardFragment extends HomeLeaguerFragment {

    public static HomeCreditCardFragment newInstance(Bundle extra) {
        final HomeCreditCardFragment f = new HomeCreditCardFragment();
        if (extra != null) {
            f.setArguments(extra);
        }
        return f;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUrl = ApiUrl.API_SERVER_URL + ApiUrl.URL_CREDITCARD;
        //url = ApiUrl.URL_CREDITCARD;
    }

    @Override
    protected void initEventAndData() {
        viewPageType = VIEW_VERTICAL;
        super.initEventAndData();
        getBinding().llTitle.setVisibility(View.GONE);
        setStatusBarTextColor(true);
    }
}
