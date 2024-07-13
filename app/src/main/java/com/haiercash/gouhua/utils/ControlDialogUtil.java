package com.haiercash.gouhua.utils;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseControlDialog;
import com.haiercash.gouhua.base.BaseNotifyDialog;
import com.haiercash.gouhua.base.BaseRaisDialog;
import com.haiercash.gouhua.beans.Coupon;
import com.haiercash.gouhua.beans.Credit;
import com.haiercash.gouhua.beans.PopDialogBean;
import com.haiercash.gouhua.beans.PopDialogNewBean;
import com.haiercash.gouhua.beans.risk.CouponBean;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.interfaces.LoginCallbackC;
import com.haiercash.gouhua.interfaces.PopCallBack;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.network.NetHelper;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * 整合弹窗+权益弹窗util
 */
public class ControlDialogUtil implements INetResult, PopCallBack {
    private final NetHelper netHelper;
    private final BaseActivity context;
    private BaseControlDialog controlDialog;
    private BaseRaisDialog dialog;
    private BaseNotifyDialog notifyDialog;
    private String mPage;//区分哪个页面:首页fpage，首页会员页member，我的页mine
    private String pageName, pageCode;
    private String actionPage;

    private final ClickCouponToUseUtil couponToUseUtil;
    private Observable<FragmentEvent> lifeObservable;
    /**
     * 首页营销弹窗计时器
     */
    private CountDownTimer homePopupCountDownTimer;
    private Disposable subscribe;//MainFragmentNew生命周期观察者

    public ControlDialogUtil(BaseActivity context) {
        this.context = context;
        netHelper = new NetHelper(context, this);
        couponToUseUtil = new ClickCouponToUseUtil(context);
    }

    public void setUmParam(String pageName, String pageCode) {
        this.pageName = pageName;
        this.pageCode = pageCode;
    }

    /**
     * 对应触发页面或者对应节点检查是否有弹窗
     * 5.0.1弹窗改版，升级为新的弹窗检测接口ApiUrl.POST_POP_INFO_NEW，非首页部分页面还是用旧的ApiUrl.POST_POP_INFO
     *
     * @param actionPage 触发页面，优先级高
     * @param actionNode 页面触发节点	进入:enter 退出:exit
     * @param node       触发节点
     */
    public void checkDialog(String actionPage, String actionNode, String node) {
        if (AppApplication.isLogIn()) {
            this.actionPage = actionPage;
            if (TextUtils.isEmpty(actionPage)) {
                mPage = node;
                Map<String, String> map = new HashMap<>();
                map.put("userId", RSAUtils.encryptByRSA(AppApplication.userid));
                map.put("stepNode", RSAUtils.encryptByRSA(node));
                netHelper.postService(ApiUrl.POST_POP_INFO, map, PopDialogBean.class);
            } else {
                getPopupInfo(actionPage, actionNode, node);
            }
        }
    }

    public void checkDialog(String node) {
        checkDialog(null, null, node);
    }

    /**
     * 首页弹窗检测
     */
    public void checkDialog() {
        checkDialog("home", "enter", "fpage");
    }

    /**
     * 获取营销弹窗
     *
     * @param actionPage 触发页面，优先级高
     * @param actionNode 页面触发节点	进入:enter 退出:exit
     * @param node       触发节点
     * @description 不限制登录状态, 单个页面、设备id和userId发生变化时发起请求,否则单个页面只请求一次
     */
    public void getPopupInfo(String actionPage, String actionNode, String node) {
        this.actionPage = actionPage;
        Map<String, Object> param = new HashMap<>();
        param.put("effectCarrier", "GH");//生效载体
        param.put("touchPage", actionPage);//触发页面
        //页面触发节点  进入:enter 退出:exit
        if (!TextUtils.isEmpty(actionNode)) {
            param.put("touchNode", actionNode);
        }
        String processId = SpHp.getLogin(SpKey.LOGIN_PROCESS_ID);
        if (!TextUtils.isEmpty(processId)) {
            param.put("processId", processId);
        }
        if ("home".equals(actionPage)) {
            //触发页面：首页
            mPage = node;
            Map<String, String> map = new HashMap<>();
            map.put("userId", RSAUtils.encryptByRSA(AppApplication.userid));
            map.put("stepNode", RSAUtils.encryptByRSA(node));
            //4.1.6新增入参：业务线
            map.put("biz", TokenHelper.getInstance().getSmyParameter("business"));
            //4.1.6新增入参：载体
            map.put("carrier", TokenHelper.getInstance().getSmyParameter("registerVector"));
            //4.1.6新增入参：页面(app首页：NATIVEINDEX)
            map.put("page", "NATIVEINDEX");
            //4.1.6新增入参：节点(进入页面: ENTER,退出页面: EXIT )
            map.put("node", "ENTER");
            param.put("oldPopupRequest", map);
            param.put("haierDeviceId", SystemUtils.getDeviceID(context));
        }
        netHelper.postService(ApiUrl.POST_POP_INFO_NEW, param, PopDialogNewBean.class);

    }

