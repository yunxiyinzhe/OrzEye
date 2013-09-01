package com.dylan.orzeye;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class DrawImageView extends ImageView {
	Context mContext;

	public DrawImageView(Context context, AttributeSet attrs) {
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
			canvas.drawRect(80, 100, width - 80, 200, paint);
		}
	}
}
