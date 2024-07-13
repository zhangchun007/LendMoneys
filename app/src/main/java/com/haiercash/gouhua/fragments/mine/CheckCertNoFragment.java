package com.haiercash.gouhua.fragments.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/8/29<br/>
 * 描    述：验证身份<br/>
 * 修订历史：mobileNo-》用户手机号；custNo-》实名信息编号<br/>
 * ================================================================
 */
public class CheckCertNoFragment extends BaseFragment {
    public static final String PAGE_KEY = "CheckCertNoPageKey";
    public static final String PAGE_CLOSE = "CheckCertNoPageClose";
    public static final String PAGE_COMPARE_NO = "CheckCertNoCompareCerNo";

    @BindView(R.id.name)
    EditText mEditTextName;
    @BindView(R.id.idcard)
    EditText mEditTextIdcard;
    @BindView(R.id.tvSetOK)
    TextView mTextViewSetOK;

    private String mobileNo;
    private String custNo;
    private Serializable pageClass;
    private boolean isNeedClose = true;
    private boolean isCompareCertNo = true;
    private Bundle mArguments;

    public static CheckCertNoFragment newInstance(Bundle bd) {
        final CheckCertNoFragment f = new CheckCertNoFragment();
        if (bd != null) {
            f.setArguments(bd);
        }
        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_check_certno;
    }

    @Override
    protected void initEventAndData() {
        SystemUtils.setWindowSecure(mActivity);
        mActivity.setTitle("验证身份");
        mArguments = getArguments();
        if (mArguments != null) {
            isNeedClose = mArguments.getBoolean(PAGE_CLOSE, true);
            pageClass = mArguments.getSerializable(PAGE_KEY);
            mobileNo = mArguments.getString("mobileNo");
            custNo = mArguments.getString("custNo");
            isCompareCertNo = mArguments.getBoolean(PAGE_COMPARE_NO, true);
        } else {
            UiUtil.toast("账号异常，请退出重试");
            mActivity.finish();
            return;
        }
        mArguments.remove(PAGE_KEY);//移除当前页面指定下一个页面的pageKey
        mTextViewSetOK.setTypeface(FontCustom.getMediumFont(mActivity));
        mTextViewSetOK.setEnabled(false);
    }

    public void submit() {
        String name = mEditTextName.getText().toString();
        String idcard = mEditTextIdcard.getText().toString();
        if (CheckUtil.isEmpty(name)) {
            UiUtil.toast("姓名不能为空");
            return;
        }
        if (CheckUtil.isEmpty(idcard)) {
            UiUtil.toast("身份证号不能为空");
            return;
        }
        String custName = SpHp.getUser(SpKey.USER_CUSTNAME);
        String certNo = SpHp.getUser(SpKey.USER_CERTNO);
        if (!CheckUtil.isEmpty(certNo) && isCompareCertNo) {//本地存在实名数据信息
            if (name.equals(custName) && idcard.equals(certNo)) {
                goRealPage(name, idcard);
            } else {
                UiUtil.toast("请输入正确的姓名和身份证信息");
                return;
            }
        } else {//本地不存在实名数据信息
            Bundle bundle = mArguments;//new Bundle();
            bundle.putString("mobileNo", mobileNo);
            bundle.putString("userCardName", name);
            bundle.putString("userIdCard", idcard);
            bundle.putString("custNo", custNo);
            ContainerActivity.to(mActivity, ((Class) pageClass).getSimpleName(), bundle);
        }
        if (isNeedClose) {
            mActivity.finish();
        }
    }

    /**
     * 实名时候跳转对应界面
     */
    private void goRealPage(String name, String idcard) {
        if (CheckUtil.checkSupperClass(pageClass, BaseActivity.class)) {
            //Intent intent = new Intent(mActivity, FaceCheckActivity.class);
            Intent intent = new Intent(mActivity, (Class<?>) pageClass);
            if (mArguments != null) {
                intent.putExtras(mArguments);
                intent.putExtra("activityTitle","验证身份");
                startActivity(intent);
            } else {
                UiUtil.toast("账号异常，请退出重试");
            }
        } else if (CheckUtil.checkSupperClass(pageClass, BaseFragment.class)) {
            Bundle bundle = mArguments;//new Bundle();
            bundle.putString("mobileNo", mobileNo);
            bundle.putString("userCardName", name);
            bundle.putString("userIdCard", idcard);
            bundle.putString("custNo", custNo);
            ContainerActivity.to(mActivity, ((Class) pageClass).getSimpleName(), bundle);
        }
    }

    @OnClick({R.id.tvSetOK})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.tvSetOK) {
            submit();
        }
    }

    @OnTextChanged(value = {R.id.name, R.id.idcard}, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged() {
        String name = mEditTextName.getText().toString();
        String idcard = mEditTextIdcard.getText().toString();
        mTextViewSetOK.setEnabled(!(CheckUtil.isEmpty(name) || CheckUtil.isEmpty(idcard)));
    }
}
