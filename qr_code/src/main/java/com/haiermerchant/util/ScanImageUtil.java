package com.haiermerchant.util;

import android.content.res.Resources;
import android.graphics.Bitmap;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

public class ScanImageUtil {
    public static com.google.zxing.Result[] decodeQR(Bitmap srcBitmap) {

        com.google.zxing.Result[] result = null;
        if (srcBitmap != null) {
            int width = srcBitmap.getWidth();
            int height = srcBitmap.getHeight();
            int[] pixels = new int[width * height];
            srcBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            // 新建一个RGBLuminanceSource对象
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            // 将图片转换成二进制图片
            BinaryBitmap binaryBitmap = new BinaryBitmap(new
                    GlobalHistogramBinarizer(source));
            QRCodeMultiReader reader = new QRCodeMultiReader();// 初始化解析对象
            try {
                result = reader.decodeMultiple(binaryBitmap,
                        CodeHints.getDefaultDecodeHints());// 解析获取一个Result数组
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
