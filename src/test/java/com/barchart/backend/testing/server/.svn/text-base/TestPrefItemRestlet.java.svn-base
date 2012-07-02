package com.barchart.backend.testing.server;

import static com.barchart.backend.testing.server.TestVals.PLUG1;
import static com.barchart.backend.testing.server.TestVals.PREF1;
import static com.barchart.backend.testing.server.TestVals.PREF_KEY1;
import static com.barchart.backend.testing.server.TestVals.PREF_VAL1;
import static com.barchart.backend.testing.server.TestVals.PROFILE1;

import org.junit.Test;
import org.restlet.ext.simpledb.util.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.backend.client.api.PrefValue;

public class TestPrefItemRestlet extends BackendServerTestBase {
	
	private static final Logger log = LoggerFactory
			.getLogger(TestPrefItemRestlet.class);
	
	@Test
	public void test() {
		
		putPref(PREF_KEY1, PREF_VAL1);
		
		PrefValue testValGet = getPref(PREF_KEY1);
		
		/**
		 * This tests our server PrefItemRestlet by performing a PUT and then
		 * comparing the value to a GET
		 */
		assertEquals(PREF_VAL1.getText(), testValGet.getText());
		
		final String itemName = ACCOUNT_ID + "," + PROFILE1 + "," + PLUG1 + "," + PREF1;
		final Props props = dbGet(prefVolume, itemName);
		final String dbTestVal = props.get("text").toString();
		
		/**
		 * This test compares the value to a raw SimpleDB lookup
		 */
		assertEquals(PREF_VAL1.getText(), dbTestVal);
		
		deletePref(PREF_KEY1);
		
		testValGet = getPref(PREF_KEY1);
		
		/**
		 * Test delete 
		 */
		assertNull(testValGet);
		
		log.debug("PrefItemRestlet tests passed");
		
	}
	

}
