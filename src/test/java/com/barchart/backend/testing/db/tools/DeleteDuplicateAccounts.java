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
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;

public class DeleteDuplicateAccounts {

	public static final String ACCT_VOLUME_ID = "volume|platform_accounts";
	public static final String PREF_VOLUME_ID = "volume|platform_preferences";

	private static AmazonSimpleDBClient AWSclient;

	public static void main(final String[] args) throws Exception {

		AWSclient = SimpleDBClientFactory.make("AKIAJYUTSX5C4HB5GZMA",
				 "oCa9h7Qgwv7jTPL1d7g3BM0S/P0e5fr1bR41e3lg");

		final Map<String, Volume> volMap = getVolumeMap();

		final Volume actVolume = volMap.get(ACCT_VOLUME_ID);
		final Volume prefVolume = volMap.get(PREF_VOLUME_ID);

		// 2500 is max
		final String query = " limit 2500 ";

		final Map<String, List<Props>> accountData = VolumeUtil.selectItems(
				AWSclient, actVolume, query);

		final Map<String, List<Props>> byName = new HashMap<String, List<Props>>();
		
		// Sort by account name
		for (final Entry<String, List<Props>> entry : accountData.entrySet()) {

			if (entry.getValue().size() != 1) {
				System.out.println("itemName with muiltiple props");
				continue;
			}

			final String username = entry.getValue().get(0).get("username")
					.toString();
			
			if (!byName.containsKey(username)) {
				byName.put(username, new LinkedList<Props>());
			}

			byName.get(username).add(entry.getValue().get(0));

		}
		
		final List<String> toDelete = new ArrayList<String>();

		final String prefQuery = "where itemName() like ";
		final Map<String, List<Map<String, List<Props>>>> prefs = 
				new HashMap<String, List<Map<String, List<Props>>>>();

		StringBuilder sb = new StringBuilder();
		
		/* Query each username with duplicates and store */
		for (final Entry<String, List<Props>> entry : byName.entrySet()) {
			if (entry.getValue().size() > 1) {

				if (entry.getKey().startsWith("QUD")) {
//						|| entry.getValue().get(0).get("vendor_id")
//							//	.equals("coffeenetwork")) {
//								.equals("Quodd")) {

					prefs.put(entry.getKey(),
							new ArrayList<Map<String, List<Props>>>());

					sb.append("Name: " + entry.getKey() + "\n");
					for (final Props p : entry.getValue()) {
						sb.append("\t" + printProp(p) + "\n");

						toDelete.add(p.get("account_id").toString());

						Map<String, List<Props>> tempProps = 
								VolumeUtil.selectItems(AWSclient, prefVolume,
										prefQuery + "'" + p.get("account_id")
												+ "%'" + query);
						
						if(tempProps.isEmpty()) {
							toDelete.add((String) p.get("account_id"));
						}
						
						prefs.get(entry.getKey()).add(tempProps);
					}
					
					/* Prints full map */
					System.out.println(sb.toString());
				}
			}
		}
		
//		for (final Entry<String, List<Map<String, List<Props>>>> entry : prefs
//				.entrySet()) {
//
//			sb = new StringBuilder();
//
//			sb.append(entry.getKey() + "\n");
//
//			for (final Map<String, List<Props>> prefList : entry.getValue()) {
//
//				for (final Entry<String, List<Props>> prefMap : prefList
//						.entrySet()) {
//
//					sb.append("\t" + prefMap.getKey() + "\n");
//
//					for (final Props prop : prefMap.getValue()) {
//						sb.append("\t\t" + printProp(prop) + "\n");
//					}
//				}
//			}
//
//			System.out.println(sb.toString());
//
//		}
		
		
		/* THIS DELETES EVERYTHING - BE VERY CAREFUL */
		/* WHY DO WE EVEN HAVE THIS??? */
		/* Batch Delete */
//		for(String id : toDelete) {
//			System.out.println("Deleting " + id);
//			for (int i = 0; i < actVolume.getDomainCount(); i++) {
//				final DeleteAttributesRequest delete = new DeleteAttributesRequest(
//						actVolume.getDomainName(i), id);
//				AWSclient.deleteAttributes(delete);
//			}
//		}
		
		/* For individual deletes */
//		String singleDelete = "07f1b263-e723-4fa3-940f-1251b1adbd77";
//		System.out.println("Deleting " + singleDelete);
//		for (int i = 0; i < actVolume.getDomainCount(); i++) {
//			final DeleteAttributesRequest delete = new DeleteAttributesRequest(
//					actVolume.getDomainName(i), singleDelete);
//			AWSclient.deleteAttributes(delete);
//		}

	}

	private static String printProp(final Props p) {
		final StringBuilder sb = new StringBuilder();

		for (final Entry<String, Object> e : p.entrySet()) {
			sb.append(e.getKey() + ":" + e.getValue().toString() + " ");
		}

		return sb.toString();
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

}
