package com.dylan.orzeye;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class WebTranslationActivity extends Activity {
	
	private TextView wordView;
	private TextView phoneticView;
	private TextView basicTanslationView;
	private TextView webTanslationView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_webtranslation);
		
		wordView = (TextView) findViewById(R.id.WordView);
		phoneticView = (TextView) findViewById(R.id.PhoneticView);
		basicTanslationView = (TextView) findViewById(R.id.BasicTranslationView);
		webTanslationView = (TextView) findViewById(R.id.WebTranslationView);
		
		Intent intent = getIntent();
		
		wordView.setText(intent.getStringExtra("word"));
		phoneticView.setText(intent.getStringExtra("phonetic"));
		basicTanslationView.setText(intent.getStringExtra("basictanslation"));
		webTanslationView.setText(intent.getStringExtra("webtanslation"));
	}
}
