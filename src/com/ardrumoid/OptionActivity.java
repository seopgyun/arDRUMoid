package com.ardrumoid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

public class OptionActivity extends AppCompatActivity {

	SharedPreferences pref;
	
	private EditText syncValueText;
	private SeekBar syncSeekBar;
	private float syncValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		pref = getSharedPreferences(MainApp.PREFERENCE_SHARED_FILENAME, Context.MODE_PRIVATE);
		
		syncValue = MainApp.syncTime;
		
		syncValueText = (EditText) findViewById(R.id.option_synctime_text);
		syncValueText.setText(String.valueOf(syncValue));

		syncSeekBar = (SeekBar) findViewById(R.id.option_seekbar_synctime);
		syncSeekBar.setProgress((int) (syncValue * 1000) + 1000);
		syncSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				syncValue = (float)(seekBar.getProgress() - 1000) / 1000;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				double value = (double) (progress - 1000) / 1000;
				syncValueText.setText(String.valueOf(value));
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.abc_fade_in, android.R.anim.fade_out);
	}
	
	public void onSTLeftBtnClick(View v) {
		int value = (int) (syncValue * 1000);
		if (value == -1000) {
			return;
		} else if (value < -995) {
			syncValue = -1.0f;
			syncValueText.setText("-1.0");
			return;
		}
		
		int remainder = value % 5;
		if (remainder == 0) {
			syncValue = (float) (Math.floor((double)((int)(syncValue*1000) - 5)) * 0.001);
		} else {
			syncValue = (float) Math.floor((double)((int)(syncValue*1000) - remainder) * 0.001);
		}
		
		syncValueText.setText(String.valueOf(syncValue));
	}
	
	public void onSTRightBtnClick(View v) {
		int value = (int) (syncValue * 1000);
		if (value == 1000) {
			return;
		} else if (value > 995) {
			syncValue = 1.0f;
			syncValueText.setText("1.0");
			return;
		}
		
		int remainder = value % 5;
		if (remainder == 0) {
			syncValue = (float) (Math.floor((double)((int)(syncValue*1000) + 5)) * 0.001);
		} else {
			syncValue = (float) (Math.floor((double)((int)(syncValue*1000) + (5-remainder))) * 0.001);
		}
		
		syncValueText.setText(String.valueOf(syncValue));
	}
	
	public void onSTDefaultBtnClick(View v) {
		syncValue = MainApp.SYNC_TIME_DEFAULT;
		syncValueText.setText(String.valueOf(syncValue));
		syncSeekBar.setProgress((int) (syncValue * 1000) + 1000);
	}
	
	public void onSTApplyBtnClick(View v) {
		String text = syncValueText.getText().toString();
		if (text.equals("")) {
			syncValueText.setText(String.valueOf(syncValue));
		}
		syncValue = Float.parseFloat(syncValueText.getText().toString());
		MainApp.syncTime = syncValue;
		syncSeekBar.setProgress((int) (syncValue * 1000) + 1000);
		
		SharedPreferences.Editor mEditor = pref.edit(); 
		mEditor.putFloat(MainApp.PREF_SYNC_ID, syncValue);
		mEditor.apply();
		Toast.makeText(MainApp.getContext(), "적용되었습니다.", Toast.LENGTH_SHORT).show();
	}
	
	public void onUsbConnBtnClick(View v) {
		Intent intent = new Intent(this, SplashActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}

}
