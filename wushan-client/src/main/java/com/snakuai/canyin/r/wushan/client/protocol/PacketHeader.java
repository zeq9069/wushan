package com.snakuai.canyin.r.wushan.client.protocol;

import io.netty.buffer.ByteBuf;

public final class PacketHeader {

	// 2017(4byte) + 项目版本号（4byte） + 消息类型（1byte） + 协议类型（4byte）
	public static final int HEADER_PROTO = 4 + 4 + 1 + 4;

	public static final int MARK = 2017;

	public static final int VERSION = 1;// 系统版本

	public static final byte REQUEST = (byte) 1;// 请求消息

	public static final byte RESPONSE = (byte) 0;// 回复消息
	
	public static final byte HEARTBEAT = (byte) 2;// 心跳消息
	
	
	//消息协议类型没解析
	public static boolean checkHeader(ByteBuf buf){
		if(buf.readableBytes() < HEADER_PROTO){
			return false;
		}
		int mark = buf.readInt();
		if(mark != MARK){
			return false;
		}
		int version = buf.readInt();
		if(version != VERSION){
			return false;
		}
		byte msg_type = buf.readByte();
		PacketType type = PacketType.typeOf((int)msg_type);
		if(type == null){
			return false;
		}
		return true;
	}
	
	public static void writeHeader(ByteBuf buf , byte msg_type , int protocolType){
		buf.writeInt(MARK);
		buf.writeInt(VERSION);
		buf.writeByte(msg_type);
		buf.writeInt(protocolType);
	}
}
