package com.sankuai.canyin.r.wushan.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sankuai.canyin.r.wushan.server.datanode.exception.UnknownDataPacketException;
import com.sankuai.canyin.r.wushan.server.message.DataPacket;
import com.sankuai.canyin.r.wushan.server.message.HeartbeatPakcet;
import com.sankuai.canyin.r.wushan.server.protocol.DBInfoProtocol;
import com.sankuai.canyin.r.wushan.server.protocol.HeartbeatPacketProtocol;
import com.sankuai.canyin.r.wushan.server.protocol.ProtocolFactory;
import com.sankuai.canyin.r.wushan.server.protocol.TransferDataProtocol;
import com.sankuai.canyin.r.wushan.service.DBInfo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NameNodeEncode extends MessageToByteEncoder<Object>{

	private static final Logger LOG = LoggerFactory.getLogger(NameNodeEncode.class);
	
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
		}else if(msg instanceof DBInfo){
			buf = ProtocolFactory.getProtocol(DBInfoProtocol.TYPE).encode(msg);
		}else{
			throw new UnknownDataPacketException("NameNodeEncode encode failed. unknown message type , please check your send message type !");
		}
		
		if(buf != null){
			out.writeBytes(buf);
		}
	}
}
