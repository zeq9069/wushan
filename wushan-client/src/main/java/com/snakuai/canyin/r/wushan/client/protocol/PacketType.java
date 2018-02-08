package com.snakuai.canyin.r.wushan.client.protocol;

/**
 * 数据包类型
 * （request,response,heartbeat）
 * @author kyrin
 *
 */
public enum PacketType {
	
	REQUEST(1),RESPONSE(0),HEARTBEAT(2);
	
	private int type;
	
	private PacketType(int type) {
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public static PacketType typeOf(int type){
		PacketType[] types = values();
		for(PacketType t : types){
			if(t.type == type){
				return t;
			}
		}
		return null;
	}
}
