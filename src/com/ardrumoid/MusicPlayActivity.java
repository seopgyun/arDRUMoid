package com.ardrumoid;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ardrumoid.data.PlayMusicData;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MusicPlayActivity extends AppCompatActivity {

	private static final String LOG_TAG = "MusicPlayActivity";

	private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

	public static int deviceWidth, deviceHeight;

	private GameView mGameView;
	private PlayMusicData mData;
	private SerialInputOutputManager mSerialIoManager;
	private SoundPool mSoundPool;
	private int soundId, KickId;
	public static long musicStartTime = 0;

	private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {

		@Override
		public void onRunError(Exception e) {
			Log.d("MusicPlayActivity.SerialInputOutputManager", "Runner stopped.");
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

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGameView = new GameView(getApplicationContext(), mData);

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
						GameThread.note[0].get(j).dY = deviceHeight + 100;
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
			mSoundPool = new SoundPool(2, AudioManager.STREAM_ALARM, 0);
		} else {
			mSoundPool = new Builder().setMaxStreams(2).build();
		}

		soundId = mSoundPool.load(this, R.raw.snare1, 1);
		KickId = mSoundPool.load(this, R.raw.kick1, 1);
		new MPAsyncTask().execute();
	}

	public static MediaPlayer mp = null;

	private class MPAsyncTask extends AsyncTask<String, Boolean, String> {
		@Override
		protected String doInBackground(String... params) {
			mp = MediaPlayer.create(getApplicationContext(), R.raw.up_above);

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
			} else {
				final UsbManager usbManager = (UsbManager) getSystemService(
						Context.USB_SERVICE);

				UsbDeviceConnection connection = usbManager
						.openDevice(MainApp.getPort().getDriver().getDevice());
				if (connection == null) {
					// playDataText.setText("Opening device failed");
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
				try {
					MainApp.getPort().close();
				} catch (IOException e2) {
					// Ignore.
				}
				MainApp.setPort(null);
				return;
			}
			onDeviceStateChange();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mp != null && mp.isPlaying()) {
			mp.pause();
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
		if (new String(data).equalsIgnoreCase("1")) {
			mSoundPool.play(soundId, 0.7F, 0.7F, 1, 0, 1.0F);
			int j;
			for (j = 0; j < GameThread.note[0].size(); j++) {
				if (deviceHeight * 0.7 < GameThread.note[0].get(j).dY
						&& GameThread.note[0].get(j).dY < deviceHeight * 0.9) {
					long score = Math.abs(300 - Math.abs((int) (deviceHeight * 0.8 - 25)
							- GameThread.note[0].get(j).dY));
					// Toast.makeText(getApplicationContext(), "TT" + score,
					// Toast.LENGTH_SHORT).show();
					GameThread.note[0].get(j).dY = deviceHeight + 100;
					break;
				}
			}
		} else if (new String(data).equalsIgnoreCase("2")) {
			mSoundPool.play(KickId, 0.7F, 0.7F, 1, 0, 1.0F);
		} else if (new String(data).equalsIgnoreCase("12")) {
			mSoundPool.play(soundId, 0.7F, 0.7F, 1, 0, 1.0F);
			mSoundPool.play(KickId, 0.7F, 0.7F, 1, 0, 1.0F);
		}
	}
}
