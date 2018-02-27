package com.sankuai.canyin.r.wushan.server.protocol;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Charsets;
import com.sankuai.canyin.r.wushan.server.message.PacketHeader;
import com.sankuai.canyin.r.wushan.server.worker.WorkerStatus;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * WorkerStatus协议
 * @author kyrin
 *
 */
public class WorkerStatueProtocol implements WushanProtocol{

	public static final int TYPE = 4;
	
	public ByteBuf encode(Object msg) {
		if(msg == null || !(msg instanceof WorkerStatus)){
			return null;
		}
		WorkerStatus status = (WorkerStatus)msg;
		
		int idLen = status.getTaskId().getBytes(Charsets.UTF_8).length;
		
		int allDBsLen = 0;
		
		for(String db : status.getAllDBs()){
			allDBsLen+=db.getBytes(Charsets.UTF_8).length;
		}
		
		int overDBLen = 0;
		
		for(String db : status.getOverDB()){
			overDBLen+=db.getBytes(Charsets.UTF_8).length;
		}
		
		int bodyLen = 4 + (4 + idLen ) + ( 4 + 4 + status.getAllDBs().size() * 4 + allDBsLen ) + ( 4 + 4 + status.getOverDB().size() * 4 +overDBLen ) + 1 + 8;
		
		int allLen = PacketHeader.HEADER_PROTO + bodyLen;
		
		ByteBuf buf = Unpooled.buffer(allLen);
		
		PacketHeader.writeHeader(buf, (byte) PacketType.REQUEST.getType() , TYPE);
		
		buf.writeInt(bodyLen);
		
		buf.writeInt(idLen);
		
		buf.writeBytes(status.getTaskId().getBytes(Charsets.UTF_8));
		
		buf.writeInt(status.getAllDBs().size());
		
		buf.writeInt(allDBsLen + 4 * status.getAllDBs().size());
		
		for(String db : status.getAllDBs()){
			byte[] bytes = db.getBytes(Charsets.UTF_8);
			buf.writeInt(bytes.length);
			buf.writeBytes(bytes);
		}
		
		buf.writeInt(status.getOverDB().size());

		buf.writeInt(overDBLen + 4 * status.getOverDB().size());
		
		for(String db : status.getOverDB()){
			byte[] bytes = db.getBytes(Charsets.UTF_8);
			buf.writeInt(bytes.length);
			buf.writeBytes(bytes);
		}
		
		buf.writeBoolean(status.isOver());//1个字节
		
		buf.writeLong(status.getRunTimestamp());
		
		return buf;
	}

	public Object decode(ByteBuf buf) {
		if(buf.readableBytes() < 40){
			return null;
		}
		
		buf.skipBytes(4);//skip body len
		
		int idLen = buf.readInt();
		
		byte[] id = new byte[idLen];
		
		buf.readBytes(id);
		
		int allDBsNum = buf.readInt();
		buf.skipBytes(4);//skip all allDBs  len
		Set<String> allDBs = new HashSet<String>();
		for(int i = 0 ; i < allDBsNum ; i++){
			int len = buf.readInt();
			byte[] db = new byte[len];
			allDBs.add(new String(db,Charsets.UTF_8));
		}
		
		int overDBNum = buf.readInt();
		buf.skipBytes(4);//skip all over db  len
		Set<String> overDBs = new HashSet<String>();
		for(int i = 0 ; i < overDBNum ; i++){
			int len = buf.readInt();
			byte[] db = new byte[len];
			buf.readBytes(db);
			overDBs.add(new String(db,Charsets.UTF_8));
		}
		
		boolean isOver = buf.readBoolean();
		
		long runTimestamp = buf.readLong();
		
		return new WorkerStatus(new String(id , Charsets.UTF_8), allDBs, overDBs, isOver, runTimestamp);
	}

}
