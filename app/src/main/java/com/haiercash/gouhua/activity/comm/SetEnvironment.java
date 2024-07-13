package com.haiercash.gouhua.activity.comm;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.interfaces.SpKey;
import com.app.haiercash.base.utils.system.CheckUtil;

import butterknife.BindView;

/**
 * @Author: Sun
 * @Date :    2017/12/8
 * @FileName: SetEnvironment
 * @Description:
 */

public class SetEnvironment extends BaseFragment {

    public static final int ID = SetEnvironment.class.hashCode();
    @BindView(R.id.tv_url)
    TextView tv_url;
    @BindView(R.id.rg_environment)
    RadioGroup environment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_environment;
    }

    public static SetEnvironment newInstance(Bundle bd) {
        final SetEnvironment f = new SetEnvironment();
        if (bd != null) {
            f.setArguments(bd);
        }
        return f;
    }


    @Override
    protected void initEventAndData() {
        mActivity.setTitle("环境切换");
        String type = SpHelper.getInstance().readMsgFromSp(SpKey.LOCK, SpKey.AMBIENT_SPACE);
        if (CheckUtil.isEmpty(type)) {
            type = "3";
        }
        int space = Integer.parseInt(type);
        if (space > 3 || space < 1) {
            space = 1;
        }
        //设置已经选择的环境
        ((RadioButton) environment.getChildAt(space - 1)).setChecked(true);
        String str = "当前环境：type=" + type + "->" + ApiUrl.API_SERVER_URL + "\n\nPS：type = 1 -> 生产 | type = 2 -> 测试 | type = 3 -> 封测（非1、2、3时当生产环境处理）";
        tv_url.setText(str);
        environment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ActivityUntil.finishOthersActivity(mActivity.getClass());
                //切换环境
//                String choose = (String) environment.findViewById(checkedId).getTag();
//                SpHelper.getInstance().saveMsgToSp(SpKey.LOCK, SpKey.AMBIENT_SPACE, choose);
//                ApiUrl.setEnvironment();
//                startActivity(new Intent(mActivity, SplashActivity.class));
            }
        });
    }


}
