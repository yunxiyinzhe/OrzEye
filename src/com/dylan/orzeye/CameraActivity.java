package com.dylan.orzeye;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Locale;

import com.dylan.orzeye.dictionary.DictionaryTool;
import com.dylan.orzeye.dictionary.YoudaoJsonParser;
import com.dylan.orzeye.dictionary.YoudaoTranslater;
import com.dylan.orzeye.image.ImageProcessTool;
import com.dylan.orzeye.ocr.OCRTool;
import com.dylan.orzeye.ui.MainTabsActivity;
import com.dylan.orzeye.ui.WebTranslationActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.DisplayMetrics;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity implements SurfaceHolder.Callback,
		PreviewCallback {
	private boolean isPreview = false;
	private SurfaceView mPreviewSV = null;
	private SurfaceHolder mSurfaceHolder = null;
	private Camera mCamera = null;
	private static TextView recognizedView = null;
	private static TextView translatedView = null;
	private ImageButton triggerButton = null;
	private ImageButton dicWebSearchButton = null;
	private ImageButton addNotesButton = null;
	private ImageButton overflowMenu_Button = null;
	private DisplayMetrics dm = null;

	private final static String[] recognizedText = { new String("") };
	
	private OCRTool mOCRTool;
	private DictionaryTool mDictionaryTool;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camera);

		mPreviewSV = (SurfaceView) findViewById(R.id.surfaceView);
		mPreviewSV.setZOrderOnTop(false);
		mSurfaceHolder = mPreviewSV.getHolder();
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		mSurfaceHolder.addCallback(this);

		triggerButton = (ImageButton) findViewById(R.id.TriggerButton);
		triggerButton.setOnClickListener(new TriggerButtonOnClickListener());

		dicWebSearchButton = (ImageButton) findViewById(R.id.web_search_btn);
		dicWebSearchButton
				.setOnClickListener(new WebSearchButtonOnClickListener());

		addNotesButton = (ImageButton) findViewById(R.id.notes_add_btn);
		addNotesButton
				.setOnClickListener(new AddNotesButtonOnClickListener());
		recognizedView = (TextView) findViewById(R.id.RecognizedView);
		translatedView = (TextView) findViewById(R.id.TranslatedView);
		
		overflowMenu_Button = (ImageButton) findViewById(R.id.Overflow_Menu);
		overflowMenu_Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PopupMenu overflowMneu = new PopupMenu(getBaseContext(), v);
				MenuInflater inflater = overflowMneu.getMenuInflater();
				inflater.inflate(R.menu.overflow_menu, overflowMneu.getMenu());
				
				overflowMneu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Intent intent = new Intent();
						switch (item.getItemId()) {
						case R.id.notes_item:
							intent.setClass(CameraActivity.this, MainTabsActivity.class);
							intent.putExtra("position", 0);
							break;
							
						case R.id.dictionary_item:
							intent.setClass(CameraActivity.this, MainTabsActivity.class);
							intent.putExtra("position", 1);
							break;
							
						default:
							break;
						}
						
						startActivity(intent);
						return true;
					}
				});
				overflowMneu.show();
				
			}
		});
		
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mOCRTool = new OCRTool();
		mDictionaryTool = new DictionaryTool();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		initCamera();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			if (null != mCamera) {
				mCamera.release();
				mCamera = null;
			}
			e.printStackTrace();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		if (null != mCamera) {
			mCamera.setOneShotPreviewCallback(null);

			mCamera.stopPreview();
			isPreview = false;
			mCamera.release();
			mCamera = null;
		}
	}

	private void initCamera() {
		if (isPreview) {
			mCamera.stopPreview();
		}
		if (null != mCamera) {
			Camera.Parameters mCameraParam = mCamera.getParameters();
			mCameraParam.setPictureFormat(ImageFormat.JPEG);
			mCameraParam.setPreviewSize(mCameraParam.getSupportedPreviewSizes()
					.get(1).width,
					mCameraParam.getSupportedPreviewSizes().get(1).height);
			mCamera.setDisplayOrientation(90);
			mCameraParam
					.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			mCamera.setParameters(mCameraParam);
			mCamera.startPreview();
			isPreview = true;
		}
	}

	public DisplayMetrics getDisplayMetrics() {
		return dm;
	}

	@Override
	public void onPreviewFrame(final byte[] data, final Camera camera) {
		if (null != data) {
			enableTriggerButton(false);

			ProgressDialog dialog = showProgressDialog(getString(R.string.ocrprogressdlg_title),
					getString(R.string.waiting_msg));
			final UpdateUIHandler handler = new UpdateUIHandler(dialog, CameraActivity.this);
			
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					String translatedText;
					Bitmap ocrBitmap = ImageProcessTool.getOCRBitmapFromCamera(
							data, camera, dm);
					if (mOCRTool.isOCREngineReasy()
							&& mDictionaryTool.isDictionaryReady()) {
						recognizedText[0] = mOCRTool.OCRStart(ocrBitmap);
						translatedText = mDictionaryTool
								.lookUpDictionary(recognizedText[0]
										.toLowerCase(Locale.getDefault()));
					} else {
						translatedText = getString(R.string.datanotfound_Msg);
					}
					Message msg = new Message();
					Bundle bundle = new Bundle();
					bundle.putString("translated", translatedText);
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
			});
			thread.start();

			enableTriggerButton(true);

		}
	}

	private void enableTriggerButton(boolean enable) {
		triggerButton
				.setOnClickListener(enable ? new TriggerButtonOnClickListener()
						: null);
		triggerButton.setEnabled(enable);
	}

	long waitTime = 2000;
	long touchTime = 0;

	@Override
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - touchTime) >= waitTime) {
			Toast.makeText(this, getString(R.string.presstoquit_msg), Toast.LENGTH_SHORT)
					.show();
			touchTime = currentTime;
		} else {
			finish();
		}
	}

	private ProgressDialog showProgressDialog(String title, String msg) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.show();
		
		return dialog;
	}

	
	static class UpdateUIHandler extends Handler {
		private ProgressDialog dialog;
		WeakReference<CameraActivity> mActivity;
		
		UpdateUIHandler(ProgressDialog dialog, CameraActivity activity) {
			this.dialog = dialog;
			mActivity = new WeakReference<CameraActivity>(activity);
		}
		public void handleMessage(android.os.Message msg) {
			if(dialog != null) {
				dialog.cancel();
			}
			if(msg.what != 1 && recognizedView !=null && translatedView !=null) {
				recognizedView.setText(recognizedText[0]);
				translatedView.setText(msg.getData().getString("translated"));
			}
			
		}
	
	}
	
	class TriggerButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (null != mCamera && isPreview) {
				mCamera.setOneShotPreviewCallback(CameraActivity.this);
			}
		}
	}

	class WebSearchButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (recognizedView.getText().toString().isEmpty()) {
				Toast.makeText(CameraActivity.this, getString(R.string.textempty_msg),
						Toast.LENGTH_SHORT).show();
			} else {

				ProgressDialog dialog = showProgressDialog(getString(R.string.tanslateprogressdlg_msg), getString(R.string.waiting_msg));
				final UpdateUIHandler handler = new UpdateUIHandler(dialog, CameraActivity.this);
						
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						YoudaoJsonParser youdaoJsonParser = YoudaoTranslater
								.translate(recognizedText[0]);
						if(youdaoJsonParser != null) {
							Intent intent = new Intent();
							intent.setClass(CameraActivity.this, WebTranslationActivity.class);
							intent.putExtra("word", recognizedText[0]);
							intent.putExtra("phonetic", youdaoJsonParser.getPhonetic());
							intent.putExtra("basictanslation", youdaoJsonParser.getBasicTanslation());
							intent.putExtra("webtanslation", youdaoJsonParser.getWebTanslation());
							startActivity(intent);
						}
						
						handler.sendEmptyMessage(1);
					}
				});
				thread.start();

			}
		}
	}
	
	class AddNotesButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			SQLiteDatabase db = openOrCreateDatabase("OrzEye.db", Context.MODE_PRIVATE, null);
			db.execSQL("CREATE TABLE IF NOT EXISTS notes (_id INTEGER PRIMARY KEY AUTOINCREMENT, word VARCHAR, tanslation VARCHAR)");
			if(!recognizedView.getText().toString().isEmpty() && 
					!translatedView.getText().toString().isEmpty() &&
					!translatedView.getText().toString().equals("not found!")) {
				//TODO These code should be refined later.  
				ContentValues cv = new ContentValues();
		        cv.put("word", recognizedView.getText().toString());  
		        cv.put("tanslation", translatedView.getText().toString());   
		        db.insert("notes", null, cv);
			}
			else {
				Toast.makeText(CameraActivity.this, getString(R.string.textempty_msg),
						Toast.LENGTH_SHORT).show();
			}
			
		}
		
	}
}
