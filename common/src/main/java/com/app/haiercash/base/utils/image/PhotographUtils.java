package com.app.haiercash.base.utils.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.crop.Crop;
import com.app.haiercash.base.BaseApplication;
import com.app.haiercash.base.utils.FileUtils;

import java.io.File;

/**
 * ================================================================
 * 作    者：stone
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn
 * 版    本：1.0
 * 创建日期：2017/11/16
 * 描    述：拍照工具类
 * 修订历史：
 * ================================================================
 */
public class PhotographUtils {
    public static final String IMAGE_FILE_NAME = "faceImage.jpg";

    public static final int THECAMERA = 222;//相机
    public static final int PHOTOALBUM = 333;//相册
    public static final int THEEDITOR = 555;//图片裁剪

    public static final int PHOTOALBUM_WEB_JS = 777;//提供JS跳转的


    /**
     * 裁剪图片方法实现
     */
    public static void startPhotoZoom(Activity activity, Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
            return;
        }
        Crop.of(uri).withAspect(1, 1)
                .withMaxSize(320, 320)
                .start(activity, THEEDITOR);
    }

    /**
     * 打开摄像机拍照
     */
    public static void startCameraCapture(Object activityOrFragment) {
        startCameraCapture(activityOrFragment, IMAGE_FILE_NAME);
    }

    /**
     * 打开摄像机拍照
     */
    public static void startCameraCapture(Object activityOrFragment, String fileName) {
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (activityOrFragment instanceof Fragment) {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                    getCameraUri(((Fragment) activityOrFragment).getContext(), fileName));
            ((Fragment) activityOrFragment).startActivityForResult(intentFromCapture, THECAMERA);
        } else {
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                    getCameraUri((Activity) activityOrFragment, fileName));
            ((Activity) activityOrFragment).startActivityForResult(intentFromCapture, THECAMERA);
        }
    }

    public static void startPhotoAlbum(Object activityOrFragment) {
        startPhotoAlbum(activityOrFragment, false);
    }

    /**
     * 打开相册
     */
    public static void startPhotoAlbum(Object activityOrFragment, boolean isFromJs) {
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*"); // 设置文件类型
        intentFromGallery.setAction(Intent.ACTION_PICK);
        if (!isFromJs) {
            if (activityOrFragment instanceof Activity) {
                ((Activity) activityOrFragment).startActivityForResult(intentFromGallery, PHOTOALBUM);
            } else if (activityOrFragment instanceof Fragment) {
                ((Fragment) activityOrFragment).startActivityForResult(intentFromGallery, PHOTOALBUM);
            }
        } else {
            if (activityOrFragment instanceof Activity) {
                ((Activity) activityOrFragment).startActivityForResult(intentFromGallery, PHOTOALBUM_WEB_JS);
            } else if (activityOrFragment instanceof Fragment) {
                ((Fragment) activityOrFragment).startActivityForResult(intentFromGallery, PHOTOALBUM_WEB_JS);
            }
        }
    }

    /**
     * 拍照 获取File
     */
    public static File getCameraFile(String dir, String fileName) {
        ImageUtils.createOrExistsDir(new File(dir));
        return new File(FileUtils.getExternalFilesDir() + dir, fileName);
    }

    /**
     * 拍照 获取File
     */
    public static File getCameraFile(String fileName) {
        return new File(FileUtils.getExternalFilesDir(), fileName);
    }

    /**
     * 拍照 获取uri
     */
    public static Uri getCameraUri(Context context, String fileName) {
        File tempFile = getCameraFile(fileName);
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context != null ? context : BaseApplication.CONTEXT, "com.haiercash.gouhua.fileprovider", tempFile);
        } else {
            fileUri = Uri.fromFile(tempFile);
        }
        return fileUri;
    }
}
