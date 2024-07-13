package com.haiercash.gouhua.beans.br;

import java.util.List;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2020/1/7<br/>
 * 描    述：<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class BrEventParam {
    private BrDataBean brData;
    private String token_id;
    private String userId;
    private String gid;
    private String af_swift_number;

    public BrDataBean getBrData() {
        return brData;
    }

    public void setBrData(BrDataBean brData) {
        this.brData = brData;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getAf_swift_number() {
        return af_swift_number;
    }

    public void setAf_swift_number(String af_swift_number) {
        this.af_swift_number = af_swift_number;
    }

    public class BrDataBean {
        private String api_code;
        private String swift_number;
        private String sign;
        private Object param;//ParamBean

        public String getApi_code() {
            return api_code;
        }

        public void setApi_code(String api_code) {
            this.api_code = api_code;
        }

        public String getSwift_number() {
            return swift_number;
        }

        public void setSwift_number(String swift_number) {
            this.swift_number = swift_number;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public Object getParam() {
            return param;
        }

        public void setParam(Object param) {
            this.param = param;
        }
    }

    public class ParamBean {
        private String user_id;
        private String event;
        private String plat_type;
        private String br_version;
        private String battery_plug;
        private String battery_status;
        private String device_id;
        private String bssid_ip;
        private String gid;
        private String token_id;
        private String area_hash;
        private List<String> light_list;
        private List<GyroListBean> gyro_list;
        private List<OriListBean> ori_list;
        private List<AccListBean> acc_list;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getPlat_type() {
            return plat_type;
        }

        public void setPlat_type(String plat_type) {
            this.plat_type = plat_type;
        }

        public String getBr_version() {
            return br_version;
        }

        public void setBr_version(String br_version) {
            this.br_version = br_version;
        }

        public String getBattery_plug() {
            return battery_plug;
        }

        public void setBattery_plug(String battery_plug) {
            this.battery_plug = battery_plug;
        }

        public String getBattery_status() {
            return battery_status;
        }

        public void setBattery_status(String battery_status) {
            this.battery_status = battery_status;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getBssid_ip() {
            return bssid_ip;
        }

        public void setBssid_ip(String bssid_ip) {
            this.bssid_ip = bssid_ip;
        }

        public String getGid() {
            return gid;
        }

        public void setGid(String gid) {
            this.gid = gid;
        }

        public String getToken_id() {
            return token_id;
        }

        public void setToken_id(String token_id) {
            this.token_id = token_id;
        }

        public String getArea_hash() {
            return area_hash;
        }

        public void setArea_hash(String area_hash) {
            this.area_hash = area_hash;
        }

        public List<String> getLight_list() {
            return light_list;
        }

        public void setLight_list(List<String> light_list) {
            this.light_list = light_list;
        }

        public List<GyroListBean> getGyro_list() {
            return gyro_list;
        }

        public void setGyro_list(List<GyroListBean> gyro_list) {
            this.gyro_list = gyro_list;
        }

        public List<OriListBean> getOri_list() {
            return ori_list;
        }

        public void setOri_list(List<OriListBean> ori_list) {
            this.ori_list = ori_list;
        }

        public List<AccListBean> getAcc_list() {
            return acc_list;
        }

        public void setAcc_list(List<AccListBean> acc_list) {
            this.acc_list = acc_list;
        }
    }

    public class GyroListBean {
        private String x;
        private String y;
        private String z;

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public String getZ() {
            return z;
        }

        public void setZ(String z) {
            this.z = z;
        }
    }

    public class OriListBean {
        private String x;
        private String y;
        private String z;

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public String getZ() {
            return z;
        }

        public void setZ(String z) {
            this.z = z;
        }
    }

    public class AccListBean {
        private String x;
        private String y;
        private String z;

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public String getZ() {
            return z;
        }

        public void setZ(String z) {
            this.z = z;
        }
    }
}
