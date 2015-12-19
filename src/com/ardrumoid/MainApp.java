package com.ardrumoid;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDeviceConnection;
import android.preference.Preference;
import android.util.Log;

public class MainApp extends Application {

	public static final String PREFERENCE_SHARED_FILENAME = "SharedPrefs";
	public static final String PREF_SYNC_ID = "sync_time";
	public static final float SYNC_TIME_DEFAULT = 0.075f;
	
	public static float syncTime;

	private static Context mContext;
	private static UsbSerialPort mPort = null;
	private static UsbDeviceConnection mConnection = null;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		Fresco.initialize(mContext);
		
		SharedPreferences pref = getSharedPreferences(PREFERENCE_SHARED_FILENAME, Context.MODE_PRIVATE);
		syncTime = pref.getFloat(PREF_SYNC_ID, SYNC_TIME_DEFAULT);
	}
	
	public static Context getContext() {
		return mContext;
	}

	public static UsbSerialPort getPort() {
		return mPort;
	}

	public static void setPort(UsbSerialPort port) {
		mPort = port;
	}

	public static void setConnection(UsbDeviceConnection connection) {
		mConnection = connection;
	}

	public static UsbDeviceConnection getConnection() {
		return mConnection;
	}

}
