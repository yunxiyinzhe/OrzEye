package com.dylan.orzeye.ui;

import com.dylan.orzeye.R;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class WebTranslationActivity extends Activity {
	
	private CardUI mCardView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_webtranslation);
		Intent intent = getIntent();
		
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);

		CardStack stack0 = new CardStack();
		stack0.setTitle(intent.getStringExtra("word"));
		mCardView.addStack(stack0);
		
		CardStack stack1 = new CardStack();
		stack1.setTitle(intent.getStringExtra(" "));
		mCardView.addStack(stack1);
		mCardView.addCard(new CardView(getString(R.string.phonetic_title), 
				intent.getStringExtra("phonetic"), "#e00707", "#e00707"));
		
		CardStack stack2 = new CardStack();
		stack2.setTitle(intent.getStringExtra(" "));
		mCardView.addStack(stack2);
		mCardView.addCard(new CardView(getString(R.string.basictranslation_title), 
				intent.getStringExtra("basictanslation"), "#f2a400", "#f2a400"));
		
		CardStack stack3 = new CardStack();
		stack3.setTitle(intent.getStringExtra(" "));
		mCardView.addStack(stack3);
		mCardView.addCard(new CardView(getString(R.string.webtranslation_title), 
				intent.getStringExtra("webtanslation"), "#9d36d0", "#9d36d0"));
		
		mCardView.refresh();
	}
}
