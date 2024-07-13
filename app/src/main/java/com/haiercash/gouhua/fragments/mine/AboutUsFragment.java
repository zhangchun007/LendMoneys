package com.haiercash.gouhua.fragments.mine;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.login.VersionInfo;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.uihelper.CallPhoneNumberHelper;
import com.haiercash.gouhua.uihelper.VersionHelper;
import com.haiercash.gouhua.utils.UiUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 关于我们
 * Created by Administrator on 2017/6/27.
 */
@Route(path = PagePath.FRAGMENT_ABOUT_US)
public class AboutUsFragment extends BaseFragment {
    public static final int ID = AboutUsFragment.class.hashCode();
    private VersionHelper mVersionHelper;

    @BindView(R.id.tv_version)
    TextView tv_version;
    @BindView(R.id.iv_version_update)
    ImageView mUpdate;

    @Override
    protected int getLayoutId() {
        return R.layout.a_aboutus;
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle("关于我们");
        VersionHelper.HAS_SHOW_UPDATE = false;
        mVersionHelper = new VersionHelper(mActivity, tv_version, true, onVersionBackListener);
        tv_version.setText(UiUtil.getStr("版本号 V", SystemUtils.getAppVersion(getActivity())));
        if (!VersionHelper.isLastVersion(mActivity)) {
            mUpdate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    @OnClick({R.id.rl_consumer_hotline, R.id.rl_check_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_consumer_hotline:
                CallPhoneNumberHelper.callServiceNumber(mActivity, getString(R.string.about_us_phone_number),
                        "呼叫", "取消");
                break;
            case R.id.rl_check_update:
                mVersionHelper.startCheckVersionService();
                break;
            default:
                break;
        }
    }

    private VersionHelper.OnVersionBackListener onVersionBackListener = new VersionHelper.OnVersionBackListener() {
        @Override
        public void onVersionBack(int status, Object response) {
            showProgress(false);
            switch (status) {
                case VersionHelper.VERSION_SUCCEED_CANCLE:
                    mUpdate.setVisibility(View.VISIBLE);
                    break;
                case VersionHelper.VERSION_SUCCEED_NOUPDATE:
                    UiUtil.toast("当前已是最新版本");
                    mUpdate.setVisibility(View.GONE);
                    break;
                case VersionHelper.VERSION_CHECKED_SUCC:
                    mVersionHelper.showUpdateDialog((VersionInfo) response);
                    break;
                case VersionHelper.VERSION_CHECKED_ERROR:
                case VersionHelper.VERSION_CHECKED_ERROR_NO_BLOCK:
                    onError((BasicResponse) response, null);
                    break;
                case VersionHelper.VERSION_SUCCEED_UPDATE:
                    UiUtil.toastLongTime("正在下载新版本,请稍候");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        if (mVersionHelper != null) {
            mVersionHelper.clearnHelper();
            mVersionHelper = null;
        }
        super.onDestroyView();
    }
}
