package com.app.haiercash.base.db;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.room.Room;

import com.app.haiercash.base.db.database.AddressDatabase;
import com.app.haiercash.base.db.database.AppDatabase;
import com.app.haiercash.base.utils.log.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * *@Author:    Sun
 * *@Date  :    2020/7/22
 * *@FileName: DbUtils
 * *@Description: 作为数据库对外的入口
 */
public class DbUtils {
    private static String DB_CITY_AREA_PATH;
    private static final String DB_ADDRESS_NAME = "city_database.data";
    private static final String DB_APP_NAME = "gouhua";

    private static Context application;

    /**
     * 数据库初始化
     */
    public static void init(Context context) {
        application = context.getApplicationContext();
        DB_CITY_AREA_PATH = application.getCacheDir().getAbsolutePath() + "/databases/";
        copyDatabase();
    }

    /**
     * 地址数据库
     */
    public static AddressDatabase getAddress() {
        return Holder.database;
    }


    private static class Holder {
        static String dbPath = DB_CITY_AREA_PATH + DB_ADDRESS_NAME;
        private static final AddressDatabase database;

        static {
            if (isCityDbValid()) {
                Logger.e("AddressDatabase", "createFromFile(new File(dbPath))");
                database = Room.databaseBuilder(application, AddressDatabase.class, DB_ADDRESS_NAME)
                        //通过File创建数据库
                        .createFromFile(new File(dbPath))
                        .allowMainThreadQueries()
                        .build();
            } else {
                Logger.e("AddressDatabase", "createFromAsset(DB_ADDRESS_NAME)");
                database = Room.databaseBuilder(application, AddressDatabase.class, DB_ADDRESS_NAME)
                        //通过asset创建数据库
                        .createFromAsset(DB_ADDRESS_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
    }


    /**
     * APP数据库
     *
     * @return
     */
    public static AppDatabase getAppDatabase() {
        return AppHolder.database;
    }


    private static class AppHolder {
        private static AppDatabase database = Room.databaseBuilder(application,
                AppDatabase.class, DB_APP_NAME)
                .fallbackToDestructiveMigration()//检测到数据库版本变化后，破坏性重建，删掉旧的，新建
                .allowMainThreadQueries()
                .build();
    }

    /* ******************************************************************************************** */
    private static int retryCount = 0;

    private static void copyDatabase() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                copyAddressDb();
            }
        });
        thread.setDaemon(true);//设置为守护线程,确保thread引发的崩溃问题；
        thread.start();
    }

    private static void copyAddressDb() {
        if (retryCount >= 4) {
            return;
        }
        retryCount++;
        String path = DB_CITY_AREA_PATH + DB_ADDRESS_NAME;
        File dbFile = new File(path);
        //查看数据库文件是否存在
        if (dbFile.exists()) {
            Logger.d("DB文件已存在");
            return;
        }
        boolean isCopySuccess = false;
        File dbFile_ = new File(DB_CITY_AREA_PATH);
        if (!dbFile_.mkdirs()) {
            copyAddressDb();
        } else {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            AssetManager assetManager = null;
            try {
                fos = new FileOutputStream(path);
                assetManager = application.getAssets();
                inputStream = assetManager.open(DB_ADDRESS_NAME);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
                isCopySuccess = true;
                Logger.d("DB文件复制成功");
            } catch (IOException e) {
                copyAddressDb();
            } finally {
                try {
                    if (null != fos) {
                        fos.flush();
                        fos.close();
                    }
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        //复制失败，且文件存在-》则删除文件；
                        if (!isCopySuccess && dbFile.exists()) {
                            dbFile.delete();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean isCityDbValid() {
        return new File(DB_CITY_AREA_PATH + DB_ADDRESS_NAME).exists();
    }
}
