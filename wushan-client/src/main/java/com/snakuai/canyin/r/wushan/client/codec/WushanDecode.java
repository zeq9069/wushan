package com.snakuai.canyin.r.wushan.client.codec;

import java.util.List;

import com.snakuai.canyin.r.wushan.client.protocol.PacketHeader;
import com.snakuai.canyin.r.wushan.client.protocol.ProtocolFactory;
import com.snakuai.canyin.r.wushan.client.protocol.WushanProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

public class WushanDecode extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		if(msg.readableBytes()<=PacketHeader.HEADER_PROTO){
			return;
		}
		msg.markReaderIndex();
		
		boolean res = PacketHeader.checkHeader(msg);
		
		if(!res){
			msg.resetReaderIndex();
			System.out.println("未知消息类型，丢弃");
			return;
		}
		
		int proto_type = msg.readInt();
		
		WushanProtocol proto = ProtocolFactory.getProtocol(proto_type);
		
		if(proto == null){
			System.out.println("未知的消息协议类型，丢弃");
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