    public void setTimerSchedule(Observable<FragmentEvent> observable) {
        this.lifeObservable = observable;
    }

    public void setCountDownTimerStatus(boolean isLogin) {
        if (homePopupCountDownTimer != null && isLogin) {
            homePopupCountDownTimer.cancel();
            homePopupCountDownTimer = null;
        }
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        if (ApiUrl.POST_POP_INFO_NEW.equals(url)) {
            dialogResult(t);
        } else if (ApiUrl.POST_POP_INFO.equals(url)) {
            dialogResultOld(t);
        } else if (ApiUrl.POST_RECEIVE_COUPON.equals(url)) {
            context.showDialog("恭喜您!", "福利领取成功,您可在「我的-免息券」中查看", (dialog, which) -> dialog.dismiss());
        }
    }

    //弹窗检测结果处理
    public void dialogResult(Object t) {
        if (t instanceof PopDialogNewBean) {
            PopDialogNewBean popDialogNewBean = (PopDialogNewBean) t;
            if (popDialogNewBean.getOldPopupResp() != null && !CheckUtil.isEmpty(popDialogNewBean.getOldPopupResp().getNoticeId())) {
                //走旧的权益弹窗
                dialogResultOld(popDialogNewBean.getOldPopupResp());
            } else {
                //走新的整合弹窗
                showControlDialog(popDialogNewBean);
            }
        }
    }

    //弹窗检测结果处理---旧的弹窗逻辑
    private void dialogResultOld(Object t) {
        if (t instanceof PopDialogBean) {
            PopDialogBean bean = (PopDialogBean) t;
            if ("RAIS".equals(bean.getPopUpsType())) {
                showRaisDialog(bean);
            } else if ("NOTIFY".equals(bean.getPopUpsType()) || "FREE".equals(bean.getPopUpsType())) {
                showNotifyDialog(bean);
            }
        }
    }

