package com.sankuai.canyin.r.wushan.server.datanode.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.Service;
import com.sankuai.canyin.r.wushan.config.Configuration;

import javassist.NotFoundException;

public class StoreService implements Service{

	private static final Logger LOG = LoggerFactory.getLogger(StoreService.class);
	
	private StorageFactory storageFactory ;
	
	private Configuration config;
	
	public StoreService(Configuration config) throws NotFoundException {
		if(config == null){
			throw new NotFoundException("config is NULL");
		}
		this.config = config;
	}
	
	public void init() {
		storageFactory = new StorageFactory(config);
		storageFactory.init();
	}

	public void start() {
		//LOG.info("StoreServices starting...");
		//TODO
	}

	public void destroy() {
		//LOG.info("StoreServices destroy...");
		//TODO
		
	}
	
	

	public StorageFactory getStorageFactory() {
		return storageFactory;
	}

}
