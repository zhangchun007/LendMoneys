package com.haiercash.gouhua.beans.risk;

import java.io.Serializable;

public class BrBean implements Serializable {
    private String api_code;
    private String plat_type;
    private String br_version;
    private String gid;
    private String token_id;
    private String event;
    private String user_id;
    private String business_id;
    private String device_id;
    private String MAC;
    private String android_id;
    private Object application_list;
    private String imsi;
    private String fingerpring;
    private String UUID;
    private String brand;
    private String os_version;
    private String app_name;
    private String app_version;
    private String package_name;
    private String carrier_name;
    private String model;
    private String resolution;
    private String network_speed;
    private String network_signal;
    private String client_status;
    private String board;
    private String bootloader;
    private String cpu_abi;
    private String driver;
    private String device_version;
    private String hardware;
    private String device_host;
    private String manufacturer;
    private String product;
    private String tags;
    private String type;
    private String incremental;
    private String cpuInfo;
    private String is_root;
    private String is_simulator;
    private String is_vpn_proxy;
    private String is_wifi_proxy;
    private String disk_total;
    private String disk_free;
    private String is_dev;
    private String is_siml;
    private String is_ect;
    private String is_uct;
    private String is_rv;
    private String is_monkey;
    private String boot_time;
    private String boot_duration;
    private String battery_health;
    private String battery_scale;
    private String battery_vol;
    private String battery_tem;
    private String battery_level;
    private String volume_call;
    private String volume_alarm;
    private String volume_system;
    private String volume_media;
    private String volume_ring;
    private String brightness;
    private String time_zone;
    private String memory_total;
    private Object languages;
    private String network_type;
    private String mcc;
    private String mnc;
    private String statistic_id;
    private String is_wlan_open;
    private String is_multirun;
    private String network_ip;
    private String bssid_ip;
    private String battery_plug;
    private String battery_status;
    private String area_hash;
    private String light_list;
    private String gyro_list;
    private String ori_list;
    private String acc_list;

    public String afSwiftNumber;
    private Object obj;
    /////自采集
    public String bluetooth_mac;  //	蓝牙物理地址	蓝牙物理地址
    public String cell;   //	基站信息	基站信息
    public String cpuCount;   //	设备CPU核数量	设备CPU核数量
    public String sd; //	SD卡容量	SD卡容量
    public String sensor; //	传感器信息	传感器信息
    public String cpu_abi2;   //	CPU指令集	CPU指令集
    public String cpu_model;  //	CPU型号	CPU型号
    public String ro_debuggable;  //	系统调试开关，1：可调试	系统调试开关，1：可调试
    public String persist_sys_country;    //	系统默认国家	系统默认国家
    public String persist_sys_language;   //	系统默认语言	系统默认语言
    public String net_dns1;   //	系统网络dns	系统网络dns
    public String net_hostname;   //	主机名	主机名
    public String net_eth0_gw;    //	eth0网络设备的网关	eth0网络设备的网关
    public String bssid;  //	当前wifi热点物理地址	当前wifi热点物理地址
    public String network;    //	手机网络连接方式	手机网络连接方式
    public String signal_strength;    //	当前wifi信号强度	当前wifi信号强度
    public String wifiname;   //	当前wifi名称	当前wifi名称


    public String getApi_code() {
        return api_code;
    }

