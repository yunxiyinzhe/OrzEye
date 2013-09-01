package com.dylan.orzeye.dictionary;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class YoudaoTranslaterAPIConnection {

	private static final int REQUEST_TIME_OUT = 1000*10;
	private static final int READ_TIME_OUT = 1000*5;
	
	public static HttpURLConnection getHttpConnection(String urlString) {
		HttpURLConnection con = null;
		
		try {
			con = (HttpURLConnection) new URL(urlString).openConnection();
			con.setReadTimeout(READ_TIME_OUT);
			con.setConnectTimeout(REQUEST_TIME_OUT);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return con;
	}

	public static boolean closeHttpConnection(HttpURLConnection con) {
		try {
			con.getInputStream().close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}