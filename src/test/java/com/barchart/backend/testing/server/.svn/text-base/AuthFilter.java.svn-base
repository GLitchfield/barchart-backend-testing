package com.barchart.backend.testing.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CopyOnWriteArrayList;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Cookie;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.routing.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthFilter extends Filter {
	
	private static final String httpsOverrideHash = "929df8f6cba42cd248eee6c59c6ea6d559b92a3ae44dc06244a63ce9819a5a5b";
	private static final String httpsOverrideProp = "httpsOverride";

	private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

	private final CopyOnWriteArrayList<Cookie> cookies = new CopyOnWriteArrayList<Cookie>();

	private final ChallengeResponse challenge = new ChallengeResponse(
			ChallengeScheme.HTTP_BASIC);

	public void setChallenge(final String id, final String secret) {

		challenge.setIdentifier(id);
		challenge.setSecret(secret);

		cookies.clear();

	}

	@Override
	protected int beforeHandle(final Request request, final Response response) {

		final String httpsOverride = System.getProperty(httpsOverrideProp);
		if (httpsOverride == null
				|| !httpsOverrideHash.equals(sha256(httpsOverride))) {
			if (Protocol.HTTPS != request.getProtocol()) {
				response.setStatus(Status.CLIENT_ERROR_FORBIDDEN,
						"must use https");
				return STOP;
			}
		}

		request.setChallengeResponse(challenge);

		request.getCookies().addAll(cookies);

		return CONTINUE;

	}

	@Override
	protected void afterHandle(final Request request, final Response response) {

		cookies.addAllAbsent(response.getCookieSettings());

	}

	private String sha256(final String in) {
		try {
			final byte byteData[] = MessageDigest.getInstance("SHA-256")
					.digest(in.getBytes());
			final StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return sb.toString();
		} catch (final NoSuchAlgorithmException e) {
			log.error("Hash failed :{}", in);
			e.printStackTrace();
		}
		return null;
	}


}
