package com.barchart.backend.testing.db;

import com.barchart.backend.api.util.JSON;
import com.barchart.backend.client.api.PrefValue;
import com.barchart.backend.testing.util.PrefValFactory;

public class TestJSONPrefKey {
	
	
	
	public static void main(String[] args) {
		
//		PrefKey key = new PrefKey("profile","plugin","pref");
//		
//		String keyString = JSON.intoText(key);
//		
//		System.out.println(keyString);
//		
//		PrefKey newKey = JSON.fromText(keyString, PrefKey.class);
//		
//		System.out.println(newKey.getProfileId() + " " + newKey.getPluginId() + " " + newKey.getPrefId());
		
		
		
		//// VALUE
		
		PrefValue val = PrefValFactory.make("sweet", 0l);
		
		String valString = JSON.intoText(val);
		
		System.out.println(valString);
		
		PrefValue newVal = JSON.fromText(valString, PrefValue.class);
		
		System.out.println(newVal.getText());
		
		
//		String fromSimpleDB = "{" + "time : \"0\", text : \"sweet\"}";
//		
//		PrefValue val = JSON.fromText(fromSimpleDB, PrefValue.class);
//		
//		System.out.println(val.getText());
		
		
		
		
	}

}
