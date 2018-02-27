package com.sankuai.canyin.r.wushan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessUtils {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProcessUtils.class);
	
	public String run(String... commands){
		ProcessBuilder builder = new ProcessBuilder(commands);
		BufferedReader reader = null;
		BufferedReader errorReader = null;
		StringBuffer result = new StringBuffer();
		try {
			Process proc = builder.start();
			errorReader = new BufferedReader(new InputStreamReader(proc.getErrorStream(), Charset.forName("UTF-8")));
			System.out.println(errorReader.readLine());
			reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), Charset.forName("UTF-8")));
			String line = null;
			while((line = reader.readLine())!=null){
				result.append(line);
			}
		} catch (IOException e) {
			LOG.error(" Commands run fialed.commands = {} ",commands,e);
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return result.toString();
	}
	
//	public static void main(String[] args) {
//		ProcessUtils p = new ProcessUtils();
//		String dir = System.getProperty("user.dir");
//		System.out.println("ProcessUtils = "+p.run("sh",dir+"/conf/cpu.sh"));
//		System.out.println("ProcessUtils = "+p.run("sh",dir+"/conf/memory.sh"));
//		System.out.println("ProcessUtils = "+p.run("sh",dir+"/conf/disk.sh"));
//	}
}