    private void showControlDialog(PopDialogNewBean bean) {
        if (controlDialog != null) {
            if (controlDialog.isShowing()) {
                return;
            } else {
                controlDialog.setCallback(this);
            }
        } else {
            controlDialog = new BaseControlDialog(context, this);
        }
        controlDialog.setUmParam(pageName, pageCode, actionPage);
        if (context != null && !context.isShowingDialog()) {
            if (lifeObservable != null) {
                subscribe = lifeObservable.subscribe(fragmentEvent -> {
                    if (fragmentEvent == FragmentEvent.RESUME) {
                        if (homePopupCountDownTimer != null) {
                            homePopupCountDownTimer.start();
                        }
                    } else if (fragmentEvent == FragmentEvent.PAUSE) {
                        if (homePopupCountDownTimer != null) {
                            homePopupCountDownTimer.cancel();
                        }
                    } else if (fragmentEvent == FragmentEvent.DESTROY) {
                        if (homePopupCountDownTimer != null) {
                            homePopupCountDownTimer.cancel();
                            homePopupCountDownTimer = null;
                        }
                    }
                });
            }
            if (!CheckUtil.isZero(bean.getPopupDelayTime())) {
                int popupDelayTime;
                try {
                    popupDelayTime = Integer.parseInt(bean.getPopupDelayTime());
                } catch (Exception e) {
                    popupDelayTime = 1;
                }

                homePopupCountDownTimer = new CountDownTimer(popupDelayTime * 1000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        controlDialog.showDialog(bean);
                        if (homePopupCountDownTimer != null) {
                            homePopupCountDownTimer.cancel();
                            homePopupCountDownTimer = null;
                        }
                    }
                }.start();
            } else {
                controlDialog.showDialog(bean);
            }
        }
    }

    private void showNotifyDialog(PopDialogBean bean) {
        if (notifyDialog != null) {
            if (notifyDialog.isShowing()) {
                return;
            } else {
                notifyDialog.setCallback(this);
            }
        } else {
            notifyDialog = new BaseNotifyDialog(context, this);
        }
        if (context != null && !context.isShowingDialog()) {
            notifyDialog.showDialog(bean, mPage);
        }
    }

    private void showRaisDialog(PopDialogBean bean) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                return;
            } else {
                dialog.setCallback(this);
            }
        } else {
            dialog = new BaseRaisDialog(context, this);
        }
        if (context != null && !context.isShowingDialog()) {
            dialog.showDialog(bean, mPage);
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        //弹窗数据请求失败以及点击关闭弹窗调用接口失败均不作处理
        if (ApiUrl.POST_RECEIVE_COUPON.equals(url)) {
            UiUtil.toast("抱歉,活动太火爆,券被抢光了...");
        }
    }

    @Override
    public void clickNode(PopDialogBean popDialogBean, boolean isClose) {
        if (popDialogBean != null) {
            //旧弹窗的上报记录事件
            Map<String, String> map = new HashMap<>();
            map.put("userId", RSAUtils.encryptByRSA(AppApplication.userid));
            map.put("noticeId", RSAUtils.encryptByRSA(popDialogBean.getNoticeId()));
            netHelper.postService(ApiUrl.POST_POP_INFO_CALLBACK, map);

            //如果是免息券弹框
            if ("FREE".equals(popDialogBean.getPopUpsType()) && !isClose) {
                List<Coupon> couponList = popDialogBean.getCouponList();
                if (couponList != null && couponList.size() > 0) {
                    couponToUseUtil.clickCouponToUse(couponList.get(0).getCouponId());
                }
            }

        }
    }

    @Override
    public void postRecord(String popupId) {
        //弹窗的上报记录事件
        if (!CheckUtil.isEmpty(popupId)) {
            Map<String, String> map = new HashMap<>();
            map.put("popupId", popupId);
            map.put("haierDeviceId", SystemUtils.getDeviceID(context));
            netHelper.postService(ApiUrl.POST_POP_INFO_RECORD, map);
        }
    }

    @Override
    public void clickJump(String routeType, String jumpUrl, String popupId) {
        //处理跳转逻辑
        switch (UiUtil.getEmptyStr(routeType, "")) {
            case "goBackPage"://关闭弹窗，返回上一页，如果没有上一页则只关闭弹窗
                dismissControlDialog();
                if (ActivityUntil.getActivitySize() > 1) {
                    context.finish();
                }
                break;
            case "goHomePage"://关闭弹窗，到首页
                dismissControlDialog();
                MainHelper.backToMain();
                break;
            case "closeDialog"://关闭弹窗
                dismissControlDialog();
                break;
            case "getCoupon"://首页营销弹窗领券
                AppApplication.setLoginCallbackTodo(true, new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        getCoupon(popupId);
                        //能正常跳转，才关闭弹窗
                        dismissControlDialog();

                    }
                });
                break;
            case "loanPage"://支用页，后端返H5链接走default逻辑
            case "eduLoanPage"://申额，后端返H5链接走default逻辑
            case "repayBillPage"://七日待还，后端返H5链接走default逻辑
            default:
                AppApplication.setLoginCallbackTodo(true, new LoginCallbackC() {
                    @Override
                    public void onLoginSuccess() {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isHideCloseIcon",true);
                        if (WebHelper.startActivityForUrl(context, jumpUrl,bundle)) {
                            //能正常跳转，才关闭弹窗
                            dismissControlDialog();
                        }
                    }
                });

                break;
        }
    }

    private void getCoupon(String popupId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("popupId", popupId);
        netHelper.postService(ApiUrl.POST_RECEIVE_COUPON, map, CouponBean.class);
    }

    private void dismissControlDialog() {
        if (controlDialog != null && controlDialog.isShowing()) {
            controlDialog.dismiss();
        }
        if (subscribe != null) {
            subscribe.dispose();
        }
        if (homePopupCountDownTimer != null) {
            homePopupCountDownTimer.cancel();
            homePopupCountDownTimer = null;
        }
    }

    /**
     * 释放资源
     */
    public void onDestroy() {
        if (couponToUseUtil != null) {
            couponToUseUtil.onDestroy();
        }
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
        dismissControlDialog();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (notifyDialog != null && notifyDialog.isShowing()) {
            notifyDialog.dismiss();
        }
    }


    public void setPopupGInfo(Credit smartH5BeanCredit){
        if (controlDialog != null) {
            controlDialog.setPopupGInfo(smartH5BeanCredit);
        }
    }
}
