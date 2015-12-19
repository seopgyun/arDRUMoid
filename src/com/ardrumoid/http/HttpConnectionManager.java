package com.ardrumoid.http;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnectionManager {
	private static final String LOG_TAG = "HttpConnectionManager";

	public static HttpURLConnection getHttpURLConnection(String targetURL) {
		HttpURLConnection httpConnection = null;
		try {
			URL url = new URL(targetURL);

			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setConnectTimeout(15000);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpConnection;
	}

	public static void setDismissConnection(HttpURLConnection returnedConn, Reader inR,
			Writer outW) {

		if (inR != null) {
			try {
				inR.close();
			} catch (IOException ioe) {

			}
		}
		if (outW != null) {
			try {
				outW.close();
			} catch (IOException ioe) {

			}
		}
		if (returnedConn != null) {

			returnedConn.disconnect();
		}
	}
}
