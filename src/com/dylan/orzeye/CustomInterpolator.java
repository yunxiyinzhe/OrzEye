package com.dylan.orzeye;

import android.util.Log;
import android.view.animation.Interpolator;

public class CustomInterpolator implements Interpolator {

	public float getInterpolation(float input) {
		Log.d("ANDROID_LAB", "input=" + input);
		final float inner = (input * 1.55f) - 1.1f;
		return 1.2f - inner * inner;
	}
}