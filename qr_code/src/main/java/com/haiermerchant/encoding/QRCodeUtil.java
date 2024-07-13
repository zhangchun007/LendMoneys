package com.haiermerchant.encoding;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author Ryan Tang
 *有图片二维码生成工具类
 */
public final class QRCodeUtil {
	private static final int BLACK = 0xff000000;
	private static final int PADDING_SIZE_MIN = 10; // 最小留白长度, 单位: px

	public static Bitmap createQRCode(String str, int widthAndHeight)
	{
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "iso-8859-1");
		try
		{
			str = new String(str.getBytes("UTF-8"), "GBK");
			BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			int[] pixels = new int[width * height];

			boolean isFirstBlackPoint = false;
			int startX = 0;
			int startY = 0;

			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					if (matrix.get(x, y))
					{
						if (isFirstBlackPoint == false)
						{
							isFirstBlackPoint = true;
							startX = x;
							startY = y;
						}
						pixels[y * width + x] = BLACK;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

			// 剪切中间的二维码区域，减少padding区域
			if (startX <= PADDING_SIZE_MIN) {
                return bitmap;
            }

			int x1 = startX - PADDING_SIZE_MIN;
			int y1 = startY - PADDING_SIZE_MIN;
			if (x1 < 0 || y1 < 0) {
                return bitmap;
            }

			int w1 = width - x1 * 2;
			int h1 = height - y1 * 2;

			Bitmap bitmapQR = Bitmap.createBitmap(bitmap, x1, y1, w1, h1);

			return bitmapQR;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 生成二维码Bitmap
	 *
	 * @param content   内容
	 * @param widthPix  图片宽度
	 * @param heightPix 图片高度
	 * @param logoBm    二维码中心的Logo图标（可以为null）
	 * @return 生成二维码及保存文件是否成功
	 */
	public static Bitmap createQRImage(String content, int widthPix, int heightPix, Bitmap logoBm) {
		Bitmap bitmap = null;
		try {
			if (content == null || "".equals(content)) {
				return null;
			}

			//配置参数
			Map<EncodeHintType, Object> hints = new HashMap<>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			//容错级别
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			//设置空白边距的宽度
//            hints.put(EncodeHintType.MARGIN, 2); //default is 4

			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
			int[] pixels = new int[widthPix * heightPix];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < heightPix; y++) {
				for (int x = 0; x < widthPix; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * widthPix + x] = 0xff000000;
					} else {
						pixels[y * widthPix + x] = 0xffffffff;
					}
				}
			}

			// 生成二维码图片的格式，使用ARGB_8888
			bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

			if (logoBm != null) {
				bitmap = addLogo(bitmap, logoBm);
			}

			//必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	/**
	 * 在二维码中间添加Logo图案
	 */
	private static Bitmap addLogo(Bitmap src, Bitmap logo) {
		if (src == null) {
			return null;
		}

		if (logo == null) {
			return src;
		}

		//获取图片的宽高
		int srcWidth = src.getWidth();
		int srcHeight = src.getHeight();
		int logoWidth = logo.getWidth();
		int logoHeight = logo.getHeight();

		if (srcWidth == 0 || srcHeight == 0) {
			return null;
		}

		if (logoWidth == 0 || logoHeight == 0) {
			return src;
		}

		//logo大小为二维码整体大小的1/5
		float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
		Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
		try {
			Canvas canvas = new Canvas(bitmap);
			canvas.drawBitmap(src, 0, 0, null);
			canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
			canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);

			canvas.save();
			canvas.restore();
		} catch (Exception e) {
			bitmap = null;
			e.getStackTrace();
		}

		return bitmap;
	}

	/**
	 * 生成条形码
	 * @param contents 内容
	 * @param desiredWidth 图片宽度
	 * @param desiredHeight 图片高度
	 * @return
	 */
	public static Bitmap encodeAsBitmap(String contents, int desiredWidth, int desiredHeight) {
		final int WHITE = 0xFFFFFFFF;
		final int BLACK = 0xFF000000;

		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix result = null;
		try {
			result = writer.encode(contents, BarcodeFormat.CODE_128, desiredWidth,
					desiredHeight, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int width = result.getWidth();
		int height = result.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			int offset = y * width;
			for (int x = 0; x < width; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
}
