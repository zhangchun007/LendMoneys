package com.app.haiercash.base.db.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.app.haiercash.base.bean.CollectInfo;
import com.app.haiercash.base.db.dao.CollectInfoDao;

/**
 * *@Author:    Sun
 * *@Date  :    2020/7/22
 * *@FileName: AppDatabase
 * *@Description:
 */
@Database(entities = {CollectInfo.class}, version = 3,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CollectInfoDao collectInfoDao();
}
