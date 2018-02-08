package com.sankuai.canyin.r.wushan.server.namenode;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.config.Configuration;
import com.sankuai.canyin.r.wushan.server.namenode.dispatcher.Dispatcher;
import com.sankuai.canyin.r.wushan.server.namenode.service.ClientService;
import com.sankuai.canyin.r.wushan.server.namenode.service.NameNodeRpcService;

public class NameNode {
	
	private static final Logger LOG = LoggerFactory.getLogger(NameNode.class);
	
	private NameNodeRpcService rpcService;
	
	private ClientService clientService;
	
	Dispatcher dispatcher = new Dispatcher();
	
	Configuration config;
	
	public NameNode() throws IOException {
		init();
	}
	
	private void init() throws IOException{
		
		config = new Configuration();
		
		rpcService = new NameNodeRpcService(config.getNameNodeRpcPort());
		rpcService.init();
		
		clientService = new ClientService(config.getNameNodeClientRpcPort(),dispatcher);
		clientService.init();
	}
	
	public void start(){
		LOG.info("starting all service...");
		rpcService.start();
		clientService.start();
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
