package com.ardrumoid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MusicResultActivity extends AppCompatActivity {

	private static final String LOG_TAG = "MusicResultActivity";

	private int score;
	private boolean isFinished;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		score = getIntent().getIntExtra("score", 0);
		isFinished = getIntent().getBooleanExtra("isFinished", false);
		setContentView(R.layout.activity_music_result);
		if (!isFinished) {
			final RelativeLayout rl = (RelativeLayout) findViewById(R.id.result_bg);
			rl.setBackgroundResource(R.drawable.bg_purple);
		}
		final TextView tv = (TextView) findViewById(R.id.result_score_text);
		tv.setText(String.valueOf(score) + " 점");
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.abc_fade_in, android.R.anim.fade_out);
	}

	public void onScoreBtnClick(View v) {
		finish();
	}
}
