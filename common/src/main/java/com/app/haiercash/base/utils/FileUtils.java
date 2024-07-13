package com.app.haiercash.base.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import com.app.haiercash.base.BaseApplication;
import com.app.haiercash.base.interfaces.SaveImageResult;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.image.ImageUtils;
import com.app.haiercash.base.utils.image.MemoryConstants;
import com.app.haiercash.base.utils.image.PhotographUtils;
import com.app.haiercash.base.utils.time.TimeUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * Author: Sun
 * Date :    2017/11/6
 * FileName: FileUtils
 * Description:
 */

public class FileUtils {

    //文件下载成功
    public static final int DOWN_LOAD_SUCC = 0x10;
    //文件下载失败
    public static final int DOWN_LOAD_ERROR = 0x11;

    //文件解压失败
    public static final int UNZIP_ERROR = 0x12;
    //文件解压成功
    public static final int UNZIP_SUCC = 0x13;

    //复制文件成功
    public static final int COPY_FILE_SUCC = 0x14;

    //复制文件失败
    public static final int COPY_FILE_ERROR = 0x15;

    public static final String PATH_FEEDBACK = "feedbackImage/";
    public static final String PATH_ADVERT = "advert/";
    public static final String PATH_DB = "database/";

    /**
     * 先根遍历序递归删除文件夹
     *
     * @param dirFile 要被删除的文件或者目录
     */
    public static void deleteFile(final File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (dirFile == null || !dirFile.exists()) {
            return;
        }
        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if (dirFile.isFile()) {
                    dirFile.delete();
                } else {
                    for (File file : dirFile.listFiles()) {
                        deleteFile(file);
                    }
                }
            }
        });
    }

    public static String saveImage(Bitmap bitmap) {
        String filePath = null;
        if (bitmap != null) {
            String fileName = EncryptUtil.md5(TimeUtil.calendarToString()) + ".jpg";
            String path = getExternalFilesDir() + "nameAuth";
            //保存Bitmap
            saveImageFile(bitmap, path, fileName);
            //获取全路径
            filePath = path + File.separator + fileName;
        }
        return filePath;
    }


    /**
     * bitmap保存文件
     */
    public static void saveImage(Bitmap mBitmap, String fileName, SaveImageResult result) {
        String path = getExternalFilesDir() + "pic" + File.separator;
        //保存Bitmap
        saveImageFile(mBitmap, path, fileName, result);
    }


    public static void saveImageFile(Bitmap bitmap, String filePath, String fileName) {
        saveImageFile(bitmap, filePath, fileName, null);
    }

    /**
     * 保存一个图像文件到本地
     * <p>
     * 注意：
     * 1. 文件存储路径不能乱存，如何存储请看BaseApplication
     * 2. 调用前请检查SD卡写入权限
     *
     * @param bitmap   图像
     * @param filePath 保存路径，可以不以“/”结尾
     * @param fileName 文件名，可以不以“.jpg”结尾
     * @param result   存储回调，可以为null
     */
    public static void saveImageFile(Bitmap bitmap, String filePath, String fileName, SaveImageResult result) {
        if (bitmap == null) {
            if (result != null) {
                result.onSaveFailed("HUNO_NULL", "没有获取到图像信息，保存失败");
            }
            return;
        }
        try {
            boolean sdCardWritePermission = BaseApplication.CONTEXT.getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, BaseApplication.CONTEXT.getPackageName()) == PackageManager.PERMISSION_GRANTED;
            if (!sdCardWritePermission) {
                if (result != null) {
                    result.onSaveFailed("HUNO_PERMISSION", "没有sd卡读写权限");
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println(file.mkdirs());
            }
            File imageFile = new File(file.getPath(), fileName);
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
                if (result != null) {
                    result.onSaveSuccess(imageFile, fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (result != null) {
                    result.onSaveFailed("HUNO_ERROR", e.getMessage());
                }
            } finally {
                if (outStream != null) {
                    try {
                        outStream.flush();
                        outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (result != null) {
                result.onSaveFailed("HUNO_SDCARD_INVALIBLE", "sd卡不可用");
            }
        }
    }


    public static String toZip(String inFilePath) {
        ZipOutputStream zos = null;
        File srcFile = new File(inFilePath);
        if (srcFile == null || !srcFile.exists()) {
            return null;
        }
        File outFile = new File(srcFile.getParentFile().getAbsolutePath(), EncryptUtil.md5(inFilePath) + ".zip");
        if (outFile.exists()) {
            return outFile.getAbsolutePath();
        }
        FileInputStream in = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(outFile));
            zos.putNextEntry(new ZipEntry(srcFile.getName()));
            byte[] buf = new byte[1024];
            int len;
            in = new FileInputStream(srcFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (zos != null) {
                    zos.closeEntry();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outFile.getAbsolutePath();
    }


    /**
     * 解压缩文件
     *
     * @param cycleHandler
     * @param zipPath
     * @param unZipPath
     */
    public static void unZipFile(final Handler cycleHandler, final String zipPath, final String unZipPath) {
        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipPath));
                    ZipEntry zipEntry;
                    String szName = "";
                    while ((zipEntry = inZip.getNextEntry()) != null) {
                        szName = zipEntry.getName();
                        if (zipEntry.isDirectory()) { //获取部件的文件夹名
                            szName = szName.substring(0, szName.length() - 1);
                            File folder = new File(unZipPath + File.separator + szName);
                            folder.mkdirs();
                        } else {
                            File file = new File(unZipPath + File.separator + szName);
                            if (!file.exists()) {
                                file.getParentFile().mkdirs();
                                file.createNewFile();
                            } // 获取文件的输出流
                            FileOutputStream out = new FileOutputStream(file);
                            int len;
                            byte[] buffer = new byte[1024]; // 读取（字节）字节到缓冲区
                            while ((len = inZip.read(buffer)) != -1) { // 从缓冲区（0）位置写入（字节）字节
                                out.write(buffer, 0, len);
                                out.flush();
                            }
                            out.close();
                        }
                    }
                    inZip.close();
                    if (cycleHandler != null) {
                        deleteFile(new File(zipPath));
                        cycleHandler.sendEmptyMessage(UNZIP_SUCC);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (cycleHandler != null) {
                        cycleHandler.sendEmptyMessage(UNZIP_ERROR);
                    }
                }
            }
        });

    }

    /**
     * 获取SD卡路径,当SD卡不存在时返回内部存储路径
     */
    public static String getExternalFilesDir() {
        String path;
        File file = BaseApplication.CONTEXT.getExternalFilesDir(null);
        if (file != null) {
            path = file.getAbsolutePath() + File.separator;
        } else {
            path = getInternalFilesDir();
        }
        return path;
    }

    public static File getAndroidRPicturePath() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

    /**
     * 获取内部存储位置
     *
     * @return
     */
    public static String getInternalFilesDir() {
        return BaseApplication.CONTEXT.getFilesDir() + File.separator;
    }


    /**
     * 保存裁剪之后的图片数据
     */
    @SuppressWarnings({"AccessStaticViaInstance", "ResultOfMethodCallIgnored"})
    public static String setImageToView(Context context, Intent data) {
        String avatarUrl = null;
        Bundle extras = data.getExtras();
        if (extras != null) {
            String fileName;
            String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
            Bitmap bitmap = extras.getParcelable("data");
            FileOutputStream b = null;
            String s = String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            File file = new File(s + "/myImage/");
            // 创建文件夹
            file.mkdirs();
            fileName = s + "/myImage/" + name;
            try {
                b = new FileOutputStream(fileName);
                assert bitmap != null;
                // 把数据写入文件
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert b != null;
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            avatarUrl = ImageUtils.bitmapToBase64(bitmap);
            bitmap.recycle();
        }
        return avatarUrl;
    }

    /**
     * 文件下载
     *
     * @param cycleHandler 文件下载回调
     * @param filePath     文件路径
     * @param extension    文件后缀名
     * @param fileUrl      文件下载地址
     */
    public static void downLoadFile(final Handler cycleHandler,
                                    final String filePath, final String extension, final String fileUrl) {
        if (cycleHandler == null) {
            return;
        }
        //根据url处理命名
        String fileName = EncryptUtil.md5(fileUrl);
        final File file = new File(filePath, fileName + extension);
        //如果文件存在则直接返回成功
        if (file.exists()) {
            if (file.length() <= 0) {
                cycleHandler.sendEmptyMessage(DOWN_LOAD_ERROR);
            } else {
                Message message = cycleHandler.obtainMessage();
                message.what = DOWN_LOAD_SUCC;
                message.obj = file.getAbsolutePath();
                cycleHandler.sendMessage(message);
            }
            return;
        } else {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
        }
        ExecutorService executorService = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                //Logger.e("开始下载图片" + System.currentTimeMillis());
                try {
                    //构造URL
                    URL url = new URL(fileUrl);
                    //打开连接
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.setRequestMethod("GET");
                    //设置请求的字符编码
                    con.setRequestProperty("Charset", "utf-8");
                    //设置connection打开链接资源
                    con.setConnectTimeout(10 * 1000);
                    con.setReadTimeout(10 * 1000);
                    con.connect();
                    FileOutputStream outputStream = new FileOutputStream(file);
                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        //得到服务器响应的输入流
                        InputStream inputStream = con.getInputStream();
                        //创建缓冲输入流对象，相对于inputStream效率要高一些
                        BufferedInputStream bfi = new BufferedInputStream(inputStream);
                        //此处的len表示每次循环读取的内容长度
                        int len;
                        //bytes是用于存储每次读取出来的内容
                        byte[] bytes = new byte[1024];
                        while ((len = bfi.read(bytes)) != -1) {
                            //通过文件输出流写入从服务器中读取的数据
                            outputStream.write(bytes, 0, len);
                        }
                        //关闭打开的流对象
                        outputStream.close();
                        inputStream.close();
                        bfi.close();
                        if (cycleHandler != null) {
                            Message message = cycleHandler.obtainMessage();
                            message.what = DOWN_LOAD_SUCC;
                            message.obj = file.getAbsolutePath();
                            cycleHandler.sendMessage(message);
                        }
                        //Logger.e("下载完成");
                    } else {
                        //Logger.e("下载失败");
                        if (cycleHandler != null) {
                            cycleHandler.sendEmptyMessage(DOWN_LOAD_ERROR);
                        }
                    }
                } catch (Exception e) {
                    //Logger.e("下载失败" + e.toString());
                    if (file.exists()) {
                        file.delete();
                    }
                    if (cycleHandler != null) {
                        cycleHandler.sendEmptyMessage(DOWN_LOAD_ERROR);
                    }
                }
            }
        });
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String
     * @param newPath String
     * @return boolean
     */
    public static void copyFile(Handler cycleHandler, String oldPath, String newPath, String newName) {
        try {
            int byteread = 0;
            File newfile = new File(newPath);
            //文件存在时
            if (!newfile.exists()) {
                newfile.createNewFile();
            }
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                //读入原文件
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath + File.separator + newName);
                byte[] buffer = new byte[1024];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                if (cycleHandler != null) {
                    Message message = cycleHandler.obtainMessage();
                    message.what = COPY_FILE_SUCC;
                    message.obj = newPath + File.separator + newName;
                    cycleHandler.sendMessage(message);
                }
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cycleHandler != null) {
            cycleHandler.sendEmptyMessage(COPY_FILE_ERROR);
        }
    }

    /**
     * 获取文件长度
     */
    public static long getFileSize(File file) {
        if (file != null && file.exists() && file.isFile()) {
            String fileName = file.getName();
            System.out.println("文件" + fileName + "的大小是：" + file.length() + "--" + file.length() / 1024 + "KB");
            return file.length();
        }
        return 0;
    }

    /**
     * 将单张图片压缩到指定大小以内
     *
     * @param size     指定需要压缩的大小 <= size * 1024
     * @param dirPath  压缩后保存文件夹的目录
     * @param filePath 需要压缩的文件
     * @return 压缩后文件的路径path
     */
    public static String compressToMaxSize(int size, String dirPath, String filePath) {
        if (size <= 0) {
            return null;
        }
        long maxImageSize = size * MemoryConstants.KB;
        File newFile = PhotographUtils.getCameraFile(dirPath, "compress_" + System.currentTimeMillis() + ".jpg");
        Bitmap src = ImageUtils.compressByQuality(ImageUtils.getBitmap(filePath), maxImageSize, false);
        ImageUtils.save(src, newFile, Bitmap.CompressFormat.JPEG, true);
        boolean compress = true;
        while (true) {
            if (FileUtils.getFileSize(newFile) > maxImageSize) {
                Bitmap src1;
                if (compress) {
                    src1 = ImageUtils.compressByScale(ImageUtils.getBitmap(newFile), 0.5F, 0.5F);
                } else {
                    src1 = ImageUtils.compressByQuality(ImageUtils.getBitmap(newFile), 50);
                }
                compress = !compress;
                //Bitmap src1 = ImageUtils.compressBySampleSize(ImageUtils.getBitmap(newFile), 2);
                if (newFile.delete()) {
                    newFile = PhotographUtils.getCameraFile(dirPath, "compress_" + System.currentTimeMillis() + ".jpg");
                }
                ImageUtils.save(src1, newFile, Bitmap.CompressFormat.JPEG, true);
            } else {
                break;
            }
        }
        return newFile.getAbsolutePath();
    }


    /**
     * 通过URI获取文件路径
     */
    public static String getFilePathByUri(Context context, Uri uri) {
        String path = null;
        if (uri == null) {
            //Logger.i("tag", "The uri is not exist.");
            return path;
        }
        path = getFilePath(context, uri);
        if (path == null) {
            path = getFilePath2(context, uri);
        }
        return path;
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    private static String getFilePath2(final Context context, final Uri uri) {
        if (null == uri) {
            return null;
        }
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index == -1) {
                        index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    }
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 专为Android4.4以上设计的从Uri获取文件路径
     */
    private static String getFilePath(Context context, Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                //Logger.d("isExternalStorageDocument");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                //Logger.d("isDownloadsDocument");
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                //Logger.d("isMediaDocument");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }// MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static void saveBytesToFile(String filePath, byte[] data) {
        File file = new File(FileUtils.getExternalFilesDir(), "imageByte.txt");
        BufferedOutputStream outStream = null;
        try {
            outStream = new BufferedOutputStream(new FileOutputStream(file));
            outStream.write(data);
            outStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != outStream) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据byte数组，生成文件
     */
    public static void getFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void updatePhotoAlbum(final Context mContext, File file) {
        String type = getMimeType(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
            values.put(MediaStore.MediaColumns.MIME_TYPE, type);
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM);
            ContentResolver contentResolver = mContext.getContentResolver();
            Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                return;
            }
            try {
                OutputStream out = contentResolver.openOutputStream(uri);
                FileInputStream fis = new FileInputStream(file);
                android.os.FileUtils.copy(fis, out);
                fis.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            try {
                MediaStore.Images.Media.insertImage(mContext.getContentResolver(), file.getAbsolutePath(), file.getName(), "");
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getParent())));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //以下方案在vivo/oppo做过尝试，无效，分别为8 和 9
//            Uri uri = Uri.fromFile(file);
//            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
//            mContext.sendBroadcast(intent);

//            MediaScannerConnection mMediaScanner = new MediaScannerConnection(mContext, null);
//
//            mMediaScanner.connect();
//
//            if (mMediaScanner !=null && mMediaScanner.isConnected()) {
//
//                mMediaScanner.scanFile(file.getAbsolutePath(), type);

//            MediaScannerConnection.scanFile(mContext.getApplicationContext(), new String[]{file.getAbsolutePath()}, new String[]{type}, new MediaScannerConnection.OnScanCompletedListener() {
//                @Override
//                public void onScanCompleted(String path, Uri uri) {
//                    Logger.e("=============onScanCompleted");
////                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
////                    mediaScanIntent.setData(uri);
////                    mContext.sendBroadcast(mediaScanIntent);
//                }
//            });
        }
    }


    public static String getMimeType(File file) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String type = fileNameMap.getContentTypeFor(file.getName());
        return type;
    }
}
