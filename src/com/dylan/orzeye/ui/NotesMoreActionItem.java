package com.dylan.orzeye.ui;

import android.graphics.drawable.Drawable;
import android.view.View.OnClickListener;

public class NotesMoreActionItem {
	private Drawable icon;
	private String actionName;
	private OnClickListener onClickListener;

	public NotesMoreActionItem(Drawable img, String name, OnClickListener listener) {
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