package com.haiercash.gouhua.utils;

import android.content.Context;

import com.app.haiercash.base.db.DbUtils;
import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.baidu.location.Address;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.beans.location.AreaCode;
import com.haiercash.gouhua.beans.location.City;
import com.haiercash.gouhua.beans.location.District;
import com.haiercash.gouhua.beans.location.LocationCodeBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.network.NetHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * author: Sun
 * date: 2017/9/18.
 * fileName: LocationUtils
 * 描述 :  用于百度定位,获取百度定位的相关信息；
 * 1，获取成功则保存 经纬度 省市名称 省市编码
 * 2，如果定位失败则进行相应提示
 */

public class LocationUtils {

    private final NetHelper netHelper;
    private LocationClient mLocationClient;
    private LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Hight_Accuracy;
    private Context mCtx;

    public static final String LOCATION_ERROR_CONFIM = "获取定位失败，请稍后重试";
    public static final String LOCATION_ERROR_PERMISSIN = "请开启定位服务及存储权限";
    public static final String LOCATION_ERROR_NO_SERVICE = "所在城市未开通服务";
    private OnLocationListener mOnLocationListener;


    private Address address;
    private String proCode;
    private String cityCode;
    private String areaCode;

    /**
     * 初始化 定位的参数
     */
    public LocationUtils(Context context) {
        mCtx = context.getApplicationContext();
        netHelper = new NetHelper(this);
        initLocation();

    }

    /**
     * 开启百度定位
     *
     * @param onLocationListener 回调监听
     */

    void startLocation(OnLocationListener onLocationListener) {
        mOnLocationListener = onLocationListener;
        mLocationClient.start();
        mLocationClient.requestLocation();
    }


