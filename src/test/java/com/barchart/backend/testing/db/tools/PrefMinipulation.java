package com.barchart.backend.testing.db.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.restlet.ext.simpledb.api.Volume;
import org.restlet.ext.simpledb.bean.VolumeBean;
import org.restlet.ext.simpledb.props.SDBProperties;
import org.restlet.ext.simpledb.props.SDBPropertiesLoader;
import org.restlet.ext.simpledb.props.SDBVolumesProperties;
import org.restlet.ext.simpledb.util.Props;
import org.restlet.ext.simpledb.util.SimpleUtil;
import org.restlet.ext.simpledb.util.VolumeUtil;

import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.barchart.backend.api.util.JSON;
import com.barchart.backend.client.api.PrefMap;

public class PrefMinipulation {

	public static final String ACCT_VOLUME_ID = "volume|platform_accounts";
	public static final String PREF_VOLUME_ID = "volume|platform_preferences";

	private static AmazonSimpleDBClient AWSclient;
	
	public static void main(final String[] args) throws Exception {
		
		AWSclient = SimpleDBClientFactory.make();

		final Map<String, Volume> volMap = getVolumeMap();

		final Volume actVolume = volMap.get(ACCT_VOLUME_ID);
		final Volume prefVolume = volMap.get(PREF_VOLUME_ID);

		// 2500 is max
		final String query = " limit 2500 ";

		/* Get map of all account IDs */
		final Map<String, List<Props>> accountData = VolumeUtil.selectItems(
				AWSclient, actVolume, query);
		
		final String prefQuery = "where itemName() like ";
		final Map<String, Map<String, List<Props>>> prefs = 
				new HashMap<String, Map<String, List<Props>>>();

		StringBuilder sb = new StringBuilder();
		
		/* For each account ID, get it's preferences */
		for (final Entry<String, List<Props>> entry : accountData.entrySet()) {

			final String accountID = entry.getKey();
			
			if(!accountID.equals("db01fed7-80c7-4332-9741-a5ea7311e194")) {
				continue;
			}
			
			prefs.put(accountID,
					new HashMap<String, List<Props>>());

			sb.append("Name: " + accountID + "\n");
			for(final Props p : entry.getValue()) {
				//sb.append("\t" + printProp(p) + "\n");

				Map<String, List<Props>> tempProps = 
						VolumeUtil.selectItems(AWSclient, prefVolume,
								prefQuery + "'" + p.get("account_id")
										+ "%'" + query);
				
				//For a mass pull, no reason to store each pref, need to do whatever 
				// inside this loop
				prefs.get(accountID).putAll(tempProps);
				
				for(Entry<String, List<Props>> e : tempProps.entrySet()) {
					sb.append(e.getKey() + "\n");
					for(Props pr : e.getValue()) {
						sb.append(printProp(pr) + "\n");
					}
				}
				sb.append("\n");
			}
			
//			/* Prints full map */
			//System.out.println(sb.toString());
			
		}
		
	}
	
	private static Map<String, Volume> getVolumeMap() throws Exception {

		final SDBProperties sdbProps = SDBPropertiesLoader.load();
		final String domain = sdbProps.getSDBConfigDomain();
		final String propsItem = SDBVolumesProperties.ITEM_VOLUMES_PROPS;

		final SDBVolumesProperties props = SimpleUtil.getObject(//
				AWSclient, domain, propsItem, SDBVolumesProperties.class);

		final String prefix = props.getSearchPrefix();

		final Map<String, List<VolumeBean>> volumeBeanMap = SimpleUtil
				.findItems(AWSclient, domain, prefix, VolumeBean.class);

		final Map<String, Volume> volMap = new HashMap<String, Volume>();

		for (final Entry<String, List<VolumeBean>> entry : volumeBeanMap
				.entrySet()) {

			if (entry.getValue().size() == 1) {
				volMap.put(entry.getKey(), entry.getValue().get(0));
			}

		}

		return volMap;
	}
	
	@SuppressWarnings("unchecked")
	private static String printProp(final Props p) {
		final StringBuilder sb = new StringBuilder();

		List<String> prefs = (List<String>) p.get("text");
		
		String json = PrefEncoder.amazonDecode(prefs);

		PrefMap newMap = JSON.fromText(json, PrefMap.class);
		
		return sb.toString();
	}
	
}
