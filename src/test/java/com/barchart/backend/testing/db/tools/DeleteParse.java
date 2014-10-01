package com.barchart.backend.testing.db.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.restlet.engine.util.Base64;

import com.barchart.backend.client.api.PrefValue;

public class DeleteParse {
	
	static final String OLD_ACT = "f9595d10-1556-41d8-88e8-8cdcbde0843a";
	static final String TARGET_ACC_ID = "a952de29-6b07-4c13-9d62-56a95bd4c675";
	
	public static void main(final String[] args) throws Exception {
		
		final File file = new File("/home/gavin/Desktop/expocafe_Prefs_0");
		
		final InputStream inStream = new FileInputStream(file);
		final Reader inStreamR = new InputStreamReader(inStream);
		final BufferedReader reader = new BufferedReader(inStreamR);
		
		String key = reader.readLine().replace("\t", "").replace(OLD_ACT, TARGET_ACC_ID);
		System.out.println(key);
		
		String val = reader.readLine().replace("\t", "");
		
		String time = val.split(":")[1].split(" ")[0];
		
		String text = val.split(":")[2].substring(1);
		text = text.substring(0, text.length()-1);
		
		final List<String> prefs  = new ArrayList<String>();
		
		for(String s : text.split(",")) {
			
			s = s.trim();
			prefs.add(s);
			
		}
		
		System.out.println(amazonDecode(prefs));
		
		
		
		PrefValue value = new PrefValue();
		
		value.setTime(Long.parseLong(time));
		value.setText(text);
		
		System.out.println(value.getEncoding());
		System.out.println(value.getText());
	}
	
	static final int AWS_SIZE_PREFIX = 3;
	static final String UTF_8 = "UTF-8";
	static final Charset CS_UTF_8 = Charset.forName(UTF_8);
	
	static String amazonDecode(final List<String> list) {

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
