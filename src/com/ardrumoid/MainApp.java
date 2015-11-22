package com.ardrumoid;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hoho.android.usbserial.driver.UsbSerialPort;

import android.app.Application;
import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;

public class MainApp extends Application {

	public static double syncTime = 0.075;

	private static Context mContext;
	private static UsbSerialPort mPort = null;
	private static UsbDeviceConnection mConnection = null;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		Fresco.initialize(mContext);
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
