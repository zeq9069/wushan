package com.sankuai.canyin.r.wushan.server.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {
	
	public static String getOut(InputStream is){
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuffer st = new StringBuffer();
		String line = null;
		try {
			while((line = reader.readLine()) != null){
				st.append(line);
				st.append("\n");
			}
			return st.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
