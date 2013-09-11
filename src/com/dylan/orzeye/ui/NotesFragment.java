package com.dylan.orzeye.ui;

import com.dylan.orzeye.R;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class NotesFragment extends Fragment implements ListView.OnScrollListener, OnItemClickListener,
		android.view.View.OnClickListener {
	private Handler handler;
	private DisapearThread disapearThread;
	/** 标识List的滚动状态。 */
	private int scrollState;
	private ListAdapter listAdapter;
	private ListView listMain;
	private TextView txtOverlay;
	private WindowManager windowManager;
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	View view = inflater.inflate( R.layout.activity_notes,container, false);
    	handler = new Handler();
   		// 初始化首字母悬浮提示框
   		txtOverlay = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.popup_char_hint, null);
   		txtOverlay.setVisibility(View.INVISIBLE);
   		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,
   				LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
   				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
   						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
   		windowManager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
   		windowManager.addView(txtOverlay, lp);
   		// 初始化ListAdapter
   		listAdapter = new ListAdapter(getActivity(), stringArr, this);
   		listMain = (ListView) view.findViewById(R.id.listInfo);
   		listMain.setOnItemClickListener(this);
   		listMain.setOnScrollListener(this);
   		listMain.setAdapter(listAdapter);
   		disapearThread = new DisapearThread();
    	   return view;
    }
    
	/** ListView.OnScrollListener */
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		// 以中间的ListItem为标准项。
		txtOverlay.setText(String.valueOf(stringArr[firstVisibleItem + (visibleItemCount >> 1)]
				.charAt(0)));
	}

	/** ListView.OnScrollListener */
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
			handler.removeCallbacks(disapearThread);
			// 提示延迟1.5s再消失
			boolean bool = handler.postDelayed(disapearThread, 1500);
			Log.d("ANDROID_INFO", "postDelayed=" + bool);
		} else {
			txtOverlay.setVisibility(View.VISIBLE);
		}
	}

	/** OnItemClickListener */
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
	}

	/**
	 * View.OnClickListener <br/>
	 * 点击“咧牙”图片。<br/>
	 */
	public void onClick(View view) {
		if (view instanceof ImageView) {
			// "咧牙"图标
			int position = ((Integer) view.getTag()).intValue();
			ActionItem actionViewNotesWord = new ActionItem(getResources().getDrawable(R.drawable.view_notes__word_icon),
					"View", this);
			ActionItem actionDeleteNotesWord = new ActionItem(getResources().getDrawable(R.drawable.delete_notes_word_icon),
					"Delete", this);

			QuickActionBar qaBar = new QuickActionBar(view, position);
			qaBar.addActionItem(actionViewNotesWord);
			qaBar.addActionItem(actionDeleteNotesWord);
			qaBar.show();
		} else if (view instanceof LinearLayout) {
			// ActionItem组件
			LinearLayout actionsLayout = (LinearLayout) view;
			QuickActionBar bar = (QuickActionBar) actionsLayout.getTag();
			bar.dismissQuickActionBar();
		}
	}

	private class DisapearThread implements Runnable {
		public void run() {
			// 避免在1.5s内，用户再次拖动时提示框又执行隐藏命令。
			if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
				txtOverlay.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void onDestroy() {
		super.onDestroy();
		// 将txtOverlay删除。
		txtOverlay.setVisibility(View.INVISIBLE);
		windowManager.removeView(txtOverlay);
	}

	private String[] stringArr = { "Abbaye de Belloc","Zanetti Parmigiano Reggiano" };

}