package com.barchart.backend.testing.db;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;

import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;

public class SimpleDBJacksonModuleFactory  {

	// Static factory method
	// Serializers and deserializers for SimpleDB objects
	public static Module make() {
		
		SimpleModule module = new SimpleModule("SimpleDB", new Version(0,1,1,"alpha"));
		
		module.addSerializer(ReplaceableAttribute.class, new JsonSerializer<ReplaceableAttribute>() {
			
			@Override
			public void serialize(ReplaceableAttribute att, JsonGenerator json,
					SerializerProvider arg2) throws IOException,
					JsonProcessingException {
				
				json.writeStartObject();
				json.writeStringField("name", att.getName());
				json.writeStringField("value", att.getValue());
				json.writeBooleanField("replace", att.getReplace());
				json.writeEndObject();
			}
		});
		
		module.addDeserializer(ReplaceableAttribute.class, new JsonDeserializer<ReplaceableAttribute>() {
			
			@Override
			public ReplaceableAttribute deserialize(JsonParser jsonParser,
					DeserializationContext arg1) throws IOException,
					JsonProcessingException {
				
				final ObjectCodec oc = jsonParser.getCodec();
				final JsonNode node = oc.readTree(jsonParser);
				return new ReplaceableAttribute(node.get("name").getTextValue(),
						node.get("value").getTextValue(), node.get("replace").getBooleanValue());
			}
		});
		
		module.addSerializer(ReplaceableItem.class, new JsonSerializer<ReplaceableItem>() {

			@Override
			public void serialize(ReplaceableItem value, JsonGenerator jgen,
					SerializerProvider provider) throws IOException,
					JsonProcessingException {
				
				jgen.writeStartObject();
				jgen.writeStringField("name", value.getName());
				jgen.writeObjectField("attributes", value.getAttributes());
				jgen.writeEndObject();
			}
		});
		
		module.addDeserializer(ReplaceableItem.class, new JsonDeserializer<ReplaceableItem>() {

			@Override
			public ReplaceableItem deserialize(JsonParser jp,
					DeserializationContext ctxt) throws IOException,
					JsonProcessingException {
				
				final ObjectCodec oc = jp.getCodec();
				final JsonNode node = oc.readTree(jp);
				
				List<ReplaceableAttribute> atts = new LinkedList<ReplaceableAttribute>();
				Iterator<JsonNode> jsonAtts = node.get("attributes").getElements();
				while(jsonAtts.hasNext()) {
					JsonNode tempNode = jsonAtts.next();
					atts.add(new ReplaceableAttribute(tempNode.get("name").getTextValue(), 
							tempNode.get("value").getTextValue(), tempNode.get("replace").getValueAsBoolean()));
				}
				
				return new ReplaceableItem(node.get("name").getTextValue(), atts);
			}
			
		});
		
		
		return module;
	}

}
