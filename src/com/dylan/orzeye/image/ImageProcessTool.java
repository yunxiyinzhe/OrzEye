package com.dylan.orzeye.image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.DisplayMetrics;

public class ImageProcessTool {

	public static Bitmap getOCRBitmapFromCamera(byte[] data, Camera camera,
			DisplayMetrics dm) {
		Size imageSize = camera.getParameters().getPreviewSize();
		int imageFormat = camera.getParameters().getPreviewFormat();
		Bitmap mBitmap = null;
		Bitmap rectBitmap = null;
		YuvImage image = new YuvImage(data, imageFormat, imageSize.width,
				imageSize.height, null);
		if (image != null) {
			try {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				image.compressToJpeg(new Rect(0, 0, imageSize.width,
						imageSize.height), 100, stream);
				mBitmap = BitmapFactory.decodeByteArray(stream.toByteArray(),
						0, stream.size());
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Matrix matrix = new Matrix();
			matrix.postRotate((float) 90.0);
			Bitmap rotaBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
					mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
			int width = dm.widthPixels;
			int height = dm.heightPixels;
			double heightRatio = (double) rotaBitmap.getHeight()
					/ (double) height;
			double widthRatio = (double) rotaBitmap.getWidth()
					/ (double) width;

			rectBitmap = Bitmap.createBitmap(rotaBitmap,
					(int) (width/5 * widthRatio), (int) (height/8 * heightRatio),
					(int) ((width - 2*width/5) * widthRatio),
					(int) (height/8 * heightRatio));
		}
		return rectBitmap;
	}

}
