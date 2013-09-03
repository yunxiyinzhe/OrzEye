package com.dylan.orzeye.dictionary;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class YoudaoJsonParser extends JSONParser {
	JSONObject jsonObj;
	
	public YoudaoJsonParser(String jsonStr) {
		try {
			jsonObj = (JSONObject) parse(jsonStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String getPhonetic() {
		String phonetic = "";
		try {
			phonetic = ((JSONObject)jsonObj.get("basic")).get("phonetic").toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return phonetic;
		
	}
	
	public String getBasicTanslation() {
		String basicTanslations = "";
		try {
			JSONArray explains = (JSONArray)((JSONObject)jsonObj.get("basic")).get("explains");
			for(int i = 0; i < explains.size(); i++) {
				basicTanslations = basicTanslations + explains.get(i).toString() + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return basicTanslations;
		
	}
	
	public String getWebTanslation() {
		String webTanslations = "";
		try {
			JSONArray tanslations = (JSONArray)(jsonObj.get("web"));
			for(int i = 0; i < tanslations.size(); i++) {
				JSONObject tanslation = (JSONObject) tanslations.get(i);
				webTanslations = webTanslations + getWebTanslationValue(tanslation) + " " +getWebTanslationKey(tanslation) + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return webTanslations;
		
	}
	
	private String getWebTanslationValue(JSONObject tanslation) {
		String WebTanslationValue = "";
		try {
			WebTanslationValue = (tanslation.get("value")).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return WebTanslationValue;
		
	}
	
	private String getWebTanslationKey(JSONObject tanslation) {
		String WebTanslationKey = "";
		try {
			WebTanslationKey = (tanslation.get("key")).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return WebTanslationKey;
		
	}
}
