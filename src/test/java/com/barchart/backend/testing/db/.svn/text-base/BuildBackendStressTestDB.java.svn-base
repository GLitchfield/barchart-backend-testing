package com.barchart.backend.testing.db;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;

public class BuildBackendStressTestDB {

	private static final String AWS_ID = "AWS_ACCESS_KEY_ID";
	private static final String AWS_SECRECT = "AWS_SECRET_KEY";
	private static final String PROP_CONFIG_DOMAIN = "PARAM5";

	/** User: Gavin.Litchfield */
	private static final String id = "AKIAJXGV7WFHIOB7P44Q";
	private static final String key = "A+qCRlLZnrfskyi6CriJTqVBI/wXbOiWFDXhHDni";
	private static final String config = "simple_config";
	private static final String domain = "barchart-backend-stresstest";
	
	private static final String value = "value";
	
	private final AmazonSimpleDBClient AWSclient;
	
	public static void main(String[] args) {
		BuildBackendStressTestDB build = new BuildBackendStressTestDB();
	}
	
	public BuildBackendStressTestDB() {
	
		System.setProperty(AWS_ID, id);
		System.setProperty(AWS_SECRECT, key);
		System.setProperty(PROP_CONFIG_DOMAIN, config);
		
		AWSCredentials creds = new BasicAWSCredentials(id, key);
		
		AWSclient = new AmazonSimpleDBClient(creds);
		
		for(int i = 1; i <= 100000; i++) {
			final List<ReplaceableAttribute> atts = new LinkedList<ReplaceableAttribute>();
			atts.add(makeAtt());
			AWSclient.putAttributes(new PutAttributesRequest(domain, String.valueOf(i), atts));
		}

	}
	
	private ReplaceableAttribute makeAtt() {
		return new ReplaceableAttribute(
				value, UUID.randomUUID().toString() + UUID.randomUUID().toString(), true);
	}
	
}
