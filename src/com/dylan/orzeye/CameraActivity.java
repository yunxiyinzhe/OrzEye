package com.dylan.orzeye;

import java.io.IOException;

import com.dylan.orzeye.dictionary.DictionaryTool;
import com.dylan.orzeye.image.ImageProcessTool;
import com.dylan.orzeye.ocr.OCRTool;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity implements SurfaceHolder.Callback,PreviewCallback{
	private boolean isPreview = false;
	private SurfaceView mPreviewSV = null;
	private SurfaceHolder mSurfaceHolder = null;
	private Camera mCamera = null;
	private TextView resultView;
	private ImageButton triggerButton;
	private DisplayMetrics dm;
	
	private OCRTool mOCRTool;
	private DictionaryTool mDictionaryTool;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camera);
		
		mPreviewSV = (SurfaceView)findViewById(R.id.surfaceView);
		mPreviewSV.setZOrderOnTop(false);
		mSurfaceHolder = mPreviewSV.getHolder();
		mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
		mSurfaceHolder.addCallback(this);
		
		triggerButton = (ImageButton)findViewById(R.id.triggerButton);
		triggerButton.setOnClickListener(new TriggerButtonOnClickListener());
		resultView = (TextView)findViewById(R.id.ResultView);
		resultView.setText("");
		
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		mOCRTool = new OCRTool();
		mDictionaryTool = new DictionaryTool();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		initCamera();
	}

	public void surfaceCreated(SurfaceHolder holder) {	
		mCamera = Camera.open(); 
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(null != mCamera){
				mCamera.release();
				mCamera = null;
			}
			e.printStackTrace();
		}
	}


	public void surfaceDestroyed(SurfaceHolder holder) {
		if(null != mCamera)
		{
			mCamera.setOneShotPreviewCallback(null);
			
			mCamera.stopPreview(); 
			isPreview = false; 
			mCamera.release();
			mCamera = null;     
		}
	}

	private void initCamera(){
		if(isPreview){
			mCamera.stopPreview();
		}
		if(null != mCamera){			
			Camera.Parameters mCameraParam = mCamera.getParameters();
			mCameraParam.setPictureFormat(ImageFormat.JPEG);
			mCameraParam.setPreviewSize(
					mCameraParam.getSupportedPreviewSizes().get(1).width, 
					mCameraParam.getSupportedPreviewSizes().get(1).height);
			mCamera.setDisplayOrientation(90);  
			mCameraParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
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
		if(null != data){
			enableTriggerButton(false);
			
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle("正在识别中...");
			dialog.setMessage("请稍后...");
			dialog.show();
			final String[] textDisplayOnView = {new String("")}; 
			final Handler handler = new Handler() {
				public void handleMessage(android.os.Message msg) 
				{
					dialog.cancel();
					resultView.setText(textDisplayOnView[0]);
				}
			};
			
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					Bitmap ocrBitmap = ImageProcessTool.getOCRBitmapFromCamera(data, camera, dm);
					if(mOCRTool.isOCREngineReasy() && mDictionaryTool.isDictionaryReady()) {
						String recognizedText = mOCRTool.OCRStart(ocrBitmap);
						String result = mDictionaryTool.lookUpDictionary(recognizedText.toLowerCase());
						textDisplayOnView[0] = recognizedText + "\n" + result;
					}
					else {
						textDisplayOnView[0] = "OCR Data or Dictionary Data can not be found!";
					}
					handler.sendEmptyMessage(0);
				}
			});
			thread.start();
			
			enableTriggerButton(true);

		}
	}
	
    private void enableTriggerButton(boolean enable) {
		// TODO Auto-generated method stub
        triggerButton.setOnClickListener(enable ? new TriggerButtonOnClickListener() : null);
		triggerButton.setEnabled(enable);
	}

	long waitTime = 2000;
    long touchTime = 0;
    @Override
    public void onBackPressed() {
    	long currentTime = System.currentTimeMillis();
    	if((currentTime-touchTime)>=waitTime) {
    		Toast.makeText(this, "Press again to quit.", Toast.LENGTH_SHORT).show();
    		touchTime = currentTime;
    	}else {
    		finish();
    	}
    }
    class TriggerButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(null != mCamera && isPreview)
			{
				mCamera.setOneShotPreviewCallback(CameraActivity.this);
			}
		}
    }
}
