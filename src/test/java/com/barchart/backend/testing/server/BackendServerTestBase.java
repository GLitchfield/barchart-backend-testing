package com.barchart.backend.testing.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Cookie;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.ext.httpclient.HttpClientHelper;
import org.restlet.ext.simpledb.api.Volume;
import org.restlet.ext.simpledb.bean.VolumeBean;
import org.restlet.ext.simpledb.json.JSON;
import org.restlet.ext.simpledb.json.ReplaceableItemList;
import org.restlet.ext.simpledb.props.SDBProperties;
import org.restlet.ext.simpledb.props.SDBPropertiesLoader;
import org.restlet.ext.simpledb.props.SDBVolumesProperties;
import org.restlet.ext.simpledb.util.AQ;
import org.restlet.ext.simpledb.util.Props;
import org.restlet.ext.simpledb.util.SimpleUtil;
import org.restlet.ext.simpledb.util.VolumeUtil;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.GetAttributesRequest;
import com.amazonaws.services.simpledb.model.GetAttributesResult;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.barchart.backend.api.bean.Session;
import com.barchart.backend.api.name.Name;
import com.barchart.backend.api.name.Rest;
import com.barchart.backend.client.api.PrefKey;
import com.barchart.backend.client.api.PrefMap;
import com.barchart.backend.server.util.Smap;
import com.barchart.backend.client.api.PrefValue;

public abstract class BackendServerTestBase extends TestCase {

	private static final Logger log = LoggerFactory
			.getLogger(BackendServerTestBase.class);
	
	// Dummy barchart backend login creds
	
	final static String LOCATION = "https://platform-backend-dev.aws.barchart.com";
	final static String VENDOR = "barchart";
	final static String USERNAME = TestVals.PROFILE1;
	final static String PASSWORD = "gavin";
	final static String SESSION_ID = "123456";
	public static String ACCOUNT_ID;
	
	// Templates
	
	private final Template pathSessionInit = new Template(Name.API_SESSIONS);
	private final Template pathPrefItem = new Template(Name.API_PREF_ITEM);
	private final Template pathPrefBatch = new Template(Name.API_PREF_BATCH);
	private final Template pathSelect = new Template(Name.API_PREF_SELECT);
	
	// HTTPS override props
	
	private static final String httpsOverrideProp = "httpsOverride";
	private static final String httpsOverrideKey = "DEBUG_Barchart";

	// AWS cred prop names
	
	private static final String AWS_ID = "AWS_ACCESS_KEY_ID";
	private static final String AWS_SECRECT = "AWS_SECRET_KEY";
	private static final String PROP_CONFIG_DOMAIN = "PARAM5";
	
	// Volume
	
	private Map<String,Volume> volumeMap = new HashMap<String,Volume>();
	
	public static final String PREF_VOLUME_ID = "volume|platform_preferences";
	public static final String ACCT_VOLUME_ID = "volume|platform_accounts";
	
	public Volume prefVolume;
	public Volume actVolume;
	
	// Restlet client objects
	
	private Context context;
	private Protocol protocol;
	private Client client;
	private AuthFilter filter;
	
	// AWS client
	
	public AmazonSimpleDBClient AWSclient;
	
	@Before
	public void setUp() throws Exception {
		
		// Init Restlet objects
		
		protocol = Protocol.HTTPS;
		context = new Context();
		client = new Client(context, protocol);
		
		final HttpClientHelper helper = new HttpClientHelper(client);
		helper.setHelped(client);
		
		filter = new AuthFilter();
		filter.setNext(client);
		
		filter.setChallenge(USERNAME, PASSWORD);
		
		// Override https requirement for testing
		
		System.setProperty(httpsOverrideProp, httpsOverrideKey);
		
		// Init AWS client
		
		/** User: Gavin.Litchfield */
		final String id = "AKIAJXGV7WFHIOB7P44Q";
		final String key = "A+qCRlLZnrfskyi6CriJTqVBI/wXbOiWFDXhHDni";
		final String config = "simple_config";
		
		System.setProperty(AWS_ID, id);
		System.setProperty(AWS_SECRECT, key);
		System.setProperty(PROP_CONFIG_DOMAIN, config);
		
		AWSCredentials creds = new BasicAWSCredentials(id, key);
		
		AWSclient = new AmazonSimpleDBClient(creds);
		
		// Build volume map
		
		volumeMap = getVolumeMap();
		
		prefVolume = volumeMap.get(PREF_VOLUME_ID);
		actVolume = volumeMap.get(ACCT_VOLUME_ID);
		
		// Retrieve accountID for test login
		
		final Session session = getSession();
		
		ACCOUNT_ID = session.getAccountId();
		
		// Clean DB
		
		AQ.init();
		
		final String s = AQ.where().item().like().attr("'" + ACCOUNT_ID +"%" + "'").end();
		
		final Map<String,List<Props>> toDelete = dbSelectQuery(s);
		
		log.debug("Cleaning DB");
		for(String itemName : toDelete.keySet()) {
			
			try {
				final PrefKey dkey = new PrefKey(itemName.split(",")[1],
						itemName.split(",")[2],
						itemName.split(",")[3]);
				log.debug("Deleting " + dkey.toString());
				deletePref(dkey);
			} catch (Exception e) {
				log.error("Failed to Delete " + itemName);
			}
			
		}
		
	}
	
