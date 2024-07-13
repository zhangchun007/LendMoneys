package com.megvii.livenesslib.baiduface;

import android.content.Context;

import com.app.haiercash.base.bui.BaseGHActivity;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.baidu.idl.face.api.manager.FaceConst;
import com.baidu.idl.face.api.manager.FaceInitCallback;
import com.baidu.idl.face.api.manager.FaceServiceCallbck;
import com.baidu.idl.face.api.manager.FaceServiceManager;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.facelive.api.FaceLiveManager;
import com.baidu.idl.facelive.api.entity.FaceLiveConfig;
import com.baidu.idl.facelive.api.entity.FaceLivenessType;
import com.baidu.idl.facelive.api.entity.LivenessValueModel;
import com.megvii.livenesslib.baiduface.exception.FaceException;
import com.megvii.livenesslib.baiduface.manager.ConsoleConfigManager;
import com.megvii.livenesslib.baiduface.model.Config;
import com.megvii.livenesslib.baiduface.model.ConsoleConfig;
import com.megvii.livenesslib.baiduface.model.LivenessVsIdcardResult;
import com.megvii.livenesslib.baiduface.utils.IBaiduFaceCallBack;
import com.megvii.livenesslib.baiduface.utils.PoliceCheckResultParser;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 百度人脸识别工具类
 * https://ai.baidu.com/ai-doc/FACE/Ykxh51xsl
 * @Author: zhangchun
 * @CreateDate: 2023/8/30
 * @Version: 1.0
 */
public class BaiduFaceUtils {

    private BaseGHActivity activity;
    private IBaiduFaceCallBack callBack;
    /**
     * console识别配置
     */
    private ConsoleConfig consoleConfig;

    /**
     * 身份证类型
     */
    protected int cardType = FaceConst.MAIN_LAND_IDCARD;

    public BaiduFaceUtils(BaseGHActivity activity, IBaiduFaceCallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;
    }

    /**
     * 百度人脸初始化
     */
    public void startFace() {
        // 初始化
        //1000为成功，其他为失败，详情参考resultCode错误码说明
        FaceServiceManager.getInstance().init(activity,
                Config.licenseID, Config.licenseFileName, Config.keyName, new FaceInitCallback() {
                    @Override
                    public void onCallback(int resultCode, String resultMsg) {
                        if (resultCode == 1000) { //初始化成功
                            startFaceCollect(activity);
                        } else {
                            if (callBack != null) {
                                callBack.faceCallBack(false, resultCode, resultMsg,"baiduFaceInit()");
                            }
                        }
                    }
                });
    }


