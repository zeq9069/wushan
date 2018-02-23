package com.snakuai.canyin.r.wushan.client.protocol;

/**
 * 协议工厂
 * 
 * @author kyrin
 *
 */
public final class ProtocolFactory {
	
	private static WushanProtocol[]  protocols = new WushanProtocol[4]; 
	
	static{
		registerProtocol(TransferDataProtocol.TYPE,new TransferDataProtocol());
		registerProtocol(TaskProtocol.TYPE,new TaskProtocol());
	}
	
	private static void registerProtocol(int type , WushanProtocol proto){
		if(type >= protocols.length){
			WushanProtocol[]  newProtocols = new WushanProtocol[type+1];
			System.arraycopy(protocols, 0, protocols, 0, protocols.length);
			protocols = newProtocols;
		}
		protocols[type] = proto;
	}
	
	public static WushanProtocol getProtocol(int type){
		return protocols[type];
	}
}
