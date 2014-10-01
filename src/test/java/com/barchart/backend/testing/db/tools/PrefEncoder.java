package com.barchart.backend.testing.db.tools;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.restlet.engine.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrefEncoder {
	
	private static Logger log = LoggerFactory.getLogger(PrefEncoder.class);

	static final String AWS_HEAD = "000";

	static final int AWS_BASE_PREFIX = 500;

	static final int AWS_SIZE_PREFIX = 3;

	static final int AWS_SIZE_ATTRIB = 1000;

	static {
		assert AWS_SIZE_PREFIX + AWS_SIZE_ATTRIB < 1024;
	}

	static final int AWS_COUNT_TOTAL = 250;
	static final int AWS_SIZE_TOTAL = AWS_COUNT_TOTAL * AWS_SIZE_ATTRIB;

	static final String UTF_8 = "UTF-8";

	static final Charset CS_UTF_8 = Charset.forName(UTF_8);
	
	public static List<String> amazonEncode(final String value) {

		final String result = Base64.encode(value.toCharArray(), UTF_8, false);

		final int sizeChar = result.length();

		final int count = sizeChar / AWS_SIZE_ATTRIB;

		final List<String> list = new ArrayList<String>(count);

		if (count > AWS_COUNT_TOTAL) {
			log.error("preference is too long; "
					+ "permitted: {} requested : {}", AWS_SIZE_TOTAL,
					result.length());
			log.error("preference : \n{}", value);
			return list;
		}

		list.add(AWS_HEAD);

		int step = 0;
		for (int index = 0; index < sizeChar; index += AWS_SIZE_ATTRIB) {

			final int start = index;
			final int finish = Math.min(sizeChar, index + AWS_SIZE_ATTRIB);

			final String entry = (AWS_BASE_PREFIX + step)
					+ result.substring(start, finish);

			list.add(entry);

			step++;

		}

		return list;

	}

	public static String amazonDecode(final List<String> list) {

		Collections.sort(list);

		list.remove(0); // AWS_HEAD

		int size = 0;

		for (final String item : list) {
			size += item.length();
		}

		final StringBuilder text = new StringBuilder(size);

		for (final String item : list) {
			text.append(item.substring(AWS_SIZE_PREFIX));
		}

		byte[] array = Base64.decode(text.toString());

		return new String(array, CS_UTF_8);

	}
	
}
