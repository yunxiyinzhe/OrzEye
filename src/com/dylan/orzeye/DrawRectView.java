package com.dylan.orzeye;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class DrawRectView extends ImageView {
	Context mContext;

	public DrawRectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	Paint paint = new Paint();
	{
		paint.setAntiAlias(true);
		paint.setColor(Color.GREEN);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2.5f);
		paint.setAlpha(100);
	};

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mContext instanceof CameraActivity) {
			DisplayMetrics dm = ((CameraActivity) mContext).getDisplayMetrics();
			int width = dm.widthPixels;
			int height = dm.heightPixels;
			canvas.drawRect(width/5, height/8, width - width/5, height/4, paint);
			canvas.clipRect(width/5, height/8, width - width/5, height/4);
			canvas.drawARGB(100, 0, 0, 0);
			canvas.drawLine(width/2-20,(height/8+height/4)/2,width/2+20,(height/8+height/4)/2,paint);
			canvas.drawLine(width/2,(height/8+height/4)/2-20,width/2,(height/8+height/4)/2+20,paint);
		}
	}
}
