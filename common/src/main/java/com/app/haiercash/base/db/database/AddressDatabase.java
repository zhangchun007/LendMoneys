package com.app.haiercash.base.db.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.app.haiercash.base.bean.ArrayBean;
import com.app.haiercash.base.db.dao.AddressDao;

import java.util.List;

/**
 * *@Author:    Sun
 * *@Date  :    2020/7/22
 * *@FileName: AppDatabase
 * *@Description: 地址数据库
 */
@Database(entities = {ArrayBean.class}, version = 1, exportSchema = false)
public abstract class AddressDatabase extends RoomDatabase {


    /**
     * 根据name查询code
     *
     * @param name name
     * @return code
     */
    public String getProvinceCode(String name) {
        return addressDao().getAreaCode("PROVINCE", name);
    }

    /**
     * 根据code查询名称
     *
     * @param name name
     * @return code
     */
    public String getCityCode(String parentCode, String name) {
        return addressDao().getAreaCode(parentCode, "CITY", name);
    }

    /**
     * 根据code查询名称
     *
     * @param name name
     * @return code
     */
    public String getAreaCode(String parentCode, String name) {
        return addressDao().getAreaCode(parentCode, "DISTRICT", name);
    }


    /**
     * 根据code查询名称
     *
     * @param name name
     * @return code
     */
    public String getCityAndAreaCode(String name) {
        return addressDao().getAreaCode("CITYANDAREA", name);
    }


    /**
     * 根据省code查询所有市
     *
     * @param code 省code
     * @return 省下的市区
     */
    public List<ArrayBean> getCityByProvince(String code) {
        return addressDao().getAreaInfo("CITY", code);
    }

    /**
     * 根据市code查询所有区县
     *
     * @param code 市code
     * @return 市下的区县
     */
    public List<ArrayBean> getAreaByCity(String code) {
        return addressDao().getAreaInfo("DISTRICT", code);
    }

    /**
     * 重置DB数据
     */
    public void resetDbData(List<ArrayBean> list) {
        addressDao().deleteAll();
        addressDao().insertAddress(list);
    }


    public abstract AddressDao addressDao();
}
