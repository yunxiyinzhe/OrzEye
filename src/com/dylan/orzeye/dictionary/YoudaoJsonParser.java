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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getPhonetic() {
		return ((JSONObject)jsonObj.get("basic")).get("phonetic").toString();
		
	}
	
	public String getBasicTanslation() {
		String basicTanslations = "";
		JSONArray explains = (JSONArray)((JSONObject)jsonObj.get("basic")).get("explains");
		for(int i = 0; i < explains.size(); i++) {
			basicTanslations = basicTanslations + explains.get(i).toString() + "\n";
		}
		return basicTanslations;
		
	}
	
	public String getWebTanslation() {
		String webTanslations = "";
		JSONArray tanslations = (JSONArray)(jsonObj.get("web"));
		for(int i = 0; i < tanslations.size(); i++) {
			JSONObject tanslation = (JSONObject) tanslations.get(i);
			webTanslations = webTanslations + getWebTanslationValue(tanslation) + " " +getWebTanslationKey(tanslation) + "\n";
		}
		return webTanslations;
		
	}
	
	private String getWebTanslationValue(JSONObject tanslation) {
		return (tanslation.get("value")).toString();
		
	}
	
	private String getWebTanslationKey(JSONObject tanslation) {
		return (tanslation.get("key")).toString();
		
	}
}
