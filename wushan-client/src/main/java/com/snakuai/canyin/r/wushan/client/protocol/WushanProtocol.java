package com.snakuai.canyin.r.wushan.client.protocol;

import io.netty.buffer.ByteBuf;

/**
 * 协议接口
 * @author kyrin
 *
 */
public interface WushanProtocol {
	
	public ByteBuf encode(Object msg);
	
	public Object decode(ByteBuf buf);

}
