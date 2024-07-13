package com.app.haiercash.base.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.app.haiercash.base.bean.CollectInfo;

import java.util.List;

/**
 * *@Author:    Sun
 * *@Date  :    2020/7/24
 * *@FileName: CollectInfoDao
 * *@Description: 操作用户轨迹表
 */
@Dao
public interface CollectInfoDao {

    /**
     * 查询所有数据
     *
     * @return 所有数据
     */
    @Query("select * FROM  collectinfo")
    List<CollectInfo> getAllCollect();

    /**
     * 插入一条数据
     *
     * @param info
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOneInfo(CollectInfo info);

    /**
     * 删除全部数据
     */
    @Query("delete from collectinfo")
    void deleteAll();

}
