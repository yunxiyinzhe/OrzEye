package com.dylan.orzeye.ui;

import java.util.Vector;

import com.dylan.orzeye.R;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
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

public class QuickActionBar {
	private View anchor;
	private Vector<ActionItem> vecActions;
	private PopupWindow popupWindow;
	private View qaBarRoot;
	
	private int phoneScreenWidth;
	private int phoneScreenHeight;

	private LayoutInflater inflater;
	private Drawable popupWindowBackground;
	private int listItemIndex;

	public QuickActionBar(View anchor, int idx) {
		if (vecActions == null) {
			vecActions = new Vector<ActionItem>();
		}
		this.anchor = anchor;
		this.listItemIndex = idx;
		Context context = this.anchor.getContext();
		this.popupWindow = new PopupWindow(context);
		
		popupWindow.setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					QuickActionBar.this.popupWindow.dismiss();
					return true;
				}
				return false;
			}
		});
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		phoneScreenWidth = windowManager.getDefaultDisplay().getWidth();
		phoneScreenHeight = windowManager.getDefaultDisplay().getHeight();

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		qaBarRoot = (ViewGroup) inflater.inflate(R.layout.qa_bar, null);
	}

	public void addActionItem(ActionItem actionWeb) {
		vecActions.add(actionWeb);
	}

	public void show() {
		int[] location = new int[2];
		anchor.getLocationOnScreen(location);
		Rect anchorRect = new Rect(location[0], location[1], location[0] + anchor.getWidth(),
				location[1] + anchor.getHeight());
		qaBarRoot.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		qaBarRoot.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int rootWidth = qaBarRoot.getMeasuredWidth();
		int rootHeight = qaBarRoot.getMeasuredHeight();

		int xPos = (phoneScreenWidth - rootWidth) >> 1;

		int yPos = anchorRect.bottom;

		int diff = -10;
		
		if (anchorRect.bottom + diff + rootHeight > phoneScreenHeight) {
			yPos = anchorRect.top - rootHeight;
			diff = -diff;
		}

		LinearLayout actionsLayout = (LinearLayout) qaBarRoot.findViewById(R.id.actionsLayout);
		appendActionsItemUI(actionsLayout, vecActions);

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
		popupWindow.setContentView(qaBarRoot);
		popupWindow.showAtLocation(this.anchor, Gravity.NO_GRAVITY, xPos, yPos + diff);
	}

	private void appendActionsItemUI(ViewGroup actionsLayout, Vector<ActionItem> vec) {
		if (vec == null || vec.size() == 0) {
			return;
		}

		View view = null;
		int idx = 1;
		int size = vec.size();
		for (int i = 0; i < size; i++, idx++) {
			view = getActionItemUI(vecActions.get(i));
			view.setTag(this);
			actionsLayout.addView(view, idx);
		}
	}

	private View getActionItemUI(ActionItem item) {
		LinearLayout actionItemLayout = (LinearLayout) inflater.inflate(R.layout.action_item, null);
		ImageView icon = (ImageView) actionItemLayout.findViewById(R.id.qa_actionItem_icon);
		TextView txtName = (TextView) actionItemLayout.findViewById(R.id.qa_actionItem_name);
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