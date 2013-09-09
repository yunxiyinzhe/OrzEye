package com.dylan.orzeye;

import android.graphics.drawable.Drawable;
import android.view.View.OnClickListener;

public class ActionItem {
	private Drawable icon;
	private String actionName;
	private OnClickListener onClickListener;

	public ActionItem(Drawable img, String name, OnClickListener listener) {
		icon = img;
		actionName = name;
		this.onClickListener = listener;
	}

	public Drawable getIcon() {
		return icon;
	}

	public String getActionName() {
		return actionName;
	}

	public OnClickListener getOnClickListener() {
		return this.onClickListener;
	}

}