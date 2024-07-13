package com.haiercash.gouhua.fragments;

import android.graphics.Bitmap;
import android.net.Uri;

import com.app.haiercash.base.utils.image.ImageUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.interfaces.IScanResult;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiermerchant.activity.ZXingFragment;
import com.haiermerchant.util.ScanImageUtil;

/**
 * ================================================================
 * 作    者：stone<p/>
 * 邮    箱：shixiangfei@haiercash.com<p/>
 * 版    本：1.0<p/>
 * 创建日期：2021/7/22<p/>
 * 描    述：<p/>
 * 修订历史：<br/>
 * ================================================================
 */
public class ScanQrCodeFragment extends ZXingFragment {
    private IScanResult iResult;

    public void setIResult(IScanResult iResult) {
        this.iResult = iResult;
    }

    public void continueToScan() {
        restartPreview();
    }

    //处理图库选择的图片
    public void dealImg(Uri uri) {
        try {
            Bitmap result = ImageUtils.decodeUri(getContext(), uri, 720, 1280);
            com.google.zxing.Result[] resultString = ScanImageUtil.decodeQR(result);
            if (resultString != null) {
                if (resultString.length > 1) {
                    UiUtil.toast("单次仅允许扫描一个二维码");
                } else if (resultString.length == 1) {
                    String scanTv = resultString[0].getText().toString();
                    if (CheckUtil.isEmpty(scanTv)) {
                        UiUtil.toast("获取二维码失败");
                    } else {
                        if (iResult != null) {
                            iResult.scanResults(scanTv);
                        }
                    }
                } else {
                    UiUtil.toast("获取二维码失败");
                }
            }
            result.recycle();
        } catch (Exception e) {
            UiUtil.toast("获取二维码失败");
        }
    }

    @Override
    protected void successful(String str) {
        super.successful(str);
        if (iResult != null) {
            iResult.scanResults(str);
        }
    }
}
