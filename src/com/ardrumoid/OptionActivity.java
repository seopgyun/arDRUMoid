package com.ardrumoid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class OptionActivity extends AppCompatActivity {

	private TextView syncValueText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option);

		syncValueText = (TextView) findViewById(R.id.option_synctime_value_text);
		syncValueText.setText(String.valueOf(MainApp.syncTime));

		SeekBar mSeekBar = (SeekBar) findViewById(R.id.option_seekbar_synctime);
		mSeekBar.setProgress((int) (MainApp.syncTime * 1000) + 1000);
		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

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
	
	public void onSyncTimeBtnClick(View v) {
		MainApp.syncTime = Double.parseDouble(syncValueText.getText().toString());
		Toast.makeText(MainApp.getContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
	}

}
