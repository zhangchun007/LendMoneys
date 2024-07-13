package com.haiercash.gouhua.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.app.haiercash.base.interfaces.PicturesToChoose;
import com.app.haiercash.base.utils.image.PhotographUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;

import io.reactivex.functions.Consumer;


/**
 * Created by use on 2017/6/14.
 * heq
 * 照片获取途径
 */

public class UtilPhoto<T extends PicturesToChoose> {
    /**
     * 0 : 拍照和选择照片都需要<p/>
     * 1: 只允许拍照<p/>
     * 2: 只允许选择照片<p/>
     */
    private int selectType = 0;
    private View view;
    private AlertDialog adToast;

    public UtilPhoto() {
    }

    /**
     * @param selectType 0 : 拍照和选择照片都需要<br/>
     *                   1: 只允许拍照<br/>
     *                   2: 只允许选择照片<br/>
     */
    public UtilPhoto(int selectType) {
        this.selectType = selectType;
    }

    public void showDialog(final BaseActivity context, String fileName) {
        final String fName = CheckUtil.isEmpty(fileName) ? null : fileName;
        showDialog(context, (code, name) -> {
            switch (code) {
                //拍照
                case "1":
                    context.requestPermission((Consumer<Boolean>) aBoolean -> {
                        if (aBoolean) {
                            PhotographUtils.startCameraCapture(context, fName);
                        } else {
                            context.showDialog("请授权“相机”权限");
                        }
                    }, R.string.permission_camera, Manifest.permission.CAMERA);
                    break;
                //从相册库中选择
                case "2":
                    context.requestPermission((Consumer<Boolean>) aBoolean -> {
                        if (aBoolean) {
                            PhotographUtils.startPhotoAlbum(context);
                        } else {
                            context.showDialog("请授权手机存储权限");
                        }
                    }, R.string.permission_storage, Manifest.permission.READ_EXTERNAL_STORAGE);
                    break;
                default:
                    break;
            }
        });
    }

    public void showDialog(Context context, final PicturesToChoose picView) {
        showDialog(context, picView, false);
    }

    /***
     * 选择相机拍照还是从相册中选择
     */
    public void showDialog(Context context, final PicturesToChoose picView, boolean canceledOnTouchOutside) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        adToast = builder.create();
        view = View.inflate(context, R.layout.pictures_to_choose_dialog, null);
        Window window = adToast.getWindow();
        if( window != null){
            window.getDecorView().setPadding(0, 0, 0, 0);
            window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = SystemUtils.getDeviceWidth(context);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setGravity(Gravity.BOTTOM);
        }
        adToast.show();
        adToast.setContentView(view);
        adToast.setCanceledOnTouchOutside(canceledOnTouchOutside);

        if (selectType == 0) {
            view.findViewById(R.id.tvTakingPictures).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tvPhotoAlbum).setVisibility(View.VISIBLE);
        } else if (selectType == 1) {
            view.findViewById(R.id.tvTakingPictures).setVisibility(View.VISIBLE);
            view.findViewById(R.id.tvPhotoAlbum).setVisibility(View.GONE);
        } else if (selectType == 2) {
            view.findViewById(R.id.tvTakingPictures).setVisibility(View.GONE);
            view.findViewById(R.id.tvPhotoAlbum).setVisibility(View.VISIBLE);
        }
        view.findViewById(R.id.tvTakingPictures).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //拍照
                adToast.dismiss();
                picView.setPicturesToChoose("1", "拍照");
            }
        });

        view.findViewById(R.id.tvPhotoAlbum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //从相册库选择
                adToast.dismiss();
                picView.setPicturesToChoose("2", "从相册中选择一张");
            }
        });

        view.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //取消
                adToast.dismiss();
            }
        });
    }

    /**
     * 获取color的最大差值
     */
    public static int getMaxDiff(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int diffRG = Math.abs(red - green);
        int diffRB = Math.abs(red - blue);
        int diffGB = Math.abs(green - blue);
        int max = diffRG;
        if (diffRB > max) {
            max = diffRB;
        }
        if (diffGB > max) {
            max = diffGB;
        }
        return max;
    }
}
