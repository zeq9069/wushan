package com.sankuai.canyin.r.wushan.server.protocol;

/**
 * 协议工厂
 * 
 * @author kyrin
 *
 */
public final class ProtocolFactory {
	
	private static WushanProtocol[]  protocols = new WushanProtocol[5]; 
	
	static{
		registerProtocol(HeartbeatPacketProtocol.TYPE,new HeartbeatPacketProtocol());
		registerProtocol(TransferDataProtocol.TYPE,new TransferDataProtocol());
		registerProtocol(DBInfoProtocol.TYPE,new DBInfoProtocol());
		registerProtocol(TaskProtocol.TYPE,new TaskProtocol());
		registerProtocol(WorkerStatueProtocol.TYPE,new WorkerStatueProtocol());

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
