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
		
		wordView.setText(intent.getStringExtra(getString(R.string.word_key)));
		phoneticView.setText(intent.getStringExtra(getString(R.string.phonetic_key)));
		basicTanslationView.setText(intent.getStringExtra(getString(R.string.basictanslation_key)));
		webTanslationView.setText(intent.getStringExtra(getString(R.string.webtanslation_key)));
	}
}
