package com.dylan.orzeye.dictionary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import com.dylan.orzeye.dictionary.YoudaoTranslaterAPIConnection;

public class YoudaoTranslater {
	private final static String URL_STRING = "http://fanyi.youdao.com/openapi.do?keyfrom=OrzEye&key=1924278310&type=data&doctype=json&version=1.1&q=";

	public static YoudaoJsonParser translate(String text) {

		String jsonStr = "";
		HttpURLConnection httpURLConnection = null;
		String YoudaoAPIURL = getAdressUrl(text);
		httpURLConnection = YoudaoTranslaterAPIConnection
				.getHttpConnection(YoudaoAPIURL);

		YoudaoJsonParser youdaoJsonParser = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					httpURLConnection.getInputStream(), "utf-8"));

			StringBuilder sb = new StringBuilder();
			String str;

			while ((str = br.readLine()) != null) {
				sb.append(str);
				sb.append('\n');
			}

			jsonStr = sb.toString();
			youdaoJsonParser = new YoudaoJsonParser(jsonStr);
			//TODO handel error
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			YoudaoTranslaterAPIConnection
					.closeHttpConnection(httpURLConnection);
		}

		return youdaoJsonParser;
	}

	private static String getAdressUrl(String text) {
		StringBuilder YoudaoAPIURL = new StringBuilder();

		try {
			YoudaoAPIURL.append(URL_STRING).append(
					URLEncoder.encode(text, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return YoudaoAPIURL.toString();
	}

}