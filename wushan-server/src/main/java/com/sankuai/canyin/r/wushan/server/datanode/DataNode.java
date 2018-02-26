package com.sankuai.canyin.r.wushan.server.datanode;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.config.Configuration;
import com.sankuai.canyin.r.wushan.server.datanode.service.DataNodeRpcService;
import com.sankuai.canyin.r.wushan.server.datanode.service.DataNodeTransferDataRpcService;
import com.sankuai.canyin.r.wushan.server.datanode.service.WorkerManager;
import com.sankuai.canyin.r.wushan.server.datanode.service.WorkerService;
import com.sankuai.canyin.r.wushan.server.datanode.store.StoreService;
import com.sankuai.canyin.r.wushan.service.DataNodeClientSideService;
import com.sankuai.canyin.r.wushan.service.DataNodeServiceImpl;

import javassist.NotFoundException;

/**
 * 
 * @author kyrin
 *
 */
public class DataNode{
	
	private static final Logger LOG = LoggerFactory.getLogger(DataNode.class);
	
	private DataNodeTransferDataRpcService transferService;
	
	private DataNodeRpcService rpcService;
	
	private StoreService storeService;
	
	DataNodeClientSideService client;
	
	SystemInfo sysInfo;
	
	Configuration config;
	
	WorkerService workerService;
	
	WorkerManager workerManager;
	
	public DataNode() throws IOException, NotFoundException {
		init();
	}
	
	private void init() throws IOException, NotFoundException{
		config = new Configuration();
		workerManager = new WorkerManager();

		
		sysInfo = new SystemInfo();
		sysInfo.init();
		
		DataNodeServiceImpl protocolImpl = new DataNodeServiceImpl(null);

		rpcService = new DataNodeRpcService(config.getNameNodeServerHost(),config.getNameNodeRpcPort() , protocolImpl);
		rpcService.init();
		
		client = new DataNodeClientSideService(protocolImpl);
		
		storeService = new StoreService(config , client);
		storeService.init();
		
		transferService = new DataNodeTransferDataRpcService(config.getNameNodeServerHost(),config.getNameNodeTransferDataRpcPort()
				,storeService.getStorageFactory() , sysInfo);
		transferService.init();
		
		workerService = new WorkerService(config.getDataNodeWorkerRpcPort() , workerManager);
		workerService.init();
		
	}
	
	public void start(){
		LOG.info("startup all service...");
		rpcService.start();
		transferService.start();
		workerService.start();
	}
	
	public static void main(String[] args) throws IOException, NotFoundException {
		LOG.info("DataNode starting..");
		DataNode datanode = new DataNode();
		LOG.info("Namenode port -> {}",datanode.config.getNameNodeRpcPort());
		datanode.start();
	}

}
