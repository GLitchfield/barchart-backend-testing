package com.barchart.backend.testing.logging;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;

public class ThrowableJacksonModuleFactory {

	public static Module make() {
		
		SimpleModule module = new SimpleModule("SimpleDB", new Version(0,1,1,"alpha"));
		
		module.addSerializer(Throwable.class, new JsonSerializer<Throwable>() {

			@Override
			public void serialize(Throwable value, JsonGenerator jgen,
					SerializerProvider provider) throws IOException,
					JsonProcessingException {
				
				jgen.writeStartObject();
				jgen.writeStringField("name", value.getMessage());
				jgen.writeFieldName("stack trace");
				jgen.writeStartArray();
				
				for(StackTraceElement e : value.getStackTrace()) {
					jgen.writeString(e.toString());
				}
				jgen.writeEndArray();
				jgen.writeEndObject();
				
			}
			
		});
		
		return module;
		
	}
	
}