    private void startFaceCollect(Context context) {
        // 人脸阈值设置
        setFaceQualityConfig(context);
        /**
         * 单纯使用移动端人脸采集能力，返回加密数据需要配合AI开放平台接口使用
         * 人脸对比v4接口：https://ai.baidu.com/ai-doc/FACE/Oktmssfse
         */
        FaceServiceManager.getInstance().startFaceCollect(context, new FaceServiceCallbck() {
            @Override
            public void onCallback(final int resultCode, final Map<String, Object> resultMap) {
               activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultCode == FaceConst.SUCCESS) { // 采集成功
                            Logger.e(resultCode + ":" + resultMap.get("resultMsg") + ","
                                            + resultMap.get("sKey") + "," + resultMap.get("xDeviceId"));
                            handleResultFaceCollect(resultCode, resultMap);
                        } else { // 采集失败
                            // SDK本地错误码
                            if (callBack != null)
                                callBack.faceCallBack(false, resultCode, (String) resultMap.get("resultMsg"),"baiduFaceStartFaceCollect()");
                        }
                    }
                });
            }
        });
    }

    /**
     * 人脸识别后的逻辑判断
     *
     * @param resultCode
     * @param resultMap
     */
    private void handleResultFaceCollect(int resultCode, Map<String, Object> resultMap) {
        if (resultCode == 0) {
            try {
                if (callBack != null)
                    callBack.faceCallBack(true, resultCode, resultMap,"baiduFaceStartFaceCollect()");
            } catch (Exception e) {
                // 服务端错误
                if (callBack != null)
                    callBack.faceCallBack(false, resultCode, e.toString(),"baiduFaceStartFaceCollect()");
            }
        } else {
            // SDK本地错误码
            if (callBack != null)
                callBack.faceCallBack(false, resultCode, (String) resultMap.get("resultMsg"),"baiduFaceStartFaceCollect()");
        }
    }



    /**
     * 百度人脸参数配置
     *
     * @param context
     */
    private void setFaceQualityConfig(Context context) {
        consoleConfig = ConsoleConfigManager.getInstance(context).getConfig();
        try {
            FaceLiveConfig faceLiveConfig = new FaceLiveConfig();
            // faceUI默认展示结果页，此处必须设置为false
            faceLiveConfig.setShowResultView(false);
            // 设置模糊度阈值
            faceLiveConfig.setBlurnessValue(consoleConfig.getBlur());
            // 设置最小光照阈值（范围0-255）
            faceLiveConfig.setBrightnessValue(consoleConfig.getIllumination());
            // 设置最大光照阈值（范围0-255）
            faceLiveConfig.setBrightnessMaxValue(consoleConfig.getMaxIllumination());
            // 设置左眼遮挡阈值
            faceLiveConfig.setOcclusionLeftEyeValue(consoleConfig.getLeftEyeOcclu());
            // 设置右眼遮挡阈值
            faceLiveConfig.setOcclusionRightEyeValue(consoleConfig.getRightEyeOcclu());
            // 设置鼻子遮挡阈值
            faceLiveConfig.setOcclusionNoseValue(consoleConfig.getNoseOcclu());
            // 设置嘴巴遮挡阈值
            faceLiveConfig.setOcclusionMouthValue(consoleConfig.getMouthOcclu());
            // 设置左脸颊遮挡阈值
            faceLiveConfig.setOcclusionLeftContourValue(consoleConfig.getLeftCheekOcclu());
            // 设置右脸颊遮挡阈值
            faceLiveConfig.setOcclusionRightContourValue(consoleConfig.getRightCheekOcclu());
            // 设置下巴遮挡阈值
            faceLiveConfig.setOcclusionChinValue(consoleConfig.getChinOcclu());
            // 设置人脸姿态Pitch阈值
            faceLiveConfig.setHeadPitchValue(consoleConfig.getPitch());
            // 设置人脸姿态Yaw阈值
            faceLiveConfig.setHeadYawValue(consoleConfig.getYaw());
            // 设置人脸姿态Roll阈值
            faceLiveConfig.setHeadRollValue(consoleConfig.getRoll());
            // 是否开启录制视频
            faceLiveConfig.setOpenRecord(false);
            // 设置是否显示超时弹框
            faceLiveConfig.setIsShowTimeoutDialog(true);
            // 输出图片类型：0原图、1抠图
            faceLiveConfig.setOutputImageType(FaceEnvironment.VALUE_OUTPUT_IMAGE_TYPE);
            // 是否忽略录制异常（只能忽略采集时间过短，采集后无文件输出的异常）
            faceLiveConfig.setIgnoreRecordError(true);
            // 眨眼张嘴遮挡开关
            faceLiveConfig.setActiveStrict(true);
            // 设置活体类型相关
            setFaceLivenessConfig(faceLiveConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFaceLivenessConfig(FaceLiveConfig faceLiveConfig) {
        try {
            // 设置活体类型：炫彩活体、动作活体、静默活体
            FaceLivenessType faceLivenessType = null;
            // 配置活体动作集合、动作个数，活体阈值，无活体
            LivenessValueModel livenessValueModel = null;
            if (consoleConfig.getFaceLiveType() == 0) {
                faceLivenessType = FaceLivenessType.COLORLIVENESS;
                // 是否开启炫彩活体能力
                faceLiveConfig.setIsOpenColorLive(true);
                // 是否开启动作活体能力
                faceLiveConfig.setIsOpenActionLive(true);
                livenessValueModel = faceLiveConfig.getLivenessValueModel();
                livenessValueModel.actionList.addAll(consoleConfig.getActions());
                livenessValueModel.livenessScore = consoleConfig.getLiveScore();
            } else if (consoleConfig.getFaceLiveType() == 1) {
                faceLivenessType = FaceLivenessType.ACTIONLIVENESS;
                // 是否开启炫彩活体能力
                faceLiveConfig.setIsOpenColorLive(false);
                // 是否开启动作活体能力
                faceLiveConfig.setIsOpenActionLive(true);
                livenessValueModel = faceLiveConfig.getLivenessValueModel();
                livenessValueModel.actionList.addAll(consoleConfig.getActions());
                livenessValueModel.actionRandomNumber = consoleConfig.getFaceActionNum();
                livenessValueModel.livenessScore = consoleConfig.getLiveScore();
            } else if (consoleConfig.getFaceLiveType() == 2) {
                faceLivenessType = FaceLivenessType.SILENTLIVENESS;
                // 是否开启炫彩活体能力
                faceLiveConfig.setIsOpenColorLive(false);
                // 是否开启动作活体能力
                faceLiveConfig.setIsOpenActionLive(false);
                livenessValueModel = faceLiveConfig.getLivenessValueModel();
                livenessValueModel.livenessScore = consoleConfig.getLiveScore();
            } else if (consoleConfig.getFaceLiveType() == 3) {
                // 是否开启炫彩活体能力
                faceLiveConfig.setIsOpenColorLive(true);
                // 是否开启动作活体能力
                faceLiveConfig.setIsOpenActionLive(false);
                livenessValueModel = faceLiveConfig.getLivenessValueModel();
            }
            // faceLiveConfig.setFaceLivenessType(faceLivenessType, livenessValueModel);
            faceLiveConfig.setLivenessValueModel(livenessValueModel);
            FaceLiveManager.getInstance().setFaceConfig(faceLiveConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
