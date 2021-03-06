package com.barchart.backend.testing.server;

import static com.barchart.backend.testing.server.TestVals.PREF_KEY1;
import static com.barchart.backend.testing.server.TestVals.PREF_KEY12;
import static com.barchart.backend.testing.server.TestVals.PREF_KEY13;
import static com.barchart.backend.testing.server.TestVals.PREF_KEY14;
import static com.barchart.backend.testing.server.TestVals.PREF_KEY15;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL1;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL2;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL3;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL4;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL5;
import static com.barchart.backend.testing.server.TestVals.PROFILE1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;
import org.restlet.ext.simpledb.json.JSON;
import org.restlet.ext.simpledb.json.ReplaceableItemList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.barchart.backend.client.api.PrefKey;
import com.barchart.backend.client.api.PrefMap;
import com.barchart.backend.client.api.PrefValue;

public class TestPrefBatchRestlet extends BackendServerTestBase {
	
	private static final Logger log = LoggerFactory
			.getLogger(TestPrefBatchRestlet.class);
	
	@Test
	public void test() {
		
		PrefMap prefMap = new PrefMap();
		
		List<PrefValue> tempVals = new ArrayList<PrefValue>();
		tempVals.add(PREF_VAL1);
		prefMap.put(PREF_KEY1, tempVals);
		
		tempVals = new ArrayList<PrefValue>();
		tempVals.add(PREF_VAL2);
		prefMap.put(PREF_KEY12, tempVals);
		
		tempVals = new ArrayList<PrefValue>();
		tempVals.add(PREF_VAL3);
		prefMap.put(PREF_KEY13, tempVals);
		
		tempVals = new ArrayList<PrefValue>();
		tempVals.add(PREF_VAL4);
		prefMap.put(PREF_KEY14, tempVals);
		
		tempVals = new ArrayList<PrefValue>();
		tempVals.add(PREF_VAL5);
		prefMap.put(PREF_KEY15, tempVals);
		
		
		// Test JSON
//		final ReplaceableItemList itemList = new ReplaceableItemList();
//		
//		for(Entry<PrefKey, List<PrefValue>> entry : prefMap.entrySet()) {
//			
//			final PrefValue val = entry.getValue().get(0);
//			
//			final List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();
//			
//			atts.add(new ReplaceableAttribute("text", val.getEncoding(), true));
//			
//			atts.add(new ReplaceableAttribute("time", String.valueOf(val.getTime()), true));
//			
//			itemList.add(new ReplaceableItem(ACCOUNT_ID + "," + entry.getKey().toString(), atts));
//		}
//		
//		final String entityString = JSON.intoText(itemList);
//		
//		System.out.println(entityString);
//		
//		final ReplaceableItemList testItemList = JSON.fromText(
//				entityString, ReplaceableItemList.class);
//		
//		final String domain = prefVolume.getDomainName(ACCOUNT_ID);
//		
//		final BatchPutAttributesRequest batchRequest = 
//				new BatchPutAttributesRequest(domain, testItemList);
//		
//		AWSclient.batchPutAttributes(batchRequest);
//		
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
//		GetAttributesRequest getRequest = new GetAttributesRequest(domain, PREF_KEY1.toString());
//		
//		GetAttributesResult getResult = AWSclient.getAttributes(getRequest);
//		
//		if(getResult != null) {
//			log.debug(getResult.toString());
//		} else {
//			log.debug("Get was null");
//		}
		
		putBatchPref(prefMap, PROFILE1);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
		
		PrefMap resPrefMap = selectPref(PROFILE1);
		
		log.debug(JSON.intoText(resPrefMap));
		
		
	}

}