	private Map<String,Volume> getVolumeMap() throws Exception {
		
		SDBProperties sdbProps = SDBPropertiesLoader.load();
		String domain = sdbProps.getSDBConfigDomain();
		String propsItem = SDBVolumesProperties.ITEM_VOLUMES_PROPS;

		SDBVolumesProperties props = SimpleUtil.getObject(//
				AWSclient, domain, propsItem, SDBVolumesProperties.class);

		String prefix = props.getSearchPrefix();

		Map<String,List<VolumeBean>> volumeBeanMap = SimpleUtil.findItems(AWSclient, domain,
				prefix, VolumeBean.class);
		
		Map<String,Volume> volMap = new HashMap<String,Volume>();
		
		for(Entry<String,List<VolumeBean>> entry : volumeBeanMap.entrySet()) {
			
			if(entry.getValue().size() == 1) {
				volMap.put(entry.getKey(), entry.getValue().get(0));
			}
			
		}
		
		return volMap;
	}
	
	private Session getSession() {
		
		ClientResource resource = null;
		try {
			
			resource = makeSessionResource();
			return JSON.fromText(resource.get(
					MediaType.TEXT_PLAIN).getText(), Session.class);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(resource.getStatus().getDescription());
			log.debug(resource.getStatus().getReasonPhrase());
			assertTrue(false);
		} finally {
			consumeResource(resource);
		}
		return null;
	}
	
	@After
	public void tearDown() throws Exception {
		
		AWSclient.shutdown();
		
	}
	
	// Methods for raw simpleDB access
	
