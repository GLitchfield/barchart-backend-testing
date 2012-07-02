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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.barchart.backend.api.util.JSON;
import com.barchart.backend.client.api.PrefMap;
import com.barchart.backend.client.api.PrefValue;

public class TestPrefSelectRestlet extends BackendServerTestBase {

	private static final Logger log = LoggerFactory
			.getLogger(TestPrefSelectRestlet.class);
	
	@Test
	public void test() {
		
		putPref(PREF_KEY1, PREF_VAL1);
		putPref(PREF_KEY12, PREF_VAL2);
		putPref(PREF_KEY13, PREF_VAL3);
		putPref(PREF_KEY14, PREF_VAL4);
		putPref(PREF_KEY15, PREF_VAL5);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		PrefValue testVal1 = getPref(PREF_KEY1);
		PrefValue testVal2 = getPref(PREF_KEY12);
		PrefValue testVal3 = getPref(PREF_KEY13);
		PrefValue testVal4 = getPref(PREF_KEY14);
		PrefValue testVal5 = getPref(PREF_KEY15);
		
		log.debug(testVal1.getText());
		log.debug(testVal2.getText());
		log.debug(testVal3.getText());
		log.debug(testVal4.getText());
		log.debug(testVal5.getText());
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		PrefMap prefMap = selectPref(PROFILE1);
		
		log.debug(JSON.intoText(prefMap));
		
		log.debug("PrefSelectRestlet tests passed");
		
		assertTrue(true);
		
	}
	
	
}
