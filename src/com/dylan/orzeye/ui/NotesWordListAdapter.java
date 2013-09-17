package com.dylan.orzeye.ui;

import java.util.List;

import com.dylan.orzeye.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NotesWordListAdapter extends BaseAdapter {
	
	
	private LayoutInflater layoutInflater;
	private OnClickListener onClickListener;
	private List<String> stringArr;

	public NotesWordListAdapter(Context context, List<String> arr, OnClickListener listener) {
		layoutInflater = LayoutInflater.from(context);
		this.onClickListener = listener;
		stringArr = arr;
	}

	public int getCount() {
		return stringArr == null ? 0 : stringArr.size();
	}

	public Object getItem(int position) {
		if (stringArr != null) {
			return stringArr.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.notes_word_item, null);
			holder = new ViewHolder();
			holder.firstCharHintTextView = (TextView) convertView
					.findViewById(R.id.text_first_char_hint);
			holder.orderTextView = (TextView) convertView.findViewById(R.id.list_order_number);
			holder.wordTextView = (TextView) convertView.findViewById(R.id.notes_word);

			holder.imgView = (ImageView) convertView.findViewById(R.id.notes_more_action_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.orderTextView.setText(String.valueOf(position + 1) + ".");
		holder.wordTextView.setText(stringArr.get(position));
		holder.imgView.setOnClickListener(onClickListener);
		holder.imgView.setTag(position);
		int idx = position - 1;
		char previewChar = idx >= 0 ? stringArr.get(idx).charAt(0) : ' ';
		char currentChar = stringArr.get(position).charAt(0);
		if (currentChar != previewChar) {
			holder.firstCharHintTextView.setVisibility(View.VISIBLE);
			holder.firstCharHintTextView.setText(String.valueOf(currentChar));
		} else {
			holder.firstCharHintTextView.setVisibility(View.GONE);
		}
		return convertView;
	}

	public final class ViewHolder {
		public TextView firstCharHintTextView;
		public TextView orderTextView;
		public TextView wordTextView;
		public ImageView imgView;
	}
}