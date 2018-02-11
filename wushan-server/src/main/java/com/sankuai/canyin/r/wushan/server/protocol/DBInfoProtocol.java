package com.sankuai.canyin.r.wushan.server.protocol;

import com.sankuai.canyin.r.wushan.server.message.PacketHeader;
import com.sankuai.canyin.r.wushan.service.DBInfo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class DBInfoProtocol implements WushanProtocol{

	public static final int TYPE = 2;
	
	public ByteBuf encode(Object msg) {
		if(msg == null){
			return null;
		}
		if(!(msg instanceof DBInfo)){
			return null;
		}
		
		DBInfo dbInfo = (DBInfo)msg;
		
		int dbLen = dbInfo.getDb() == null ? 0 : dbInfo.getDb().length ;
		 
		int bodyLen = 4 + dbLen + 8 * 4;
		
		int allLen = PacketHeader.HEADER_PROTO + bodyLen;
		
		ByteBuf buf = Unpooled.buffer(allLen);	
		
		PacketHeader.writeHeader(buf, (byte)PacketType.REQUEST.getType(), TYPE);
		
		buf.writeInt(bodyLen);
		buf.writeInt(dbLen);
		buf.writeBytes(dbInfo.getDb());
		buf.writeLong(dbInfo.getAllSize());
		buf.writeLong(dbInfo.getKeyCount());
		buf.writeLong(dbInfo.getReadCount());
		buf.writeLong(dbInfo.getLastUpdatetimestamp());
		return buf;
	}

	public Object decode(ByteBuf buf) {
		if(buf.readableBytes() < PacketHeader.HEADER_PROTO + 36){
			return null;
		}
		
		buf.skipBytes(4);//skip body len
		
		int dbLen = buf.readInt();
		
		byte[] db = new byte[dbLen];
		buf.readBytes(db);
		
		long db_allSzie = buf.readLong();
		long db_keyCount = buf.readLong();
		long db_readCount = buf.readLong();
		long db_lastUpdatetimestamp = buf.readLong();
		return new DBInfo(db, db_allSzie, db_keyCount, db_readCount,db_lastUpdatetimestamp);
	}

}
