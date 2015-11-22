package com.ardrumoid;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {

	private static final String LOG_TAG = "SplashActivity";
	private static final int MESSAGE_REFRESH = 101;
	private static final long REFRESH_TIMEOUT_MILLIS = 5000;

	private final MyHandler mHandler = new MyHandler(this);

	private UsbManager mUsbManager;

	private int animCount = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
		final TextView splashTitle = (TextView) findViewById(R.id.splash_title);
		final Animation mAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
		mAnim.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				splashTitle.startAnimation(mAnim);
				if (animCount < 1) {
					Toast.makeText(MainApp.getContext(),
							"장치가 연결되지 않았습니다.\n설정에서 다시 연결해 주세요.", Toast.LENGTH_SHORT)
							.show();
					Intent intent = new Intent(SplashActivity.this, MenuActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					SplashActivity.this.finish();
				} else {
					animCount--;
				}
			}
		});
		splashTitle.startAnimation(mAnim);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(MESSAGE_REFRESH);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeMessages(MESSAGE_REFRESH);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.abc_fade_in, android.R.anim.fade_out);
	}

	private void refreshDeviceList() {
		// showProgressBar();

		new AsyncTask<String, Void, UsbSerialPort>() {
			@Override
			protected UsbSerialPort doInBackground(String... params) {
				Log.d(LOG_TAG, "Refreshing device list ...");
				SystemClock.sleep(1000);

				List<UsbSerialDriver> drivers = null;
				try {
					drivers = UsbSerialProber.getDefaultProber()
							.findAllDrivers(mUsbManager);
				} catch (NullPointerException e) {
					e.printStackTrace();
					return null;
				}

				final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
				for (final UsbSerialDriver driver : drivers) {
					final List<UsbSerialPort> ports = driver.getPorts();
					Log.d(LOG_TAG, String.format("+ %s: %s port%s", driver,
							Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
					result.addAll(ports);
				}

				if (result.isEmpty() || result.size() == 0) {
					return null;
				}

				return result.get(0);
			}

			@Override
			protected void onPostExecute(UsbSerialPort result) {
				// hideProgressBar();

				if (result == null) {
					/*
					 * Toast.makeText(getApplicationContext(),
					 * "onPostExecute\n리스트가 비었습니다.", Toast.LENGTH_SHORT).show();
					 */
					return;
				}

				MainApp.setPort(result);

				UsbDeviceConnection connection = mUsbManager
						.openDevice(result.getDriver().getDevice());
				MainApp.setConnection(connection);
				Intent intent = new Intent(SplashActivity.this, MenuActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SplashActivity.this.finish();
			}
		}.execute();
	}

	private void handleMessage(Message msg) {
		switch (msg.what) {
		case MESSAGE_REFRESH:
			refreshDeviceList();
			mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
			break;
		default:
			// super.handleMessage(msg);
			break;
		}
	}

	private static class MyHandler extends Handler {
		private final WeakReference<SplashActivity> mActivity;

		public MyHandler(SplashActivity activity) {
			mActivity = new WeakReference<SplashActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			SplashActivity activity = mActivity.get();
			if (activity != null) {
				activity.handleMessage(msg);
			}
		}
	}

}
