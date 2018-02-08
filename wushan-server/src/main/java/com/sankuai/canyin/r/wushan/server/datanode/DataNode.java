package com.sankuai.canyin.r.wushan.server.datanode;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.config.Configuration;
import com.sankuai.canyin.r.wushan.server.datanode.store.StoreService;

import javassist.NotFoundException;

/**
 * 
 * @author kyrin
 *
 */
public class DataNode {
	
	private static final Logger LOG = LoggerFactory.getLogger(DataNode.class);
	
	private DataNodeRpcService rpcService;
	
	private StoreService storeService;
	
	Configuration config;
	
	public DataNode() throws IOException, NotFoundException {
		init();
	}
	
	private void init() throws IOException, NotFoundException{
		config = new Configuration();
		
		storeService = new StoreService(config);
		storeService.init();
		
		rpcService = new DataNodeRpcService(config.getNameNodeServerHost(),config.getNameNodeRpcPort()
				,storeService.getStorageFactory());
	}
	
	public void start(){
		LOG.info("startup all service...");
		rpcService.start();
	}
	
	public static void main(String[] args) throws IOException, NotFoundException {
		LOG.info("DataNode starting..");
		DataNode datanode = new DataNode();
		LOG.info("Namenode port -> {}",datanode.config.getNameNodeRpcPort());
		datanode.start();
	}

}
