package com.dylan.orzeye.ocr;

import com.googlecode.tesseract.android.TessBaseAPI;

import android.graphics.Bitmap;
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
		tessOCRApi.setImage(ocrBitmap);
		try {
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
