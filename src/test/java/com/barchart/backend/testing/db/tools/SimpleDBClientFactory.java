package com.barchart.backend.testing.db.tools;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;

public class SimpleDBClientFactory {

	private static final String AWS_ID = "AWS_ACCESS_KEY_ID";
	private static final String AWS_SECRECT = "AWS_SECRET_KEY";
	private static final String PROP_CONFIG_DOMAIN = "PARAM5";
	
	public static AmazonSimpleDBClient make() {
		
		/** User: Gavin.Litchfield */
		final String id = System.getProperty(AWS_ID);
		final String key = System.getProperty(AWS_SECRECT);
		final String config = "simple_config";
		
		System.setProperty(PROP_CONFIG_DOMAIN, config);
		
		final AWSCredentials creds = new BasicAWSCredentials(id, key);
		
		return new AmazonSimpleDBClient(creds);
		
	}
	
}
