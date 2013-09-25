package com.dylan.orzeye.ui;

import java.util.ArrayList;
import java.util.List;

import com.dylan.orzeye.R;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class NotesFragment extends Fragment implements ListView.OnScrollListener,
		android.view.View.OnClickListener {
	private Handler handler;
	private DisapearThread disapearThread;
	private int scrollState;
	private NotesWordListAdapter listAdapter;
	private ListView notesWordList;
	private TextView charHint;
	private WindowManager windowManager;
	
	private List<String> stringArr = new ArrayList<String>();
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
   		SQLiteDatabase db = getActivity().openOrCreateDatabase("OrzEye.db", Context.MODE_PRIVATE, null);
   		Cursor cursor = db.query("notes", new String[]{"word"}, null,null, null, null, null);
   		while (cursor.moveToNext()) {
   			stringArr.add(cursor.getString(0));
   		}
    	
    	View view = inflater.inflate( R.layout.activity_notes,container, false);
    	handler = new Handler();
   		charHint = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.notes_popup_char_hint, null);
   		charHint.setVisibility(View.INVISIBLE);
   		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,
   				LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
   				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
   						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
   		windowManager = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
   		windowManager.addView(charHint, lp);
   		listAdapter = new NotesWordListAdapter(getActivity(), stringArr, this);
   		notesWordList = (ListView) view.findViewById(R.id.notes_word_list);
   		notesWordList.setOnScrollListener(this);
   		notesWordList.setAdapter(listAdapter);
   		disapearThread = new DisapearThread();
   		
    	return view;
    }
    
	/** ListView.OnScrollListener */
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		if(stringArr.isEmpty()) {
			return;
		}
		charHint.setText(String.valueOf(stringArr.get(firstVisibleItem + (visibleItemCount >> 1))
				.charAt(0)));
	}

	/** ListView.OnScrollListener */
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
			handler.removeCallbacks(disapearThread);
			boolean bool = handler.postDelayed(disapearThread, 1500);
			Log.d("ANDROID_INFO", "postDelayed=" + bool);
		} else {
			charHint.setVisibility(View.VISIBLE);
		}
	}

	public void onClick(View view) {
		if (view instanceof ImageView) {
			int position = ((Integer) view.getTag()).intValue();
			NotesMoreActionItem actionViewNotesWord = new NotesMoreActionItem(getResources().getDrawable(R.drawable.view_notes__word_icon),
					"View", this);
			NotesMoreActionItem actionDeleteNotesWord = new NotesMoreActionItem(getResources().getDrawable(R.drawable.delete_notes_word_icon),
					"Delete", this);

			NotesMoreActionBar moreActionBar = new NotesMoreActionBar(view, position);
			moreActionBar.addActionItem(actionViewNotesWord);
			moreActionBar.addActionItem(actionDeleteNotesWord);
			moreActionBar.show();
		} else if (view instanceof LinearLayout) {
			LinearLayout actionsLayout = (LinearLayout) view;
			NotesMoreActionBar bar = (NotesMoreActionBar) actionsLayout.getTag();
			bar.dismissQuickActionBar();
		}
	}

	private class DisapearThread implements Runnable {
		public void run() {
			if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
				charHint.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void onDestroy() {
		super.onDestroy();
		charHint.setVisibility(View.INVISIBLE);
		windowManager.removeView(charHint);
	}

}