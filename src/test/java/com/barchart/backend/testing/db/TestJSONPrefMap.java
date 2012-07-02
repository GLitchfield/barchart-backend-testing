package com.barchart.backend.testing.db;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.barchart.backend.api.util.JSON;
import com.barchart.backend.client.api.PrefKey;
import com.barchart.backend.client.api.PrefMap;
import com.barchart.backend.client.api.PrefValue;
import com.barchart.backend.testing.server.TestVals;

public class TestJSONPrefMap {
	
	
	public static void main(String[] args) {
		PrefMap map = new PrefMap();
		
		List<PrefValue> list = new LinkedList<PrefValue>();
		
		list.add(TestVals.PREF_VAL1);
		map.put(TestVals.PREF_KEY1, list);
		
		list = new LinkedList<PrefValue>();
		list.add(TestVals.PREF_VAL2);
		map.put(TestVals.PREF_KEY12, list);
		
		list = new LinkedList<PrefValue>();
		list.add(TestVals.PREF_VAL3);
		map.put(TestVals.PREF_KEY13, list);
		
		list = new LinkedList<PrefValue>();
		list.add(TestVals.PREF_VAL4);
		map.put(TestVals.PREF_KEY14, list);
		
		String mapString = JSON.intoText(map);
		
		System.out.println(mapString);
		
		PrefMap newMap = JSON.fromText(mapString, PrefMap.class);
		
		String newMapString = JSON.intoText(newMap);
		
		for(Entry<PrefKey, List<PrefValue>> entry : newMap.entrySet()) {
			
			System.out.println(entry.getKey());
			
			for(PrefValue val : entry.getValue()) {
				System.out.println(val.getText());
			}
			
			System.out.println();
		}
		
		
	}

}
