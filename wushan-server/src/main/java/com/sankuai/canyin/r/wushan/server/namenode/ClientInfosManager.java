package com.sankuai.canyin.r.wushan.server.namenode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.message.HeartbeatPakcet;
import com.sankuai.canyin.r.wushan.service.DBInfo;

import io.netty.channel.Channel;

/**
 * datanode信息管理
 * 
 * @author kyrin
 *
 */
public final class ClientInfosManager {
	
	private static final Logger LOG = LoggerFactory.getLogger(ClientInfosManager.class);
	
	private static final Map<String, Channel> rpcClients = new ConcurrentHashMap<String, Channel>();//上报信息的client

	private static final Map<String,Set<DBInfo>> dbInfos = new ConcurrentHashMap<String, Set<DBInfo>>();
	
	private static final Map<String, Channel> transferDataClients = new ConcurrentHashMap<String, Channel>();//保存数据的datanode

	private static DataInfo[] tmp = new DataInfo[0];

	//TODO 按照LRU，保存一段历史信息
	private static final Map<String, HeartbeatPakcet> heartbeat = new ConcurrentHashMap<String, HeartbeatPakcet>();

	public static void registClient(String host, int port, Channel channel) {
		rpcClients.put(host + ":" + port, channel);
	}

	public static void unregistClient(String host, int port) {
		LOG.info("{}:{} 与namenode 断开！",host,port);
		rpcClients.remove(host + ":" + port);
	} 
	
	public static Collection<Channel> getAllRpcClientConn(){
		return rpcClients.values();
	}
	
	public static Channel getRpcClientConn(String key){
		return rpcClients.get(key);
	}
	
	public static Channel getRpcClientConnByIp(String ip){
		Iterator<String> it = rpcClients.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			if(key.startsWith(ip) && rpcClients.get(key) != null && rpcClients.get(key).isActive()){
				return rpcClients.get(key);
			}
		}
		return null;
	}
	
	public static void registTransferDataClient(String host, int port, Channel channel) {
		transferDataClients.put(host + ":" + port, channel);
		rebuildArray();
	}

	public static void unregistTransferDataClient(String host, int port) {
		transferDataClients.remove(host + ":" + port);
		rebuildArray();
	}
	
	public static void addDBInfo(String host , int port, DBInfo info){
		if(info == null){
			return;
		}
		Set<DBInfo> sets = dbInfos.get(host + ":" + port);
		if(sets == null){
			sets = new HashSet<DBInfo>();
			dbInfos.put(host + ":" + port, sets);
		}
		sets.add(info);
	}
	
	public static Map<String, Set<DBInfo>> getDbinfos() {
		return dbInfos;
	}
	
	public static void removeDatanode(String ip , int port){
		dbInfos.remove(ip+":"+port);
	}

	private static void rebuildArray(){
		DataInfo[] t = new DataInfo[transferDataClients.size()];
		int i = 0;
		for (String hostAndPort : transferDataClients.keySet()) {
			String[] hosts = hostAndPort.split(":");
			DataInfo dataInfo = new DataInfo(hosts[0], Integer.parseInt(hosts[1]));
			dataInfo.updateHeartbeat(heartbeat.get(hostAndPort));
			t[i++] = dataInfo;
		}
		tmp = t;
	}

	public static void updateHeartbeat(String host, int port, final HeartbeatPakcet packet) {
		if (packet == null) {
			return;
		}
		heartbeat.put(host + ":" + port, packet);
	}

	public HeartbeatPakcet getHeartbeatPacket(String host, int port) {
		return heartbeat.get(host + ":" + port);
	}

	public static Channel getTransferDataChannel(String host, int port) {
		return transferDataClients.get(host + ":" + port);
	}
	
	public static Channel getRandomUsableChannel(){
		for(Map.Entry<String,Channel> entry : transferDataClients.entrySet()){
			if(entry.getValue() != null && entry.getValue().isOpen()){
				return entry.getValue();
			}
		}
		return null;
	}

	public static Channel getTransferDataChannel(DataInfo dataInfo) {
		if (dataInfo == null) {
			return null;
		}
		return getTransferDataChannel(dataInfo.getIp(), dataInfo.getPort());
	}

	public static DataInfo[] toArrayForTransferClient() {
		return tmp;
	}

	public static Set<DataInfo> getAllTransferClient() {
		Set<DataInfo> sets = new HashSet<DataInfo>();
		for (String hostAndPort : transferDataClients.keySet()) {
			String[] hosts = hostAndPort.split(":");
			DataInfo dataInfo = new DataInfo(hosts[0], Integer.parseInt(hosts[1]));
			dataInfo.updateHeartbeat(heartbeat.get(hostAndPort));
			sets.add(dataInfo);
		}
		return sets;
	}
}
