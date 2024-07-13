package com.haiercash.gouhua.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.FileUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.rxbus.ActionEvent;
import com.app.haiercash.base.utils.rxbus.RxBus;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.beans.IconMain;
import com.haiercash.gouhua.fragments.main.MainHelper;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.SpHp;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2019/3/19<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class DownIconWork extends Worker implements INetResult {
    private NetHelper netHelper;
    private String md5IconStyleContent;

    public DownIconWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        netHelper = new NetHelper(this);
        netHelper.getService(ApiUrl.HOME_ICON_CHECK_URL, null);
        System.out.println("--DownIconWork-------------->doWork()");
        return Result.success();
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        System.out.println("--DownIconWork-------------->onSuccess:" + url);
        if (ApiUrl.HOME_ICON_CHECK_URL.equals(url)) {
            Map iconTypeMap = (Map) t;
            String localContent = SpHp.getOther(SpKey.HOME_ICON_MD5);
            md5IconStyleContent = String.valueOf(iconTypeMap.get("md5IconStyleContent"));
            //String showSetting = String.valueOf(iconTypeMap.get("showSetting"));
            if (!CheckUtil.isEmpty(md5IconStyleContent)) {
                if (!localContent.equals(md5IconStyleContent)) {
                    Map<String, String> map = new HashMap<>(1);
                    map.put("iconType", "base64");
                    netHelper.getService(ApiUrl.HOME_ICON_URL, map);
                } else {
                    String filePath = MainHelper.getIconFilePath();
                    if (!CheckUtil.isEmpty(filePath)) {
                        File file = new File(filePath);
                        if (!file.exists() || file.list() == null || file.list().length >= 8) {
                            FileUtils.deleteFile(file);
                            //下载文件;
                            Map<String, String> map = new HashMap<>(1);
                            map.put("iconType", "base64");
                            netHelper.getService(ApiUrl.HOME_ICON_URL, map);
                        }
                    }
                }
            } else {
                deleteIconAndReset();
            }
        } else if (ApiUrl.HOME_ICON_URL.equals(url)) {
            final List<IconMain> list = JsonUtils.fromJsonArray(t, "iconStyles", IconMain.class);
            ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>());
            executorService.submit(() -> {
                if (MainHelper.saveHomeIcon(list)) {
                    SpHp.saveSpOther(SpKey.HOME_ICON_MD5, md5IconStyleContent);
                    //通知首页更新Icon
                    RxBus.getInstance().post(new ActionEvent(ActionEvent.MainShowIcon, "newIcon"));
                } else {
                    deleteIconAndReset();
                }
            });
        }
    }

    private void deleteIconAndReset() {
        String filePath = MainHelper.getIconFilePath();
        if (!CheckUtil.isEmpty(filePath)) {
            FileUtils.deleteFile(new File(filePath));
        }
        SpHp.deleteOther(SpKey.HOME_ICON_MD5);
        //通知首页更新Icon
        RxBus.getInstance().post(new ActionEvent(ActionEvent.MainShowIcon, "default"));
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (ApiUrl.HOME_ICON_CHECK_URL.equals(url) || ApiUrl.HOME_ICON_URL.equals(url)) {
            Logger.d("数据请求失败：" + url + "\n" + error.getHead().getRetMsg());
        }
    }
}
