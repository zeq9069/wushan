package com.snakuai.canyin.r.wushan.client;

import com.snakuai.canyin.r.wushan.client.message.DataPacket;

public interface ClientService {
	
	public void send(DataPacket packet) throws Exception;

}
