package com.snakuai.canyin.r.wushan.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.snakuai.canyin.r.wushan.client.message.DataPacket;
import com.snakuai.canyin.r.wushan.client.message.Task;

public class ClientManager {

	private ClientService service;
	
	private ClientInstance instance;
	
	private String host;
	
	private int port;
	
	public ClientManager(String host , int port){
		this.host = host;
		this.port = port;
		 init();
	}
	
	private void init(){
		instance = new ClientInstance(host, port);
		instance.start();
		service = new ClientServiceImpl(instance.channel);
	}
	
	public ClientService getClientService(){
		return service;
	}
	
	class ClientServiceImpl implements ClientService{
		ClientChannel channel;
		public ClientServiceImpl(ClientChannel channel) {
			this.channel = channel;
		}
		public synchronized void send(DataPacket packet) throws Exception {
			channel.getChannel().writeAndFlush(packet);
		}
		
		public synchronized void sendBatch(List<DataPacket> packets) throws Exception {
			for(DataPacket dp : packets){
				channel.getChannel().write(dp);
			}
			channel.getChannel().flush();
		}
		
		public void uploadExpression(String expression, Set<String> dbs, Map<String, Object> params) throws Exception {
			channel.getChannel().writeAndFlush(new Task(expression, dbs, params));
		}
	}
}
