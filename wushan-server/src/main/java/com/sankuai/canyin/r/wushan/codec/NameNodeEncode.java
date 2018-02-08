package com.sankuai.canyin.r.wushan.codec;

import com.sankuai.canyin.r.wushan.server.message.DataPacket;
import com.sankuai.canyin.r.wushan.server.message.HeartbeatPakcet;
import com.sankuai.canyin.r.wushan.server.protocol.HeartbeatPacketProtocol;
import com.sankuai.canyin.r.wushan.server.protocol.ProtocolFactory;
import com.sankuai.canyin.r.wushan.server.protocol.TransferDataProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NameNodeEncode extends MessageToByteEncoder<Object>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		if(msg == null){
			return;
		}
		
		ByteBuf buf = null;

		if(msg instanceof DataPacket){
			buf = ProtocolFactory.getProtocol(TransferDataProtocol.TYPE).encode(msg);
		}else if(msg instanceof HeartbeatPakcet){
			buf = ProtocolFactory.getProtocol(HeartbeatPacketProtocol.TYPE).encode(msg);
		}
		
		if(buf != null){
			out.writeBytes(buf);
		}
	}
}
