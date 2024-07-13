package com.haiercash.gouhua.activity.comm;

import android.content.DialogInterface;
import android.content.Intent;

import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.activity.borrowmoney.GoBorrowMoneyActivity;
import com.haiercash.gouhua.activity.edu.ApplyWaiting;
import com.haiercash.gouhua.activity.edu.NameAuthIdCardPatchActivity;
import com.haiercash.gouhua.activity.edu.NameAuthStartActivity;
import com.haiercash.gouhua.activity.edu.PerfectInfoActivity;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.service.GhNetServer;
import com.haiercash.gouhua.tplibrary.livedetect.FaceRecognitionActivity;
import com.haiercash.gouhua.utils.GhLocation;
import com.haiercash.gouhua.utils.SpHp;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户loan状态帮助类
 */
public class UserLoanStatusHelper {
    /**
     * 请求用户额度状态
     * isBorrow区分是否跳转借款页面
     * 注意请求结束后activity.showProgress(false);
     */
    public static void requestUserLendStatus(BaseActivity activity, NetHelper netHelper,
                                             boolean isBorrow, DialogInterface.OnClickListener listener) {
        activity.showProgress(true);
        new GhLocation(activity, true, (isSuccess, reason) -> {
            activity.showProgress(false);
            if (isSuccess) {
                lendOrBorrow(activity, netHelper, isBorrow);
            } else {
                activity.showBtn2Dialog(reason, "知道了", (dialog, which) -> {
                    if (listener != null) {
                        listener.onClick(dialog, which);
                    }
                });
            }
        }).requestLocation();
    }

    /**
     * 额度申请或支用判断
     * 注意请求结束后activity.showProgress(false);
     */
    public static void lendOrBorrow(BaseActivity activity, NetHelper netHelper, boolean isBorrow) {
        GhNetServer.startGhNetServer(activity, GhNetServer.CHANNEL_SET, null);
        activity.showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("userId", SpHp.getLogin(SpKey.LOGIN_USERID));
        map.put("needApplyLoan", isBorrow ? "Y" : "N");//是否需要进行支用申请   Y 是 N 否  传Y 则增加支用校验
        if (isBorrow) {
            // 定位成功 则进行 录单校验
            String provinceCode = SpHelper.getInstance().readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_PROVINCECODE);//省代码
            String cityCode = SpHelper.getInstance().readMsgFromSp(SpKey.LOCATION, SpKey.LOCATION_CITYCODE);//市代码
            map.put("provinceCode", provinceCode);//省编码code
            map.put("cityCode", cityCode);//市编码code
            map.put("typGrp", "02");//贷款类别
        }
        netHelper.postService(ApiUrl.POST_USER_LEND_STATUS, map);
    }

    public interface Callback {
        void callback(boolean goEDJH);
    }

    /**
     * 处理ApiUrl.POST_USER_LEND_STATUS返回的数据map
     */
    public static void doForLoanStatus(BaseActivity activity, boolean isBorrow, Map<?, ?> map) {
        String status = null;
        if (map.containsKey("status")) {
            status = (String) map.get("status");
        }
        if (CheckUtil.isEmpty(status)) {
            activity.showDialog("获取用户状态失败，请稍后重试");
            return;
        }
        //00 有额度,结束流程/允许支用
        //以下状态为无额度
        //01 全部信息齐全，进申额咖啡页
        //02 未实名，走OCR实名
        //03 未进行人脸，走人脸
        //04 无交易密码，设置密码
        //05 个人资料不全，补资料
        //06 无身份证照片，补照片
        //以下为支用验证流程状态码
        //07 不允许支用
        switch (status) {
            case "00":
                if (isBorrow) {
                    if (map.containsKey("availAmt")) {
                        SpHp.saveUser(SpKey.USER_EDU_AVLIABLE, (String) map.get("availAmt"));
                    }
                    activity.startActivity(new Intent(activity, GoBorrowMoneyActivity.class));
                } else {
                    MainHelper.backToMainHome();
                }
                break;
            case "01":
                activity.startActivity(new Intent(activity, ApplyWaiting.class));
                break;
            case "02":
                activity.startActivity(new Intent(activity, NameAuthStartActivity.class));
                break;
            case "03":
            case "04":
                Intent intent04 = new Intent(activity, FaceRecognitionActivity.class);
                intent04.putExtra("tag", "EDJH");
                activity.startActivity(intent04);
                break;
            case "05":
                Intent intent05 = new Intent(activity, PerfectInfoActivity.class);
                intent05.putExtra("tag", "EDJH");
                activity.startActivity(intent05);
                break;
            case "06":
                Intent intent06 = new Intent(activity, NameAuthIdCardPatchActivity.class);
                intent06.putExtra("tag", "EDJH");
                activity.startActivity(intent06);
                break;
            case "09":
                MainHelper.backToMainHome();
                break;
            default:
                activity.showDialog("请回到首页试试");
                break;
        }
    }
}
