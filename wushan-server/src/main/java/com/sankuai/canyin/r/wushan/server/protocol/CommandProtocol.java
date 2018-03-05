package com.sankuai.canyin.r.wushan.server.protocol;

import com.google.common.base.Charsets;
import com.sankuai.canyin.r.wushan.server.message.PacketHeader;
import com.sankuai.canyin.r.wushan.server.worker.Command;
import com.sankuai.canyin.r.wushan.server.worker.Command.CommandType;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Command protocol
 * @author kyrin
 *
 */
public class CommandProtocol implements WushanProtocol{

	public static final int TYPE = 5;
	
	public ByteBuf encode(Object msg) {
		if(msg == null){
			return null;
		}
		Command cmd = (Command)msg;
		int type = cmd.getType().code();
		String taskId = cmd.getTaskId();
		int bodyLen = 4 + 4 + ( 4 + taskId.getBytes(Charsets.UTF_8).length);
		int allLen = PacketHeader.HEADER_PROTO + bodyLen;
		ByteBuf buf = Unpooled.buffer(allLen);
		PacketHeader.writeHeader(buf, (byte)PacketType.REQUEST.getType(), TYPE);
		buf.writeInt(bodyLen);
		buf.writeInt(type);
		buf.writeInt(taskId.getBytes(Charsets.UTF_8).length);
		buf.writeBytes(taskId.getBytes(Charsets.UTF_8));
		return buf;
	}

	public Object decode(ByteBuf buf) {
		if(buf.readableBytes() < PacketHeader.HEADER_PROTO + 8){
			return null;
		}
		buf.skipBytes(4);
		int type = buf.readInt();
		int taskIdLen = buf.readInt();
		byte[] taskIdArr = new byte[taskIdLen];
		buf.readBytes(taskIdArr);
		return new Command(CommandType.codeOf(type) , new String(taskIdArr,Charsets.UTF_8));
	}
	
}
