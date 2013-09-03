package com.dylan.orzeye;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Window;

public class SplashActivity extends Activity {
	public final String SD_PATH = Environment
			.getExternalStorageDirectory().toString() ;
	public  String APP_PATH ;
	public  String OCR_DATA_PATH ;
	public  String DIC_DATA_PATH;
	private String DIC_DATA_FILENAME;
	private String OCR_DATA_FILENAME;

	private final int SPLASH_DISPLAY_LENGHT = 3000;
	private boolean isOCRDataExisted = false;
	private boolean isDictionaryDataExisted = false;
	
	public SplashActivity() {
		APP_PATH = SD_PATH + getString(R.string.app_path);
		OCR_DATA_PATH = APP_PATH + getString(R.string.ocrdata_path);
		DIC_DATA_PATH = APP_PATH + getString(R.string.dicdata_path);
		DIC_DATA_FILENAME = getString(R.string.dicdata_filename);
		OCR_DATA_FILENAME = getString(R.string.ocrdata_filename);
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		if (isDataFileExisted()) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent mainIntent = new Intent(SplashActivity.this,
							CameraActivity.class);
					SplashActivity.this.startActivity(mainIntent);
					SplashActivity.this.finish();
				}
			}, SPLASH_DISPLAY_LENGHT);
		} else {
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle(getString(R.string.installdata_msg));
			dialog.setMessage(getString(R.string.waiting_msg));
			dialog.show();
			final UpdateUIHandler handler = new UpdateUIHandler(dialog, SplashActivity.this);

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					installDataFile();
					handler.sendEmptyMessage(0);
					Intent mainIntent = new Intent(SplashActivity.this,
							CameraActivity.class);
					SplashActivity.this.startActivity(mainIntent);
					SplashActivity.this.finish();
				}
			});
			thread.start();
		}
	}

	private void installDataFile() {
		if (!(new File(APP_PATH)).exists()) {
			(new File(APP_PATH)).mkdir();
		}

		if (!isOCRDataExisted) {
			copyDataFileToAppFolder(OCR_DATA_PATH, OCR_DATA_FILENAME, R.raw.eng);
		}

		if (!isDictionaryDataExisted) {
			copyDataFileToAppFolder(DIC_DATA_PATH, DIC_DATA_FILENAME,
					R.raw.dictionary);
		}
	}

	private boolean isDataFileExisted() {
		if ((new File(OCR_DATA_PATH + OCR_DATA_FILENAME)).exists()) {
			isOCRDataExisted = true;
		}

		if ((new File(DIC_DATA_PATH + DIC_DATA_FILENAME)).exists()) {
			isDictionaryDataExisted = true;
		}

		return isOCRDataExisted && isDictionaryDataExisted;
	}

	private void copyDataFileToAppFolder(String filePath, String fileName,
			int source) {
		if (!(new File(filePath)).exists()) {
			(new File(filePath)).mkdir();
		}

		try {
			InputStream is = getResources().openRawResource(source);
			FileOutputStream fos;
			fos = new FileOutputStream(filePath + fileName);
			byte[] buffer = new byte[8192];
			int count = 0;
			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onBackPressed() {
		// do nothing
	}

	static class UpdateUIHandler extends Handler {
		private ProgressDialog dialog;
		WeakReference<SplashActivity> mActivity;
		
		UpdateUIHandler(ProgressDialog dialog, SplashActivity activity) {
			this.dialog = dialog;
			mActivity = new WeakReference<SplashActivity>(activity);
		}
		public void handleMessage(android.os.Message msg) {
			if(dialog != null) {
				dialog.cancel();
			}
		}
	
	}
}
