package com.dylan.orzeye;

import java.util.Vector;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @author Sodino E-mail:sodinoopen@hotmail.com
 * @version Time：2011-5-2 下午09:57:23
 */
public class QuickActionBar {
	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;

	public static final int ANIM_AUTO = 4;
	public static final Interpolator interpolator;

	private View anchor;
	private Vector<ActionItem> vecActions;
	private PopupWindow popupWindow;
	private View qaBarRoot;
	
	private int phoneScreenWidth;
	private int phoneScreenHeight;
	private int animType;
	
	private LayoutInflater inflater;
	private Drawable popupWindowBackground;
	private ImageView arrowUp;
	private ImageView arrowDown;
	private boolean enableActionsLayout;
	private Animation actionsLayoutAnim;
	private int listItemIndex;

	static {
		interpolator = new CustomInterpolator();
	}

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
		arrowUp = (ImageView) qaBarRoot.findViewById(R.id.qa_arrow_up);
		arrowDown = (ImageView) qaBarRoot.findViewById(R.id.qa_arrow_down);
		animType = ANIM_AUTO;

		actionsLayoutAnim = AnimationUtils.loadAnimation(anchor.getContext(),
				R.anim.anim_actionslayout);
		actionsLayoutAnim.setInterpolator(interpolator);
	}

	public void setAnimType(int type) {
		this.animType = type;
	}

	public void setEnableActionsLayoutAnim(boolean bool) {
		enableActionsLayout = bool;
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
		boolean arrowOnTop = true;
		if (anchorRect.bottom + diff + rootHeight > phoneScreenHeight) {
			arrowOnTop = false;
			yPos = anchorRect.top - rootHeight;
			diff = -diff;
		}

		int arrowId = arrowOnTop ? R.id.qa_arrow_up : R.id.qa_arrow_down;
		int marginLeft = anchorRect.centerX() - (arrowUp.getMeasuredWidth() >> 1);
		handleArrow(arrowId, marginLeft);

		LinearLayout actionsLayout = (LinearLayout) qaBarRoot.findViewById(R.id.actionsLayout);
		appendActionsItemUI(actionsLayout, vecActions);


		setAnimationStyle(phoneScreenWidth, anchorRect.centerX(), arrowOnTop);
	
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

		if (enableActionsLayout) {
			actionsLayout.startAnimation(actionsLayoutAnim);
		}
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

	private void handleArrow(int arrowShowId, int marginLeft) {
		View showArrow = (arrowShowId == R.id.qa_arrow_up) ? arrowUp : arrowDown;
		View hideArrow = (arrowShowId == R.id.qa_arrow_up) ? arrowDown : arrowUp;
		showArrow.setVisibility(View.VISIBLE);
		hideArrow.setVisibility(View.INVISIBLE);
		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow
				.getLayoutParams();
		param.leftMargin = marginLeft;
	}
	private void setAnimationStyle(int screenWidth, int coordinateX, boolean arrowOnTop) {
		int arrowPos = coordinateX - (arrowUp.getMeasuredWidth() >> 1);
		switch (animType) {
		case ANIM_GROW_FROM_LEFT:
			popupWindow.setAnimationStyle((arrowOnTop) ? R.style.QuickActionBar_PopDown_Left
					: R.style.QuickActionBar_PopUp_Left);
			break;
		case ANIM_GROW_FROM_RIGHT:
			popupWindow.setAnimationStyle((arrowOnTop) ? R.style.QuickActionBar_PopDown_Right
					: R.style.QuickActionBar_PopUp_Right);
			break;
		case ANIM_GROW_FROM_CENTER:
			popupWindow.setAnimationStyle((arrowOnTop) ? R.style.QuickActionBar_PopDown_Center
					: R.style.QuickActionBar_PopUp_Center);
			break;
		case ANIM_AUTO:
			if (arrowPos <= screenWidth / 4) {
				popupWindow.setAnimationStyle((arrowOnTop) ? R.style.QuickActionBar_PopDown_Left
						: R.style.QuickActionBar_PopUp_Left);
			} else if (arrowPos > screenWidth / 4 && arrowPos < 3 * (screenWidth / 4)) {
				popupWindow.setAnimationStyle((arrowOnTop) ? R.style.QuickActionBar_PopDown_Center
						: R.style.QuickActionBar_PopUp_Center);
			} else {
				popupWindow.setAnimationStyle((arrowOnTop) ? R.style.QuickActionBar_PopDown_Right
						: R.style.QuickActionBar_PopUp_Right);
			}
			break;
		}
	}

	public void dismissQuickActionBar() {
		popupWindow.dismiss();
	}

	public int getListItemIndex() {
		return listItemIndex;
	}
}