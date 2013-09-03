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
			
			Rect centerObjectRect = getCenterObjectRect(ocrBitmap);
			if(centerObjectRect == null) {
				return "";
			}
			tessOCRApi.setImage(Bitmap.createBitmap(ocrBitmap,
					centerObjectRect.left,
					centerObjectRect.top, 
					centerObjectRect.width(),
					centerObjectRect.height()));
			String recognizedText = tessOCRApi.getUTF8Text();
			return recognizedText;
		} catch (Exception e) {
		
		}
		tessOCRApi.end();
		return "";

	}

	private Rect getCenterObjectRect(Bitmap ocrBitmap) {
		tessOCRApi.setImage(ocrBitmap);
		List<Rect> wordsRects = tessOCRApi.getWords().getBoxRects();
		int objectIndex = 0;
		for(Rect rect :wordsRects) {
			if(rect.contains(ocrBitmap.getWidth()/2, ocrBitmap.getHeight()/2)) {
				break;
			}
			objectIndex ++;	
		}
		if(objectIndex == wordsRects.size()) {
			return null;
		}
		else {
			return wordsRects.get(objectIndex);
		}
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
