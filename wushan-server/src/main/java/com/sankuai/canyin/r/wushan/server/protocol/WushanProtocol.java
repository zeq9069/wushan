package com.sankuai.canyin.r.wushan.server.protocol;

import io.netty.buffer.ByteBuf;

/**
 * 协议接口
 * 
 * TODO 后期会支持hession、kyro、protobuf、jdk等几种主流的协议
 * 
 * @author kyrin
 *
 */
public interface WushanProtocol {
	
	public ByteBuf encode(Object msg);
	
	public Object decode(ByteBuf buf);

}
