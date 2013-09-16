package com.dylan.orzeye.ui;

import java.util.Vector;

import com.dylan.orzeye.R;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
@SuppressWarnings("deprecation")
public class NotesMoreActionBar {
	private View anchor;
	private Vector<NotesMoreActionItem> moreActions;
	private PopupWindow popupWindow;
	private View moreActionBarRoot;

	private LayoutInflater inflater;
	private Drawable popupWindowBackground;
	private int listItemIndex;

	public NotesMoreActionBar(View anchor, int idx) {
		if (moreActions == null) {
			moreActions = new Vector<NotesMoreActionItem>();
		}
		this.anchor = anchor;
		this.listItemIndex = idx;
		Context context = this.anchor.getContext();
		this.popupWindow = new PopupWindow(context);
		
		popupWindow.setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					NotesMoreActionBar.this.popupWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		moreActionBarRoot = (ViewGroup) inflater.inflate(R.layout.notes_more_action_bar, null);
	}

	public void addActionItem(NotesMoreActionItem actionWeb) {
		moreActions.add(actionWeb);
	}

	public void show() {
		moreActionBarRoot.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		moreActionBarRoot.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		LinearLayout actionsLayout = (LinearLayout) moreActionBarRoot.findViewById(R.id.actionsLayout);
		appendActionsItemUI(actionsLayout, moreActions);

		if (popupWindowBackground == null) {
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
		} else {
			popupWindow.setBackgroundDrawable(popupWindowBackground);
		}
		popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setContentView(moreActionBarRoot);
		popupWindow.showAsDropDown(anchor);
	}

	private void appendActionsItemUI(ViewGroup actionsLayout, Vector<NotesMoreActionItem> vec) {
		if (vec == null || vec.size() == 0) {
			return;
		}

		View view = null;
		int idx = 1;
		int size = vec.size();
		for (int i = 0; i < size; i++, idx++) {
			view = getActionItemUI(moreActions.get(i));
			view.setTag(this);
			actionsLayout.addView(view, idx);
		}
	}

	private View getActionItemUI(NotesMoreActionItem item) {
		LinearLayout actionItemLayout = (LinearLayout) inflater.inflate(R.layout.notes_more_action_item, null);
		ImageView icon = (ImageView) actionItemLayout.findViewById(R.id.notes_more_action_Item_icon);
		TextView txtName = (TextView) actionItemLayout.findViewById(R.id.notes_more_action_Item_name);
		Drawable drawable = item.getIcon();
		if (drawable == null) {
			icon.setVisibility(View.GONE);
		} else {
			icon.setImageDrawable(drawable);
		}
		String name = item.getActionName();
		if (name == null) {
			txtName.setVisibility(View.GONE);
		} else {
			txtName.setText(name);
		}
		actionItemLayout.setOnClickListener(item.getOnClickListener());
		return actionItemLayout;
	}
	
	public void dismissQuickActionBar() {
		popupWindow.dismiss();
	}

	public int getListItemIndex() {
		return listItemIndex;
	}
}