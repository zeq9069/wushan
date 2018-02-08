package com.sankuai.canyin.r.wushan.server.handle;

import java.net.InetSocketAddress;

import com.sankuai.canyin.r.wushan.server.message.DataPacket;
import com.sankuai.canyin.r.wushan.server.namenode.dispatcher.Dispatcher;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 
 * @author kyrin
 *
 */
@Sharable
public class ClientServerHandler extends ChannelInboundHandlerAdapter{
	
	private Dispatcher dispatcher;
	
	public ClientServerHandler(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
	//激活
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		System.out.println("连接");
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
		if(msg instanceof DataPacket){
			dispatcher.dispatch(msg);
		}else{
			throw new Exception("错误的数据包");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
		InetSocketAddress addr = (InetSocketAddress)ctx.channel().remoteAddress();
		ctx.close();
	}
}
