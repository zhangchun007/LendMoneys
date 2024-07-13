package com.haiercash.gouhua.fragments.main;

import android.os.Handler;
import android.os.Looper;

import com.app.haiercash.base.utils.FileUtils;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.router.ARouterUntil;
import com.app.haiercash.base.utils.router.ActivityUntil;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.EduBean;
import com.haiercash.gouhua.beans.IconMain;
import com.haiercash.gouhua.beans.borrowmoney.LoanRatAndProduct;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.tplibrary.PagePath;
import com.haiercash.gouhua.utils.SpHp;

import java.io.File;
import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/9/19<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class MainHelper {

    /**
     * 保存当前用户业务状态
     */
    public static void saveMoneyState(String availLimit, String totalLimit) {
        SpHp.saveUser(SpKey.USER_EDU_ALL, CheckUtil.deletePointZero(totalLimit));
        SpHp.saveUser(SpKey.USER_EDU_AVLIABLE, CheckUtil.deletePointZero(availLimit));
    }

    /**
     * 过滤掉产品是随借随还的产品，够花不支持随借随还，因为够花是有固定期数的
     */
    public static String[] getCodeAndMin(List<LoanRatAndProduct> info) {
        String[] codeMin = new String[3];
        if (!CheckUtil.isEmpty(info)) {
            for (LoanRatAndProduct ratBean : info) {
                if (!"D".equals(ratBean.getTnrOpt())) {
                    codeMin[0] = ratBean.getProducts().get(0).getTypCde();
                    codeMin[1] = ratBean.getProducts().get(0).getMinAmt();
                    codeMin[2] = ratBean.getProducts().get(0).getTypLvlCde();
                    break;
                }
            }
        }
        return codeMin;
    }

    /* *******************************************MainHead***************************************************** */

    /**
     * Url 拦截跳转
     *
     * @param url 入参url有可能是包含gouhua://也有可能是一个真实的网址
     */
    public static boolean ImageLinkRoute(BaseActivity mActivity, String url) {
        return WebHelper.startActivityForUrl(mActivity, url);
    }

    public static final String strFormatDefault = "src_%s_default.jpg";
    public static final String strFormatSelect = "src_%s_select.jpg";

    /**
     * Icon保存路径
     */
    public static String getIconFilePath() {
        //boolean sdCardWritePermission = context.getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
        //if (!sdCardWritePermission) {
        //    Logger.e("没有sd卡读写权限-无法保存ICON");
        //    return null;
        //}
        //return Environment.getExternalStoragePublicDirectory(SAVE_DATA_PATH + ICON_FILE_PATH).getPath() + "/";
        return FileUtils.getExternalFilesDir() + "icon/";
    }

    /**
     * 保存首页四组icon
     */
    public static boolean saveHomeIcon(List<IconMain> list) {
        if (list.size() >= 4) {
            String filePath = getIconFilePath();
            if (CheckUtil.isEmpty(filePath)) {
                return false;
            }
            //FileUtils.deleteFile(new File(filePath));
            File dirFile = new File(filePath);
            if (dirFile.exists()) {
                if (!dirFile.delete()) {
                    Logger.e("MAIN_ICON", "ERROR:[" + filePath + "]路径的文件删除失败");
                    return false;
                }
            }

            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    Logger.e("MAIN_ICON", "ERROR:[" + filePath + "]路径的文件夹创建失败");
                    return false;
                }
            }
            for (int i = 0; i < list.size(); i++) {
                IconMain iconMain = list.get(i);
                if (!CheckUtil.isEmpty(iconMain.getSrcImgDefault())) {
                    Logger.e("MAIN_ICON", "SUCCESS第" + i + "个Default Icon保存成功");
                    EncryptUtil.saveImage(filePath + File.separator + String.format(strFormatDefault, i), iconMain.getSrcImgDefault());
                } else {
                    Logger.e("MAIN_ICON", "ERROR第" + i + "个Default Icon为空-无法保存ICON");
                    return false;
                }
                if (!CheckUtil.isEmpty(iconMain.getSrcImgSelect())) {
                    Logger.e("MAIN_ICON", "SUCCESS第" + i + "个Select Icon保存成功");
                    EncryptUtil.saveImage(filePath + File.separator + String.format(strFormatSelect, i), iconMain.getSrcImgSelect());
                } else {
                    Logger.e("MAIN_ICON", "ERROR第" + i + "个Select Icon为空-无法保存ICON");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 获取用户当前额度状态
     *
     * @param mEduBean 用户额度、借款等状态的数据源
     * @return Manager所配置的用户额度或借款等状态
     */
    public static String getCurrentUserStatue(EduBean mEduBean) {
        String userStatue = "eduStatusNoApply";
        if (mEduBean == null) {
            return userStatue;
        } else if ("01".equals(mEduBean.getUserState())) {
            if ("01".equals(mEduBean.getOutSts())) {//额度审核中
                userStatue = "eduStatusChecking";
            } else if ("25".equals(mEduBean.getOutSts()) || "03".equals(mEduBean.getOutSts())) {//额度申请被拒
                userStatue = "eduStatusApplyDenied";
            } else if ("26".equals(mEduBean.getOutSts())) {//额度申请取消
                userStatue = "eduStatusApplyCancel";
            } else if ("00".equals(mEduBean.getOutSts()) || "99".equals(mEduBean.getOutSts())) {//额度未申请
                userStatue = "eduStatusNoApply";
            } else if ("22".equals(mEduBean.getOutSts())) {//额度被退回
                userStatue = "eduStatusApplyReturned";
            }
        } else if ("02".equals(mEduBean.getUserState())) {//已有额度
            userStatue = "eduStatusExist";
        } else if ("03".equals(mEduBean.getUserState())) {//额度逾期&冻结
            if ("Y".equals(mEduBean.getOdStatus())) {
                userStatue = "eduStatusOverdue";
            } else {
                userStatue = "eduStatusFroze";
            }
        } else if ("05".equals(mEduBean.getUserState())) {//额度失效
            userStatue = "eduStatusInvalid";
        } else if ("04".equals(mEduBean.getUserState())) {
            if ("25".equals(mEduBean.getOutSts())) {//额度申请被拒
                userStatue = "eduStatusApplyDenied";
            } else {
                userStatue = "eduStatusNoApply";//额度未申请
            }
        }
        return userStatue;
    }

    /**
     * 回到首页并且展示初始默认tab页面
     */
    public static void backToMainHome() {
        MainActivity mainActivity = ActivityUntil.findActivity(MainActivity.class);
        if (mainActivity != null) {
            mainActivity.setFragment();
            ActivityUntil.finishOthersActivity(MainActivity.class);
        } else {
            ARouterUntil.getInstance(PagePath.ACTIVITY_MAIN).navigation();
            new Handler(Looper.getMainLooper()).postDelayed(() ->//延迟关闭，是因为防止首页还没完全启动起来而其他页都关掉了出现短暂黑屏
                    ActivityUntil.finishOthersActivity(MainActivity.class), 200);
        }
    }

    public static void backToMain() {
        MainActivity mainActivity = ActivityUntil.findActivity(MainActivity.class);
        if (mainActivity == null) {
            ARouterUntil.getInstance(PagePath.ACTIVITY_MAIN).navigation();
            new Handler(Looper.getMainLooper()).postDelayed(() ->//延迟关闭，是因为防止首页还没完全启动起来而其他页都关掉了出现短暂黑屏
                    ActivityUntil.finishOthersActivity(MainActivity.class), 200);
        } else {
            ActivityUntil.finishOthersActivity(MainActivity.class);
        }
    }
}
