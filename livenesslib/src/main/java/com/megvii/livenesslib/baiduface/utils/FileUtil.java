/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.megvii.livenesslib.baiduface.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class FileUtil {
    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }

    public static String readAssetFileUtf8String(AssetManager assetManager, String filename) throws IOException {
        byte[] bytes = readAssetFileContent(assetManager, filename);
        return new String(bytes, Charset.forName("UTF-8"));
    }

    public static byte[] readAssetFileContent(AssetManager assetManager, String filename) throws IOException {
        Log.i("FileUtil", " try to read asset file :" + filename);
        InputStream is = assetManager.open(filename);
        int size = is.available();
        byte[] buffer = new byte[size];
        int realSize = is.read(buffer);
        if (realSize != size) {
            throw new IOException("realSize is not equal to size: " + realSize + " : " + size);
        }
        is.close();
        return buffer;
    }
}
