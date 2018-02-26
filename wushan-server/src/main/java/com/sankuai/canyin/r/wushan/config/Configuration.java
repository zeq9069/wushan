package com.sankuai.canyin.r.wushan.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	
	private static final String DEFAULT_CONFIG = "wushan.properties";

	private static ClassLoader loader;
	
	private static Properties properties;
	
	private ConfigConstant constant = new ConfigConstant();
	
	static{
		loader = Thread.currentThread().getContextClassLoader();
		if(loader == null){
			loader = Configuration.class.getClassLoader();
		}
	}
	
	public Configuration() throws IOException {
		init();
	}
	
	private synchronized void init() throws IOException{
		InputStream stream = loader.getResourceAsStream(DEFAULT_CONFIG);
		Properties prop = new Properties();
		try {
			prop.load(stream);
			properties = prop;
		} catch (IOException e) {
			throw new IOException("load configuration error.Please checkout "+DEFAULT_CONFIG);
		}
	}
	
	public String getString(String key , String defaultValue){
		return properties.getProperty(key,defaultValue);
	}
	
	public Integer getInt(String key , int defaultValue){
		String value = properties.getProperty(key);
		if(value == null){
			return defaultValue;
		}
		return Integer.parseInt(value);
	}
	
	public int getNameNodeTransferDataRpcPort(){
		return getInt(constant.WUSHAN_NAMENODE_TRANSFER_DATA_RPC_PORT, constant.DEFAULT_WUSHAN_NAMENODE_TRANSFER_DATA_RPC_PORT);
	}
	
	public int getNameNodeRpcPort(){
		return getInt(constant.WUSHAN_NAMENODE_RPC_PORT, constant.DEFAULT_WUSHAN_NAMENODE_RPC_PORT);
	}
	
	public int getNameNodeClientRpcPort(){
		return getInt(constant.WUSHAN_NAMENODE_CLIENT_RPC_PORT, constant.DEFAULT_WUSHAN_NAMENODE_CLIENT_RPC_PORT);
	}
	
	public String getNameNodeServerHost(){
		return getString(constant.WUSHAN_NAMENODE_HOST, constant.DEFUALT_WUSHAN_NAMENODE_HOST);
	}
	
	public String getDataNodeStorePath(){
		return getString(constant.WUSHAN_DATANODE_STORE_PATH, constant.DEFAULT_WUSHAN_DATANODE_STORE_PATH);
	}
	
	public int getDataNodeWorkerRpcPort(){
		return getInt(constant.WUSHAN_DATANODE_WORKER_RPC_PORT, constant.DEFAULT_WUSHAN_DATANODE_WORKER_RPC_PORT);
	}
	
	public void clear(){
		properties.clear();
	}
	
	public void reload() throws IOException{
		clear();
		init();
	}
	
	public static void main(String[] args) throws IOException {
		Configuration conf = new Configuration();
		System.out.println(conf.getNameNodeServerHost());
	}
}
