package com.ardrumoid;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ardrumoid.data.SelectMusicData;
import com.ardrumoid.surface.GameThread;
import com.ardrumoid.surface.GameView;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.media.SoundPool.Builder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class MusicPlayActivity extends AppCompatActivity {

	private static final String LOG_TAG = "MusicPlayActivity";

	public static int deviceWidth, deviceHeight;
	public static int score = 0;

	public static long musicStartTime = 0;

	private GameView mGameView;

	private SoundPool mSoundPool;
	private SelectMusicData mData;

	private int SnareId, KickId, CymbalId, TomId, HihatId;

	private SerialInputOutputManager mSerialIoManager;

	private static final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

	private SerialInputOutputManager.Listener mListener;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mListener = new SerialInputOutputManager.Listener() {

			@Override
			public void onRunError(Exception e) {
				Log.d("MenuActivity.SerialInputOutputManager", "Runner stopped.");
			}

			@Override
			public void onNewData(final byte[] data) {
				MusicPlayActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MusicPlayActivity.this.updateReceivedData(data);
					}
				});
			}
		};

		mData = getIntent().getParcelableExtra("data");

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mGameView = new GameView(getApplicationContext(), mData.dataUrl);

		score = 0;

		setContentView(mGameView);

		mGameView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int j;
				for (j = 0; j < GameThread.note[0].size(); j++) {
					if (deviceHeight * 0.7 < GameThread.note[0].get(j).dY
							&& GameThread.note[0].get(j).dY < deviceHeight * 0.9) {
						long score = Math
								.abs(300 - Math.abs((int) (deviceHeight * 0.8 - 25)
										- GameThread.note[0].get(j).dY));
						// Toast.makeText(getApplicationContext(), "TT" + score,
						// Toast.LENGTH_SHORT).show();
						// GameThread.note[0].get(j).dY = deviceHeight + 100;
						GameThread.note[0].get(j).isChecked = true;
						GameThread.judgeTimeArray[0] = System.currentTimeMillis();
						break;
					}
				}

			}
		});

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			Display display = getWindowManager().getDefaultDisplay();
			deviceWidth = display.getWidth();
			deviceHeight = display.getHeight();
		} else {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			deviceWidth = size.x;
			deviceHeight = size.y;
		}

		Log.d(LOG_TAG, "W // " + deviceWidth + "   H // " + deviceHeight);

		if (Build.VERSION.SDK_INT < 21) {
			mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		} else {
			mSoundPool = new Builder().setMaxStreams(5).build();
		}

		SnareId = mSoundPool.load(this, R.raw.snare1, 1);
		KickId = mSoundPool.load(this, R.raw.kick1, 1);
		CymbalId = mSoundPool.load(this, R.raw.cymbal1, 1);
		TomId = mSoundPool.load(this, R.raw.tom1, 1);
		HihatId = mSoundPool.load(this, R.raw.hihat1, 1);
		new MPAsyncTask().execute(mData.bgUrl);
	}

	public static MediaPlayer mp = null;

	private class MPAsyncTask extends AsyncTask<String, Boolean, String> {
		@Override
		protected String doInBackground(String... params) {
			if (params[0].equalsIgnoreCase("1")) {
				mp = MediaPlayer.create(getApplicationContext(), R.raw.up_above_short);
			} else {
				mp = MediaPlayer.create(getApplicationContext(), R.raw.up_above);
			}

			mp.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer player) {

					// player.start();
					// musicStartTime = System.currentTimeMillis();
				}
			});

			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer player) {
					if (mp != null) {
						mp.release();
						mp = null;
					}

					Intent intent = new Intent(MusicPlayActivity.this,
							MusicResultActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					intent.putExtra("isFinished", true);
					intent.putExtra("score", score);
					startActivity(intent);
					finish();
				}
			});

			try {
				mp.prepare();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (MainApp.getPort() == null) {
				// playDataText.setText("No serial device.");
				Toast.makeText(getApplicationContext(), "장치를 다시 연결해주세요.\nport",
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				final UsbManager usbManager = (UsbManager) getSystemService(
						Context.USB_SERVICE);

				UsbDeviceConnection connection = usbManager
						.openDevice(MainApp.getPort().getDriver().getDevice());
				
				MainApp.setConnection(connection);
				if (connection == null) {
					// playDataText.setText("Opening device failed");
					Toast.makeText(getApplicationContext(), "장치를 다시 연결해주세요.\nconn",
							Toast.LENGTH_SHORT).show();
					return;
				}

			}

			try {
				MainApp.getPort().open(MainApp.getConnection());
				MainApp.getPort().setParameters(115200, 8, UsbSerialPort.STOPBITS_1,
						UsbSerialPort.PARITY_NONE);
			} catch (IOException e) {
				// Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
				// playDataText.setText("Error opening device: " +
				// e.getMessage());
				Toast.makeText(getApplicationContext(), "장치를 다시 연결해주세요.\nio",
						Toast.LENGTH_SHORT).show();
				try {
					MainApp.getPort().close();
				} catch (IOException e2) {
					// Ignore.
				}
				// MainApp.setPort(null);
				return;
			}
			onDeviceStateChange();
		}

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(MusicPlayActivity.this, MusicResultActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.putExtra("isFinished", false);
		intent.putExtra("score", score);
		startActivity(intent);
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mp != null && mp.isPlaying()) {
			mp.pause();
		}
		try {
			MainApp.getPort().close();
		} catch (IOException e2) {
			// Ignore.
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mp != null) {
			mp.release();
			mp = null;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mp != null && !mp.isPlaying()) {
			mp.start();
		}

		// playDataText.setText("Serial device: " +
		// MainApp.getPort().getClass().getSimpleName());

	}

	private void onDeviceStateChange() {
		stopIoManager();
		startIoManager();
	}

	private void stopIoManager() {
		if (mSerialIoManager != null) {
			Log.i("MusicPlayActivity.OnResume.stopIoManager", "Stopping io manager ..");
			mSerialIoManager.stop();
			mSerialIoManager = null;
		}
	}

	private void startIoManager() {
		if (MainApp.getPort() != null) {
			Log.i("MusicPlayActivity.OnResume.startIoManager", "Starting io manager ..");
			mSerialIoManager = new SerialInputOutputManager(MainApp.getPort(), mListener);
			mExecutor.submit(mSerialIoManager);
		}
	}

	public void onTempBtnClick(View v) {
		Intent intent = new Intent(this, MusicResultActivity.class);
		startActivity(intent);
	}

	private void updateReceivedData(byte[] data) {
		// final String message = "Read " + data.length + " bytes: \n"
		// + HexDump.dumpHexString(data) + "\n\n";

		// final String message = "Read " + data.length + " bytes: \n" + new
		// String(data);
		// playDataText.append(message);

		char dataArray[] = new String(data).toCharArray();
		int num = 0;
		if (dataArray[num] == '0') {
			mSoundPool.play(CymbalId, 0.5F, 0.5F, 1, 0, 1.0F);
			checkNote(2);
			num++;
		}
		if (dataArray.length > num && dataArray[num] == '1') {
			mSoundPool.play(HihatId, 0.7F, 0.7F, 1, 0, 1.0F);
			checkNote(0);
			num++;
		}
		if (dataArray.length > num && dataArray[num] == '2') {
			mSoundPool.play(SnareId, 0.7F, 0.7F, 1, 0, 1.0F);
			checkNote(1);
			num++;
		}
		if (dataArray.length > num && dataArray[num] == '3') {
			mSoundPool.play(TomId, 0.7F, 0.7F, 1, 0, 1.0F);
			checkNote(3);

		}
		if (dataArray.length > num && dataArray[num] == '4') {
			mSoundPool.play(KickId, 1.0F, 1.0F, 1, 0, 1.0F);
			checkNote(4);

		}

	}

	public void checkNote(int note_num) {
		if (GameThread.note[note_num] != null) {
			for (int j = 0; j < GameThread.note[note_num].size(); j++) {
				if (deviceHeight * 0.7 < GameThread.note[note_num].get(j).dY
						&& GameThread.note[note_num].get(j).dY < deviceHeight * 0.9) {
					score += Math.abs(200 - Math.abs((int) (deviceHeight * 0.8 - 25)
							- GameThread.note[note_num].get(j).dY));

					GameThread.note[note_num].get(j).dY = deviceHeight + 100;
					GameThread.note[note_num].get(j).isChecked = true;
					GameThread.judgeTimeArray[note_num] = System.currentTimeMillis();
					break;
				}
			}
		}
	}
}
