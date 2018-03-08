package com.sankuai.canyin.r.wushan.server.namenode;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.config.Configuration;
import com.sankuai.canyin.r.wushan.server.namenode.dispatcher.Dispatcher;
import com.sankuai.canyin.r.wushan.server.namenode.service.ClientService;
import com.sankuai.canyin.r.wushan.server.namenode.service.NameNodeRpcService;
import com.sankuai.canyin.r.wushan.server.namenode.service.NameNodeTransferDataRpcService;
import com.sankuai.canyin.r.wushan.server.worker.TaskManager;

public class NameNode {
	
	private static final Logger LOG = LoggerFactory.getLogger(NameNode.class);
	
	private NameNodeTransferDataRpcService transferService;
	
	private NameNodeRpcService rpcService;
	
	private ClientService clientService;
	
	Dispatcher dispatcher = new Dispatcher();
	
	private TaskManager taskManager = new TaskManager();
	
	Configuration config;
	
	public NameNode() throws IOException {
		init();
	}
	
	private void init() throws IOException{
		
		config = new Configuration();
		
		transferService = new NameNodeTransferDataRpcService(config.getNameNodeTransferDataRpcPort());// namenode -> datanode transfer data 
		transferService.init();
		 
		clientService = new ClientService(config.getNameNodeClientRpcPort(),dispatcher , taskManager);// client -> namenode transfer data or Worker
		clientService.init();
		
		rpcService = new NameNodeRpcService(config.getNameNodeRpcPort() , taskManager);// datanode -> namenode upload db info
		rpcService.init();
		
	}
	
	public void start(){
		LOG.info("starting all service...");
		transferService.start();
		clientService.start();
		rpcService.start();
	}
	
	public Dispatcher getDispatcher() {
		return dispatcher;
	}

	public static void main(String[] args) {
		LOG.info("NameNode starting...");
		NameNode namenode;
		try {
			namenode = new NameNode();
			LOG.info("Namenode port -> "+namenode.config.getNameNodeRpcPort()+" , client port -> "+namenode.config.getNameNodeClientRpcPort());
			namenode.start();
		} catch (IOException e) {
			LOG.error("Namenode startup fail.",e);
		}
	}
}
