package com.snakuai.canyin.r.wushan.client.codec;

import com.snakuai.canyin.r.wushan.client.message.DataPacket;
import com.snakuai.canyin.r.wushan.client.protocol.ProtocolFactory;
import com.snakuai.canyin.r.wushan.client.protocol.TransferDataProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class WushanEncode extends MessageToByteEncoder<Object>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		if(msg == null){
			return;
		}
		
		ByteBuf buf = null;

		if(msg instanceof DataPacket){
			buf = ProtocolFactory.getProtocol(TransferDataProtocol.TYPE).encode(msg);
		}
		
		if(buf != null){
			out.writeBytes(buf);
		}
	}
}
