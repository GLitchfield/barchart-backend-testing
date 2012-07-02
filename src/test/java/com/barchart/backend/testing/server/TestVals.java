package com.barchart.backend.testing.server;

import com.barchart.backend.client.api.PrefKey;
import com.barchart.backend.client.api.PrefValue;
import com.barchart.backend.testing.util.PrefValFactory;


public class TestVals {

	public final static String PROFILE1 = "gavin";
	
	public final static String PLUG1 = "dancemachine";
	public final static String PLUG2 = "rainbows";
	public final static String PLUG3 = "fruitstand";
	public final static String PLUG4 = "burritos";
	public final static String PLUG5 = "nuclearlaunch";
	
	public final static String PREF1 = "flavor";
	public final static String PREF2 = "color";
	public final static String PREF3 = "temp";
	public final static String PREF4 = "danger";
	public final static String PREF5 = "fun";
	
	public final static String PREF_VAL1_T = "sweet";
	public final static String PREF_VAL2_T = "red";
	public final static String PREF_VAL3_T = "hot";
	public final static String PREF_VAL4_T = "sharp";
	public final static String PREF_VAL5_T = "readingthemanual";
	
	public final static PrefKey PREF_KEY1 = new PrefKey(PROFILE1, PLUG1, PREF1);
	public final static PrefKey PREF_KEY12 = new PrefKey(PROFILE1, PLUG2, PREF2);
	public final static PrefKey PREF_KEY13 = new PrefKey(PROFILE1, PLUG3, PREF3);
	public final static PrefKey PREF_KEY14 = new PrefKey(PROFILE1, PLUG4, PREF4);
	public final static PrefKey PREF_KEY15 = new PrefKey(PROFILE1, PLUG5, PREF5);
	
	public final static PrefValue PREF_VAL1 = PrefValFactory.make(PREF_VAL1_T, 0l);
	public final static PrefValue PREF_VAL2 = PrefValFactory.make(PREF_VAL2_T, 0l);
	public final static PrefValue PREF_VAL3 = PrefValFactory.make(PREF_VAL3_T, 0l);
	public final static PrefValue PREF_VAL4 = PrefValFactory.make(PREF_VAL4_T, 0l);
	public final static PrefValue PREF_VAL5 = PrefValFactory.make(PREF_VAL5_T, 0l);
	
}