	protected Map<String,List<Props>> dbSelectQuery(final String query) {
		
		try {
			
			return VolumeUtil.selectItems(AWSclient, prefVolume, query);
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		return null;
	}
	
	protected Props dbGet(Volume volume, final String itemName) {
		
		try {
			
			final String domain = volume.getDomainName(itemName);
			final GetAttributesResult result = 
					AWSclient.getAttributes(new GetAttributesRequest(domain, itemName));
			final List<Attribute> atts = result.getAttributes();
			final Props props = new Props();
			
			for(Attribute att : atts) {
				props.put(att.getName(), att.getValue());
			}
			return props;
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		return null;
	}
	
	void dbPut(final String itemName, final List<Props> data) {
		
	}
	
	void dbDelete(final String itmeName) {
		
	}
	
	// Barchart backend Restlet methods
	
	protected PrefValue getPref(PrefKey key) {
		
		ClientResource prefGet = null;
		try {
			prefGet = makePrefItemResource(key);
			
			Representation prefRep = null;
			
			try {
				prefRep = prefGet.get();
			} catch (ResourceException e){
				// Returns null if resource is not found
				if(e.getStatus().getCode() == 404) {
					return null;
				} else {
					throw new Exception(e);
				}
			}
			String temp = (prefRep.getText());	
			System.out.println(temp);
			return JSON.fromText(temp, PrefValue.class);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			consumeResource(prefGet);
		}
		
		return null;
	}
	
	protected PrefMap selectPref(String profile) {
		ClientResource prefSelect = null;
		try {
			prefSelect = makePrefSelectResource(profile);
			final Representation rep = prefSelect.get();
			final String json = rep.getText(); 
			log.debug(json);
			final PrefMap propMap = JSON.fromText(json, PrefMap.class);
			return propMap;
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			consumeResource(prefSelect);
		}
		
		return null;
	}
	
	protected void putPref(PrefKey key, PrefValue value) {
		
		ClientResource prefPut = null;
		try {
			prefPut = makePrefItemResource(key);
			prefPut.put(value, MediaType.TEXT_PLAIN);
			
			// Delay for simpleDB to catch up
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			consumeResource(prefPut);
		}
		
	}
	
	protected void putBatchPref(final PrefMap prefMap, final String profile) {
		
		// Make itemList from prefMap
		final ReplaceableItemList itemList = new ReplaceableItemList();
		
		for(Entry<PrefKey, List<PrefValue>> entry : prefMap.entrySet()) {
			
			final PrefValue val = entry.getValue().get(0);
			
			final List<ReplaceableAttribute> atts = new ArrayList<ReplaceableAttribute>();
			
			atts.add(new ReplaceableAttribute("text", val.getEncoding(), true));
			
			atts.add(new ReplaceableAttribute("time", String.valueOf(val.getTime()), true));
			
			itemList.add(new ReplaceableItem(ACCOUNT_ID + "," + entry.getKey().toString(), atts));
		}
		
		final String entityString = JSON.intoText(itemList);
		
		log.debug(entityString);
		
		ClientResource prefBatch = null;
		
		try {
			prefBatch = makePrefBatchResource(profile);
			prefBatch.put(entityString, MediaType.APPLICATION_JSON);
			
			Thread.sleep(1000);
		} catch (Exception e) {
			log.debug(prefBatch.getStatus().getDescription());
			log.debug(prefBatch.getStatus().getReasonPhrase());
			
			e.printStackTrace();
			assertTrue(false);
		} finally {
			consumeResource(prefBatch);
		}
		
	}
	
	protected void deletePref(PrefKey key) {
		
		ClientResource prefDelete = null;
		try {
			prefDelete = makePrefItemResource(key);
			prefDelete.delete();
			
			// Delay for simpleDB to catch up
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			consumeResource(prefDelete);
		}
		
	}

	// Barchart backend client resource builders
	
	/**
	 * Gets the Session, which is just a hamfisted way of getting the accountID
	 * @return
	 */
	ClientResource makeSessionResource() {

		final Smap map = new Smap();
		map.put(Name.Id.VENDOR, VENDOR);

		final String uri = LOCATION + pathSessionInit.format(map);

		final ClientResource res = new ClientResource(uri);
		
		res.setNext(filter);
		
		log.debug("Init uri : {}", uri);

		return res;
		
	}
	
	/**
	 * Gets, Puts, or Deletes individual preferences
	 * @param acctID
	 * @param key
	 * @return
	 */
	ClientResource makePrefItemResource(final PrefKey key) {
		
		final Smap map = new Smap();

		map.put(Name.Id.VENDOR, VENDOR);
		map.put(Name.Id.ACCOUNT, ACCOUNT_ID);

		map.put(Name.Id.PROFILE, key.getProfileId());
		map.put(Name.Id.PLUGIN, key.getPluginId());
		map.put(Name.Id.PREF, key.getPrefId());

		final String uri = LOCATION + pathPrefItem.format(map);

		final ClientResource res = new ClientResource(uri);
		
		res.getCookies().add(new Cookie(Rest.SESSION, SESSION_ID));
		
		res.setNext(filter);
		
		return res;
		
	}
	
	/**
	 * 
	 * @param profile
	 * @return
	 */
	ClientResource makePrefSelectResource(final String profileToQry) {
		
		final Smap map = new Smap();
		
		map.put(Name.Id.VENDOR, VENDOR);
		map.put(Name.Id.ACCOUNT, ACCOUNT_ID);
		
		map.put(Name.Id.PROFILE, profileToQry);
		
		final String uri = LOCATION + pathSelect.format(map);
		
		log.debug(uri);
		
		final ClientResource res = new ClientResource(uri);
		
		res.getCookies().add(new Cookie(Rest.SESSION, SESSION_ID));
		
		res.setNext(filter);
		
		return res;
	}
	
	/**
	 * 
	 * @param profile
	 * @return
	 */
	ClientResource makePrefBatchResource(final String profile) {
		
		final Smap map = new Smap();
		map.put(Name.Id.VENDOR, VENDOR);
		map.put(Name.Id.ACCOUNT, ACCOUNT_ID);
		map.put(Name.Id.PROFILE, profile);
		
		final String uri = LOCATION + pathPrefBatch.format(map);
		
		log.debug(uri);
		
		final ClientResource res = new ClientResource(uri);
		
		res.getCookies().add(new Cookie(Rest.SESSION, SESSION_ID));
		
		res.setNext(filter);
		
		return res;
	}
	
	/**
	 * Released and exhausts a client resource
	 * @param resource
	 */
	void consumeResource(final ClientResource resource) {
		
		try {
			if (resource != null) {
				
				final Representation request = resource.getRequestEntity();
				if (request != null) {
					request.release();
					request.exhaust();
				}
	
				final Representation response = resource.getResponseEntity();
				if (response != null) {
					response.release();
					response.exhaust();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
	}
	
}
