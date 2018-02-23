package com.snakuai.canyin.r.wushan.client;

import com.snakuai.canyin.r.wushan.client.message.DataPacket;
import com.snakuai.canyin.r.wushan.client.message.Task;

public interface ClientService {
	
	public void send(DataPacket packet) throws Exception;
	
	public void uploadTask(Task task) throws Exception;

}