    void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
    }


    private INetResult mINetResult = new INetResult() {
        @Override
        public <T> void onSuccess(T success, String url) {
            if (success == null) {
                onOnLocationCallBack(LOCATION_ERROR_CONFIM);
                return;
            }
            LocationCodeBean locationCodeBean = (LocationCodeBean) success;
            if (!CheckUtil.isEmpty(locationCodeBean)
                    && !CheckUtil.isEmpty(locationCodeBean.getAreaCodeList())) {
                AreaCode areaCodeBean = locationCodeBean.getAreaCodeList().get(0);
                if (!CheckUtil.isEmpty(areaCodeBean)) {
                    String provinceCode = areaCodeBean.getCode(); //省code
                    if (!CheckUtil.isEmpty(provinceCode)
                            && !CheckUtil.isEmpty(areaCodeBean.getCitys())) {
                        City cityBean = areaCodeBean.getCitys().get(0);
                        if (!CheckUtil.isEmpty(cityBean)) {
                            String cityCode = cityBean.getCode();
                            if (!CheckUtil.isEmpty(cityCode)
                                    && !CheckUtil.isEmpty(cityBean.getDistricts())) {
                                District districtBean = cityBean.getDistricts().get(0);
                                String districtId = districtBean.getCode();
                                if (!CheckUtil.isEmpty(districtId)) {
                                    setLocationCode(provinceCode, cityCode, districtId);
                                    onOnLocationCallBack("");
                                } else {
                                    onOnLocationCallBack(LOCATION_ERROR_CONFIM);
                                }
                            } else {
                                onOnLocationCallBack(LOCATION_ERROR_CONFIM);
                            }
                        } else {
                            onOnLocationCallBack(LOCATION_ERROR_CONFIM);
                        }

                    } else {
                        onOnLocationCallBack(LOCATION_ERROR_CONFIM);
                    }
                } else {
                    onOnLocationCallBack(LOCATION_ERROR_CONFIM);
                }


            } else {
                onOnLocationCallBack(LOCATION_ERROR_CONFIM);
            }

/*
            Map<String, String> map = (Map<String, String>) success;
            String paramValue = map.get("paramValueNew");
            if (CheckUtil.isEmpty(paramValue)) {
                paramValue = map.get("paramValue");
            }
            if (!CheckUtil.isEmpty(paramValue)) {
                // 对返回的数据进行处理
                String[] paramValues = paramValue.split(":");
                //如果配置的数据不对返回错误
                if (paramValues.length == 2) {//市：区
                    //保存地理位置信息
                    setLocationCode(proCode, paramValues[0], paramValues[1]);
                } else if (paramValues.length == 3) {//省：市：区
                    setLocationCode(paramValues[0], paramValues[1], paramValues[2]);
                } else {
                    onOnLocationCallBack(LOCATION_ERROR_CONFIM);
                    return;
                }
                onOnLocationCallBack("");
            } else {
                onOnLocationCallBack(LOCATION_ERROR_CONFIM);
            }*/
        }

        @Override
        public void onError(BasicResponse error, String url) {
            onOnLocationCallBack(LOCATION_ERROR_CONFIM);
        }
    };

    private void onOnLocationCallBack(String error) {
        if (mOnLocationListener == null) {
            return;
        }
        if (CheckUtil.isEmpty(error)) {
            mOnLocationListener.onSuccess();
        } else {
            //mOnLocationListener.onErr(LOCATION_ERROR_CONFIM);
            mOnLocationListener.onErr(error);
        }
    }


    /**
     * 实现实位回调监听
     */
    public class LocationListener extends BDAbstractLocationListener {


        @Override
        public void onReceiveLocation(BDLocation location) {
            // 获取到信息，则停止定位
            stopLocation();
            //定位失败
            if (location == null || location.getLatitude() <= 0 || location.getLongitude() <= 0) {
                onOnLocationCallBack(LOCATION_ERROR_CONFIM);
                return;
            }
            // 获取定位的经纬度
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            Logger.e("经度" + lat + "纬度" + lon);

            if (!CheckUtil.isEmpty(location.getAddrStr())) {
                SpHelper baiduSp = SpHelper.getInstance();
                baiduSp.saveMsgToSp(SpKey.LOCATION, SpKey.LOCATION_UPDATE, System.currentTimeMillis() + "");
                address = location.getAddress();

                //  Logger.e("当前位置：", JsonUtils.toJson(location));  //oppo R9s上测试环境会挂在这
                baiduSp.saveMsgToSp(SpKey.LOCATION, SpKey.LOCATION_LAT, String.valueOf(lat));
                baiduSp.saveMsgToSp(SpKey.LOCATION, SpKey.LOCATION_LON, String.valueOf(lon));
                baiduSp.saveMsgToSp(SpKey.LOCATION, SpKey.LOCATION_JSON, JsonUtils.toJson(address));

                getConfigLocation(address);

//                proCode = DbUtils.getAddress().getProvinceCode(address.province);
//                if (CheckUtil.isEmpty(proCode)) {
//                    onOnLocationCallBack(LOCATION_ERROR_NO_SERVICE);
//                    postErrorMsgToServer(JsonUtils.toJson(address));
//                    return;
//                }
//                cityCode = DbUtils.getAddress().getCityCode(proCode, address.city);
//                areaCode = DbUtils.getAddress().getAreaCode(cityCode, address.district);
//                if (CheckUtil.isEmpty(cityCode) || CheckUtil.isEmpty(areaCode)) {
//                    tryGetLocation();
//                }
//                setLocationCode(proCode, cityCode, areaCode);
//                // 如果查询到的市区编码为空，则请求服务器端进行校验
//                if (CheckUtil.isEmpty(cityCode) || CheckUtil.isEmpty(areaCode)) {
                //               getConfigLocation(address);
//                } else { // 如果数据正常则回调
//                    onOnLocationCallBack("");
                //  }
            } else {
                onOnLocationCallBack(LOCATION_ERROR_CONFIM);
            }

        }
    }


    /**
     * 如果本地数据库查询失败，则再次尝试查询
     */
    private void tryGetLocation() {
        String cityAndArea = DbUtils.getAddress().getCityAndAreaCode(address.city + ":" + address.district);
        if (!CheckUtil.isEmpty(cityAndArea) && cityAndArea.contains(":")) {
            cityCode = cityAndArea.split(":")[0];
            areaCode = cityAndArea.split(":")[1];
        }
    }

    /**
     * 获取服务端的配置位置信息
     *
     * @param address
     */
    /**
     * {
     * "province":"上海市",
     * "city":"上海市",
     * "district":"浦东新区"
     * }
     *
     * @param address
     */
    private void getConfigLocation(Address address) {
        NetHelper netHelper = new NetHelper(mINetResult);
        Map<String, String> map = new HashMap<>();
        map.put("province", address.province);
        map.put("city", address.city);
        map.put("district", address.district);
        netHelper.getService(ApiUrl.GET_AREA_CODE, map, LocationCodeBean.class);
    }

    /**
     * 保存省市区信息
     *
     * @param proCode
     * @param cityCode
     * @param areaCode
     */
    private void setLocationCode(String proCode, String cityCode, String areaCode) {
        SpHelper spLocation = SpHelper.getInstance();
        spLocation.saveMsgToSp(SpKey.LOCATION, SpKey.LOCATION_PEOVINCENAME, address.province);
        spLocation.saveMsgToSp(SpKey.LOCATION, SpKey.LOCATION_PROVINCECODE, proCode);
        spLocation.saveMsgToSp(SpKey.LOCATION, SpKey.LOCATION_CITYCODE, cityCode);
        spLocation.saveMsgToSp(SpKey.LOCATION, SpKey.LOCATION_CITYNAME, address.city);
        spLocation.saveMsgToSp(SpKey.LOCATION, SpKey.LOCATION_AREACODE, areaCode);
        spLocation.saveMsgToSp(SpKey.LOCATION, SpKey.LOCATION_AREANAME, address.district);
    }


    private void initLocation() {
        mLocationClient = new LocationClient(mCtx);
        mLocationClient.registerLocationListener(new LocationListener()); // 注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);// 设置定位模式为高精度
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02，设置为 bd09ll
        option.setIsNeedAddress(true);  // 返回的定位结果包含地址信息
        option.setIgnoreKillProcess(true);
        option.setOpenGps(true);// 设置是否使用GPS 定位
        mLocationClient.setLocOption(option);
    }


    public interface OnLocationListener {
        /**
         * 定位失败
         *
         * @param reason 失败的原因
         */
        void onErr(String reason);

        /**
         * 定位成功
         */
        void onSuccess();
    }

    /**
     * 上报异常数据
     *
     * @param msg 详细信息,详细位置
     */
    public void postErrorMsgToServer(String msg) {
        if (netHelper != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("code", "-1");
            map.put("type", "所在城市未开通服务");
            map.put("errorMethod", "LOCATION_ERROR_NO_SERVICE");
            map.put("time", TimeUtil.calendarToString());
            map.put("userId", AppApplication.userid);
            map.put("msg", msg);
            netHelper.postService(ApiUrl.POST_APP_ACTION_LOG, map);
        }

    }

}
