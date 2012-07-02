package com.barchart.backend.testing.db;

import java.util.LinkedList;
import java.util.List;

import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.barchart.backend.api.util.JSON;

public class TestSimpleDBJacksonModule {
	
	public static void main(final String[] args) {
		
		final String name = "Gavin";
		final String value = "GavinVal";
		
		JSON.registerModule(SimpleDBJacksonModuleFactory.make());
		
		final ReplaceableAttribute testAtt = new ReplaceableAttribute(name, value, true);
		
		final String json = JSON.intoText(testAtt);
		
		System.out.println(json);
		System.out.println(testAtt.isReplace());
		
		final ReplaceableAttribute newTestAtt= JSON.fromText(json, ReplaceableAttribute.class);
		
		System.out.println(newTestAtt.getName());
		System.out.println(newTestAtt.getValue());
		System.out.println(newTestAtt.getReplace());
		
		final List<ReplaceableAttribute> atts = new LinkedList<ReplaceableAttribute>();
		
		atts.add(newTestAtt);
		
		final ReplaceableItem testItem = new ReplaceableItem("GavinItem", atts);
		
		final String itemJSON = JSON.intoText(testItem);
		
		System.out.println(itemJSON);
		
		final ReplaceableItem newTestItem = JSON.fromText(itemJSON, ReplaceableItem.class);
		
		System.out.println(newTestItem.getName());
		System.out.println(newTestItem.getAttributes().get(0).getName());
		
	}

}