    public void setApi_code(String api_code) {
        this.api_code = api_code;
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

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getFingerpring() {
        return fingerpring;
    }

    public void setFingerpring(String fingerpring) {
        this.fingerpring = fingerpring;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getOs_version() {
        return os_version;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getCarrier_name() {
        return carrier_name;
    }

    public void setCarrier_name(String carrier_name) {
        this.carrier_name = carrier_name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getNetwork_speed() {
        return network_speed;
    }

    public void setNetwork_speed(String network_speed) {
        this.network_speed = network_speed;
    }

    public String getNetwork_signal() {
        return network_signal;
    }

    public void setNetwork_signal(String network_signal) {
        this.network_signal = network_signal;
    }

    public String getClient_status() {
        return client_status;
    }

    public void setClient_status(String client_status) {
        this.client_status = client_status;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getBootloader() {
        return bootloader;
    }

    public void setBootloader(String bootloader) {
        this.bootloader = bootloader;
    }

    public String getCpu_abi() {
        return cpu_abi;
    }

    public void setCpu_abi(String cpu_abi) {
        this.cpu_abi = cpu_abi;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getDevice_version() {
        return device_version;
    }

    public void setDevice_version(String device_version) {
        this.device_version = device_version;
    }

    public String getHardware() {
        return hardware;
    }

    public void setHardware(String hardware) {
        this.hardware = hardware;
    }

    public String getDevice_host() {
        return device_host;
    }

    public void setDevice_host(String device_host) {
        this.device_host = device_host;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIncremental() {
        return incremental;
    }

    public void setIncremental(String incremental) {
        this.incremental = incremental;
    }

    public String getCpuInfo() {
        return cpuInfo;
    }

    public void setCpuInfo(String cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

    public String getIs_root() {
        return is_root;
    }

    public void setIs_root(String is_root) {
        this.is_root = is_root;
    }

    public String getIs_simulator() {
        return is_simulator;
    }

    public void setIs_simulator(String is_simulator) {
        this.is_simulator = is_simulator;
    }

    public String getIs_vpn_proxy() {
        return is_vpn_proxy;
    }

    public void setIs_vpn_proxy(String is_vpn_proxy) {
        this.is_vpn_proxy = is_vpn_proxy;
    }

    public String getIs_wifi_proxy() {
        return is_wifi_proxy;
    }

    public void setIs_wifi_proxy(String is_wifi_proxy) {
        this.is_wifi_proxy = is_wifi_proxy;
    }

    public String getDisk_total() {
        return disk_total;
    }

    public void setDisk_total(String disk_total) {
        this.disk_total = disk_total;
    }

    public String getDisk_free() {
        return disk_free;
    }

    public void setDisk_free(String disk_free) {
        this.disk_free = disk_free;
    }

    public String getIs_dev() {
        return is_dev;
    }

    public void setIs_dev(String is_dev) {
        this.is_dev = is_dev;
    }

    public String getIs_siml() {
        return is_siml;
    }

    public void setIs_siml(String is_siml) {
        this.is_siml = is_siml;
    }

    public String getIs_ect() {
        return is_ect;
    }

    public void setIs_ect(String is_ect) {
        this.is_ect = is_ect;
    }

    public String getIs_uct() {
        return is_uct;
    }

    public void setIs_uct(String is_uct) {
        this.is_uct = is_uct;
    }

    public String getIs_rv() {
        return is_rv;
    }

    public void setIs_rv(String is_rv) {
        this.is_rv = is_rv;
    }

    public String getIs_monkey() {
        return is_monkey;
    }

    public void setIs_monkey(String is_monkey) {
        this.is_monkey = is_monkey;
    }

    public String getBoot_time() {
        return boot_time;
    }

    public void setBoot_time(String boot_time) {
        this.boot_time = boot_time;
    }

    public String getBoot_duration() {
        return boot_duration;
    }

    public void setBoot_duration(String boot_duration) {
        this.boot_duration = boot_duration;
    }

    public String getBattery_health() {
        return battery_health;
    }

    public void setBattery_health(String battery_health) {
        this.battery_health = battery_health;
    }

    public String getBattery_scale() {
        return battery_scale;
    }

    public void setBattery_scale(String battery_scale) {
        this.battery_scale = battery_scale;
    }

    public String getBattery_vol() {
        return battery_vol;
    }

    public void setBattery_vol(String battery_vol) {
        this.battery_vol = battery_vol;
    }

    public String getBattery_tem() {
        return battery_tem;
    }

    public void setBattery_tem(String battery_tem) {
        this.battery_tem = battery_tem;
    }

    public String getBattery_level() {
        return battery_level;
    }

    public void setBattery_level(String battery_level) {
        this.battery_level = battery_level;
    }

    public String getVolume_call() {
        return volume_call;
    }

    public void setVolume_call(String volume_call) {
        this.volume_call = volume_call;
    }

    public String getVolume_alarm() {
        return volume_alarm;
    }

    public void setVolume_alarm(String volume_alarm) {
        this.volume_alarm = volume_alarm;
    }

    public String getVolume_system() {
        return volume_system;
    }

    public void setVolume_system(String volume_system) {
        this.volume_system = volume_system;
    }

    public String getVolume_media() {
        return volume_media;
    }

    public void setVolume_media(String volume_media) {
        this.volume_media = volume_media;
    }

    public String getVolume_ring() {
        return volume_ring;
    }

    public void setVolume_ring(String volume_ring) {
        this.volume_ring = volume_ring;
    }

    public String getBrightness() {
        return brightness;
    }

    public void setBrightness(String brightness) {
        this.brightness = brightness;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public String getMemory_total() {
        return memory_total;
    }

    public void setMemory_total(String memory_total) {
        this.memory_total = memory_total;
    }


    public String getNetwork_type() {
        return network_type;
    }

    public void setNetwork_type(String network_type) {
        this.network_type = network_type;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getStatistic_id() {
        return statistic_id;
    }

    public void setStatistic_id(String statistic_id) {
        this.statistic_id = statistic_id;
    }

    public String getIs_wlan_open() {
        return is_wlan_open;
    }

    public void setIs_wlan_open(String is_wlan_open) {
        this.is_wlan_open = is_wlan_open;
    }

    public String getIs_multirun() {
        return is_multirun;
    }

    public void setIs_multirun(String is_multirun) {
        this.is_multirun = is_multirun;
    }

    public String getNetwork_ip() {
        return network_ip;
    }

    public void setNetwork_ip(String network_ip) {
        this.network_ip = network_ip;
    }

    public String getBssid_ip() {
        return bssid_ip;
    }

    public void setBssid_ip(String bssid_ip) {
        this.bssid_ip = bssid_ip;
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

    public String getArea_hash() {
        return area_hash;
    }

    public void setArea_hash(String area_hash) {
        this.area_hash = area_hash;
    }

    public String getLight_list() {
        return light_list;
    }

    public void setLight_list(String light_list) {
        this.light_list = light_list;
    }

    public String getGyro_list() {
        return gyro_list;
    }

    public void setGyro_list(String gyro_list) {
        this.gyro_list = gyro_list;
    }

    public String getOri_list() {
        return ori_list;
    }

    public void setOri_list(String ori_list) {
        this.ori_list = ori_list;
    }

    public String getAcc_list() {
        return acc_list;
    }

    public void setAcc_list(String acc_list) {
        this.acc_list = acc_list;
    }

    public String getAfSwiftNumber() {
        return afSwiftNumber;
    }

    public void setAfSwiftNumber(String afSwiftNumber) {
        this.afSwiftNumber = afSwiftNumber;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getBluetooth_mac() {
        return bluetooth_mac;
    }

    public void setBluetooth_mac(String bluetooth_mac) {
        this.bluetooth_mac = bluetooth_mac;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getCpuCount() {
        return cpuCount;
    }

    public void setCpuCount(String cpuCount) {
        this.cpuCount = cpuCount;
    }

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public String getCpu_abi2() {
        return cpu_abi2;
    }

    public void setCpu_abi2(String cpu_abi2) {
        this.cpu_abi2 = cpu_abi2;
    }

    public String getCpu_model() {
        return cpu_model;
    }

    public void setCpu_model(String cpu_model) {
        this.cpu_model = cpu_model;
    }

    public String getRo_debuggable() {
        return ro_debuggable;
    }

    public void setRo_debuggable(String ro_debuggable) {
        this.ro_debuggable = ro_debuggable;
    }

    public String getPersist_sys_country() {
        return persist_sys_country;
    }

    public void setPersist_sys_country(String persist_sys_country) {
        this.persist_sys_country = persist_sys_country;
    }

    public String getPersist_sys_language() {
        return persist_sys_language;
    }

    public void setPersist_sys_language(String persist_sys_language) {
        this.persist_sys_language = persist_sys_language;
    }

    public String getNet_dns1() {
        return net_dns1;
    }

    public void setNet_dns1(String net_dns1) {
        this.net_dns1 = net_dns1;
    }

    public String getNet_hostname() {
        return net_hostname;
    }

    public void setNet_hostname(String net_hostname) {
        this.net_hostname = net_hostname;
    }

    public String getNet_eth0_gw() {
        return net_eth0_gw;
    }

    public void setNet_eth0_gw(String net_eth0_gw) {
        this.net_eth0_gw = net_eth0_gw;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getSignal_strength() {
        return signal_strength;
    }

    public void setSignal_strength(String signal_strength) {
        this.signal_strength = signal_strength;
    }

    public String getWifiname() {
        return wifiname;
    }

    public void setWifiname(String wifiname) {
        this.wifiname = wifiname;
    }

    public Object getApplication_list() {
        return application_list;
    }

    public void setApplication_list(Object application_list) {
        this.application_list = application_list;
    }

    public Object getLanguages() {
        return languages;
    }

    public void setLanguages(Object languages) {
        this.languages = languages;
    }
}
