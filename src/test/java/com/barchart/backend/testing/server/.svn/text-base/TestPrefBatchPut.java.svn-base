package com.barchart.backend.testing.server;

import static com.barchart.backend.testing.server.TestVals.PREF_KEY1;
import static com.barchart.backend.testing.server.TestVals.PREF_KEY12;
import static com.barchart.backend.testing.server.TestVals.PREF_KEY13;
import static com.barchart.backend.testing.server.TestVals.PREF_KEY14;
import static com.barchart.backend.testing.server.TestVals.PREF_KEY15;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL1;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL1_T;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL2;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL2_T;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL3;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL3_T;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL4;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL4_T;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL5;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL5_T;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.barchart.backend.client.api.PrefKey;
import com.barchart.backend.client.api.PrefValue;
import com.barchart.backend.testing.util.PrefValFactory;

public class TestPrefBatchPut extends BackendServerTestBase {
	
	private static final Logger log = LoggerFactory
			.getLogger(TestPrefBatchPut.class);
	
	@Test
	public void test() {
		
		final String domain = prefVolume.getDomainName(ACCOUNT_ID);
		
		final List<ReplaceableItem> items = new LinkedList<ReplaceableItem>();
		
		items.add(makeItem(PREF_KEY1, PREF_VAL1));
		items.add(makeItem(PREF_KEY12, PREF_VAL2));
		items.add(makeItem(PREF_KEY13, PREF_VAL3));
		items.add(makeItem(PREF_KEY14, PREF_VAL4));
		items.add(makeItem(PREF_KEY15, PREF_VAL5));
		
		BatchPutAttributesRequest batchPutRequest = new BatchPutAttributesRequest(domain, items);
		
		AWSclient.batchPutAttributes(batchPutRequest);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// 1
		PrefValue resultPrefVal = getValue(PREF_KEY1);
		String resString = resultPrefVal.getText();
		log.debug("PrefVal1 : " + PREF_VAL1_T + " Result: " + resString);
		assertTrue(PREF_VAL1_T.equals(resString));
		
		// 2
		resultPrefVal = getValue(PREF_KEY12);
		resString = resultPrefVal.getText();
		log.debug("PrefVal1 : " + PREF_VAL2_T + " Result: " + resString);
		assertTrue(PREF_VAL2_T.equals(resString));
		
		// 3
		resultPrefVal = getValue(PREF_KEY13);
		resString = resultPrefVal.getText();
		log.debug("PrefVal1 : " + PREF_VAL3_T + " Result: " + resString);
		assertTrue(PREF_VAL3_T.equals(resString));
				
		// 4
		resultPrefVal = getValue(PREF_KEY14);
		resString = resultPrefVal.getText();
		log.debug("PrefVal1 : " + PREF_VAL4_T + " Result: " + resString);
		assertTrue(PREF_VAL4_T.equals(resString));				
		
		// 5
		resultPrefVal = getValue(PREF_KEY15);
		resString = resultPrefVal.getText();
		log.debug("PrefVal1 : " + PREF_VAL5_T + " Result: " + resString);
		assertTrue(PREF_VAL5_T.equals(resString));
		
	}
	
	private PrefValue getValue(final PrefKey key) {
		
		final String domain = prefVolume.getDomainName(ACCOUNT_ID);
		
		GetAttributesRequest getRequest = new GetAttributesRequest(
				domain, ACCOUNT_ID + "," + key.toString());
		
		GetAttributesResult getResult = AWSclient.getAttributes(getRequest);
		
		List<Attribute> atts = getResult.getAttributes();
		
		String text = null;
		long time = -1;
		
		for(Attribute att : atts) {
			if(att.getName().equals("text")) {
				text = att.getValue();
			}
			if(att.getName().equals("time")) {
				time = Long.parseLong(att.getValue());
			}
		}
		
		if(text != null && time != -1) {
			return PrefValFactory.make(text, time);
		}
		
		return null;
	}
	
	private ReplaceableItem makeItem(PrefKey key, PrefValue val) {
		
		final String itemName = ACCOUNT_ID + "," + key.toString();
		
		return new ReplaceableItem(itemName, makeAttributes(val));
		
	}
	
	private List<ReplaceableAttribute> makeAttributes(final PrefValue prefVal) {
		
		final List<ReplaceableAttribute> atts = new LinkedList<ReplaceableAttribute>();
		
		atts.add(new ReplaceableAttribute("text", prefVal.getText(), true));
		
		atts.add(new ReplaceableAttribute("time", String.valueOf(prefVal.getTime()), true));
		
		return atts;
		
	}
	
	

}
