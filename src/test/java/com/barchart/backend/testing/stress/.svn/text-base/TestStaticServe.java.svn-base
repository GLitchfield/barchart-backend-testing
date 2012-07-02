package com.barchart.backend.testing.stress;

import java.io.IOException;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.Protocol;
import org.restlet.ext.httpclient.HttpClientHelper;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.routing.Template;

import com.barchart.backend.api.name.Name;
import com.barchart.backend.server.util.Smap;
import com.barchart.backend.testing.server.AuthFilter;

public class TestStaticServe {
	
private static final String location = "https://platform-backend-dev.aws.barchart.com";
	
	private static final Template path = 
			new Template(Name.Test.TEST + Name.Test.TEST_STATIC_SERVE);
	
	private static Context context;
	private static Client client;
	private static AuthFilter filter;
	
	private static final Protocol protocol = Protocol.HTTPS;
	
	public static void main(String[] args) {
		
		context = new Context();
		client = new Client(context, protocol);
		client.getContext().getParameters().set("tracing", "true");

		final HttpClientHelper helper = new HttpClientHelper(client);
		helper.setHelped(client);

		filter = new AuthFilter();

		filter.setNext(client);
		
		filter.setChallenge("ID", "secret");
		
		final Smap map = new Smap();
		
		map.put(Name.Test.SIZE, "100");
		final String uri = location + path.format(map);
		System.out.println(uri);
		
		ClientResource resource = null;
		
		try {
			
			resource = new ClientResource(uri);
			resource.setNext(filter);
			
			Representation rep = resource.get();
			
			String staticResult = rep.getText();
			System.out.println(staticResult);
			
			consumeResource(resource);
		} catch (IOException e) {
			System.out.println(resource.getStatus().getThrowable().getMessage());
			e.printStackTrace();
		}
		
	}
	
	private static void consumeResource(final ClientResource resource)
			throws IOException {
		if (resource != null) {

			//
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
	}


}
