package com.dylan.orzeye.ocr;

import java.util.List;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;

public class OCRTool {
	private TessBaseAPI tessOCRApi = null;

	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/OrzEye/";

	public static final String lang = "eng";

	public OCRTool() {
		initOcrEngine();
	}

	public String OCRStart(Bitmap ocrBitmap) {
		try {
			tessOCRApi.setImage(ocrBitmap);
			List<Rect> boxRects = tessOCRApi.getWords().getBoxRects();
			int objectIndex = 0;
			for(Rect boxRect : boxRects ) {
				if(boxRect.contains(ocrBitmap.getWidth()/2, ocrBitmap.getHeight()/2)) {
					break;
				}
				objectIndex ++;
			}
			if(objectIndex == boxRects.size()) {
				return "";
			}
			Rect objectBoxRect = boxRects.get(objectIndex);
			tessOCRApi.setImage(Bitmap.createBitmap(ocrBitmap, objectBoxRect.left, objectBoxRect.top, objectBoxRect.width(), objectBoxRect.height()));
			String recognizedText = tessOCRApi.getUTF8Text();
			return recognizedText;
		} catch (Exception e) {
			// TODO: handle exception
		}
		tessOCRApi.end();
		return "";

	}

	private void initOcrEngine() {
		tessOCRApi = new TessBaseAPI();
		tessOCRApi.setDebug(true);
		try {
			tessOCRApi.init(DATA_PATH, lang);
		} catch (IllegalArgumentException e) {
			tessOCRApi = null;
		}
	}

	public boolean isOCREngineReasy() {
		return tessOCRApi == null ? false : true;
	}
}
