package com.app.haiercash.base.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.app.haiercash.base.bean.ArrayBean;

import java.util.List;

/**
 * *@Author:    Sun
 * *@Date  :    2020/7/22
 * *@FileName: AddressDao
 * *@Description: 用于地址数据库表操作
 */
@Dao
public interface AddressDao {

    /**
     * 查询所有省数据数据  版本兼容
     */
//    @Query("select * from s_area where AREA_TYPE='PROVINCE' and AREA_NAME!='未知' order by AREA_CODE")
    @Query("select * from s_area where AREA_TYPE='PROVINCE' order by AREA_CODE")
    List<ArrayBean> getProvince();


    /**
     * 根据code查询名称
     *
     * @param code code
     * @return 名称
     */
    @Query("select AREA_NAME from s_area where AREA_CODE =:code")
    String getAreaName(String code);


    /**
     * 根据type 和name查询code
     *
     * @param type type
     * @param name name
     */
    @Query("select AREA_CODE from s_area where AREA_TYPE=:type and AREA_NAME=:name and AREA_PARENT_CODE=:parentCode")
    String getAreaCode(String parentCode, String type, String name);


    /**
     * 根据type 和name查询code
     *
     * @param type type
     * @param name name
     */
    @Query("select AREA_CODE from s_area where AREA_TYPE=:type and  AREA_NAME=:name")
    String getAreaCode(String type, String name);


    /**
     * 根据type和code  查所属area
     *
     * @param type type
     * @param code code
     */
    @Query("select * from s_area where AREA_TYPE=:type and AREA_PARENT_CODE=:code order by AREA_CODE")
    List<ArrayBean> getAreaInfo(String type, String code);

//    @Query("select * from s_area limit 3000")
//    List<ArrayBean> getAreaList();

    /**
     * 删除全部数据
     */
    //@Query("delete from s_area where AREA_CODE not like '%:%'")
    @Query("delete from s_area")
    void deleteAll();

    /**
     * 插入一条数据，如果重复则替换
     *
     * @param bean 插入的bean
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAddress(List<ArrayBean> bean);
}
