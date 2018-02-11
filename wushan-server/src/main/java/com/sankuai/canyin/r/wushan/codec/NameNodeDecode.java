package com.sankuai.canyin.r.wushan.codec;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.message.PacketHeader;
import com.sankuai.canyin.r.wushan.server.protocol.ProtocolFactory;
import com.sankuai.canyin.r.wushan.server.protocol.TransferDataProtocol;
import com.sankuai.canyin.r.wushan.server.protocol.WushanProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class NameNodeDecode extends ByteToMessageDecoder{

	private static final Logger LOG = LoggerFactory.getLogger(NameNodeDecode.class);
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		if(msg.readableBytes()<=PacketHeader.HEADER_PROTO){
			return;
		}
		msg.markReaderIndex();
		
		boolean res = PacketHeader.checkHeader(msg);
		
		if(!res){
			msg.resetReaderIndex();
			LOG.error("unknown message type , Please check message Header. Give up it !");
			return;
		}
		
		int proto_type = msg.readInt();
		
		WushanProtocol proto = ProtocolFactory.getProtocol(proto_type);
		
		if(proto == null){
			LOG.error("unknown protocol type , Please check message Header. Give up it !");
			return;
		}
		
		Object t = proto.decode(msg);
		if(t != null){
			out.add(t);
		}else{
			msg.resetReaderIndex();
		}
		msg.discardReadBytes();
	}
}
