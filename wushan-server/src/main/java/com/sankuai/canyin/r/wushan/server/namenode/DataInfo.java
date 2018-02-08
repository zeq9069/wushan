package com.sankuai.canyin.r.wushan.server.namenode;

import java.util.Objects;

import com.sankuai.canyin.r.wushan.server.message.HeartbeatPakcet;

public class DataInfo{
	
	private String ip;
	
	private int port;
	
	private HeartbeatPakcet heartbeat;
	
	public DataInfo(String ip , int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public void updateHeartbeat(HeartbeatPakcet heartbeat){
		if(heartbeat == null){
			return;
		}
		this.heartbeat = heartbeat;
	}
	
	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public HeartbeatPakcet getHeartbeat() {
		return heartbeat;
	}

	@Override
	public int hashCode() {
        return toString().hashCode();  
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof DataInfo)){
			return false;
		}
		
		DataInfo target = (DataInfo)obj;
		
		if(Objects.equals(ip,target.getIp()) && this.port == target.getPort()){
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return this.ip+":"+this.port;
	}
}
