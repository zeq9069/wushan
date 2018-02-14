package com.sankuai.canyin.r.wushan.server.protocol;

import com.sankuai.canyin.r.wushan.server.message.HeartbeatPakcet;
import com.sankuai.canyin.r.wushan.server.message.PacketHeader;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 心跳协议
 * @author kyrin
 *
 */
public class HeartbeatPacketProtocol implements WushanProtocol{
	
	public static final int TYPE = 0;

	public ByteBuf encode(Object msg) {
		if(msg == null){
			return null;
		}
		if(!(msg instanceof HeartbeatPakcet)){
			return null;
		}
		
		HeartbeatPakcet data = (HeartbeatPakcet)msg;
		
		int bodyLen = 4 + 4 + 8 + 4 + 8 + 4 + 8 + 8;
		
		int allLen = PacketHeader.HEADER_PROTO + bodyLen;
		
		ByteBuf buf = Unpooled.buffer(allLen);
		
		PacketHeader.writeHeader(buf, (byte)PacketType.HEARTBEAT.getType(), TYPE);
		
		buf.writeInt(bodyLen);
		
		buf.writeInt(data.getCpu());
		
		buf.writeDouble(data.getCpuLoad());
		
		buf.writeInt(data.getMemory());
		
		buf.writeDouble(data.getMemoryLoad());
		
		buf.writeInt(data.getDisk());
		
		buf.writeDouble(data.getDiskLoad());
		
		buf.writeLong(data.getLastDatetime());

		return buf;
	}

	public Object decode(ByteBuf buf) {
		
		int bodyLen = buf.readInt();
		
		if(buf.readableBytes() < bodyLen - 4){
			return null;
		}
		
		int cpu = buf.readInt();
		
		double cpuLoad = buf.readDouble();
		
		int memory = buf.readInt();
		
		double memoryLoad = buf.readDouble();
		
		int disk = buf.readInt();
		
		double diskLoad = buf.readDouble();
		
		long lastDatetime = buf.readLong();
		
		return new HeartbeatPakcet(cpu, cpuLoad, memory, memoryLoad , disk, diskLoad , lastDatetime);
	}

}
