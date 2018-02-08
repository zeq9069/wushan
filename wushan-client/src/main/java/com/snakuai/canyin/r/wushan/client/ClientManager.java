package com.snakuai.canyin.r.wushan.client;

import com.snakuai.canyin.r.wushan.client.message.DataPacket;

public class ClientManager {

	private ClientService service;
	
	private ClientInstance instance;
	
	private static final ClientManager clientManager = new ClientManager();
	
	private ClientManager(){
		init();
	}
	
	public static ClientManager getIstance(){
		return ClientManager.clientManager;
	}
	
	public void init(){
		instance = new ClientInstance("localhost", 8412);
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
		public void send(DataPacket packet) throws Exception {
			channel.getChannel().writeAndFlush(packet);
		}
	}
	
	public static void main(String[] args) throws Exception {
		ClientManager manager = ClientManager.getIstance();
		manager.getClientService().send(new DataPacket("123".getBytes(), "22222222222".getBytes()));
	}
	
}
