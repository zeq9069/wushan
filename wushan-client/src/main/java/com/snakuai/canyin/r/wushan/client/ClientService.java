package com.snakuai.canyin.r.wushan.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.snakuai.canyin.r.wushan.client.message.DataPacket;

public interface ClientService {
	
	public void send(DataPacket packet) throws Exception;
	
	public void sendBatch(List<DataPacket> packet) throws Exception;
	
	public void uploadExpression(String expression , Set<String> dbs , Map<String,Object> params) throws Exception;
}
