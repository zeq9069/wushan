package com.sankuai.canyin.r.wushan.config;

public class ConfigConstant {
	
	/** namenode **/
	// namenode -> datanode transfer data service port
	public static final String WUSHAN_NAMENODE_TRANSFER_DATA_RPC_PORT= "wushan.namenode.transfer.data.rpc.port";
	public static final int DEFAULT_WUSHAN_NAMENODE_TRANSFER_DATA_RPC_PORT= 3456;
	
	//datanode -> namenode upload db info service port
	public static final String WUSHAN_NAMENODE_RPC_PORT= "wushan.namenode.rpc.port";
	public static final int DEFAULT_WUSHAN_NAMENODE_RPC_PORT= 8787;
	
	public static final String WUSHAN_DATANODE_WORKER_RPC_PORT = "wushan.datanode.worker.rpc.port";
	public static final int DEFAULT_WUSHAN_DATANODE_WORKER_RPC_PORT = 8416;
	
	// client -> namenode transfer Data Or Task service port
	public static final String WUSHAN_NAMENODE_CLIENT_RPC_PORT= "wushan.namenode.client.rpc.port";
	public static final int DEFAULT_WUSHAN_NAMENODE_CLIENT_RPC_PORT = 6789;
	
	//namenode IP
	public static final String WUSHAN_NAMENODE_HOST= "wushan.namenode.host";
	public static final String DEFUALT_WUSHAN_NAMENODE_HOST= "localhost";
	
	/** datanode **/
	//datanode data store dir
	public static final String WUSHAN_DATANODE_STORE_PATH="wushan.datanode.store.path";
	public static final String DEFAULT_WUSHAN_DATANODE_STORE_PATH="/tmp/wushan-data";
	

}
