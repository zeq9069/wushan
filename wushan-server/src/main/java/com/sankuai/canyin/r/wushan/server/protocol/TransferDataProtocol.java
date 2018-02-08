package com.sankuai.canyin.r.wushan.server.protocol;

import com.sankuai.canyin.r.wushan.server.message.DataPacket;
import com.sankuai.canyin.r.wushan.server.message.PacketHeader;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 数据传输协议
 * 
 * @author kyrin
 *
 */
public class TransferDataProtocol implements WushanProtocol{
	
	public static final int TYPE = 1;//协议类型
	
	public ByteBuf encode(Object msg) {
		if(msg == null){
			return null;
		}
		if(!(msg instanceof DataPacket)){
			return null;
		}
		
		DataPacket data = (DataPacket)msg;

		if(data.getKey() == null || data.getData() == null){
			return null;
		}
		
		int bodyLen = 4 + 4 + data.getKey().length + 4 + data.getData().length;
		
		if(data.getDb() == null){
			bodyLen+= 4;
		}else{
			bodyLen+= 4 + data.getDb().length; 
		}
		
		int allLen = PacketHeader.HEADER_PROTO + bodyLen;
		
		ByteBuf buf = Unpooled.buffer(allLen);
		
		PacketHeader.writeHeader(buf, (byte)PacketType.REQUEST.getType(), TYPE);
		
		buf.writeInt(bodyLen);
		
		buf.writeInt(data.getDb() == null ? 0 : data.getDb().length);
		
		if(data.getDb()!=null){
			buf.writeBytes(data.getDb());
		}
		
		buf.writeInt(data.getKey().length);
		
		buf.writeBytes(data.getKey());
		
		buf.writeInt(data.getData().length);
		
		buf.writeBytes(data.getData());
		return buf;
	}

	public Object decode(ByteBuf buf) {
		//buf.markReaderIndex();
		if(buf.readableBytes() < 12){
			return null;
		}
		int bodyLen = buf.readInt();
		if(buf.readableBytes() < bodyLen - 4){
			//buf.resetReaderIndex();
			return null;
		}
		
		int dbLen = buf.readInt();
		byte[] dbBytes = null;
		if(dbLen != 0){
			dbBytes =  new byte[dbLen];
			buf.readBytes(dbBytes);	
		}
		
		int keyLen = buf.readInt();
		byte[] keyBytes = new byte[keyLen];//PooledByteBufAllocator.DEFAULT.buffer(initialCapacity)
		buf.readBytes(keyBytes);	
			
		int dataLen = buf.readInt();
		byte[] dataBytes = new byte[dataLen];
		try{
		buf.readBytes(dataBytes);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("bodyLen:"+bodyLen+" , buf.readableBytes() : "+buf.readableBytes() );
		}
		
		return new DataPacket(dbBytes,keyBytes, dataBytes);
	}
	
}
