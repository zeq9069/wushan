package com.sankuai.canyin.r.wushan.server.datanode.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.Service;
import com.sankuai.canyin.r.wushan.config.Configuration;
import com.sankuai.canyin.r.wushan.server.datanode.DataNodeClientSideService;
import com.sankuai.canyin.r.wushan.service.DataNodeProtocolImpl;

import javassist.NotFoundException;

public class StoreService implements Service{

	private static final Logger LOG = LoggerFactory.getLogger(StoreService.class);
	
	private StorageFactory storageFactory ;
	
	private Configuration config;
	
	private DataNodeClientSideService protocolImpl;
	
	public StoreService(Configuration config ,DataNodeClientSideService protocolImpl) throws NotFoundException {
		if(config == null){
			throw new NotFoundException("config is NULL");
		}
		this.config = config;
		this.protocolImpl = protocolImpl;
	}
	
	public void init() {
		storageFactory = new StorageFactory(config ,protocolImpl);
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
