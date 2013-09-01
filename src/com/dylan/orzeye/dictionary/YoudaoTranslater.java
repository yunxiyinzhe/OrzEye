package com.dylan.orzeye.dictionary;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.dylan.orzeye.dictionary.YoudaoTranslaterAPIConnection;

public class YoudaoTranslater  {
	private final static String URL_STRING = "http://fanyi.youdao.com/openapi.do?keyfrom=OrzEye&key=1924278310&type=data&doctype=json&version=1.1&q=";
    private final static String TIMEOUT_CODE = "-1";

	public static String translate(String text) {
		
        String jsonStr = "";
        HttpURLConnection httpURLConnection = null;
        String YoudaoAPIURL = getAdressUrl(text);
        httpURLConnection = YoudaoTranslaterAPIConnection.getHttpConnection(
                YoudaoAPIURL);
        
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    httpURLConnection.getInputStream(),
                    "utf-8"));
            
            StringBuilder sb = new StringBuilder();
            String str;
            
            while ((str = br.readLine()) != null) {
                sb.append(str);
                sb.append('\n');
            }

            jsonStr = sb.toString();
            JSONObject jsonObj = null;
            jsonObj = (JSONObject) new JSONParser().parse(jsonStr);
            if ("0".equals(jsonObj.get("errorCode").toString())) {
                jsonStr = jsonObj.get("translation").toString();
            }
        } catch (Exception e) {
        	if(e instanceof SocketTimeoutException) {
        		jsonStr = TIMEOUT_CODE;
        	}
        	e.printStackTrace();
        } finally {
            YoudaoTranslaterAPIConnection.closeHttpConnection(httpURLConnection);
        }
        
        return  jsonStr;
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