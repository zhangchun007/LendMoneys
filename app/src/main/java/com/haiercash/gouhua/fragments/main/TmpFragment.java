package com.haiercash.gouhua.fragments.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.config.NetConfig;
import com.app.haiercash.base.net.retrofit.RetrofitFactory;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.borrowmoney.GoBorrowMoneyActivity;
import com.haiercash.gouhua.activity.comm.ContainerActivity;
import com.haiercash.gouhua.activity.edu.ApplyWaiting;
import com.haiercash.gouhua.activity.edu.PerfectInfoActivity;
import com.haiercash.gouhua.activity.edu.PersonalCreditContractActivity;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.bill.AllBillsFragment;
import com.haiercash.gouhua.bill.PeriodBillsFragment;
import com.haiercash.gouhua.databinding.FgmTmpBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.wxapi.WxUntil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

@Route(path = PagePath.FRAGMENT_TMP)
public class TmpFragment extends BaseFragment {
    private FgmTmpBinding getBinding() {
        return (FgmTmpBinding) _binding;
    }

    public static String filePrefix;

    @Override
    protected FgmTmpBinding initBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FgmTmpBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initEventAndData() {
        mActivity.setTitle("测试工具");
        RxBus.getInstance().addSubscription(this, RxBus.getInstance().register(ActionEvent.class, (actionEvent -> {
            if (actionEvent.getActionType() == ActionEvent.RED_BAG_WX_AUTH) {
                showDialog("提示", "微信code： " + actionEvent.getActionMsg(), "取消", "复制", (dialog, which) -> {
                    if (which == 2) {
                        UiUtil.copyValueToShearPlate(mActivity, actionEvent.getActionMsg());
                    }
                });
            }
        })));
        getBinding().cbLogSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                String lockPage = getBinding().etPage.getText().toString().trim();
                String event = getBinding().etEvent.getText().toString().trim();
                UMengUtil.LOG_PAGE = CheckUtil.isEmpty(lockPage) ? UMengUtil.LOG_PAGE : lockPage;
                UMengUtil.LOG_CLICK = CheckUtil.isEmpty(event) ? UMengUtil.LOG_CLICK : event;

                Logger.isNewDebugDir = isChecked;
                if (isChecked) {
                    filePrefix = getBinding().etPrefix.getText().toString().trim();
                    filePrefix = CheckUtil.isEmpty(filePrefix) ? "gh_" : "gh_" + filePrefix;
                    Logger.initNewLogUntil(mActivity, filePrefix);//上线模式关闭Log日志
                    File file = new File(Logger.getLoggerFilePath());
                    if (file.isDirectory() && file.listFiles() != null && file.listFiles().length > 0) {
                        for (File f : file.listFiles()) {
                            System.out.println("删除文件：" + f.delete());
                        }
                    }
                    //toastDebug("delete：" + new File(Logger.FILE_PATH).delete());
                    showDialog("日志文件保存变更，日志文件将保存于：" + Logger.getLoggerFilePath() + "\n此时起，后续操作将保存到新的文件，完成后再次点击上传，方可上传日志文件");
                } else {
                    Logger.initLogUntil(mActivity, true);//上线模式关闭Log日志
                }
                //getBinding().tvConfirm.setEnabled(Logger.isNewDebugDir);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("程序发送崩溃，异常了开始输出异常信息");
                Logger.e(Log.getStackTraceString(e));
                Logger.e("---------------------------------------------------------------------------------");
                showDialog("程序发送崩溃:" + e.getMessage());
            }
        });
        getBinding().tvConfirm.setOnClickListener(this);
        //getBinding().tvConfirm.setEnabled(Logger.isNewDebugDir);
        getBinding().etPrefix.setText(filePrefix);
        setonClickByViewId(R.id.tvWebView, R.id.tvCoupon, R.id.tvGoBorrow, R.id.tvAny,
                R.id.tvGoEdAgreement, R.id.tvGoEdProgress, R.id.tvGoPerfectInfo,
                R.id.tvGoSevenBill, R.id.tvGoAllBill, R.id.tvShowDevice64Or32, R.id.tvShowUserId, R.id.tvCode);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvConfirm) {
            showProgress(true);
            Map<String, String> filePaths = new IdentityHashMap<>();
            filePaths.put("file", Logger.getLoggerFilePath() + (filePrefix == null ? "util" : filePrefix) + "-" + TimeUtil.calendarToString(Calendar.getInstance(), "MM-dd") + ".txt");
            Map<String, String> map = new HashMap<>();
            String name = SpHp.getLogin(SpKey.LOGIN_USERID);
            map.put("appType", "gouhua-" + name);
            //map.put("content", "");
            RetrofitFactory.getInstance().upLoadFileOther("http://app-tools.haiercash.net/HaierTest/uploadAppLogFile", map, filePaths, new INetResult() {

                @Override
                public void onSuccess(Object rep, String url) {
                    showProgress(false);
                    filePrefix = null;
                    getBinding().etPrefix.setText("");
                    System.out.println("结果：" + rep.toString());
                    UiUtil.toastDeBug("结果：" + rep.toString());
                    getBinding().cbLogSwitch.setChecked(false);
                }

                @Override
                public void onError(BasicResponse error, String url) {
                    showProgress(false);
                    System.out.println("结果：" + JsonUtils.toJson(error.toString()));
                    UiUtil.toastDeBug("结果：" + JsonUtils.toJson(error.toString()));
                }

            });
        } else if (v.getId() == R.id.tvWebView) {
            Intent intent = new Intent();
            intent.setClass(mActivity, JsWebBaseActivity.class);
            String url = getBinding().etWebUrl.getText().toString().trim();
            if (CheckUtil.isEmpty(url)) {
                intent.putExtra("jumpKey", "https://standardpay-p2.haiercash.com/home?processId=P16118010704323919&channelNo=19&firstSpell=CSYCS");
            } else {
                intent.putExtra("jumpKey", url);
            }
            mActivity.startActivity(intent);
        } else if (v.getId() == R.id.tvCoupon) {
            ARouterUntil.getInstance(PagePath.ACTIVITY_COUPON_BAG).navigation();
        } else if (v.getId() == R.id.tvGoBorrow) {
            mActivity.startActivity(new Intent(mActivity, GoBorrowMoneyActivity.class));
            //System.out.println("开始时间：" + TimeUtil.calendarToString(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
            //List<Object> list = RiskKfaUtils.getAppListOld(mActivity, "支用", "dfafdsfd");
            //System.out.println("list大小：" + list.size());
            //System.out.println("结束时间：" + TimeUtil.calendarToString(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss SSS"));
        } else if (v.getId() == R.id.tvGoEdAgreement) {
            mActivity.startActivity(new Intent(mActivity, PersonalCreditContractActivity.class));
        } else if (v.getId() == R.id.tvGoEdProgress) {
            Intent intent = new Intent(mActivity, ApplyWaiting.class);
            startActivity(intent);
        } else if (v.getId() == R.id.tvGoPerfectInfo) {
            Intent intent = new Intent(mActivity, PerfectInfoActivity.class);
            intent.putExtra("tag", "EDJH");
            startActivity(intent);
        } else if (v.getId() == R.id.tvGoSevenBill) {
            ContainerActivity.to(mActivity, PeriodBillsFragment.ID);
        } else if (v.getId() == R.id.tvGoAllBill) {
            ContainerActivity.to(mActivity, AllBillsFragment.ID);
        } else if (v.getId() == R.id.tvShowDevice64Or32) {
            //查看当前设备是否支持64位，查看当前APP进程是64位还是32位
            boolean isSupport64bit = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                isSupport64bit = Build.SUPPORTED_64_BIT_ABIS.length > 0;
            } else {
                try {
                    BufferedReader localBufferedReader =
                            new BufferedReader(new FileReader("/proc/cpuinfo"));
                    if (localBufferedReader.readLine().contains("aarch64")) {
                        isSupport64bit = true;
                    }
                    localBufferedReader.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            //查看当前APP进程是64位还是32位
            boolean is64;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                is64 = android.os.Process.is64Bit();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String arch = System.getProperty("os.arch");
                is64 = arch != null && arch.contains("64");
            } else {
                is64 = false;
            }
            showDialog(UiUtil.getStr("当前设备", isSupport64bit ? "支持" : "不支持", "64位", "\n当前APP进程是", is64 ? "64" : "32", "位"));
        } else if (v.getId() == R.id.tvShowUserId) {
            showDialog(UiUtil.getStr("deviceId：" + SystemUtils.getDeviceID(mActivity), "\nuserId：" + AppApplication.userid, "\nAPP-X0/X1/X2：" + NetConfig.TD_BUSINESS_SMY, "\n用户-X0/X1/X2：" + (AppApplication.isLogIn() ? TokenHelper.getInstance().getSmyParameter("business") : "未登录")));
        } else if (v.getId() == R.id.tvAny) {
            TokenHelper tokenHelper=new TokenHelper();
            tokenHelper.startTokenRequest();
//            InterestFreeBean bean = new InterestFreeBean();
//            List<InterestFreeBean.RepayCouponsBean> list = new ArrayList<>();
//            for (int i = 0; i < 10; i++) {
//                InterestFreeBean.RepayCouponsBean bean1 = new InterestFreeBean.RepayCouponsBean();
//                bean1.setCouponNo("01021442-" + i);
//                bean1.setParValue("100-" + i);
//                bean1.setUseEndDt("2021-12-12 12:10:0" + i);
//                bean1.setKind(i + "");
//                bean1.setState("UNUSE");
//                bean1.setDescribe1("setDescribe1-ddddd");
//                bean1.setDescribe2("setDescribe2--ddddd");
//                bean1.setDescribe3("setDescribe3--ddddd");
//                bean1.setBatchDesc("setBatchDesc--ddddd");
//                bean1.setBatchDetailDesc("batchDetailDesc--ddddd");
//                list.add(bean1);
//            }
//            bean.setCoupons(list);
//            //跳转免息券选择列表
//            Intent intent = new Intent(mActivity, InterestFreeChooseListActivity.class);
//            intent.putExtra("loanNum", "loanNum");
//            intent.putExtra("choosedCouponNo", "couponNo");
//            intent.putExtra("chooseList", bean);
//            startActivityForResult(intent, 1);

//            PerfectInfoDicHelper.getInstance().requestDics(new INetResult() {
//                @Override
//                public void onSuccess(Object response, String flag) {
//                    showProgress(false);
//
//                }
//
//                @Override
//                public void onError(BasicResponse error, String url) {
//                    showProgress(false);
//                    showDialog(error.getHead().getRetMsg());
//                }
//            });

//            Intent intent = new Intent(mActivity, SetTransactionPwdActivity.class);
//            intent.putExtra("tag", "EDJH");
//            mActivity.startActivity(intent);

//            Intent intent = new Intent(mActivity, FaceRecognitionActivity.class);
//            intent.putExtra("tag", "EDJH");
//            startActivity(intent);

//            HashMap<String, String> map = new HashMap<>();
//            map.put("couponDisValue", "50");
//            map.put("borrowBannerHasJump", "false");
//            Intent intent = new Intent();
//            intent.setClass(mActivity, JsWebPopActivity.class);
//            intent.putExtra("jumpKey", WebHelper.addUrlParam("http://www-p2.goudzi.com/native-membership", map));
//            startActivity(intent);
//            //从下往上弹出
//            //注意：overridePendingTransition一定要在startActivity 或者finish 之后调用，否则没有效果！而且可能会有各种其他问题！
//            mActivity.overridePendingTransition(R.anim.activity_up_in, 0);
//            SpHp.saveSpOther(SpKey.BORROW_BANNER_HAS_JUMP, "true");
        } else if (v.getId() == R.id.tvCode) {
            Bundle bundle = new Bundle();
            bundle.putString("fromRedBag", "Y");
            WxUntil.sendAuthForLogin(mActivity, bundle);
        }
    }

    @Override
    public void onDestroyView() {
        RxBus.getInstance().unSubscribe(this);
        super.onDestroyView();
    }
}
