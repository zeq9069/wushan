package com.sankuai.canyin.r.wushan.server.namenode;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sankuai.canyin.r.wushan.server.message.HeartbeatPakcet;

import io.netty.channel.Channel;

/**
 * datanode信息管理
 * 
 * @author kyrin
 *
 */
public final class ClientInfosManager {

	private static final Map<String, Channel> clients = new ConcurrentHashMap<String, Channel>();

	private static DataInfo[] tmp = new DataInfo[0];

	private static final Map<String, HeartbeatPakcet> heartbeat = new ConcurrentHashMap<String, HeartbeatPakcet>();

	public static void regist(String host, int port, Channel channel) {
		clients.put(host + ":" + port, channel);
		rebuildArray();
	}

	public static void unregist(String host, int port) {
		clients.remove(host + ":" + port);
		rebuildArray();
	}
	
	private static void rebuildArray(){
		DataInfo[] t = new DataInfo[clients.size()];
		int i = 0;
		for (String hostAndPort : clients.keySet()) {
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

	public static Channel getChannel(String host, int port) {
		return clients.get(host + ":" + port);
	}

	public static Channel getChannel(DataInfo dataInfo) {
		if (dataInfo == null) {
			return null;
		}
		return getChannel(dataInfo.getIp(), dataInfo.getPort());
	}

	public static DataInfo[] toArray() {
		return tmp;
	}

	public static Set<DataInfo> getAllClient() {
		Set<DataInfo> sets = new HashSet<DataInfo>();
		for (String hostAndPort : clients.keySet()) {
			String[] hosts = hostAndPort.split(":");
			DataInfo dataInfo = new DataInfo(hosts[0], Integer.parseInt(hosts[1]));
			dataInfo.updateHeartbeat(heartbeat.get(hostAndPort));
			sets.add(dataInfo);
		}
		return sets;
	}
}
