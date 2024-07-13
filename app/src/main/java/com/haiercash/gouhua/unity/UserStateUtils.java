package com.haiercash.gouhua.unity;

import com.app.haiercash.base.net.config.CommonSpKey;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.utils.CommomUtils;

/**
 * @Description: 关于用户状态的判断
 * @Author: zhangchun
 * @CreateDate: 2023/11/30
 * @Version: 1.0
 */
public class UserStateUtils {
    /**
     * 用户是否登录
     *
     * @return
     */
    public static boolean isLogIn() {
        return AppApplication.isLogIn();
    }

    /**
     * 是否实名
     *
     * @return
     */
    public static boolean isAuthonName() {
        return CommomUtils.isRealName();
    }

    /**
     * 保存登录的token
     *
     * @param clientSecret
     */
    public static void saveClientSecret(String clientSecret) {
        TokenHelper.getInstance().saveClientSecret(clientSecret);
    }

    /**
     * 获取登录的token
     */
    public static String getClientSecret() {
        return TokenHelper.getInstance().getClientSecret();
    }


    public static void saveApplyState(String clientSecret) {
        SpHelper.getInstance().saveMsgToSp(SpKey.QUICK_APPLY_STATE, SpKey.QUICK_APPLY_TOKEN, clientSecret);
    }

    public static String getApplyState() {
        return SpHelper.getInstance().readMsgFromSp(SpKey.QUICK_APPLY_STATE, SpKey.QUICK_APPLY_TOKEN);
    }
}
